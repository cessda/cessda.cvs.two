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

import eu.cessda.cvs.service.ExportService;
import eu.cessda.cvs.service.MetadataFieldService;
import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.utils.VocabularyUtils;
import eu.cessda.cvs.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link eu.cessda.cvs.domain.MetadataField}.
 */
@RestController
@RequestMapping("/api")
public class MetadataFieldResource {

    private final Logger log = LoggerFactory.getLogger(MetadataFieldResource.class);

    public static final String ATTACHMENT_FILENAME = "attachment; filename=";
    private static final String ENTITY_NAME = "metadataField";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MetadataFieldService metadataFieldService;

    private final ExportService exportService;

    public MetadataFieldResource(MetadataFieldService metadataFieldService, ExportService exportService) {
        this.metadataFieldService = metadataFieldService;
        this.exportService = exportService;
    }

    /**
     * {@code POST  /metadata-fields} : Create a new metadataField.
     *
     * @param metadataFieldDTO the metadataFieldDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metadataFieldDTO, or with status {@code 400 (Bad Request)} if the metadataField has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/metadata-fields")
    public ResponseEntity<MetadataFieldDTO> createMetadataField(@Valid @RequestBody MetadataFieldDTO metadataFieldDTO) throws URISyntaxException {
        log.debug("REST request to save MetadataField : {}", metadataFieldDTO);
        if (metadataFieldDTO.getId() != null) {
            throw new BadRequestAlertException("A new metadataField cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MetadataFieldDTO result = metadataFieldService.save(metadataFieldDTO);
        return ResponseEntity.created(new URI("/api/metadata-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /metadata-fields} : Updates an existing metadataField.
     *
     * @param metadataFieldDTO the metadataFieldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metadataFieldDTO,
     * or with status {@code 400 (Bad Request)} if the metadataFieldDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metadataFieldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/metadata-fields")
    public ResponseEntity<MetadataFieldDTO> updateMetadataField(@Valid @RequestBody MetadataFieldDTO metadataFieldDTO) {
        log.debug("REST request to update MetadataField : {}", metadataFieldDTO);
        if (metadataFieldDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MetadataFieldDTO result = metadataFieldService.save(metadataFieldDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, metadataFieldDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /metadata-fields} : get all the metadataFields.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metadataFields in body.
     */
    @GetMapping("/metadata-fields")
    public ResponseEntity<List<MetadataFieldDTO>> getAllMetadataFields(Pageable pageable) {
        log.debug("REST request to get a page of MetadataFields");
        Page<MetadataFieldDTO> page = metadataFieldService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /metadata-fields/:id} : get the "id" metadataField.
     *
     * @param id the id of the metadataFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metadataFieldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/metadata-fields/{id}")
    public ResponseEntity<MetadataFieldDTO> getMetadataField(@PathVariable Long id) {
        log.debug("REST request to get MetadataField : {}", id);
        Optional<MetadataFieldDTO> metadataFieldDTO = metadataFieldService.findOne(id);
        return ResponseUtil.wrapOrNotFound(metadataFieldDTO);
    }

    /**
     * {@code GET  /metadata-fields/metadata-key/:metadataKey} : get the "metadataKey" metadataField.
     *
     * @param metadataKey the metadataKey of the metadataFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metadataFieldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/metadata-fields/metadata-key/{metadataKey}")
    public ResponseEntity<MetadataFieldDTO> getMetadataFieldByMetadataKey(@PathVariable String metadataKey) {
        log.debug("REST request to get MetadataField by metadataKey : {}", metadataKey);
        Optional<MetadataFieldDTO> metadataFieldDTO = metadataFieldService.findOneByMetadataKey(metadataKey);
        // sort by position
        if( metadataFieldDTO.isPresent() ) {
            final LinkedHashSet<MetadataValueDTO> metadataValueDTOS = metadataFieldDTO.get().getMetadataValues().stream().sorted(Comparator.comparing(MetadataValueDTO::getPosition))
                .collect(Collectors.toCollection(LinkedHashSet::new));
            metadataFieldDTO.get().setMetadataValues(metadataValueDTOS);
        }
        return ResponseUtil.wrapOrNotFound(metadataFieldDTO);
    }

    /**
     * {@code GET  /metadata-fields/download/:metadataKey} : get the "metadataKey" metadataField.
     *
     * @param metadataKey the metadataKey of the metadataFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metadataFieldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping( path = "/metadata-fields/download/{metadataKey}", produces = { MediaType.APPLICATION_PDF_VALUE, ExportService.MEDIATYPE_WORD_VALUE })
    public ResponseEntity<StreamingResponseBody> getMetadataPdfFileByMetadataKey( @RequestHeader("Accept") String accept, @PathVariable String metadataKey)
    {
        var mediaType = MediaType.parseMediaType( accept );
        var downloadType = ExportService.DownloadType.fromMediaType( mediaType ).orElseThrow();
        Optional<MetadataFieldDTO> metadataFieldDTO = metadataFieldService.findOneByMetadataKey( metadataKey );

        Map<String, Object> map = new HashMap<>();
        // sort by position
        if( metadataFieldDTO.isPresent() ) {
            List<MetadataValueDTO> metadataValueDTOS = new ArrayList<>( metadataFieldDTO.get().getMetadataValues() );
            metadataValueDTOS.sort( Comparator.comparing( MetadataValueDTO::getPosition ) );
            var sections = new ArrayList<>();
            for ( MetadataValueDTO mv : metadataValueDTOS )
            {
                if ( mv.getIdentifier() != null && !mv.getIdentifier().equals( "overview" ) )
                {
                    String strictXhtml = VocabularyUtils.toStrictXhtml( mv.getValue() );
                    sections.add( strictXhtml );
                }
            }
            map.put("sections", sections );
        }

        var body = (StreamingResponseBody) outputStream ->
        {
            try
            {
                exportService.generateFileByThymeleafTemplate( "document", map, downloadType, outputStream );
            }
            catch ( JAXBException | Docx4JException e )
            {
                throw new IllegalStateException( e );
            }
        };

        return ResponseEntity.ok()
            .contentType( downloadType.getMediaType() )
            .header( HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "document." + downloadType )
            .body( body );
    }

    /**
     * {@code DELETE  /metadata-fields/:id} : delete the "id" metadataField.
     *
     * @param id the id of the metadataFieldDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/metadata-fields/{id}")
    public ResponseEntity<Void> deleteMetadataField(@PathVariable Long id) {
        log.debug("REST request to delete MetadataField : {}", id);
        metadataFieldService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/metadata-fields?query=:query} : search for the metadataField corresponding
     * to the query.
     *
     * @param query the query of the metadataField search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/metadata-fields")
    public ResponseEntity<List<MetadataFieldDTO>> searchMetadataFields(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MetadataFields for query {}", query);
        Page<MetadataFieldDTO> page = metadataFieldService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
