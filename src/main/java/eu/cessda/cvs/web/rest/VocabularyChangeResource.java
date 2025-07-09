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

import eu.cessda.cvs.service.VocabularyChangeService;
import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing {@link eu.cessda.cvs.domain.VocabularyChange}.
 */
@RestController
@RequestMapping("/api/vocabulary-changes")
public class VocabularyChangeResource {

    private final VocabularyChangeService vocabularyChangeService;

    public VocabularyChangeResource(VocabularyChangeService vocabularyChangeService)
    {
        this.vocabularyChangeService = vocabularyChangeService;
    }

    /**
     * {@code GET  /vocabulary-changes/version-id/:versionId} : get all the vocabularyChanges by versionId.
     *
     * @param versionId the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vocabularyChanges in body.
     */
    @GetMapping("/version-id/{versionId}")
    public ResponseEntity<List<VocabularyChangeDTO>> getAllVocabularyChanges(@PathVariable Long versionId) {
        var vocabularyChangeDTOS = vocabularyChangeService.findByVersionId(versionId);
        return ResponseEntity.ok(vocabularyChangeDTOS);
    }
}
