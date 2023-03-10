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

import eu.cessda.cvs.domain.MetadataField;
import eu.cessda.cvs.repository.MetadataFieldRepository;
import eu.cessda.cvs.service.MetadataFieldService;
import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import eu.cessda.cvs.service.mapper.MetadataFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link MetadataField}.
 */
@Service
@Transactional
public class MetadataFieldServiceImpl implements MetadataFieldService {

    private final Logger log = LoggerFactory.getLogger(MetadataFieldServiceImpl.class);

    private final MetadataFieldRepository metadataFieldRepository;

    private final MetadataFieldMapper metadataFieldMapper;

    public MetadataFieldServiceImpl(MetadataFieldRepository metadataFieldRepository, MetadataFieldMapper metadataFieldMapper) {
        this.metadataFieldRepository = metadataFieldRepository;
        this.metadataFieldMapper = metadataFieldMapper;
    }

    /**
     * Save a metadataField.
     *
     * @param metadataFieldDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public MetadataFieldDTO save(MetadataFieldDTO metadataFieldDTO) {
        log.debug("Request to save MetadataField : {}", metadataFieldDTO);
        MetadataField metadataField = metadataFieldMapper.toEntity(metadataFieldDTO);
        metadataField = metadataFieldRepository.save(metadataField);
        return metadataFieldMapper.toDto(metadataField);
    }

    /**
     * Get all the metadataFields.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MetadataFieldDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MetadataFields");
        return metadataFieldRepository.findAll(pageable)
            .map(metadataFieldMapper::toDto);
    }


    /**
     * Get one metadataField by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<MetadataFieldDTO> findOne(Long id) {
        log.debug("Request to get MetadataField : {}", id);
        return metadataFieldRepository.findById(id)
            .map(metadataFieldMapper::toDto);
    }

    /**
     * Get the "metadataKey" metadataField.
     *
     * @param metadataKey the metadataKey of the entity.
     * @return the entity.
     */
    @Override
    public Optional<MetadataFieldDTO> findOneByMetadataKey(String metadataKey) {
        log.debug("Request to get MetadataField metadataKey: {}", metadataKey);
        return metadataFieldRepository.findByMetadataKey(metadataKey)
            .map(metadataFieldMapper::toDto);
    }

    /**
     * Get one metadataField by metadataKey.
     *
     * @param metadataKey the metadataKey of the entity.
     * @return the entity.
     */
    @Override
    public Optional<MetadataFieldDTO> findByMetadataKey(String metadataKey) {
        log.debug("Request to get MetadataField by metadataKey : {}", metadataKey);
        return metadataFieldRepository.findByMetadataKey(metadataKey)
            .map(metadataFieldMapper::toDto);
    }

    /**
     * Delete the metadataField by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MetadataField : {}", id);
        metadataFieldRepository.deleteById(id);
    }

    @Override
    public Page<MetadataFieldDTO> search(String query, Pageable pageable) {
        return null;
    }
}
