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

import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.service.ExportService;
import eu.cessda.cvs.service.VersionService;
import eu.cessda.cvs.service.dto.VersionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static eu.cessda.cvs.web.rest.VocabularyResourceV2.JSONLD_TYPE;

/**
 * REST controller for resolving URN
 */
@RestController
@RequestMapping("/urn")
public class UrnResolver {

    private final Logger log = LoggerFactory.getLogger(UrnResolver.class);

    private final VersionService versionService;

    public UrnResolver(VersionService versionService) {
        this.versionService = versionService;
    }

    /**
     * GET  / : redirect to resourceURL by "resolverUri or URN" resolver.
     *
     * @param urn the URN of the versionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and then redirect to resource URL,
     * or with status 404 (Not Found)
     */
    @GetMapping(value = "/{urn}", produces = { MediaType.APPLICATION_JSON_VALUE, JSONLD_TYPE, MediaType.APPLICATION_PDF_VALUE, ExportService.MEDIATYPE_WORD_VALUE, ExportService.MEDIATYPE_RDF_VALUE })
    public ResponseEntity<Void> findUrnJsonResolver( @PathVariable String urn, @RequestParam(name = "lang", required = false) String lang )
    {
        Optional<ResponseEntity<Void>> responseEntity;

        if( Character.isDigit( urn.charAt( urn.length() - 1) ))
        {
            responseEntity = getVersionByUrn( urn, lang ).map( versionResult -> {
                VersionDTO v = versionResult.versionDTO;
                String uriString = "/v2/vocabularies/" + v.getNotation() + "/" + v.getNumber();
                if (versionResult.languageFound)
                {
                    uriString += "?languageVersion=" + lang + "-" + v.getNumber();
                }

                return ResponseEntity.status( HttpStatus.FOUND ).location( URI.create( uriString ) ).build();
            } );
        }
        else
        {
            responseEntity = getVersionByUrnStartWith(urn).map( v ->
                ResponseEntity.status( HttpStatus.FOUND )
                    .location( URI.create( "/v2/vocabularies/" + v.getNotation() + "/" + v.getNumber() ) )
                    .build()
            );
        }

        return responseEntity.orElse( ResponseEntity.notFound().build() );
    }

    /**
     * GET  / : redirect to resourceURL by "resolverUri or URN" resolver.
     *
     * @param urn the URN of the versionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and then redirect to resource URL,
     * or with status 404 (Not Found)
     */
    @GetMapping("/{urn}")
    public ResponseEntity<Void> findUrnResolver( @PathVariable String urn, @RequestParam(name = "lang", required = false) String lang) {
        log.debug("REST request to get Resolver by URI: {}", urn);

        Optional<ResponseEntity<Void>> responseEntity;

        if( Character.isDigit( urn.charAt( urn.length() - 1) ))
        {
            responseEntity = getVersionByUrn(urn, lang).map(v ->
            {
                var versionDTO = v.versionDTO;
                boolean languageFound = v.languageFound;

                // Derive the version number
                var versionNumber = getVersionNumber( versionDTO );

                String uriString = "/vocabulary/" + versionDTO.getNotation() + "?v=" + versionNumber;
                if (languageFound)
                {
                    uriString = uriString + "&lang=" + lang;
                }

                return ResponseEntity.status( HttpStatus.FOUND ).location( URI.create( uriString ) ).build();
            });
        }
        else
        {
            responseEntity = getVersionByUrnStartWith(urn).map( v ->
                ResponseEntity.status( HttpStatus.FOUND )
                    .location( URI.create( "/vocabulary/" + v.getNotation() ) )
                    .build()
            );
        }

        return responseEntity.orElse( ResponseEntity.notFound().build() );
    }

    private static String getVersionNumber( VersionDTO versionDTO )
    {
        String versionNumber;
        if ( versionDTO.getItemType() == ItemType.SL )
        {
            versionNumber = versionDTO.getNumber().toString();
        }
        else
        {
            versionNumber = versionDTO.getNumber().getMinorVersion();
        }
        return versionNumber;
    }

    private Optional<VersionDTO> getVersionByUrnStartWith(String urn) {
        for ( VersionDTO v : versionService.findByUrnStartingWith( urn ) )
        {
            if ( v.getStatus() == Status.PUBLISHED )
            {
                return Optional.of( v );
            }
        }
        return Optional.empty();
    }

    private Optional<VersionResult> getVersionByUrn(String urn, String lang) {
        final List<VersionDTO> versions = versionService.findByUrn(urn);

        // Attempt to find language version
        if( lang != null )
        {
            for ( VersionDTO v : versions )
            {
                if ( v.getLanguage().equals( lang ) )
                {
                    return Optional.of( new VersionResult( v, true ) );
                }
            }
        }

        // No language specific variants found or language not specified, attempt to find SL version
        // TODO: is this the right behaviour? Should 404 be returned if a language variant is not found?
        for ( VersionDTO v : versions )
        {
            if ( v.getItemType() == ItemType.SL )
            {
                return Optional.of( new VersionResult( v, false ) );
            }
        }

        return Optional.empty();
    }

    private static final class VersionResult
    {
        private final VersionDTO versionDTO;
        private final boolean languageFound;

        private VersionResult( VersionDTO versionDTO, boolean languageFound )
        {
            this.versionDTO = versionDTO;
            this.languageFound = languageFound;
        }
    }
}
