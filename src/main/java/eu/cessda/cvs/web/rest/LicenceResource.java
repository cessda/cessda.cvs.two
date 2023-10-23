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
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.LicenceService;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.service.dto.LicenceDTO;
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
 * REST controller for managing {@link eu.cessda.cvs.domain.Licence}.
 */
@RestController
@RequestMapping("/api")
public class LicenceResource {

    @Autowired
    private AuditEventPublisher auditPublisher;

    private final Logger log = LoggerFactory.getLogger(LicenceResource.class);

    private static final String ENTITY_NAME = "licence";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LicenceService licenceService;

    public LicenceResource(LicenceService licenceService) {
        this.licenceService = licenceService;
    }

    /**
     * {@code POST  /licences} : Create a new licence.
     *
     * @param licenceDTO the licenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new licenceDTO, or with status {@code 400 (Bad Request)} if the licence has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/licences")
    public ResponseEntity<LicenceDTO> createLicence(@Valid @RequestBody LicenceDTO licenceDTO) throws URISyntaxException {
        log.debug("REST request to save Licence : {}", licenceDTO);
        if (licenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new licence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LicenceDTO result = licenceService.save(licenceDTO);
        
        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, result, licenceDTO, "CREATE_LICENCE");
        return ResponseEntity.created(new URI("/api/licences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /licences} : Updates an existing licence.
     *
     * @param licenceDTO the licenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated licenceDTO,
     * or with status {@code 400 (Bad Request)} if the licenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the licenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/licences")
    public ResponseEntity<LicenceDTO> updateLicence(@Valid @RequestBody LicenceDTO licenceDTO) throws URISyntaxException {
        log.debug("REST request to update Licence : {}", licenceDTO);
        if (licenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LicenceDTO result = licenceService.save(licenceDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, result, licenceDTO, "UPDATE_LICENCE");
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, licenceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /licences} : get all the licences.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of licences in body.
     */
    @GetMapping("/licences")
    public ResponseEntity<List<LicenceDTO>> getAllLicences(Pageable pageable) {
        log.debug("REST request to get a page of Licences");
        Page<LicenceDTO> page = licenceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /licences/:id} : get the "id" licence.
     *
     * @param id the id of the licenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the licenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/licences/{id}")
    public ResponseEntity<LicenceDTO> getLicence(@PathVariable Long id) {
        log.debug("REST request to get Licence : {}", id);
        Optional<LicenceDTO> licenceDTO = licenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(licenceDTO);
    }

    /**
     * {@code DELETE  /licences/:id} : delete the "id" licence.
     *
     * @param id the id of the licenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/licences/{id}")
    public ResponseEntity<Void> deleteLicence(@PathVariable Long id) {
        log.debug("REST request to delete Licence : {}", id);

        //notify the auditing mechanism
        Optional<LicenceDTO> licenceyDTO = licenceService.findOne(id);
        LicenceDTO licenceDTOTemp = null;
        if (licenceyDTO.isPresent()) {
            licenceDTOTemp = licenceyDTO.get();
        }
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, licenceDTOTemp, null, "DELETE_LICENCE");

        licenceService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/licences?query=:query} : search for the licence corresponding
     * to the query.
     *
     * @param query the query of the licence search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/licences")
    public ResponseEntity<List<LicenceDTO>> searchLicences(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Licences for query {}", query);
        Page<LicenceDTO> page = licenceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
