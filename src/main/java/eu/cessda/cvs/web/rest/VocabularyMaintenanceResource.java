package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.web.rest.domain.Maintenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ResponseEntity<Maintenance> getGenerateJson() throws IOException {
        log.debug("REST request to get a page of Vocabularies");
        final String output = vocabularyService.generateJsonAllVocabularyPublish();
        Maintenance maintenanceOut = new Maintenance(output, "GENERATE_JSON");
        return ResponseEntity.ok().body(maintenanceOut);
    }

    @GetMapping("/index/agency-stats")
    public ResponseEntity<Maintenance> indexAgencyStats() {
        log.debug("REST request to index Agencies Stats");
        vocabularyService.indexAllAgencyStats();
        Maintenance maintenanceOut = new Maintenance("Done indexing Agency Stats", "INDEX_AGENCY_STAT");
        return ResponseEntity.ok().body( maintenanceOut);
    }

    @GetMapping("/index/vocabulary")
    public ResponseEntity<Maintenance> indexVocabulary() {
        log.debug("REST request to index published Vocabularies");
        vocabularyService.indexAllPublished();
        Maintenance maintenanceOut = new Maintenance("Done indexing published Vocabularies", "INDEX_VOCABULARY_PUBLISH");
        return ResponseEntity.ok().body( maintenanceOut);
    }

    @GetMapping("/index/vocabulary/editor")
    public ResponseEntity<Maintenance> indexVocabularyEditor() {
        log.debug("REST request to index Vocabularies in editor");
        vocabularyService.indexAllEditor();
        Maintenance maintenanceOut = new Maintenance("Done indexing Vocabulary Editor", "INDEX_VOCABULARY_EDITOR");
        return ResponseEntity.ok().body(maintenanceOut);
    }

    @GetMapping("/index/agency")
    public ResponseEntity<Maintenance> indexAgency() {
        log.debug("REST request to index Agencies");
        agencyService.indexAll();
        Maintenance maintenanceOut = new Maintenance("Done indexing Agency", "INDEX_AGENCY");
        return ResponseEntity.ok().body(maintenanceOut);
    }

    /**
     * {@code GET  check-all/tl-normalization} :
     *
     * @return result of tl normalization checking
     */
    @GetMapping("/check-all/tl-normalization")
    public ResponseEntity<Maintenance> checkAllTlNormalization() {
        log.debug("REST request to check all Tl_normalization");
        Maintenance maintenanceOut = new Maintenance(vocabularyService.performTlMigrationNormalizationChecking(), "INDEX_AGENCY");
        return ResponseEntity.ok().body( maintenanceOut );
    }

    /**
     * {@code POST  /check/tl-normalization} :
     *
     */
    @PostMapping("/check/tl-normalization")
    public ResponseEntity<String> checkTlNormalization( @RequestBody String notations){
        log.debug("REST request to perform Tl_normalization with notation {}", notations );
        return ResponseEntity.ok().body(vocabularyService.performTlMigrationNormalization(true, notations) );
    }

    /**
     * {@code POST  /perform/tl-normalization} :
     *
     */
    @PostMapping("/perform/tl-normalization")
    public ResponseEntity<String> performTlNormalization( @RequestBody String notations){
        log.debug("REST request to perform Tl_normalization with notation {}", notations );
        return ResponseEntity.ok().body(vocabularyService.performTlMigrationNormalization(false, notations) );
    }

}
