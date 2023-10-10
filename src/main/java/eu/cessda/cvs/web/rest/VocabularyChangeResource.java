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

import eu.cessda.cvs.service.VocabularyChangeService;
import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import eu.cessda.cvs.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing {@link eu.cessda.cvs.domain.VocabularyChange}.
 */
@RestController
@RequestMapping("/api")
public class VocabularyChangeResource {

    private final Logger log = LoggerFactory.getLogger(VocabularyChangeResource.class);

    private static final String ENTITY_NAME = "vocabularyChange";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyChangeService vocabularyChangeService;

    public VocabularyChangeResource(VocabularyChangeService vocabularyChangeService) {
        this.vocabularyChangeService = vocabularyChangeService;
    }

    /**
     * {@code POST  /vocabulary-changes} : Create a new vocabularyChange.
     *
     * @param vocabularyChangeDTO the vocabularyChangeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vocabularyChangeDTO, or with status {@code 400 (Bad Request)} if the vocabularyChange has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vocabulary-changes")
    public ResponseEntity<VocabularyChangeDTO> createVocabularyChange(@Valid @RequestBody VocabularyChangeDTO vocabularyChangeDTO) throws URISyntaxException {
        log.debug("REST request to save VocabularyChange : {}", vocabularyChangeDTO);
        if (vocabularyChangeDTO.getId() != null) {
            throw new BadRequestAlertException("A new vocabularyChange cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VocabularyChangeDTO result = vocabularyChangeService.save(vocabularyChangeDTO);
        return ResponseEntity.created(new URI("/api/vocabulary-changes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /vocabulary-changes} : Updates an existing vocabularyChange.
     *
     * @param vocabularyChangeDTO the vocabularyChangeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vocabularyChangeDTO,
     * or with status {@code 400 (Bad Request)} if the vocabularyChangeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vocabularyChangeDTO couldn't be updated.
     */
    @PutMapping("/vocabulary-changes")
    public ResponseEntity<VocabularyChangeDTO> updateVocabularyChange(@Valid @RequestBody VocabularyChangeDTO vocabularyChangeDTO)
    {
        log.debug("REST request to update VocabularyChange : {}", vocabularyChangeDTO);
        if (vocabularyChangeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VocabularyChangeDTO result = vocabularyChangeService.save(vocabularyChangeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vocabularyChangeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /vocabulary-changes} : get all the vocabularyChanges.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularyChanges in body.
     */
    @GetMapping("/vocabulary-changes")
    public ResponseEntity<List<VocabularyChangeDTO>> getAllVocabularyChanges(Pageable pageable) {
        log.debug("REST request to get a page of VocabularyChanges");
        Page<VocabularyChangeDTO> page = vocabularyChangeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vocabulary-changes/:id} : get the "id" vocabularyChange.
     *
     * @param id the id of the vocabularyChangeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vocabularyChangeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vocabulary-changes/{id}")
    public ResponseEntity<VocabularyChangeDTO> getVocabularyChange(@PathVariable Long id) {
        log.debug("REST request to get VocabularyChange : {}", id);
        Optional<VocabularyChangeDTO> vocabularyChangeDTO = vocabularyChangeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vocabularyChangeDTO);
    }

    /**
     * {@code DELETE  /vocabulary-changes/:id} : delete the "id" vocabularyChange.
     *
     * @param id the id of the vocabularyChangeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vocabulary-changes/{id}")
    public ResponseEntity<Void> deleteVocabularyChange(@PathVariable Long id) {
        log.debug("REST request to delete VocabularyChange : {}", id);
        vocabularyChangeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/vocabulary-changes?query=:query} : search for the vocabularyChange corresponding
     * to the query.
     *
     * @param query the query of the vocabularyChange search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/vocabulary-changes")
    public ResponseEntity<List<VocabularyChangeDTO>> searchVocabularyChanges(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of VocabularyChanges for query {}", query);
        Page<VocabularyChangeDTO> page = vocabularyChangeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vocabulary-changes/version-id/:versionId} : get all the vocabularyChanges by versionId.
     *
     * @param versionId the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularyChanges in body.
     */
    @GetMapping("/vocabulary-changes/version-id/{versionId}")
    public ResponseEntity<List<VocabularyChangeDTO>> getAllVocabularyChanges(@PathVariable Long versionId) {
        log.debug("REST request to get list of VocabularyChanges by versionId {}", versionId);
        List<VocabularyChangeDTO> vocabularyChangeDTOS = vocabularyChangeService.findByVersionId(versionId);
        return ResponseEntity.ok().body(vocabularyChangeDTOS);
    }
}
