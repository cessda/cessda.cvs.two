package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.VocabularyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller for managing {@link eu.cessda.cvs.domain.Agency}.
 */
@RestController
@RequestMapping("/api/index")
public class IndexingResource {

    private final Logger log = LoggerFactory.getLogger(IndexingResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgencyService agencyService;

    private final VocabularyService vocabularyService;

    public IndexingResource(AgencyService agencyService, VocabularyService vocabularyService) {
        this.agencyService = agencyService;
        this.vocabularyService = vocabularyService;
    }

    @GetMapping("/vocabulary")
    public ResponseEntity<String> indexVocabulary() throws IOException {
        log.debug("REST request to index published Vocabularies");
        vocabularyService.indexAllPublished();
        return ResponseEntity.ok().body("Done indexing published Vocabularies");
    }

    @GetMapping("/vocabulary/editor")
    public ResponseEntity<String> indexVocabularyEditor() throws IOException {
        log.debug("REST request to index Vocabularies in editor");
        vocabularyService.indexAllEditor();
        return ResponseEntity.ok().body("Done indexing Vocabulary Editor");
    }

    @GetMapping("/agency")
    public ResponseEntity<String> indexAgency() throws IOException {
        log.debug("REST request to index Agencies");
        agencyService.indexAll();
        return ResponseEntity.ok().body("Done indexing Agency");
    }

    @GetMapping("/agency-stats")
    public ResponseEntity<String> indexAgencyStats() throws IOException {
        log.debug("REST request to index Agencies Stats");
        vocabularyService.indexAllAgencyStats();
        return ResponseEntity.ok().body("Done indexing Agency Stats");
    }

    @GetMapping("/agency-vocabulary")
    public ResponseEntity<String> indexAgencyVocabulary() throws IOException {
        log.debug("REST request to index published Vocabularies for agency");
        vocabularyService.indexAllVocabForAgency();
        return ResponseEntity.ok().body("Done indexing published Vocabularies for agency");
    }
}
