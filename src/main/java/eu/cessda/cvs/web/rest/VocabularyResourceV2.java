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
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.utils.LanguageVersion;
import eu.cessda.cvs.utils.VersionNumber;
import eu.cessda.cvs.utils.VersionUtils;
import eu.cessda.cvs.utils.VocabularyUtils;
import eu.cessda.cvs.web.rest.domain.CvResult;
import eu.cessda.cvs.web.rest.utils.ResourceUtils;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static eu.cessda.cvs.web.rest.utils.ResourceUtils.MEDIATYPE_JSONLD;
import static eu.cessda.cvs.web.rest.utils.ResourceUtils.MEDIATYPE_JSONLD_VALUE;


/**
 * REST controller for managing {@link Vocabulary}.
 */
@SuppressWarnings( "deprecation" )
@Api( description = "Vocabulary Resource API (v2)" )
@RestController
@RequestMapping( value ="/v2", name = "Vocabulary Resource API (v2)" )
@Validated
public class VocabularyResourceV2 {
    private static final Logger log = LoggerFactory.getLogger(VocabularyResourceV2.class);

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";
    private static final String VERSION_WITH_INCLUDED_VERSIONS = "REST request to get a JSON file of vocabulary {} with version {} with included versions {}";

    private final VocabularyService vocabularyService;

    public VocabularyResourceV2( VocabularyService vocabularyService ) {
        this.vocabularyService = vocabularyService;
    }

    /*
     * Search Endpoints
     */

    /**
     * {@code GET  /search} : This vocabulary search API is used for the front-end of CVS publication page.
     * It is hidden from swagger since, the parameters filter "f" used in the API are complex.
     *

     * @param q the query term.
     * @param f the filter.
     * @param pageable pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping("/search")
    @ApiOperation( value = "Searching the vocabularies", hidden = true )
    public ResponseEntity<CvResult> searchVocabularies(
        @ApiParam( value = "the query", example = "Family") @RequestParam(name = "q", required = false) String q,
        @ApiParam( value = "the filter", example = "agency:DDI Alliance;language:en") @RequestParam(name = "f", required = false) String f,
        Pageable pageable) {
        log.debug("REST request search vocabulary by query");
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
     * @param pageable pagination information.
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
            value = "The search term",
            example = "Economics"
        ) @RequestParam (required = false) String query,
        @ApiParam(
            value = "The agency",
            example = "CESSDA"
        ) @RequestParam (required = false) String agency,
        @ApiParam(
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
     * @param pageable pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping(
        value="/search/vocabularies",
        produces = MEDIATYPE_JSONLD_VALUE)
    @ApiOperation(
        value = "Search vocabularies",
        notes = "This method searches the vocabulary with the default size of 20")
    public ResponseEntity<List<Object>> searchVocabulariesJsonLd(
        @ApiParam(
            value = "The search term",
            example = "Economics"
        ) @RequestParam(required = false) String query,
        @ApiParam(
            value = "The agency",
            example = "CESSDA"
        ) @RequestParam (required = false) String agency,
        @ApiParam(
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
        produces = MEDIATYPE_JSONLD_VALUE
    )
    @ApiOperation( value = "Search vocabulary codes, produces JSON-LD based on the Skosmos format" )
    public Map<String, Object> getAllVocabulariesCode(
        @ApiParam(
            value = "The query, supports wildcards (e.g. eco*)",
            example = "Economics",
            required = true
        ) @RequestParam ( name = "query") String query,
        @ApiParam(
            value = "The agency",
            example = "CESSDA"
        ) @RequestParam (required = false, name = "agency") String agency,
        @ApiParam(
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @RequestParam String vocab,
        @ApiParam(
            value = "The language",
            example = "en",
            required = true
        ) @RequestParam(name = "lang") String lang,
        @ApiParam(
            value = "Amount of codes to return, maximum 100",
            allowableValues = "range[1,100]",
            example = "20"
        ) @RequestParam( defaultValue = "20", name = "size" ) @Max( value = 100 ) int size
    ) {
        log.debug("REST request to get a page of Vocabularies");
        String filter = "language:" + lang + ";vocab:" + vocab ;

        // Append agency if set
        if( agency != null ) {
            filter += ";agency:" + agency;
        }

        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(query, filter, null, SearchScope.PUBLICATIONSEARCH);
        esq.setCodeSize( size );
        vocabularyService.searchCode(esq);
        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();

        var iterator =  vocabulariesPage.iterator();
        if (iterator.hasNext())
        {
            var vocabularyDTO = iterator.next();
            return ResourceUtils.convertVocabularyDtoToJsonLdSkosMos( vocabularyDTO, vocabularyDTO.getCodes(), lang );
        }
        else
        {
            // empty response
            return Collections.emptyMap();
        }
    }

    /*
     * Vocabularies Endpoints
     */

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param request the request.
     * @param vocabulary the vocabulary.
     * @param versionNumberSl the version number of the source language.
     * @param languageVersion included language versions, e.g. en-4.0_de-4.0.1, separated by _
     * @return Vocabulary in HTML format
     */
    @GetMapping(
        value="/vocabularies/{vocabulary}/{versionNumberSl}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MEDIATYPE_JSONLD_VALUE,
            MediaType.TEXT_HTML_VALUE,
            MediaType.APPLICATION_PDF_VALUE,
            ExportService.MEDIATYPE_WORD_VALUE,
            ExportService.MEDIATYPE_RDF_VALUE
        }
    )
    @ApiOperation( value = "Get a Vocabulary", response = VocabularyDTO.class )
    @ApiResponses( {
        @ApiResponse( code = 200, message = "The vocabulary" ),
        @ApiResponse( code = 404, message = "Vocabulary not found")
    } )
    public ResponseEntity<Object> getVocabulary(
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
    )
    {
        var accept = request.getHeader( "Accept" );
        MediaType mediaType;
        if (accept != null)
        {
            mediaType = MediaType.parseMediaType( accept );
        }
        else
        {
            mediaType = MediaType.ALL;
        }

        // Switch based on compatible media type - default to application/json if unspecified
        if (mediaType.isCompatibleWith( MediaType.APPLICATION_JSON ))
        {
            var vocabularyDTO = getVocabularyJson( vocabulary, versionNumberSl, languageVersion );
            return ResponseEntity.ok( vocabularyDTO );
        }
        else if (mediaType.isCompatibleWith( MEDIATYPE_JSONLD ))
        {
            var jsonLd = getVocabularyJsonLd( vocabulary, versionNumberSl, languageVersion );
            return ResponseEntity.ok( jsonLd );
        }
        else if (mediaType.isCompatibleWith( MediaType.APPLICATION_PDF )
            || mediaType.isCompatibleWith( ExportService.MEDIATYPE_WORD )
            || mediaType.isCompatibleWith( ExportService.MEDIATYPE_RDF ))
        {
            return getVocabularyExport( request, vocabulary, versionNumberSl, languageVersion );
        }
        else if (mediaType.isCompatibleWith( MediaType.TEXT_HTML ))
        {
            return getVocabularyHtml( request, vocabulary, versionNumberSl, languageVersion );
        }

        return ResponseEntity.badRequest().build();
    }


    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param request         the request.
     * @param vocabulary      the vocabulary.
     * @param versionNumberSl the version number of the source language.
     * @param languageVersion included language versions, e.g. en-4.0_de-4.0.1, separated by _
     * @return Vocabulary in HTML format
     */
    public ResponseEntity<Object> getVocabularyHtml(
        HttpServletRequest request, String vocabulary, String versionNumberSl, String languageVersion
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
     * @param vocabulary the vocabulary.
     * @param versionNumberSl the version number of the source language.
     * @param languageVersion included language versions, e.g. en-4.0_de-4.0.1, separated by _.
     * @return Vocabulary
     */
    public VocabularyDTO getVocabularyJson( String vocabulary, String versionNumberSl, String languageVersion ) {
        log.debug(VERSION_WITH_INCLUDED_VERSIONS, vocabulary, versionNumberSl, languageVersion);
        VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions(vocabulary, versionNumberSl, languageVersion);
        if (vocabularyDTO.getVersions().isEmpty()) {
            throw new ResourceNotFoundException( "Unable to find a vocabulary with " + versionNumberSl + " SL version number and/or notation " + vocabulary,  vocabulary, "404");
        } else {
            return vocabularyDTO;
        }
    }

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param vocabulary the vocabulary.
     * @param versionNumberSl the version number of the source language.
     * @param languageVersion included language versions, e.g. en-4.0_de-4.0.1, separated by _
     * @return Vocabulary in JSON-LD format
     */
    public List<Map<String, Object>> getVocabularyJsonLd( String vocabulary, String versionNumberSl, String languageVersion ) {
        log.debug(VERSION_WITH_INCLUDED_VERSIONS, vocabulary, versionNumberSl, languageVersion);
        VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions( vocabulary, versionNumberSl, languageVersion );

        Set<CodeDTO> codeDTOs = CodeDTO.generateCodesFromVersion(vocabularyDTO.getVersions(), false);
        var languageSet = vocabularyDTO.getVersions().stream().map( VersionDTO::getLanguage ).collect( Collectors.toSet() );

        return ResourceUtils.convertVocabularyDtoToJsonLd(vocabularyDTO, codeDTOs, languageSet );
    }

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl} : Get Vocabulary
     *
     * @param request         the request.
     * @param vocabulary      the vocabulary.
     * @param versionNumberSl the version number of the source language.
     * @param languageVersion included language versions, e.g. en-4.0_de-4.0.1, separated by _
     * @return Vocabulary
     */
    public ResponseEntity<Object> getVocabularyExport(
        HttpServletRequest request, String vocabulary, String versionNumberSl, String languageVersion
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

    /*
     * Code Endpoint
     */

    /**
     * {@code GET  /vocabularies/:vocabulary/:versionNumberSl/:language} : Get Vocabulary
     *
     * @param vocabulary the vocabulary.
     * @param versionNumber the version number.
     * @param language the language.
     * @return Set of codes.
     */
    @GetMapping(
        value="/codes/{vocabulary}/{versionNumber}/{language}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation( value = "Get a Code" )
    public Set<ConceptDTO> getCodeJson(
        @ApiParam(
            value = "The vocabulary",
            example = "TopicClassification",
            required = true
        ) @PathVariable String vocabulary,
        @ApiParam(
            value = "The version number",
            example = "4.0",
            required = true
        ) @PathVariable String versionNumber,
        @ApiParam(
            value = "The language" ,
            example = "en",
            required = true
        ) @PathVariable String language
    ) {
        final String langVersion = language + "-" + versionNumber;
        log.debug(VERSION_WITH_INCLUDED_VERSIONS, vocabulary, VersionUtils.getSlVersionNumber(versionNumber), langVersion );
        final VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions(vocabulary, VersionUtils.getSlVersionNumber(versionNumber).toString(), langVersion);
        var versionsIterator = vocabularyDTO.getVersions().iterator();
        if (versionsIterator.hasNext()) {
            return versionsIterator.next().getConcepts();
        } else {
            return Collections.emptySet();
        }
    }

    /*
     * Compare Vocabulary Endpoint
     */

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
        LanguageVersion splitLanguageVersion1 = VersionUtils.splitLanguageVersion(lv1);
        LanguageVersion splitLanguageVersion2 = VersionUtils.splitLanguageVersion(lv2);

        VocabularyDTO vocabulary1 = vocabularyService.getVocabularyByNotationAndVersion( cv, splitLanguageVersion1.getVersionNumber().getBasePatchVersion().toString(), false );
        VocabularyDTO vocabulary2 = vocabularyService.getVocabularyByNotationAndVersion( cv, splitLanguageVersion2.getVersionNumber().getBasePatchVersion().toString(), false );

        VersionDTO version1 = VocabularyUtils.getVersionByLangVersion( vocabulary1, splitLanguageVersion1.getLanguage(), splitLanguageVersion1.getVersionNumber() );
        VersionDTO version2 = VocabularyUtils.getVersionByLangVersion( vocabulary2, splitLanguageVersion2.getLanguage(), splitLanguageVersion2.getVersionNumber());

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
    @ApiOperation( value = "Get a map of all agencies with links to their published controlled vocabularies",
        notes = "The controlled vocabulary links are relative links and must be resolved from the base URL of the server.")
    public Map<String, Map<String, Map<String, Map<VersionNumber, URI>>>> getAllVocabularies(HttpServletRequest request) throws URISyntaxException
    {
        log.debug( "REST: Getting all Vocabularies" );
        var agencyCvMap = new TreeMap<String, Map<String, Map<String, Map<VersionNumber, URI>>>>();

        // Get all vocabularies that are not withdrawn, sorted by notation
        var vocabularies = vocabularyService.findAll().stream()
            .filter( voc -> Boolean.TRUE.equals( voc.isWithdrawn() ) )
            .sorted( Comparator.comparing( VocabularyDTO::getNotation ) )
            .collect( Collectors.toList() );

        for ( VocabularyDTO voc : vocabularies )
        {
            // check if there is published version
            if ( voc.getVersions().stream().anyMatch( v -> v.getStatus() == Status.PUBLISHED ) )
            {
                var vocabMap = agencyCvMap.computeIfAbsent( voc.getAgencyName(), k -> new LinkedHashMap<>() );
                List<VersionDTO> versions = new ArrayList<>( voc.getVersions() );
                versions.sort( Comparator.comparing( VersionDTO::getNumber ) );
                for ( VersionDTO version : versions )
                {
                    var langMap = vocabMap.computeIfAbsent( version.getNotation(), k -> new LinkedHashMap<>() );
                    var versionMap = langMap.computeIfAbsent(
                        version.getLanguage() + "(" + version.getItemType() + ")", k -> new LinkedHashMap<>() );
                    var uriString = request.getContextPath() +
                        "/v2/vocabularies/" + version.getNotation() +
                        "/" + version.getNumber().getBasePatchVersion()
                        + "?languageVersion=" + version.getLanguage() + "-" + version.getNumber();
                    versionMap.put( version.getNumber(), new URI( uriString ) );
                    for ( VersionHistoryDTO versionHistory : version.getVersionHistories() )
                    {
                        var versionHistoryUriString = request.getContextPath() +
                            "/v2/vocabularies/" + version.getNotation() +
                            "/" + versionHistory.getVersion().getBasePatchVersion() +
                            "?languageVersion=" + version.getLanguage() + "-" + versionHistory.getVersion();
                        versionMap.put( versionHistory.getVersion(), new URI( versionHistoryUriString ) );
                    }
                }
            }
        }
        return agencyCvMap;
    }
}
