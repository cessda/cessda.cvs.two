package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.LicenceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Licence}.
 */
public interface LicenceService {

    /**
     * Save a licence.
     *
     * @param licenceDTO the entity to save.
     * @return the persisted entity.
     */
    LicenceDTO save(LicenceDTO licenceDTO);

    /**
     * Get all the licences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LicenceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" licence.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LicenceDTO> findOne(Long id);

    /**
     * Delete the "id" licence.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the licence corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LicenceDTO> search(String query, Pageable pageable);
}
