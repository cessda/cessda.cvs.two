package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.VocabularyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * REST controller for managing {@link eu.cessda.cvs.domain.Agency}.
 */
@RestController
@RequestMapping("/api/maintenance")
public class VocabularyMaintenanceResource {

    private final Logger log = LoggerFactory.getLogger(VocabularyMaintenanceResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgencyService agencyService;

    private final VocabularyService vocabularyService;

    public VocabularyMaintenanceResource(AgencyService agencyService, VocabularyService vocabularyService) {
        this.agencyService = agencyService;
        this.vocabularyService = vocabularyService;
    }

    @GetMapping("/publication/generate-json")
    public ResponseEntity<String> getGenerateJson() throws IOException {
        log.debug("REST request to get a page of Vocabularies");
        vocabularyService.generateJsonAllVocabularyPublish();
        return ResponseEntity.ok().body("Done generating json");
    }

    @GetMapping("/index/agency-stats")
    public ResponseEntity<String> indexAgencyStats() throws IOException {
        log.debug("REST request to index Agencies Stats");
        vocabularyService.indexAllAgencyStats();
        return ResponseEntity.ok().body("Done indexing Agency Stats");
    }

    @GetMapping("/index/vocabulary")
    public ResponseEntity<String> indexVocabulary() throws IOException {
        log.debug("REST request to index published Vocabularies");
        vocabularyService.indexAllPublished();
        return ResponseEntity.ok().body("Done indexing published Vocabularies");
    }

    @GetMapping("/index/vocabulary/editor")
    public ResponseEntity<String> indexVocabularyEditor() throws IOException {
        log.debug("REST request to index Vocabularies in editor");
        vocabularyService.indexAllEditor();
        return ResponseEntity.ok().body("Done indexing Vocabulary Editor");
    }

    @GetMapping("/index/agency")
    public ResponseEntity<String> indexAgency() throws IOException {
        log.debug("REST request to index Agencies");
        agencyService.indexAll();
        return ResponseEntity.ok().body("Done indexing Agency");
    }

    /**
     * {@code GET  check-all/tl-normalization} :
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/check-all/tl-normalization")
    public ResponseEntity<String> checkAllTlNormalization() throws IOException {
        log.debug("REST request to check all Tl_normalization");
        return ResponseEntity.ok().body(vocabularyService.performTlMigrationNormalizationChecking() );
    }

    /**
     * {@code POST  /check/tl-normalization} :
     *
     */
    @PostMapping("/check/tl-normalization")
    public ResponseEntity<String> checkTlNormalization( @RequestBody String notations) throws URISyntaxException {
        log.debug("REST request to perform Tl_normalization with notation {}", notations );
        return ResponseEntity.ok().body(vocabularyService.performTlMigrationNormalization(true, notations) );
    }

    /**
     * {@code POST  /perform/tl-normalization} :
     *
     */
    @PostMapping("/perform/tl-normalization")
    public ResponseEntity<String> performTlNormalization( @RequestBody String notations) throws URISyntaxException {
        log.debug("REST request to perform Tl_normalization with notation {}", notations );
        return ResponseEntity.ok().body(vocabularyService.performTlMigrationNormalization(false, notations) );
    }

}
