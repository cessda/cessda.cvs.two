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

import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.service.ExportService;
import eu.cessda.cvs.service.ResourceNotFoundException;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.CodeDTO;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.utils.VersionUtils;
import eu.cessda.cvs.utils.VocabularyUtils;
import eu.cessda.cvs.web.rest.domain.CvResult;
import eu.cessda.cvs.web.rest.utils.ResourceUtils;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/v2")
public class VocabularyResourceV2 {
    private static final Logger log = LoggerFactory.getLogger(VocabularyResourceV2.class);

    public static final String ATTACHMENT_FILENAME = "attachment; filename=";
    public static final String LANGUAGE = "@language";
    public static final String ID = "@id";
    public static final String JSONLD_TYPE = "application/ld+json";
    private static final String VERSION = "version";
    private static final String VERSION_WITH_INCLUDED_VERSIONS = "REST request to get a JSON file of vocabulary {} with version {} with included versions {}";

    private final VocabularyService vocabularyService;

    public VocabularyResourceV2( VocabularyService vocabularyService ) {
        this.vocabularyService = vocabularyService;
    }

    /**
     * {@code GET  /search} : This vocabulary search API is used for the front-end of CVS publication page.
     * It is hidden from swagger since, the parameters filter "f" used in the API are complex.
     *

     * @param q the query term.
     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping("/search")
    @ApiOperation( value = "Searching the vocabularies", hidden = true )
    public ResponseEntity<CvResult> searchVocabularies(
        @ApiParam( value = "the query, e.g. Family" ) @RequestParam(name = "q", required = false) String q,
        @ApiParam( value = "the filter, e.g agency:DDI Alliance;language:en" ) @RequestParam(name = "f", required = false) String f,
        Pageable pageable) {
        log.debug("REST request search vocabulary by query");
        if (q == null)
            q = "";
        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(q, f, pageable, SearchScope.PUBLICATIONSEARCH);
        vocabularyService.search(esq);
        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), vocabulariesPage);
        return ResponseEntity.ok().headers(headers).body( VocabularyUtils.mapResultToCvResult(esq, vocabulariesPage) );
    }

    /**
     * {@code GET  /search/vocabularies} : Search vocabularies returning JSON of Vocabularies and Codes
     *
     * @param query the query term.
     * @param agency the agency.
     * @param lang the language.
     * @param pageable
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping(
        value="/search/vocabularies",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
        value = "Search vocabularies",
        notes = "This method searches the vocabulary with the default size of 20")
    public ResponseEntity<CvResult> searchVocabulariesJson(
        @ApiParam(
            name = "query",
            type = "String",
            value = "The search term",
            example = "Economics"
        ) @RequestParam (required = false) String query,
        @ApiParam(
            name = "agency",
            type = "String",
            value = "The agency",
            example = "CESSDA"
        ) @RequestParam (required = false) String agency,
        @ApiParam(
            name = "lang",
            type = "String",
            value = "The language",
            example = "en"
        ) @RequestParam (required = false) String lang,
         Pageable pageable
    ) {
        log.debug("REST request search vocabulary, produces JSON");
        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(query, agency, lang, pageable, SearchScope.PUBLICATIONSEARCH);
        vocabularyService.search(esq);
        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();
        // remove unused property
        vocabulariesPage.get().forEach(vocab -> {
            VocabularyDTO.cleanUpContentForApi( vocab );
            // remove other included languages
            if( lang != null ) {
                vocab.setLanguagesPublished( new HashSet<>(Collections.singletonList(lang)));
                String titleTemp = vocab.getTitleByLanguage(lang);
                String defTemp = vocab.getDefinitionByLanguage(lang);
                vocab.clearContent();
                vocab.setTitleDefinition(titleTemp, defTemp, lang, false);

                for (CodeDTO code : vocab.getCodes()) {
                    String codeTitleTemp = code.getTitleByLanguage(lang);
                    String codeDefTemp = code.getDefinitionByLanguage(lang);
                    code.clearContents();
                    code.setId( null );
                    code.setPosition( null );
                    code.setTitleDefinition(codeTitleTemp, codeDefTemp, lang, false);
                }
            }
        });

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), vocabulariesPage);
        return ResponseEntity.ok().headers(headers).body( VocabularyUtils.mapResultToCvResult(esq, vocabulariesPage) );
    }

    /**
     * {@code GET  /search/vocabularies} : Search vocabularies returning JSON of Vocabularies and Codes
     *
     * @param query the query term.
     * @param agency the agency.
     * @param lang the language.
     * @param pageable
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping(
        value="/search/vocabularies",
        produces = JSONLD_TYPE)
    @ApiOperation(
        value = "Search vocabularies",
        notes = "This method searches the vocabulary with the default size of 20")
    public ResponseEntity<List<Object>> searchVocabulariesJsonLd(
        @ApiParam(
            name = "query",
            type = "String",
            value = "The search term",
            example = "Economics"
        ) @RequestParam (required = false) String query,
        @ApiParam(
            name = "agency",
            type = "String",
            value = "The agency",
            example = "CESSDA"
        ) @RequestParam (required = false) String agency,
        @ApiParam(
            name = "lang",
            type = "String",
            value = "The language",
            example = "en"
        ) @RequestParam (required = false) String lang,
        Pageable pageable
    ) {
        log.debug("REST request search vocabulary, produces JSON LD");
        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(query, agency, lang, pageable, SearchScope.PUBLICATIONSEARCH);
        vocabularyService.search(esq);
        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();
        List<Object> vocabularyJsonLds = ResourceUtils.convertVocabulariesToJsonLd(esq, vocabulariesPage);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), vocabulariesPage);
        return ResponseEntity.ok().headers(headers).body( vocabularyJsonLds );
    }

    /**
     * {@code GET  /search/vocabularies} : Search codes
     *
     * @param query The query, e.g. Economics or with wildcard eco*
     * @param agency The specific agency/institution
     * @param vocab The specific vocabulary e.g. TopicClassification
     * @param lang The language e.g. en
     * @param size The maximum size of codes returned, default 20
     * @return map of Codes in JSON-LD based on Skosmos format
     */
    @GetMapping(
        value="/search/codes",
        produces = JSONLD_TYPE
    )
    @ApiOperation( value = "Searching the vocabularies codes that produces JSON-LD based on Skosmos format" )
    public Map<String, Object> getAllVocabulariesCode(
        @ApiParam(
            name = "query",
            type = "String",
            value = "The query, e.g. Economics or with wildcard eco*",
            example = "Economics",
            required = true
        ) @RequestParam String query,
        @ApiParam(
            name = "agency",
            type = "String",
            value = "The agency",
            example = "CESSDA"
        ) @RequestParam (required = false) String agency,
        @ApiParam(
            name = "vocab",
            type = "String",
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @RequestParam String vocab,
        @ApiParam(
            name = "lang",
            type = "String",
            value = "The language",
            example = "en",
            required = true
        ) @RequestParam String lang,
        @ApiParam(
            name = "size",
            type = "Integer",
            value = "The maximum size of codes returned, default 20 maximum 100",
            example = "20"
        ) @RequestParam( required = false ) Integer size
    ) {
        log.debug("REST request to get a page of Vocabularies");
        if (query == null)
            query = "";
        String filter = "language:" +lang;
        if( agency != null ) {
            filter += ";agency:" + agency;
        }
        if( vocab != null ) {
            filter += ";vocab:" + vocab;
        }
        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(query, filter, null, SearchScope.PUBLICATIONSEARCH);
        esq.setCodeSize( size == null ? 20 : size );
        vocabularyService.searchCode(esq);
        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();

        if ( !vocabulariesPage.getContent().isEmpty() ) {
            for ( VocabularyDTO vocabularyDTO : vocabulariesPage.getContent() )
            {
                return ResourceUtils.convertVocabularyDtoToJsonLdSkosMos( vocabularyDTO, vocabularyDTO.getCodes(), lang );
            }
        }

        // empty response
        return Collections.emptyMap();
    }

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param request
     * @param vocabulary
     * @param versionNumberSl
     * @param languageVersion
     * @return Vocabulary in HTML format
     */
    @GetMapping(
        value="/vocabularies/{vocabulary}/{versionNumberSl}",
        produces = MediaType.TEXT_HTML_VALUE
    )
    @ApiOperation( value = "Get a Vocabulary in HTML file" )
    public ResponseEntity<Resource> getVocabularyHtml(
        HttpServletRequest request,
        @ApiParam(
            name = "vocabulary",
            type = "String",
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @PathVariable String vocabulary,
        @ApiParam(
            name = "versionNumberSl",
            type = "String",
            value = "The version number of Source language",
            example = "4.0",
            required = true
        ) @PathVariable String versionNumberSl,
        @ApiParam(
            name = "languageVersion",
            type = "String",
            value = "included language version, e.g. en-4.0_de-4.0.1, separated by _" ,
            example = "en-4.0"
        ) @RequestParam( required = false )  String languageVersion
    ) {
        log.debug("REST request to get a HTML file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);

        String requestURL = ResourceUtils.getURLWithContextPath( request );

        Path fileName = vocabularyService.generateVocabularyFileDownload( vocabulary, versionNumberSl, languageVersion, ExportService.DownloadType.HTML, requestURL, true);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName.getFileName())
            .body(new FileSystemResource( fileName ));
    }

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param request
     * @param vocabulary
     * @param versionNumberSl
     * @param languageVersion
     * @return Vocabulary in JSON format
     */
    @GetMapping(
        value="/vocabularies/{vocabulary}/{versionNumberSl}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation( value = "Get a Vocabulary in JSON format" )
    public VocabularyDTO getVocabularyJson(
        HttpServletRequest request,
        @ApiParam(
            name = "vocabulary",
            type = "String",
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @PathVariable String vocabulary,
        @ApiParam(
            name = "versionNumberSl",
            type = "String",
            value = "The version number of Source language",
            example = "4.0",
            required = true
        ) @PathVariable String versionNumberSl,
        @ApiParam(
            name = "languageVersion",
            type = "String",
            value = "included language version, e.g. en-4.0_de-4.0.1, separated by _" ,
            example = "en-4.0"
        ) @RequestParam( required = false )  String languageVersion
    ) {
        log.debug(VERSION_WITH_INCLUDED_VERSIONS, vocabulary, versionNumberSl, languageVersion);
        VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions(vocabulary, versionNumberSl, languageVersion);
        if (vocabularyDTO.getVersions().isEmpty()) {
            throw new ResourceNotFoundException( "Unable to find a vocabulary with " + versionNumberSl + " SL version number and/or notation " + vocabulary,  vocabulary, "404");
        } else {
            return vocabularyDTO;
        }
    }

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl/:language} : Get Vocabulary
     *
     * @param request
     * @param vocabulary
     * @param versionNumber
     * @param language
     * @return Vocabulary in JSON format
     */
    @GetMapping(
        value="/codes/{vocabulary}/{versionNumber}/{language}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation( value = "Get a Code in JSON format" )
    public Set<ConceptDTO> getCodeJson(
        HttpServletRequest request,
        @ApiParam(
            name = "vocabulary",
            type = "String",
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @PathVariable String vocabulary,
        @ApiParam(
            name = "versionNumber",
            type = "String",
            value = "The version number",
            example = "4.0",
            required = true
        ) @PathVariable String versionNumber,
        @ApiParam(
            name = "language",
            type = "String",
            value = "The language" ,
            example = "en",
            required = true
        ) @PathVariable( required = false )  String language
    ) {
        final String langVersion = language + "-" + versionNumber;
        log.debug(VERSION_WITH_INCLUDED_VERSIONS, vocabulary, VersionUtils.getSlVersionNumber(versionNumber), langVersion );
        final VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions(vocabulary, VersionUtils.getSlVersionNumber(versionNumber).toString(), language + "-" + versionNumber);
        var versionsIterator = vocabularyDTO.getVersions().iterator();
        if (versionsIterator.hasNext()) {
            return versionsIterator.next().getConcepts();
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param request
     * @param vocabulary
     * @param versionNumberSl
     * @param languageVersion
     * @return Vocabulary in JSON-LD format
     */
    @GetMapping(
        value="/vocabularies/{vocabulary}/{versionNumberSl}",
        produces = JSONLD_TYPE
    )
    @ApiOperation( value = "Get a Vocabulary in JSON-LD format" )
    public List<Map<String, Object>> getVocabularyJsonLd(
        HttpServletRequest request,
        @ApiParam(
            name = "vocabulary",
            type = "String",
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @PathVariable String vocabulary,
        @ApiParam(
            name = "versionNumberSl",
            type = "String",
            value = "The version number of Source language",
            example = "4.0",
            required = true
        ) @PathVariable String versionNumberSl,
        @ApiParam(
            name = "languageVersion",
            type = "String",
            value = "included language version, e.g. en-4.0_de-4.0.1, separated by _" ,
            example = "en-4.0"
        ) @RequestParam( required = false )  String languageVersion
    ) {
        log.debug(VERSION_WITH_INCLUDED_VERSIONS, vocabulary, versionNumberSl, languageVersion);
        VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions( vocabulary, versionNumberSl, languageVersion );

        Set<CodeDTO> codeDTOs = CodeDTO.generateCodesFromVersion(vocabularyDTO.getVersions(), false);
        var languageSet = vocabularyDTO.getVersions().stream().map( VersionDTO::getLanguage ).collect( Collectors.toSet() );

        return ResourceUtils.convertVocabularyDtoToJsonLd(vocabularyDTO, codeDTOs, languageSet );
    }

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param request
     * @param vocabulary
     * @param versionNumberSl
     * @param languageVersion
     * @return Vocabulary
     */
    @GetMapping(
        value="/vocabularies/{vocabulary}/{versionNumberSl}",
        produces = { MediaType.APPLICATION_PDF_VALUE, ExportService.MEDIATYPE_WORD_VALUE, ExportService.MEDIATYPE_RDF_VALUE }
    )
    @ApiOperation( value = "Get a Vocabulary in PDF file" )
    public ResponseEntity<Resource> getVocabularyPdf(
        HttpServletRequest request,
        @ApiParam(
            name = "vocabulary",
            type = "String",
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @PathVariable String vocabulary,
        @ApiParam(
            name = "versionNumberSl",
            type = "String",
            value = "The version number of Source language",
            example = "4.0",
            required = true
        ) @PathVariable String versionNumberSl,
        @ApiParam(
            name = "languageVersion",
            type = "String",
            value = "included language version, e.g. en-4.0_de-4.0.1, separated by _" ,
            example = "en-4.0"
        ) @RequestParam( required = false )  String languageVersion
    ) {
        log.debug("REST request to get a PDF file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);

        var requestURL = ResourceUtils.getURLWithContextPath( request );
        var mediaType = MediaType.parseMediaType( request.getHeader( "accept" ) );
        var type = ExportService.DownloadType.fromMediaType( mediaType ).orElseThrow(); // produces attribute should restrict to acceptable values

        Path fileName = vocabularyService.generateVocabularyFileDownload( vocabulary, versionNumberSl, languageVersion, type, requestURL, true );

        return ResponseEntity.ok()
            .contentType( type.getMediaType() )
            .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName.getFileName())
            .body(new FileSystemResource( fileName ) );
    }

    /**
     * {@code GET  /editors/vocabularies/compare-prev/:id} : get the text to be compared by diff-algorithm,
     * given "id" vocabulary and previous version from vocabulary. This method is hidden from swagger-UI, since the di
     *
     * @param cv the id of the vocabularyDTO to be compared with previous version.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vocabularyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/compare-vocabulary/{cv}/{lv1}/{lv2}")
    @ApiOperation( value = "Get the Vocabulary content of 2 different versions, in order to be compared with diff-app", hidden = true )
    public ResponseEntity<List<String>> getVocabularyComparePrev(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" )  @PathVariable String cv,
        @ApiParam( value = "the first version e.g en-2.0" ) @PathVariable String lv1,
        @ApiParam( value = "the second version e.g en-1.0" )@PathVariable String lv2
    ) {
        log.debug("REST request to get Vocabulary comparison text from JSON files of CV {}, between version {} and {}", cv, lv1, lv2);
        final String[] splitLanguageVersion1 = VersionUtils.splitLanguageVersion(lv1);
        final String[] splitLanguageVersion2 = VersionUtils.splitLanguageVersion(lv2);

        VersionDTO version1 = VocabularyUtils.getVersionByLangVersion(vocabularyService.getVocabularyByNotationAndVersion(cv, splitLanguageVersion1[0], false), splitLanguageVersion1[2], splitLanguageVersion1[1]);
        VersionDTO version2 = VocabularyUtils.getVersionByLangVersion(vocabularyService.getVocabularyByNotationAndVersion(cv, splitLanguageVersion2[0], false), splitLanguageVersion2[2], splitLanguageVersion2[1]);

        return ResourceUtils.getListResponseEntity(version1, version2);
    }

    private VocabularyDTO getVocabularyDTOAndFilterVersions(String vocabulary, String versionNumberSl, String languageVersion) {
        VocabularyDTO vocabularyDTO = vocabularyService.getVocabularyByNotationAndVersion(vocabulary, versionNumberSl, true);
        vocabularyDTO.setVersions(vocabularyService.filterOutVocabularyVersions(languageVersion, vocabularyDTO));
        return vocabularyDTO;
    }

    /**
     * GET   : get all the vocabularies .
     *
     * @return map of Agencies and CVs code
     */
    @GetMapping
    (
    value="/vocabularies-published",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation( value = "Get list of the agencies and the published controlled vocabularies with the details of the rest API link. The rest API links need a specific content type e.g. JSON, PDF" )
    public Map<String, Map<String, Map<String, Map<String, String>>>> getAllVocabularies(HttpServletRequest request)
    {
        log.debug( "REST: Getting all Vocabularies" );
        Map<String, Map<String, Map<String, Map<String, String>>>> agencyCvMap = new TreeMap<>();
        List<VocabularyDTO> vocabularies = vocabularyService.findAll();
        vocabularies = vocabularies.stream().sorted( Comparator.comparing( VocabularyDTO::getNotation ) ).collect( Collectors.toList() );
        for ( VocabularyDTO voc : vocabularies )
        {
            // ignore withdrawn vocabularies
            if ( Boolean.TRUE.equals( voc.isWithdrawn() ) )
            {
                continue;
            }

            // check if there is published version
            if ( voc.getVersions().stream().anyMatch( v -> v.getStatus() == Status.PUBLISHED ) )
            {
                Map<String, Map<String, Map<String, String>>> vocabMap = agencyCvMap.computeIfAbsent( voc.getAgencyName(), k -> new LinkedHashMap<>() );
                List<VersionDTO> versions = new ArrayList<>( voc.getVersions() );
                versions.sort( Comparator.comparing( VersionDTO::getNumber ) );
                for ( VersionDTO version : versions )
                {
                    Map<String, Map<String, String>> langMap = vocabMap.computeIfAbsent( version.getNotation(), k -> new LinkedHashMap<>() );
                    Map<String, String> versionMap = langMap.computeIfAbsent(
                        version.getLanguage() + "(" + version.getItemType() + ")", k -> new LinkedHashMap<>() );
                    versionMap.put( version.getNumber().toString(),
                        request.getContextPath() + "/" + "v2/vocabularies/" + version.getNotation() + "/" +
                            version.getNumber().getBasePatchVersion() + "?languageVersion=" +
                            version.getLanguage() +
                            "-" + version.getNumber() );
                    for ( Map<String, Object> versionHistory : version.getVersionHistories() )
                    {
                        versionMap.put( versionHistory.get( VERSION ).toString(),
                            request.getContextPath() + "/" + "v2/vocabularies/" + version.getNotation() + "/" +
                                VersionUtils.getSlVersionNumber( versionHistory.get( VERSION ).toString() ) +
                                "?languageVersion=" + version.getLanguage() + "-" +
                                versionHistory.get( VERSION ).toString() );
                    }
                }
            }
        }
        return agencyCvMap;
    }
}
