package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.utils.VersionUtils;
import eu.cessda.cvs.utils.VocabularyUtils;
import eu.cessda.cvs.web.rest.domain.CvResult;
import io.github.jhipster.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/v2")
public class PublicationResource {

    private final Logger log = LoggerFactory.getLogger(PublicationResource.class);
    public static final String JSON_FORMAT = ".json";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyService vocabularyService;
    private final ApplicationProperties applicationProperties;

    public PublicationResource(VocabularyService vocabularyService, ApplicationProperties applicationProperties) {
        this.vocabularyService = vocabularyService;
        this.applicationProperties = applicationProperties;
    }

    /**
     * {@code GET  /search/complete} : get all the vocabularies from elasticsearch.
     *

     * @param q the query term.
     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping("/search/complete")
    public ResponseEntity<EsQueryResultDetail> getAllVocabulariesWithCompleteDetails(
        @RequestParam(name = "q", required = false) String q,
        @RequestParam(name = "f", required = false) String f,
        Pageable pageable) {
        log.debug("REST request to get a page of Vocabularies");
        EsQueryResultDetail esq = VocabularyUtils.prepareEsQuerySearching(q, f, pageable, SearchScope.PUBLICATIONSEARCH);
        vocabularyService.search(esq);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), esq.getVocabularies());
        return ResponseEntity.ok().headers(headers).body(esq);
    }

    /**
     * {@code GET  /search} : get all the vocabularies from elasticsearch.
     *

     * @param q the query term.
     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularies in body.
     */
    @GetMapping("/search")
    public ResponseEntity<CvResult> getAllVocabularies(@RequestParam(name = "q", required = false) String q,
                                                       @RequestParam(name = "f", required = false) String f,
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


    @GetMapping("/search/generate-json")
    public ResponseEntity<String> getGenerateJson() throws IOException {
        log.debug("REST request to get a page of Vocabularies");
        vocabularyService.generateJsonAllVocabularyPublish();
        return ResponseEntity.ok().body("Done generating json");
    }

    /**
     * {@code GET  /editors/vocabularies/compare-prev/:id} : get the text to be compared by diff-algorithm,
     * given "id" vocabulary and previous version from vocabulary.
     *
     * @param cv the id of the vocabularyDTO to be compared with previous version.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vocabularyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/compare-vocabulary/{cv}/{lv1}/{lv2}")
    public ResponseEntity<List<String>> getVocabularyComparePrev(
        HttpServletRequest request,
        @PathVariable String cv,
        @PathVariable String lv1,
        @PathVariable String lv2
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

}
