package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.domain.Licence;
import eu.cessda.cvmanager.repository.LicenceRepository;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.mapper.LicenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Licence.
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
     * Save a license.
     *
     * @param licenceDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public LicenceDTO save(LicenceDTO licenceDTO) {
        log.debug("Request to save Licence : {}", licenceDTO);
        Licence licence = licenceMapper.toEntity(licenceDTO);
        licence = licenceRepository.save(licence);
        LicenceDTO result = licenceMapper.toDto(licence);
//        licenseSearchRepository.save(license);
        return result;
    }
    
    /**
     * Get all the licenses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<LicenceDTO> findAll() {
        log.debug("Request to get all Licenses");
        return licenceRepository.findAll().stream()
            .map(licenceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the licenses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LicenceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Licenses");
        return licenceRepository.findAll(pageable)
            .map(licenceMapper::toDto);
    }

    /**
     * Get one license by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public LicenceDTO findOne(Long id) {
        log.debug("Request to get Licence : {}", id);
        Licence licence = licenceRepository.getOne(id);
        return licenceMapper.toDto(licence);
    }

    /**
     * Delete the license by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Licence : {}", id);
        licenceRepository.deleteById(id);
//        licenseSearchRepository.delete(id);
    }

    /**
     * Search for the license corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LicenceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Licenses for query {}", query);
//        Page<Licence> result = licenseSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(licenceMapper::toDto);
    }
}
