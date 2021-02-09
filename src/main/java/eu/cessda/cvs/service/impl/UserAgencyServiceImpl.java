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

import eu.cessda.cvs.domain.UserAgency;
import eu.cessda.cvs.repository.UserAgencyRepository;
import eu.cessda.cvs.repository.UserRepository;
import eu.cessda.cvs.service.UserAgencyService;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.mapper.UserAgencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link UserAgency}.
 */
@Service
@Transactional
public class UserAgencyServiceImpl implements UserAgencyService {

    private final Logger log = LoggerFactory.getLogger(UserAgencyServiceImpl.class);

    private final UserAgencyRepository userAgencyRepository;

    private final UserAgencyMapper userAgencyMapper;

    private final UserRepository userRepository;

    public UserAgencyServiceImpl(UserAgencyRepository userAgencyRepository, UserAgencyMapper userAgencyMapper, UserRepository userRepository) {
        this.userAgencyRepository = userAgencyRepository;
        this.userAgencyMapper = userAgencyMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a userAgency.
     *
     * @param userAgencyDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UserAgencyDTO save(UserAgencyDTO userAgencyDTO) {
        log.debug("Request to save UserAgency : {}", userAgencyDTO);
        UserAgency userAgency = userAgencyMapper.toEntity(userAgencyDTO);

        if( userAgencyDTO.getUserId() != null ){
            UserAgency finalUserAgency = userAgency;
            userRepository.findById( userAgencyDTO.getUserId() ).ifPresent(finalUserAgency::setUser);
        }

        userAgency = userAgencyRepository.save(userAgency);
        return userAgencyMapper.toDto(userAgency);
    }

    /**
     * Get all the userAgencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserAgencyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserAgencies");
        return userAgencyRepository.findAll(pageable)
            .map(userAgencyMapper::toDto);
    }


    /**
     * Get one userAgency by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserAgencyDTO> findOne(Long id) {
        log.debug("Request to get UserAgency : {}", id);
        return userAgencyRepository.findById(id)
            .map(userAgencyMapper::toDto);
    }

    /**
     * Delete the userAgency by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserAgency : {}", id);
        userAgencyRepository.deleteById(id);
    }

    @Override
    public Page<UserAgencyDTO> search(String query, Pageable pageable) {
        return null;
    }
}
