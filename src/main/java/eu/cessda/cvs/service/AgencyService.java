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

import eu.cessda.cvs.domain.search.AgencyStat;
import eu.cessda.cvs.service.dto.AgencyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Agency}.
 */
public interface AgencyService {

    /**
     * Save a agency.
     *
     * @param agencyDTO the entity to save.
     * @return the persisted entity.
     */
    AgencyDTO save(AgencyDTO agencyDTO);

    /**
     * Get all the agencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgencyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" agency.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AgencyDTO> findOne(Long id);

    /**
     * Delete the "id" agency.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the agency corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgencyStat> search(String query, Pageable pageable);

    /**
     * Perform indexing in a Agency for statistic purpose
     *
     * @param agencyDTO the AgencyDTO needs to be re-indexed
     */
    void index(AgencyDTO agencyDTO);

    /**
     * Perform indexing in all Agency for statistic purpose
     *
     */
    void indexAll();
}
