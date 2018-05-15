package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.VersionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Version.
 */
public interface VersionService {

    /**
     * Save a version.
     *
     * @param versionDTO the entity to save
     * @return the persisted entity
     */
    VersionDTO save(VersionDTO versionDTO);

    /**
     * Get all the versions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<VersionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" version.
     *
     * @param id the id of the entity
     * @return the entity
     */
    VersionDTO findOne(Long id);

    /**
     * Delete the "id" version.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the version corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<VersionDTO> search(String query, Pageable pageable);
}
