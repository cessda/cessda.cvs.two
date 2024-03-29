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

import eu.cessda.cvs.config.audit.AuditEventPublisher;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.InsufficientVocabularyAuthorityException;
import eu.cessda.cvs.service.VocabularyAlreadyExistException;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/api")
public class VocabularyResource {

    @Autowired
    private AuditEventPublisher auditPublisher;

    private final Logger log = LoggerFactory.getLogger(VocabularyResource.class);

    private static final String ENTITY_NAME = "vocabulary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyService vocabularyService;

    public VocabularyResource(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    /**
     * {@code POST  /vocabularies} : Create a new vocabulary.
     *
     * @param vocabularyDTO the vocabularyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vocabularyDTO, or with status {@code 400 (Bad Request)} if the vocabulary has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws VocabularyAlreadyExistException {@code 400 (Bad Request)} if the notation is already exist/Vocabulary exist.
     * @throws InsufficientVocabularyAuthorityException {@code 403 (Forbidden)} if the user does not have sufficiet rights to access the resource.
     */
    @PostMapping("/vocabularies")
    public ResponseEntity<VocabularyDTO> createVocabulary(@Valid @RequestBody VocabularyDTO vocabularyDTO) throws URISyntaxException {
        log.debug("REST request to save Vocabulary : {}", vocabularyDTO);
        if (vocabularyDTO.getId() != null) {
            throw new BadRequestAlertException("A new vocabulary cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VocabularyDTO result = vocabularyService.save(vocabularyDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, result, null, null, "CREATE_VOCABULARY");

        return ResponseEntity.created(new URI("/api/vocabularies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /vocabularies} : Updates an existing vocabulary.
     *
     * @param vocabularyDTO the vocabularyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vocabularyDTO,
     * or with status {@code 400 (Bad Request)} if the vocabularyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vocabularyDTO couldn't be updated.
     */
    @PutMapping("/vocabularies")
    public ResponseEntity<VocabularyDTO> updateVocabulary(@Valid @RequestBody VocabularyDTO vocabularyDTO) {
        log.debug("REST request to update Vocabulary : {}", vocabularyDTO);
        if (vocabularyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VocabularyDTO result = vocabularyService.save(vocabularyDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, result, null, null, "UPDATE_VOCABULARY");

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vocabularyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /vocabularies} : get all the vocabularies.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping("/vocabularies")
    public ResponseEntity<List<VocabularyDTO>> getAllVocabularies(Pageable pageable) {
        log.debug("REST request to get a page of Vocabularies");
        Page<VocabularyDTO> page = vocabularyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vocabularies/:id} : get the "id" vocabulary.
     *
     * @param id the id of the vocabularyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vocabularyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vocabularies/{id}")
    public ResponseEntity<VocabularyDTO> getVocabulary(@PathVariable Long id) {
        log.debug("REST request to get Vocabulary : {}", id);
        Optional<VocabularyDTO> vocabularyDTO = vocabularyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vocabularyDTO);
    }

    /**
     * {@code GET  /vocabularies/:notation/:versionNumber}
     *
     * @param notation the notation of the vocabularyDTO to retrieve.
     * @param versionNumber the versionNumber of the vocabularyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vocabularyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vocabularies/{notation}/{versionNumber}")
    public ResponseEntity<VocabularyDTO> getVocabularyByNotationAndVersion(@PathVariable String notation, @PathVariable String versionNumber) {
        log.debug("REST request to get Vocabulary by notation {} and by version {}", notation, versionNumber);
        VocabularyDTO vocabularyDTO = vocabularyService.getVocabularyByNotationAndVersion(notation, versionNumber, false);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable( vocabularyDTO ));
    }

    /**
     * {@code DELETE  /vocabularies/:id} : delete the "id" vocabulary.
     *
     * @param id the id of the vocabularyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vocabularies/{id}")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable Long id) {
        log.debug("REST request to delete Vocabulary : {}", id);

        //notify the auditing mechanism
        Optional<VocabularyDTO> result = vocabularyService.findOne(id);
        VocabularyDTO vocabulary = new VocabularyDTO();
        if (result.isPresent()) {
            vocabulary = result.get();
        }
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, vocabulary, null, null, "DELETE_VOCABULARY");

        vocabularyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
