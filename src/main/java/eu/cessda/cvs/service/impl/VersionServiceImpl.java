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

import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.repository.VersionRepository;
import eu.cessda.cvs.service.VersionService;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.mapper.VersionMapper;
import eu.cessda.cvs.utils.VersionNumber;
import eu.cessda.cvs.utils.VocabularyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    public Stream<VersionDTO> findAllByVocabulary(Long vocabularyId) {
        log.debug("Request to get versions by vocabularyId : {}", vocabularyId);
        return versionRepository
            .findByVocabulary(vocabularyId)
            .sorted(
                // #398 - We need to perform sorting by version number here, since at the DB level, the sorting is based on string comparison,
                // and some methods, which rely on ordered results may fail.
                // For instance, if not sorted correctly, the v9.9.9 would be treated as the latest version,
                // despite there is a newer version v9.9.10.
                VocabularyUtils.VERSION_COMPARATOR
            )
            .map(versionMapper::toDto);
    }

    @Override
    public Stream<VersionDTO> findAllPublishedByVocabulary(Long vocabularyId) {
        log.debug("Request to get versions with PUBLISHED status by vocabularyId : {}", vocabularyId);
        return versionRepository
            .findPublishedByVocabulary(vocabularyId)
            .sorted(
                // #398 - We need to perform sorting by version number here, since at the DB level, the sorting is based on string comparison,
                // and some methods, which rely on ordered results may fail.
                // For instance, if not sorted corectly, the v9.9.9 would be treated as the latest version,
                // despite there is a newer version v9.9.10.
                VocabularyUtils.VERSION_COMPARATOR
            )
            .map(versionMapper::toDto);
    }

    @Override
    public Stream<VersionDTO> findOlderPublishedByVocabularyLanguageId(Long vocabularyId, String languageIso, Long versionId) {
        log.debug("Request to get older versions with PUBLISHED status by vocabularyId {} and languageIso {}", vocabularyId, languageIso);
        return versionRepository
            .findOlderPublishedByVocabularyLanguageId(vocabularyId, languageIso, versionId)
            .sorted(
                // #398 - We need to perform sorting by version number here, since at the DB level, the sorting is based on string comparison,
                // and some methods, which rely on ordered results may fail.
                // For instance, if not sorted corectly, the v9.9.9 would be treated as the latest version,
                // despite there is a newer version v9.9.10.
                VocabularyUtils.VERSION_COMPARATOR
            )
            .map(versionMapper::toDto);
    }

    @Override
    public Stream<VersionDTO> findAllByVocabularyAndVersionSl(Long vocabularyId, VersionNumber versionNumberSl) {
        // #398 must be a 2-number string (##.##) in order to perform string prefix search in versionRepository.findAllByVocabularyIdAndVersionNumberSl
        String slNumberPrefix = versionNumberSl.getMinorVersion();
        log.debug("Request to get versions by vocabularyId {}, versionNumberSl prefix {}", vocabularyId, slNumberPrefix);
        return versionRepository
            .findAllByVocabularyIdAndVersionNumberSl(vocabularyId, slNumberPrefix)
            .sorted(
                // #398 - We need to perform sorting by version number here, since at the DB level, the sorting is based on string comparison,
                // and some methods, which rely on ordered results may fail.
                // For instance, if not sorted corectly, the v9.9.9 would be treated as the latest version,
                // despite there is a newer version v9.9.10.
                VocabularyUtils.VERSION_COMPARATOR
            )
            .map(versionMapper::toDto);
    }

    @Override
    public Stream<VersionDTO> findAllPublishedByVocabularyAndVersionSl(Long vocabularyId, VersionNumber versionNumberSl) {
        // #398 must be a 2-number string (##.##) in order to perform string prefix search in versionRepository.findAllPublishedByVocabularyIdAndVersionNumberSl
        String slNumberPrefix = versionNumberSl.getMinorVersion();
        log.debug("Request to get published versions by vocabularyId {}, versionNumberSl prefix {}", vocabularyId, slNumberPrefix);
        return versionRepository
            .findAllPublishedByVocabularyIdAndVersionNumberSl( vocabularyId, slNumberPrefix )
            .sorted(
                // #398 - We need to perform sorting by version number here, since at the DB level, the sorting is based on string comparison,
                // and some methods, which rely on ordered results may fail.
                // For instance, if not sorted corectly, the v9.9.9 would be treated as the latest version,
                // despite there is a newer version v9.9.10.
                VocabularyUtils.VERSION_COMPARATOR
            )
            .map(versionMapper::toDto);
    }

    @Override
    public Page<VersionDTO> search(String query, Pageable pageable) {
        return null;
    }

    @Override
    public Stream<VersionDTO> findByUrn(String urn) {
        log.debug("Request to get versions with URN {}", urn);
        return versionRepository.findByCanonicalUri(urn).map(versionMapper::toDto);
    }

    @Override
    public Stream<VersionDTO> findByUrnStartingWith(String urn) {
        log.debug("Request to get versions with URN starting with{}", urn);
        return versionRepository.findByCanonicalUriStartingWith(urn).map(versionMapper::toDto);
    }

    @Override
    public Stream<String> findAllLanguagesByStatus(List<String> status) {
        log.debug("Request to get languages used in vocabularies by version status {}", status);
        return versionRepository.findLanguagesByStatus(status);
    }
}
