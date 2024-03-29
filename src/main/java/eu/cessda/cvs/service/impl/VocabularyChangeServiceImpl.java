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

import eu.cessda.cvs.domain.VocabularyChange;
import eu.cessda.cvs.repository.VocabularyChangeRepository;
import eu.cessda.cvs.service.VocabularyChangeService;
import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import eu.cessda.cvs.service.mapper.VocabularyChangeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link VocabularyChange}.
 */
@Service
@Transactional
public class VocabularyChangeServiceImpl implements VocabularyChangeService {

    private final Logger log = LoggerFactory.getLogger(VocabularyChangeServiceImpl.class);

    private final VocabularyChangeRepository vocabularyChangeRepository;

    private final VocabularyChangeMapper vocabularyChangeMapper;

    public VocabularyChangeServiceImpl(VocabularyChangeRepository vocabularyChangeRepository, VocabularyChangeMapper vocabularyChangeMapper) {
        this.vocabularyChangeRepository = vocabularyChangeRepository;
        this.vocabularyChangeMapper = vocabularyChangeMapper;
    }

    /**
     * Save a vocabularyChange.
     *
     * @param vocabularyChangeDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VocabularyChangeDTO save(VocabularyChangeDTO vocabularyChangeDTO) {
        log.debug("Request to save VocabularyChange : {}", vocabularyChangeDTO);
        VocabularyChange vocabularyChange = vocabularyChangeMapper.toEntity(vocabularyChangeDTO);
        vocabularyChange = vocabularyChangeRepository.save(vocabularyChange);
        return vocabularyChangeMapper.toDto(vocabularyChange);
    }

    /**
     * Get all the vocabularyChanges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyChangeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all VocabularyChanges");
        return vocabularyChangeRepository.findAll(pageable)
            .map(vocabularyChangeMapper::toDto);
    }


    /**
     * Get one vocabularyChange by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VocabularyChangeDTO> findOne(Long id) {
        log.debug("Request to get VocabularyChange : {}", id);
        return vocabularyChangeRepository.findById(id)
            .map(vocabularyChangeMapper::toDto);
    }

    /**
     * Delete the vocabularyChange by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete VocabularyChange : {}", id);
        vocabularyChangeRepository.deleteById(id);
    }

    @Override
    public Page<VocabularyChangeDTO> search(String query, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VocabularyChangeDTO> findByVersionId( Long versionId ) {
        log.debug("Request to get all VocabularyChanges by versionId {}", versionId);
        return vocabularyChangeRepository.findByVersionId(versionId).stream().map(vocabularyChangeMapper::toDto).collect(Collectors.toList());
    }
}
