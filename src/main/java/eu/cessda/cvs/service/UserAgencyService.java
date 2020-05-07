package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.UserAgencyDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.UserAgency}.
 */
public interface UserAgencyService {

    /**
     * Save a userAgency.
     *
     * @param userAgencyDTO the entity to save.
     * @return the persisted entity.
     */
    UserAgencyDTO save(UserAgencyDTO userAgencyDTO);

    /**
     * Get all the userAgencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserAgencyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" userAgency.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserAgencyDTO> findOne(Long id);

    /**
     * Delete the "id" userAgency.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the userAgency corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserAgencyDTO> search(String query, Pageable pageable);
}
