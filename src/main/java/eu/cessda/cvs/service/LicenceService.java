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

import eu.cessda.cvs.service.dto.LicenceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Licence}.
 */
public interface LicenceService {

    /**
     * Save a licence.
     *
     * @param licenceDTO the entity to save.
     * @return the persisted entity.
     */
    LicenceDTO save(LicenceDTO licenceDTO);

    /**
     * Get all the licences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LicenceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" licence.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LicenceDTO> findOne(Long id);

    /**
     * Delete the "id" licence.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the licence corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LicenceDTO> search(String query, Pageable pageable);
}
