/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.domain.search.VocabularyEditor;
import eu.cessda.cvs.domain.search.VocabularyPublish;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.LicenceRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.repository.search.AgencyStatSearchRepository;
import eu.cessda.cvs.repository.search.VocabularyEditorSearchRepository;
import eu.cessda.cvs.repository.search.VocabularyPublishSearchRepository;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.*;
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.service.mapper.VocabularyEditorMapper;
import eu.cessda.cvs.service.mapper.VocabularyMapper;
import eu.cessda.cvs.service.mapper.VocabularyPublishMapper;
import eu.cessda.cvs.service.search.EsFilter;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.utils.VersionNumber;
import eu.cessda.cvs.utils.VocabularyUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service Implementation for managing {@link Vocabulary}.
 */
@Service
@Transactional
public class VocabularyServiceImpl implements VocabularyService
{

    public static final String COUNT = "_count";
    public static final String TITLE = "title";
    public static final String DEFINITION = "definition";
    public static final String NOTATION = "notation";
    public static final String HIGHLIGHT_START = "<span class=\"highlight\">";
    public static final String HIGHLIGHT_END = "</span>";
    public static final int SIZE_OF_ITEMS_ON_AGGREGATION = 10000;
    public static final String JSON_FORMAT = ".json";
    public static final String UNABLE_FIND_VOCABULARY = "Unable to find vocabulary with Id ";
    public static final String UNABLE_FIND_VERSION = "Unable to find version with Id ";
    public static final String LATEST = "latest";
    public static final String ALL = "all";
    private static final Logger log = LoggerFactory.getLogger( VocabularyServiceImpl.class );
    private static final String VOCABULARYPUBLISH = "vocabularypublish";
    private static final String UNABLE_TO_FIND_VERSION = "Unable to find version with Id ";
    private static final String UNABLE_TO_FIND_VOCABULARY = "Unable to find vocabulary with Id ";
    private static final String CODE_PATH = "codes";

    private final AgencyRepository agencyRepository;

    private final ConceptService conceptService;

    private final ExportService exportService;

    private final LicenceRepository licenceRepository;

    private final VersionService versionService;

    private final VocabularyChangeService vocabularyChangeService;

    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;

    private final VocabularyEditorMapper vocabularyEditorMapper;

    private final VocabularyPublishMapper vocabularyPublishMapper;

    private final VocabularyEditorSearchRepository vocabularyEditorSearchRepository;

    private final VocabularyPublishSearchRepository vocabularyPublishSearchRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    private final ApplicationProperties applicationProperties;

    private final AgencyStatSearchRepository agencyStatSearchRepository;

    @SuppressWarnings( "squid:S107" ) // since constructor params are autowired
    public VocabularyServiceImpl( AgencyRepository agencyRepository, ConceptService conceptService, ExportService exportService,
                                  LicenceRepository licenceRepository, VersionService versionService,
                                  VocabularyChangeService vocabularyChangeService, VocabularyRepository vocabularyRepository,
                                  VocabularyMapper vocabularyMapper, VocabularyEditorMapper vocabularyEditorMapper,
                                  VocabularyPublishMapper vocabularyPublishMapper, VocabularyEditorSearchRepository vocabularyEditorSearchRepository,
                                  VocabularyPublishSearchRepository vocabularyPublishSearchRepository,
                                  ElasticsearchOperations elasticsearchOperations, ApplicationProperties applicationProperties,
                                  AgencyStatSearchRepository agencyStatSearchRepository ) {
        this.agencyRepository = agencyRepository;
        this.conceptService = conceptService;
        this.exportService = exportService;
        this.licenceRepository = licenceRepository;
        this.versionService = versionService;
        this.vocabularyChangeService = vocabularyChangeService;
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyMapper = vocabularyMapper;
        this.vocabularyEditorMapper = vocabularyEditorMapper;
        this.vocabularyPublishMapper = vocabularyPublishMapper;
        this.vocabularyEditorSearchRepository = vocabularyEditorSearchRepository;
        this.vocabularyPublishSearchRepository = vocabularyPublishSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.applicationProperties = applicationProperties;
        this.agencyStatSearchRepository = agencyStatSearchRepository;
    }

    public static QueryBuilder generateMainAndNestedQuery( String term, List<String> languageFields, int innerHitSize ) {
        if (term != null && term.length() > 1)
        {
            return QueryBuilders.boolQuery().should( generateMainQuery( term, languageFields ) )
                // Append nested query
                .should( generateNestedQuery( term, languageFields, innerHitSize ) );
        }
        else
        {
            return QueryBuilders.matchAllQuery();
        }
    }

    public static QueryBuilder generateMainQuery( String term, List<String> languageFields ) {
        QueryBuilder query;
        if ( languageFields.size() == 1 )
        {
            query = QueryBuilders.boolQuery()
                .should( QueryBuilders.matchQuery( TITLE + languageFields.get( 0 ), term ).fuzziness( 0.7 ).boost( 10.0f ) )
                .should( QueryBuilders.matchQuery( DEFINITION + languageFields.get( 0 ), term ).fuzziness( 0.7 ).boost( 4.0f ) )
                .should( QueryBuilders.wildcardQuery( NOTATION, term.toLowerCase().replace( " ", "" ) + "*" ).boost( 2.0f ) );
        }
        else
        {
            List<String> fields = new ArrayList<>( ( languageFields.size() * 2 ) + 1 );
            fields.add( NOTATION );
            for ( String langIso : languageFields )
            {
                fields.add( TITLE + langIso );
                fields.add( DEFINITION + langIso );
            }
           query = QueryBuilders.multiMatchQuery( term, fields.toArray( String[]::new ) );
        }
        return query;
    }

    public static NestedQueryBuilder generateNestedQuery( String term, List<String> languageFields, int innerHitSize ) {
        // query for all languages
        QueryBuilder query;

        if ( languageFields.size() == 1 )
        {
            query = QueryBuilders.boolQuery()
                .should( QueryBuilders.matchQuery( CODE_PATH + "." + TITLE + languageFields.get( 0 ), term ).fuzziness( 0.7 )
                    .boost( 3.0f ) )
                .should( QueryBuilders.matchQuery( CODE_PATH + "." + DEFINITION + languageFields.get( 0 ), term ).fuzziness( 0.7 )
                    .boost( 2.0f ) )
                .should( QueryBuilders.wildcardQuery( CODE_PATH + "." + NOTATION, term.toLowerCase().replace( " ", "" ) + "*" )
                    .boost( 1.0f ) );
        }
        else
        {
            List<String> fields = new ArrayList<>(( languageFields.size() * 2 ) + 1 );
            fields.add( CODE_PATH + "." + NOTATION );
            for ( String langIso : languageFields ) {
                fields.add( CODE_PATH + "." + TITLE + langIso );
                fields.add( CODE_PATH + "." + DEFINITION + langIso );
            }
            query = QueryBuilders.multiMatchQuery( term, fields.toArray( String[]::new ) );
        }

        InnerHitBuilder innerHitBuilder = new InnerHitBuilder( CODE_PATH ).setSize( innerHitSize );
        if ( term.length() > 2 )
        {
            // Configure inner hit highlighters
            var nestedHighlightBuilder = generateHighlightBuilder(
                List.of( CODE_PATH + "." + TITLE, CODE_PATH + "." + DEFINITION ),
                languageFields
            );
            innerHitBuilder.setHighlightBuilder( nestedHighlightBuilder );
        }
        return QueryBuilders.nestedQuery( CODE_PATH, query, ScoreMode.Total ).innerHit( innerHitBuilder );
    }

    private static HighlightBuilder generateHighlightBuilder( List<String> highlightFields, List<String> languageFields )
    {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for ( String field : highlightFields )
        {
            for ( String langIso : languageFields )
            {
                highlightBuilder.field( field + langIso ).preTags( HIGHLIGHT_START ).postTags( HIGHLIGHT_END );
            }
        }
        return highlightBuilder;
    }

    public static QueryBuilder generateFilterQuery( List<EsFilter> esFilters ) {
        BoolQueryBuilder boolQueryFilter = QueryBuilders.boolQuery();

        for ( EsFilter esFilter : esFilters ) {
            if ( esFilter.getValues() != null && !esFilter.getValues().isEmpty() ) {
                if ( esFilter.getValues().size() == 1 ) { // use AND if an item in the filter is selected
                    boolQueryFilter.must( QueryBuilders.termQuery( esFilter.getField(), esFilter.getValues().get( 0 ) ) );
                } else {
                    BoolQueryBuilder withinFilterBoolQueryFilter = QueryBuilders.boolQuery();
                    // use OR for multiple values within a filter
                    for ( String filterValue : esFilter.getValues() ) {
                        withinFilterBoolQueryFilter.should( QueryBuilders.termQuery( esFilter.getField(), filterValue ) );
                    }
                    // use AND to combine filter with the rest
                    boolQueryFilter.must( withinFilterBoolQueryFilter );
                }
            }

        }

        return boolQueryFilter;
    }

    /**
     * Save a vocabulary.
     *
     * @param vocabularyDTO
     *            the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VocabularyDTO save( VocabularyDTO vocabularyDTO ) {
        log.debug( "Request to save Vocabulary : {}", vocabularyDTO );
        Vocabulary vocabulary;
        // for new vocabulary
        if ( !vocabularyDTO.isPersisted() ) {
            vocabulary = createNewVocabulary( vocabularyDTO );
        }
        // if updated vocabulary
        else {
            vocabulary = updateVocabulary( vocabularyDTO );
        }
        vocabularyDTO = vocabularyMapper.toDto( vocabulary );
        // indexing
        indexEditor( vocabularyDTO );
        return vocabularyDTO;
    }

    private Vocabulary updateVocabulary( VocabularyDTO vocabularyDTO ) {
        return vocabularyRepository.save( vocabularyMapper.toEntity( vocabularyDTO ) );
    }

    @Override
    public VocabularyDTO saveVocabulary( VocabularySnippet vocabularySnippet ) {
        if ( vocabularySnippet.getActionType() == ActionType.CREATE_CV ) {
            return save( new VocabularyDTO( vocabularySnippet ) );
        } else if ( vocabularySnippet.getActionType() == ActionType.ADD_TL_CV ) {
            return saveTlVocabulary( vocabularySnippet );
        } else {
            // get Vocabulary from database
            VocabularyDTO vocabularyDTO = findOne( vocabularySnippet.getVocabularyId() )
                .orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVocabularyId() ) );

            // find version on vocabulary
            VersionDTO versionDTO = vocabularyDTO.getVersions().stream()
                .filter( v -> v.getId().equals( vocabularySnippet.getVersionId() ) ).findFirst()
                .orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVersionId() ) );

            if ( vocabularySnippet.getActionType() == ActionType.EDIT_CV ) {
                // check if user authorized to edit VocabularyResource
                SecurityUtils.checkResourceAuthorization( ActionType.EDIT_CV,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
                // set non-editable (via edit-cv-form) versionNumber and language on
                // vocabularySnippet with versionDTO
                vocabularySnippet.setLanguage( versionDTO.getLanguage() );
                vocabularySnippet.setVersionNumber( versionDTO.getNumber() );
                // only title, definition, notes, translateAgency, translateAgencyLink
                vocabularyDTO.setContentByVocabularySnippet( vocabularySnippet );
                versionDTO.setContentByVocabularySnippet( vocabularySnippet );

                // check if codeSnippet contains changeType
                storeChangeType( vocabularySnippet, versionDTO );
            } else if ( vocabularySnippet.getActionType() == ActionType.EDIT_DDI_CV ) {
                SecurityUtils.checkResourceAuthorization( ActionType.EDIT_DDI_CV,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

                versionDTO.setDdiUsage( vocabularySnippet.getDdiUsage() );
            } else if ( vocabularySnippet.getActionType() == ActionType.EDIT_IDENTITY_CV ) {
                SecurityUtils.checkResourceAuthorization( ActionType.EDIT_IDENTITY_CV,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

                versionDTO.setTranslateAgency( vocabularySnippet.getTranslateAgency() );
                versionDTO.setTranslateAgencyLink( vocabularySnippet.getTranslateAgencyLink() );
            } else if ( vocabularySnippet.getActionType() == ActionType.EDIT_VERSION_INFO_CV ) {
                SecurityUtils.checkResourceAuthorization( ActionType.EDIT_VERSION_INFO_CV,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

                updateCvVersionInfo( vocabularySnippet, versionDTO );
            } else {
                SecurityUtils.checkResourceAuthorization( ActionType.EDIT_NOTE_CV,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

                versionDTO.setNotes( vocabularySnippet.getNotes() );
            }

            vocabularyDTO = save( vocabularyDTO );

            // regenerate json file is version already published
            if ( versionDTO.getStatus() == Status.PUBLISHED ) {
                generateJsonVocabularyPublish( vocabularyDTO );
            }
            return vocabularyDTO;
        }
    }

    private void updateCvVersionInfo( VocabularySnippet vocabularySnippet, VersionDTO versionDTO ) {
        versionDTO.setVersionNotes( vocabularySnippet.getVersionNotes() );
        versionDTO.setVersionChanges( vocabularySnippet.getVersionChanges() );

        if ( vocabularySnippet.getVersionNumber() != null )
            versionDTO.setNumber( vocabularySnippet.getVersionNumber() );
        if ( vocabularySnippet.getLicenseId() != null )
            versionDTO.setLicenseId( vocabularySnippet.getLicenseId() );
    }

    @Override
    public VersionDTO createNewVersion(Long prevVersionId) {
        log.debug( "Request to create new Vocabulary version from existing version with ID : {}", prevVersionId );

        // get version
        VersionDTO prevVersionDTO = versionService
            .findOne( prevVersionId )
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VERSION + prevVersionId));

        if ( prevVersionDTO.getStatus() != Status.PUBLISHED ) {
            log.error("Unable to create new version from version with ID {}, version is not yet PUBLISHED", prevVersionDTO.getId());
            throw new IllegalArgumentException(
                "Unable to create new version from version with ID "
                    + prevVersionDTO.getId()
                    + ", version is not yet PUBLISHED"
            );
        }

        VocabularyDTO vocabularyDTO = findOne(prevVersionDTO.getVocabularyId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VOCABULARY + prevVersionDTO.getVocabularyId()));

        VersionDTO currentSlVersion = vocabularyDTO
            .getVersions()
            .stream()
            .filter(v -> v.getItemType() == ItemType.SL )
            .filter(v -> v.getNumber().equals(vocabularyDTO.getVersionNumber()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                UNABLE_FIND_VERSION + "SL version number " + vocabularyDTO.getVersionNumber()
            ));

        VersionDTO newVersion;
        if ( prevVersionDTO.getItemType() == ItemType.SL ){
            // check if user authorized to create new SL version
            SecurityUtils.checkResourceAuthorization(
                ActionType.CREATE_NEW_CV_SL_VERSION,
                vocabularyDTO.getAgencyId(),
                prevVersionDTO.getLanguage()
            );
            newVersion = new VersionDTO(prevVersionDTO, null);
            // update vocabularyDTO
            vocabularyDTO.setVersionNumber(newVersion.getNumber());
            vocabularyDTO.setStatus(Status.DRAFT);
        } else {
            // check if user authorized to create new TL version
            SecurityUtils.checkResourceAuthorization(
                ActionType.CREATE_NEW_CV_TL_VERSION,
                vocabularyDTO.getAgencyId(),
                prevVersionDTO.getLanguage()
            );
            newVersion = new VersionDTO(prevVersionDTO, currentSlVersion);
        }

        newVersion.setCreator(SecurityUtils.getCurrentUserId());
        newVersion = versionService.save( newVersion );

        final VersionDTO finalNewVersion = newVersion;
        currentSlVersion
            .getConcepts()
            .forEach(conceptSlDTO -> {
                ConceptDTO conceptDTO;
                if ( finalNewVersion.getItemType() == ItemType.SL ) {
                    conceptDTO = new ConceptDTO( conceptSlDTO, conceptSlDTO, finalNewVersion.getId() );
                } else {
                    ConceptDTO prevConcept = prevVersionDTO
                        .getConcepts()
                        .stream()
                        .filter(c -> c.getNotation().equals(conceptSlDTO.getNotation()))
                        .findFirst()
                        .orElse(null);
                    conceptDTO = new ConceptDTO(conceptSlDTO, prevConcept, finalNewVersion.getId());
                }
                finalNewVersion.addConcept(conceptDTO);
            });
        // save and reindex
        addVersionAndSaveIndexVocabulary( vocabularyDTO, finalNewVersion );
        return finalNewVersion;
    }

    private void addVersionAndSaveIndexVocabulary( VocabularyDTO vocabularyDTO, VersionDTO finalNewVersion ) {
        // linked vocabulary and version
        vocabularyDTO.addVersion( finalNewVersion );
        // at the end save (version will automatically saved with Cascade)
        Vocabulary vocabulary = vocabularyMapper.toEntity( vocabularyDTO );
        vocabulary = vocabularyRepository.save( vocabulary );
        vocabularyDTO = vocabularyMapper.toDto( vocabulary );
        // index editor
        indexEditor( vocabularyDTO );
    }

    private VocabularyDTO saveTlVocabulary( VocabularySnippet vocabularySnippet ) {
        // get Vocabulary from database
        VocabularyDTO vocabularyDTO = findOne( vocabularySnippet.getVocabularyId() )
            .orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVocabularyId() ) );
        // get slVersion from vocabularySnippet
        VersionDTO versionSlDTO = vocabularyDTO.getVersions().stream().filter( v -> v.getId().equals( vocabularySnippet.getVersionSlId() ) )
            .findFirst()
            .orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVersionSlId() ) );

        // create new tl version by vocabularySnippet
        VersionDTO versionDTO = new VersionDTO( vocabularySnippet, versionSlDTO );
        // find previous version with same language if any
        VersionDTO prevVersionTlDTO = null;
        Optional<VersionDTO> prevVersionTlDTOOptl = vocabularyDTO.getVersions().stream()
            .filter( v -> v.getLanguage().equals( vocabularySnippet.getLanguage() ) ).findFirst();
        if ( prevVersionTlDTOOptl.isPresent() ) {
            prevVersionTlDTO = prevVersionTlDTOOptl.get();
            versionDTO.setPreviousVersion( prevVersionTlDTO.getId() );
            versionDTO.setInitialVersion(
                prevVersionTlDTO.getInitialVersion() == null ? prevVersionTlDTO.getId() : prevVersionTlDTO.getInitialVersion() );
        }
        versionDTO.setCreator( SecurityUtils.getCurrentUserId() );
        // copy DDI-Usage of SL to TL
        versionDTO.setDdiUsage( versionSlDTO.getDdiUsage() );
        versionDTO = versionService.save( versionDTO );
        // generate concepts from currentSlConcept and previousVersion
        generateConceptFromSlAndPrevVersion( versionDTO, versionSlDTO, prevVersionTlDTO );
        // save and reindex
        addVersionAndSaveIndexVocabulary( vocabularyDTO, versionDTO );
        return vocabularyDTO;
    }

    private void generateConceptFromSlAndPrevVersion( VersionDTO versionDTO, VersionDTO versionSlDTO, VersionDTO prevVersionTlDTO ) {
        versionSlDTO.getConcepts().forEach( conceptSlDTO ->
        {
            ConceptDTO conceptDTO = new ConceptDTO( conceptSlDTO );
            conceptDTO.setVersionId( versionDTO.getId() );
            // simply fill with prevVersionTlDTO if exist
            // if on SL notation is changed then the prevConcept will not be linked anymore
            // (Improvement in future lo linked the prev concept with modified notation?)
            if ( prevVersionTlDTO != null ) {
                prevVersionTlDTO.getConcepts().stream().filter( c -> c.getNotation().equals( conceptDTO.getNotation() ) )
                    .findFirst().ifPresent( cPrev ->
                    {
                        conceptDTO.setPreviousConcept( cPrev.getId() );
                        conceptDTO.setTitle( cPrev.getTitle() );
                        conceptDTO.setDefinition( cPrev.getDefinition() );
                    } );
            }
            // add concept to version
            versionDTO.addConcept( conceptDTO );
        } );
    }

    private Vocabulary createNewVocabulary( VocabularyDTO vocabularyDTO ) {
        // check if Vocabulary already exist for new vocabulary
        if ( vocabularyRepository.existsByNotation( vocabularyDTO.getNotation() ) ) {
            throw new VocabularyAlreadyExistException(vocabularyDTO.getNotation());
        }

        // query agency
        final Agency agency = agencyRepository.getOne( vocabularyDTO.getAgencyId() );

        // set Vocabulary attribute for initial version
        vocabularyDTO.setStatus( Status.DRAFT );
        vocabularyDTO.setAgencyLink( agency.getLink() );
        vocabularyDTO.setAgencyName( agency.getName() );
        vocabularyDTO.setAgencyLogo( agency.getLogopath() );

        // create a new version by vocabularyDTO
        VersionDTO versionDTO = new VersionDTO( vocabularyDTO );
        vocabularyDTO.addVersion( versionDTO );
        // add more version details
        versionDTO.setCreator( SecurityUtils.getCurrentUserId() );

        // save vocabulary
        Vocabulary vocabulary = vocabularyMapper.toEntity( vocabularyDTO );
        Version initialSlVersion = vocabulary.getVersions().iterator().next();
        initialSlVersion.setVocabulary( vocabulary );
        // at the end save (version will automatically saved with Cascade)
        vocabulary = vocabularyRepository.save( vocabulary );
        return vocabulary;
    }

    /**
     * Save a concept by codeSnippet
     *
     * @param codeSnippet
     *            the minimized version of Code and Version information
     * @return saved conceptDTO
     */
    @Override
    public ConceptDTO saveCode( CodeSnippet codeSnippet ) {
        // if we add code we need to ensure that it will be cloned to every TL probably as the workflow had been changed!!!
        log.debug( "Request to save Concept and Version from CodeSnippet : {}", codeSnippet.getConceptId() );
        ConceptDTO conceptDTO = null;
        // get version
        VersionDTO versionDTO = versionService.findOne( codeSnippet.getVersionId() )
            .orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + codeSnippet.getVersionId() ) );

        // reject if version status is published
        if ( versionDTO.getStatus() == Status.PUBLISHED ) {
            throw new IllegalArgumentException( "Unable to add Code " + codeSnippet.getNotation() + ", Version is already PUBLISHED" );
        }

        VocabularyDTO vocabularyDTO = findOne( versionDTO.getVocabularyId() )
            .orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VOCABULARY + versionDTO.getVocabularyId() ) );

        switch ( codeSnippet.getActionType() )
        {
            case CREATE_CODE:
                conceptDTO = createCode( codeSnippet, versionDTO, vocabularyDTO );
                break;
            case EDIT_CODE:
            case ADD_TL_CODE:
            case EDIT_TL_CODE:
            case DELETE_TL_CODE:
                conceptDTO = editCode( codeSnippet, versionDTO, vocabularyDTO );
                break;
            default:
                break;
        }

        return conceptDTO;
    }

    private ConceptDTO editCode( CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO ) {
        ConceptDTO conceptDTO;
        // check for authorization
        SecurityUtils.checkResourceAuthorization( codeSnippet.getActionType(), vocabularyDTO.getAgencyId(), versionDTO.getLanguage() );
        // get concept from versionDTO
        conceptDTO = versionDTO.getConcepts().stream().filter( c -> c.getId().equals( codeSnippet.getConceptId() ) ).findFirst()
            .orElseThrow( () -> new EntityNotFoundException( "Unable to find concept with Id " + codeSnippet.getConceptId() ) );

        // check duplicated code notation
        if ( codeSnippet.getActionType() == ActionType.EDIT_CODE && !conceptDTO.getNotation().equals( codeSnippet.getNotation() ) ) {
            if ( versionDTO.getConcepts().stream()
                .anyMatch( c -> c.getNotation().equals( codeSnippet.getNotation() ) ) ) {
                throw new CodeAlreadyExistException( codeSnippet.getNotation() );
            }

            // check if this concept influence parent and child concepts
            for ( ConceptDTO next : versionDTO.getConcepts() ) {
                if ( next.getParent() != null && next.getParent().startsWith( conceptDTO.getNotation() ) ) {
                    next.setParent( next.getParent().replace( conceptDTO.getNotation(), codeSnippet.getNotation() ) );
                    next.setNotation( next.getNotation().replace( conceptDTO.getNotation(), codeSnippet.getNotation() ) );
                }
            }

            // change current concept notation property
            conceptDTO.setNotation( codeSnippet.getNotation() );
        }

        switch ( codeSnippet.getActionType() )
        {
            case EDIT_CODE:
            case ADD_TL_CODE:
            case EDIT_TL_CODE:
                conceptDTO.setTitle( codeSnippet.getTitle() );
                conceptDTO.setDefinition( codeSnippet.getDefinition() );
                break;
            case DELETE_TL_CODE:
                conceptDTO.setTitle( null );
                conceptDTO.setDefinition( null );
                break;
            default:
                break;
        }

        // save versionDTO together with concepts
        versionDTO = versionService.save( versionDTO );

        // check if codeSnippet contains changeType, store if exist
        storeChangeType( codeSnippet, versionDTO );

        // find the newly created code from version
        conceptDTO = versionDTO.findConceptByNotation( conceptDTO.getNotation() );

        // index editor
        indexEditor( vocabularyDTO );
        return conceptDTO;
    }

    private void storeChangeType( VocabularySnippet vocabularySnippet, VersionDTO versionDTO ) {
        if ( vocabularySnippet.getChangeType() != null ) {
            vocabularySnippet.setVersionId( versionDTO.getId() );
            VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO( vocabularySnippet, SecurityUtils.getCurrentUser(),
                versionDTO.getVocabularyId() );
            vocabularyChangeService.save( vocabularyChangeDTO );
        }
    }

    public void storeChangeType( CodeSnippet codeSnippet, VersionDTO versionDTO ) {
        if ( codeSnippet.getChangeType() != null && !versionDTO.isInitialVersion() ) {
            codeSnippet.setVersionId( versionDTO.getId() );
            VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO( codeSnippet, SecurityUtils.getCurrentUser(),
                versionDTO.getVocabularyId() );
            vocabularyChangeService.save( vocabularyChangeDTO );
        }
    }

    private Integer getConceptPositionById( Long conceptId, Set<ConceptDTO> concepts ) {
        if ( conceptId == null ) {
            return null;
        }
        ConceptDTO conceptDTO = concepts.stream()
            .filter( c -> c.getId().compareTo( conceptId ) == 0 ).findFirst().orElse( null );
        return conceptDTO != null ? conceptDTO.getPosition() : null;
    }

    private ConceptDTO createCode( CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO ) {
        ConceptDTO conceptDTO;
        // check for authorization
        SecurityUtils.checkResourceAuthorization( ActionType.CREATE_CODE,
            vocabularyDTO.getAgencyId(), versionDTO.getLanguage() );

        // check if concept already exist for new concept
        if ( versionDTO.getConcepts().stream().anyMatch( c -> c.getNotation().equals( codeSnippet.getNotation() ) ) ) {
            throw new CodeAlreadyExistException( codeSnippet.getNotation() );
        }
        // ==> #349: calculate code position at the server side instead of the client side
        Integer refConceptPos = getConceptPositionById( codeSnippet.getInsertionRefConceptId(), versionDTO.getConcepts() );
        if ( refConceptPos == null ) {
            refConceptPos = versionDTO.getConcepts().size();
        } else {
            Integer relPosToRefConcept = codeSnippet.getRelPosToRefConcept();
            relPosToRefConcept = Math.min( 1, relPosToRefConcept == null ? 0 : Math.max( 0, relPosToRefConcept ) );
            refConceptPos += relPosToRefConcept;
        }
        codeSnippet.setPosition( refConceptPos );
        // <== #349
        // create concept by codeSnippet
        ConceptDTO newConceptDTO = new ConceptDTO( codeSnippet );
        // add concept to version and save version to save new concept
        versionDTO.addConceptAt( newConceptDTO, newConceptDTO.getPosition() );
        versionDTO = versionService.save( versionDTO );

        // check if codeSnippet contains changeType, store if exist
        storeChangeType( codeSnippet, versionDTO );

        // find the newly created code from version
        conceptDTO = versionDTO.findConceptByNotation( newConceptDTO.getNotation() );

        // index editor
        indexEditor( vocabularyDTO );
        return conceptDTO;
    }

    /**
     * Get all the vocabularies.
     *
     * @return the list of entities
     */
    @Override
    @Transactional( readOnly = true )
    public List<VocabularyDTO> findAll() {
        log.debug( "Request to get all Vocabularies" );
        List<VocabularyDTO> vocabulariesDTO = vocabularyRepository.findAll().stream()
            .map( vocabularyMapper::toDto )
            .collect( Collectors.toList() );

        sortVocabularyVersions( vocabulariesDTO );
        return vocabulariesDTO;
    }

    private void sortVocabularyVersions( List<VocabularyDTO> vocabulariesDTO ) {
        for ( VocabularyDTO vocabularyDTO : vocabulariesDTO ) {
            LinkedHashSet<VersionDTO> sortedVersion = vocabularyDTO.getVersions().stream()
                .sorted( VocabularyUtils.VERSION_DTO_COMPARATOR )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
            vocabularyDTO.setVersions( sortedVersion );
        }
    }

    /**
     * Get all the vocabularies.
     *
     * @param pageable
     *            the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional( readOnly = true )
    public Page<VocabularyDTO> findAll( Pageable pageable ) {
        log.debug( "Request to get all Vocabularies" );
        final Page<VocabularyDTO> vocabPage = vocabularyRepository.findAll( pageable ).map( vocabularyMapper::toDto );
        sortVocabularyVersions( vocabPage.getContent() );
        return vocabPage;

    }

    /**
     * Get one vocabulary by id.
     *
     * @param id
     *            the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional( readOnly = true )
    public Optional<VocabularyDTO> findOne( Long id ) {
        log.debug( "Request to get Vocabulary : {}", id );
        return vocabularyRepository.findById( id )
            .map( vocabularyMapper::toDto );
    }

    /**
     * Delete the vocabulary by id.
     *
     * @param id
     *            the id of the entity.
     */
    @Override
    public void delete( Long id ) {
        log.debug( "Request to delete Vocabulary : {}", id );
        VocabularyDTO vocabularyDTO = findOne( id )
            .orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VOCABULARY + id ) );
        String notation = vocabularyDTO.getNotation();

        // update/create agency publish
        agencyStatSearchRepository.findById( vocabularyDTO.getAgencyId() ).ifPresent( agencyPublish ->
        {
            agencyPublish.deleteVocabStat( vocabularyDTO.getNotation() );
            agencyStatSearchRepository.save( agencyPublish );
        } );

        vocabularyRepository.deleteById( id );
        // delete index
        vocabularyEditorSearchRepository.deleteById( id );
        vocabularyPublishSearchRepository.deleteById( id );
        // delete files if any
        deleteCvJsonDirectoryAndContent( applicationProperties.getVocabJsonPath().resolve( notation ) );
    }

    public void deleteCvJsonDirectoryAndContent( Path dirPath ) {
        try {
            PathUtils.deleteDirectory( dirPath );
        } catch ( NoSuchFileException e ) {
            log.trace( "Attempted to delete non-existent dir: {}", dirPath );
        } catch (IOException e) {
            try {
                if ( PathUtils.isEmptyDirectory( dirPath ) ) {
                    // consider an empty directory as a success
                    return;
                }
            } catch ( IOException ede ) {
                e.addSuppressed( ede );
            }

            // directory wasn't empty or an IO error occurred
            log.error( "Unable to delete directory", e );
        }
    }

    @Override
    public VocabularyDTO getByNotation( String notation ) {
        if ( notation == null || notation.isEmpty() ) {
            log.error( "Error notation could not be empty or null" );
            throw new IllegalArgumentException( "Error notation could not be empty or null" );
        }
        log.debug( "Request to get Vocabulary by notation: {}", notation );
        final List<Vocabulary> vocabularies = vocabularyRepository.findAllByNotation( notation );
        if ( vocabularies.isEmpty() ) {
            log.error( "Error vocabulary with notation {} does not exist", notation );
            throw new ResourceNotFoundException( UNABLE_FIND_VOCABULARY + "or notation " + notation,  notation, "404" );
        }
        return vocabularyMapper.toDto( vocabularies.get( 0 ) );
    }

    @Override
    public VersionDTO cloneTl( VersionDTO currentVersionSl, VersionDTO prevVersionSl, VersionDTO prevVersionTl ) {
        VersionDTO newTlVersion = new VersionDTO( prevVersionTl, currentVersionSl );
        newTlVersion.setCreator( SecurityUtils.getCurrentUserId() );
        // if previous TL version is not published yet, assign version as initial version
        if ( prevVersionTl.getStatus() != Status.PUBLISHED ) {
            newTlVersion.setPreviousVersion( null );
            newTlVersion.setInitialVersion( null );
        }
        newTlVersion = versionService.save( newTlVersion );

        for ( ConceptDTO conceptSlDTO : currentVersionSl.getConcepts() ) {
            var newConceptTl = cloneTLConcept( prevVersionSl, prevVersionTl, conceptSlDTO, newTlVersion.getId() );
            newTlVersion.addConcept( newConceptTl );
        }

        // #423 -->
        List<VocabularyChangeDTO> currentChangeLog = vocabularyChangeService.findByVersionId(currentVersionSl.getId());
        for ( VocabularyChangeDTO change : currentChangeLog )
        {
            change.setVersionId( newTlVersion.getId() );
            vocabularyChangeService.save( change );
        }
        newTlVersion.setVersionChanges(currentVersionSl.getVersionChanges());
        // <-- #423

        return newTlVersion;
    }

    private ConceptDTO cloneTLConcept( VersionDTO prevVersionSl, VersionDTO prevVersionTl, ConceptDTO conceptSlDTO, Long versionID )
    {
        ConceptDTO newConceptTl = new ConceptDTO( conceptSlDTO );
        newConceptTl.setVersionId( versionID );

        if ( conceptSlDTO.getPreviousConcept() != null ) {
            // try to find concept title, definition from previous concept
            // first, try to find the previous concept. in this case the TL prev concept will be found
            // if there is no change in the notation between prev and current SL concept
            ConceptDTO prevConceptTl = prevVersionTl.getConcepts().stream()
                .filter( conceptDTO -> conceptDTO.getNotation().equals( conceptSlDTO.getNotation() ) ).findFirst()
                // if not found, try to find old notation from the previous SL concept
                .orElseGet( () -> getPrevTlConceptByPrevSlNotation( prevVersionSl, prevVersionTl, conceptSlDTO ) );

            // assign title and definition if previous TL concept found
            if ( prevConceptTl != null ) {
                newConceptTl.setTitle( prevConceptTl.getTitle() );
                newConceptTl.setDefinition( prevConceptTl.getDefinition() );
                newConceptTl.setPreviousConcept( prevConceptTl.getId() );

                // if previous TL version is not published yet, assign concept as initial concept
                if ( prevVersionTl.getStatus() != Status.PUBLISHED ) {
                    newConceptTl.setPreviousConcept( null );
                } else {
                    newConceptTl.setPreviousConcept( prevConceptTl.getId() );
                }
            }
        }
        return newConceptTl;
    }

    private ConceptDTO getPrevTlConceptByPrevSlNotation( VersionDTO prevVersionSl, VersionDTO prevVersionTl, ConceptDTO conceptSlDTO )
    {
        for ( ConceptDTO prevVersionSlConcept : prevVersionSl.getConcepts() )
        {
            if ( prevVersionSlConcept.getId().equals( conceptSlDTO.getPreviousConcept() ) )
            {
                for ( ConceptDTO prevTlConcept : prevVersionTl.getConcepts() )
                {
                    if ( prevTlConcept.getNotation().equals( prevVersionSlConcept.getNotation() ) )
                    {
                        return prevTlConcept;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public VocabularyDTO getVocabularyByNotationAndVersion( String notation, String slVersionNumber, boolean onlyPublished ) {
        // Parameter validation
        Objects.requireNonNull( slVersionNumber, "slVersionNumber number cannot be null" );
        if ( slVersionNumber.isEmpty() ) {
            throw new IllegalArgumentException( "slVersionNumber cannot be empty" );
        }

        VocabularyDTO vocabulary = getByNotation( notation );
        final Agency agency = agencyRepository.getOne( vocabulary.getAgencyId() );
        vocabulary.setAgencyLink( agency.getLink() );
        vocabulary.setAgencyLogo( agency.getLogopath() );
        vocabulary.setAgencyLink( agency.getLink() );

        if ( !onlyPublished ) {
            if ( slVersionNumber.equals( ALL ) )
                return vocabulary;
            else if ( slVersionNumber.equals( LATEST ) ) {
                final VersionDTO latestSlVersion = vocabulary.getVersions().stream().min(VocabularyUtils.VERSION_DTO_COMPARATOR).orElse( null );
                if ( latestSlVersion != null ) {
                    slVersionNumber = latestSlVersion.getNumber().toString();
                }

                // get all available licenses
                final List<Licence> licenceList = licenceRepository.findAll();

                getOneLatestVersionEachLanguage( VersionNumber.fromString(slVersionNumber), licenceList, vocabulary, true );
            }
        } else {
            preparePublishedVocabulary( slVersionNumber, vocabulary );
        }
        return vocabulary;
    }

    private void preparePublishedVocabulary( String slVersionNumber, VocabularyDTO vocabulary ) {
        boolean findAllLatestVersion = false;
        VersionNumber slVersion;
        if ( slVersionNumber.equals( LATEST ) ) {
            findAllLatestVersion = true;
            slVersion = vocabulary.getVersions().stream()
                .filter(v -> v.getStatus() == Status.PUBLISHED )
                .min(VocabularyUtils.VERSION_DTO_COMPARATOR)
                .map(VersionDTO::getNumber)
                .orElseThrow();
        } else {
            slVersion = VersionNumber.fromString(slVersionNumber);
        }

        Set<VersionDTO> includedVersions = versionService.findAllPublishedByVocabularyAndVersionSl( vocabulary.getId(), slVersion );

        if ( findAllLatestVersion ) {
            Set<String> includedLangs = includedVersions.stream().map( VersionDTO::getLanguage ).collect( Collectors.toSet() );
            for ( VersionDTO version : versionService.findAllPublishedByVocabulary( vocabulary.getId() ) ) {
                if ( !includedLangs.contains( version.getLanguage() ) ) {
                    includedLangs.add( version.getLanguage() );
                    includedVersions.add( version );
                }
            }
        }

        // set included version
        vocabulary.setVersions( includedVersions );

        Map<Long, Licence> licenceMap = licenceRepository.findAll().stream()
            .collect( Collectors.toMap( Licence::getId, Function.identity() ) );

        for ( VersionDTO version : vocabulary.getVersions() ) {
            prepareVersionAndConcept( licenceMap, version );
            addVersionHistories( vocabulary, version );
        }
    }

    private void getOneLatestVersionEachLanguage(
        VersionNumber slVersionNumber,
        List<Licence> licenceList,
        VocabularyDTO vocabulary,
        boolean isAddHistory ) {
        Set<VersionDTO> versionDTOs = new LinkedHashSet<>();
        List<VersionDTO> versionsGroup = versionService.findAllByVocabularyAndVersionSl( vocabulary.getId(), slVersionNumber );
        Set<String> languages = new HashSet<>();
        for ( VersionDTO version : versionsGroup ) {
            // check for more than one version in one language, only return latest one
            if ( languages.contains( version.getLanguage() ) )
            {
                continue;
            }

            languages.add( version.getLanguage() );

            // add version to new list and sort concepts
            LinkedHashSet<ConceptDTO> sortedConcepts = version.getConcepts().stream()
                .sorted( Comparator.comparing( ConceptDTO::getPosition ) ).collect( Collectors.toCollection( LinkedHashSet::new ) );
            version.setConcepts( sortedConcepts );

            // assign licence if present
            if ( licenceList != null ) {
                for ( Licence licence : licenceList )
                {
                    if ( licence.getId().equals( version.getLicenseId() ) )
                    {
                        version.setLicense( licence.getAbbr() );
                        version.setLicenseLink( licence.getLink() );
                        version.setLicenseName( licence.getName() );
                        version.setLicenseLogo( licence.getLogoLink() );
                        break;
                    }
                }
            }

            // add history
            if ( isAddHistory ) {
                addVersionHistories( vocabulary, version );
            }

            versionDTOs.add( version );
        }
        vocabulary.setVersions( versionDTOs );
    }

    @Override
    public void indexAllEditor() {
        log.info( "INDEXING ALL EDITOR VOCABULARIES START" );
        // remove any index
        vocabularyEditorSearchRepository.deleteAll();
        // index all vocabularies
        findAll().forEach( v ->
        {
            // skip for withdrawn vocabulary
            if ( Boolean.TRUE.equals( v.isWithdrawn() ) )
                return;
            indexEditor( v );
        } );
        log.info( "INDEXING ALL EDITOR VOCABULARIES FINISHED" );
    }

    @Override
    public void indexEditor( VocabularyDTO vocabulary ) {
        log.info( "Indexing editor vocabulary with id {} and notation {}", vocabulary.getId(), vocabulary.getNotation() );
        // filter only include latest vocabulary
        // get latest version
        final VersionDTO maxSlVersion = vocabulary.getVersions().stream()
            .filter( v -> v.getItemType() == ItemType.SL )
            .max(Comparator.comparing(VersionDTO::getNumber))
            .orElse( null );

        if ( maxSlVersion == null )
            return;

        // on editor set Version to be latest SL of any version
        if ( !vocabulary.getVersionNumber().equals( maxSlVersion.getNumber() ) ) {
            vocabulary.setVersionNumber( maxSlVersion.getNumber() );
        }

        getOneLatestVersionEachLanguage( maxSlVersion.getNumber(), null, vocabulary, false );

        // update/create agency publish
        agencyStatSearchRepository.findById( vocabulary.getAgencyId() ).ifPresent( agencyPublish ->
        {
            agencyPublish.updateVocabStat( vocabulary );
            agencyStatSearchRepository.save( agencyPublish );
        } );

        // fill vocabulary with versions
        VocabularyDTO.fillVocabularyByVersions( vocabulary, vocabulary.getVersions() );
        // fill CodeDTO object from versions
        vocabulary.setCodes( CodeDTO.generateCodesFromVersion( vocabulary.getVersions(), true ) );

        VocabularyEditor vocab = vocabularyEditorMapper.toEntity( vocabulary );
        vocabularyEditorSearchRepository.save( vocab );
    }

    @Override
    public void indexAllAgencyStats() {
        log.info( "INDEXING ALL AGENCY VOCABULARIES STATISTIC START" );
        findAll().stream().filter( v -> !Boolean.TRUE.equals( v.isWithdrawn() ) ).forEach( this::indexAgencyStats );
        log.info( "INDEXING ALL AGENCY VOCABULARIES STATISTIC FINISHED" );
    }

    @Override
    public void indexAgencyStats( VocabularyDTO vocabulary ) {
        // update/create agency publish
        agencyStatSearchRepository.findById( vocabulary.getAgencyId() ).ifPresent( agencyPublish ->
        {
            agencyPublish.updateVocabStat( vocabulary );
            agencyStatSearchRepository.save( agencyPublish );
        } );
    }

    @Override
    public void indexAllPublished() {
        log.info( "INDEXING ALL PUBLISHED VOCABULARIES START" );
        // remove any indexed vocabularies
        vocabularyPublishSearchRepository.deleteAll();
        // index all vocabularies
        findAll().forEach( v ->
        {
            // skip for withdrawn vocabulary
            if ( Boolean.TRUE.equals( v.isWithdrawn() ) )
                return;
            indexPublished( v );
        } );
        log.info( "INDEXING ALL PUBLISHED VOCABULARIES FINISHED" );
    }

    @Override
    public List<Path> getPublishedCvPaths() {
        try ( Stream<Path> paths = Files.find(
            applicationProperties.getVocabJsonPath(), 2, (p, a) -> a.isRegularFile()
        ) ) {
            return paths.collect( Collectors.toList() );
        } catch (IOException e) {
            log.error( "Failed to retrieve a list of published CVs: {}", e.getMessage() );
            return Collections.emptyList();
        }
    }

    @Override
    public void indexPublished( VocabularyDTO vocabulary ) {
        log.info( "Indexing published vocabulary with id {} and notation {}", vocabulary.getId(), vocabulary.getNotation() );
        if ( Boolean.TRUE.equals( vocabulary.isWithdrawn() ) )
            return;
        // check if there is published version
        if ( vocabulary.getVersions().stream().noneMatch( v -> v.getStatus() == Status.PUBLISHED ) ) {
            return;
        }

        setVocabularyWithLatestPublishedVersions( vocabulary );

        // fill vocabulary with versions
        VocabularyDTO.fillVocabularyByVersions( vocabulary, vocabulary.getVersions() );
        // fill CodeDTO object from versions
        vocabulary.setCodes( CodeDTO.generateCodesFromVersion( vocabulary.getVersions(), false ) );

        VocabularyPublish vocab = vocabularyPublishMapper.toEntity( vocabulary );
        vocabularyPublishSearchRepository.save( vocab );
    }

    private void setVocabularyWithLatestPublishedVersions( VocabularyDTO vocabulary ) {
        // collect all latest published version
        Set<VersionDTO> latestVersionsEachLangs = new LinkedHashSet<>();
        Set<String> publishedLangs = new HashSet<>();

        for ( VersionDTO version : versionService.findAllPublishedByVocabulary( vocabulary.getId() ) ) {
            // collect latest version across SL
            if ( !publishedLangs.contains( version.getLanguage() ) ) {
                latestVersionsEachLangs.add( version );
                publishedLangs.add( version.getLanguage() );
            }
        }

        vocabulary.setVersions( latestVersionsEachLangs );
        vocabulary.setLanguagesPublished( publishedLangs );
    }

    @Override
    public EsQueryResultDetail search( EsQueryResultDetail esQueryResultDetail ) {
        // get user keyword
        String searchTerm = esQueryResultDetail.getSearchTerm();

        // determine which language fields include into query
        List<String> languageFields = new ArrayList<>();
        if ( !esQueryResultDetail.isSearchAllLanguages() ) {
            languageFields.add( StringUtils.capitalize( esQueryResultDetail.getSortLanguage() ) );
        } else {
            // search on all fields
            languageFields.addAll( Language.getCapitalizedIsos() );
        }

        // build query builder
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
            .withSearchType( SearchType.DEFAULT )
            .withQuery( generateMainAndNestedQuery( searchTerm, languageFields, esQueryResultDetail.getCodeSize() ) )
            .withFilter( generateFilterQuery( esQueryResultDetail.getEsFilters() ) )
            .withPageable( esQueryResultDetail.getPage() );

        // add highlighter after 2nd character typed
        boolean isSearchWithKeyword = searchTerm != null && searchTerm.length() > 2;
        if ( isSearchWithKeyword )
        {
            var highlightBuilder = generateHighlightBuilder( List.of( TITLE, DEFINITION ), languageFields );
            searchQueryBuilder.withHighlightBuilder( highlightBuilder );
        }

        // add aggregation
        setUpAggregations( searchQueryBuilder, esQueryResultDetail );

        // at the end build search query
        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        // put the vocabulary results
        if ( esQueryResultDetail.getSearchScope() == SearchScope.EDITORSEARCH )
        {
            var indexCoordinates = IndexCoordinates.of( "vocabularyeditor" );
            var searchResponse = elasticsearchOperations.search( searchQuery, VocabularyEditor.class, indexCoordinates );
            processSearchResults( esQueryResultDetail, searchResponse, vocabularyEditorMapper::toDto, isSearchWithKeyword );
        }
        else
        {
            var indexCoordinates = IndexCoordinates.of( VOCABULARYPUBLISH );
            var searchResponse = elasticsearchOperations.search( searchQuery, VocabularyPublish.class, indexCoordinates );
            processSearchResults( esQueryResultDetail, searchResponse, vocabularyPublishMapper::toDto , isSearchWithKeyword );
        }

        return esQueryResultDetail;
    }

    private <T> void processSearchResults( EsQueryResultDetail esQueryResultDetail, org.springframework.data.elasticsearch.core.SearchHits<T> searchResponse, Function<T, VocabularyDTO> vocabularyDTOMapper, boolean isSearchWithKeyword )
    {
        // get search response for aggregation, hits, inner hits and highlighter

        // assign aggregation to esQueryResultDetail
        generateAggregationFilter( esQueryResultDetail, searchResponse );

        Page<VocabularyDTO> vocabularyPage = SearchHitSupport.searchPageFor( searchResponse, esQueryResultDetail.getPage() )
            .map( SearchHit::getContent )
            .map( vocabularyDTOMapper );

        // update vocabulary based on highlight and inner hit
        if ( isSearchWithKeyword )
        {
            applySearchHitAndHighlight( vocabularyPage, searchResponse );
        }
        else
        {
            // remove unnecessary nested code entities
            for ( VocabularyDTO vocab : vocabularyPage.getContent() ) {
                vocab.setCodes( Collections.emptySet() );
            }
        }

        // set selected language in case language filter is selected with specific language
        String fieldType;
        if ( esQueryResultDetail.getSearchScope() == SearchScope.EDITORSEARCH ) {
            fieldType = EsFilter.LANGS_AGG;
        } else {
            fieldType = EsFilter.LANGS_PUB_AGG;
        }
        setVocabularySelectedLanguage( esQueryResultDetail, vocabularyPage.getContent(), fieldType);

        esQueryResultDetail.setVocabularies( vocabularyPage );
    }

    @Override
    public EsQueryResultDetail searchCode( EsQueryResultDetail esQueryResultDetail ) {
        // params
        String term = esQueryResultDetail.getSearchTerm();
        String language = StringUtils.capitalize( esQueryResultDetail.getSortLanguage() );

        // nested query for codes
        BoolQueryBuilder boolQuery;

        if ( term.contains( "*" ) ) {
            boolQuery = QueryBuilders.boolQuery()
                .should( QueryBuilders.wildcardQuery( CODE_PATH + "." + NOTATION, term.toLowerCase().replace( " ", "" ) )
                    .boost( 2.0f ) );
        } else {
            boolQuery = QueryBuilders.boolQuery()
                .should( QueryBuilders.matchQuery( CODE_PATH + "." + TITLE + language, term ).fuzziness( 0.7 ).boost( 1.0f ) )
                .should( QueryBuilders.wildcardQuery( CODE_PATH + "." + NOTATION, term.toLowerCase().replace( " ", "" ) + "*" )
                    .boost( 2.0f ) );
        }
        final NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.Total )
            .innerHit( new InnerHitBuilder( CODE_PATH ).setSize( esQueryResultDetail.getCodeSize() ) );

        // build query builder
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
            .withSearchType( SearchType.DEFAULT )
            .withQuery( nestedQueryBuilder )
            .withFilter( generateFilterQuery( esQueryResultDetail.getEsFilters() ) )
            .withPageable( esQueryResultDetail.getPage() );

        // add aggregations
        setUpAggregations( searchQueryBuilder, esQueryResultDetail );

        // at the end build search query
        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        IndexCoordinates indexCoordinates = IndexCoordinates.of( VOCABULARYPUBLISH );
        SearchHits<VocabularyPublish> searchHits = elasticsearchOperations.search( searchQuery, VocabularyPublish.class, indexCoordinates );
        Page<VocabularyDTO> vocabularyPage = SearchHitSupport.searchPageFor( searchHits, esQueryResultDetail.getPage() )
            .map( SearchHit::getContent )
            .map( vocabularyPublishMapper::toDto );

        for ( SearchHit<VocabularyPublish> hit : searchHits ) {
            Optional<VocabularyDTO> vocabOpt = VocabularyDTO.findByIdFromList( vocabularyPage.getContent(), hit.getId() );
            if ( vocabOpt.isPresent() ) {
                VocabularyDTO cvHit = vocabOpt.get();

                if ( hit.getInnerHits().isEmpty() )
                {
                    continue;
                }

                applyCodeHighlighter( hit, cvHit, esQueryResultDetail.isWithHighlight() );
            }
        }

        generateAggregationFilter( esQueryResultDetail, searchHits );
        esQueryResultDetail.setVocabularies( vocabularyPage );
        return esQueryResultDetail;
    }

    private void generateAggregationFilter( EsQueryResultDetail esQueryResultDetail, SearchHits<?> searchResponse ) {
        // generate aggregation filter
        for ( String field : esQueryResultDetail.getAggFields() ) {
            if ( !esQueryResultDetail.isAnyFilterActive() ) {
                buildNonFilteredAggregation( esQueryResultDetail, searchResponse, field );
            } else {
                buildFilteredAggregation( esQueryResultDetail, searchResponse, field );
            }
        }
    }

    private void buildNonFilteredAggregation( EsQueryResultDetail esQueryResultDetail, SearchHits<?> searchResponse, String field ) {
        Aggregations aggregations = searchResponse.getAggregations();
        if ( aggregations == null )
        {
            return;
        }

        Terms aggregation = aggregations.get( field + COUNT );
        if ( aggregation == null)
        {
            return;
        }

        esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter ->
        {
            esFilter.clearBucket();
            for ( Terms.Bucket b : aggregation.getBuckets() )
                esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
        } );
    }

    private void buildFilteredAggregation( EsQueryResultDetail esQueryResultDetail, SearchHits<?> searchResponse, String field ) {
        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null)
        {
            return;
        }

        Filters aggFilters = aggregations.get( "aggregration_filter" );
        if ( aggFilters == null )
        {
            return;
        }

        esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter ->
        {
            esFilter.clearBucket();
            List<? extends Filters.Bucket> buckets = aggFilters.getBuckets();
            for ( Filters.Bucket bucket : buckets ) {
                if ( bucket.getDocCount() != 0 )
                {
                    fillFilterBucket( esFilter, bucket.getAggregations().get( field + COUNT ) );
                }
            }
        } );
    }

    private void fillFilterBucket( EsFilter esFilter, Terms terms ) {
        List<? extends Terms.Bucket> bkts = terms.getBuckets();
        for ( Terms.Bucket b : bkts ) {

            if ( b.getDocCount() == 0 )
            {
                continue;
            }

            if ( !EsFilter.isActiveFilter( esFilter, b.getKeyAsString() ) )
            {
                esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
            }
            else
            {
                esFilter.addFilteredBucket( b.getKeyAsString(), b.getDocCount() );
            }
        }
    }

    private void applySearchHitAndHighlight( Page<VocabularyDTO> vocabularyPage, SearchHits<?> searchResponse ) {
        for ( SearchHit<?> hit : searchResponse ) {
            VocabularyDTO.findByIdFromList( vocabularyPage.getContent(), hit.getId() ).ifPresent( cvHit -> {

                if ( !hit.getHighlightFields().isEmpty() )
                {
                    applyVocabularyHighlighter( hit, cvHit );
                }

                if ( !hit.getInnerHits().isEmpty() )
                {
                    applyCodeHighlighter( hit, cvHit, true );
                }
            });
        }
    }

    private void applyVocabularyHighlighter( org.springframework.data.elasticsearch.core.SearchHit<?> hit, VocabularyDTO cvHit ) {
        for ( Map.Entry<String, List<String>> entry : hit.getHighlightFields().entrySet() ) {
            String fieldName = entry.getKey();
            List<String> highlightField = entry.getValue();
            StringBuilder highLightText = new StringBuilder();
            for ( String text : highlightField ) {
                highLightText.append( text ).append( " " );
            }

            if ( fieldName.contains( TITLE ) )
                cvHit.setTitleDefinition( highLightText.toString(), null, fieldName.substring( TITLE.length() ), true );
            if ( fieldName.contains( DEFINITION ) )
                cvHit.setTitleDefinition( null, highLightText.toString(), fieldName.substring( DEFINITION.length() ), true );
        }
    }

    private void applyCodeHighlighter( SearchHit<?> hit, VocabularyDTO cvHit, boolean withHighlight ) {
        Set<CodeDTO> newCodes = new LinkedHashSet<>();

        for ( Map.Entry<String, SearchHits<?>> innerHitEntry : hit.getInnerHits().entrySet() ) {
            for ( SearchHit<?> innerHit : innerHitEntry.getValue() ) {
                highlightEachCode( cvHit, newCodes, innerHit, withHighlight );
            }
        }

        cvHit.setCodes( newCodes );
    }

    private void highlightEachCode( VocabularyDTO cvHit, Set<CodeDTO> newCodes, SearchHit<?> innerHit, boolean withHighlight ) {
        Optional<CodeDTO> codeOpt = CodeDTO.findByIdFromList( cvHit.getCodes(), ((Code) innerHit.getContent()).getId().intValue() );
        if ( codeOpt.isPresent() )
        {
            CodeDTO codeHit = codeOpt.get();
            if ( withHighlight )
            {
                applyHighlightCode( cvHit, innerHit, codeHit );
            }
            newCodes.add( codeHit );
        }
    }

    private void applyHighlightCode( VocabularyDTO cvHit, SearchHit<?> innerHit, CodeDTO codeHit ) {
        for ( Map.Entry<String, List<String>> entry : innerHit.getHighlightFields().entrySet() ) {
            String fieldName = entry.getKey();
            List<String> highlighField = entry.getValue();
            StringBuilder highLightText = new StringBuilder();
            for ( String text : highlighField ) {
                if ( !fieldName.contains( TITLE ) && !text.endsWith( "." ) ) {
                    highLightText.append( text ).append( " ... " );
                } else
                    highLightText.append( text );
            }
            if ( fieldName.contains( TITLE ) )
                codeHit.setTitleDefinition( highLightText.toString(), null, fieldName.substring( (CODE_PATH + "." + TITLE).length() ),
                    true );
            if ( fieldName.contains( DEFINITION ) )
                codeHit.setTitleDefinition( null, highLightText.toString(), fieldName.substring( (CODE_PATH + "." + DEFINITION).length() ),
                    true );
            setSelectedLanguageByHighlight( cvHit, fieldName );
        }
    }

    // set selected language based on highlight
    private void setSelectedLanguageByHighlight( VocabularyDTO cvHit, String highlightField ) {
        // only set selected language once
        if ( cvHit.getSelectedLang() == null ) {
            // get last language information from the field and get the Language enum
            String langIso = highlightField.substring( highlightField.length() - 2 );
            cvHit.setSelectedLang( langIso.toLowerCase() );
        }
    }

    private void setUpAggregations( NativeSearchQueryBuilder searchQueryBuilder, EsQueryResultDetail esQueryResultDetail ) {
        if ( !esQueryResultDetail.isAnyFilterActive() ) {
            for ( String aggField : esQueryResultDetail.getAggFields() )
            {
                searchQueryBuilder.addAggregation(
                    AggregationBuilders.terms( aggField + COUNT ).field( aggField ).size( SIZE_OF_ITEMS_ON_AGGREGATION ) );
            }
        } else {
            FiltersAggregationBuilder filtersAggregation = AggregationBuilders.filters( "aggregration_filter",
                generateFilterQuery( esQueryResultDetail.getEsFilters() ) );

            for ( String aggField : esQueryResultDetail.getAggFields() )
            {
                filtersAggregation.subAggregation(
                    AggregationBuilders.terms( aggField + COUNT ).field( aggField ).size( SIZE_OF_ITEMS_ON_AGGREGATION ) );
            }

            searchQueryBuilder.addAggregation( filtersAggregation );
        }
    }

    @Override
    public String generateJsonAllVocabularyPublish() {
        StringBuilder output = new StringBuilder();
        output.append( "Performing Vocabulary Publish JSON files creation.\n" );
        // remove all static JSON files on vocabulary (delete entire and including vocabulary
        // directory)
        deleteCvJsonDirectoryAndContent( applicationProperties.getVocabJsonPath() );
        output.append( "Clean up: All existing JSON files are deleted.\n" );

        List<VocabularyDTO> vocabularyDTOS = findAll();
        output.append( "Starting to generate new JSON files.\n" );
        output.append( generateJsonVocabularyPublish( vocabularyDTOS.toArray( new VocabularyDTO[0] ) ) );
        output.append( "Generating new JSON files are done!\n" );
        return output.toString();
    }

    @Override
    public String generateJsonVocabularyPublish( VocabularyDTO... vocabularies ) {
        StringBuilder output = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule( new JavaTimeModule() );
        mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );

        // getAgencyAndLicense
        Map<Long, Agency> agencyMap = agencyRepository.findAll().stream().collect( Collectors.toMap( Agency::getId, Function.identity() ) );
        Map<Long, Licence> licenceMap = licenceRepository.findAll().stream()
            .collect( Collectors.toMap( Licence::getId, Function.identity() ) );

        for ( VocabularyDTO vocabulary : vocabularies ) {
            output.append("Generate JSON files for Vocabulary ").append(vocabulary.getNotation()).append(".\n");
            // get all published versions (sorted by SL and number)
            List<VersionDTO> versions = versionService.findAllPublishedByVocabulary( vocabulary.getId() );
            // skip vocabulary without published version
            if ( versions.isEmpty() )
            {
                continue;
            }
            // remove unused attributes, and add the required attribute for vocabulary
            updateVocabularyContentForJsonfy( agencyMap, vocabulary );
            // put into map based on versionSL, the SL need to be listed in beginning (sorted first)
            Map<String, List<VersionDTO>> slNumberVersionsMap = new LinkedHashMap<>();
            // add special Map for all latest version across SL version
            // just remember that the versions are always already sorted from latest to oldest
            List<VersionDTO> latestVersionsAcrossSl = new ArrayList<>();
            Set<String> latestVersionsLangs = new HashSet<>();
            slNumberVersionsMap.put( LATEST, latestVersionsAcrossSl );
            for ( VersionDTO version : versions ) {
                output.append("Generate JSON file for Vocabulary ").append(vocabulary.getNotation()).append(" Version").append(version.getNumber()).append(".\n");
                prepareVersionAndConceptForJsonfy( licenceMap, vocabulary, slNumberVersionsMap, version, latestVersionsAcrossSl, latestVersionsLangs );
            }
            generateJsonFile( mapper, vocabulary, slNumberVersionsMap );
        }
        return output.toString();
    }

    private void prepareVersionAndConceptForJsonfy(
        Map<Long, Licence> licenceMap,
        VocabularyDTO vocabulary,
        Map<String, List<VersionDTO>> slNumberVersionsMap,
        VersionDTO version,
        List<VersionDTO> latestVersionsAcrossSl,
        Set<String> latestVersionsLangs ) {
        prepareVersionAndConcept( licenceMap, version );

        List<VersionDTO> versionDTOs;
        if ( version.getItemType() == ItemType.SL ) {
            versionDTOs = slNumberVersionsMap.computeIfAbsent( version.getNumber().getMinorVersion(), k -> new ArrayList<>() );
        } else {
            versionDTOs = slNumberVersionsMap.get( version.getNumber().getMinorVersion() );
            if ( versionDTOs == null )
            {
                throw new IllegalArgumentException( "SL version missing from version number of " + version.getNotation() + " " + version.getNumber().getMinorVersion() );
            }
        }

        // check for version history
        addVersionHistories( vocabulary, version );
        versionDTOs.add( version );

        // collect latest version across SL
        if ( !latestVersionsLangs.contains( version.getLanguage() ) || version.getNumber().equals(vocabulary.getVersionNumber()) ) {
            latestVersionsAcrossSl.add( version );
            latestVersionsLangs.add( version.getLanguage() );
        }
    }

    private void prepareVersionAndConcept( Map<Long, Licence> licenceMap, VersionDTO version ) {
        // get concepts and sorted by position
        List<ConceptDTO> concepts = conceptService.findByVersion( version.getId() );
        // remove unused attributes, and add the required attribute for version
        updateVersionContentForJsonfy( licenceMap, version );
        // remove unused attributes, and add the required attribute for concept
        updateConceptContentForJsonfy( concepts );

        version.setConcepts( new LinkedHashSet<>( concepts ) );
    }

    private void updateConceptContentForJsonfy( List<ConceptDTO> concepts ) {
        for ( ConceptDTO concept : concepts ) {
            if ( concept.getDefinition() != null )
                concept.setDefinition( concept.getDefinition().trim() );
            concept.setSlConcept( null );
            concept.setVersionId( null );
        }
    }

    private void updateVersionContentForJsonfy( Map<Long, Licence> licenceMap, VersionDTO version ) {
        Licence licence = licenceMap.get( version.getLicenseId() );
        if ( licence != null ) {
            version.setLicense( licence.getAbbr() );
            version.setLicenseLink( licence.getLink() );
            version.setLicenseName( licence.getName() );
            version.setLicenseLogo( licence.getLogoLink() );
        }
        version.setNotes( version.getNotes() == null ? "" : version.getNotes().trim() );
        version.setCreator( null );
        version.setDiscussionNotes( null );
        version.setVocabularyId( null );
        version.setLicenseId( null );
    }

    private void updateVocabularyContentForJsonfy( Map<Long, Agency> agencyMap, VocabularyDTO vocabulary ) {
        // clear vocabulary content (title, definition), since it is not needed
        vocabulary.clearContent();
        // update data remove unused and add more agency information
        vocabulary.setDiscoverable( null );
        vocabulary.setArchived( null );
        vocabulary.setCodes( null );
        Agency agency = agencyMap.get( vocabulary.getAgencyId() );
        if ( agency != null ) {
            vocabulary.setAgencyLink( agency.getLink() );
        }
    }

    private void addVersionHistories( VocabularyDTO vocabulary, VersionDTO version ) {
        var olderVersions = versionService.findOlderPublishedByVocabularyLanguageId( vocabulary.getId(), version.getLanguage(), version.getId() );
        var olderVersionHistories = new ArrayList<Map<String, Object>>(olderVersions.size());
        for ( VersionDTO olderVersion : olderVersions )
        {
            Map<String, Object> versionHistoryMap = new LinkedHashMap<>();
            versionHistoryMap.put( "id", olderVersion.getId() );
            versionHistoryMap.put( "version", olderVersion.getNumber() );
            versionHistoryMap.put( "date", olderVersion.getPublicationDate().toString() );
            versionHistoryMap.put( "note", olderVersion.getVersionNotes() );
            versionHistoryMap.put( "changes", olderVersion.getVersionChanges() );
            if ( olderVersion.getPreviousVersion() != null &&
                !olderVersion.getPreviousVersion().equals( olderVersion.getId() ) )
            {
                versionHistoryMap.put( "prevVersion", olderVersion.getPreviousVersion() );
            }
            olderVersionHistories.add( versionHistoryMap );
        }
        version.setVersionHistories( olderVersionHistories );
    }

    private void generateJsonFile(ObjectMapper mapper, VocabularyDTO vocabulary, Map<String, List<VersionDTO>> slNumberVersionsMap ) {
        for ( Map.Entry<String, List<VersionDTO>> entry : slNumberVersionsMap.entrySet() ) {
            String entryKey = entry.getKey();
            vocabulary.setVersions( new LinkedHashSet<>( entry.getValue() ) );

            try {
                var file = createDirectoryAndFileName(vocabulary.getNotation(), entryKey, entryKey.equals(LATEST));
                try (var outputStream = Files.newOutputStream(file)) {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, vocabulary);
                }
            } catch (IOException e) {
                log.error( "Failed to generate JSON for {}", vocabulary.getNotation(), e );
            }
        }
    }

    private Path createDirectoryAndFileName(String notation, String versionNumber, boolean isAllLatestVersion ) throws IOException {
        Path dirPath = applicationProperties.getVocabJsonPath().resolve( notation );

        if (!isAllLatestVersion) {
            // Add the version number to the path
            dirPath = dirPath.resolve(versionNumber);
        }

        // Create the destination directory, does nothing if the directory already exists
        Files.createDirectories(dirPath);

        if ( isAllLatestVersion ) {
            return dirPath.resolve(notation + JSON_FORMAT);
        } else {
            return dirPath.resolve(notation + "_" + versionNumber + JSON_FORMAT );
        }
    }

    @Override
    public Path generateVocabularyFileDownload( String vocabularyNotation, String versionSl, String versionList, ExportService.DownloadType downloadType, String requestURL, boolean onlyPublished )
    {
        var fileNameStringBuilder = new StringBuilder().append( vocabularyNotation ).append( "-" ).append( versionSl );
        if (versionList != null)
        {
            // version list may be null, only append if non-null
            fileNameStringBuilder.append( "_" ).append( versionList );
        }
        fileNameStringBuilder.append( "." ).append( downloadType );
        var resolvedPath = applicationProperties.getExportFilePath().resolve( fileNameStringBuilder.toString() );

        // Test if a pre-existing export is present
        if (Files.isRegularFile( resolvedPath )) {
            return resolvedPath;
        }

        log.info( "Generate file {} for Vocabulary {} versionSl {}", downloadType, vocabularyNotation, versionSl );
        VocabularyDTO vocabularyDTO = getVocabularyByNotationAndVersion( vocabularyNotation, versionSl, onlyPublished );

        Set<VersionDTO> includedVersions = filterOutVocabularyVersions( versionList, vocabularyDTO );

        // 604 - duplicate entries in SKOS output
        if ( downloadType == ExportService.DownloadType.SKOS ) {
            Map<String, VersionDTO> latestVersionsMap = new LinkedHashMap<>();
            for (VersionDTO versionDTO : includedVersions) {
                latestVersionsMap.merge(versionDTO.getLanguage(), versionDTO,
                    // Select the latest version (defined by the lower result from the comparator)
                    (r, k) -> VocabularyUtils.VERSION_DTO_COMPARATOR.compare(r, k) < 0 ? r : k
                );
            }
            includedVersions = new LinkedHashSet<>(latestVersionsMap.values());
        }

        vocabularyDTO.setVersions( includedVersions );
        Map<String, Object> map = new HashMap<>();
        for ( VersionDTO includedVersion : includedVersions ) {
            // escaping HTML to strict XHTML
            includedVersion.setVersionNotes( VocabularyUtils.toStrictXhtml( includedVersion.getVersionNotes() ) );
            includedVersion.setVersionChanges( VocabularyUtils.toStrictXhtml( includedVersion.getVersionChanges() ) );
            includedVersion.setDdiUsage( VocabularyUtils.toStrictXhtml( includedVersion.getDdiUsage() ) );
            // sort concepts by position (see #330)
            includedVersion.setConcepts(
                includedVersion.getConcepts().stream().sorted( Comparator.comparing( ConceptDTO::getPosition ) )
                    .collect( Collectors.toCollection( LinkedHashSet::new ) ) );
        }

        // sorted versions
        map.put( "versions", includedVersions );

        // agency object
        AgencyDTO agencyDTO = new AgencyDTO();
        agencyDTO.setName( vocabularyDTO.getAgencyName() );
        agencyDTO.setLink( vocabularyDTO.getAgencyLink() );

        if ( downloadType == ExportService.DownloadType.SKOS ) {
            final VersionDTO versionIncluded = includedVersions.iterator().next();
            String uriSl = versionIncluded.getUriSl();
            if ( uriSl == null ) {
                uriSl = versionIncluded.getUri();
            }
            map.put( "docId", uriSl );
            map.put( "docVersionOf", vocabularyDTO.getUri() );
            map.put( "docNotation", vocabularyDTO.getNotation() );
            map.put( "docSourceLanguage", vocabularyDTO.getSourceLanguage() );
            map.put( "docVersion", versionIncluded.getNumber().toString() );
            map.put( "docLicense", versionIncluded.getLicenseName() );
            map.put( "docLicenseUrl", versionIncluded.getLicenseLink() );
            map.put( "docRight", versionIncluded.getLicenseName() );
            map.put( CODE_PATH, CodeDTO.generateCodesFromVersion( includedVersions, false ) );
        } else {
            vocabularyDTO.setVersions( includedVersions );
            prepareAdditionalAttributesForNonSkos( vocabularyDTO, map, agencyDTO );
        }

        map.put( "agency", agencyDTO );

        // Derive the year from the date of publication, falling back to the last modified date if present.
        // Otherwise, the current date is used to provide the year.
        int year;
        if ( vocabularyDTO.getPublicationDate() != null) {
            year = vocabularyDTO.getPublicationDate().getYear();
        } else if ( vocabularyDTO.getLastModified() != null ) {
            year = vocabularyDTO.getLastModified().getYear();
        } else {
            year = LocalDate.now().getYear();
        }
        map.put( "year", year );
        map.put( "baseUrl", requestURL );

        String suffix = "";
        if (agencyDTO.getName().equals("DDI Alliance") && downloadType == ExportService.DownloadType.SKOS ) {
            suffix = "-ddi";
        }

        // Write to export file
        try
        {
            // Create all necessary directories
            Files.createDirectories( resolvedPath.getParent() );
            try ( var outputStream = Files.newOutputStream( resolvedPath ) )
            {
                exportService.generateFileByThymeleafTemplate( "export" + suffix, map, downloadType, outputStream );
                return resolvedPath;
            }
        }
        catch ( IOException | JAXBException | Docx4JException e )
        {
            throw new VocabularyFileGenerationFailedException( e );
        }
    }


    @Override
    @Transactional
    public void updateVocabularyUri( Long agencyId, String agencyUri, String agencyUriCode ) {
        final List<Vocabulary> vocabularies = vocabularyRepository.findAllByAgencyId( agencyId );
        for ( Vocabulary vocabulary : vocabularies ) {
            vocabulary.setUri( VocabularyUtils.generateUri( agencyUri, vocabulary ) );
            final List<Version> versions = vocabulary.getVersions().stream()
                    .sorted( VocabularyUtils.VERSION_COMPARATOR )
                    .collect( Collectors.toList() );
            for ( Version version : versions ) {
                updateUriForVersionAndConcept( agencyUri, agencyUriCode, vocabulary, version );
                if ( version.getUriSl() != null ) {
                    version.setUriSl( VocabularyUtils.generateUri( agencyUri, vocabulary, version, null ) );
                }
            }
            vocabularyRepository.save( vocabulary );
        }
    }

    @Override
    @Transactional
    public void updateVocabularyLogo( Long agencyId, String agencyLogoPath ) {
        final List<Vocabulary> vocabularies = vocabularyRepository.findAllByAgencyId( agencyId );
        final List<Long> changedVocabularies = new ArrayList<>();
        for ( Vocabulary vocabulary : vocabularies ) {
            // exit if no logo update
            if ( ( vocabulary.getAgencyLogo() != null ) && ( vocabulary.getAgencyLogo().equals( agencyLogoPath ) ) ) {
                continue;
            }

            // update db
            vocabulary.setAgencyLogo( agencyLogoPath );
            vocabularyRepository.save( vocabulary );

            // mark the vocabulary as changed
            changedVocabularies.add( vocabulary.getId() );
        }

        // update elasticsearch indexes
        findAll().stream()
            // skip for withdrawn vocabulary
            .filter( v -> !Boolean.TRUE.equals( v.isWithdrawn() ) )
            .filter( v -> changedVocabularies.contains( v.getId() ) )
            .forEach( v ->
            {
                // delete changed vocabularies from indexes
                vocabularyEditorSearchRepository.deleteById( v.getId() );
                vocabularyPublishSearchRepository.deleteById( v.getId() );

                // insert changed vocabulary to indexes
                indexPublished( v );
                indexEditor( v );
            } );
    }

    @Override
    @Transactional
    public VersionDTO forwardStatus( VocabularySnippet vocabularySnippet ) throws IllegalActionTypeException
    {
        if ( vocabularySnippet.getVersionId() == null ) {
            throw new MissingIdentifierException( "Missing version id" );
        }
        if ( vocabularySnippet.getActionType() == null ) {
            throw new IllegalActionTypeException();
        }
        VocabularyDTO vocabularyDTO = findOne( vocabularySnippet.getVocabularyId() )
            .orElseThrow( () -> new EntityNotFoundException( UNABLE_TO_FIND_VOCABULARY + vocabularySnippet.getVocabularyId() ) );
        // pick version from vocabularyDTO
        VersionDTO versionDTO = vocabularyDTO.getVersions().stream().filter( v -> v.getId().equals( vocabularySnippet.getVersionId() ) )
            .findFirst()
            .orElseThrow( () -> new EntityNotFoundException( UNABLE_TO_FIND_VERSION + vocabularySnippet.getVersionId() ) );

        Licence licence = null;
        Agency agency = null;
        // get license information only when we are publishing the vocabularies
        if ( vocabularySnippet.getActionType() == ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE ||
            vocabularySnippet.getActionType() == ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH ) {
            licence = licenceRepository.getOne( vocabularySnippet.getLicenseId() );
            agency = agencyRepository.getOne( vocabularySnippet.getAgencyId() );
        }
        // check authorization
        switch (vocabularySnippet.getActionType()) {
            case FORWARD_CV_SL_STATUS_REVIEW:
                SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_SL_STATUS_REVIEW,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
                versionDTO.setStatus( Status.REVIEW );
                versionDTO.setLastStatusChangeDate( LocalDate.now() );
                vocabularyDTO.setStatus( Status.REVIEW );
                break;
            case FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE:
                SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
                versionDTO.setStatus( Status.READY_TO_TRANSLATE );
                versionDTO.setLastStatusChangeDate( LocalDate.now() );
                vocabularyDTO.setVersionNumberByVocabularySnippet(vocabularySnippet);
                vocabularyDTO.setStatus( Status.READY_TO_TRANSLATE );
                // we will only publish the version info!!!
                versionDTO.prepareSlPublishing( vocabularySnippet, licence, agency );
                break;
            case FORWARD_CV_SL_STATUS_PUBLISH:
                SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_SL_STATUS_PUBLISH,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

                // If SL is already published, we need to:
                // 1) clone published SL to the new bundle in order to keep track between consecutive bundles
                // 2) clone published TLs to the new bundle
                // 3) move unpublished TLs to the new bundle

                final Agency agencyFinal = agencyRepository.getOne( vocabularySnippet.getAgencyId() );
                VersionDTO newVersionDTO = null;
                if ( versionDTO.getStatus() == Status.PUBLISHED ) {
                    newVersionDTO = cloneVersion(versionDTO, versionDTO.getNumber().increasePatchNumber(), agencyFinal);
                    vocabularyDTO.addVersion(newVersionDTO);
                }

                final VersionNumber actualVersionNumber = versionDTO.getNumber();
                final VersionNumber finalPublishingVersionNumber = (
                    newVersionDTO != null
                ) ? newVersionDTO.getNumber() : versionDTO.getNumber();

                List<VersionDTO> clonedTls = new ArrayList<>();

                final VocabularyDTO finalVocabularyDTO = vocabularyDTO;
                final VersionDTO finalVersionDTO = versionDTO;

                // walk through the latest TL versions of the current major.minor bundle
                finalVocabularyDTO
                    .getVersions()
                    .stream()
                    .filter(v -> v.getItemType() == ItemType.TL )
                    .filter(v -> v.getNumber().equalMinorVersionNumber(actualVersionNumber))
                    .collect(
                        Collectors.groupingBy(
                            VersionDTO::getLanguage,
                            LinkedHashMap::new,
                            Collectors.maxBy(Comparator.comparing(VersionDTO::getNumber)))
                    )
                    .values()
                    .stream()
                    .flatMap(Optional::stream)
                    .forEach(v -> {
                        // publish TL, if it's ready
                        if ( v.getStatus() == Status.READY_TO_PUBLISH ) {
                            // update the TL's version number
                            v.setNumber(finalPublishingVersionNumber);
                            v.setStatus(Status.PUBLISHED);
                            // update URIs
                            v.updateUri(agencyFinal);
                            v.updateCanonicalUri(agencyFinal);
                            // update concept URIs
                            v.getConcepts().forEach(c -> c.setUri(VocabularyUtils.generateUri(agencyFinal.getUriCode(), finalVersionDTO, c ) ));
                            v.setLastStatusChangeDate(LocalDate.now());
                            finalVocabularyDTO.setVersionByLanguage(
                                v.getLanguage(),
                                v.getNumber().toString()
                            );
                        } else if ( v.getStatus() == Status.PUBLISHED ) {
                            clonedTls.add(cloneVersion(v, finalPublishingVersionNumber, agencyFinal));
                        } else {
                            if (v.getNumber().compareTo(finalPublishingVersionNumber) <= 0) {
                                // if this version is not yet published or ready to be published,
                                // it will be moved into the next bundle
                                v.setNumber(finalPublishingVersionNumber.increasePatchNumber());
                            }
                        }
                    });

                if (newVersionDTO != null) {
                    if (!clonedTls.isEmpty()) {
                        vocabularyDTO.getVersions().addAll(clonedTls);
                    }
                    versionDTO = newVersionDTO;
                } else {
                    // update SL version number
                    if (!versionDTO.getNumber().equals(finalPublishingVersionNumber)) {
                        versionDTO.setNumber(finalPublishingVersionNumber);
                    }
                }

                // publish SL
                versionDTO.setStatus(Status.PUBLISHED);
                versionDTO.setLastStatusChangeDate(LocalDate.now());
                versionDTO.setPublicationDate(LocalDate.now());
                versionDTO.setDeprecatedConceptsValidUntilVersionId(versionDTO.getId());
                vocabularyDTO.prepareSlPublishing(versionDTO);
                vocabularyDTO.setStatus(Status.PUBLISHED);
                break;
            case FORWARD_CV_TL_STATUS_REVIEW:
                SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_TL_STATUS_REVIEW,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
                versionDTO.setStatus( Status.REVIEW );
                versionDTO.setLastStatusChangeDate( LocalDate.now() );
                break;
            case FORWARD_CV_TL_STATUS_READY_TO_PUBLISH:
                SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH,
                    vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
                versionDTO.setStatus( Status.READY_TO_PUBLISH );
                versionDTO.setLastStatusChangeDate( LocalDate.now() );
                versionDTO.prepareTlPublishing( vocabularySnippet, licence, agency );
                vocabularyDTO.setVersionByLanguage( versionDTO.getLanguage(), versionDTO.getNumber().toString() );
                vocabularyDTO.setTitleDefinition(versionDTO.getTitle(), versionDTO.getDefinition(), versionDTO.getLanguage(), false);
                break;
            default:
                throw new IllegalActionTypeException( vocabularySnippet.getActionType() );
        }
        // check if SL published and not initial version, is there any TL needs to be cloned as
        // DRAFT?
        if ( vocabularySnippet.getActionType() == ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE && !versionDTO.isInitialVersion() ) {
            cloneTLsIfAny( vocabularyDTO, versionDTO );
        }
        // save at the end
        vocabularyDTO = save( vocabularyDTO );
        // indexing publication, delete existing one
        if ( versionDTO.getStatus() == Status.PUBLISHED ) {
            // generate json files
            generateJsonVocabularyPublish( vocabularyDTO );

            // reindex published json
            indexPublished( vocabularyDTO );
        }
        return versionDTO;
    }

    private void cloneTLsIfAny( VocabularyDTO vocabularyDTO, VersionDTO versionDTO ) {
        // find previous SL version, check if there is TLs
        Optional<VersionDTO> prevVersionSlOpt = versionService.findOne( versionDTO.getPreviousVersion() );
        if ( prevVersionSlOpt.isPresent() ) {
            VersionDTO prevVersionSl = prevVersionSlOpt.get();
            List<VersionDTO> clonedTls = new ArrayList<>();
            List<VersionDTO> prevVersions = vocabularyDTO.getVersionsByMinorVersionNumber(prevVersionSl.getNumber(), true );

            prevVersions.forEach( prevVersion ->
            {
                if ( prevVersion.getItemType() == ItemType.SL ) {
                    return;
                }
                log.info( "Clone {} TL {} version {} to version {}_DRAFT", versionDTO.getNotation(),
                    prevVersion.getLanguage(), prevVersion.getNumber() + "_" + prevVersion.getStatus(), new VersionNumber(versionDTO.getNumber(), 1) );
                // cloning need currentSL (as main reference for notation and position), previousSl
                // (as secondary reference if notation is changed in the current SL),
                // and previous TL for the rest of properties
                clonedTls.add( cloneTl( versionDTO, prevVersionSl, prevVersion ) );
            } );
            if ( !clonedTls.isEmpty() ) // save if any TLs is cloned
            {
                vocabularyDTO.getVersions().addAll( clonedTls );
            }
        }
    }

    private void updateUriForVersionAndConcept( String agencyUri, String agencyUriCode, Vocabulary vocabulary, Version version ) {
        if ( version.getStatus() == Status.PUBLISHED ) {
            version.setUri( VocabularyUtils.generateUri( agencyUri, vocabulary, version, null ) );
            for ( Concept concept : version.getConcepts() ) {
                concept.setUri( VocabularyUtils.generateUri( agencyUriCode, vocabulary, version, concept ) );
            }
        } else {
            version.setUri( null );
            for ( Concept concept : version.getConcepts() ) {
                concept.setUri( null );
            }
        }
    }

    @Override
    public Set<VersionDTO> filterOutVocabularyVersions( String versionList, VocabularyDTO vocabularyDTO ) {
        // filter out version
        if ( versionList != null ) {
            Set<VersionDTO> includedVersions = new LinkedHashSet<>();
            String[] versionSplits = versionList.split( "_" );
            for ( String vs : versionSplits ) {
                String[] s = vs.split( "-" );
                if ( s.length != 2 )
                    continue;
                VersionNumber versionNumberInList = VersionNumber.fromString(s[1]);
                Optional<VersionDTO> versionDTOOpt = vocabularyDTO
                    .getVersions()
                    .stream()
                    .filter(
                        v -> v.getLanguage().equals(s[0])
                            && v.getNumber().compareTo(versionNumberInList) == 0
                    ).findFirst();
                versionDTOOpt.ifPresent( includedVersions::add );
            }
            return includedVersions;
        } else {
            return vocabularyDTO.getVersions();
        }
    }

    private void prepareAdditionalAttributesForNonSkos( VocabularyDTO vocabularyDTO, Map<String, Object> map, AgencyDTO agencyDTO ) {
        if (vocabularyDTO.getAgencyLogo() != null)
        {
            readAgencyLogo( vocabularyDTO, agencyDTO );
        }

        for (VersionDTO versionDTO : vocabularyDTO.getVersions())
        {
            if ( versionDTO.getLicenseLogo() != null )
            {
                readLicenseLogo( versionDTO );
            }
        }

        for (VersionDTO versionDTO : vocabularyDTO.getVersions())
        {
            if (versionDTO.getCanonicalUri() != null)
            {
                int index = versionDTO.getCanonicalUri().lastIndexOf(':');
                map.put("cvUrn", versionDTO.getCanonicalUri().substring(0, index));
                break;
            }
        }
    }

    private void readAgencyLogo( VocabularyDTO vocabularyDTO, AgencyDTO agencyDTO )
    {
        Path logoFile = applicationProperties.getAgencyImagePath().resolve( vocabularyDTO.getAgencyLogo() );
        try
        {
            String base64Data = readFileToBase64( logoFile );
            agencyDTO.setLogo( base64Data );
        }
        catch ( IOException e )
        {
            log.warn( "Loading agency image from {} failed: {}", logoFile, e.toString() );
            agencyDTO.setLogo( null );
        }
    }

    private void readLicenseLogo( VersionDTO versionDTO )
    {
        Path licenceLogo = applicationProperties.getLicenseImagePath().resolve( versionDTO.getLicenseLogo() );
        try
        {
            String base64Data = readFileToBase64( licenceLogo );
            versionDTO.setLicenseLogo( base64Data );
        }
        catch ( IOException e )
        {
            log.warn( "Loading licence image from {} failed: {}", licenceLogo, e.toString() );
            versionDTO.setLicenseLogo( null );
        }
    }

    private static String readFileToBase64( Path path ) throws IOException
    {
        String data = DatatypeConverter.printBase64Binary( Files.readAllBytes( path ) );
        String type = Files.probeContentType( path );
        return "data:" + type + ";base64," + data;
    }

    private void setVocabularySelectedLanguage(
        EsQueryResultDetail esQueryResultDetail,
        List<VocabularyDTO> vocabularyList,
        String fieldType
    ) {
        if ( esQueryResultDetail.isAnyFilterActive() ) {
            esQueryResultDetail.getEsFilterByField( fieldType ).ifPresent( langFilter ->
            {
                if ( langFilter.getValues().size() == 1 ) {
                    for ( VocabularyDTO vocab : vocabularyList ) {
                        vocab.setSelectedLang( langFilter.getValues().get( 0 ) );
                    }
                }
            } );
        } else {
            for ( VocabularyDTO vocab : vocabularyList ) {
                if ( vocab.getSelectedLang() == null ) {
                    vocab.setSelectedLang( vocab.getSourceLanguage() );
                }
            }
        }
    }

    public ConceptDTO cloneConcept(ConceptDTO c, Long newVersionId) {

        ConceptDTO conceptCloned = c.copy();

        // override cloned attributes
        conceptCloned.setId(null);
        conceptCloned.setVersionId(newVersionId);
        if (c.getValidUntilVersionId() != null && c.getValidUntilVersionId().equals(c.getId())) {
            conceptCloned.setValidUntilVersionId(newVersionId);
        }
        conceptCloned.setPreviousConcept(c.getId());

        return conceptCloned;
    }

    public VersionDTO cloneVersion(final VersionDTO v, VersionNumber newVersionNumber, Agency agency) {

        VersionDTO versionCloned = v.copy();

        // override cloned attributes
        versionCloned.setId(null);
        versionCloned.setPreviousVersion(v.getId());
        versionCloned.setNumber(newVersionNumber);
        versionCloned.setVersionChanges(null);
        versionCloned.setVersionNotes(null);

        // update URIs
        versionCloned.updateUri(agency);
        versionCloned.updateCanonicalUri(agency);

        // save cloned version
        final VersionDTO finalVersionCloned = versionService.save(versionCloned);

        // migrate concepts
        v.getConcepts().forEach(concept -> {
            ConceptDTO conceptCloned = cloneConcept(concept, finalVersionCloned.getId());
            // update concept URI
            conceptCloned.setUri(
                VocabularyUtils.generateUri(
                    agency != null ? agency.getUriCode() : "",
                    finalVersionCloned,
                    conceptCloned
                )
            );
            finalVersionCloned.addConcept(conceptCloned);
        });

        v.getComments().forEach(comment -> {
            CommentDTO commentCloned = new CommentDTO();
            commentCloned.setContent(comment.getContent());
            commentCloned.setDateTime(comment.getDateTime());
            commentCloned.setId(null);
            commentCloned.setInfo(comment.getInfo());
            commentCloned.setUserId(comment.getUserId());
            commentCloned.setVersionId(finalVersionCloned.getId());
            finalVersionCloned.addComment(commentCloned);
        });

        return finalVersionCloned;
    }
}
