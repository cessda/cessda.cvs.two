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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ResourceUtils {
    public static final String LANGUAGE = "@language";
    public static final String VALUE = "@value";
    public static final String ID = "@id";
    public static final String TYPE = "@type";

    public static final String MEDIATYPE_RDF_VALUE = "application/rdf+xml";
    public static final MediaType MEDIATYPE_RDF = MediaType.parseMediaType(MEDIATYPE_RDF_VALUE);

    private ResourceUtils(){}

    public static ResponseEntity<List<String>> getListResponseEntity(VersionDTO version1, VersionDTO version2) {
        List<String> compareVersions = VersionUtils.compareCurPrevCV(version1, version2);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Prev-Cv-Version", version2.getNotation() + " " +version2.getItemType() + " v." + version2.getNumber());
        headers.add("X-Current-Cv-Version", version1.getNotation() + " " +version1.getItemType() + " v." + version1.getNumber());
        return ResponseEntity.ok().headers(headers).body(compareVersions);
    }

    /**
     * Get server base path from the Servlet request
     * @param request
     * @return
     */
    public static String getBasePath(HttpServletRequest request) {
        if( request.getScheme().equals( "http" ) && request.getServerPort() != 80)
            return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        return request.getScheme() + "://" + request.getServerName() + request.getContextPath() + "/";
    }

    public static List<Map<String, Object>> convertVocabularyDtoToJsonLdSkosMos(VocabularyDTO vocabularyDTO, Set<CodeDTO> codeDtos, Set<String> languages){
        List<Map<String, Object>> vocabularyJsonLds = new ArrayList<>();
        String lang = "en";

        Map<String, Object> contextJsonLdMap = new LinkedHashMap<>();
        vocabularyJsonLds.add( contextJsonLdMap);
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
        if( languages != null && !languages.isEmpty()){
            lang = languages.iterator().next();
            contextContentMap.put(LANGUAGE, lang);
        }

        contextJsonLdMap.put("versionInfo", vocabularyDTO.getVersionNumber());

        List<Object> results = new ArrayList<>();
        contextJsonLdMap.put("results", results );

        // concepts
        for (CodeDTO c : codeDtos) {
            Map<String, Object> conceptJsonLdMap = new LinkedHashMap<>();
            results.add( conceptJsonLdMap);
            conceptJsonLdMap.put("uri", c.getUri());
            conceptJsonLdMap.put("type", new String[]{"skos:Concept"});
            conceptJsonLdMap.put("notation", c.getNotation());
            final String titleByLanguage = c.getTitleByLanguage(lang);
            if( titleByLanguage != null && !titleByLanguage.isEmpty())
                conceptJsonLdMap.put("prefLabel", titleByLanguage);
            final String definitionByLanguage = c.getDefinitionByLanguage(lang);
            if( definitionByLanguage != null && !definitionByLanguage.isEmpty())
                conceptJsonLdMap.put("definition", definitionByLanguage);
            conceptJsonLdMap.put("lang", lang);
            conceptJsonLdMap.put("vocab", vocabularyDTO.getNotation());
        }
        return vocabularyJsonLds;
    }

    public static List<Object> convertVocabulariesToJsonLd(EsQueryResultDetail esq, Page<VocabularyDTO> vocabulariesPage) {
        List<Object> vocabularyJsonLds = new ArrayList<>();
        if ( !vocabulariesPage.getContent().isEmpty() ) {
            vocabulariesPage.getContent().forEach(vocabularyDTO -> {
                Set<String> languages = new LinkedHashSet<>();
                if( esq.getSortLanguage() != null )
                    languages.add( esq.getSortLanguage() );
                else
                    languages.addAll( vocabularyDTO.getLanguagesPublished());
                List<Map<String, Object>> vocabularyJsonLdMap = convertVocabularyDtoToJsonLd(vocabularyDTO, vocabularyDTO.getCodes(), languages);
                vocabularyJsonLds.addAll(vocabularyJsonLdMap);
            });
        }
        return vocabularyJsonLds;
    }

    public static List<Map<String, Object>> convertVocabularyDtoToJsonLd( VocabularyDTO vocabularyDTO, Set<CodeDTO> codeDtos, Set<String> languages){
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
        languages.forEach(v -> {
            Map<String, String> langValue = new LinkedHashMap<>();
            langValue.put(LANGUAGE, v);
            langValue.put(VALUE, vocabularyDTO.getDefinitionByLanguage(v));
            descList.add(langValue);
        });
        // isVersionOf
        List<Map<String,String>> isVersionOfList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/isVersionOf", isVersionOfList);
        Map<String, String> isVersionOfMap = new LinkedHashMap<>();
        isVersionOfMap.put(ID, vocabularyDTO.getUri());
        isVersionOfList.add(isVersionOfMap);
        // license
        if( !vocabularyDTO.getVersions().isEmpty() ) {
            final VersionDTO versionDTO = vocabularyDTO.getVersions().iterator().next();
            List<Map<String, String>> licenseList = new ArrayList<>();
            vocabularyJsonLdMap.put("http://purl.org/dc/terms/license", licenseList);
            Map<String, String> licenseMap = new LinkedHashMap<>();
            licenseMap.put(ID, versionDTO.getLicenseName());
            licenseList.add(licenseMap);
        }
        // rights
        List<Map<String,String>> rightsList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/rights", rightsList);
        Map<String, String> rightsMap = new LinkedHashMap<>();
        rightsMap.put(VALUE, "Copyright ©" + vocabularyDTO.getAgencyName() + " " + vocabularyDTO.getPublicationDate().getYear());
        rightsList.add(rightsMap);
        // title
        List<Map<String,String>> titleList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/title", titleList);
        languages.forEach(v -> {
            Map<String, String> langValue = new LinkedHashMap<>();
            langValue.put(LANGUAGE, v);
            langValue.put(VALUE, vocabularyDTO.getTitleByLanguage(v));
            titleList.add(langValue);
        });
        // versionInfo
        List<Map<String,String>> versionInfoList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://www.w3.org/2002/07/owl#versionInfo", versionInfoList);
        Map<String, String> versionInfoMap = new LinkedHashMap<>();
        versionInfoMap.put(VALUE, vocabularyDTO.getVersionNumber().toString());
        versionInfoList.add(versionInfoMap);
        // hasTopConcept
        List<Map<String,String>> hasTopConceptList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://www.w3.org/2004/02/skos/core#hasTopConcept", hasTopConceptList);
        codeDtos.forEach(c -> {
            if( c.getParent() == null ) {
                Map<String, String> docIdMap = new LinkedHashMap<>();
                docIdMap.put(ID, c.getUri());
                hasTopConceptList.add(docIdMap);
            }
        });
        // notation
        List<Map<String,String>> notationList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://www.w3.org/2004/02/skos/core#notation", notationList);
        Map<String, String> notationMap = new LinkedHashMap<>();
        notationMap.put(VALUE, vocabularyDTO.getNotation());
        notationList.add(notationMap);

        // concepts
        codeDtos.forEach(c -> {
            Map<String, Object> conceptJsonLdMap = new LinkedHashMap<>();
            vocabularyJsonLds.add( conceptJsonLdMap);
            // concept
            conceptJsonLdMap.put(ID, c.getUri());
            conceptJsonLdMap.put(TYPE, new String[]{"http://www.w3.org/2004/02/skos/core#Concept"});
            //inScheme
            List<Map<String,String>> inSchemeList = new ArrayList<>();
            conceptJsonLdMap.put("http://www.w3.org/2004/02/skos/core#inScheme", inSchemeList);
            Map<String, String> inSchemeMap = new LinkedHashMap<>();
            inSchemeMap.put(ID, docId);
            inSchemeList.add(inSchemeMap);

            // prefLabel
            List<Map<String,String>> cDefList = new ArrayList<>();
            conceptJsonLdMap.put("http://www.w3.org/2004/02/skos/core#definition", cDefList);
            // definition
            List<Map<String,String>> cTitleList = new ArrayList<>();
            conceptJsonLdMap.put("http://purl.org/dc/terms/title", cTitleList);

            languages.forEach(v -> {
                Map<String, String> cDefMap = new LinkedHashMap<>();
                cDefMap.put(LANGUAGE, v);
                cDefMap.put(VALUE, c.getDefinitionByLanguage(v));
                cDefList.add(cDefMap);

                Map<String, String> cTitleMap = new LinkedHashMap<>();
                cTitleMap.put(LANGUAGE, v);
                cTitleMap.put(VALUE, c.getTitleByLanguage(v));
                cTitleList.add(cTitleMap);
            });

            // notation
            List<Map<String,String>> cNotationList = new ArrayList<>();
            conceptJsonLdMap.put("http://www.w3.org/2004/02/skos/core#notation", cNotationList);
            Map<String, String> cNotationMap = new LinkedHashMap<>();
            cNotationMap.put(VALUE, c.getNotation());
            cNotationList.add(cNotationMap);

            // narrower
            List<Map<String,String>> cNarrowerList = new ArrayList<>();
            codeDtos.forEach(c2 -> {
                if( c2.getParent() != null && c2.getParent().equals(c.getNotation())) {
                    Map<String, String> cNarrowerMap = new LinkedHashMap<>();
                    cNarrowerMap.put(ID, c2.getUri());
                    cNarrowerList.add(cNarrowerMap);
                }
            });
            if( !cNarrowerList.isEmpty() )
                conceptJsonLdMap.put("http://www.w3.org/2004/02/skos/core#narrower", cNarrowerList);
        });

        return vocabularyJsonLds;
    }

    public static String getDocIdFromVersionOrCode(VocabularyDTO vocabularyDTO) {
        String docId = vocabularyDTO.getUri();
        if( !vocabularyDTO.getVersions().isEmpty() ) {
            final VersionDTO versionDto = vocabularyDTO.getVersions().iterator().next();
            if( versionDto.getItemType().equals(ItemType.TL.toString()) )
                docId = versionDto.getUriSl();
            else
                docId = versionDto.getUri();
        } else {
            if( !vocabularyDTO.getCodes().isEmpty() ) {
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
