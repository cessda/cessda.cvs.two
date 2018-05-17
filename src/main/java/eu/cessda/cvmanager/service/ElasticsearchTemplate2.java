package eu.cessda.cvmanager.service;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.facet.FacetRequest;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.core.query.IndexBoost;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.ScriptField;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;

/**
 * Due to issue in the highlighting see 
 * https://stackoverflow.com/questions/46752784/elastic-search-5-5-highlight-field-is-not-working
 * https://jira.spring.io/browse/DATAES-412
 * the ElasticsearchTemplate needs to be extended
 * @ Original author
 */
public class ElasticsearchTemplate2 extends ElasticsearchTemplate{

	private String searchTimeout;
	
	public ElasticsearchTemplate2(Client client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public ElasticsearchTemplate2(Client client, EntityMapper entityMapper) {
		super(client, new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext()), entityMapper);
	}
	
	@Override
	public <T> AggregatedPage<T> queryForPage(SearchQuery query, Class<T> clazz) {
		return queryForPage(query, clazz, getResultsMapper());
	}

	@Override
	public <T> AggregatedPage<T> queryForPage(SearchQuery query, Class<T> clazz, SearchResultMapper mapper) {
		SearchResponse response = doSearch(prepareSearch(query, clazz), query);
		return mapper.mapResults(response, clazz, query.getPageable());
	}

	@Override
	public <T> T query(SearchQuery query, ResultsExtractor<T> resultsExtractor) {
		SearchResponse response = doSearch(prepareSearch(query), query);
		return resultsExtractor.extract(response);
	}

	private <T> SearchRequestBuilder prepareSearch(Query query, Class<T> clazz) {
		setPersistentEntityIndexAndType(query, clazz);
		return prepareSearch(query);
	}

	private SearchRequestBuilder prepareSearch(Query query) {
		Assert.notNull(query.getIndices(), "No index defined for Query");
		Assert.notNull(query.getTypes(), "No type defined for Query");

		int startRecord = 0;
		SearchRequestBuilder searchRequestBuilder = getClient().prepareSearch(toArray(query.getIndices()))
				.setSearchType(query.getSearchType()).setTypes(toArray(query.getTypes()));

		if (query.getSourceFilter() != null) {
			SourceFilter sourceFilter = query.getSourceFilter();
			searchRequestBuilder.setFetchSource(sourceFilter.getIncludes(), sourceFilter.getExcludes());
		}

		if (query.getPageable().isPaged()) {
			startRecord = query.getPageable().getPageNumber() * query.getPageable().getPageSize();
			searchRequestBuilder.setSize(query.getPageable().getPageSize());
		}
		searchRequestBuilder.setFrom(startRecord);

		if (!query.getFields().isEmpty()) {
			searchRequestBuilder.setFetchSource(toArray(query.getFields()),null);
		}

		if (query.getSort() != null) {
			for (Sort.Order order : query.getSort()) {
				searchRequestBuilder.addSort(order.getProperty(),
						order.getDirection() == Sort.Direction.DESC ? SortOrder.DESC : SortOrder.ASC);
			}
		}

		if (query.getMinScore() > 0) {
			searchRequestBuilder.setMinScore(query.getMinScore());
		}
		return searchRequestBuilder;
	}
	
	private void setPersistentEntityIndexAndType(Query query, Class clazz) {
		if (query.getIndices().isEmpty()) {
			query.addIndices(retrieveIndexNameFromPersistentEntity(clazz));
		}
		if (query.getTypes().isEmpty()) {
			query.addTypes(retrieveTypeFromPersistentEntity(clazz));
		}
	}
	
	private String[] retrieveIndexNameFromPersistentEntity(Class clazz) {
		if (clazz != null) {
			return new String[] { getPersistentEntityFor(clazz).getIndexName() };
		}
		return null;
	}

	private String[] retrieveTypeFromPersistentEntity(Class clazz) {
		if (clazz != null) {
			return new String[] { getPersistentEntityFor(clazz).getIndexType() };
		}
		return null;
	}
	
	private static String[] toArray(List<String> values) {
		String[] valuesAsArray = new String[values.size()];
		return values.toArray(valuesAsArray);
	}
	
	private SearchResponse doSearch(SearchRequestBuilder searchRequest, SearchQuery searchQuery) {
		if (searchQuery.getFilter() != null) {
			searchRequest.setPostFilter(searchQuery.getFilter());
		}

		if (!isEmpty(searchQuery.getElasticsearchSorts())) {
			for (SortBuilder sort : searchQuery.getElasticsearchSorts()) {
				searchRequest.addSort(sort);
			}
		}

		if (!searchQuery.getScriptFields().isEmpty()) {
			//_source should be return all the time
			//searchRequest.addStoredField("_source");
			for (ScriptField scriptedField : searchQuery.getScriptFields()) {
				searchRequest.addScriptField(scriptedField.fieldName(), scriptedField.script());
			}
		}

		if (searchQuery.getHighlightFields() != null) {
			HighlightBuilder myBuilder = new HighlightBuilder();
            for (HighlightBuilder.Field highlightField : searchQuery.getHighlightFields()) {
                myBuilder.field(highlightField);

            }
            searchRequest.highlighter(myBuilder);
		}

		if (!isEmpty(searchQuery.getIndicesBoost())) {
			for (IndexBoost indexBoost : searchQuery.getIndicesBoost()) {
				searchRequest.addIndexBoost(indexBoost.getIndexName(), indexBoost.getBoost());
			}
		}

		if (!isEmpty(searchQuery.getAggregations())) {
			for (AbstractAggregationBuilder aggregationBuilder : searchQuery.getAggregations()) {
				searchRequest.addAggregation(aggregationBuilder);
			}
		}

		if (!isEmpty(searchQuery.getFacets())) {
			for (FacetRequest aggregatedFacet : searchQuery.getFacets()) {
				searchRequest.addAggregation(aggregatedFacet.getFacet());
			}
		}
		return getSearchResponse(searchRequest.setQuery(searchQuery.getQuery()).execute());
	}
	
	private SearchResponse getSearchResponse(ListenableActionFuture<SearchResponse> response) {
		return searchTimeout == null ? response.actionGet() : response.actionGet(searchTimeout);
	}

	
}
