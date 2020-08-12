package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;


/**
 * REST controller for managing {@link Vocabulary}.
 */
@RestController
@RequestMapping("/api")
public class VocabularySearchResource {

    private final Logger log = LoggerFactory.getLogger(VocabularySearchResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VocabularyService vocabularyService;

    public VocabularySearchResource(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
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

}
