package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing VocabularyChange.
 */
public interface VocabularyChangeService {

    /**
     * Save a conceptChange.
     *
     * @param vocabularyChangeDTO the entity to save
     * @return the persisted entity
     */
    VocabularyChangeDTO save(VocabularyChangeDTO vocabularyChangeDTO);

    /**
     * Get all the conceptChanges.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<VocabularyChangeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" conceptChange.
     *
     * @param id the id of the entity
     * @return the entity
     */
    VocabularyChangeDTO findOne(Long id);

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
    Page<VocabularyChangeDTO> search(String query, Pageable pageable);
}
