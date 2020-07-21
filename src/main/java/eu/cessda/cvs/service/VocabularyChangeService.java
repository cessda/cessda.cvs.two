package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.VocabularyChange}.
 */
public interface VocabularyChangeService {

    /**
     * Save a vocabularyChange.
     *
     * @param vocabularyChangeDTO the entity to save.
     * @return the persisted entity.
     */
    VocabularyChangeDTO save(VocabularyChangeDTO vocabularyChangeDTO);

    /**
     * Get all the vocabularyChanges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VocabularyChangeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" vocabularyChange.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VocabularyChangeDTO> findOne(Long id);

    /**
     * Delete the "id" vocabularyChange.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the vocabularyChange corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VocabularyChangeDTO> search(String query, Pageable pageable);

    /**
     * Get all vocabularyChange corresponding to the version ID.
     *
     * @param versionId the ID of version.
     *
     * @return the list of entities.
     */
    List<VocabularyChangeDTO> findByVersionId(Long versionId);
}
