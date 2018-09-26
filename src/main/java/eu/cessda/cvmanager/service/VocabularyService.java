package eu.cessda.cvmanager.service;

import java.util.List;

import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.dto.VersionDTO;
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
    
    Page<VocabularyDTO> findAllWithdrawn(Pageable pageable);
    
    Page<VocabularyDTO> findAllWithdrawn(Long agencyId, Pageable pageable);
    
    void detach( VocabularyDTO vocabularyDTO);
    
    void refresh( VocabularyDTO vocabularyDTO);
    
    EsQueryResultDetail search (EsQueryResultDetail esQueryResultDetail);
    
    EsQueryResultDetail searchPublished (EsQueryResultDetail esQueryResultDetail);

	VocabularyDTO getByUri(String cvUri);

	List<VocabularyDTO> findByAgency(Long agencyId);

	VocabularyDTO getByNotation(String notation);

	boolean existsByNotation(String notation);
	
	void index( VocabularyDTO vocabulary);
	
	void indexPublish( VocabularyDTO vocabulary, VersionDTO currentVersion);
	
	VocabularyDTO withdraw( VocabularyDTO vocabulary );
	
	VocabularyDTO restore( VocabularyDTO vocabulary );
	
	void completeDelete( VocabularyDTO vocabulary );
//	/**
//	 * Create new version of the Vocabulary
//	 * 
//	 * 
//	 * 
//	 * @param currentVocabulary
//	 * @param cvItem
//	 * @return the new cv link
//	 */
//	String createNewVersion(VocabularyDTO vocabulary, CvItem cvItem, Language language);
}
