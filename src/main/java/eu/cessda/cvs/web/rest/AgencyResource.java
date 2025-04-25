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
import eu.cessda.cvs.domain.search.AgencyStat;
import eu.cessda.cvs.repository.search.AgencyStatSearchRepository;
import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.web.rest.errors.BadRequestAlertException;
import eu.cessda.cvs.security.SecurityUtils;

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
 * REST controller for managing {@link eu.cessda.cvs.domain.Agency}.
 */
@RestController
@RequestMapping("/api")
public class AgencyResource {

    @Autowired
    private AuditEventPublisher auditPublisher;

    private final Logger log = LoggerFactory.getLogger(AgencyResource.class);

    private static final String ENTITY_NAME = "agency";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgencyService agencyService;

    private final VocabularyService vocabularyService;

    private final AgencyStatSearchRepository agencyStatSearchRepository;

    public AgencyResource(AgencyService agencyService, VocabularyService vocabularyService,
                          AgencyStatSearchRepository agencyStatSearchRepository) {
        this.agencyService = agencyService;
        this.vocabularyService = vocabularyService;
        this.agencyStatSearchRepository = agencyStatSearchRepository;
    }

    /**
     * {@code POST  /agencies} : Create a new agency.
     *
     * @param agencyDTO the agencyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agencyDTO, or with status {@code 400 (Bad Request)} if the agency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/agencies")
    public ResponseEntity<AgencyDTO> createAgency(@Valid @RequestBody AgencyDTO agencyDTO) throws URISyntaxException {
        log.debug("REST request to save Agency : {}", agencyDTO);
        if (agencyDTO.getId() != null) {
            throw new BadRequestAlertException("A new agency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AgencyDTO result = agencyService.save(agencyDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, agencyDTO, "AGENCY_CREATED");

        return ResponseEntity.created(new URI("/api/agencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /agencies} : Updates an existing agency.
     *
     * @param agencyDTO the agencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agencyDTO,
     * or with status {@code 400 (Bad Request)} if the agencyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agencyDTO couldn't be updated.
     */
    @PutMapping("/agencies")
    public ResponseEntity<AgencyDTO> updateAgency(@Valid @RequestBody AgencyDTO agencyDTO) {
        log.debug("REST request to update Agency : {}", agencyDTO);
        if (agencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        vocabularyService.updateVocabularyUri(agencyDTO.getId(), agencyDTO.getUri(), agencyDTO.getUriCode());
        vocabularyService.updateVocabularyLogo(agencyDTO.getId(), agencyDTO.getLogopath());

        AgencyDTO result = agencyService.save(agencyDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, agencyDTO, "AGENCY_UPDATED");

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agencyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /agencies} : get all the agencies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of agencies in body.
     */
    @GetMapping("/agencies")
    public ResponseEntity<List<AgencyDTO>> getAllAgencies(Pageable pageable) {
        log.debug("REST request to get a page of Agencies");
        Page<AgencyDTO> page = agencyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /agencies/:id} : get the "id" agency.
     *
     * @param id the id of the agencyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/agencies/{id}")
    public ResponseEntity<AgencyDTO> getAgency(@PathVariable Long id) {
        log.debug("REST request to get Agency : {}", id);
        Optional<AgencyDTO> agencyDTO = agencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(agencyDTO);
    }

    /**
     * {@code DELETE  /agencies/:id} : delete the "id" agency.
     *
     * @param id the id of the agencyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/agencies/{id}")
    public ResponseEntity<Void> deleteAgency(@PathVariable Long id) {
        log.debug("REST request to delete Agency : {}", id);
        
        //notify the auditing mechanism
        Optional<AgencyDTO> agencyDTO = agencyService.findOne(id);
        AgencyDTO agencyDTOTemp = null;
        if (agencyDTO.isPresent()) {
            agencyDTOTemp = agencyDTO.get();
        }
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, agencyDTOTemp, "AGENCY_DELETED");

        agencyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/agencies?query=:query} : search for the agency corresponding
     * to the query.
     *
     * @param query the query of the agency search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/agencies")
    public ResponseEntity<List<AgencyStat>> searchAgencies(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Agencies for query {}", query);
        Page<AgencyStat> page = agencyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /agencies-statistic/:id} : get the "id" agency.
     *
     * @param id the id of the agencyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/agencies-stat/{id}")
    public ResponseEntity<AgencyStat> getAgencyStatistic(@PathVariable Long id) {
        log.debug("REST request to get Agency : {}", id);
        Optional<AgencyStat> agencyStat = agencyStatSearchRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(agencyStat);
    }
}
