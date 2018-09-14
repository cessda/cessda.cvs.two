package eu.cessda.cvmanager.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vaadin.data.TreeData;

import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;

/**
 * Service Interface for managing Code.
 */
public interface CodeService {

    /**
     * Save a code.
     *
     * @param codeDTO the entity to save
     * @return the persisted entity
     */
    CodeDTO save(CodeDTO codeDTO);

    /**
     * Get all the codes.
     *
     * @return the list of entities
     */
    List<CodeDTO> findAll();
    
    /**
     * Get all the codes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CodeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" code.
     *
     * @param id the id of the entity
     * @return the entity
     */
    CodeDTO findOne(Long id);

    /**
     * Delete the "id" code.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Delete the code.
     *
     * @param code the code of the entity
     */
    void delete(CodeDTO code);
    
    /**
     * Search for the code corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CodeDTO> search(String query, Pageable pageable);

	CodeDTO getByUri(String uri);

	CodeDTO getByNotation(String notation);
	
	List<CodeDTO> findByVocabulary( Long vocabularyId);
	
	List<CodeDTO> findByVocabularyAndVersionNumber( Long vocabularyId, String versionNumber);
	
	List<CodeDTO> findByVocabularyAndVersion( Long vocabularyId, Long versionId);
	
	List<CodeDTO> findArchivedByVocabularyAndVersionNumber( Long vocabularyId, String versionNumber);
	
	List<CodeDTO> findArchivedByVocabularyAndVersion( Long vocabularyId, Long versionId);
	
	List<CodeDTO> findWorkflowCodesByVocabulary( Long vocabularyId );
	
	void deleteCodeTree( CodeDTO pivotCode, Long vocabularyId);
	
	void deleteCodeTree( TreeData<CodeDTO> treeData, CodeDTO code, VersionDTO currentVersion);
	
	void storeCodeTree( TreeData<CodeDTO> treeData);
}
