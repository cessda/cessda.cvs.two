package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.UserDTO;
import java.util.List;

/**
 * Service Interface for managing User.
 */
public interface UserService {

    /**
     * Save a user.
     *
     * @param userDTO the entity to save
     * @return the persisted entity
     */
    UserDTO save(UserDTO userDTO);

    /**
     * Get all the userS.
     *
     * @return the list of entities
     */
    List<UserDTO> findAll();

    /**
     * Get the "id" user.
     *
     * @param id the id of the entity
     * @return the entity
     */
    UserDTO findOne(Long id);

    /**
     * Delete the "id" user.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

	List<UserDTO> findAll(String value);
}
