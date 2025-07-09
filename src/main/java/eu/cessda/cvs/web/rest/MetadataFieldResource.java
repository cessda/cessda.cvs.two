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
import io.github.jhipster.web.util.ResponseUtil;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.xml.bind.JAXBException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link eu.cessda.cvs.domain.MetadataField}.
 */
@RestController
@RequestMapping("/api/metadata-fields")
public class MetadataFieldResource {

    private final Logger log = LoggerFactory.getLogger(MetadataFieldResource.class);

    public static final String ATTACHMENT_FILENAME = "attachment; filename=";

    private final MetadataFieldService metadataFieldService;

    private final ExportService exportService;

    public MetadataFieldResource(MetadataFieldService metadataFieldService, ExportService exportService) {
        this.metadataFieldService = metadataFieldService;
        this.exportService = exportService;
    }

    /**
     * {@code GET  /metadata-fields/metadata-key/:metadataKey} : get the "metadataKey" metadataField.
     *
     * @param metadataKey the metadataKey of the metadataFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metadataFieldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/metadata-key/{metadataKey}")
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
    @GetMapping( path = "/download/{metadataKey}", produces = { MediaType.APPLICATION_PDF_VALUE, ExportService.MEDIATYPE_WORD_VALUE })
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
}
