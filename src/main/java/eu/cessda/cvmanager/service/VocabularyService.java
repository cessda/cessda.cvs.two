package eu.cessda.cvmanager.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.publication.EsQueryResultDetail;

/**
 * Service Interface for managing Vocabulary.
 */
public interface VocabularyService {

    /**
     * Save a vocabulary.
     *
     * @param vocabularyDTO the entity to save
     * @return the persisted entity
     */
    VocabularyDTO save(VocabularyDTO vocabularyDTO);

    /**
     * Get all the vocabularies.
     *
     * @return the list of entities
     */
    List<VocabularyDTO> findAll();
    
    /**
     * Get all the vocabularies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<VocabularyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" vocabulary.
     *
     * @param id the id of the entity
     * @return the entity
     */
    VocabularyDTO findOne(Long id);

    /**
     * Delete the "id" vocabulary.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Delete the vocabulary.
     *
     * @param vocabulary the vocabulary of the entity
     */
    void delete(VocabularyDTO vocabulary);
    
    /**
     * Search for the vocabulary corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<VocabularyDTO> search(String query, Pageable pageable);
    
    EsQueryResultDetail search (EsQueryResultDetail esQueryResultDetail);
    
    EsQueryResultDetail searchPublished (EsQueryResultDetail esQueryResultDetail);

	VocabularyDTO getByUri(String cvUri);

	List<VocabularyDTO> findByAgency(Long agencyId);

	VocabularyDTO getByNotation(String notation);

	
}
