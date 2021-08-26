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

import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.repository.VersionRepository;
import eu.cessda.cvs.service.VersionService;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.mapper.VersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Version}.
 */
@Service
@Transactional
public class VersionServiceImpl implements VersionService {

    private final Logger log = LoggerFactory.getLogger(VersionServiceImpl.class);

    private final VersionRepository versionRepository;

    private final VersionMapper versionMapper;

    public VersionServiceImpl(VersionRepository versionRepository, VersionMapper versionMapper) {
        this.versionRepository = versionRepository;
        this.versionMapper = versionMapper;
    }

    /**
     * Save a version.
     *
     * @param versionDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public VersionDTO save(VersionDTO versionDTO) {
        log.debug("Request to save Version : {}", versionDTO);
        Version version = versionMapper.toEntity(versionDTO);
        version = versionRepository.save(version);
        return versionMapper.toDto(version);
    }

    /**
     * Get all the versions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VersionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Versions");
        return versionRepository.findAll(pageable)
            .map(versionMapper::toDto);
    }


    /**
     * Get one version by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<VersionDTO> findOne(Long id) {
        log.debug("Request to get Version : {}", id);
        return versionRepository.findById(id)
            .map(versionMapper::toDto);
    }

    /**
     * Delete the version by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Version : {}", id);
        versionRepository.deleteById(id);
    }

    @Override
    public List<VersionDTO> findAllByVocabulary(Long vocabularyId) {
        log.debug("Request to get versions by vocabularyId : {}", vocabularyId);
        return versionRepository.findAllByVocabulary( vocabularyId ).stream()
            .map(versionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<VersionDTO> findAllPublishedByVocabulary(Long vocabularyId) {
        log.debug("Request to get versions with PUBLISHED status by vocabularyId : {}", vocabularyId);
        return versionRepository.findAllPublishedByVocabulary( vocabularyId ).stream()
            .map(versionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<VersionDTO> findOlderPublishedByVocabularyLanguageId(Long vocabularyId, String languageIso, Long versionId) {
        log.debug("Request to get older versions with PUBLISHED status by vocabularyId {} and languageIso {}", vocabularyId, languageIso);
        return versionRepository.findOlderPublishedByVocabularyLanguageId(vocabularyId, languageIso, versionId).stream()
            .map(versionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<VersionDTO> findAllByVocabularyAndVersionSl(Long vocabularyId, String versionNumberSl) {
        log.debug("Request to get versions by vocabularyId {}, versionNumberSl {}", vocabularyId, versionNumberSl);
        return versionRepository.findAllByVocabularyIdAndVersionNumberSl( vocabularyId, versionNumberSl ).stream()
            .map(versionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Set<VersionDTO> findAllPublishedByVocabularyAndVersionSl(Long vocabularyId, String versionNumberSl) {
        log.debug("Request to get published versions by vocabularyId {}, versionNumberSl {}", vocabularyId, versionNumberSl);
        return versionRepository.findAllPublishedByVocabularyIdAndVersionNumberSl( vocabularyId, versionNumberSl ).stream()
            .map(versionMapper::toDto)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Page<VersionDTO> search(String query, Pageable pageable) {
        return null;
    }

    @Override
    public List<VersionDTO> findByUrn(String urn) {
        log.debug("Request to get versions with URN {}", urn);
        return versionRepository.findByCanonicalUri(urn ).stream()
            .map(versionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<VersionDTO> findByUrnStartingWith(String urn) {
        log.debug("Request to get versions with URN starting with{}", urn);
        return versionRepository.findByCanonicalUriStartingWith(urn ).stream()
            .map(versionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
