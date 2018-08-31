package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.LicenseDTO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing License.
 */
public interface LicenseService {

    /**
     * Save a license.
     *
     * @param licenseDTO the entity to save
     * @return the persisted entity
     */
    LicenseDTO save(LicenseDTO licenseDTO);
    
    /**
     * Get all the licenses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    List<LicenseDTO> findAll();

    /**
     * Get all the licenses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LicenseDTO> findAll(Pageable pageable);

    /**
     * Get the "id" license.
     *
     * @param id the id of the entity
     * @return the entity
     */
    LicenseDTO findOne(Long id);

    /**
     * Delete the "id" license.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the license corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LicenseDTO> search(String query, Pageable pageable);
}
