package eu.cessda.cvmanager.service.impl;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.gesis.wts.domain.enumeration.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.Field;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.repository.VocabularyRepository;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.ElasticsearchTemplate2;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.view.publication.EsQueryResultDetail;
import eu.cessda.cvmanager.ui.view.publication.FilterItem;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Vocabulary.
 */
@Service
@Transactional
public class VocabularyServiceImpl implements VocabularyService {
	
	private static final String CODE_PATH = "codes";

    private final Logger log = LoggerFactory.getLogger(VocabularyServiceImpl.class);

    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;

    private final VocabularySearchRepository vocabularySearchRepository;
    
    // Use original ElasticsearchTemplate if this bug is fixed
    // https://jira.spring.io/browse/DATAES-412
    private final ElasticsearchTemplate2 elasticsearchTemplate;

    public VocabularyServiceImpl(VocabularyRepository vocabularyRepository, VocabularyMapper vocabularyMapper, 
    		VocabularySearchRepository vocabularySearchRepository, ElasticsearchTemplate2 elasticsearchTemplate) {
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyMapper = vocabularyMapper;
        this.vocabularySearchRepository = vocabularySearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    /**
     * Save a vocabulary.
     *
     * @param vocabularyDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public VocabularyDTO save(VocabularyDTO vocabularyDTO) {
        log.debug("Request to save Vocabulary : {}", vocabularyDTO);
        Vocabulary vocabulary = vocabularyMapper.toEntity(vocabularyDTO);
        vocabulary = vocabularyRepository.save(vocabulary);
        VocabularyDTO result = vocabularyMapper.toDto(vocabulary);
//        if( vocabulary.isDiscoverable() != null && vocabulary.isDiscoverable())
//        	vocabularySearchRepository.save(vocabulary);
        return result;
    }

    /**
     * Get all the vocabularies.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<VocabularyDTO> findAll() {
        log.debug("Request to get all Vocabularies");
        return vocabularyRepository.findAll().stream()
            .map(vocabularyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    /**
     * Get all the vocabularies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Vocabularies");
        return vocabularyRepository.findAll(pageable)
            .map(vocabularyMapper::toDto);
    }


    /**
     * Get one vocabulary by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public VocabularyDTO findOne(Long id) {
        log.debug("Request to get Vocabulary : {}", id);
        Vocabulary vocabulary = vocabularyRepository.getOne(id);
        return vocabularyMapper.toDto(vocabulary);
    }

    /**
     * Delete the vocabulary by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vocabulary : {}", id);
        vocabularyRepository.deleteById(id);
        
    }
    
	@Override
	public void delete(VocabularyDTO vocabulary) {
		if(  vocabulary.isDiscoverable() != null && vocabulary.isDiscoverable() )
			vocabularySearchRepository.deleteById( vocabulary.getId() );
		
		delete( vocabulary.getId() );
	}

    /**
     * Search for the vocabulary corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Vocabularies for query {}", query);
        Page<Vocabulary> result = vocabularySearchRepository.search(queryStringQuery(query), pageable);
        return result.map(vocabularyMapper::toDto);
    }

	@Override
	public VocabularyDTO getByUri(String cvUri) {
		Vocabulary vocabulary = vocabularyRepository.findByUri( cvUri );
        return vocabularyMapper.toDto(vocabulary);
	}

	@Override
	public List<VocabularyDTO> findByAgency(Long agencyId) {
		 log.debug("Request to get all Vocabularies by agency");
	        return vocabularyRepository.findByAgency(agencyId).stream()
	            .map(vocabularyMapper::toDto)
	            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public VocabularyDTO getByNotation(String notation) {
		Vocabulary vocabulary = vocabularyRepository.findByNotation( notation );
        return vocabularyMapper.toDto(vocabulary);
	}
	
	// SEARCH
	// notation
	// title
	// definition
	
	// AGGR
	// agencyName
	// languages
	
	// Pageable and Sort

	@SuppressWarnings("rawtypes")
	@Override
	public EsQueryResultDetail search(EsQueryResultDetail esQueryResultDetail) {
		String searchTerm = esQueryResultDetail.getSearchTerm();
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
			.withQuery(  generateMainAndNestedQuery ( searchTerm ))
			.withSearchType(SearchType.DEFAULT)
			.withIndices("vocabulary").withTypes("vocabulary")
			.withFilter( generateFilterQuery( esQueryResultDetail.getFilterItems()) )
			.withPageable( esQueryResultDetail.getPage());
		
		// add sorting
		if(esQueryResultDetail.getFirstSortOrder().equals("_score") )
			searchQueryBuilder.withSort( SortBuilders.scoreSort());
		else {
			Order order = esQueryResultDetail.getFirstSortOrder();
			searchQueryBuilder.withSort( SortBuilders.fieldSort( order.getProperty()).order( order.getDirection().equals( Direction.ASC) ? SortOrder.ASC : SortOrder.DESC));
		}
		
		// add aggregation
		List<AbstractAggregationBuilder> aggregrations =  generateAggregrations( esQueryResultDetail.getFilterItems(), esQueryResultDetail.getAggFields());
		for(AbstractAggregationBuilder aggregration : aggregrations )
			searchQueryBuilder.addAggregation(aggregration);
		
		if( searchTerm != null && !searchTerm.isEmpty())
			searchQueryBuilder.withHighlightFields( generateHighlightBuilder() );
		
		// at the end build search query
		SearchQuery searchQuery = searchQueryBuilder.build();
				
		Page<VocabularyDTO> vocabulary = elasticsearchTemplate.queryForPage( searchQuery, Vocabulary.class).map(vocabularyMapper::toDto);
		
		// get search response for aggregation, hits, inner hits and highlighter
		SearchResponse searchResponse = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<SearchResponse>() {
			@Override
			public SearchResponse extract(SearchResponse response) {
				return response;
			}
		});
		
		Aggregations aggregations = searchResponse.getAggregations();
		esQueryResultDetail.setAggregations(aggregations);
		
		// update vocabulary based on highlight and inner hit
		if( esQueryResultDetail.getSearchTerm() != null && !esQueryResultDetail.getSearchTerm().isEmpty()) {
			Map<String, SearchHits> innerHits = new LinkedHashMap<>();
			for(SearchHit hit : searchResponse.getHits()) {
				if( VocabularyDTO.findByIdFromList( vocabulary.getContent(), hit.getId()).isPresent()) {
					VocabularyDTO cvHit = VocabularyDTO.findByIdFromList( vocabulary.getContent(), hit.getId()).get();
					
					if( hit.getHighlightFields() != null ) {
						
						for (Map.Entry<String, HighlightField> entry : hit.getHighlightFields().entrySet()) {
							String fieldName = entry.getKey();
							HighlightField highlighField = entry.getValue();
							StringBuilder highLightText = new StringBuilder();
							for( Text text: highlighField.getFragments()) {
								highLightText.append( text.string() + " ");
							}
							java.lang.reflect.Field declaredField = null;
							try {
								declaredField = VocabularyDTO.class.getDeclaredField( fieldName );
								declaredField.setAccessible( true );
								declaredField.set(cvHit, highLightText.toString());
							} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
								e.printStackTrace();
							}
							
								
						}
						
					}
					
					
				}
				
//				VocabularyDTO.findByIdFromList( vocabulary.getContent(), hit.getId()).ifPresent( cvHit -> {
//					if( hit.getHighlightFields() != null ) {
//						for ( String key : hit.getSource().keySet() ) {
//							java.lang.reflect.Field declaredField = null;
////							declaredField = VocabularyDTO.class.getDeclaredField( hit.)
//						}
//						
//					}
//				});
				innerHits.putAll( hit.getInnerHits() );
			}
			esQueryResultDetail.setInnerHits(innerHits);
			
			esQueryResultDetail.setVocabularies(vocabulary);
		} else {
			// no search term then just assign vocabulary as it is
			esQueryResultDetail.setVocabularies(vocabulary);
		}
		
		return esQueryResultDetail;
	}
	
	public static HighlightBuilder.Field[] generateHighlightBuilder() {
		List<HighlightBuilder.Field> fields = new ArrayList<>();
		// all language fields
		for(String langIso : Language.getCapitalizedEnum()) {
			fields.add( new HighlightBuilder.Field( "title" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>") );
			fields.add( new HighlightBuilder.Field( "definition" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>") );
			fields.add( new HighlightBuilder.Field( CODE_PATH +".title" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>") );
			fields.add( new HighlightBuilder.Field( CODE_PATH +".definition" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>") );
		}
		
//		fields.add( new HighlightBuilder.Field( "definitionFi" ).preTags("<span class=\"highlight\">").postTags("</span>") );
//		fields.add( new HighlightBuilder.Field( "definitionEn" ).preTags("<span class=\"highlight\">").postTags("</span>") );
		return fields.toArray( new HighlightBuilder.Field[ fields.size() ] );
	}
	
	public static QueryBuilder generateMainAndNestedQuery( String term ) {
		if( term != null && !term.isEmpty())
			return QueryBuilders.boolQuery().should( generateMainQuery( term ) ).should( generateNestedQuery( term ) );
		else
			return QueryBuilders.matchAllQuery();
	}
	
	public static QueryBuilder generateMainQuery( String term ) {
		// query for all languages
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		
		// all language fields
		for(String langIso : Language.getCapitalizedEnum()) {
			boolQuery
				.should( QueryBuilders.matchQuery( "title" + langIso, term).boost( 3.0f ))
				.should( QueryBuilders.matchQuery( "definition" + langIso, term).boost( 2.0f ));
		}
//		boolQuery.should( QueryBuilders.termQuery( "notation", term).boost( 2.0f ));
		
		return boolQuery;
	}
	
	public static QueryBuilder generateNestedQuery( String term ) {
		// query for all languages
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		
		// all language fields
		for(String langIso : Language.getCapitalizedEnum()) {
			boolQuery
				.should( QueryBuilders.matchQuery( CODE_PATH +".title" + langIso, term).boost( 2.0f ))
				.should( QueryBuilders.matchQuery( CODE_PATH +".definition" + langIso, term).boost( 1.0f ));
		}
//		boolQuery.should( QueryBuilders.termQuery( CODE_PATH +".notation", term).boost( 2.0f ));
		
		return QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.None).innerHit( new InnerHitBuilder());
	}
	
	public static  QueryBuilder generateFilterQuery( List<FilterItem> filterItems ) {
		BoolQueryBuilder boolQueryFilter = QueryBuilders.boolQuery();
		
		for(FilterItem filterItem : filterItems) {
			boolQueryFilter.must(QueryBuilders.termQuery( filterItem.getField(), filterItem.getValue()));
		}
		
		return boolQueryFilter;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<AbstractAggregationBuilder> generateAggregrations( List<FilterItem> filterItems, List<String> aggFields){
		Map<String, List<FilterItem>> activeAggsMap = new LinkedHashMap<>();
		
		if( filterItems != null && !filterItems.isEmpty()) {
			for( FilterItem filterItem : filterItems) {
				if( aggFields.contains( filterItem.getField())) {
					List<FilterItem> activeAggs = activeAggsMap.get( filterItem.getField() );
					if( activeAggs == null ) {
						activeAggs = new ArrayList<>();
						activeAggsMap.put( filterItem.getField(), activeAggs );
					}
					activeAggs.add(filterItem);
				}
			}
		}
		
		List<AbstractAggregationBuilder> aggBuilders = new ArrayList<>();
		
		if( filterItems == null || filterItems.isEmpty() || activeAggsMap.isEmpty() ) {
			for(String aggField: aggFields)
				aggBuilders.add( AggregationBuilders.terms( aggField + "_count").field( aggField));
		}
		else {
			FiltersAggregationBuilder filtersAggregation = AggregationBuilders.filters( "aggregration_filter", generateFilterQuery( filterItems )) ;
			
			for(String aggField: aggFields)
				filtersAggregation.subAggregation( AggregationBuilders.terms( aggField + "_count").field( aggField ) );
			
			aggBuilders.add(filtersAggregation);
		}
		return aggBuilders;
	}
}
