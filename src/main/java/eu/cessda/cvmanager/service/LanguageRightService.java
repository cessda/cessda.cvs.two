package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.LanguageRightDTO;
import java.util.List;

/**
 * Service Interface for managing LanguageRight.
 */
public interface LanguageRightService {

    /**
     * Save a languageRight.
     *
     * @param languageRightDTO the entity to save
     * @return the persisted entity
     */
    LanguageRightDTO save(LanguageRightDTO languageRightDTO);

    /**
     * Get all the languageRights.
     *
     * @return the list of entities
     */
    List<LanguageRightDTO> findAll();

    /**
     * Get the "id" languageRight.
     *
     * @param id the id of the entity
     * @return the entity
     */
    LanguageRightDTO findOne(Long id);

    /**
     * Delete the "id" languageRight.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

	List<LanguageRightDTO> findAll(String value);
}
