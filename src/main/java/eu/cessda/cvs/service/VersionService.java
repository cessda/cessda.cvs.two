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
package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.utils.VersionNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Version}.
 */
public interface VersionService {

    /**
     * Save a version.
     *
     * @param versionDTO the entity to save.
     * @return the persisted entity.
     */
    VersionDTO save(VersionDTO versionDTO);

    /**
     * Get all the versions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VersionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" version.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VersionDTO> findOne(Long id);

    /**
     * Delete the "id" version.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Find all versions by vocabularyId
     *
     * @param vocabularyId
     * @return
     */
    Stream<VersionDTO> findAllByVocabulary(Long vocabularyId);

    /**
     * Find all published versions by vocabularyId
     *
     * @param vocabularyId
     * @return
     */
    Stream<VersionDTO> findAllPublishedByVocabulary(Long vocabularyId);

    /**
     * Find all older published version than given versionId, from specific vocabulary and language
     *
     * @param vocabularyId
     * @param languageIso
     * @param versionId
     * @return
     */
    Stream<VersionDTO> findOlderPublishedByVocabularyLanguageId(Long vocabularyId, String languageIso, Long versionId);

    /**
     * Find all versions than given versionId, from specific vocabulary and language
     *
     * @param vocabularyId
     * @param versionNumberSl
     * @return
     */
    Stream<VersionDTO> findAllByVocabularyAndVersionSl(Long vocabularyId, VersionNumber versionNumberSl);

    /**
     * Find all published versions than given versionId, from specific vocabulary and language
     *
     * @param vocabularyId
     * @param versionNumberSl
     * @return
     */
    Stream<VersionDTO> findAllPublishedByVocabularyAndVersionSl(Long vocabularyId, VersionNumber versionNumberSl);

    /**
     * Search for the version corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VersionDTO> search(String query, Pageable pageable);

    /**
     * Get list of versions by URN
     * @param urn
     * @return
     */
    Stream<VersionDTO> findByUrn(String urn);

    /**
     * Get list of versions by URN that starting with
     * @param urn
     * @return
     */
    Stream<VersionDTO> findByUrnStartingWith(String urn);

    /**
     * Get list of used languages by version status
     *
     * @param status
     * @return
     */
    Stream<String> findAllLanguagesByStatus(List<String> status);
}
