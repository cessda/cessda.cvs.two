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

package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.ConceptDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Concept}.
 */
public interface ConceptService {

    /**
     * Save a concept.
     *
     * @param conceptDTO the entity to save.
     * @return the persisted entity.
     */
    ConceptDTO save(ConceptDTO conceptDTO);

    /**
     * Get all the concepts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ConceptDTO> findAll(Pageable pageable);


    /**
     * Get the "id" concept.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ConceptDTO> findOne(Long id);

    /**
     * Delete the "id" concept.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the concepts given version
     *
     * @param versionId the version information.
     * @return the list of entities.
     */
    List<ConceptDTO> findByVersion(Long versionId);

    /**
     *  Get all the concepts for given notation
     * 
     * @param notation the concept notation
     * @return tjhe list of entities
     */
    List<ConceptDTO> getByNotation(String notation);

    /**
     * Search for the concept corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ConceptDTO> search(String query, Pageable pageable);
}
