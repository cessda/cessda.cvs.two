package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.LicenseService;
import eu.cessda.cvmanager.domain.License;
import eu.cessda.cvmanager.repository.LicenseRepository;
import eu.cessda.cvmanager.service.dto.LicenseDTO;
import eu.cessda.cvmanager.service.mapper.LicenseMapper;
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
 * Service Implementation for managing License.
 */
@Service
@Transactional
public class LicenseServiceImpl implements LicenseService {

    private final Logger log = LoggerFactory.getLogger(LicenseServiceImpl.class);

    private final LicenseRepository licenseRepository;

    private final LicenseMapper licenseMapper;


    public LicenseServiceImpl(LicenseRepository licenseRepository, LicenseMapper licenseMapper) {
        this.licenseRepository = licenseRepository;
        this.licenseMapper = licenseMapper;
    }

    /**
     * Save a license.
     *
     * @param licenseDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public LicenseDTO save(LicenseDTO licenseDTO) {
        log.debug("Request to save License : {}", licenseDTO);
        License license = licenseMapper.toEntity(licenseDTO);
        license = licenseRepository.save(license);
        LicenseDTO result = licenseMapper.toDto(license);
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
    public List<LicenseDTO> findAll() {
        log.debug("Request to get all Licenses");
        return licenseRepository.findAll().stream()
            .map(licenseMapper::toDto)
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
    public Page<LicenseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Licenses");
        return licenseRepository.findAll(pageable)
            .map(licenseMapper::toDto);
    }

    /**
     * Get one license by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public LicenseDTO findOne(Long id) {
        log.debug("Request to get License : {}", id);
        License license = licenseRepository.getOne(id);
        return licenseMapper.toDto(license);
    }

    /**
     * Delete the license by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete License : {}", id);
        licenseRepository.deleteById(id);
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
    public Page<LicenseDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Licenses for query {}", query);
//        Page<License> result = licenseSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(licenseMapper::toDto);
    }
}
