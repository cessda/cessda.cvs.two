package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.UserAgencyDTO;
import java.util.List;

/**
 * Service Interface for managing UserAgency.
 */
public interface UserAgencyService {

    /**
     * Save a userAgency.
     *
     * @param userAgencyDTO the entity to save
     * @return the persisted entity
     */
    UserAgencyDTO save(UserAgencyDTO userAgencyDTO);

    /**
     * Get all the userAgencies.
     *
     * @return the list of entities
     */
    List<UserAgencyDTO> findAll();
    
    /**
     * Get all the userAgencies.
     *
     * @return the list of entities
     */
    List<UserAgencyDTO> findAll(String keyword);

    /**
     * Get the "id" userAgency.
     *
     * @param id the id of the entity
     * @return the entity
     */
    UserAgencyDTO findOne(Long id);

    /**
     * Delete the "id" userAgency.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    
}
