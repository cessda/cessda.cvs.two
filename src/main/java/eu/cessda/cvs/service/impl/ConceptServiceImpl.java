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
package eu.cessda.cvs.service.impl;

import eu.cessda.cvs.domain.Concept;
import eu.cessda.cvs.repository.ConceptRepository;
import eu.cessda.cvs.service.ConceptService;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.mapper.ConceptMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Concept}.
 */
@Service
@Transactional
public class ConceptServiceImpl implements ConceptService {

    private final Logger log = LoggerFactory.getLogger(ConceptServiceImpl.class);

    private final ConceptRepository conceptRepository;

    private final ConceptMapper conceptMapper;

    public ConceptServiceImpl(ConceptRepository conceptRepository, ConceptMapper conceptMapper) {
        this.conceptRepository = conceptRepository;
        this.conceptMapper = conceptMapper;
    }

    /**
     * Save a concept.
     *
     * @param conceptDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ConceptDTO save(ConceptDTO conceptDTO) {
        log.debug("Request to save Concept : {}", conceptDTO);
        Concept concept = conceptMapper.toEntity(conceptDTO);
        concept = conceptRepository.save(concept);
        return conceptMapper.toDto(concept);
    }

    /**
     * Get all the concepts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ConceptDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Concepts");
        return conceptRepository.findAll(pageable)
            .map(conceptMapper::toDto);
    }


    /**
     * Get one concept by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ConceptDTO> findOne(Long id) {
        log.debug("Request to get Concept : {}", id);
        return conceptRepository.findById(id)
            .map(conceptMapper::toDto);
    }

    /**
     * Delete the concept by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Concept : {}", id);
        conceptRepository.deleteById(id);
    }

    @Override
    public List<ConceptDTO> findByVersion(Long versionId) {
        log.debug("Request to get all Concepts belong to version {}", versionId);
        return conceptRepository.findByVersion( versionId ).stream()
            .map(conceptMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Page<ConceptDTO> search(String query, Pageable pageable) {
        return null;
    }
}
