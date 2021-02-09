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

import eu.cessda.cvs.domain.Licence;
import eu.cessda.cvs.repository.LicenceRepository;
import eu.cessda.cvs.service.LicenceService;
import eu.cessda.cvs.service.dto.LicenceDTO;
import eu.cessda.cvs.service.mapper.LicenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Licence}.
 */
@Service
@Transactional
public class LicenceServiceImpl implements LicenceService {

    private final Logger log = LoggerFactory.getLogger(LicenceServiceImpl.class);

    private final LicenceRepository licenceRepository;

    private final LicenceMapper licenceMapper;

    public LicenceServiceImpl(LicenceRepository licenceRepository, LicenceMapper licenceMapper) {
        this.licenceRepository = licenceRepository;
        this.licenceMapper = licenceMapper;
    }

    /**
     * Save a licence.
     *
     * @param licenceDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public LicenceDTO save(LicenceDTO licenceDTO) {
        log.debug("Request to save Licence : {}", licenceDTO);
        Licence licence = licenceMapper.toEntity(licenceDTO);
        licence = licenceRepository.save(licence);
        return licenceMapper.toDto(licence);
    }

    /**
     * Get all the licences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LicenceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Licences");
        return licenceRepository.findAll(pageable)
            .map(licenceMapper::toDto);
    }


    /**
     * Get one licence by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LicenceDTO> findOne(Long id) {
        log.debug("Request to get Licence : {}", id);
        return licenceRepository.findById(id)
            .map(licenceMapper::toDto);
    }

    /**
     * Delete the licence by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Licence : {}", id);
        licenceRepository.deleteById(id);
    }

    @Override
    public Page<LicenceDTO> search(String query, Pageable pageable) {
        return null;
    }
}
