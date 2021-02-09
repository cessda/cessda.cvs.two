/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.web.rest.domain.Maintenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/publication/generate-json")
    public ResponseEntity<Maintenance> getGenerateJson() throws IOException {
        log.debug("REST request to get a page of Vocabularies");
        final String output = vocabularyService.generateJsonAllVocabularyPublish();
        Maintenance maintenanceOut = new Maintenance(output, "GENERATE_JSON");
        return ResponseEntity.ok().body(maintenanceOut);
    }

    @PostMapping("/index/agency-stats")
    public ResponseEntity<Maintenance> indexAgencyStats() {
        log.debug("REST request to index Agencies Stats");
        vocabularyService.indexAllAgencyStats();
        Maintenance maintenanceOut = new Maintenance("Done indexing Agency Stats", "INDEX_AGENCY_STAT");
        return ResponseEntity.ok().body( maintenanceOut);
    }

    @PostMapping("/index/vocabulary")
    public ResponseEntity<Maintenance> indexVocabulary() {
        log.debug("REST request to index published Vocabularies");
        vocabularyService.indexAllPublished();
        Maintenance maintenanceOut = new Maintenance("Done indexing published Vocabularies", "INDEX_VOCABULARY_PUBLISH");
        return ResponseEntity.ok().body( maintenanceOut);
    }

    @PostMapping("/index/vocabulary/editor")
    public ResponseEntity<Maintenance> indexVocabularyEditor() {
        log.debug("REST request to index Vocabularies in editor");
        vocabularyService.indexAllEditor();
        Maintenance maintenanceOut = new Maintenance("Done indexing Vocabulary Editor", "INDEX_VOCABULARY_EDITOR");
        return ResponseEntity.ok().body(maintenanceOut);
    }

    @PostMapping("/index/agency")
    public ResponseEntity<Maintenance> indexAgency() {
        log.debug("REST request to index Agencies");
        agencyService.indexAll();
        Maintenance maintenanceOut = new Maintenance("Done indexing Agency", "INDEX_AGENCY");
        return ResponseEntity.ok().body(maintenanceOut);
    }
}
