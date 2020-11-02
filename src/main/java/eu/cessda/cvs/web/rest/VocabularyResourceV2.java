package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.service.ExportService;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.CodeDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.utils.VersionUtils;
import eu.cessda.cvs.utils.VocabularyUtils;
import eu.cessda.cvs.web.rest.domain.CvResult;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/v2")
public class VocabularyResourceV2 {

    public static final String ATTACHMENT_FILENAME = "attachment; filename=";
    public static final String LANGUAGE = "@language";
    public static final String VALUE = "@value";
    public static final String ID = "@id";
    private final Logger log = LoggerFactory.getLogger(VocabularyResourceV2.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyService vocabularyService;

    public VocabularyResourceV2(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    /**
     * {@code GET  /search} : get all the vocabularies from elasticsearch.
     *

     * @param q the query term.
     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping("/search")
    @ApiOperation( value = "Searching the vocabularies" )
    public ResponseEntity<CvResult> getAllVocabularies(@ApiParam( value = "the query, e.g. Family" ) @RequestParam(name = "q", required = false) String q,
                                                       @ApiParam( value = "the filter, e.g agency:DDI Alliance;language:en" ) @RequestParam(name = "f", required = false) String f,
                                                       Pageable pageable) {
        log.debug("REST request to get a page of Vocabularies");
        if (q == null)
            q = "";
        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(q, f, pageable, SearchScope.PUBLICATIONSEARCH);
        vocabularyService.search(esq);
        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), vocabulariesPage);
        return ResponseEntity.ok().headers(headers).body( VocabularyUtils.mapResultToCvResult(esq, vocabulariesPage) );
    }

//    /**
//     * {@code GET  /search} : get all the vocabularies from elasticsearch.
//     *
//
//     * @param q the query term.
//     * @param pageable the pagination information.
//
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
//     */
//    @GetMapping("/search/jsonld")
//    public ResponseEntity<List<Object>> getAllVocabulariesJsonLd(@RequestParam(name = "q", required = false) String q,
//                                                       @RequestParam(name = "f", required = false) String f,
//                                                       Pageable pageable) {
//        log.debug("REST request to get a page of Vocabularies");
//        if (q == null)
//            q = "";
//        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(q, f, pageable, SearchScope.PUBLICATIONSEARCH);
//        vocabularyService.search(esq);
//        Page<VocabularyDTO> vocabulariesPage = esq.getVocabularies();
//
//        List<Object> vocabularyJsonLds = new ArrayList<>();
//        if ( !vocabulariesPage.getContent().isEmpty() ) {
//            vocabulariesPage.getContent().forEach(vocabularyDTO -> {
//                List<Map<String, Object>> vocabularyJsonLdMap = convertVocabularyDtoToJsonLd(vocabularyDTO);
//                vocabularyJsonLds.addAll(vocabularyJsonLdMap);
//            });
//        }
//
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), vocabulariesPage);
//        return ResponseEntity.ok().headers(headers).body( vocabularyJsonLds );
//    }


    /**
     * {@code GET  /editors/vocabularies/compare-prev/:id} : get the text to be compared by diff-algorithm,
     * given "id" vocabulary and previous version from vocabulary.
     *
     * @param cv the id of the vocabularyDTO to be compared with previous version.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vocabularyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/compare-vocabulary/{cv}/{lv1}/{lv2}")
    @ApiOperation( value = "Get the Vocabulary content of 2 different versions, in order to be compared with diff-app" )
    public ResponseEntity<List<String>> getVocabularyComparePrev(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" )  @PathVariable String cv,
        @ApiParam( value = "the first version e.g 2.0" ) @PathVariable String lv1,
        @ApiParam( value = "the second version e.g 1.0" )@PathVariable String lv2
    ) {
        log.debug("REST request to get Vocabulary comparison text from JSON files of CV {}, between version {} and {}", cv, lv1, lv2);
        final String[] splitLanguageVersion1 = VersionUtils.splitLanguageVersion(lv1);
        final String[] splitLanguageVersion2 = VersionUtils.splitLanguageVersion(lv2);

        VersionDTO version1 = VocabularyUtils.generateVersionByPath(vocabularyService.getPublishedCvPath(cv, splitLanguageVersion1[0]), splitLanguageVersion1[2], splitLanguageVersion1[1]);
        VersionDTO version2 = VocabularyUtils.generateVersionByPath(vocabularyService.getPublishedCvPath(cv, splitLanguageVersion2[0]), splitLanguageVersion2[2], splitLanguageVersion2[1]);

        List<String> compareVersions = VersionUtils.buildComparisonCurrentAndPreviousCV(version1, version2);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Prev-Cv-Version", version2.getNotation() + " " +version2.getItemType() + " v." + version2.getNumber());
        headers.add("X-Current-Cv-Version", version1.getNotation() + " " +version1.getItemType() + " v." + version1.getNumber());
        return ResponseEntity.ok().headers(headers).body(compareVersions);
    }

    /**
     * {@code GET  /v2/vocabularies/json/:notation/:versionNumber}
     *  get a JSON vocabulary {vocabulary} with version {versionNumberSl} with included versions {languageVersion}
     *
     * @param vocabulary controlled vocabulary
     * @param versionNumberSl controlled vocabulary version
     * @param languageVersion included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/vocabularies/json/{vocabulary}/{versionNumberSl}")
    @ApiOperation( value = "Get a Vocabulary in JSON format" )
    public ResponseEntity<VocabularyDTO> getVocabularyInJson(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String vocabulary,
        @ApiParam( value = "the CV SL version, e.g. 1.0" ) @PathVariable String versionNumberSl,
        @ApiParam( value = "included language version, e.g. en-1.0_de-1.0.1, separated by _" ) @RequestParam(name = "languageVersion", required = false) String languageVersion
    ) {
        log.debug("REST request to get a JSON file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);
        VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions(vocabulary, versionNumberSl, languageVersion);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/json"))
            .body(vocabularyDTO);
    }

    /**
     * {@code GET  /v2/vocabularies/jsonld/:notation/:versionNumber}
     *  get a vocabulary {vocabulary} in JSON-LD with version {versionNumberSl} with included versions {languageVersion}
     *
     * @param vocabulary controlled vocabulary
     * @param versionNumberSl controlled vocabulary version
     * @param languageVersion included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/vocabularies/jsonld/{vocabulary}/{versionNumberSl}")
    @ApiOperation( value = "Get a Vocabulary in JSON-LD format" )
    public ResponseEntity<List<Object>> getVocabularyInJsonLd(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String vocabulary,
        @ApiParam( value = "the CV SL version, e.g. 1.0" ) @PathVariable String versionNumberSl,
        @ApiParam( value = "included language version, e.g. en-1.0_de-1.0.1, separated by _" ) @RequestParam(name = "languageVersion", required = false) String languageVersion
    ) {
        log.debug("REST request to get a JSONLD file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);
        VocabularyDTO vocabularyDTO = getVocabularyDTOAndFilterVersions(vocabulary, versionNumberSl, languageVersion);

        List<Object> vocabularyJsonLds = new ArrayList<>();
        List<Map<String, Object>> vocabularyJsonLdMap = convertVocabularyDtoToJsonLd(vocabularyDTO);
        vocabularyJsonLds.addAll(vocabularyJsonLdMap);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/json"))
            .body(vocabularyJsonLds);
    }

    private VocabularyDTO getVocabularyDTOAndFilterVersions(@PathVariable String vocabulary, @PathVariable String versionNumberSl, @RequestParam(name = "languageVersion", required = false) String languageVersion) {
        VocabularyDTO vocabularyDTO = VocabularyUtils.generateVocabularyByPath(vocabularyService.getPublishedCvPath(vocabulary, versionNumberSl));
        vocabularyDTO.setVersions(vocabularyService.filterOutVocabularyVersions(languageVersion, vocabularyDTO));
        return vocabularyDTO;
    }

    private List<Map<String, Object>> convertVocabularyDtoToJsonLd( VocabularyDTO vocabularyDTO){
        List<Map<String, Object>> vocabularyJsonLds = new ArrayList<>();

        Map<String, Object> skosAttributeMap = new HashMap<>();
        VocabularyUtils.setSkosMapAttribute(skosAttributeMap, vocabularyDTO, vocabularyDTO.getVersions().iterator().next());
        final Set<CodeDTO> codeDtos = CodeDTO.generateCodesFromVersion(vocabularyDTO.getVersions(), false);

        Map<String, Object> vocabularyJsonLdMap = new LinkedHashMap<>();
        vocabularyJsonLds.add( vocabularyJsonLdMap);
        // scheme
        String docId = skosAttributeMap.get("docId").toString();
        vocabularyJsonLdMap.put(ID, docId);
        vocabularyJsonLdMap.put("@type", new String[]{"http://www.w3.org/2004/02/skos/core#ConceptScheme"});
        // desc
        List<Map<String,String>> descList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/description", descList);
        vocabularyDTO.getVersions().forEach(v -> {
            Map<String, String> langValue = new LinkedHashMap<>();
            langValue.put(LANGUAGE, v.getLanguage());
            langValue.put(VALUE, v.getDefinition());
            descList.add(langValue);
        });
        // isVersionOf
        List<Map<String,String>> isVersionOfList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/isVersionOf", isVersionOfList);
        Map<String, String> isVersionOfMap = new LinkedHashMap<>();
        isVersionOfMap.put(ID, skosAttributeMap.get("docVersionOf").toString());
        isVersionOfList.add(isVersionOfMap);
        // license
        List<Map<String,String>> licenseList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/license", licenseList);
        Map<String, String> licenseMap = new LinkedHashMap<>();
        licenseMap.put(ID, skosAttributeMap.get("docLicense").toString());
        licenseList.add(licenseMap);
        // rights
        List<Map<String,String>> rightsList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/rights", rightsList);
        Map<String, String> rightsMap = new LinkedHashMap<>();
        rightsMap.put(VALUE, "Copyright ©" + vocabularyDTO.getAgencyName() + " " + vocabularyDTO.getPublicationDate().getYear());
        rightsList.add(rightsMap);
        // title
        List<Map<String,String>> titleList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://purl.org/dc/terms/title", titleList);
        vocabularyDTO.getVersions().forEach(v -> {
            Map<String, String> langValue = new LinkedHashMap<>();
            langValue.put(LANGUAGE, v.getLanguage());
            langValue.put(VALUE, v.getTitle());
            titleList.add(langValue);
        });
        // versionInfo
        List<Map<String,String>> versionInfoList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://www.w3.org/2002/07/owl#versionInfo", versionInfoList);
        Map<String, String> versionInfoMap = new LinkedHashMap<>();
        versionInfoMap.put(VALUE, skosAttributeMap.get("docVersion").toString());
        versionInfoList.add(versionInfoMap);
        // hasTopConcept
        List<Map<String,String>> hasTopConceptList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://www.w3.org/2004/02/skos/core#hasTopConcept", hasTopConceptList);
        codeDtos.forEach(c -> {
            if( c.getParent() == null ) {
                Map<String, String> docIdMap = new LinkedHashMap<>();
                docIdMap.put(ID, docId + "#" + c.getNotation());
                hasTopConceptList.add(docIdMap);
            }
        });
        // notation
        List<Map<String,String>> notationList = new ArrayList<>();
        vocabularyJsonLdMap.put("http://www.w3.org/2004/02/skos/core#notation", notationList);
        Map<String, String> notationMap = new LinkedHashMap<>();
        notationMap.put(VALUE, skosAttributeMap.get("docNotation").toString());
        notationList.add(notationMap);

        // concepts
        codeDtos.forEach(c -> {
            Map<String, Object> conceptJsonLdMap = new LinkedHashMap<>();
            vocabularyJsonLds.add( conceptJsonLdMap);
            // concept
            conceptJsonLdMap.put(ID, docId + "#" + c.getNotation());
            conceptJsonLdMap.put("@type", new String[]{"http://www.w3.org/2004/02/skos/core#Concept"});
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

            vocabularyDTO.getVersions().forEach(v -> {
                Map<String, String> cDefMap = new LinkedHashMap<>();
                cDefMap.put(LANGUAGE, v.getLanguage());
                cDefMap.put(VALUE, c.getDefinitionByLanguage(v.getLanguage()));
                cDefList.add(cDefMap);

                Map<String, String> cTitleMap = new LinkedHashMap<>();
                cTitleMap.put(LANGUAGE, v.getLanguage());
                cTitleMap.put(VALUE, c.getTitleByLanguage(v.getLanguage()));
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
                    cNarrowerMap.put(ID, docId + "#" + c2.getNotation());
                    cNarrowerList.add(cNarrowerMap);
                }
            });
            if( !cNarrowerList.isEmpty() )
                conceptJsonLdMap.put("http://www.w3.org/2004/02/skos/core#narrower", cNarrowerList);
        });

        return vocabularyJsonLds;
    }

    /**
     * {@code GET  /v2/vocabularies/pdf/:notation/:versionNumber}
     *  get a PDF file of vocabulary {vocabulary} with version {versionNumberSl} with included versions {languageVersion}
     *
     * @param vocabulary controlled vocabulary
     * @param versionNumberSl controlled vocabulary version
     * @param languageVersion included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/vocabularies/pdf/{vocabulary}/{versionNumberSl}")
    @ApiOperation( value = "Get a Vocabulary in PDF format" )
    public ResponseEntity<Resource> getVocabularyInPdf(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String vocabulary,
        @ApiParam( value = "the CV SL version, e.g. 1.0" ) @PathVariable String versionNumberSl,
        @ApiParam( value = "included language version, e.g. en-1.0_de-1.0.1, separated by _" ) @RequestParam(name = "languageVersion", required = false) String languageVersion
    ) throws IOException {
        log.debug("REST request to get a PDF file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);
        File pdfFile = vocabularyService.generateVocabularyPublishFileDownload(vocabulary, versionNumberSl, languageVersion, ExportService.DownloadType.PDF, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(pdfFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + pdfFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/pdf"))
            .body(resource);
    }

    /**
     * {@code GET  /v2/vocabularies/docx/:notation/:versionNumber}
     *  get a DOCX file of vocabulary {vocabulary} with version {versionNumberSl} with included versions {languageVersion}
     *
     * @param vocabulary controlled vocabulary
     * @param versionNumberSl controlled vocabulary version
     * @param languageVersion included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/vocabularies/docx/{vocabulary}/{versionNumberSl}")
    @ApiOperation( value = "Get a Vocabulary in DOCX format" )
    public ResponseEntity<Resource> getVocabularyInDocx(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String vocabulary,
        @ApiParam( value = "the CV SL version, e.g. 1.0" ) @PathVariable String versionNumberSl,
        @ApiParam( value = "included language version, e.g. en-1.0_de-1.0.1, separated by _" ) @RequestParam(name = "languageVersion", required = false) String languageVersion
    ) throws IOException {
        log.debug("REST request to get a WORD-DOCX file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);
        File wordFile = vocabularyService.generateVocabularyPublishFileDownload( vocabulary, versionNumberSl, languageVersion, ExportService.DownloadType.WORD, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(wordFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + wordFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            .body(resource);
    }

    /**
     * {@code GET  /v2/html/docx/:notation/:versionNumber}
     *  get a HTML file of vocabulary {vocabulary} with version {versionNumberSl} with included versions {languageVersion}
     *
     * @param vocabulary controlled vocabulary
     * @param versionNumberSl controlled vocabulary version
     * @param languageVersion included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/vocabularies/html/{vocabulary}/{versionNumberSl}")
    @ApiOperation( value = "Get a Vocabulary in HTML format" )
    public ResponseEntity<Resource> getVocabularyInHtml(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String vocabulary,
        @ApiParam( value = "the CV SL version, e.g. 1.0" ) @PathVariable String versionNumberSl,
        @ApiParam( value = "included language version, e.g. en-1.0_de-1.0.1, separated by _" ) @RequestParam(name = "languageVersion", required = false) String languageVersion
    ) throws IOException {
        log.debug("REST request to get a HTML file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);
        File htmlFile = vocabularyService.generateVocabularyPublishFileDownload( vocabulary, versionNumberSl, languageVersion, ExportService.DownloadType.HTML, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(htmlFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + htmlFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("text/html"))
            .body(resource);
    }

    /**
     * {@code GET  /v2/vocabularies/skos/:notation/:versionNumber}
     *  get a SKOS file of vocabulary {vocabulary} with version {versionNumberSl} with included versions {languageVersion}
     *
     * @param vocabulary controlled vocabulary
     * @param versionNumberSl controlled vocabulary version
     * @param languageVersion included version to be exported with format language_version e.g en-1.0_de-1.0.1
     * @return
     * @throws IOException
     */
    @GetMapping("/vocabularies/rdf/{vocabulary}/{versionNumberSl}")
    @ApiOperation( value = "Get a Vocabulary in SKOS format" )
    public ResponseEntity<Resource> getVocabularyInSkos(
        HttpServletRequest request,
        @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String vocabulary,
        @ApiParam( value = "the CV SL version, e.g. 1.0" ) @PathVariable String versionNumberSl,
        @ApiParam( value = "included language version, e.g. en-1.0_de-1.0.1, separated by _" ) @RequestParam(name = "languageVersion", required = false) String languageVersion
    ) throws IOException {
        log.debug("REST request to get a SKOS-RDF file of vocabulary {} with version {} with included versions {}", vocabulary, versionNumberSl, languageVersion);
        File rdfFile = vocabularyService.generateVocabularyPublishFileDownload( vocabulary, versionNumberSl, languageVersion, ExportService.DownloadType.SKOS, request );

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(rdfFile.toPath()) );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + rdfFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("text/xml"))
            .body(resource);
    }

}
