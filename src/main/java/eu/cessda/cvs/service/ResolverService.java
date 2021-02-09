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

import eu.cessda.cvs.service.dto.ResolverDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Resolver}.
 */
public interface ResolverService {

    /**
     * Save a resolver.
     *
     * @param resolverDTO the entity to save.
     * @return the persisted entity.
     */
    ResolverDTO save(ResolverDTO resolverDTO);

    /**
     * Get all the resolvers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ResolverDTO> findAll(Pageable pageable);

    /**
     * Get the "id" resolver.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ResolverDTO> findOne(Long id);

    /**
     * Delete the "id" resolver.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the resolver corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ResolverDTO> search(String query, Pageable pageable);
}
