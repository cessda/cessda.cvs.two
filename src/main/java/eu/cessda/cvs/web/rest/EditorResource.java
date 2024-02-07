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
package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.config.audit.AuditEventPublisher;
import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.*;
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.utils.VocabularyUtils;
import eu.cessda.cvs.web.rest.domain.CvResult;
import eu.cessda.cvs.web.rest.errors.BadRequestAlertException;
import eu.cessda.cvs.web.rest.utils.ResourceUtils;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/api")
public class EditorResource {

    @Autowired
    private AuditEventPublisher auditPublisher;

    public static final String UNABLE_TO_FIND_VERSION = "Unable to find version with Id ";
    public static final String UNABLE_TO_FIND_CONCEPT = "Unable to find concept with Id ";
    public static final String TO_BE_DELETED = " to be deleted";
    public static final String TO_BE_DEPRECATED = " to be deprecated";
    public static final String AS_A_REPLACING_CONCEPT = " as a replacing concept";
    public static final String UNABLE_TO_FIND_VOCABULARY = "Unable to find vocabulary with Id ";
    public static final String INVALID_ID = "Invalid id";
    public static final String ID_EXIST = "idexists";
    public static final String ID_NULL = "idnull";
    private static final Logger log = LoggerFactory.getLogger(EditorResource.class);
    public static final String ATTACHMENT_FILENAME = "attachment; filename=";
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

    private final CommentService commentService;

    private final MetadataFieldService metadataFieldService;

    private final MetadataValueService metadataValueService;

    private final ApplicationProperties applicationProperties;

    private final VocabularyChangeService vocabularyChangeService;

    @SuppressWarnings("squid:S107") // since constructor params are autowired
    public EditorResource(VocabularyService vocabularyService, VersionService versionService, ConceptService conceptService,
                          CommentService commentService, MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
                          ApplicationProperties applicationProperties, VocabularyChangeService vocabularyChangeService) {
        this.vocabularyService = vocabularyService;
        this.versionService = versionService;
        this.conceptService = conceptService;
        this.commentService = commentService;
        this.metadataFieldService = metadataFieldService;
        this.metadataValueService = metadataValueService;
        this.applicationProperties = applicationProperties;
        this.vocabularyChangeService = vocabularyChangeService;
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
        if (vocabularySnippet.getActionType().equals( ActionType.CREATE_CV)) {
            SecurityUtils.checkResourceAuthorization(ActionType.CREATE_CV, vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage());

            if (vocabularySnippet.getVocabularyId() != null) {
                throw new BadRequestAlertException("A new vocabulary cannot already have an ID", ENTITY_VOCABULARY_NAME, ID_EXIST);
            }
        }
        else if (vocabularySnippet.getActionType().equals(ActionType.ADD_TL_CV)) {
            SecurityUtils.checkResourceAuthorization(ActionType.ADD_TL_CV, vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage());
        } else {
            throw new IllegalArgumentException( "Illegal action type for POST" + vocabularySnippet.getActionType() );
        }

        VocabularyDTO result = vocabularyService.saveVocabulary(vocabularySnippet);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, result, null, vocabularySnippet, "CREATE_VOCABULARY");

        return ResponseEntity.created(new URI("/api/vocabularies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_VOCABULARY_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code POST  /editors/vocabularies/new-version/{id}} : Create a new vocabulary version from existing version Rest API.
     *
     * @param id the ID of version to be cloned/add new version
     * @return the created vocabulary version
     */
    @PostMapping("/editors/vocabularies/new-version/{id}")
    public ResponseEntity<VersionDTO> createNewVocabularyVersion(@PathVariable Long id) {
        log.debug("REST request to create new Vocabulary with version ID: {}", id);
        VersionDTO result = vocabularyService.createNewVersion(id);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, null, result, null, "CREATE_NEW_VOCABULARY_VERSION");

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
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PutMapping("/editors/vocabularies")
    public ResponseEntity<VocabularyDTO> updateVocabulary(@Valid @RequestBody VocabularySnippet vocabularySnippet) {
        log.debug("REST request to update Vocabulary : {}", vocabularySnippet);
        if (vocabularySnippet.getVocabularyId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_VOCABULARY_NAME, ID_NULL);
        }
        if(
            !(vocabularySnippet.getActionType().equals( ActionType.EDIT_CV ) ||
                vocabularySnippet.getActionType().equals( ActionType.EDIT_DDI_CV) ||
                vocabularySnippet.getActionType().equals( ActionType.EDIT_IDENTITY_CV) ||
                vocabularySnippet.getActionType().equals( ActionType.EDIT_VERSION_INFO_CV) ||
                vocabularySnippet.getActionType().equals( ActionType.EDIT_NOTE_CV))
        ) {
            throw new IllegalArgumentException( "Incorrect ActionType" + vocabularySnippet.getActionType() );
        }
        VocabularyDTO result = vocabularyService.saveVocabulary(vocabularySnippet);

        //notify the auditing mechanism
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(vocabularySnippet.getVocabularyId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VOCABULARY + vocabularySnippet.getVersionId()));
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, vocabularyDTO, null, vocabularySnippet, "UPDATE_VOCABULARY_" + vocabularySnippet.getActionType());

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
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PutMapping("/editors/vocabularies/forward-status")
    public ResponseEntity<VersionDTO> forwardStatusVocabulary(@Valid @RequestBody VocabularySnippet vocabularySnippet) {
        log.debug("REST request to update forward status Vocabulary : {}", vocabularySnippet);
        VersionDTO versionDTO = vocabularyService.forwardStatus(vocabularySnippet);

        //notify the auditing mechanism
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(vocabularySnippet.getVocabularyId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VOCABULARY + vocabularySnippet.getVersionId()));
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, vocabularySnippet, vocabularySnippet.getActionType().name(), true);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_VERSION_NAME, versionDTO.getNotation()))
            .body(versionDTO);
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
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + id));
        if( versionDTO.getPreviousVersion() == null ) {
            throw new IllegalArgumentException( "Unable to create previous comparison text, previous version is missing!" );
        }
        VersionDTO prevVersionDTO = versionService.findOne(versionDTO.getPreviousVersion())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + versionDTO.getPreviousVersion()));

        return ResourceUtils.getListResponseEntity(versionDTO, prevVersionDTO);
    }

    /**
     * {@code DELETE  /editors/vocabularies/:id} : delete the "id" version.
     *
     * @param id the id of the versionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/editors/vocabularies/{id}")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable Long id) throws IOException {
        log.debug("REST request to delete Version : {}", id);
        // first check version and determine delete strategy
        VersionDTO versionDTO = versionService.findOne(id)
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + id + TO_BE_DELETED));
        Long vocabId = versionDTO.getVocabularyId();
        VocabularyDTO vocabularyDTO = vocabularyService.findOne( vocabId )
            .orElseThrow( () -> new EntityNotFoundException(UNABLE_TO_FIND_VOCABULARY + vocabId ));
        // replace the equal Version object with the one from VocabularyDto
        versionDTO = vocabularyDTO.getVersions().stream().filter(v -> v.getId().equals( id )).findFirst()
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + id  ));

        // check if user authorized to delete VocabularyResource
        SecurityUtils.checkResourceAuthorization(ActionType.DELETE_CV,
            vocabularyDTO.getAgencyId(), versionDTO.getLanguage());

        if( versionDTO.getItemType().equals(ItemType.TL.toString()) ){
            deleteTlVocabulary(versionDTO, vocabularyDTO);
        } else {
            if( versionDTO.isInitialVersion() ) {
                // delete whole vocabulary
                //notify the auditing mechanism
                String auditUserString = "";
                Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
                if (auditUser.isPresent()) {
                    auditUserString = auditUser.get();
                }
                auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, null, "DELETE_WHOLE_VOCABULARY");

                vocabularyService.delete(versionDTO.getVocabularyId());
            } else {
                // delete version SL and related TLs

                //notify the auditing mechanism
                String auditUserString = "";
                Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
                if (auditUser.isPresent()) {
                    auditUserString = auditUser.get();
                }
                auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, null, "DELETE_VOCABULARY_SL_AND_RELATED_TL(S)_VERSION");

                VersionDTO finalVersionDTO = versionDTO;
                List<VersionDTO> versionDTOs = vocabularyDTO.getVersions().stream().filter(v -> v.getNumber().equalPatchVersionNumber(finalVersionDTO.getNumber())).collect(Collectors.toList());
                versionDTOs.forEach(vocabularyDTO.getVersions()::remove);
                // change vocabulary version id to the previous version number
                vocabularyDTO.clearContent();

                VersionDTO prevSlVersionDto = vocabularyDTO.getVersions().stream().filter(v -> v.getId().equals(finalVersionDTO.getPreviousVersion())).findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + finalVersionDTO.getId()  ));

                vocabularyDTO.setVersionNumber( prevSlVersionDto.getNumber());
                Set<VersionDTO> prevVersions = vocabularyDTO.getVersions().stream().filter(v -> v.getNumber().equalPatchVersionNumber(prevSlVersionDto.getNumber())).collect(Collectors.toSet());
                VocabularyDTO.fillVocabularyByVersions(vocabularyDTO, prevVersions);
                vocabularyDTO = vocabularyService.save(vocabularyDTO);
                // indexing editor
                vocabularyService.indexEditor(vocabularyDTO);

                if( versionDTO.getStatus().equals(Status.PUBLISHED.toString())) {
                    // remove published JSON file, re-create the JSON file and re-index for published vocabulary
                    vocabularyService.deleteCvJsonDirectoryAndContent( Path.of( applicationProperties.getVocabJsonPath(), vocabularyDTO.getNotation() ));
                    vocabularyService.generateJsonVocabularyPublish(vocabularyDTO);
                    vocabularyService.indexPublished(vocabularyDTO);
                }
            }
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_VERSION_NAME, id.toString())).build();
    }

    private void deleteTlVocabulary(VersionDTO versionDTO, VocabularyDTO vocabularyDTO) throws IOException {
        boolean isTlPublished = versionDTO.getStatus().equals( Status.PUBLISHED.toString());
        vocabularyDTO.removeVersion( versionDTO);
        if( isTlPublished ) {
            // if published update vocabulary
            if( versionDTO.isInitialVersion() ) {
                vocabularyDTO.setTitleDefinition(null, null, versionDTO.getLanguage(), false);
                vocabularyDTO.setVersionByLanguage(versionDTO.getLanguage(), null );
            } else {
                // check if there is previous version
                VersionDTO versionPrevDTO = null;
                if (versionDTO.getPreviousVersion() != null)
                    versionPrevDTO = versionService.findOne(versionDTO.getPreviousVersion()).orElse(null);
                if( versionPrevDTO != null) {
                    vocabularyDTO.setTitleDefinition(versionPrevDTO.getTitle(), versionPrevDTO.getDefinition(), versionPrevDTO.getLanguage(), false);
                    vocabularyDTO.setVersionByLanguage(versionPrevDTO.getLanguage(), versionPrevDTO.getNumber().toString() );
                }
            }
        }

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, null, "DELETE_TL_VOCABULARY");

        vocabularyDTO.removeVersion( versionDTO );
        vocabularyDTO = vocabularyService.save(vocabularyDTO);
        vocabularyService.indexEditor( vocabularyDTO );
        if( isTlPublished ) {
            // remove published JSON file, re-create the JSON file and re-index for published vocabulary
            vocabularyService.deleteCvJsonDirectoryAndContent( Path.of( applicationProperties.getVocabJsonPath(), vocabularyDTO.getNotation() ) );
            vocabularyService.generateJsonVocabularyPublish(vocabularyDTO);
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
            throw new BadRequestAlertException("A new code/concept cannot already have an ID", ENTITY_CODE_NAME, ID_EXIST);
        }

        ConceptDTO result = vocabularyService.saveCode(codeSnippet);

        //notify the auditing mechanism
        VersionDTO versionDTO = versionService.findOne(codeSnippet.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + codeSnippet.getVersionId()));
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, null, versionDTO, result, null, codeSnippet, "CREATE_CODE");

        return ResponseEntity.created(new URI("/api/concepts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_CODE_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code POST  /editors/codes/batch} : Create batch of new codes/concepts via editor Rest API.
     *
     * @param codeSnippets the conceptDTOs helper to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ConceptDTO, or with status {@code 400 (Bad Request)} if the concept has already an ID.
     * @throws CodeAlreadyExistException                {@code 400 (Bad Request)} if the codes is already exist.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PostMapping("/editors/codes/batch")
    public ResponseEntity<List<ConceptDTO>> createBatchCode(@Valid @RequestBody CodeSnippet[] codeSnippets) {
        if (codeSnippets.length == 0) {
            throw new IllegalArgumentException("CodeSnippet[] can not be empty array");
        }

        // get version
        VersionDTO versionDTO = versionService.findOne(codeSnippets[0].getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + codeSnippets[0].getVersionId()));

        // reject if version status is published
        if (versionDTO.getStatus().equals(Status.PUBLISHED.toString())) {
            throw new IllegalArgumentException("Unable to add Code " + codeSnippets[0].getNotation() + ", Version is already PUBLISHED");
        }

        final Long vocabularyId = versionDTO.getVocabularyId();
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(vocabularyId)
            .orElseThrow(() -> new EntityNotFoundException("Unable to add Vocabulary " + vocabularyId));

        // check for authorization
        if (codeSnippets[0].getActionType().equals( ActionType.CREATE_CODE))
            SecurityUtils.checkResourceAuthorization(ActionType.CREATE_CODE,
                vocabularyDTO.getAgencyId(), versionDTO.getLanguage());
        else if (codeSnippets[0].getActionType().equals( ActionType.ADD_TL_CODE))
            SecurityUtils.checkResourceAuthorization(ActionType.ADD_TL_CODE,
                vocabularyDTO.getAgencyId(), versionDTO.getLanguage());

        List<ConceptDTO> storedCodes = new ArrayList<>();
        for (CodeSnippet codeSnippet : codeSnippets) {
            log.debug("REST request to save Code/Concept : {}", codeSnippet);

            ConceptDTO conceptDTO = null;

            if (codeSnippet.getActionType().equals(ActionType.CREATE_CODE)) {
                conceptDTO = addNewConcept(versionDTO, codeSnippet);
            } else if (codeSnippet.getActionType().equals(ActionType.ADD_TL_CODE)) {
                conceptDTO = versionDTO.getConcepts().stream().filter(c -> c.getId().equals(codeSnippet.getConceptId())).findFirst().orElse(null);
                if (conceptDTO != null) {
                    conceptDTO.setTitle(codeSnippet.getTitle());
                    conceptDTO.setDefinition(codeSnippet.getDefinition());

                    //notify the auditing mechanism
                    String auditUserString = "";
                    Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
                    if (auditUser.isPresent()) {
                        auditUserString = auditUser.get();
                    }
                    auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, conceptDTO, null, codeSnippet, "ADD_TL_CODE");
                }
            }
            if (conceptDTO == null)
                continue;

            versionDTO = versionService.save(versionDTO);

            // check if codeSnippet contains changeType, store if exist
            vocabularyService.storeChangeType(codeSnippet, versionDTO);

            // find the newly created code from version
            ConceptDTO concept = versionDTO.findConceptByNotation(conceptDTO.getNotation());
            storedCodes.add(concept);
        }

        // index editor
        vocabularyService.indexEditor(vocabularyDTO);

        String conceptsNotation = storedCodes.stream().map(ConceptDTO::getNotation).collect(Collectors.joining());
        if (conceptsNotation.length() > 20) {
            conceptsNotation = conceptsNotation.substring(0, 20) + "...";
        }
        conceptsNotation += " (" + storedCodes.size() + " new codes added)";

        HttpHeaders headers = HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_CODE_NAME, conceptsNotation);
        headers.add("import-status", "Successfully importing " + storedCodes.size() + " codes");
        return ResponseEntity.ok().headers(headers).body(storedCodes);
    }

    private ConceptDTO addNewConcept(VersionDTO versionDTO, CodeSnippet codeSnippet) {
        // check if concept already exist for new concept
        if (versionDTO.getConcepts().stream()
            .anyMatch(c -> c.getNotation().equals(codeSnippet.getNotation()))) {
            throw new CodeAlreadyExistException(codeSnippet.getNotation());
        }
        // set position if not available
        if (codeSnippet.getPosition() == null)
            codeSnippet.setPosition( versionDTO.getConcepts().size() - 1 );
        // create concept by codeSnippet
        ConceptDTO newConceptDTO = new ConceptDTO(codeSnippet);

        //notify the auditing mechanism
        String auditUserString = SecurityUtils.getCurrentUserLogin().orElse( "" );
        auditPublisher.publish(auditUserString, null, versionDTO, newConceptDTO, null, codeSnippet, "CREATE_CODE");

        // add concept to version and save version to save new concept
        versionDTO.addConceptAt(newConceptDTO, newConceptDTO.getPosition());
        return newConceptDTO;
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
        if (!(codeSnippet.getActionType().equals(ActionType.EDIT_CODE) ||
            codeSnippet.getActionType().equals(ActionType.ADD_TL_CODE) ||
            codeSnippet.getActionType().equals(ActionType.EDIT_TL_CODE) ||
            codeSnippet.getActionType().equals(ActionType.DELETE_TL_CODE)))
            throw new IllegalArgumentException("Action type " + codeSnippet.getActionType() + "not supported");
        if (codeSnippet.getConceptId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_CODE_NAME, ID_NULL);
        }
        ConceptDTO result = vocabularyService.saveCode(codeSnippet);

        //notify the auditing mechanism
        VersionDTO versionDTO = versionService.findOne(codeSnippet.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + codeSnippet.getVersionId()));
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, null, versionDTO, result, null, codeSnippet, codeSnippet.getActionType().name());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_CODE_NAME, result.getNotation()))
            .body(result);
    }

    /**
     * {@code POST  /editors/codes/deprecate} : Deprecates an existing code/concept via editor Rest API.
     *
     * @param codeSnippet the conceptDTO helper to create.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conceptDTO,
     * or with status {@code 400 (Bad Request)} if the conceptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conceptDTO couldn't be updated.
     * @throws CodeAlreadyExistException {@code 400 (Bad Request)} if the codes is already exist.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficient rights to access the resource.
     */
    @PostMapping("/editors/codes/deprecate")
    public ResponseEntity<Void> deprecateCode(@Valid @RequestBody CodeSnippet codeSnippet) {
        log.debug("REST request to deprecate Code/Concept : {}", codeSnippet);
        if( !(codeSnippet.getActionType().equals( ActionType.DEPRECATE_CODE )) )
            throw new IllegalArgumentException( "Action type " + codeSnippet.getActionType() + "not supported" );
        if (codeSnippet.getConceptId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_CODE_NAME, ID_NULL);
        }
        ConceptDTO conceptDTO = conceptService.findOne(codeSnippet.getConceptId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_CONCEPT + codeSnippet.getConceptId() + TO_BE_DEPRECATED));
        VersionDTO versionDTO = versionService.findOne(conceptDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + conceptDTO.getVersionId() ));
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(versionDTO.getVocabularyId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VOCABULARY + versionDTO.getVocabularyId() ));

        // check if user authorized to delete VocabularyResource
        SecurityUtils.checkResourceAuthorization(ActionType.DEPRECATE_CODE,
            vocabularyDTO.getAgencyId(), versionDTO.getLanguage());

        conceptDTO.setDeprecated(true);
        conceptDTO.setReplacedById(codeSnippet.getReplacedById());

        ConceptDTO replacingConceptDTO = null;
        if (codeSnippet.getReplacedById() != null && codeSnippet.getReplacedById() >= 0) {
            replacingConceptDTO = conceptService.findOne(codeSnippet.getReplacedById())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_CONCEPT + codeSnippet.getReplacedById() + AS_A_REPLACING_CONCEPT));
        }

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, conceptDTO, replacingConceptDTO, codeSnippet, codeSnippet.getActionType().name());

        for (ConceptDTO conceptNext : versionDTO.getConcepts()) {
            if (conceptNext.equals(conceptDTO)) {
                conceptNext.setDeprecated(true);
                conceptNext.setReplacedById(codeSnippet.getReplacedById());

                // store changes if not initial version
                recordCodeDeprecatedAction(conceptNext, versionDTO, replacingConceptDTO);
            }
            // deprecate any child if exist
            if (conceptNext.getParent() != null && conceptNext.getParent().startsWith(conceptDTO.getNotation())) {
                conceptNext.setDeprecated(true);

                // store changes if not initial version
                recordCodeDeprecatedAction(conceptNext, versionDTO, null);
            }
        }
        versionService.save(versionDTO);

        // index editor after deprecation
        vocabularyService.indexEditor(vocabularyDTO);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "alert.code.deprecated", conceptDTO.getNotation())).build();
    }

    private void recordCodeDeprecatedAction(ConceptDTO conceptDTO, VersionDTO versionDTO, ConceptDTO replacingConceptDTO) {
        if( !versionDTO.isInitialVersion() ) {
            VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO(SecurityUtils.getCurrentUser(),
                versionDTO.getVocabularyId(), versionDTO.getId(), "Code deprecated", conceptDTO.getNotation() + (replacingConceptDTO != null ? (", replaced by " + replacingConceptDTO.getNotation()) : ""));
            vocabularyChangeService.save(vocabularyChangeDTO);
        }
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
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_CONCEPT + id + TO_BE_DELETED));
        VersionDTO versionDTO = versionService.findOne(conceptDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + conceptDTO.getVersionId()));
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(versionDTO.getVocabularyId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VOCABULARY + versionDTO.getVocabularyId()));

        // check if user authorized to delete VocabularyResource
        SecurityUtils.checkResourceAuthorization(ActionType.DELETE_CODE,
            vocabularyDTO.getAgencyId(), versionDTO.getLanguage());

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, conceptDTO, null, null, ActionType.DELETE_CODE.name());

        // remove parent-child link and save, orphan concept will be automatically deleted
        versionDTO.removeConcept(conceptDTO);

        // store changes if not initial version
        recordCodeDeleteAction(conceptDTO, versionDTO);

        // remove any child if exist
        Iterator<ConceptDTO> conceptIterator = versionDTO.getConcepts().iterator();
        while (conceptIterator.hasNext()) {
            ConceptDTO conceptNext = conceptIterator.next();
            if (conceptNext.getParent() != null && conceptNext.getParent().startsWith( conceptDTO.getNotation())){
                conceptIterator.remove();

                // store changes if not initial version
                recordCodeDeleteAction(conceptNext, versionDTO);
            }
        }
        versionService.save(versionDTO);

        // index editor after delete
        vocabularyService.indexEditor( vocabularyDTO );
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_CODE_NAME, conceptDTO.getNotation())).build();
    }

    private void recordCodeDeleteAction(ConceptDTO conceptDTO, VersionDTO versionDTO) {
        if( !versionDTO.isInitialVersion() ) {
            VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO(SecurityUtils.getCurrentUser(),
                versionDTO.getVocabularyId(), versionDTO.getId(), "Code deleted", conceptDTO.getNotation());
            vocabularyChangeService.save(vocabularyChangeDTO);
        }
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

        if ( !codeSnippet.getActionType().equals(ActionType.REORDER_CODE) ) {
            throw new IllegalArgumentException( "ActionType REORDER_CODE needed" );
        }

        VersionDTO versionDTO = versionService.findOne(codeSnippet.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + codeSnippet.getVersionId() ));
        VocabularyDTO vocabularyDTO = vocabularyService.findOne(versionDTO.getVocabularyId())
            .orElseThrow( () -> new EntityNotFoundException(UNABLE_TO_FIND_VOCABULARY + versionDTO.getVocabularyId() ));

        //notify the auditing mechanism
        ConceptDTO conceptDTO = conceptService.findOne(codeSnippet.getConceptId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_CONCEPT + codeSnippet.getConceptId()));
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, vocabularyDTO, versionDTO, conceptDTO, null, codeSnippet, ActionType.REORDER_CODE.name());

        // check if user authorized to reorder VocabularyResource
        SecurityUtils.checkResourceAuthorization(ActionType.REORDER_CODE,
            vocabularyDTO.getAgencyId(), versionDTO.getLanguage());

        for (int i = 0; i < codeSnippet.getConceptStructureIds().size(); i++) {
            int finalI = i;
            ConceptDTO concept = versionDTO.getConcepts().stream().filter(c -> c.getId().equals(codeSnippet.getConceptStructureIds().get(finalI))).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Unable to find code/concept with Id " + codeSnippet.getConceptStructureIds().get(finalI)));

            String newNotation = codeSnippet.getConceptStructures().get(finalI);
            int dotIndex = newNotation.lastIndexOf('.');
            if (dotIndex < 0) {
                concept.setParent( null );
            } else {
                concept.setParent( newNotation.substring(0, dotIndex));
            }
            concept.setNotation(newNotation);
            concept.setPosition(i);
        }

        // save
        versionService.save(versionDTO);
        // reindex
        vocabularyService.indexEditor(vocabularyDTO);

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
            throw new BadRequestAlertException("A new comment cannot already have an ID", ENTITY_CODE_NAME, ID_EXIST);
        }

        if (commentDTO.getVersionId() == null) {
            throw new IllegalArgumentException( "Comment need to be linked to version. Version ID is null" );
        }

        @Valid CommentDTO finalCommentDTO = commentDTO;
        VersionDTO versionDTO = versionService.findOne(commentDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + finalCommentDTO.getVersionId() ));
        if (commentDTO.getUserId() == null)
            commentDTO.setUserId( SecurityUtils.getCurrentUserId() );
        ZonedDateTime dateTime = ZonedDateTime.now();
        commentDTO.setDateTime(dateTime);
        versionDTO.addComment(commentDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, versionDTO, commentDTO, "ADD_COMMENT");

        versionDTO = versionService.save(versionDTO);

        CommentDTO newSavedComment = versionDTO.getComments().stream().filter(c -> c.getDateTime().equals(dateTime)).findFirst().orElse(null);
        if (newSavedComment != null)
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
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentDTO,
     * or with status {@code 400 (Bad Request)} if the commentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commentDTO couldn't be updated.
     */
    @PutMapping("/editors/comments")
    public ResponseEntity<CommentDTO> updateComment(@Valid @RequestBody CommentDTO commentDTO) {
        log.debug("REST request to update Comment : {}", commentDTO);
        if (commentDTO.getId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_CODE_NAME, ID_NULL);
        }

        // find version
        VersionDTO versionDTO = versionService.findOne(commentDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + commentDTO.getVersionId() ));
        // find comment from version
        CommentDTO commentFromVersion = versionDTO.getComments().stream().filter(c -> c.getId().equals( commentDTO.getId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Unable to find comment with Id " + commentDTO.getId() ));

        // update comment from version
        commentFromVersion.setDateTime( ZonedDateTime.now());
        commentFromVersion.setContent( commentDTO.getContent());
        versionService.save(versionDTO );

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, versionDTO, commentDTO, "UPDATE_COMMENT");

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
            .orElseThrow(() -> new EntityNotFoundException("Unable to find comment with Id " + id + TO_BE_DELETED));
        VersionDTO versionDTO = versionService.findOne(commentDTO.getVersionId())
            .orElseThrow(() -> new EntityNotFoundException(UNABLE_TO_FIND_VERSION + commentDTO.getId() ));
        commentDTO.setVersionId( null );
        versionDTO.removeComment(commentDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, versionDTO, commentDTO, "DELETE_COMMENT");

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
            throw new BadRequestAlertException("A new metadataValueDTO cannot already have an ID", ENTITY_CODE_NAME, ID_EXIST);
        }

        if (metadataValueDTO.getMetadataKey() == null) {
            throw new IllegalArgumentException( "MetadataValue need to be linked to metadataKey. metadataKey is null" );
        }

        MetadataFieldDTO metadataFieldDTO = metadataFieldService.findByMetadataKey(metadataValueDTO.getMetadataKey())
            .orElse(null);
        if (metadataFieldDTO == null) {
            metadataFieldDTO = new MetadataFieldDTO();
            metadataFieldDTO.setMetadataKey(metadataValueDTO.getMetadataKey());
            metadataFieldDTO.setObjectType(metadataValueDTO.getObjectType());
            metadataFieldDTO = metadataFieldService.save(metadataFieldDTO);
        }

        metadataValueDTO.setMetadataFieldId(metadataFieldDTO.getId());
        metadataFieldDTO.addMetadataValue(metadataValueDTO);

        metadataFieldDTO = metadataFieldService.save( metadataFieldDTO);

        if (metadataFieldDTO.getMetadataValues().isEmpty()) {
            throw new EntityNotFoundException( "Unable to get any MetadataValues from metadataFieldDTO "+ metadataFieldDTO.getId() );
        }

        MetadataValueDTO result = metadataFieldDTO.getMetadataValues().iterator().next();

        if (metadataFieldDTO.getMetadataValues().size() > 1) {
            result = metadataFieldDTO.getMetadataValues().stream().filter(v -> v.getValue().equals(metadataValueDTO.getValue()))
                .findFirst().orElse(result);
        }

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, metadataValueDTO, metadataFieldDTO, "CREATE_METADATA");

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
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metadataValueDTO,
     * or with status {@code 400 (Bad Request)} if the metadataValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metadataValueDTO couldn't be updated.
     */
    @PutMapping("/editors/metadatas")
    public ResponseEntity<MetadataValueDTO> updateAppMetadata(@Valid @RequestBody MetadataValueDTO metadataValueDTO) {
        log.debug("REST request to update MetadataValue : {}", metadataValueDTO);
        if (metadataValueDTO.getId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_CODE_NAME, ID_NULL);
        }

        if (metadataValueDTO.getMetadataFieldId() == null) {
            throw new IllegalArgumentException( "MetadataValue need to be linked to MetadataField. MetadataField.ID is null" );
        }

        MetadataFieldDTO metadataFieldDTO = metadataFieldService.findOne(metadataValueDTO.getMetadataFieldId() )
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataField with metadataKey " + metadataValueDTO.getMetadataKey() ));
        // find metadataValueDTO from MetadataFieldDTO
        MetadataValueDTO result  = metadataFieldDTO.getMetadataValues().stream().filter(v -> v.getId().equals(metadataValueDTO.getId())).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataValue with Id " + metadataValueDTO.getId() ));

        result.setIdentifier(metadataValueDTO.getIdentifier());
        result.setPosition(metadataValueDTO.getPosition());
        result.setValue(metadataValueDTO.getValue());
        metadataFieldService.save(metadataFieldDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, metadataValueDTO, metadataFieldDTO, "UPDATE_METADATA");

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
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataValue with Id " + id + TO_BE_DELETED));
        MetadataFieldDTO metadataFieldDTO = metadataFieldService.findOne(metadataValueDTO.getMetadataFieldId())
            .orElseThrow(() -> new EntityNotFoundException("Unable to find metadataField with Id " + metadataValueDTO.getMetadataFieldId() ));

        // automatically remove metadataValue
        metadataValueDTO.setMetadataFieldId( null );
        metadataFieldDTO.removeMetadataValue(metadataValueDTO);
        metadataFieldService.save(metadataFieldDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, metadataValueDTO, metadataFieldDTO, "DELETE_METADATA");

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true,
            ENTITY_METADATAVALUE_NAME, id.toString())).build();
    }

    /**
     *  get a SKOS file of vocabulary {cv} with version {v} with included versions {lv} from Editor DB
     *
     * @param cv controlled vocabulary
     * @param v controlled vocabulary version
     * @param lv included version to be exported with format language_version e.g en-1.0_de-1.0.1
     */
    @GetMapping("/editors/download/rdf/{cv}/{v}")
    public ResponseEntity<Resource> getVocabularyInSkos(
        HttpServletRequest request, @PathVariable String cv, @PathVariable String v,
        @RequestParam(name = "lv", required = true) String lv
    ) {
        log.debug("Editor REST request to get a SKOS-RDF file of vocabulary {} with version {} with included versions {}", cv, v, lv);

        var outputStream = new FastByteArrayOutputStream();
        String fileName = vocabularyService.generateVocabularyEditorFileDownload( cv, v, lv, ExportService.DownloadType.SKOS, ResourceUtils.getURLWithContextPath(request), outputStream );

        InputStreamResource resource = new InputStreamResource(outputStream.getInputStream());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName);
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(ResourceUtils.MEDIATYPE_RDF)
            .body(resource);
    }

    /**
     *  get a PDF file of vocabulary {cv} with version {v} with included versions {lv} from Editor DB
     *
     * @param cv controlled vocabulary
     * @param v controlled vocabulary version
     * @param lv included version to be exported with format language_version e.g en-1.0_de-1.0.1
     */
    @GetMapping("/editors/download/pdf/{cv}/{v}")
    public ResponseEntity<Resource> getVocabularyInPdf(
        HttpServletRequest request, @PathVariable String cv, @PathVariable String v,
        @RequestParam(name = "lv", required = true) String lv
    ) {
        log.debug("Editor REST request to get a PDF file of vocabulary {} with version {} with included versions {}", cv, v, lv);

        var outputStream = new FastByteArrayOutputStream();
        String fileName = vocabularyService.generateVocabularyEditorFileDownload( cv, v, lv, ExportService.DownloadType.PDF, ResourceUtils.getURLWithContextPath(request), outputStream );

        InputStreamResource resource = new InputStreamResource(outputStream.getInputStream());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName);
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/pdf"))
            .body(resource);
    }

    /**
     *  get a HTML file of vocabulary {cv} with version {v} with included versions {lv} from Editor DB
     *
     * @param cv controlled vocabulary
     * @param v controlled vocabulary version
     * @param lv included version to be exported with format language_version e.g en-1.0_de-1.0.1
     */
    @GetMapping("/editors/download/html/{cv}/{v}")
    public ResponseEntity<Resource> getVocabularyInHtml(
        HttpServletRequest request, @PathVariable String cv, @PathVariable String v,
        @RequestParam(name = "lv", required = true) String lv
    ) {
        log.debug("Editor REST request to get a HTML file of vocabulary {} with version {} with included versions {}", cv, v, lv);

        var outputStream = new FastByteArrayOutputStream();
        String fileName = vocabularyService.generateVocabularyEditorFileDownload( cv, v, lv, ExportService.DownloadType.HTML, ResourceUtils.getURLWithContextPath(request), outputStream );

        InputStreamResource resource = new InputStreamResource(outputStream.getInputStream());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName);
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("text/html"))
            .body(resource);
    }

    /**
     * {@code GET  /search} : get all the vocabularies from elasticsearch.
     *

     * @param q the query term.
     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping("/editors/search")
    public ResponseEntity<CvResult> getAllVocabularies(@RequestParam(name = "q", required = false) String q,
                                                       @RequestParam(name = "f", required = false) String f,
                                                       Pageable pageable) {
        log.debug("REST request to get a page of Vocabularies");
        if (q == null)
            q = "";
        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(q, f, pageable, SearchScope.EDITORSEARCH);
        vocabularyService.search(esq);
        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), vocabulariesPage);
        return ResponseEntity.ok().headers(headers).body( VocabularyUtils.mapResultToCvResult(esq, vocabulariesPage) );
    }

}
