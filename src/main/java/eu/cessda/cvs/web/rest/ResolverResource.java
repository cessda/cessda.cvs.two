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

import eu.cessda.cvs.service.ResolverService;
import eu.cessda.cvs.service.dto.ResolverDTO;
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
 * REST controller for managing {@link eu.cessda.cvs.domain.Resolver}.
 */
@RestController
@RequestMapping("/api")
public class ResolverResource {

    private final Logger log = LoggerFactory.getLogger(ResolverResource.class);

    private static final String ENTITY_NAME = "resolver";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResolverService resolverService;

    public ResolverResource(ResolverService resolverService) {
        this.resolverService = resolverService;
    }

    /**
     * {@code POST  /resolvers} : Create a new resolver.
     *
     * @param resolverDTO the resolverDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resolverDTO, or with status {@code 400 (Bad Request)} if the resolver has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/resolvers")
    public ResponseEntity<ResolverDTO> createResolver(@Valid @RequestBody ResolverDTO resolverDTO) throws URISyntaxException {
        log.debug("REST request to save Resolver : {}", resolverDTO);
        if (resolverDTO.getId() != null) {
            throw new BadRequestAlertException("A new resolver cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResolverDTO result = resolverService.save(resolverDTO);
        return ResponseEntity.created(new URI("/api/resolvers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /resolvers} : Updates an existing resolver.
     *
     * @param resolverDTO the resolverDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resolverDTO,
     * or with status {@code 400 (Bad Request)} if the resolverDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resolverDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/resolvers")
    public ResponseEntity<ResolverDTO> updateResolver(@Valid @RequestBody ResolverDTO resolverDTO) throws URISyntaxException {
        log.debug("REST request to update Resolver : {}", resolverDTO);
        if (resolverDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ResolverDTO result = resolverService.save(resolverDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resolverDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /resolvers} : get all the resolvers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of resolvers in body.
     */
    @GetMapping("/resolvers")
    public ResponseEntity<List<ResolverDTO>> getAllResolvers(Pageable pageable) {
        log.debug("REST request to get a page of Resolvers");
        Page<ResolverDTO> page = resolverService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /resolvers/:id} : get the "id" resolver.
     *
     * @param id the id of the resolverDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resolverDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/resolvers/{id}")
    public ResponseEntity<ResolverDTO> getResolver(@PathVariable Long id) {
        log.debug("REST request to get Resolver : {}", id);
        Optional<ResolverDTO> resolverDTO = resolverService.findOne(id);
        return ResponseUtil.wrapOrNotFound(resolverDTO);
    }

    /**
     * {@code DELETE  /resolvers/:id} : delete the "id" resolver.
     *
     * @param id the id of the resolverDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/resolvers/{id}")
    public ResponseEntity<Void> deleteResolver(@PathVariable Long id) {
        log.debug("REST request to delete Resolver : {}", id);
        resolverService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/resolvers?query=:query} : search for the resolver corresponding
     * to the query.
     *
     * @param query the query of the resolver search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/resolvers")
    public ResponseEntity<List<ResolverDTO>> searchResolvers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Resolvers for query {}", query);
        Page<ResolverDTO> page = resolverService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
