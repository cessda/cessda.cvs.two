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
import eu.cessda.cvs.service.VersionService;
import eu.cessda.cvs.service.dto.VersionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Optional;

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
    @GetMapping("/{urn}")
    public ResponseEntity<String> findUrnResolver(HttpServletRequest request,
          @PathVariable String urn, @RequestParam(name = "lang", required = false) String lang) {
        log.debug("REST request to get Resolver by URI: {}", urn);
        Optional<ResponseEntity<String>> headers;
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        if( Character.isDigit( urn.charAt( urn.length() - 1) ))
        {
            headers = getVersionByUrn(baseUrl, urn, lang);
        }
        else
        {
            headers = getVersionByUrnStartWith(baseUrl, urn);
        }

        return headers.orElseGet( () -> ResponseEntity.notFound().build() );
    }

    private Optional<ResponseEntity<String>> getVersionByUrnStartWith(String baseUrl, String urn) {
        return versionService.findByUrnStartingWith(urn).stream()
            .filter(v -> v.getStatus().equals(Status.PUBLISHED.toString())).findFirst()
            .map(versionDTO -> {
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create( baseUrl + "/vocabulary/" + versionDTO.getNotation()));
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            });
    }

    private Optional<ResponseEntity<String>> getVersionByUrn(String baseUrl, String urn, String lang) {
        final List<VersionDTO> versions = versionService.findByUrn(urn);
        Optional<VersionDTO> versionDTOOptional = Optional.empty();
        if( lang != null ) {
            versionDTOOptional = versions.stream().filter(v -> v.getLanguage().equals(lang)).findFirst();
        }

        String additionalQuery;
        if (versionDTOOptional.isPresent()) {
            // Got a language variant
            additionalQuery = "&lang=" + lang;
        } else {
            versionDTOOptional = versions.stream().filter(v -> v.getItemType().equals(ItemType.SL.toString())).findFirst();
            additionalQuery = "";
        }

        return versionDTOOptional.map(versionDTO -> {
            HttpHeaders headers = new HttpHeaders();

            // Derive the version number
            String versionNumber;
            if (versionDTO.getItemType().equals(ItemType.SL.toString())) {
                versionNumber = versionDTO.getNumber().toString();
            } else {
                versionNumber = versionDTO.getNumber().getMinorVersion();
            }

            headers.setLocation(URI.create( baseUrl + "/vocabulary/" + versionDTO.getNotation() + "?v=" + versionNumber + additionalQuery));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        });
    }
}
