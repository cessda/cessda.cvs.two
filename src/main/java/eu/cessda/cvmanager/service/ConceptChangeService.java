package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.ConceptChangeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing ConceptChange.
 */
public interface ConceptChangeService {

    /**
     * Save a conceptChange.
     *
     * @param conceptChangeDTO the entity to save
     * @return the persisted entity
     */
    ConceptChangeDTO save(ConceptChangeDTO conceptChangeDTO);

    /**
     * Get all the conceptChanges.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ConceptChangeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" conceptChange.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ConceptChangeDTO findOne(Long id);

    /**
     * Delete the "id" conceptChange.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the conceptChange corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ConceptChangeDTO> search(String query, Pageable pageable);
}
