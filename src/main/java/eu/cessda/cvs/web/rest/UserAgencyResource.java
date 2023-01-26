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

import eu.cessda.cvs.service.UserAgencyService;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link eu.cessda.cvs.domain.UserAgency}.
 */
@RestController
@RequestMapping("/api")
public class UserAgencyResource {

    private final Logger log = LoggerFactory.getLogger(UserAgencyResource.class);

    private static final String ENTITY_NAME = "userAgency";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserAgencyService userAgencyService;

    public UserAgencyResource(UserAgencyService userAgencyService) {
        this.userAgencyService = userAgencyService;
    }

    /**
     * {@code POST  /user-agencies} : Create a new userAgency.
     *
     * @param userAgencyDTO the userAgencyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAgencyDTO, or with status {@code 400 (Bad Request)} if the userAgency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-agencies")
    public ResponseEntity<UserAgencyDTO> createUserAgency(@RequestBody UserAgencyDTO userAgencyDTO) throws URISyntaxException {
        log.debug("REST request to save UserAgency : {}", userAgencyDTO);
        if (userAgencyDTO.getId() != null) {
            throw new BadRequestAlertException("A new userAgency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserAgencyDTO result = userAgencyService.save(userAgencyDTO);
        return ResponseEntity.created(new URI("/api/user-agencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-agencies} : Updates an existing userAgency.
     *
     * @param userAgencyDTO the userAgencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAgencyDTO,
     * or with status {@code 400 (Bad Request)} if the userAgencyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userAgencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-agencies")
    public ResponseEntity<UserAgencyDTO> updateUserAgency(@RequestBody UserAgencyDTO userAgencyDTO) throws URISyntaxException {
        log.debug("REST request to update UserAgency : {}", userAgencyDTO);
        if (userAgencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserAgencyDTO result = userAgencyService.save(userAgencyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAgencyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-agencies} : get all the userAgencies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userAgencies in body.
     */
    @GetMapping("/user-agencies")
    public ResponseEntity<List<UserAgencyDTO>> getAllUserAgencies(Pageable pageable) {
        log.debug("REST request to get a page of UserAgencies");
        Page<UserAgencyDTO> page = userAgencyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-agencies/:id} : get the "id" userAgency.
     *
     * @param id the id of the userAgencyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAgencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-agencies/{id}")
    public ResponseEntity<UserAgencyDTO> getUserAgency(@PathVariable Long id) {
        log.debug("REST request to get UserAgency : {}", id);
        Optional<UserAgencyDTO> userAgencyDTO = userAgencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userAgencyDTO);
    }

    /**
     * {@code DELETE  /user-agencies/:id} : delete the "id" userAgency.
     *
     * @param id the id of the userAgencyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-agencies/{id}")
    public ResponseEntity<Void> deleteUserAgency(@PathVariable Long id) {
        log.debug("REST request to delete UserAgency : {}", id);
        userAgencyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/user-agencies?query=:query} : search for the userAgency corresponding
     * to the query.
     *
     * @param query the query of the userAgency search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-agencies")
    public ResponseEntity<List<UserAgencyDTO>> searchUserAgencies(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of UserAgencies for query {}", query);
        Page<UserAgencyDTO> page = userAgencyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
