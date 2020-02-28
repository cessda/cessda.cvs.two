package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.LicenceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Licence.
 */
public interface LicenceService
{

    /**
     * Save a license.
     *
     * @param licenceDTO the entity to save
     * @return the persisted entity
     */
    LicenceDTO save(LicenceDTO licenceDTO);
    
    /**
     * Get all the licenses.
     *
     * @return the list of entities
     */
    List<LicenceDTO> findAll();

    /**
     * Get all the licenses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LicenceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" license.
     *
     * @param id the id of the entity
     * @return the entity
     */
    LicenceDTO findOne(Long id);

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
    Page<LicenceDTO> search(String query, Pageable pageable);
}
