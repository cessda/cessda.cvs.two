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

package eu.cessda.cvs.service.impl;

import eu.cessda.cvs.domain.MetadataValue;
import eu.cessda.cvs.repository.MetadataValueRepository;
import eu.cessda.cvs.service.MetadataValueService;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.service.mapper.MetadataValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link MetadataValue}.
 */
@Service
@Transactional
public class MetadataValueServiceImpl implements MetadataValueService {

    private final Logger log = LoggerFactory.getLogger(MetadataValueServiceImpl.class);

    private final MetadataValueRepository metadataValueRepository;

    private final MetadataValueMapper metadataValueMapper;

    public MetadataValueServiceImpl(MetadataValueRepository metadataValueRepository, MetadataValueMapper metadataValueMapper) {
        this.metadataValueRepository = metadataValueRepository;
        this.metadataValueMapper = metadataValueMapper;
    }

    /**
     * Save a metadataValue.
     *
     * @param metadataValueDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public MetadataValueDTO save(MetadataValueDTO metadataValueDTO) {
        log.debug("Request to save MetadataValue : {}", metadataValueDTO);
        MetadataValue metadataValue = metadataValueMapper.toEntity(metadataValueDTO);
        metadataValue = metadataValueRepository.save(metadataValue);
        return metadataValueMapper.toDto(metadataValue);
    }

    /**
     * Get all the metadataValues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MetadataValueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MetadataValues");
        return metadataValueRepository.findAll(pageable)
            .map(metadataValueMapper::toDto);
    }


    /**
     * Get one metadataValue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<MetadataValueDTO> findOne(Long id) {
        log.debug("Request to get MetadataValue : {}", id);
        return metadataValueRepository.findById(id)
            .map(metadataValueMapper::toDto);
    }

    /**
     * Delete the metadataValue by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MetadataValue : {}", id);
        metadataValueRepository.deleteById(id);
    }

    @Override
    public Page<MetadataValueDTO> search(String query, Pageable pageable) {
        return null;
    }
}
