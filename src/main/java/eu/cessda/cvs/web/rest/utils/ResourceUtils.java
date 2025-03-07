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
package eu.cessda.cvs.web.rest.utils;

import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.service.dto.CodeDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.utils.VersionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ResourceUtils {
    public static final String LANGUAGE = "@language";
    public static final String VALUE = "@value";
    public static final String ID = "@id";
    public static final String TYPE = "@type";

    private ResourceUtils(){}

    public static ResponseEntity<List<String>> getListResponseEntity(VersionDTO version1, VersionDTO version2) {
        List<String> compareVersions = VersionUtils.compareCurPrevCV(version1, version2);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Prev-Cv-Version", version2.getNotation() + " " +version2.getItemType() + " v." + version2.getNumber());
        headers.add("X-Current-Cv-Version", version1.getNotation() + " " +version1.getItemType() + " v." + version1.getNumber());
        return ResponseEntity.ok().headers(headers).body(compareVersions);
    }

    public static Map<String, Object> convertVocabularyDtoToJsonLdSkosMos(VocabularyDTO vocabularyDTO, Set<CodeDTO> codeDTOs, String lang) {

        Map<String, Object> contextJsonLdMap = new LinkedHashMap<>();
        Map<String, Object> contextContentMap = new LinkedHashMap<>();
        contextJsonLdMap.put("@context", contextContentMap );

        contextContentMap.put("skos", "http://www.w3.org/2004/02/skos/core#");
        contextContentMap.put("owl", "http://www.w3.org/2002/07/owl#");
        contextContentMap.put("uri", ID);
        contextContentMap.put("type", TYPE);
        contextContentMap.put("onki", "http://schema.onki.fi/onki#");

        Map<String, Object> contextResultMap = new LinkedHashMap<>();
        contextResultMap.put(ID, "onki:results");
        contextResultMap.put("@container", "@list");

        contextContentMap.put("results", contextResultMap);
        contextContentMap.put("versionInfo", "skos:versionInfo");
        contextContentMap.put("notation", "skos:notation");
        contextContentMap.put("prefLabel", "skos:prefLabel");
        contextContentMap.put("definition", "skos:definition");
        if( !StringUtils.isEmpty( lang ) ){
            contextContentMap.put(LANGUAGE, lang);
        }

        contextJsonLdMap.put("versionInfo", vocabularyDTO.getVersionNumber());

        // concepts
        List<Map<String, Object>> results = new ArrayList<>();
        for (CodeDTO c : codeDTOs) {
            Map<String, Object> conceptJsonLdMap = new LinkedHashMap<>();
            conceptJsonLdMap.put("uri", c.getUri());
            conceptJsonLdMap.put("type", new String[]{"skos:Concept"});
            conceptJsonLdMap.put("notation", c.getNotation());
            final String titleByLanguage = c.getTitleByLanguage(lang);
            if( !StringUtils.isEmpty( titleByLanguage ) )
            {
                conceptJsonLdMap.put("prefLabel", titleByLanguage);
            }
            final String definitionByLanguage = c.getDefinitionByLanguage(lang);
            if( !StringUtils.isEmpty( definitionByLanguage ) )
            {
                conceptJsonLdMap.put("definition", definitionByLanguage);
            }
            conceptJsonLdMap.put("lang", lang);
            conceptJsonLdMap.put("vocab", vocabularyDTO.getNotation());
            results.add( conceptJsonLdMap );
        }

        contextJsonLdMap.put("results", results );

        // return completed map
        return contextJsonLdMap;
    }

    public static List<Object> convertVocabulariesToJsonLd(EsQueryResultDetail esq, Page<VocabularyDTO> vocabulariesPage) {
        List<Object> vocabularyJsonLds = new ArrayList<>();
        if ( !vocabulariesPage.getContent().isEmpty() ) {
            for ( VocabularyDTO vocabularyDTO : vocabulariesPage.getContent() )
            {
                Set<String> languages = new LinkedHashSet<>();
                if ( esq.getSortLanguage() != null )
                {
                    languages.add( esq.getSortLanguage() );
                }
                else
                {
                    languages.addAll( vocabularyDTO.getLanguagesPublished() );
                }
                List<Map<String, Object>> vocabularyJsonLdMap = convertVocabularyDtoToJsonLd( vocabularyDTO, vocabularyDTO.getCodes(), languages );
                vocabularyJsonLds.addAll( vocabularyJsonLdMap );
            }
        }
        return vocabularyJsonLds;
    }

    public static List<Map<String, Object>> convertVocabularyDtoToJsonLd( VocabularyDTO vocabularyDTO, Set<CodeDTO> codeDTOs, Set<String> languages )
    {
        List<Map<String, Object>> vocabularyJsonLds = new ArrayList<>();

        Map<String, Object> vocabularyJsonLdMap = new LinkedHashMap<>();
        vocabularyJsonLds.add( vocabularyJsonLdMap);
        // scheme
        String docId = ResourceUtils.getDocIdFromVersionOrCode(vocabularyDTO);

        vocabularyJsonLdMap.put(ID, docId);
        vocabularyJsonLdMap.put(TYPE, new String[]{"http://www.w3.org/2004/02/skos/core#ConceptScheme"});

        // desc
        List<Map<String,String>> descList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/description", descList);
        for ( String s : languages )
        {
            descList.add(
                Map.of(
                    LANGUAGE, s,
                    VALUE, vocabularyDTO.getDefinitionByLanguage( s )
                )
            );
        }

        // isVersionOf
        if (vocabularyDTO.getUri() != null)
        {
            vocabularyJsonLdMap.put( "http://purl.org/dc/terms/isVersionOf", List.of(
                Map.of( ID, vocabularyDTO.getUri() )
            ) );
        }

        // license
        var versionDTOIterator = vocabularyDTO.getVersions().iterator();
        if( versionDTOIterator.hasNext() ) {
            final VersionDTO versionDTO = versionDTOIterator.next();
            if (versionDTO.getLicenseName() != null)
            {
                vocabularyJsonLdMap.put( "http://purl.org/dc/terms/license", List.of(
                    Map.of( ID, versionDTO.getLicenseName() )
                ) );
            }
        }

        // rights
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/rights", List.of(
            Map.of(VALUE, "Copyright ©" + vocabularyDTO.getAgencyName() + " " + vocabularyDTO.getPublicationDate().getYear())
        ));

        // title
        List<Map<String,String>> titleList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/title", titleList);
        for ( String language : languages )
        {
            titleList.add(
                Map.of(
                    LANGUAGE, language,
                    VALUE, vocabularyDTO.getTitleByLanguage( language )
                )
            );
        }

        // versionInfo
        vocabularyJsonLdMap.put("http://www.w3.org/2002/07/owl#versionInfo",
            List.of(
                Map.of(VALUE, vocabularyDTO.getVersionNumber())
            )
        );

        // hasTopConcept
        List<Map<String,String>> hasTopConceptList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://www.w3.org/2004/02/skos/core#hasTopConcept", hasTopConceptList);
        for ( CodeDTO codeDTO : codeDTOs )
        {
            if ( codeDTO.getParent() == null && codeDTO.getUri() != null)
            {
                hasTopConceptList.add(
                    Map.of( ID, codeDTO.getUri() )
                );
            }
        }

        // notation
        vocabularyJsonLdMap.put("http://www.w3.org/2004/02/skos/core#notation", List.of(
            Map.of(VALUE, vocabularyDTO.getNotation())
        ));

        // concepts
        for ( CodeDTO c : codeDTOs )
        {
            Map<String, Object> conceptJsonLdMap = new LinkedHashMap<>();
            vocabularyJsonLds.add( conceptJsonLdMap );
            // concept
            conceptJsonLdMap.put( ID, c.getUri() );
            conceptJsonLdMap.put( TYPE, new String[]{ "http://www.w3.org/2004/02/skos/core#Concept" } );
            //inScheme
            List<Map<String, String>> inSchemeList = List.of(
                Map.of( ID, docId )
            );
            conceptJsonLdMap.put( "http://www.w3.org/2004/02/skos/core#inScheme", inSchemeList );

            // prefLabel
            List<Map<String, String>> cDefList = new ArrayList<>();
            conceptJsonLdMap.put( "http://www.w3.org/2004/02/skos/core#definition", cDefList );
            // definition
            List<Map<String, String>> cTitleList = new ArrayList<>();
            conceptJsonLdMap.put( "http://purl.org/dc/terms/title", cTitleList );

            for ( String v : languages )
            {
                cDefList.add( Map.of(
                    LANGUAGE, v,
                    VALUE, c.getDefinitionByLanguage( v )
                ) );

                cTitleList.add( Map.of(
                    LANGUAGE, v,
                    VALUE, c.getTitleByLanguage( v )
                ) );
            }

            // notation
            conceptJsonLdMap.put( "http://www.w3.org/2004/02/skos/core#notation",
                List.of(
                    Map.of( VALUE, c.getNotation() )
                )
            );

            // narrower
            List<Map<String, String>> cNarrowerList = new ArrayList<>();
            for ( CodeDTO c2 : codeDTOs )
            {
                if ( c2.getParent() != null && c2.getParent().equals( c.getNotation() ) && c2.getUri() != null )
                {
                    cNarrowerList.add( Map.of( ID, c2.getUri() ) );
                }
            }
            if ( !cNarrowerList.isEmpty() )
            {
                conceptJsonLdMap.put( "http://www.w3.org/2004/02/skos/core#narrower", cNarrowerList );
            }
        }

        return vocabularyJsonLds;
    }

    public static String getDocIdFromVersionOrCode(VocabularyDTO vocabularyDTO) {
        String docId = vocabularyDTO.getUri();
        var versionsIterator = vocabularyDTO.getVersions().iterator();
        if( versionsIterator.hasNext() )
        {
            final VersionDTO versionDto = versionsIterator.next();
            if( versionDto.getItemType().equals(ItemType.TL.toString()) )
            {
                docId = versionDto.getUriSl();
            }
            else
            {
                docId = versionDto.getUri();
            }
        }
        else
        {
            if( !vocabularyDTO.getCodes().isEmpty() )
            {
                final CodeDTO codeDTO = vocabularyDTO.getCodes().iterator().next();
                docId = codeDTO.getUri();
            }
        }
        return docId;
    }

    public static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
