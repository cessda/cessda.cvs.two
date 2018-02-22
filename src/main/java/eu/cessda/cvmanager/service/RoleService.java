package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.RoleDTO;
import java.util.List;

/**
 * Service Interface for managing Role.
 */
public interface RoleService {

    /**
     * Save a role.
     *
     * @param roleDTO the entity to save
     * @return the persisted entity
     */
    RoleDTO save(RoleDTO roleDTO);

    /**
     * Get all the roles.
     *
     * @return the list of entities
     */
    List<RoleDTO> findAll();

    /**
     * Get the "id" role.
     *
     * @param id the id of the entity
     * @return the entity
     */
    RoleDTO findOne(Long id);

    /**
     * Delete the "id" role.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
