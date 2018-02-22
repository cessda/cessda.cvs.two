package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.UserAgencyRoleDTO;
import java.util.List;

/**
 * Service Interface for managing UserAgencyRole.
 */
public interface UserAgencyRoleService {

    /**
     * Save a userAgencyRole.
     *
     * @param userAgencyRoleDTO the entity to save
     * @return the persisted entity
     */
    UserAgencyRoleDTO save(UserAgencyRoleDTO userAgencyRoleDTO);

    /**
     * Get all the userAgencyRoles.
     *
     * @return the list of entities
     */
    List<UserAgencyRoleDTO> findAll();

    /**
     * Get the "id" userAgencyRole.
     *
     * @param id the id of the entity
     * @return the entity
     */
    UserAgencyRoleDTO findOne(Long id);

    /**
     * Delete the "id" userAgencyRole.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

	List<UserAgencyRoleDTO> findAll(String keyword);
}
