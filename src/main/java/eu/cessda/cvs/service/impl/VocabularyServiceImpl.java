package eu.cessda.cvs.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import eu.cessda.cvs.utils.VersionUtils;
import eu.cessda.cvs.utils.VocabularyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service Implementation for managing {@link Vocabulary}.
 */
@Service
@Transactional
public class VocabularyServiceImpl implements VocabularyService {

    private final Logger log = LoggerFactory.getLogger(VocabularyServiceImpl.class);

    public static final String COUNT = "_count";
    public static final String TITLE = "title";
    public static final String DEFINITION = "definition";
    public static final String HIGHLIGHT_START = "<span class=\"highlight\">";
    public static final String HIGHLIGHT_END = "</span>";
    public static final int SIZE_OF_ITEMS_ON_AGGREGATION = 10000;
    public static final String JSON_FORMAT = ".json";
    public static final String UNABLE_FIND_VOCABULARY = "Unable to find vocabulary with Id ";
    public static final String UNABLE_FIND_VERSION = "Unable to find version with Id ";
    public static final String LATEST = "latest";
    public static final String ALL = "all";

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

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final ApplicationProperties applicationProperties;

    private final AgencyStatSearchRepository agencyStatSearchRepository;

    public VocabularyServiceImpl(AgencyRepository agencyRepository, ConceptService conceptService, ExportService exportService,
                                 LicenceRepository licenceRepository, VersionService versionService,
                                 VocabularyChangeService vocabularyChangeService, VocabularyRepository vocabularyRepository,
                                 VocabularyMapper vocabularyMapper, VocabularyEditorMapper vocabularyEditorMapper,
                                 VocabularyPublishMapper vocabularyPublishMapper, VocabularyEditorSearchRepository vocabularyEditorSearchRepository,
                                 VocabularyPublishSearchRepository vocabularyPublishSearchRepository,
                                 ElasticsearchTemplate elasticsearchTemplate, ApplicationProperties applicationProperties,
                                 AgencyStatSearchRepository agencyStatSearchRepository) {
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
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.applicationProperties = applicationProperties;
        this.agencyStatSearchRepository = agencyStatSearchRepository;
    }

    /**
     * Save a vocabulary.
     *
     * @param vocabularyDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VocabularyDTO save(VocabularyDTO vocabularyDTO) {
        log.debug("Request to save Vocabulary : {}", vocabularyDTO);
        Vocabulary vocabulary;
        // for new vocabulary
        if( !vocabularyDTO.isPersisted()) {
            vocabulary = createNewVocabulary(vocabularyDTO);
        }
        // if updated vocabulary
        else {
            vocabulary = updateVocabulary(vocabularyDTO);
        }
        vocabularyDTO = vocabularyMapper.toDto(vocabulary);
        // indexing
        indexEditor(vocabularyDTO);
        return vocabularyDTO;
    }

    private Vocabulary updateVocabulary(VocabularyDTO vocabularyDTO) {
        return vocabularyRepository.save(vocabularyMapper.toEntity(vocabularyDTO));
    }

    @Override
    public VocabularyDTO saveVocabulary(VocabularySnippet vocabularySnippet) {
        if( vocabularySnippet.getActionType().equals( ActionType.CREATE_CV )){
            return save(new VocabularyDTO( vocabularySnippet ));
        } else if( vocabularySnippet.getActionType().equals( ActionType.ADD_TL_CV )){
            return saveTlVocabulary(vocabularySnippet);
        } else if(
            vocabularySnippet.getActionType().equals( ActionType.EDIT_CV ) ||
            vocabularySnippet.getActionType().equals( ActionType.EDIT_DDI_CV) ||
            vocabularySnippet.getActionType().equals( ActionType.EDIT_VERSION_INFO_CV) ||
            vocabularySnippet.getActionType().equals( ActionType.EDIT_NOTE_CV)
        ){
            // get Vocabulary from database
            VocabularyDTO vocabularyDTO = findOne(vocabularySnippet.getVocabularyId())
                .orElseThrow( () -> new EntityNotFoundException(UNABLE_FIND_VERSION + vocabularySnippet.getVocabularyId() ));

            if( vocabularyDTO == null) {
                throw new IllegalArgumentException( "Unable to save edited vocabulary, Vocabulary with ID "
                    + vocabularySnippet.getVersionId() + " not found");
            }

            // find version on vocabulary
            VersionDTO versionDTO = vocabularyDTO.getVersions().stream()
                .filter(v -> v.getId().equals( vocabularySnippet.getVersionId())).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VERSION + vocabularySnippet.getVersionId()) );

            if( versionDTO == null ) {
                throw new IllegalArgumentException( "Unable to save edited vocabulary, version with ID "
                    + vocabularySnippet.getVersionId() + " not found");
            }

            if( vocabularySnippet.getActionType().equals( ActionType.EDIT_CV ) ) {
                // check if user authorized to edit VocabularyResource
                SecurityUtils.checkResourceAuthorization(ActionType.EDIT_CV,
                    vocabularySnippet.getAgencyId(), ActionType.EDIT_CV.getAgencyRoles(), vocabularySnippet.getLanguage());
                // set non-editable (via edit-cv-form) versionNumber and language on vocabularySnippet with versionDTO
                vocabularySnippet.setLanguage( versionDTO.getLanguage());
                vocabularySnippet.setVersionNumber( versionDTO.getNumber() );
                // only title, definition, notes, translateAgency, translateAgencyLink
                vocabularyDTO.setContentByVocabularySnippet( vocabularySnippet );
                versionDTO.setContentByVocabularySnippet( vocabularySnippet );

                // check if codeSnippet contains changetype
                storeChangeType(vocabularySnippet, versionDTO);
            } else if (vocabularySnippet.getActionType().equals( ActionType.EDIT_DDI_CV )) {
                // check if user authorized to edit ddi-usage VocabularyResource
                SecurityUtils.checkResourceAuthorization(ActionType.EDIT_DDI_CV,
                    vocabularySnippet.getAgencyId(), ActionType.EDIT_DDI_CV.getAgencyRoles(), vocabularySnippet.getLanguage());

                versionDTO.setDdiUsage( vocabularySnippet.getDdiUsage() );
            } else if (vocabularySnippet.getActionType().equals( ActionType.EDIT_NOTE_CV )) {
                // check if user authorized to edit ddi-usage VocabularyResource
                SecurityUtils.checkResourceAuthorization(ActionType.EDIT_NOTE_CV,
                    vocabularySnippet.getAgencyId(), ActionType.EDIT_NOTE_CV.getAgencyRoles(), vocabularySnippet.getLanguage());

                versionDTO.setNotes( vocabularySnippet.getNotes() );
            } else if (vocabularySnippet.getActionType().equals( ActionType.EDIT_VERSION_INFO_CV )) {
                // check if user authorized to edit ddi-usage VocabularyResource
                SecurityUtils.checkResourceAuthorization(ActionType.EDIT_VERSION_INFO_CV,
                    vocabularySnippet.getAgencyId(), ActionType.EDIT_VERSION_INFO_CV.getAgencyRoles(), vocabularySnippet.getLanguage());

                updateCvVersionInfo(vocabularySnippet, versionDTO);
            }

            vocabularyDTO = save( vocabularyDTO );

            // regenerate json file is version already published
            if( versionDTO.getStatus().equals(Status.PUBLISHED.toString())){
                generateJsonVocabularyPublish( vocabularyDTO );
            }

            return vocabularyDTO;
        }
        return null;// change with exception "Action not found"
    }

    private void updateCvVersionInfo(VocabularySnippet vocabularySnippet, VersionDTO versionDTO) {
        versionDTO.setVersionNotes( vocabularySnippet.getVersionNotes() );
        versionDTO.setVersionChanges( vocabularySnippet.getVersionChanges() );

        if( vocabularySnippet.getVersionNumber() != null )
            versionDTO.setNumber( vocabularySnippet.getVersionNumber() );
        if( vocabularySnippet.getLicenseId() != null )
            versionDTO.setLicenseId( vocabularySnippet.getLicenseId() );
    }

    @Override
    public VersionDTO createNewVersion(Long prevVersionId ) {
        log.debug("Request to create new Vocabulary version from existing version with ID : {}", prevVersionId);
        // get version
        VersionDTO prevVersionDTO = versionService.findOne(prevVersionId)
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VERSION + prevVersionId));

        if( !prevVersionDTO.getStatus().equals(Status.PUBLISHED.toString())) {
            log.error( "Unable to create new version from version with ID " + prevVersionDTO.getId() + ", version is not yet PUBLISHED" );
            throw new IllegalArgumentException( "Unable to create new version from version with ID " + prevVersionDTO.getId() + ", version is not yet PUBLISHED" );
        }

        VocabularyDTO vocabularyDTO = findOne(prevVersionDTO.getVocabularyId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VOCABULARY + prevVersionDTO.getVocabularyId()));

        VersionDTO currentSlVersion = vocabularyDTO.getVersions().stream()
            .filter( v -> v.getItemType().equals(ItemType.SL.toString()) && v.getNumber().equals( vocabularyDTO.getVersionNumber()))
            .findFirst().orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VERSION + "SL version number " + vocabularyDTO.getVersionNumber()));

        VersionDTO newVersion;
        if( prevVersionDTO.getItemType().equals( ItemType.SL.toString() )) {
            // check if user authorized to create new SL version
            SecurityUtils.checkResourceAuthorization(ActionType.CREATE_NEW_CV_SL_VERSION,
                vocabularyDTO.getAgencyId(), ActionType.CREATE_NEW_CV_SL_VERSION.getAgencyRoles(), prevVersionDTO.getLanguage());
            newVersion = new VersionDTO( prevVersionDTO, null);
            // update vocabularyDTO
            vocabularyDTO.setVersionNumber( newVersion.getNumber());
            vocabularyDTO.setStatus( Status.DRAFT.toString() );
        } else {
            // check if user authorized to create new TL version
            SecurityUtils.checkResourceAuthorization(ActionType.CREATE_NEW_CV_TL_VERSION,
                vocabularyDTO.getAgencyId(), ActionType.CREATE_NEW_CV_TL_VERSION.getAgencyRoles(), prevVersionDTO.getLanguage());
            newVersion = new VersionDTO( prevVersionDTO, currentSlVersion);
        }
        newVersion.setCreator( SecurityUtils.getCurrentUserId() );
        newVersion = versionService.save( newVersion );

        VersionDTO finalNewVersion = newVersion;
        currentSlVersion.getConcepts().forEach(conceptSlDTO -> {
            ConceptDTO conceptDTO;
            if( finalNewVersion.getItemType().equals( ItemType.SL.toString()) ) {
                conceptDTO = new ConceptDTO(conceptSlDTO, conceptSlDTO, finalNewVersion.getId());
            } else {
                ConceptDTO prevConcept = prevVersionDTO.getConcepts().stream()
                    .filter(c -> c.getNotation().equals(conceptSlDTO.getNotation())).findFirst().orElse(null);
                conceptDTO = new ConceptDTO(conceptSlDTO, prevConcept, finalNewVersion.getId());
            }
            finalNewVersion.addConcept(conceptDTO );
        });
        // save and reindex
        addVersionAndSaveIndexVocabulary(vocabularyDTO, finalNewVersion);
        return finalNewVersion;
    }

    private void addVersionAndSaveIndexVocabulary(VocabularyDTO vocabularyDTO, VersionDTO finalNewVersion) {
        // linked vocabulary and version
        vocabularyDTO.addVersion( finalNewVersion );
        // at the end save (version will automatically saved with Cascade)
        Vocabulary vocabulary = vocabularyMapper.toEntity(vocabularyDTO);
        vocabulary = vocabularyRepository.save(vocabulary);
        vocabularyDTO = vocabularyMapper.toDto(vocabulary);
        // index editor
        indexEditor(vocabularyDTO );
    }

    private VocabularyDTO saveTlVocabulary(VocabularySnippet vocabularySnippet) {
        // get Vocabulary from database
        VocabularyDTO vocabularyDTO = findOne(vocabularySnippet.getVocabularyId())
            .orElseThrow( () -> new EntityNotFoundException(UNABLE_FIND_VERSION + vocabularySnippet.getVocabularyId() ));
        // get slVersion from vocabularySnippet
        VersionDTO versionSlDTO = vocabularyDTO.getVersions().stream().filter(v -> v.getId().equals( vocabularySnippet.getVersionSlId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VERSION + vocabularySnippet.getVersionSlId()) );

        // create new tl version by vocabularySnippet
        VersionDTO versionDTO = new VersionDTO(vocabularySnippet, versionSlDTO);
        // find previous version with same language if any
        VersionDTO prevVersionTlDTO = null;
        Optional<VersionDTO> prevVersionTlDTOOptl = vocabularyDTO.getVersions().stream().filter(v -> v.getLanguage().equals(vocabularySnippet.getLanguage())).findFirst();
        if( prevVersionTlDTOOptl.isPresent() ) {
            prevVersionTlDTO = prevVersionTlDTOOptl.get();
            versionDTO.setPreviousVersion( prevVersionTlDTO.getId() );
            versionDTO.setDdiUsage( prevVersionTlDTO.getDdiUsage() );
            versionDTO.setInitialVersion( prevVersionTlDTO.getInitialVersion() == null ? prevVersionTlDTO.getId() : prevVersionTlDTO.getInitialVersion());
        }
        versionDTO.setCreator( SecurityUtils.getCurrentUserId() );
        versionDTO = versionService.save( versionDTO );
        // generate concepts from currentSlconcept and previousVersion
        generateConceptFromSlAndPrevVersion( versionDTO, versionSlDTO, prevVersionTlDTO);
        // save and reindex
        addVersionAndSaveIndexVocabulary(vocabularyDTO, versionDTO);
        return vocabularyDTO;
    }

    private void generateConceptFromSlAndPrevVersion(VersionDTO versionDTO, VersionDTO versionSlDTO, VersionDTO prevVersionTlDTO) {
        versionSlDTO.getConcepts().forEach(conceptSlDTO -> {
            ConceptDTO conceptDTO = new ConceptDTO( conceptSlDTO );
            conceptDTO.setVersionId( versionDTO.getId() );
            // simply fill with prevVersionTlDTO if exist
            // if on SL notation is changed then the prevConcept will not be linked anymore
            // (Improvement in future lo linked the prev concept with modified notation?)
            if( prevVersionTlDTO != null ) {
                prevVersionTlDTO.getConcepts().stream().filter(c -> c.getNotation().equals( conceptDTO.getNotation() ))
                    .findFirst().ifPresent(cPrev -> {
                        conceptDTO.setPreviousConcept( cPrev.getId() );
                        conceptDTO.setTitle( cPrev.getTitle() );
                        conceptDTO.setDefinition( cPrev.getDefinition() );
                });
            }
            // add concept to version
            versionDTO.addConcept(conceptDTO );
        });
    }

    private Vocabulary createNewVocabulary(VocabularyDTO vocabularyDTO) {
        // check if Vocabulary already exist for new vocabulary
        if (vocabularyRepository.existsByNotation(vocabularyDTO.getNotation())) {
            throw new VocabularyAlreadyExistException();
        }

        // query agency
        final Agency agency = agencyRepository.getOne(vocabularyDTO.getAgencyId());

        // set Vocabulary attribute for initial version
        vocabularyDTO.setStatus(Status.DRAFT.toString());
        vocabularyDTO.setAgencyLink( agency.getLink() );
        vocabularyDTO.setAgencyName( agency.getName() );
        vocabularyDTO.setAgencyLogo( agency.getLogopath() );
        vocabularyDTO.setUri( VocabularyUtils.generateAgencyBaseUri( agency.getUri() ) + vocabularyDTO.getNotation() + "/" + vocabularyDTO.getSourceLanguage());

        // create a new version by vocabularyDTO
        VersionDTO versionDTO = new VersionDTO( vocabularyDTO );
        vocabularyDTO.addVersion(versionDTO);
        // add more version details
        versionDTO.setCreator( SecurityUtils.getCurrentUserId() );

        // save vocabulary
        Vocabulary vocabulary = vocabularyMapper.toEntity(vocabularyDTO);
        Version initialSlVersion = vocabulary.getVersions().iterator().next();
        initialSlVersion.setVocabulary( vocabulary );
        // at the end save (version will automatically saved with Cascade)
        vocabulary = vocabularyRepository.save(vocabulary);
        return vocabulary;
    }

    /**
     * Save a concept by codeSnippet
     *
     * @param codeSnippet the minimized version of Code and Version information
     * @return saved conceptDTO
     */
    @Override
    public ConceptDTO saveCode(CodeSnippet codeSnippet) {
        log.debug("Request to save Concept and Version from CodeSnippet : {}", codeSnippet.getConceptId());
        ConceptDTO conceptDTO = null;
        // get version
        VersionDTO versionDTO = versionService.findOne(codeSnippet.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VERSION + codeSnippet.getVersionId()));

        // reject if version status is published
        if( versionDTO.getStatus().equals( Status.PUBLISHED.toString() )) {
            throw new IllegalArgumentException( "Unable to add Code " + codeSnippet.getNotation() + ", Version is already PUBLISHED" );
        }

        VocabularyDTO vocabularyDTO = findOne(versionDTO.getVocabularyId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VOCABULARY + versionDTO.getVocabularyId()));

        if( codeSnippet.getActionType().equals( ActionType.CREATE_CODE )){
            conceptDTO = createCode(codeSnippet, versionDTO, vocabularyDTO);
        } else if( codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.ADD_TL_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.EDIT_TL_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.DELETE_TL_CODE )){
            conceptDTO = editCode(codeSnippet, versionDTO, vocabularyDTO);
        }
        return conceptDTO;
    }

    private ConceptDTO editCode(CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO) {
        ConceptDTO conceptDTO;
        // check for authorization
        checkEditCodeAuthorization(codeSnippet, versionDTO, vocabularyDTO);
        // get concept from versionDTO
        conceptDTO = versionDTO.getConcepts().stream().filter(c -> c.getId().equals(codeSnippet.getConceptId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Unable to find concept with Id " + codeSnippet.getConceptId()));

        // check duplicated code notation
        if( codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) && !conceptDTO.getNotation().equals( codeSnippet.getNotation()) ) {
            if (versionDTO.getConcepts().stream()
                .anyMatch(c -> c.getNotation().equals( codeSnippet.getNotation()))) {
                throw new CodeAlreadyExistException();
            }

            // check if this concept influence parent and child concepts
            for (ConceptDTO next : versionDTO.getConcepts()) {
                if (next.getParent() != null && next.getParent().startsWith(conceptDTO.getNotation())) {
                    next.setParent(next.getParent().replace(conceptDTO.getNotation(), codeSnippet.getNotation()));
                    next.setNotation(next.getNotation().replace(conceptDTO.getNotation(), codeSnippet.getNotation()));
                }
            }

            // change current concept notation property
            conceptDTO.setNotation( codeSnippet.getNotation() );
        }

        if ( codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.ADD_TL_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.EDIT_TL_CODE )) {
            conceptDTO.setTitle( codeSnippet.getTitle());
            conceptDTO.setDefinition( codeSnippet.getDefinition());
        } else if ( codeSnippet.getActionType().equals( ActionType.DELETE_TL_CODE )) {
            conceptDTO.setTitle( null );
            conceptDTO.setDefinition( null );
        }

        // save versionDTO together with concepts
        versionDTO = versionService.save(versionDTO);

        // check if codeSnippet contains changeType, store if exist
        storeChangeType(codeSnippet, versionDTO);

        // find the newly created code from version
        conceptDTO = versionDTO.findConceptByNotation(conceptDTO.getNotation());

        // index editor
        indexEditor(vocabularyDTO);
        return conceptDTO;
    }

    private void storeChangeType(VocabularySnippet vocabularySnippet, VersionDTO versionDTO) {
        if ( vocabularySnippet.getChangeType() != null ) {
            vocabularySnippet.setVersionId( versionDTO.getId());
            VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO(vocabularySnippet, SecurityUtils.getCurrentUser(),
                versionDTO.getVocabularyId() );
            vocabularyChangeService.save(vocabularyChangeDTO );
        }
    }

    private void storeChangeType(CodeSnippet codeSnippet, VersionDTO versionDTO) {
        if ( codeSnippet.getChangeType() != null && !versionDTO.isInitialVersion()) {
            codeSnippet.setVersionId( versionDTO.getId());
            VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO(codeSnippet, SecurityUtils.getCurrentUser(),
                versionDTO.getVocabularyId() );
            vocabularyChangeService.save(vocabularyChangeDTO );
        }
    }

    private void checkEditCodeAuthorization(CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO) {
        if( codeSnippet.getActionType().equals( ActionType.EDIT_CODE  ))
            SecurityUtils.checkResourceAuthorization(ActionType.EDIT_CODE,
                vocabularyDTO.getAgencyId(), ActionType.EDIT_CODE.getAgencyRoles(), versionDTO.getLanguage());
        else if( codeSnippet.getActionType().equals( ActionType.ADD_TL_CODE  ))
            SecurityUtils.checkResourceAuthorization(ActionType.ADD_TL_CODE,
                vocabularyDTO.getAgencyId(), ActionType.ADD_TL_CODE.getAgencyRoles(), versionDTO.getLanguage());
        else if( codeSnippet.getActionType().equals( ActionType.EDIT_TL_CODE  ))
            SecurityUtils.checkResourceAuthorization(ActionType.EDIT_TL_CODE,
                vocabularyDTO.getAgencyId(), ActionType.EDIT_TL_CODE.getAgencyRoles(), versionDTO.getLanguage());
        else if( codeSnippet.getActionType().equals( ActionType.DELETE_TL_CODE  ))
            SecurityUtils.checkResourceAuthorization(ActionType.DELETE_TL_CODE,
                vocabularyDTO.getAgencyId(), ActionType.DELETE_TL_CODE.getAgencyRoles(), versionDTO.getLanguage());
    }

    private ConceptDTO createCode(CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO) {
        ConceptDTO conceptDTO;
        // check for authorization
        SecurityUtils.checkResourceAuthorization(ActionType.CREATE_CODE,
            vocabularyDTO.getAgencyId(), ActionType.CREATE_CODE.getAgencyRoles(), versionDTO.getLanguage());

        // check if concept already exist for new concept
        if (versionDTO.getConcepts().stream()
            .anyMatch(c -> c.getNotation().equals( codeSnippet.getNotation()))) {
            throw new CodeAlreadyExistException();
        }
        // set position if not available
        if( codeSnippet.getPosition() == null )
            codeSnippet.setPosition( versionDTO.getConcepts().size() - 1 );
        // create concept by codeSnippet
        ConceptDTO newConceptDTO = new ConceptDTO( codeSnippet );
        // add concept to version and save version to save new concept
        versionDTO.addConceptAt(newConceptDTO, newConceptDTO.getPosition());
        versionDTO = versionService.save(versionDTO);

        // check if codeSnippet contains changeType, dtore if exist
        storeChangeType(codeSnippet, versionDTO);

        // find the newly created code from version
        conceptDTO = versionDTO.findConceptByNotation(newConceptDTO.getNotation());

        // index editor
        indexEditor(vocabularyDTO);
        return conceptDTO;
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
        List<VocabularyDTO> vocabulariesDTO = vocabularyRepository.findAll().stream()
            .map(vocabularyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));

        sortVocabularyVersions( vocabulariesDTO);
        return vocabulariesDTO;
    }

    private void sortVocabularyVersions(List<VocabularyDTO> vocabulariesDTO) {
        for (VocabularyDTO vocabularyDTO : vocabulariesDTO) {
            LinkedHashSet<VersionDTO> sortedVersion = vocabularyDTO.getVersions().stream().sorted(VocabularyUtils.versionDtoComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
            vocabularyDTO.setVersions(sortedVersion);
        }
    }

    /**
     * Get all the vocabularies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Vocabularies");
        final Page<VocabularyDTO> vocabPage = vocabularyRepository.findAll(pageable).map(vocabularyMapper::toDto);
        sortVocabularyVersions( vocabPage.getContent());
        return vocabPage;

    }


    /**
     * Get one vocabulary by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VocabularyDTO> findOne(Long id) {
        log.debug("Request to get Vocabulary : {}", id);
        return vocabularyRepository.findById(id)
            .map(vocabularyMapper::toDto);
    }

    /**
     * Delete the vocabulary by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vocabulary : {}", id);
        VocabularyDTO vocabularyDTO = findOne( id )
            .orElseThrow( () -> new EntityNotFoundException(UNABLE_FIND_VOCABULARY + id ));
        String notation = vocabularyDTO.getNotation();

        // update/create agency publish
        agencyStatSearchRepository.findById(vocabularyDTO.getAgencyId()).ifPresent(agencyPublish -> {
            agencyPublish.deleteVocabStat( vocabularyDTO.getNotation() );
            agencyStatSearchRepository.save(agencyPublish );
        });

        vocabularyRepository.deleteById(id);
        // delete index
        vocabularyEditorSearchRepository.deleteById(id);
        vocabularyPublishSearchRepository.deleteById(id);
        // delete files if any
        deleteCvJsonDirectoryAndContent(applicationProperties.getVocabJsonPath() + notation);
    }

    public void deleteCvJsonDirectoryAndContent(String path) {
        File dirPath = new File( path );
        if( dirPath.isDirectory() ) {
            try {
                FileUtils.deleteDirectory(dirPath);
            } catch (IOException e) {
                log.error( "Unable to delete file " + e.getMessage() );
            }
        }
    }

    @Override
    public void doIndexingEditorAndPublicationCvs( boolean force ) {
        if( vocabularyRepository.count() > 0 && vocabularyEditorSearchRepository.count() == 0L || force) {
            log.info( "Performing indexing on all Editor Vocabularies" );
            indexAllEditor();
        }
        if( getPublishedCvPaths() != null && !getPublishedCvPaths().isEmpty() && vocabularyPublishSearchRepository.count() == 0 || force) {
            log.info( "Performing indexing on all Published Vocabularies" );
            indexAllPublished();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyDTO> findAllWithdrawn(Pageable pageable) {
        log.debug("Request to get all withdrawn Vocabularies");
        return vocabularyRepository.findAllWithdrawn(pageable)
            .map(vocabularyMapper::toDto);
    }

    @Override
    public Page<VocabularyDTO> findAllWithdrawn(Long agencyId, Pageable pageable) {
        log.debug("Request to get all withdrawn Vocabularies by agencyId {}", agencyId);
        return vocabularyRepository.findAllWithdrawn(agencyId, pageable)
            .map(vocabularyMapper::toDto);
    }

    @Override
    public VocabularyDTO getByNotation(String notation) {
        if( notation == null || notation.isEmpty()) {
            log.error("Error notation could not be empty or null");
            throw new IllegalArgumentException("Error notation could not be empty or null");
        }
        log.debug("Request to get Vocabulary by notation: {}", notation);
        Vocabulary vocabulary = vocabularyRepository.findByNotation(notation);
        if( vocabulary == null) {
            log.error("Error vocabulary with notation {} does not exist", notation);
            throw new EntityNotFoundException( UNABLE_FIND_VOCABULARY + "or notation " + notation);
        }
        return vocabularyMapper.toDto(vocabulary);
    }

    @Override
    public VersionDTO cloneTl(VersionDTO currentVersionSl, VersionDTO prevVersionSl, VersionDTO prevVersionTl) {
        VersionDTO newTlVersion = new VersionDTO( prevVersionTl, currentVersionSl);
        newTlVersion.setCreator( SecurityUtils.getCurrentUserId() );
        // if previous TL version is not published yet, assign version as initial version
        if( !prevVersionTl.getStatus().equals( Status.PUBLISHED.toString()) ) {
            newTlVersion.setPreviousVersion( null );
            newTlVersion.setInitialVersion( null );
        }
        newTlVersion = versionService.save( newTlVersion );

        VersionDTO finalNewTlVersion = newTlVersion;
        currentVersionSl.getConcepts().forEach(conceptSlDTO -> {
            ConceptDTO newConceptTl = new ConceptDTO( conceptSlDTO );
            newConceptTl.setVersionId( finalNewTlVersion.getId() );

            if( conceptSlDTO.getPreviousConcept() != null ){
                // try to find concept title, definition from previous concept
                // first, try to find the previous concept. in this case the TL prev concept will be found
                // if there is no change in the notation between prev and current SL concept
                ConceptDTO prevConceptTl = prevVersionTl.getConcepts().stream()
                    .filter(c -> c.getNotation().equals(conceptSlDTO.getNotation())).findFirst().orElse(null);
                // if not found, try to find old notation from the previous SL concept
                if( prevConceptTl == null ) {
                    prevConceptTl = getPrevTlConceptByPrevSlNotation(prevVersionSl, prevVersionTl, conceptSlDTO, prevConceptTl);
                }
                // assign title and definition if previous TL concept found
                if( prevConceptTl != null ) {
                    newConceptTl.setTitle( prevConceptTl.getTitle());
                    newConceptTl.setDefinition(prevConceptTl.getDefinition());
                    newConceptTl.setPreviousConcept( prevConceptTl.getId() );
                    // if previous TL version is not published yet, assign concept as initial concept
                    if( !prevVersionTl.getStatus().equals( Status.PUBLISHED.toString()) ) {
                        newConceptTl.setPreviousConcept( null );
                    } else {
                        newConceptTl.setPreviousConcept( prevConceptTl.getId() );
                    }
                }
            }

            finalNewTlVersion.addConcept(newConceptTl);
        });

        return newTlVersion;
    }

    private ConceptDTO getPrevTlConceptByPrevSlNotation(VersionDTO prevVersionSl, VersionDTO prevVersionTl, ConceptDTO conceptSlDTO, ConceptDTO prevConceptTl) {
        String oldSlNotation = prevVersionSl.getConcepts().stream()
            .filter(c -> c.getId().equals(conceptSlDTO.getPreviousConcept())).map(ConceptDTO::getNotation).findFirst().orElse(null);
        if( oldSlNotation != null ) {
            prevConceptTl = prevVersionTl.getConcepts().stream()
                .filter(c -> c.getNotation().equals(oldSlNotation)).findFirst().orElse(null);
        }
        return prevConceptTl;
    }

    @Override
    public VocabularyDTO getWithVersionsByNotationAndVersion(String notation, String slVersionNumber) {
        // get all available licenses
        final List<Licence> licenceList = licenceRepository.findAll();

        if( slVersionNumber == null || slVersionNumber.isEmpty()) {
            log.error("Error version number could not be empty or null");
            throw new IllegalArgumentException("Error version number  could not be empty or null");
        }
        VocabularyDTO vocabulary = getByNotation(notation);
        final Agency agency = agencyRepository.getOne(vocabulary.getAgencyId());
        vocabulary.setAgencyLink(agency.getLink());
        vocabulary.setAgencyLogo(agency.getLogopath());
        vocabulary.setAgencyLink(agency.getLink());

        if( slVersionNumber.equals(ALL))
            return vocabulary;

        if( slVersionNumber.equals(LATEST)) {
            final VersionDTO latestSlVersion = vocabulary.getVersions().stream().sorted(VocabularyUtils.versionDtoComparator())
                .findFirst().orElse(null);
            if( latestSlVersion != null )
                slVersionNumber = latestSlVersion.getNumber();
        }

        getOneLatestVersionEachLanguage(slVersionNumber, licenceList, vocabulary, true);

        return vocabulary;
    }

    private void getOneLatestVersionEachLanguage(String slVersionNumber, List<Licence> licenceList,
                                                 VocabularyDTO vocabulary, boolean isAddHistory) {
        Set<VersionDTO> versionDTOs = new LinkedHashSet<>();
        List<VersionDTO> versionsGroup = versionService.findAllByVocabularyAnyVersionSl(vocabulary.getId(), slVersionNumber);

        Set<String> languages = new HashSet<>();
        for (VersionDTO version : versionsGroup) {
            // check for more than one version in one language, only return latest one
            if (languages.contains(version.getLanguage()))
                continue;
            languages.add(version.getLanguage());

            // add version to new list and sort concepts
            LinkedHashSet<ConceptDTO> sortedConcepts = version.getConcepts().stream()
                .sorted(Comparator.comparing(ConceptDTO::getPosition)).collect(Collectors.toCollection(LinkedHashSet::new));
            version.setConcepts(sortedConcepts);

            // assign licence
            if( licenceList != null ) {
                licenceList.stream().filter(l -> l.getId().equals(version.getLicenseId())).findFirst().ifPresent(l -> {
                    version.setLicense(l.getAbbr());
                    version.setLicenseLink(l.getLink());
                    version.setLicenseName(l.getName());
                    version.setLicenseLogo(l.getLogoLink());
                });
            }

            // add history
            if( isAddHistory )
                addVersionHistories(vocabulary, version);

            versionDTOs.add(version);
        }
        vocabulary.setVersions( versionDTOs );
    }

    @Override
    public VocabularyDTO withdraw(VocabularyDTO vocabulary) {
        vocabulary.setWithdrawn( true );
        vocabulary = save(vocabulary);

        // remove vocabulary from both index
        VocabularyEditor vocabEditor = vocabularyEditorMapper.toEntity( vocabulary);
        vocabularyEditorSearchRepository.delete(vocabEditor);
        VocabularyPublish vocabPublish = vocabularyPublishMapper.toEntity( vocabulary);
        vocabularyPublishSearchRepository.delete(vocabPublish);

        return vocabulary;
    }

    @Override
    public VocabularyDTO restore(VocabularyDTO vocabulary) {
        vocabulary.setWithdrawn( false );
        vocabulary = save(vocabulary);

        // reindex for publication and editor
        VocabularyPublish vocabPublish = vocabularyPublishMapper.toEntity( vocabulary);
        vocabularyPublishSearchRepository.save(vocabPublish);
        VocabularyEditor vocabEditor = vocabularyEditorMapper.toEntity( vocabulary);
        vocabularyEditorSearchRepository.save(vocabEditor);

        return vocabulary;
    }

    @Override
    public void indexAllEditor() {
        log.info("INDEXING ALL EDITOR VOCABULARIES START");
        // remove any index
        vocabularyEditorSearchRepository.deleteAll();
        // index all vocabularies
        findAll().forEach(v -> {
            // skip for withdrawn vocabulary
            if( Boolean.TRUE.equals( v.isWithdrawn()) )
                return;
            indexEditor(v);
        });
        log.info("INDEXING ALL EDITOR VOCABULARIES FINISHED");
    }

    @Override
    public void indexEditor(VocabularyDTO vocabulary) {
        log.info("Indexing editor vocabulary with id {} and notation {}", vocabulary.getId(), vocabulary.getNotation());
        // filter only include latest vocabulary
        // get latest version
        final VersionDTO maxSlVersion = vocabulary.getVersions().stream()
            .filter(v -> v.getItemType().equals(ItemType.SL.toString()))
            .max((v1, v2) -> VersionUtils.compareVersion(v1.getNumber(), v2.getNumber()))
            .orElse(null);

        if( maxSlVersion == null )
            return;

        // on editor set Version to be latest SL of any version
        if( !vocabulary.getVersionNumber().equals(maxSlVersion.getNumber())){
            vocabulary.setVersionNumber( maxSlVersion.getNumber() );
        }

        getOneLatestVersionEachLanguage(maxSlVersion.getNumber(), null, vocabulary, false);

        // update/create agency publish
        agencyStatSearchRepository.findById(vocabulary.getAgencyId()).ifPresent(agencyPublish -> {
            agencyPublish.updateVocabStat( vocabulary);
            agencyStatSearchRepository.save(agencyPublish );
        });


        // fill vocabulary with versions
        VocabularyDTO.fillVocabularyByVersions(vocabulary, vocabulary.getVersions());
        // fill CodeDTO object from versions
        vocabulary.setCodes( CodeDTO.generateCodesFromVersion(vocabulary.getVersions(), true));

        VocabularyEditor vocab = vocabularyEditorMapper.toEntity( vocabulary);
        vocabularyEditorSearchRepository.save( vocab );
    }

    @Override
    public void indexAllAgencyStats() {
        log.info("INDEXING ALL AGENCY VOCABULARIES STATISTIC START");
        findAll().forEach(v -> {
            if( Boolean.TRUE.equals( v.isWithdrawn()) )
                return;
            indexAgencyStats(v);
        });
        log.info("INDEXING ALL AGENCY VOCABULARIES STATISTIC FINISHED");
    }

    @Override
    public void indexAgencyStats(VocabularyDTO vocabulary) {
        // update/create agency publish
        agencyStatSearchRepository.findById(vocabulary.getAgencyId()).ifPresent(agencyPublish -> {
            agencyPublish.updateVocabStat( vocabulary);
            agencyStatSearchRepository.save(agencyPublish );
        });
    }

    @Override
    public Path getPublishedCvPath(String notation, String versionNumber) {
        Path path = null;
        if( versionNumber == null ) {
            path = Paths.get(applicationProperties.getVocabJsonPath() + notation + File.separator + notation + JSON_FORMAT);
        } else {
            path = Paths.get(applicationProperties.getVocabJsonPath() + notation + File.separator +
                versionNumber + File.separator + notation + "_" + versionNumber + JSON_FORMAT);
        }
        if( path == null )
            throw new VocabularyNotFoundException();
        return path;
    }

    @Override
    public Path getPublishedCvPath(String notation) {
        return getPublishedCvPath(notation, null);
    }

    @Override
    public void indexAllPublished() {
        log.info("INDEXING ALL PUBLISHED VOCABULARIES START");
        // remove any indexed vocabularies
        vocabularyPublishSearchRepository.deleteAll();
        // indexing
        List<Path> jsonPaths = getPublishedCvPaths();
        jsonPaths.forEach( this::indexPublished );
        log.info("INDEXING ALL PUBLISHED VOCABULARIES FINISHED");
    }

    @Override
    public List<Path> getPublishedCvPaths() {
        List<Path> jsonPaths = new ArrayList<>();
        try ( Stream<Path> paths = Files.walk(Paths.get(applicationProperties.getVocabJsonPath()), 2) ){
            jsonPaths = paths.filter(Files::isRegularFile).collect(Collectors.toList());
        } catch (IOException e) {
            log.error( e.getMessage() );
        }
        return jsonPaths;
    }

    @Override
    public void indexPublished(Path jsonPath) {
        log.info("Indexing published vocabulary with path {}", jsonPath);
        indexPublished(VocabularyUtils.generateVocabularyByPath(jsonPath));
    }

    @Override
    public void indexPublished(VocabularyDTO vocabulary) {
        log.info("Indexing published vocabulary with id {} and notation {}", vocabulary.getId(), vocabulary.getNotation());
        if( Boolean.TRUE.equals( vocabulary.isWithdrawn()) )
            return;
        // fill vocabulary with versions
        VocabularyDTO.fillVocabularyByVersions(vocabulary, vocabulary.getVersions());
        // fill CodeDTO object from versions
        vocabulary.setCodes( CodeDTO.generateCodesFromVersion(vocabulary.getVersions(), false));

        VocabularyPublish vocab = vocabularyPublishMapper.toEntity( vocabulary);
        vocabularyPublishSearchRepository.save( vocab );
    }

    @Override
    @SuppressWarnings("rawtypes")
    public EsQueryResultDetail search(EsQueryResultDetail esQueryResultDetail) {
        // get user keyword
        String searchTerm = esQueryResultDetail.getSearchTerm();
        String indiceType = "vocabularypublish";

        // determine which language fields include into query
        if( esQueryResultDetail.getSearchScope().equals( SearchScope.EDITORSEARCH) ) {
            indiceType = "vocabularyeditor";
        }
        // search on all fields
        List<String> languageFields = new ArrayList<>();
        if( !esQueryResultDetail.isSearchAllLanguages() ) {
            languageFields.add(StringUtils.capitalize(esQueryResultDetail.getSortLanguage()));
        } else {
            languageFields.addAll(Language.getCapitalizedIsos());
        }

        // build query builder
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
            .withIndices( indiceType ).withTypes( indiceType )
            .withSearchType(SearchType.DEFAULT)
            .withQuery(  generateMainAndNestedQuery ( searchTerm, languageFields ))
            .withFilter( generateFilterQuery( esQueryResultDetail.getEsFilters()) )
            .withPageable( esQueryResultDetail.getPage());

        // add highlighter after 2nd character typed
        boolean isSearchWithKeyword = searchTerm != null && !searchTerm.isEmpty() && searchTerm.length() > 2;
        if( isSearchWithKeyword )
            searchQueryBuilder.withHighlightFields( generateHighlightBuilderMain( languageFields ) );

        // add aggregation
        for(AbstractAggregationBuilder aggregation : generateAggregations( esQueryResultDetail ) )
            searchQueryBuilder.addAggregation(aggregation);

        // at the end build search query
        SearchQuery searchQuery = searchQueryBuilder.build();

        // put the vocabulary results
        Page<VocabularyDTO> vocabularyPage;
        if( esQueryResultDetail.getSearchScope().equals( SearchScope.EDITORSEARCH) )
            vocabularyPage = elasticsearchTemplate.queryForPage(searchQuery, VocabularyEditor.class).map(vocabularyEditorMapper::toDto);
        else
            vocabularyPage = elasticsearchTemplate.queryForPage(searchQuery, VocabularyPublish.class).map(vocabularyPublishMapper::toDto);

        // get search response for aggregation, hits, inner hits and highlighter
        SearchResponse searchResponse = elasticsearchTemplate.query(searchQuery, response -> response );

        // assign aggregation to esQueryResultDetail
        generateAggregationFilter(esQueryResultDetail, searchResponse);

        // update vocabulary based on highlight and inner hit
        if(isSearchWithKeyword)
            applySearchHitAndHighlight(vocabularyPage, searchResponse);
        else {
            // remove unnecessary nested code entities
            for( VocabularyDTO vocab : vocabularyPage.getContent())
                vocab.setCodes( Collections.emptySet() );
        }

        // set selected language in case language filter is selected with specific language
        setVocabularySelectedLanguage(esQueryResultDetail, vocabularyPage,
            esQueryResultDetail.getSearchScope().equals( SearchScope.EDITORSEARCH) ? EsFilter.LANGS_AGG: EsFilter.LANGS_PUB_AGG);

        esQueryResultDetail.setVocabularies(vocabularyPage);


        return esQueryResultDetail;
    }

    private void generateAggregationFilter(EsQueryResultDetail esQueryResultDetail, SearchResponse searchResponse) {
        // generate aggregation filter
        for( String field : esQueryResultDetail.getAggFields() ) {
            if( !esQueryResultDetail.isAnyFilterActive()) {
                buildNonFilteredAggregration(esQueryResultDetail, searchResponse, field);
            }
            else {
                buildFilteredAggregation(esQueryResultDetail, searchResponse, field);
            }
        }
    }

    private void buildNonFilteredAggregration(EsQueryResultDetail esQueryResultDetail, SearchResponse searchResponse, String field) {
        Terms aggregation = searchResponse.getAggregations().get( field + COUNT);

        if( aggregation == null)
            return;

        esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter -> {
            esFilter.clearBucket();
            for (Terms.Bucket b : aggregation.getBuckets())
                esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
        });
    }

    private void buildFilteredAggregation(EsQueryResultDetail esQueryResultDetail, SearchResponse searchResponse, String field) {
        Filters aggFilters= searchResponse.getAggregations().get( "aggregration_filter");
        if( aggFilters == null )
            return;

        esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter -> {
            esFilter.clearBucket();
            @SuppressWarnings("unchecked")
            Collection<Filters.Bucket> buckets = (Collection<Filters.Bucket>) aggFilters.getBuckets();
            for (Filters.Bucket bucket : buckets) {
                if (bucket.getDocCount() == 0)
                    continue;
                fillFilterBucket(esFilter, bucket.getAggregations().get(field + COUNT));
            }
        });
    }

    private void fillFilterBucket(EsFilter esFilter, Terms terms) {
        @SuppressWarnings("unchecked")
        Collection<Terms.Bucket> bkts = (Collection<Terms.Bucket>) terms.getBuckets();
        for (Terms.Bucket b : bkts) {

            if (b.getDocCount() == 0)
                continue;

            if( !EsFilter.isActiveFilter(esFilter, b.getKeyAsString()) )
                esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
            else
                esFilter.addFilteredBucket( b.getKeyAsString(), b.getDocCount() );
        }
    }

    private void applySearchHitAndHighlight(Page<VocabularyDTO> vocabularyPage, SearchResponse searchResponse) {
        for(SearchHit hit : searchResponse.getHits()) {
            Optional<VocabularyDTO> vocabOpt = VocabularyDTO.findByIdFromList(vocabularyPage.getContent(), hit.getId());
            if( vocabOpt.isPresent()) {
                VocabularyDTO cvHit = vocabOpt.get();

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
                highLightText.append(text.string()).append(" ");
            }
            java.lang.reflect.Field declaredField;
            try {
                declaredField = VocabularyDTO.class.getDeclaredField( fieldName );
                declaredField.setAccessible( true );
                declaredField.set(cvHit, highLightText.toString());

                setSelectedLanguageByHighlight( cvHit, fieldName );
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }


        }
    }

    private void applyCodeHighlighter(SearchHit hit, VocabularyDTO cvHit) {
        Set<CodeDTO> newCodes = new LinkedHashSet<>();

        for( Map.Entry<String, SearchHits> innerHitEntry : hit.getInnerHits().entrySet()) {

            for( SearchHit innerHit: innerHitEntry.getValue()) {

                if( innerHit.getHighlightFields().isEmpty() || cvHit.getCodes() == null || innerHit.getSourceAsMap().get("id") == null )
                    continue;

                highlightEachCode(cvHit, newCodes, innerHit);
            }
        }

        cvHit.setCodes(newCodes);
    }

    private void highlightEachCode(VocabularyDTO cvHit, Set<CodeDTO> newCodes, SearchHit innerHit) {
        Optional<CodeDTO> codeOpt = CodeDTO.findByIdFromList(cvHit.getCodes(), (int) innerHit.getSourceAsMap().get("id"));
        if( codeOpt.isPresent()) {
            CodeDTO codeHit = codeOpt.get();
            for (Map.Entry<String, HighlightField> entry : innerHit.getHighlightFields().entrySet()) {
                String fieldName = entry.getKey();
                HighlightField highlighField = entry.getValue();
                StringBuilder highLightText = new StringBuilder();
                for( Text text: highlighField.getFragments()) {
                    if( !fieldName.contains(TITLE) && !text.string().endsWith(".")) {
                        highLightText.append(text.string()).append(" ... ");
                    } else
                        highLightText.append( text.string() );
                }
                java.lang.reflect.Field declaredField;
                try {
                    declaredField = CodeDTO.class.getDeclaredField( fieldName.substring( CODE_PATH.length() + 1 ) );
                    declaredField.setAccessible( true );
                    declaredField.set(codeHit, highLightText.toString());

                    setSelectedLanguageByHighlight( cvHit, fieldName );
                } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                }
            }
            newCodes.add(codeHit);
        }
    }


    // set selected language based on highlight
    private void setSelectedLanguageByHighlight(VocabularyDTO cvHit, String highlightField) {
        // only set selected language once
        if( cvHit.getSelectedLang() == null ) {
            // get last language information from the field and get the Language enum
            String langIso = highlightField.substring(highlightField.length() - 2);
            cvHit.setSelectedLang(langIso.toLowerCase());
        }
    }


    public static QueryBuilder generateMainAndNestedQuery(String term, List<String> languageFields ) {
        if( term != null && !term.isEmpty() && term.length() > 1) {
            return QueryBuilders.boolQuery().should(generateMainQuery(term, languageFields)).should(generateNestedQuery(term, languageFields));
        }
        else
            return QueryBuilders.matchAllQuery();
    }

    public static QueryBuilder generateMainQuery(String term, List<String> languageFields ) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if( languageFields.size() == 1 ) {
            boolQuery
                .should( QueryBuilders.matchQuery( TITLE + languageFields.get(0), term).fuzziness(0.7).boost( 10.0f ))
                .should( QueryBuilders.matchQuery( DEFINITION + languageFields.get(0), term).fuzziness(0.7).boost( 4.0f ));
        } else {
            List<String> fields = new ArrayList<>();
            for(String langIso : languageFields) {
                fields.add(TITLE + langIso);
                fields.add(DEFINITION + langIso);
            }
            boolQuery.should(QueryBuilders.multiMatchQuery(term, fields.toArray(new String[0])));
        }
        return boolQuery;
    }

    public static QueryBuilder generateNestedQuery(String term, List<String> languageFields ) {
        // query for all languages
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if( languageFields.size() == 1 ) {
            boolQuery
                .should( QueryBuilders.matchQuery( CODE_PATH +"." + TITLE + languageFields.get(0), term).fuzziness(0.7).boost( 2.0f ))
                .should( QueryBuilders.matchQuery( CODE_PATH +"." + DEFINITION + languageFields.get(0), term).fuzziness(0.7).boost( 1.0f ));
        }
        else {
            List<String> fields = new ArrayList<>();
            for(String langIso : languageFields) {
                fields.add(CODE_PATH +"." + TITLE + langIso);
                fields.add(CODE_PATH +"." + DEFINITION + langIso);
            }
            boolQuery.should(QueryBuilders.multiMatchQuery(term, fields.toArray(new String[0])));
        }


        if( term.length() > 2)
            return QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.Total)
                .innerHit( new InnerHitBuilder( CODE_PATH )
                    .setHighlightBuilder( nestedHighlightBuilder( languageFields ) ));
        return QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.Total)
            .innerHit( new InnerHitBuilder( CODE_PATH ) );
    }

    public static HighlightBuilder.Field[] generateHighlightBuilderMain(List<String> languageFields ) {
        List<HighlightBuilder.Field> fields = new ArrayList<>();
        // all language fields
        for(String langIso : languageFields ) {
            fields.add( new HighlightBuilder.Field( TITLE + langIso ).preTags(HIGHLIGHT_START).postTags(HIGHLIGHT_END));
            fields.add( new HighlightBuilder.Field( DEFINITION + langIso ).preTags(HIGHLIGHT_START).postTags(HIGHLIGHT_END) );
        }
        return fields.toArray(new HighlightBuilder.Field[0]);
    }

    private static HighlightBuilder nestedHighlightBuilder(List<String> languageFields ) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // all language fields
        for(String langIso : languageFields) {
            highlightBuilder.field( CODE_PATH +"." + TITLE + langIso ).preTags(HIGHLIGHT_START).postTags(HIGHLIGHT_END);
            highlightBuilder.field( CODE_PATH +"." + DEFINITION + langIso ).preTags(HIGHLIGHT_START).postTags(HIGHLIGHT_END);
        }
        return highlightBuilder;
    }

    public static QueryBuilder generateFilterQuery(List<EsFilter> esFilters ) {
        BoolQueryBuilder boolQueryFilter = QueryBuilders.boolQuery();

        for( EsFilter esFilter : esFilters ) {
            if( esFilter.getValues() != null && !esFilter.getValues().isEmpty()) {
                if( esFilter.getValues().size() == 1) { // use AND if an item in the filter is selected
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
    public static List<AbstractAggregationBuilder> generateAggregations(EsQueryResultDetail esQueryResultDetail ){

        List<AbstractAggregationBuilder> aggBuilders = new ArrayList<>();

        if( !esQueryResultDetail.isAnyFilterActive() ) {
            for(String aggField: esQueryResultDetail.getAggFields())
                aggBuilders.add( AggregationBuilders.terms( aggField + COUNT).field( aggField).size(SIZE_OF_ITEMS_ON_AGGREGATION));
        }
        else {
            FiltersAggregationBuilder filtersAggregation = AggregationBuilders.filters( "aggregration_filter", generateFilterQuery( esQueryResultDetail.getEsFilters() )) ;

            for(String aggField: esQueryResultDetail.getAggFields() )
                filtersAggregation.subAggregation( AggregationBuilders.terms( aggField + COUNT).field( aggField ).size(SIZE_OF_ITEMS_ON_AGGREGATION) );

            aggBuilders.add(filtersAggregation);
        }
        return aggBuilders;
    }

    @Override
    public void generateJsonAllVocabularyPublish() {
        // remove all static JSON files on vocabulary (delete entire and including vocabulary directory)
        deleteCvJsonDirectoryAndContent(applicationProperties.getVocabJsonPath());

        List<VocabularyDTO> vocabularyDTOS = findAll();
        generateJsonVocabularyPublish( vocabularyDTOS.toArray(new VocabularyDTO[0]) );
    }

    @Override
    public void generateJsonVocabularyPublish( VocabularyDTO... vocabularies ) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // getAgencyAndLicense
        Map<Long, Agency> agencyMap = agencyRepository.findAll().stream().collect( Collectors.toMap( Agency::getId, Function.identity() ));
        Map<Long, Licence> licenceMap = licenceRepository.findAll().stream().collect( Collectors.toMap( Licence::getId, Function.identity() ));

        for (VocabularyDTO vocabulary : vocabularies) {
            // get all published versions (sorted by SL and number)
            List<VersionDTO> versions = versionService.findAllPublishedByVocabulary(vocabulary.getId());
            // skip vocabulary without published version
            if( versions.isEmpty() )
                continue;
            // remove unused attributes, and add the required attribute for vocabulary
            updateVocabularyContentForJsonfy(agencyMap, vocabulary);
            // put into map based on versionSL, the SL need to be listed in beginning (sorted first)
            Map<String, List<VersionDTO>> slNumberVersionsMap = new LinkedHashMap<>();
            // add special Map for all latest version across SL version
            // just remember that the versions are always already sorted from latest to oldest
            List<VersionDTO> latestVersionsAcrossSl = new ArrayList<>();
            Set<String> latestVersionsLangs = new HashSet<>();
            slNumberVersionsMap.put( LATEST, latestVersionsAcrossSl);
            for (VersionDTO version : versions) {
                prepareVersionAndConceptForJsonfy(licenceMap, vocabulary, slNumberVersionsMap, version, latestVersionsAcrossSl, latestVersionsLangs);
            }
            generateJsonFile(mapper, vocabulary, slNumberVersionsMap);
        }
    }

    private void prepareVersionAndConceptForJsonfy(Map<Long, Licence> licenceMap, VocabularyDTO vocabulary,
                                                   Map<String, List<VersionDTO>> slNumberVersionsMap, VersionDTO version,
                                                   List<VersionDTO> latestVersionsAcrossSl, Set<String> latestVersionsLangs) {
        // get concepts and sorted by position
        List<ConceptDTO> concepts = conceptService.findByVersion(version.getId());
        // remove unused attributes, and add the required attribute for version
        updateVersionContentForJsonfy(licenceMap, version);
        // remove unused attributes, and add the required attribute for concept
        updateConceptContentForJsonfy(concepts);

        version.setConcepts( new LinkedHashSet<>(concepts));
        if( version.getItemType().equals(ItemType.SL.toString()) ) {
            List<VersionDTO> versionDTOs = slNumberVersionsMap.computeIfAbsent(version.getNumber(), k -> new ArrayList<>());
            // check for version history
            addVersionHistories(vocabulary, version);

            versionDTOs.add( version );
        } else {
            // find SL number
            int lasIndexDot = version.getNumber().lastIndexOf('.');
            if( lasIndexDot == -1)
                throw new IllegalArgumentException( "Incorrect TL version number of " + version.getNotation() +
                    " " + version.getNumber() );
            String slNumber = version.getNumber().substring(0, lasIndexDot);

            List<VersionDTO> versionDTOs = slNumberVersionsMap.get(slNumber);
            if( versionDTOs == null )
                throw new IllegalArgumentException( "SL version missing from version number of " + version.getNotation() +
                    " " + version.getNumber() );

            // check for version history
            addVersionHistories(vocabulary, version);

            versionDTOs.add( version );
        }
        // collect latest version accross SL
        if( !latestVersionsLangs.contains( version.getLanguage()) || version.getNumber().startsWith( vocabulary.getVersionNumber() )) {
            latestVersionsAcrossSl.add( version );
            latestVersionsLangs.add( version.getLanguage() );
        }
    }

    private void updateConceptContentForJsonfy(List<ConceptDTO> concepts) {
        for (ConceptDTO concept : concepts) {
            if( concept.getDefinition() != null )
                concept.setDefinition( concept.getDefinition().trim());
            concept.setSlConcept(null);
            concept.setVersionId( null );
        }
    }

    private void updateVersionContentForJsonfy(Map<Long, Licence> licenceMap, VersionDTO version) {
        Licence licence = licenceMap.get(version.getLicenseId());
        if( licence != null ){
            version.setLicense( licence.getAbbr() );
            version.setLicenseLink( licence.getLink() );
            version.setLicenseName( licence.getName() );
            version.setLicenseLogo( licence.getLogoLink() );
        }
        version.setNotes( version.getNotes() == null ? "": version.getNotes().trim());
        version.setCreator(null);
        version.setDiscussionNotes(null);
        version.setVocabularyId(null);
        version.setLicenseId( null );
    }

    private void updateVocabularyContentForJsonfy(Map<Long, Agency> agencyMap, VocabularyDTO vocabulary) {
        // clear vocabulary content (title, definition), since it is not needed
        vocabulary.clearContent();
        //update data remove unused and add more agency information
        vocabulary.setDiscoverable( null );
        vocabulary.setArchived( null );
        vocabulary.setCodes( null );
        Agency agency = agencyMap.get(vocabulary.getAgencyId());
        if( agency != null) {
            vocabulary.setAgencyLink( agency.getLink());
        }
    }

    private void addVersionHistories(VocabularyDTO vocabulary, VersionDTO version) {
        List<VersionDTO> olderVersions = versionService.findOlderPublishedByVocabularyLanguageId(vocabulary.getId(), version.getLanguage(), version.getId());
        List<Map<String,Object>> olderVersionHistories = new ArrayList<>();
        for (VersionDTO olderVersion : olderVersions) {
            Map<String, Object> versionHistoryMap = new LinkedHashMap<>();
            versionHistoryMap.put("id" , olderVersion.getId() );
            versionHistoryMap.put("version" , olderVersion.getNumber() );
            versionHistoryMap.put("date" , olderVersion.getPublicationDate().toString() );
            versionHistoryMap.put("note" , olderVersion.getVersionNotes());
            versionHistoryMap.put("changes", olderVersion.getVersionChanges());
            if ( olderVersion.getPreviousVersion() != null && !olderVersion.getPreviousVersion().equals(olderVersion.getId()))
                versionHistoryMap.put("prevVersion", olderVersion.getPreviousVersion());
            olderVersionHistories.add(versionHistoryMap);
        }
        version.setVersionHistories(olderVersionHistories);
    }

    private void generateJsonFile(ObjectMapper mapper, VocabularyDTO vocabulary, Map<String, List<VersionDTO>> slNumberVersionsMap) {
        for (Map.Entry<String, List<VersionDTO>> entry : slNumberVersionsMap.entrySet()) {
            String entryKey = entry.getKey();
            vocabulary.setVersions( new LinkedHashSet<>( entry.getValue()) );

            String jsonString;
            try {
                jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vocabulary);
                writeToFile(vocabulary.getNotation(), entryKey, jsonString, entryKey.equals(LATEST));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void writeToFile(String notation, String versionNumber, String content, boolean isAllLatestVersion) {
        String path = applicationProperties.getVocabJsonPath() + notation + File.separator + versionNumber;
        if ( isAllLatestVersion ) {
            path = applicationProperties.getVocabJsonPath() + notation;
        }
        File dirPath = new File(path);
        if( !dirPath.isDirectory() ) {
            dirPath.mkdirs();
        }
        File file;
        if( isAllLatestVersion ) {
            file = new File(path + File.separator + notation + JSON_FORMAT);
        } else {
            file = new File(path + File.separator + notation + "_" + versionNumber + JSON_FORMAT);
        }
        try(
            BufferedWriter bw = new BufferedWriter(new FileWriter(file))
        ){
            bw.write(content);
            log.info("Written to Temp file : {} ", file.getAbsolutePath());
        } catch (IOException e)
        {
            log.error(e.getMessage());
        }
    }

    @Override
    public File generateVocabularyPublishFileDownload(
        String vocabularyNotation, String versionSl, String versionList, ExportService.DownloadType downloadType, HttpServletRequest request) {
        log.info( "Publication generate file {} for Vocabulary {} versionSl {}", downloadType, vocabularyNotation, versionSl );
        VocabularyDTO vocabularyDTO = VocabularyUtils.generateVocabularyByPath( getPublishedCvPath( vocabularyNotation,versionSl ) );
        return generateVocabularyFileDownload(vocabularyNotation, versionSl, versionList, downloadType, request, vocabularyDTO);
    }

    @Override
    public File generateVocabularyEditorFileDownload(
        String vocabularyNotation, String versionSl, String versionList, ExportService.DownloadType downloadType, HttpServletRequest request) {
        log.info( "Editor generate file {0} for Vocabulary {1} versionSl {2}", downloadType, vocabularyNotation, versionSl );
        VocabularyDTO vocabularyDTO = getWithVersionsByNotationAndVersion(vocabularyNotation, versionSl);
        return generateVocabularyFileDownload(vocabularyNotation, versionSl, versionList, downloadType, request, vocabularyDTO);
    }

    @Override
    public String performTlMigrationNormalizationChecking() {
        return performTlMigrationNormalization( true, "_all");
    }

    @Override
    public String performTlMigrationNormalization(boolean isChecking, String notations) {
        StringBuilder results = new StringBuilder();
        List<VocabularyDTO> vocabularies = new ArrayList<>();
        if(notations.equals( "_all") ) {
            vocabularies = findAll();
            results.append( "Perform concepts TL normalization for entire CVs with _all param" );
        } else {
            for (String s : notations.split(",")) {
                VocabularyDTO cv = getByNotation(s.trim());
                if( cv == null ) {
                    results.append( "Error: Unable to find vocabulary with notation " + s.trim() );
                } else {
                    vocabularies.add( cv );
                }
            }
        }
        return performTlMigrationNormalization( isChecking, vocabularies.stream().toArray(VocabularyDTO[]::new));
    }

    @Override
    public String performTlMigrationNormalization(boolean isChecking, VocabularyDTO... vocabularyDTOs) {
        final String vocabulariesChecked = Arrays.stream(vocabularyDTOs).map(VocabularyDTO::getNotation).collect(Collectors.joining(", "));
        log.info( "Perform concept TLs normalization on {}",  vocabulariesChecked);
        StringBuilder results = new StringBuilder();
        results.append( "Perform concepts TL normalization in mode " + (isChecking ? "CHECKING": "WRITE" ) + " for the following Vocabularies:\n" );
        for (VocabularyDTO vocabularyDTO : vocabularyDTOs) {

            final List<VersionDTO> slVersions = vocabularyDTO.getVersions().stream()
                .filter(v -> v.getItemType().equals(ItemType.SL.toString()))
                .sorted(VocabularyUtils.versionDtoComparator()).collect(Collectors.toList());

            results.append("\nCV " + vocabularyDTO.getNotation() + " latest version: " + slVersions.get(0).getNumber() + "\n");

            for (int i = 0; i < slVersions.size(); i++) {
                VersionDTO slVersion = slVersions.get(i);
                results.append("Examining TLs from SL " + slVersion.getLanguage() + "-" + slVersion.getNumber() + "-" + slVersion.getStatus() + "\n");
                final List<VersionDTO> tlVersionGroup = vocabularyDTO.getVersions().stream()
                    .filter(v -> v.getNumber().startsWith(slVersion.getNumber()) && v.getItemType().equals(ItemType.TL.toString())).collect(Collectors.toList());

                List<VersionDTO> tlVersionGroupPrev = null;
                if( (i + 1 ) < slVersions.size() ) {
                    int finalI = i;
                    tlVersionGroupPrev = vocabularyDTO.getVersions().stream()
                        .filter(v -> v.getNumber().startsWith(slVersions.get(finalI + 1).getNumber()) && v.getItemType().equals(ItemType.TL.toString())).collect(Collectors.toList());
                }

                results.append( tlNormalization(isChecking, slVersion, tlVersionGroup, tlVersionGroupPrev));
            }
        }
        return results.toString();
    }

    private String tlNormalization(boolean isChecking, VersionDTO slVersion, List<VersionDTO> tlVersionGroup, List<VersionDTO> tlVersionGroupPrev) {
        StringBuilder results = new StringBuilder();
        for (VersionDTO tlVersion : tlVersionGroup) {
            results.append("Examining SL " + slVersion.getLanguage() + "-" + slVersion.getNumber() + "-" + slVersion.getStatus() +
                " with TL " + tlVersion.getLanguage() + "-" + tlVersion.getNumber() + "-" + tlVersion.getStatus() + "\n");
            final Set<ConceptDTO> tlConcepts = tlVersion.getConcepts();
            for (ConceptDTO slConcept : slVersion.getConcepts()) {
                ConceptDTO tlConcept = tlConcepts.stream().filter(c -> c.getNotation().equals(slConcept.getNotation())).findFirst().orElse(null);
                if( tlConcept != null ) {
                    checkExistingTlCodeWithSl(isChecking, results, slConcept, tlConcept);
                    tlConcepts.remove(tlConcept);
                } else {
                    checkMissingTlCode(isChecking, tlVersionGroupPrev, results, tlVersion, slConcept);
                }
            }
            // print remaining TL set if any and remove
            for (ConceptDTO tlConcept : tlConcepts) {
                results.append( "Problem: Concept TL is redundant with SL " + tlConcept.toString() + "\n" );
            }
        }
        return results.toString();
    }

    private void checkMissingTlCode(boolean isChecking, List<VersionDTO> tlVersionGroupPrev, StringBuilder results, VersionDTO tlVersion, ConceptDTO slConcept) {
        results.append( "Problem: Concept TL is missing with notation " + slConcept.getNotation() + "\n" );
        // find the prev concept if exist
        ConceptDTO prevConcept = null;
        if( tlVersionGroupPrev != null && !tlVersionGroupPrev.isEmpty()) {
            VersionDTO versionTlPrev = tlVersionGroupPrev.stream().filter(v -> v.getLanguage().equals( tlVersion.getLanguage() )).findFirst().orElse(null);
            if( versionTlPrev != null ) {
                prevConcept = versionTlPrev.getConcepts().stream().filter(c -> c.getNotation().equals(slConcept.getNotation())).findFirst().orElse(null);
            }
        }
        if( !isChecking) {
            createMissingTlCode(results, tlVersion, slConcept, prevConcept);
        }
    }

    private void createMissingTlCode(StringBuilder results, VersionDTO tlVersion, ConceptDTO slConcept, ConceptDTO prevConcept) {
        ConceptDTO tlConcept;
        tlConcept = new ConceptDTO();
        tlConcept.setNotation( slConcept.getNotation());
        tlConcept.setParent( slConcept.getParent() );
        tlConcept.setPosition( slConcept.getPosition());
        tlConcept.setSlConcept( slConcept.getId());
        tlConcept.setVersionId( tlVersion.getId() );
        if( tlVersion.getUri() != null ) {
            final int index = tlVersion.getUri().lastIndexOf("/" + tlVersion.getLanguage() + "/");
            if(index > 0 ) {
                tlConcept.setUri( tlVersion.getUri().substring(0, index) + "#" +
                    tlConcept.getNotation() + tlVersion.getUri().substring(index));
            }
        }
        if( prevConcept != null ) {
            results.append( "Previous concept FOUND " + prevConcept.toString() + "\n" );
            tlConcept.setTitle( prevConcept.getTitle());
            tlConcept.setDefinition( prevConcept.getDefinition() );
            tlConcept.setPreviousConcept( prevConcept.getId() );
        } else {
            results.append( "Previous concept NOT-FOUND\n" );
            if( tlVersion.getStatus().equals(Status.PUBLISHED.toString())) {
                tlConcept.setTitle( slConcept.getTitle());
                tlConcept.setDefinition( slConcept.getDefinition() );
            }
        }
        tlConcept = conceptService.save(tlConcept);
        results.append( "Solution: Save a new TL concept with details " + tlConcept.toString() + " \n" );
    }

    private void checkExistingTlCodeWithSl(boolean isChecking, StringBuilder results, ConceptDTO slConcept, ConceptDTO tlConcept) {
        if( (slConcept.getParent() != null && tlConcept.getParent() == null) ||
            (slConcept.getParent() == null && tlConcept.getParent() != null) ||
            (slConcept.getParent() != null && tlConcept.getParent() != null && !tlConcept.getParent().equals(slConcept.getParent())) ||
            !tlConcept.getPosition().equals(slConcept.getPosition())) {
            results.append( "Problem: Un-match SL and TL concept parent and/or position with notation " + slConcept.getNotation() +
                " SL id, parent and notation " + slConcept.getId() + " - " + slConcept.getParent() + " - " + slConcept.getPosition() +
                 " TL id, parent and notation " + tlConcept.getId() + " - " + tlConcept.getParent() + " - " + tlConcept.getPosition() + "\n"
                );
            if( !isChecking) {
                tlConcept.setParent( slConcept.getParent());
                tlConcept.setPosition( slConcept.getPosition() );
                conceptService.save(tlConcept);
                results.append( "Solution: Save TL concept with parent and position based on SL concept \n" );
            }
        }
    }

    private File generateVocabularyFileDownload(String vocabularyNotation, String versionSl, String versionList, ExportService.DownloadType downloadType, HttpServletRequest request, VocabularyDTO vocabularyDTO) {
        Set<VersionDTO> includedVersions = filterOutVocabularyVersions(versionList, vocabularyDTO);
        vocabularyDTO.setVersions( includedVersions );
        Map<String, Object> map = new HashMap<>();
        // escaping HTML to strict XHTML
        for (VersionDTO includedVersion : includedVersions) {
            includedVersion.setVersionNotes( toStrictXhtml(includedVersion.getVersionNotes()));
            includedVersion.setVersionChanges( toStrictXhtml(includedVersion.getVersionChanges()));
            includedVersion.setDdiUsage( toStrictXhtml(includedVersion.getDdiUsage()));
        }

        // sorted versions
        map.put("versions", includedVersions);

        // agency object
        AgencyDTO agencyDTO = new AgencyDTO();
        agencyDTO.setName( vocabularyDTO.getAgencyName() );
        agencyDTO.setLink( vocabularyDTO.getAgencyLink() );

        if( downloadType.equals( ExportService.DownloadType.SKOS ))
        {
            VocabularyUtils.setSkosMapAttribute(map, vocabularyDTO, includedVersions.iterator().next());
            map.put(CODE_PATH, CodeDTO.generateCodesFromVersion(includedVersions, false));
        }
        else {
            vocabularyDTO.setVersions(includedVersions);
            prepareAdditionalAttributesForNonSkos(vocabularyDTO, map, agencyDTO);
        }

        map.put("agency", agencyDTO);
        map.put("year", vocabularyDTO.getPublicationDate().getYear());
        map.put("baseUrl", getURLWithContextPath( request ));

        try {
            return exportService.generateFileByThymeleafTemplate(vocabularyNotation + "-" + versionSl + "_" + versionList, "export", map, downloadType);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Set<VersionDTO> filterOutVocabularyVersions(String versionList, VocabularyDTO vocabularyDTO) {
        // filter out version
        Set<VersionDTO> includedVersions = new LinkedHashSet<>();
        if( versionList != null){
            String[] versionSplits = versionList.split("_");
            for (String vs : versionSplits) {
                String[] s = vs.split("-");
                if( s.length != 2 )
                    continue;
                Optional<VersionDTO> versionDTOOpt = vocabularyDTO.getVersions().stream().filter(v -> v.getLanguage().equals(s[0]) && v.getNumber().equals(s[1])).findFirst();
                versionDTOOpt.ifPresent(includedVersions::add);
            }
        } else {
            includedVersions = vocabularyDTO.getVersions();
        }
        return includedVersions;
    }

    private String toStrictXhtml(String text) {
        if( text == null )
            return null;
        final Document document = Jsoup.parse(text);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }


    private void prepareAdditionalAttributesForNonSkos(VocabularyDTO vocabularyDTO, Map<String, Object> map, AgencyDTO agencyDTO) {
        if( vocabularyDTO.getAgencyLogo() != null ) {
            File logoFile = new File(applicationProperties.getStaticFilePath() + File.separator + "content" +
                File.separator + "images" + File.separator + "agency" + File.separator + vocabularyDTO.getAgencyLogo() );
            String data = null;
            try {
                data = DatatypeConverter.printBase64Binary(Files.readAllBytes(logoFile.toPath()));
            } catch (IOException e) {
                log.error( e.getMessage() );
            }
            agencyDTO.setLogo( "data:image/png;base64," + data );
        }
        if( !vocabularyDTO.getVersions().isEmpty() ) {
            vocabularyDTO.getVersions().forEach(version -> {
                if( version.getLicenseLogo() == null)
                    return;
                File versionLogo = new File(applicationProperties.getLicenseImagePath() + version.getLicenseLogo() );
                String versionBase64LogoData = null;
                try {
                    versionBase64LogoData = DatatypeConverter.printBase64Binary(Files.readAllBytes(versionLogo.toPath()));
                } catch (IOException e) {
                    log.error( e.getMessage() );
                }
                version.setLicenseLogo( "data:image/png;base64," + versionBase64LogoData );
            });

            VersionDTO version = vocabularyDTO.getVersions().iterator().next();
            if( version.getCanonicalUri() != null ) {
                int index = version.getCanonicalUri().lastIndexOf(':');
                map.put("cvUrn", version.getCanonicalUri().substring(0, index));
            }
        }
    }

    private String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private void setVocabularySelectedLanguage(EsQueryResultDetail esQueryResultDetail,
                                               Page<VocabularyDTO> vocabularyPage, String fieldType) {
        if( esQueryResultDetail.isAnyFilterActive() ) {
            esQueryResultDetail.getEsFilterByField(fieldType).ifPresent( langFilter -> {
                if( langFilter.getValues().size() == 1 ) {
                    for( VocabularyDTO vocab : vocabularyPage.getContent()){
                        vocab.setSelectedLang(langFilter.getValues().get(0));
                    }
                }
            });
        } else {
            setSelectedLangToSourceLang(vocabularyPage);
        }
    }

    private void setSelectedLangToSourceLang(Page<VocabularyDTO> vocabularyPage) {
        for (VocabularyDTO vocab : vocabularyPage.getContent()) {
            if( vocab.getSelectedLang() == null )
                vocab.setSelectedLang(vocab.getSourceLanguage());
        }
    }
}
