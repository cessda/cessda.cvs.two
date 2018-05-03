package eu.cessda.cvmanager.es;

//import static org.junit.Assert.*;import org.apache.lucene.search.Query;
//import org.apache.lucene.search.join.ScoreMode;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.common.io.stream.StreamOutput;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//import org.elasticsearch.common.xcontent.ToXContent.Params;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.InnerHitBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.QueryShardContext;
//import org.elasticsearch.index.query.TermQueryBuilder;
//import org.elasticsearch.index.query.TermsQueryBuilder;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.Aggregations;
//import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.core.ResultsExtractor;
//import org.springframework.data.elasticsearch.core.facet.request.TermFacetRequestBuilder;
//import org.springframework.data.elasticsearch.core.query.IndexQuery;
//import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
//import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import eu.cessda.cvmanager.domain.Vocabulary;

//import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
//import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchTemplateAggregationTests {
	
//	@Autowired
//	private ElasticsearchTemplate elasticsearchTemplate;
//
//	@Test
//	@Ignore
//	public void testTermAggregration() {
//		
//		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("languages")
//				  .field("languages")
//				  .order(Terms.Order.aggregation("_count", false));
//		
//		SearchQuery searchQuery = new NativeSearchQueryBuilder()
//				.withQuery(matchAllQuery())
//				.withSearchType(SearchType.DEFAULT)
//				.withIndices("vocabulary").withTypes("vocabulary")
//				.addAggregation( termsAggregationBuilder )
//				.build();
//
//		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
//			@Override
//			public Aggregations extract(SearchResponse response) {
//				return response.getAggregations();
//			}
//		});
//		
//		List<Vocabulary> vocabulary = elasticsearchTemplate.queryForList( searchQuery, Vocabulary.class);
//		// then
//		assertThat(aggregations, is(notNullValue()));
//		assertThat(aggregations.asMap().get("vocabulary"), is(notNullValue()));
//	}
//
//	@Test
//	@Ignore
//	public void testAggregrationFilter() {
//		TermsQueryBuilder termQueryBuilder = QueryBuilders.termsQuery("languages", "swedish", "german");
//		
//		QueryBuilder queryFilter = QueryBuilders.boolQuery()
//	            .must(QueryBuilders.termQuery("languages", "swedish"))
//	            .must(QueryBuilders.termQuery("languages", "german"));
//
//		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("languages_count")
//				  .field("languages");
//		
//		FiltersAggregationBuilder filtersAggregationBuilder = AggregationBuilders.filters( "languages_filter", queryFilter).subAggregation(termsAggregationBuilder);
//		
//		SearchQuery searchQuery = new NativeSearchQueryBuilder()
//				.withQuery(matchAllQuery())
//				.withSearchType(SearchType.DEFAULT)
//				.withIndices("vocabulary").withTypes("vocabulary")
//				.withFilter( queryFilter )
//				.addAggregation( filtersAggregationBuilder )
//				.build();
//		
//		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
//			@Override
//			public Aggregations extract(SearchResponse response) {
//				return response.getAggregations();
//			}
//		});
//		
//		List<Vocabulary> vocabulary = elasticsearchTemplate.queryForList( searchQuery, Vocabulary.class);
//		
//		// then
//		assertThat(aggregations, is(notNullValue()));
//		assertThat(aggregations.asMap().get("vocabulary"), is(notNullValue()));
//	}
//	
//	@Test
//	@Ignore
//	public void testNestedQuery() {
//		String keyword = "procedure";
//		
//		QueryBuilder matchQuery = QueryBuilders.matchQuery("_all", keyword);
//		
//				
//		QueryBuilder queryFilter = QueryBuilders.boolQuery()
//	            .must(QueryBuilders.termQuery("languages", "swedish"))
//	            .must(QueryBuilders.termQuery("languages", "german"));
//		
//		QueryBuilder nestedCodeQuery = QueryBuilders.nestedQuery( "code", matchQuery, ScoreMode.None);
//
//		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("languages_count")
//				  .field("languages");
//		
//		FiltersAggregationBuilder filtersAggregationBuilder = AggregationBuilders.filters( "languages_filter", queryFilter).subAggregation(termsAggregationBuilder);
//		
//		SearchQuery searchQuery = new NativeSearchQueryBuilder()
//				.withQuery(matchQuery)
////				.withQuery(nestedCodeQuery)
//				.withSearchType(SearchType.DEFAULT)
//				.withIndices("vocabulary").withTypes("vocabulary")
//				.withFilter( queryFilter )
//				.addAggregation( filtersAggregationBuilder )
//				.build();
//		
//		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
//			@Override
//			public Aggregations extract(SearchResponse response) {
//				return response.getAggregations();
//			}
//		});
//		
//		List<Vocabulary> vocabulary = elasticsearchTemplate.queryForList( searchQuery, Vocabulary.class);
//		
//		// then
//		assertThat(aggregations, is(notNullValue()));
//		assertThat(aggregations.asMap().get("vocabulary"), is(notNullValue()));
//	}
//	
//	@Test
//	@Ignore
//	public void testNestedQuery2() {
//		String keyword = "example";
//		
//		QueryBuilder matchQuery = QueryBuilders.matchQuery("_all", keyword);
//		
//		QueryBuilder matchQueryNested = QueryBuilders.matchQuery("codes.definitionEn", keyword);
//		
//		
//		QueryBuilder nestedCodeQuery = QueryBuilders.nestedQuery( "codes", matchQueryNested , ScoreMode.None).innerHit( new InnerHitBuilder());
//		
//		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must( matchQuery).must( nestedCodeQuery );
//		
//		
//				
//		QueryBuilder queryFilter = QueryBuilders.boolQuery()
//	            .must(QueryBuilders.termQuery("languages", "swedish"))
//	            .must(QueryBuilders.termQuery("languages", "german"));
//		
//
//		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("languages_count")
//				  .field("languages");
//		
//		FiltersAggregationBuilder filtersAggregationBuilder = AggregationBuilders.filters( "languages_filter", queryFilter).subAggregation(termsAggregationBuilder);
//		
//		SearchQuery searchQuery = new NativeSearchQueryBuilder()
//				.withQuery( boolQuery )
//				.withSearchType(SearchType.DEFAULT)
////				.withIndices("code").withTypes("code")
//				.withIndices("vocabulary").withTypes("vocabulary")
//				.withFilter( queryFilter )
//				.addAggregation( filtersAggregationBuilder )
//				.build();
//		
//		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
//			@Override
//			public Aggregations extract(SearchResponse response) {
//				return response.getAggregations();
//			}
//		});
//		
//		List<Vocabulary> vocabulary = elasticsearchTemplate.queryForList( searchQuery, Vocabulary.class);
//		
//		SearchResponse searchResponse = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<SearchResponse>() {
//
//			@Override
//			public SearchResponse extract(SearchResponse response) {
//				return response;
//			}
//			
//		});
//		Map<String, SearchHits> innerHits = new LinkedHashMap<>();
//		for(SearchHit hit : searchResponse.getHits()) {
//			innerHits.putAll( hit.getInnerHits() );
//		}
//					
//		// then
//		assertThat(aggregations, is(notNullValue()));
//		assertThat(aggregations.asMap().get("vocabulary"), is(notNullValue()));
//	}
//	
	
}
