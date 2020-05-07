package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.ConceptDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Concept}.
 */
public interface ConceptService {

    /**
     * Save a concept.
     *
     * @param conceptDTO the entity to save.
     * @return the persisted entity.
     */
    ConceptDTO save(ConceptDTO conceptDTO);

    /**
     * Get all the concepts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ConceptDTO> findAll(Pageable pageable);


    /**
     * Get the "id" concept.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ConceptDTO> findOne(Long id);

    /**
     * Delete the "id" concept.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the concepts given version
     *
     * @param versionId the version information.
     * @return the list of entities.
     */
    List<ConceptDTO> findByVersion(Long versionId);

    /**
     * Search for the concept corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ConceptDTO> search(String query, Pageable pageable);
}
