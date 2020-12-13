package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.service.MetadataValueService;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
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
 * REST controller for managing {@link eu.cessda.cvs.domain.MetadataValue}.
 */
@RestController
@RequestMapping("/api")
public class MetadataValueResource {

    private final Logger log = LoggerFactory.getLogger(MetadataValueResource.class);

    private static final String ENTITY_NAME = "metadataValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MetadataValueService metadataValueService;

    public MetadataValueResource(MetadataValueService metadataValueService) {
        this.metadataValueService = metadataValueService;
    }

    /**
     * {@code POST  /metadata-values} : Create a new metadataValue.
     *
     * @param metadataValueDTO the metadataValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metadataValueDTO, or with status {@code 400 (Bad Request)} if the metadataValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/metadata-values")
    public ResponseEntity<MetadataValueDTO> createMetadataValue(@RequestBody MetadataValueDTO metadataValueDTO) throws URISyntaxException {
        log.debug("REST request to save MetadataValue : {}", metadataValueDTO);
        if (metadataValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new metadataValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MetadataValueDTO result = metadataValueService.save(metadataValueDTO);
        return ResponseEntity.created(new URI("/api/metadata-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /metadata-values} : Updates an existing metadataValue.
     *
     * @param metadataValueDTO the metadataValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metadataValueDTO,
     * or with status {@code 400 (Bad Request)} if the metadataValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metadataValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/metadata-values")
    public ResponseEntity<MetadataValueDTO> updateMetadataValue(@RequestBody MetadataValueDTO metadataValueDTO) throws URISyntaxException {
        log.debug("REST request to update MetadataValue : {}", metadataValueDTO);
        if (metadataValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MetadataValueDTO result = metadataValueService.save(metadataValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metadataValueDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /metadata-values} : get all the metadataValues.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metadataValues in body.
     */
    @GetMapping("/metadata-values")
    public ResponseEntity<List<MetadataValueDTO>> getAllMetadataValues(Pageable pageable) {
        log.debug("REST request to get a page of MetadataValues");
        Page<MetadataValueDTO> page = metadataValueService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /metadata-values/:id} : get the "id" metadataValue.
     *
     * @param id the id of the metadataValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metadataValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/metadata-values/{id}")
    public ResponseEntity<MetadataValueDTO> getMetadataValue(@PathVariable Long id) {
        log.debug("REST request to get MetadataValue : {}", id);
        Optional<MetadataValueDTO> metadataValueDTO = metadataValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(metadataValueDTO);
    }

    /**
     * {@code DELETE  /metadata-values/:id} : delete the "id" metadataValue.
     *
     * @param id the id of the metadataValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/metadata-values/{id}")
    public ResponseEntity<Void> deleteMetadataValue(@PathVariable Long id) {
        log.debug("REST request to delete MetadataValue : {}", id);
        metadataValueService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/metadata-values?query=:query} : search for the metadataValue corresponding
     * to the query.
     *
     * @param query the query of the metadataValue search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/metadata-values")
    public ResponseEntity<List<MetadataValueDTO>> searchMetadataValues(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MetadataValues for query {}", query);
        Page<MetadataValueDTO> page = metadataValueService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
