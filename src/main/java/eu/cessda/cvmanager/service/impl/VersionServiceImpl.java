package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.domain.Version;
import eu.cessda.cvmanager.repository.VersionRepository;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.mapper.VersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Version.
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
     * @param versionDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public VersionDTO save(VersionDTO versionDTO) {
        log.debug("Request to save Version : {}", versionDTO);
        Version version = versionMapper.toEntity(versionDTO);
        version = versionRepository.save(version);
        VersionDTO result = versionMapper.toDto(version);
//        versionSearchRepository.save(version);
        return result;
    }

    /**
     * Get all the versions.
     *
     * @param pageable the pagination information
     * @return the list of entities
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
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public VersionDTO findOne(Long id) {
        log.debug("Request to get Version : {}", id);
        Version version = versionRepository.findOneWithEagerRelationships(id);
        return versionMapper.toDto(version);
    }

    /**
     * Delete the version by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Version : {}", id);
        versionRepository.deleteById(id);
//        versionSearchRepository.delete(id);
    }

    /**
     * Search for the version corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VersionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Versions for query {}", query);
//        Page<Version> result = versionSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(versionMapper::toDto);
    }
}
