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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filters.Filters;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
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
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cessda.cvmanager.domain.Code;
import eu.cessda.cvmanager.domain.Version;
import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.domain.VocabularyPublish;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.repository.CodeRepository;
import eu.cessda.cvmanager.repository.VersionRepository;
import eu.cessda.cvmanager.repository.VocabularyRepository;
import eu.cessda.cvmanager.repository.search.VocabularyPublishSearchRepository;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ElasticsearchTemplate2;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.service.mapper.VocabularyPublishMapper;
import eu.cessda.cvmanager.ui.view.publication.EsFilter;
import eu.cessda.cvmanager.ui.view.publication.EsQueryResultDetail;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Vocabulary.
 */
@Service
@Transactional
public class VocabularyServiceImpl implements VocabularyService {
	
	private static final String CODE_PATH = "codes";

    private final Logger log = LoggerFactory.getLogger(VocabularyServiceImpl.class);
    
    private final CodeService codeService;
    
    private final VersionRepository versionRepository;

    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;
    
    private final VocabularyPublishMapper vocabularyPublishMapper;

    private final VocabularySearchRepository vocabularySearchRepository;
    
    private final VocabularyPublishSearchRepository vocabularyPublishSearchRepository;

    // Use original ElasticsearchTemplate if this bug is fixed
    // https://jira.spring.io/browse/DATAES-412
    private final ElasticsearchTemplate2 elasticsearchTemplate;

    public VocabularyServiceImpl(VocabularyRepository vocabularyRepository, VocabularyMapper vocabularyMapper, 
    		VocabularySearchRepository vocabularySearchRepository, ElasticsearchTemplate2 elasticsearchTemplate,
    		VocabularyPublishMapper vocabularyPublishMapper, VersionRepository versionRepository, CodeService codeService,
    		VocabularyPublishSearchRepository vocabularyPublishSearchRepository) {
    	this.codeService = codeService;
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyMapper = vocabularyMapper;
        this.vocabularyPublishMapper = vocabularyPublishMapper;
        this.vocabularySearchRepository = vocabularySearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.versionRepository = versionRepository;
        this.vocabularyPublishSearchRepository = vocabularyPublishSearchRepository;
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
        
        // store version first if exist
        for( Version version: vocabulary.getVersions()) {
        	version = versionRepository.save( version );
        }
        
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
	
	@Override
	public boolean existsByNotation(String notation) {
		return vocabularyRepository.existsByNotation( notation );
	}
	
	
	
	
	/**
	 *  Main search method
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public EsQueryResultDetail search( EsQueryResultDetail esQueryResultDetail ) {
		String searchTerm = esQueryResultDetail.getSearchTerm();
		// build query 
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
			.withQuery(  generateMainAndNestedQuery ( searchTerm ))
			.withSearchType(SearchType.DEFAULT)
			.withIndices("vocabulary").withTypes("vocabulary")
			.withFilter( generateFilterQuery( esQueryResultDetail.getEsFilters()) )
			.withPageable( esQueryResultDetail.getPage());
		
		// add sorting
		if(esQueryResultDetail.getFirstSortOrder().getProperty().equals("_score") )
			searchQueryBuilder.withSort( SortBuilders.scoreSort());
		else {
			Order order = esQueryResultDetail.getFirstSortOrder();
			searchQueryBuilder.withSort( SortBuilders.fieldSort( order.getProperty()).order( order.getDirection().equals( Direction.ASC) ? SortOrder.ASC : SortOrder.DESC));
		}
		
		// add aggregation
		List<AbstractAggregationBuilder> aggregrations =  generateAggregrations( esQueryResultDetail );
		for(AbstractAggregationBuilder aggregration : aggregrations )
			searchQueryBuilder.addAggregation(aggregration);
		
		// add highlighter
		if( searchTerm != null && !searchTerm.isEmpty())
			searchQueryBuilder.withHighlightFields( generateHighlightBuilderMain() );
		
		// at the end build search query
		SearchQuery searchQuery = searchQueryBuilder.build();
		
		Page<Vocabulary> vocPage = elasticsearchTemplate.queryForPage( searchQuery, Vocabulary.class);
		
		// put the vocabulary results
		Page<VocabularyDTO> vocabularyPage = elasticsearchTemplate.queryForPage( searchQuery, Vocabulary.class).map(vocabularyMapper::toDto);
		
		// get search response for aggregation, hits, inner hits and highlighter
		SearchResponse searchResponse = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<SearchResponse>() {
			@Override
			public SearchResponse extract(SearchResponse response) {
				return response;
			}
		});
		
		// assign aggregation to esQueryResultDetail
		generateAggregrationFilter(esQueryResultDetail, searchResponse);
		
		// update vocabulary based on highlight and inner hit
		if( esQueryResultDetail.getSearchTerm() != null && !esQueryResultDetail.getSearchTerm().isEmpty()) {
			applySearchHitAndHighlight(vocabularyPage, searchResponse);
			
			esQueryResultDetail.setVocabularies(vocabularyPage);
		} else {
			// remove unnecessary nested code entities
			for( VocabularyDTO vocab : vocabularyPage.getContent())
				vocab.setCodes( Collections.emptySet() );
			// no search term then just assign vocabulary as it is
			esQueryResultDetail.setVocabularies(vocabularyPage);
		}
		
		return esQueryResultDetail;
	}

	private void generateAggregrationFilter(EsQueryResultDetail esQueryResultDetail, SearchResponse searchResponse) {
		// generate aggregation filter
		for( String field : esQueryResultDetail.getAggFields() ) {
			if( !esQueryResultDetail.isAnyFilterActive()) {
				Terms aggregation = searchResponse.getAggregations().get( field + "_count");//esQueryResultDetail.getAggregations().get( field + "_count");
				
				if( aggregation == null)
					continue;

				esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter -> {
					esFilter.clearBucket();
					for (Terms.Bucket b : aggregation.getBuckets()) 
						esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
				});
				
				
			}
			else {
				Filters aggFilters= searchResponse.getAggregations().get( "aggregration_filter");
				if( aggFilters == null )
					continue;
				
				esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter -> {
					esFilter.clearBucket();
					
					@SuppressWarnings("unchecked")
					Collection<Filters.Bucket> buckets = (Collection<Filters.Bucket>) aggFilters.getBuckets();
					
					for (Filters.Bucket bucket : buckets) {
						
						if (bucket.getDocCount() == 0)
							continue;
						
						Terms terms = bucket.getAggregations().get(field + "_count");
						
		                @SuppressWarnings("unchecked")
						Collection<Terms.Bucket> bkts = (Collection<Bucket>) terms.getBuckets();
		                for (Bucket b : bkts) {
		                	
		                	if (b.getDocCount() == 0)
		                		continue;
		                	
		                	if( !EsFilter.isActiveFilter(esFilter, b.getKeyAsString()) )
		                		esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
		                	else
		                		esFilter.addFilteredBucket( b.getKeyAsString(), b.getDocCount() );
		                }
						
					}
				});
			}
		}
	}

	private void applySearchHitAndHighlight(Page<VocabularyDTO> vocabularyPage, SearchResponse searchResponse) {
		for(SearchHit hit : searchResponse.getHits()) {
			if( VocabularyDTO.findByIdFromList( vocabularyPage.getContent(), hit.getId()).isPresent()) {
				VocabularyDTO cvHit = VocabularyDTO.findByIdFromList( vocabularyPage.getContent(), hit.getId()).get();
				
				if( hit.getHighlightFields() != null ) 
					applyVocabularyHighlighter(hit, cvHit);
					
				if( hit.getInnerHits() == null )
					continue;
				
				applyCodeHighlighter(hit, cvHit);
				
			}
		}
	}
	
	private void applyVocabularyHighlighter(SearchHit hit, VocabularyDTO cvHit) {
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
				
				setSelectedLanguageByHighlight( cvHit, fieldName );
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
				
		}
	}

	private void applyCodeHighlighter(SearchHit hit, VocabularyDTO cvHit) {
		Set<CodeDTO> newCodes = new LinkedHashSet<>();
		
		for( Map.Entry<String, SearchHits> innerHitEntry : hit.getInnerHits().entrySet()) {
			SearchHits innerHits = innerHitEntry.getValue();
			
			for( SearchHit innerHit: innerHits) {
				
				if( innerHit.getHighlightFields().isEmpty())
					continue;
				
				if(cvHit.getCodes() == null )
					continue;
				
				if( innerHit.getSource().get("id") == null )
					continue;
				
				if( CodeDTO.findByIdFromList( cvHit.getCodes() , (int) innerHit.getSource().get("id")).isPresent()) {
					CodeDTO codeHit = CodeDTO.findByIdFromList( cvHit.getCodes(),  (int) innerHit.getSource().get("id")).get();
					
					if( innerHit.getHighlightFields() != null ) {
						for (Map.Entry<String, HighlightField> entry : innerHit.getHighlightFields().entrySet()) {
							String fieldName = entry.getKey();
							HighlightField highlighField = entry.getValue();
							StringBuilder highLightText = new StringBuilder();
							for( Text text: highlighField.getFragments()) {
								highLightText.append( text.string() + "   ");
							}
							java.lang.reflect.Field declaredField = null;
							try {
								declaredField = CodeDTO.class.getDeclaredField( fieldName.substring( CODE_PATH.length() + 1 ) );
								declaredField.setAccessible( true );
								declaredField.set(codeHit, highLightText.toString());
								
								setSelectedLanguageByHighlight( cvHit, fieldName );
							} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
								e.printStackTrace();
							}				
								
						}					
					}
					newCodes.add(codeHit);
				}
			}
		}
		
		cvHit.setCodes(newCodes);
	}

	
	// set selected language based on highlight
	private void setSelectedLanguageByHighlight( VocabularyDTO cvHit, String highlightField) {
		// only set selected language once
		if( cvHit.getSelectedLang() == null ) {
			// get last language information from the field and get the Language enum
			String langIso = highlightField.substring( highlightField.length() - 2, highlightField.length());
			Language lang = Language.getEnum( langIso );
			cvHit.setSelectedLang(lang);
		}
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
	
	public static HighlightBuilder.Field[] generateHighlightBuilderMain() {
		List<HighlightBuilder.Field> fields = new ArrayList<>();
		// all language fields
		for(String langIso : Language.getCapitalizedEnum()) {
			fields.add( new HighlightBuilder.Field( "title" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>") );
			fields.add( new HighlightBuilder.Field( "definition" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>") );
		}
		return fields.toArray( new HighlightBuilder.Field[ fields.size() ] );
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
		
		return QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.None)
				.innerHit( new InnerHitBuilder( CODE_PATH )
				.setHighlightBuilder( nestedHighlightBuilder() ));
	}
	
	private static HighlightBuilder nestedHighlightBuilder() {
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// all language fields
		for(String langIso : Language.getCapitalizedEnum()) {
			highlightBuilder.field( CODE_PATH +".title" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>" );
			highlightBuilder.field( CODE_PATH +".definition" + langIso ).preTags("<span class=\"highlight\">").postTags("</span>" );
		}
		return highlightBuilder;
	}
	
	public static QueryBuilder generateFilterQuery( List<EsFilter> esFilters ) {
		BoolQueryBuilder boolQueryFilter = QueryBuilders.boolQuery();
		
		for( EsFilter esFilter : esFilters ) {
			if( esFilter.getValues() != null && !esFilter.getValues().isEmpty()) {
				if( esFilter.getValues().size() == 1) { // use AND if the an iten in the filter is selected
					boolQueryFilter.must(QueryBuilders.termQuery( esFilter.getField(), esFilter.getValues().get(0) ));
				} else {
					BoolQueryBuilder withinFilterBoolQueryFilter = QueryBuilders.boolQuery();
					// use OR for multiple values within a filter
					for( String filterValue : esFilter.getValues() ) {
						withinFilterBoolQueryFilter.should(QueryBuilders.termQuery( esFilter.getField(), filterValue ));
					}
					// use AND to combine filter with the rest
					boolQueryFilter.must( withinFilterBoolQueryFilter );
				}
			}
			
		}
		
		return boolQueryFilter;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<AbstractAggregationBuilder> generateAggregrations( EsQueryResultDetail esQueryResultDetail ){

		List<AbstractAggregationBuilder> aggBuilders = new ArrayList<>();
		
		if( !esQueryResultDetail.isAnyFilterActive() ) {
			for(String aggField: esQueryResultDetail.getAggFields())
				aggBuilders.add( AggregationBuilders.terms( aggField + "_count").field( aggField));
		}
		else {
			FiltersAggregationBuilder filtersAggregation = AggregationBuilders.filters( "aggregration_filter", generateFilterQuery( esQueryResultDetail.getEsFilters() )) ;
			
			for(String aggField: esQueryResultDetail.getAggFields() )
				filtersAggregation.subAggregation( AggregationBuilders.terms( aggField + "_count").field( aggField ) );
			
			aggBuilders.add(filtersAggregation);
		}
		return aggBuilders;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public EsQueryResultDetail searchPublished( EsQueryResultDetail esQueryResultDetail ) {
		String searchTerm = esQueryResultDetail.getSearchTerm();
		// build query 
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
			.withQuery(  generateMainAndNestedQuery ( searchTerm ))
			.withSearchType(SearchType.DEFAULT)
			.withIndices("vocabulary-publish").withTypes("vocabularypublish")
			.withFilter( generateFilterQuery( esQueryResultDetail.getEsFilters()) )
			.withPageable( esQueryResultDetail.getPage());
		
		// add sorting
		if(esQueryResultDetail.getFirstSortOrder().getProperty().equals("_score") )
			searchQueryBuilder.withSort( SortBuilders.scoreSort());
		else {
			Order order = esQueryResultDetail.getFirstSortOrder();
			searchQueryBuilder.withSort( SortBuilders.fieldSort( order.getProperty()).order( order.getDirection().equals( Direction.ASC) ? SortOrder.ASC : SortOrder.DESC));
		}
		
		// add aggregation
		List<AbstractAggregationBuilder> aggregrations =  generateAggregrations( esQueryResultDetail );
		for(AbstractAggregationBuilder aggregration : aggregrations )
			searchQueryBuilder.addAggregation(aggregration);
		
		// add highlighter
		if( searchTerm != null && !searchTerm.isEmpty())
			searchQueryBuilder.withHighlightFields( generateHighlightBuilderMain() );
		
		// at the end build search query
		SearchQuery searchQuery = searchQueryBuilder.build();
		
		Page<Vocabulary> vocPage = elasticsearchTemplate.queryForPage( searchQuery, Vocabulary.class);
		
		// put the vocabulary results
		Page<VocabularyDTO> vocabularyPage = elasticsearchTemplate.queryForPage( searchQuery, Vocabulary.class).map(vocabularyMapper::toDto);
		
		// get search response for aggregation, hits, inner hits and highlighter
		SearchResponse searchResponse = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<SearchResponse>() {
			@Override
			public SearchResponse extract(SearchResponse response) {
				return response;
			}
		});
		
		// assign aggregation to esQueryResultDetail
		generateAggregrationFilter(esQueryResultDetail, searchResponse);
		
		// update vocabulary based on highlight and inner hit
		if( esQueryResultDetail.getSearchTerm() != null && !esQueryResultDetail.getSearchTerm().isEmpty()) {
			applySearchHitAndHighlight(vocabularyPage, searchResponse);
			
			esQueryResultDetail.setVocabularies(vocabularyPage);
		} else {
			// remove unnecessary nested code entities
			for( VocabularyDTO vocab : vocabularyPage.getContent())
				vocab.setCodes( Collections.emptySet() );
			// no search term then just assign vocabulary as it is
			esQueryResultDetail.setVocabularies(vocabularyPage);
		}
		
		return esQueryResultDetail;
	}

	@Override
	public void index(VocabularyDTO vocabulary) {
		// index nested object as well
		// get version
		vocabulary.setVers( vocabulary.getLatestVersions());
		// set languages
		vocabulary.setLanguages( VocabularyDTO.getLanguagesFromVersions( vocabulary.getVers() ));
		// get codes
		List<CodeDTO> codes = codeService.findByVocabulary( vocabulary.getId());
		vocabulary.setCodes( new HashSet<>( codes ));
		
		
		Vocabulary vocab = vocabularyMapper.toEntity( vocabulary);
		vocabularySearchRepository.save( vocab );
	}
	
	@Override
	public void indexPublish(VocabularyDTO vocabulary) {
		// index nested object as well
		// get latest version
		vocabulary.setVers( vocabulary.getLatestVersions( Status.PUBLISHED.toString() ));
		
		// set languages from latest version
		vocabulary.setLanguages( VocabularyDTO.getLanguagesFromVersions( vocabulary.getVers() ));
		// set published languages across versions
		vocabulary.setLanguagesPublished( VocabularyDTO.getPublishedLanguagesFromVersions( vocabulary.getVers() ));
		// update/generate vocabulary content
		// 1. clear vocabulary DTO first before replacing content
		vocabulary.clearContent();
		// 2. generate vocabulary content from version
		for( VersionDTO versionDTO : vocabulary.getVers()) {
			Language versionLanguage = Language.getEnumByName( versionDTO.getLanguage() );
			vocabulary.setVersionByLanguage(versionLanguage, versionDTO.getNumber());
			vocabulary.setTitleDefinition( versionDTO.getTitle(), versionDTO.getDefinition(), versionLanguage);
		}
		
		// 3. generate code content from version, create codeMap first
		List<CodeDTO> codes = codeService.findByVocabulary( vocabulary.getId());
		Map<String, CodeDTO> codesMap = new HashMap<>();
		for( CodeDTO eachCode: codes) {
			eachCode.clearCode();
			codesMap.put( eachCode.getNotation(), eachCode);
		}
		for( VersionDTO versionDTO : vocabulary.getVers()) {
			Language versionLanguage = Language.getEnumByName( versionDTO.getLanguage() );
			for( ConceptDTO concept : versionDTO.getConcepts()) {
				CodeDTO targetCode = codesMap.get( concept.getNotation());
				if( targetCode == null )
					continue;
				
				targetCode.setTitleDefinition( concept.getTitle(), concept.getDefinition(), versionLanguage);
			}
		}
		vocabulary.setCodes( new HashSet<>( codesMap.values() ));
		
		VocabularyPublish vocab = vocabularyPublishMapper.toEntity( vocabulary);
		vocabularyPublishSearchRepository.save( vocab );
	}
}
