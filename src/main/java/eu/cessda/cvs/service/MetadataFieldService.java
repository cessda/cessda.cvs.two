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

import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.MetadataField}.
 */
public interface MetadataFieldService {

    /**
     * Save a metadataField.
     *
     * @param metadataFieldDTO the entity to save.
     * @return the persisted entity.
     */
    MetadataFieldDTO save(MetadataFieldDTO metadataFieldDTO);

    /**
     * Get all the metadataFields.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MetadataFieldDTO> findAll(Pageable pageable);

    /**
     * Get the "id" metadataField.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MetadataFieldDTO> findOne(Long id);

    /**
     * Get the "metadataKey" metadataField.
     *
     * @param metadataKey the metadataKey of the entity.
     * @return the entity.
     */
    Optional<MetadataFieldDTO> findOneByMetadataKey(String metadataKey);

    /**
     * Get the "metadataKey" metadataField.
     *
     * @param metadataKey the id of the entity.
     * @return the entity.
     */
    Optional<MetadataFieldDTO> findByMetadataKey(String metadataKey);

    /**
     * Delete the "id" metadataField.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the metadataField corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MetadataFieldDTO> search(String query, Pageable pageable);
}
