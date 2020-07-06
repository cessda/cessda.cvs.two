package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.*;
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/api")
public class EditorResource {

    private final Logger log = LoggerFactory.getLogger(EditorResource.class);

    private static final String ENTITY_VOCABULARY_NAME = "vocabulary";
    private static final String ENTITY_VERSION_NAME = "version";
    private static final String ENTITY_CODE_NAME = "code";
    private static final String ENTITY_COMMENT_NAME = "comment";
    private static final String ENTITY_METADATAVALUE_NAME = "metadataValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyService vocabularyService;

    private final VersionService versionService;

    private final ConceptService conceptService;

    private final LicenceService licenceService;

    private final AgencyService agencyService;

    private final CommentService commentService;

    private final MetadataFieldService metadataFieldService;

    private final MetadataValueService metadataValueService;

    public EditorResource(VocabularyService vocabularyService, VersionService versionService, ConceptService conceptService,
                          LicenceService licenceService, AgencyService agencyService, CommentService commentService, MetadataFieldService metadataFieldService, MetadataValueService metadataValueService) {
        this.vocabularyService = vocabularyService;
        this.versionService = versionService;
        this.conceptService = conceptService;
        this.licenceService = licenceService;
        this.agencyService = agencyService;
        this.commentService = commentService;
        this.metadataFieldService = metadataFieldService;
        this.metadataValueService = metadataValueService;
    }

    /**
     * {@code POST  /editors/vocabularies} : Create a new vocabulary via editor Rest API.
     *
     * @param vocabularySnippet the vocabularyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vocabularyDTO, or with status {@code 400 (Bad Request)} if the vocabulary has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws VocabularyAlreadyExistException {@code 400 (Bad Request)} if the notation is already exist/Vocabulary exist.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PostMapping("/editors/vocabularies")
    public ResponseEntity<VocabularyDTO> createVocabulary(@Valid @RequestBody VocabularySnippet vocabularySnippet) throws URISyntaxException {
        log.debug("REST request to save Vocabulary : {}", vocabularySnippet);
        // check if user authorized to add VocabularyResource
        if( vocabularySnippet.getActionType().equals( ActionType.CREATE_CV )) {
            SecurityUtils.checkResourceAuthorization(ActionType.CREATE_CV, vocabularySnippet.getAgencyId(),
                ActionType.CREATE_CV.getAgencyRoles(), vocabularySnippet.getLanguage());

            if (vocabularySnippet.getVocabularyId() != null) {
                throw new BadRequestAlertException("A new vocabulary cannot already have an ID", ENTITY_VOCABULARY_NAME, "idexists");
            }
        }
        else if( vocabularySnippet.getActionType().equals( ActionType.ADD_TL_CV )) {
            SecurityUtils.checkResourceAuthorization(ActionType.ADD_TL_CV, vocabularySnippet.getAgencyId(),
                ActionType.ADD_TL_CV.getAgencyRoles(), vocabularySnippet.getLanguage());
        } else {
            throw new IllegalArgumentException( "Illegal action type for POST" + vocabularySnippet.getActionType() );
        }

        VocabularyDTO result = vocabularyService.saveVocabulary( vocabularySnippet );
        return ResponseEntity.created(new URI("/api/vocabularies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_VOCABULARY_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code POST  /editors/vocabularies/new-version/{id}} : Create a new vocabulary version from existing version Rest API.
     *
     * @param id the ID of version to be cloned/add new version
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/editors/vocabularies/new-version/{id}")
    public ResponseEntity<VersionDTO> createNewVocabularyVersion(@PathVariable Long id){
        log.debug("REST request to create new Vocabulary with version ID: {}", id);
        VersionDTO result = vocabularyService.createNewVersion( id );
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_VERSION_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code PUT  /editors/vocabularies} : Updates an existing vocabulary.
     *
     * @param vocabularySnippet the vocabularyDTO to update.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vocabularyDTO,
     * or with status {@code 400 (Bad Request)} if the vocabularyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vocabularyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PutMapping("/editors/vocabularies")
    public ResponseEntity<VocabularyDTO> updateVocabulary(@Valid @RequestBody VocabularySnippet vocabularySnippet) throws URISyntaxException {
        log.debug("REST request to update Vocabulary : {}", vocabularySnippet);
        if (vocabularySnippet.getVocabularyId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_VOCABULARY_NAME, "idnull");
        }
        VocabularyDTO result = vocabularyService.saveVocabulary( vocabularySnippet );
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_VOCABULARY_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code PUT  /editors/vocabularies/forward-status} : Forward the status of Vocabulary/Version .
     *
     * @param vocabularySnippet the vocabularyDTO snippet to update.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vocabularyDTO,
     * or with status {@code 400 (Bad Request)} if the vocabularyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vocabularyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PutMapping("/editors/vocabularies/forward-status")
    public ResponseEntity<VersionDTO> forwardStatusVocabulary(@Valid @RequestBody VocabularySnippet vocabularySnippet) throws URISyntaxException {
        log.debug("REST request to update Vocabulary : {}", vocabularySnippet);
        if (vocabularySnippet.getVersionId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_VOCABULARY_NAME, "idnull");
        }
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(vocabularySnippet.getVocabularyId())
            .orElseThrow( () -> new EntityNotFoundException("Unable to find vocabulary with Id " + vocabularySnippet.getVocabularyId() ));
        // pick version from vocabularyDTO
        VersionDTO versionDTO = vocabularyDTO.getVersions().stream().filter(v -> v.getId().equals( vocabularySnippet.getVersionId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + vocabularySnippet.getVersionId()  ));

        LicenceDTO licenceDTO = null;
        AgencyDTO agencyDTO = null;
        if( vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_SL_STATUS_PUBLISHED) ||
            vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_TL_STATUS_PUBLISHED)) {
            licenceDTO = licenceService.findOne(vocabularySnippet.getLicenseId())
                .orElseThrow(() -> new EntityNotFoundException("Unable to find license with Id " + vocabularySnippet.getLicenseId()));
            agencyDTO = agencyService.findOne(vocabularySnippet.getAgencyId())
                .orElseThrow(() -> new EntityNotFoundException("Unable to find agency with Id " + vocabularySnippet.getAgencyId()));
        }
        // check authorization
        switch ( vocabularySnippet.getActionType() ){
            case FORWARD_CV_SL_STATUS_INITIAL_REVIEW:
                SecurityUtils.checkResourceAuthorization(ActionType.FORWARD_CV_SL_STATUS_INITIAL_REVIEW,
                    vocabularySnippet.getAgencyId(), ActionType.FORWARD_CV_SL_STATUS_INITIAL_REVIEW.getAgencyRoles(), vocabularySnippet.getLanguage());
                versionDTO.setStatus(Status.INITIAL_REVIEW.toString());
                vocabularyDTO.setStatus(Status.INITIAL_REVIEW.toString());
                break;
            case FORWARD_CV_SL_STATUS_FINAL_REVIEW:
                SecurityUtils.checkResourceAuthorization(ActionType.FORWARD_CV_SL_STATUS_FINAL_REVIEW,
                    vocabularySnippet.getAgencyId(), ActionType.FORWARD_CV_SL_STATUS_FINAL_REVIEW.getAgencyRoles(), vocabularySnippet.getLanguage());
                versionDTO.setStatus(Status.FINAL_REVIEW.toString());
                vocabularyDTO.setStatus(Status.FINAL_REVIEW.toString());
                break;
            case FORWARD_CV_SL_STATUS_PUBLISHED:
                SecurityUtils.checkResourceAuthorization(ActionType.FORWARD_CV_SL_STATUS_PUBLISHED,
                    vocabularySnippet.getAgencyId(), ActionType.FORWARD_CV_SL_STATUS_PUBLISHED.getAgencyRoles(), vocabularySnippet.getLanguage());

                versionDTO.setStatus(Status.PUBLISHED.toString());
                versionDTO.prepareSlPublishing(vocabularySnippet, licenceDTO, agencyDTO);
                vocabularyDTO.prepareSlPublishing(versionDTO);
                break;
            case FORWARD_CV_TL_STATUS_INITIAL_REVIEW:
                SecurityUtils.checkResourceAuthorization(ActionType.FORWARD_CV_TL_STATUS_INITIAL_REVIEW,
                    vocabularySnippet.getAgencyId(), ActionType.FORWARD_CV_TL_STATUS_INITIAL_REVIEW.getAgencyRoles(), vocabularySnippet.getLanguage());
                versionDTO.setStatus(Status.INITIAL_REVIEW.toString());
                break;
            case FORWARD_CV_TL_STATUS_FINAL_REVIEW:
                SecurityUtils.checkResourceAuthorization(ActionType.FORWARD_CV_TL_STATUS_FINAL_REVIEW,
                    vocabularySnippet.getAgencyId(), ActionType.FORWARD_CV_TL_STATUS_FINAL_REVIEW.getAgencyRoles(), vocabularySnippet.getLanguage());
                versionDTO.setStatus(Status.FINAL_REVIEW.toString());
                break;
            case FORWARD_CV_TL_STATUS_PUBLISHED:
                SecurityUtils.checkResourceAuthorization(ActionType.FORWARD_CV_TL_STATUS_PUBLISHED,
                    vocabularySnippet.getAgencyId(), ActionType.FORWARD_CV_TL_STATUS_PUBLISHED.getAgencyRoles(), vocabularySnippet.getLanguage());

                versionDTO.setStatus(Status.PUBLISHED.toString());
                versionDTO.prepareTlPublishing(vocabularySnippet, licenceDTO, agencyDTO);

                break;
            default:
                throw new IllegalArgumentException( "Action type not supported" + vocabularySnippet.getActionType() );
        }
        // save at the end
        vocabularyDTO = vocabularyService.save( vocabularyDTO );
        // check if SL published and not initial version, is there any TL needs to be cloned as DRAFT?
        if( vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_SL_STATUS_PUBLISHED) && !versionDTO.isInitialVersion()) {
            vocabularyDTO = cloneTLsIfAny(vocabularyDTO, versionDTO);
        }
        // indexing editor
        vocabularyService.indexEditor(vocabularyDTO );
        // indexing publication
        if ( versionDTO.getStatus().equals( Status.PUBLISHED.toString()) ) {
            vocabularyService.indexPublished(vocabularyDTO);
        }
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_VERSION_NAME, versionDTO.getNotation()))
            .body(versionDTO);
    }

    private VocabularyDTO cloneTLsIfAny(VocabularyDTO vocabularyDTO, VersionDTO versionDTO) {
        // find previous SL version, check if there is TLs
        Optional<VersionDTO> prevVersionSlOpt = versionService.findOne(versionDTO.getPreviousVersion());
        if ( prevVersionSlOpt.isPresent() ) {
            VersionDTO prevVersionSl = prevVersionSlOpt.get();
            List<VersionDTO> clonedTls = new ArrayList<>();
            VocabularyDTO prevVocabDTO = vocabularyService.getWithVersionsByNotationAndVersion(vocabularyDTO.getNotation(), prevVersionSl.getNumber());
            prevVocabDTO.getVersions().forEach(prevVersion -> {
                if( prevVersion.getItemType().equals(ItemType.SL.toString())) {
                    return;
                }
                log.info( "Clone {0} TL {1} version {2} to version {3}_DRAFT", versionDTO.getNotation(),
                    prevVersion.getLanguage(), prevVersion.getNumber() + "_" + prevVersion.getStatus(), versionDTO.getNumber() + ".1" );
                // cloning need currentSL (as main reference for notation and position), previousSl (as secondary reference if notation is changed in the current SL),
                // and previous TL for the rest of properties
                clonedTls.add(vocabularyService.cloneTl( versionDTO, prevVersionSl, prevVersion));
            });
            if( !clonedTls.isEmpty() ) // save if any TLs is cloned
            {
                vocabularyDTO.getVersions().addAll( clonedTls );
                vocabularyDTO = vocabularyService.save( vocabularyDTO );
            }
        } else {
            log.info( "Unable to check for available TLs to be cloned, unable to find prev SL version with ID {0}",  versionDTO.getPreviousVersion());
        }
        return vocabularyDTO;
    }

    /**
     * {@code GET  /editors/vocabularies/compare-prev/:id} : get the text to be compared by diff-algorithm,
     * given "id" vocabulary and previous version from vocabulary.
     *
     * @param id the id of the vocabularyDTO to be compared with previous version.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vocabularyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/editors/vocabularies/compare-prev/{id}")
    public ResponseEntity<List<String>> getVocabularyComparePrev(@PathVariable Long id) {
        log.debug("REST request to get Vocabulary comparison text: {}", id);
        VersionDTO versionDTO = versionService.findOne(id)
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + id));
        if( versionDTO.getPreviousVersion() == null ) {
            throw new IllegalArgumentException( "Unable to create previous comparison text, previous version is missing!" );
        }
        VersionDTO prevVersionDTO = versionService.findOne(versionDTO.getPreviousVersion())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + versionDTO.getPreviousVersion()));

        // create comparison based on current version
        StringBuilder currentVersionCvSb = new StringBuilder();
        StringBuilder prevVersionCvSb = new StringBuilder();

        currentVersionCvSb.append( "Cv Name: " + versionDTO.getTitle() + "\n");
        currentVersionCvSb.append( "Cv Def: " + versionDTO.getDefinition() + "\n");
        currentVersionCvSb.append( "Cv Notes: " + versionDTO.getNotes() + "\n\n\n");

        prevVersionCvSb.append( "Cv Name: " + prevVersionDTO.getTitle() + "\n");
        prevVersionCvSb.append( "Cv Def: " + prevVersionDTO.getDefinition() + "\n");
        prevVersionCvSb.append( "Cv Notes: " + prevVersionDTO.getNotes() + "\n\n\n");

        // get concepts and sorted by position
        List<ConceptDTO> currentConcepts = conceptService.findByVersion(versionDTO.getId());
        currentConcepts.forEach(currentConcept -> {
            currentVersionCvSb.append( "Code: " + currentConcept.getNotation()+ "\n");
            currentVersionCvSb.append( "Code Term: " + currentConcept.getTitle() + "\n");
            currentVersionCvSb.append( "Code Def: " + currentConcept.getDefinition() + "\n\n");

            ConceptDTO prevConceptDTO = null;

            if( currentConcept.getPreviousConcept() != null )
                prevConceptDTO = prevVersionDTO.getConcepts().stream()
                    .filter(prevConcept -> currentConcept.getPreviousConcept().equals( prevConcept.getId())).findFirst()
                    .orElse(null);

            if ( prevConceptDTO == null ) {
                prevVersionCvSb.append( "Code: \n");
                prevVersionCvSb.append( "Code Term: \n");
                prevVersionCvSb.append( "Code Def: \n\n");
            } else {
                prevVersionCvSb.append( "Code: " + prevConceptDTO.getNotation()+ "\n");
                prevVersionCvSb.append( "Code Term: " + prevConceptDTO.getTitle() + "\n");
                prevVersionCvSb.append( "Code Def: " + prevConceptDTO.getDefinition() + "\n\n");
            }
        });

        List<String> compareCurrentPrev = new ArrayList<>();
        compareCurrentPrev.add( prevVersionCvSb.toString() );
        compareCurrentPrev.add( currentVersionCvSb.toString() );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Prev-Cv-Version", prevVersionDTO.getNotation() + " " +prevVersionDTO.getItemType() + " v." + prevVersionDTO.getNumber());
        headers.add("X-Current-Cv-Version", versionDTO.getNotation() + " " +versionDTO.getItemType() + " v." + versionDTO.getNumber());

        return ResponseEntity.ok().headers(headers).body(compareCurrentPrev);
    }

    /**
     * {@code DELETE  /editors/vocabularies/:id} : delete the "id" version.
     *
     * @param id the id of the versionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/editors/vocabularies/{id}")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable Long id) {
        log.debug("REST request to delete Version : {}", id);
        // first check version and determine delete strategy
        VersionDTO versionDTO = versionService.findOne(id)
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + id + " to be deleted" ));
        VersionDTO finalVersionDTO = versionDTO;
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(versionDTO.getVocabularyId())
            .orElseThrow( () -> new EntityNotFoundException("Unable to find vocabulary with Id " + finalVersionDTO.getVocabularyId() ));
        // replace the equal Version object with the one from VocabularyDto
        versionDTO = vocabularyDTO.getVersions().stream().filter(v -> v.getId().equals( finalVersionDTO.getId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + finalVersionDTO.getId()  ));

        // check if user authorized to delete VocabularyResource
        SecurityUtils.checkResourceAuthorization(ActionType.DELETE_CV,
            vocabularyDTO.getAgencyId(), ActionType.DELETE_CV.getAgencyRoles(), versionDTO.getLanguage());

        if( versionDTO.getItemType().equals(ItemType.TL.toString()) ){
            deleteTlVocabulary(versionDTO, vocabularyDTO);
        } else {
            if( versionDTO.isInitialVersion() ) {
                // delete whole vocabulary
                vocabularyService.delete(versionDTO.getVocabularyId());
            } else {
                // delete version SL and related TLs
                List<VersionDTO> versionDTOs = versionService.findAllByVocabularyAnyVersionSl(versionDTO.getVocabularyId(), versionDTO.getNumber());
                versionDTOs.forEach( v -> versionService.delete(v.getId()));
            }
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_VERSION_NAME, id.toString())).build();
    }

    private void deleteTlVocabulary(VersionDTO versionDTO, VocabularyDTO vocabularyDTO) {
        boolean isTlPublished = versionDTO.getStatus().equals( Status.PUBLISHED.toString());
        vocabularyDTO.removeVersion( versionDTO);
        if( isTlPublished ) {
            // if published update vocabulary
            if( versionDTO.isInitialVersion() ) {
                vocabularyDTO.setTitleDefinition(null, null, versionDTO.getLanguage());
                vocabularyDTO.setVersionByLanguage(versionDTO.getLanguage(), null );
            } else {
                // check if there is previous version
                VersionDTO versionPrevDTO = null;
                if ( versionDTO.getPreviousVersion() != null )
                    versionPrevDTO = versionService.findOne(versionDTO.getPreviousVersion()).orElse(null);
                if( versionPrevDTO != null) {
                    vocabularyDTO.setTitleDefinition(versionPrevDTO.getTitle(), versionPrevDTO.getDefinition(), versionPrevDTO.getLanguage());
                    vocabularyDTO.setVersionByLanguage(versionPrevDTO.getLanguage(), versionPrevDTO.getNumber() );
                }
            }
        }
        versionService.delete( versionDTO.getId() );
        vocabularyDTO = vocabularyService.save(vocabularyDTO);
        vocabularyService.indexEditor( vocabularyDTO );
        if( isTlPublished ) {
            vocabularyService.indexPublished(vocabularyDTO );
        }
    }

    /**
     * {@code POST  /editors/codes} : Create a new code/concept via editor Rest API.
     *
     * @param codeSnippet the conceptDTO helper to create.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ConceptDTO, or with status {@code 400 (Bad Request)} if the concept has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws CodeAlreadyExistException {@code 400 (Bad Request)} if the codes is already exist.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PostMapping("/editors/codes")
    public ResponseEntity<ConceptDTO> createCode(@Valid @RequestBody CodeSnippet codeSnippet) throws URISyntaxException {
        log.debug("REST request to save Code/Concept : {}", codeSnippet);

        if (codeSnippet.getConceptId() != null) {
            throw new BadRequestAlertException("A new code/concept cannot already have an ID", ENTITY_CODE_NAME, "idexists");
        }

        ConceptDTO result = vocabularyService.saveCode( codeSnippet );
        return ResponseEntity.created(new URI("/api/concepts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_CODE_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code POST  /editors/codes/batch} : Create batch of new codes/concepts via editor Rest API.
     *
     * @param codeSnippets the conceptDTOs helper to create.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ConceptDTO, or with status {@code 400 (Bad Request)} if the concept has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws CodeAlreadyExistException {@code 400 (Bad Request)} if the codes is already exist.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PostMapping("/editors/codes/batch")
    public ResponseEntity<Void> createBatchCode(@Valid @RequestBody CodeSnippet[] codeSnippets) throws URISyntaxException {
        boolean isSaved = false;
        for (CodeSnippet codeSnippet : codeSnippets) {
            log.debug("REST request to save Code/Concept : {}", codeSnippet);

            if (codeSnippet.getConceptId() != null) {
                throw new BadRequestAlertException("A new code/concept cannot already have an ID", ENTITY_CODE_NAME, "idexists");
            }
        }

        for (CodeSnippet codeSnippet : codeSnippets) {
            ConceptDTO result = vocabularyService.saveCode(codeSnippet);
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityCreationAlert(applicationName, true,
            ENTITY_CODE_NAME, Arrays.stream(codeSnippets).map(c -> c.getNotation()).collect(Collectors.joining(", ")))).build();
    }

    /**
     * {@code PUT  /editors/codes} : Updates an existing code/concept via editor Rest API.
     *
     * @param codeSnippet the conceptDTO helper to create.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conceptDTO,
     * or with status {@code 400 (Bad Request)} if the conceptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conceptDTO couldn't be updated.
     * @throws CodeAlreadyExistException {@code 400 (Bad Request)} if the codes is already exist.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PutMapping("/editors/codes")
    public ResponseEntity<ConceptDTO> updateCode(@Valid @RequestBody CodeSnippet codeSnippet) {
        log.debug("REST request to update Code/Concept : {}", codeSnippet);
        if( !(codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.ADD_TL_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.EDIT_TL_CODE ) ||
            codeSnippet.getActionType().equals( ActionType.DELETE_TL_CODE )))
            throw new IllegalArgumentException( "Action type " + codeSnippet.getActionType() + "not supported" );
        if (codeSnippet.getConceptId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_CODE_NAME, "idnull");
        }
        ConceptDTO result = vocabularyService.saveCode( codeSnippet );
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_CODE_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code DELETE  /editors/codes/:id} : delete the "id" code/concept.
     *
     * @param id the id of the conceptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @DeleteMapping("/editors/codes/{id}")
    public ResponseEntity<Void> deleteCode(@PathVariable Long id) {
        log.debug("REST request to delete Code/Concept : {}", id);
        // first check version and determine delete strategy
        ConceptDTO conceptDTO = conceptService.findOne(id)
            .orElseThrow(() -> new EntityNotFoundException("Unable to find concept with Id " + id + " to be deleted" ));
        VersionDTO versionDTO = versionService.findOne(conceptDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + conceptDTO.getVersionId() ));
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(versionDTO.getVocabularyId())
            .orElseThrow( () -> new EntityNotFoundException("Unable to find vocabulary with Id " + versionDTO.getVocabularyId() ));

        // check if user authorized to delete VocabularyResource
        SecurityUtils.checkResourceAuthorization(ActionType.DELETE_CODE,
            vocabularyDTO.getAgencyId(), ActionType.DELETE_CODE.getAgencyRoles(), versionDTO.getLanguage());

        // remove parent-child link and save, orphan concept will be automatically deleted
        versionDTO.removeConcept( conceptDTO );
        // remove any child if exist
        Iterator<ConceptDTO> conceptIterator = versionDTO.getConcepts().iterator();
        while ( conceptIterator.hasNext() ) {
            ConceptDTO conceptNext = conceptIterator.next();
            if( conceptNext.getParent() != null && conceptNext.getParent().startsWith( conceptDTO.getNotation()) ){
                conceptIterator.remove();
            }
        }
        versionService.save(versionDTO);

        // index editor after delete
        vocabularyService.indexEditor( vocabularyDTO );
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_CODE_NAME, conceptDTO.getNotation())).build();
    }

    /**
     * {@code POST  /editors/codes/reorder} : Reorder the existing codes via editor Rest API.
     *
     * @param codeSnippet the conceptDTOs helper to reorder codes.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} if success.
     */
    @PostMapping("/editors/codes/reorder")
    public ResponseEntity<VersionDTO> reorderCode(@Valid @RequestBody CodeSnippet codeSnippet){
        log.debug("REST request to reorder codes : {}", codeSnippet);

        if ( !codeSnippet.getActionType().equals( ActionType.REORDER_CODE) ) {
            throw new IllegalArgumentException( "ActionType REORDER_CODE needed" );
        }

        VersionDTO versionDTO = versionService.findOne(codeSnippet.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + codeSnippet.getVersionId() ));
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(versionDTO.getVocabularyId())
            .orElseThrow( () -> new EntityNotFoundException("Unable to find vocabulary with Id " + versionDTO.getVocabularyId() ));

        // check if user authorized to reorder VocabularyResource
        SecurityUtils.checkResourceAuthorization(ActionType.REORDER_CODE,
            vocabularyDTO.getAgencyId(), ActionType.REORDER_CODE.getAgencyRoles(), versionDTO.getLanguage());

        for (int i = 0; i < codeSnippet.getConceptStructureIds().size(); i++) {
            int finalI = i;
            ConceptDTO concept = versionDTO.getConcepts().stream().filter(c -> c.getId().equals(codeSnippet.getConceptStructureIds().get(finalI))).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Unable to find code/concept with Id " + codeSnippet.getConceptStructureIds().get(finalI)));

            String newNotation = codeSnippet.getConceptStructures().get( finalI );
            int dotIndex = newNotation.lastIndexOf('.');
            if ( dotIndex < 0 ) {
                concept.setParent( null );
            } else {
                concept.setParent( newNotation.substring(0, dotIndex));
            }
            concept.setNotation( newNotation );
            concept.setPosition(i);
        }
        // save
        versionService.save( versionDTO );
        // reindex
        vocabularyService.indexEditor(vocabularyDTO );

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_VERSION_NAME, versionDTO.getNotation()))
            .body(versionDTO);
    }

    /**
     * {@code POST  /editors/comments} : Create a new comment via editor Rest API.
     *
     * @param commentDTO the commentDTO to create.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new CommentDTO, or with status {@code 400 (Bad Request)} if the comment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/editors/comments")
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentDTO commentDTO) throws URISyntaxException {
        log.debug("REST request to save Comment : {}", commentDTO);

        if (commentDTO.getId() != null) {
            throw new BadRequestAlertException("A new comment cannot already have an ID", ENTITY_CODE_NAME, "idexists");
        }

        if (commentDTO.getVersionId() == null) {
            throw new IllegalArgumentException( "Comment need to be linked to version. Version ID is null" );
        }

        @Valid CommentDTO finalCommentDTO = commentDTO;
        VersionDTO versionDTO = versionService.findOne(commentDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + finalCommentDTO.getVersionId() ));
        commentDTO.setUserId( SecurityUtils.getCurrentUserId() );
        ZonedDateTime dateTime = ZonedDateTime.now();
        commentDTO.setDateTime( dateTime );
        versionDTO.addComment( commentDTO );

        versionDTO = versionService.save(versionDTO);

        CommentDTO newSavedComment = versionDTO.getComments().stream().filter(c -> c.getDateTime().equals(dateTime)).findFirst().orElse(null);
        if( newSavedComment != null )
            commentDTO = newSavedComment;

        return ResponseEntity.created(new URI("/api/comment/" + commentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_COMMENT_NAME, commentDTO.getId().toString()))
            .body(commentDTO);
    }

    /**
     * {@code PUT  /editors/comments} : Updates an existing comment via editor Rest API.
     *
     * @param commentDTO the commentDTO to update.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new CommentDTO, or with status {@code 400 (Bad Request)} if the comment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentDTO,
     * or with status {@code 400 (Bad Request)} if the commentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commentDTO couldn't be updated.
     */
    @PutMapping("/editors/comments")
    public ResponseEntity<CommentDTO> updateComment(@Valid @RequestBody CommentDTO commentDTO) {
        log.debug("REST request to update Comment : {}", commentDTO);
        if (commentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_CODE_NAME, "idnull");
        }

        // find version
        VersionDTO versionDTO = versionService.findOne(commentDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + commentDTO.getVersionId() ));
        // find comment from version
        CommentDTO commentFromVersion = versionDTO.getComments().stream().filter(c -> c.getId().equals( commentDTO.getId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Unable to find comment with Id " + commentDTO.getId() ));

        // update comment from version
        commentFromVersion.setDateTime( ZonedDateTime.now());
        commentFromVersion.setContent( commentDTO.getContent());
        versionService.save(versionDTO );

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_COMMENT_NAME, commentFromVersion.getId().toString()))
            .body(commentFromVersion);
    }

    /**
     * {@code DELETE  /editors/comments/:id} : delete the "id" comment.
     *
     * @param id the id of the comment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/editors/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);
        // first check version and determine delete strategy
        CommentDTO commentDTO = commentService.findOne(id)
            .orElseThrow(() -> new EntityNotFoundException("Unable to find comment with Id " + id + " to be deleted" ));
        VersionDTO versionDTO = versionService.findOne(commentDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + commentDTO.getId() ));
        commentDTO.setVersionId( null );
        versionDTO.removeComment(commentDTO);
        // automatically remove comment
        versionService.save(versionDTO);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true,
            ENTITY_COMMENT_NAME, id.toString())).build();
    }

    /**
     * {@code POST  /editors/metadatas} : Create a new metadata via editor Rest API.
     *
     * @param metadataValueDTO the metadataValueDTO to create.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new MetadataValueDTO, or with status {@code 400 (Bad Request)} if the metadataValueDTO has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/editors/metadatas")
    public ResponseEntity<MetadataValueDTO> createAppMetadata(@Valid @RequestBody MetadataValueDTO metadataValueDTO) throws URISyntaxException {
        log.debug("REST request to save MetadataValue : {}", metadataValueDTO);

        if (metadataValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new metadataValueDTO cannot already have an ID", ENTITY_CODE_NAME, "idexists");
        }

        if (metadataValueDTO.getMetadataKey() == null) {
            throw new IllegalArgumentException( "MetadataValue need to be linked to metadataKey. metadataKey is null" );
        }

        MetadataFieldDTO metadataFieldDTO = metadataFieldService.findByMetadataKey( metadataValueDTO.getMetadataKey() )
            .orElse(null);
        if( metadataFieldDTO == null ) {
            metadataFieldDTO = new MetadataFieldDTO();
            metadataFieldDTO.setMetadataKey( metadataValueDTO.getMetadataKey() );
            metadataFieldDTO.setObjectType( metadataValueDTO.getObjectType() );
            metadataFieldDTO = metadataFieldService.save( metadataFieldDTO);
        }

        metadataValueDTO.setMetadataFieldId( metadataFieldDTO.getId());
        metadataFieldDTO.addMetadataValue(metadataValueDTO);

        metadataFieldDTO = metadataFieldService.save( metadataFieldDTO);

        if( metadataFieldDTO.getMetadataValues().isEmpty()) {
            throw new EntityNotFoundException( "Unable to get any MetadataValues from metadataFieldDTO "+ metadataFieldDTO.getId() );
        }

        MetadataValueDTO result = metadataFieldDTO.getMetadataValues().iterator().next();

        if( metadataFieldDTO.getMetadataValues().size() > 1) {
            result = metadataFieldDTO.getMetadataValues().stream().filter(v -> v.getValue().equals(metadataValueDTO.getValue() ))
                .findFirst().orElse(result);
        }

        return ResponseEntity.created(new URI("/api/metadata-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_METADATAVALUE_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /editors/metadatas} : Updates an existing metadataValueDTO via editor Rest API.
     *
     * @param metadataValueDTO the metadataValueDTO to update.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new MetadataValueDTO, or with status {@code 400 (Bad Request)} if the metadataValueDTO has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metadataValueDTO,
     * or with status {@code 400 (Bad Request)} if the metadataValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metadataValueDTO couldn't be updated.
     */
    @PutMapping("/editors/metadatas")
    public ResponseEntity<MetadataValueDTO> updateAppMetadata(@Valid @RequestBody MetadataValueDTO metadataValueDTO) {
        log.debug("REST request to update MetadataValue : {}", metadataValueDTO);
        if (metadataValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_CODE_NAME, "idnull");
        }

        if (metadataValueDTO.getMetadataFieldId() == null) {
            throw new IllegalArgumentException( "MetadataValue need to be linked to MetadataField. MetadataField.ID is null" );
        }

        MetadataFieldDTO metadataFieldDTO = metadataFieldService.findOne(metadataValueDTO.getMetadataFieldId() )
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataField with metadataKey " + metadataValueDTO.getMetadataKey() ));
        // find metadataValueDTO from MetadataFieldDTO
        MetadataValueDTO result  = metadataFieldDTO.getMetadataValues().stream().filter(v -> v.getId().equals(metadataValueDTO.getId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataValue with Id " + metadataValueDTO.getId() ));

        // update comment from version
        result.setValue( metadataValueDTO.getValue() );
        metadataFieldService.save( metadataFieldDTO );

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_METADATAVALUE_NAME, metadataValueDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code DELETE  /editors/metadatas/:id} : delete the "id" metadata.
     *
     * @param id the id of the metadata to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/editors/metadatas/{id}")
    public ResponseEntity<Void> deleteAppMetadata(@PathVariable Long id) {
        log.debug("REST request to delete Metadata : {}", id);
        // first check version and determine delete strategy
        MetadataValueDTO metadataValueDTO = metadataValueService.findOne(id)
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataValue with Id " + id + " to be deleted" ));
        MetadataFieldDTO metadataFieldDTO = metadataFieldService.findOne(metadataValueDTO.getMetadataFieldId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataField with Id " + metadataValueDTO.getMetadataFieldId() ));

        // automatically remove metadataValue
        metadataValueDTO.setMetadataFieldId( null );
        metadataFieldDTO.removeMetadataValue(metadataValueDTO);
        metadataFieldService.save( metadataFieldDTO );

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true,
            ENTITY_METADATAVALUE_NAME, id.toString())).build();
    }
}
