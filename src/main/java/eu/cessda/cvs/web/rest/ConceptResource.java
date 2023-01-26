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

import eu.cessda.cvs.service.ConceptService;
import eu.cessda.cvs.service.dto.ConceptDTO;
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
 * REST controller for managing {@link eu.cessda.cvs.domain.Concept}.
 */
@RestController
@RequestMapping("/api")
public class ConceptResource {

    private final Logger log = LoggerFactory.getLogger(ConceptResource.class);

    private static final String ENTITY_NAME = "concept";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConceptService conceptService;

    public ConceptResource(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    /**
     * {@code POST  /concepts} : Create a new concept.
     *
     * @param conceptDTO the conceptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conceptDTO, or with status {@code 400 (Bad Request)} if the concept has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/concepts")
    public ResponseEntity<ConceptDTO> createConcept(@Valid @RequestBody ConceptDTO conceptDTO) throws URISyntaxException {
        log.debug("REST request to save Concept : {}", conceptDTO);
        if (conceptDTO.getId() != null) {
            throw new BadRequestAlertException("A new concept cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ConceptDTO result = conceptService.save(conceptDTO);
        return ResponseEntity.created(new URI("/api/concepts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /concepts} : Updates an existing concept.
     *
     * @param conceptDTO the conceptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conceptDTO,
     * or with status {@code 400 (Bad Request)} if the conceptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conceptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/concepts")
    public ResponseEntity<ConceptDTO> updateConcept(@Valid @RequestBody ConceptDTO conceptDTO) throws URISyntaxException {
        log.debug("REST request to update Concept : {}", conceptDTO);
        if (conceptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ConceptDTO result = conceptService.save(conceptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, conceptDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /concepts} : get all the concepts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of concepts in body.
     */
    @GetMapping("/concepts")
    public ResponseEntity<List<ConceptDTO>> getAllConcepts(Pageable pageable) {
        log.debug("REST request to get a page of Concepts");
        Page<ConceptDTO> page = conceptService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /concepts/:id} : get the "id" concept.
     *
     * @param id the id of the conceptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conceptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/concepts/{id}")
    public ResponseEntity<ConceptDTO> getConcept(@PathVariable Long id) {
        log.debug("REST request to get Concept : {}", id);
        Optional<ConceptDTO> conceptDTO = conceptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(conceptDTO);
    }

    /**
     * {@code DELETE  /concepts/:id} : delete the "id" concept.
     *
     * @param id the id of the conceptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/concepts/{id}")
    public ResponseEntity<Void> deleteConcept(@PathVariable Long id) {
        log.debug("REST request to delete Concept : {}", id);
        conceptService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/concepts?query=:query} : search for the concept corresponding
     * to the query.
     *
     * @param query the query of the concept search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/concepts")
    public ResponseEntity<List<ConceptDTO>> searchConcepts(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Concepts for query {}", query);
        Page<ConceptDTO> page = conceptService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
