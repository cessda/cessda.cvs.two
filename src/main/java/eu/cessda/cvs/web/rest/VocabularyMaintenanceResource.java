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

import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.web.rest.domain.Maintenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static eu.cessda.cvs.web.rest.domain.Maintenance.Operation.*;

/**
 * REST controller for managing {@link eu.cessda.cvs.domain.Agency}.
 */
@RestController
@RequestMapping("/api/maintenance")
public class VocabularyMaintenanceResource {

    private static final Logger log = LoggerFactory.getLogger(VocabularyMaintenanceResource.class);

    private final AgencyService agencyService;
    private final VocabularyService vocabularyService;

    public VocabularyMaintenanceResource(AgencyService agencyService, VocabularyService vocabularyService) {
        this.agencyService = agencyService;
        this.vocabularyService = vocabularyService;
    }

    @PostMapping("/publication/generate-json")
    public Maintenance getGenerateJson()
    {
        log.debug("REST request to get a page of Vocabularies");
        final String output = vocabularyService.generateJsonAllVocabularyPublish();
        return new Maintenance(output, GENERATE_JSON);
    }

    @PostMapping("/index/agency-stats")
    public Maintenance indexAgencyStats() {
        log.debug("REST request to index Agencies Stats");
        vocabularyService.indexAllAgencyStats();
        return new Maintenance("Done indexing Agency Stats", INDEX_AGENCY_STAT);
    }

    @PostMapping("/index/vocabulary")
    public Maintenance indexVocabulary() {
        log.debug("REST request to index published Vocabularies");
        vocabularyService.indexAllPublished();
        return new Maintenance("Done indexing published Vocabularies", INDEX_VOCABULARY_PUBLISH);
    }

    @PostMapping("/index/vocabulary/editor")
    public Maintenance indexVocabularyEditor() {
        log.debug("REST request to index Vocabularies in editor");
        vocabularyService.indexAllEditor();
        return new Maintenance("Done indexing Vocabulary Editor", INDEX_VOCABULARY_EDITOR);
    }

    @PostMapping("/index/agency")
    public Maintenance indexAgency() {
        log.debug("REST request to index Agencies");
        agencyService.indexAll();
        return new Maintenance("Done indexing Agency", INDEX_AGENCY);
    }
}
