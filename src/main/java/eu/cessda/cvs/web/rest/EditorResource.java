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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

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

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyService vocabularyService;

    private final VersionService versionService;

    private final ConceptService conceptService;

    private final LicenceService licenceService;

    private final AgencyService agencyService;

    public EditorResource(VocabularyService vocabularyService, VersionService versionService, ConceptService conceptService, LicenceService licenceService, AgencyService agencyService) {
        this.vocabularyService = vocabularyService;
        this.versionService = versionService;
        this.conceptService = conceptService;
        this.licenceService = licenceService;
        this.agencyService = agencyService;
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
     * @throws VocabularyAlreadyExistException {@code 400 (Bad Request)} if the codes is already exist.
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
     * {@code PUT  /editors/codes} : Updates an existing code/concept via editor Rest API.
     *
     * @param codeSnippet the conceptDTO helper to create.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vocabularyDTO,
     * or with status {@code 400 (Bad Request)} if the vocabularyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conceptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PutMapping("/editors/codes")
    public ResponseEntity<ConceptDTO> updateCode(@Valid @RequestBody CodeSnippet codeSnippet) throws URISyntaxException {
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
     */
    @DeleteMapping("/editors/codes/{id}")
    public ResponseEntity<Void> deleteCode(@PathVariable Long id) {
        log.debug("REST request to delete Code/Concept : {}", id);
        // first check version and determine delete strategy
        ConceptDTO conceptDTO = conceptService.findOne(id)
            .orElseThrow(() -> new EntityNotFoundException("Unable to find concept with Id " + id + " to be deleted" ));
        VersionDTO versionDTO = versionService.findOne(conceptDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find version with Id " + id ));
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
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PostMapping("/editors/codes/reorder")
    public ResponseEntity<VersionDTO> reorderCode(@Valid @RequestBody CodeSnippet codeSnippet) throws URISyntaxException {
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
}
