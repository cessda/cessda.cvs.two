package eu.cessda.cvmanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.data.TreeData;

import eu.cessda.cvmanager.domain.Code;
import eu.cessda.cvmanager.repository.CodeRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.mapper.CodeMapper;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Code.
 */
@Service
@Transactional
public class CodeServiceImpl implements CodeService {

    private final Logger log = LoggerFactory.getLogger(CodeServiceImpl.class);

    private final CodeRepository codeRepository;

    private final CodeMapper codeMapper;
    
    private final ConceptService conceptService;

    public CodeServiceImpl(CodeRepository codeRepository, CodeMapper codeMapper,
    		ConceptService conceptService/*, CodeSearchRepository codeSearchRepository*/) {
        this.codeRepository = codeRepository;
        this.codeMapper = codeMapper;
        this.conceptService = conceptService;
    }

    /**
     * Save a code.
     *
     * @param codeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CodeDTO save(CodeDTO codeDTO) {
        log.debug("Request to save Code : {}", codeDTO);
        Code code = codeMapper.toEntity(codeDTO);
        code = codeRepository.save(code);
		return codeMapper.toDto(code);
    }

    /**
     * Get all the codes.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<CodeDTO> findAll() {
        log.debug("Request to get all Codes");
        return codeRepository.findAll().stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    /**
     * Get all the codes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CodeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Codes");
        return codeRepository.findAll(pageable)
            .map(codeMapper::toDto);
    }

    /**
     * Get one code by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public CodeDTO findOne(Long id) {
        log.debug("Request to get Code : {}", id);
        Code code = codeRepository.getOne(id);
        return codeMapper.toDto(code);
    }

    /**
     * Delete the code by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Code : {}", id);
        codeRepository.deleteById(id);
      
    }
    
	@Override
	public void delete(CodeDTO code) {
		delete( code.getId() );
	}

    /**
     * Search for the code corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CodeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Codes for query {}", query);
		// TODO: needs method implementation
        return null;
    }

	@Override
	public CodeDTO getByUri(String uri) {
		Code code = codeRepository.getByUri(uri);
        return codeMapper.toDto(code);
	}

	@Override
	public CodeDTO getByNotation(String notation) {
		Code code = codeRepository.getByNotation(notation);
        return codeMapper.toDto(code);
	}

	@Override
	public List<CodeDTO> findByVocabulary(Long vocabularyId) {
		log.debug("Request to get all Codes given vocabulary");
        return codeRepository.findAllByVocabulary( vocabularyId ).stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public void deleteCodeTree(CodeDTO pivotCode, Long vocabularyId) {
		List<CodeDTO> codeDTOs = findByVocabulary( vocabularyId );
		// re-save tree structure 
		TreeData<CodeDTO> codeTreeData = CvCodeTreeUtils.getTreeDataByCodes( codeDTOs );
		
		List<CodeDTO> toDeleteCodes = new ArrayList<>();
		toDeleteCodes.add(pivotCode);
		traverseToDeleteCode(toDeleteCodes, codeTreeData, codeTreeData.getChildren(pivotCode));
		
		for(CodeDTO toDeleteCode : toDeleteCodes) {
			List<ConceptDTO> conceptDTOs = conceptService.findAllByCode( toDeleteCode.getId());
			for( ConceptDTO conceptDTO: conceptDTOs)
				conceptService.delete( conceptDTO.getId() );
			delete(toDeleteCode);
		}
		
	}
	
	private void traverseToDeleteCode(List<CodeDTO> codes, TreeData<CodeDTO> codeTree, List<CodeDTO> codesChild) {
		for(CodeDTO code: codesChild) {
			codes.add(code);
			if( !codeTree.getChildren( code ).isEmpty())
				traverseToDeleteCode( codes, codeTree, codeTree.getChildren( code ));
		}
	}

	@Override
	public void deleteCodeTree(TreeData<CodeDTO> treeData, CodeDTO code, VersionDTO version) {
		treeData.getChildren(code).forEach( c -> deleteCodeTree(treeData, c, version));
		// delete direct concept
		List<ConceptDTO> concepts = conceptService.findAllByCode( code.getId());
		for( ConceptDTO concept: concepts) {
			version.getConcepts().remove(concept);
			conceptService.delete( concept.getId());
		}
		
		delete(code);
	}
	
	@Override
	public void deleteCodeTreeTl(TreeData<CodeDTO> treeData, CodeDTO code, VersionDTO version) {
		// delete also related concept
		treeData.getChildren(code).forEach( c -> deleteCodeTreeTl(treeData, c, version));
		
		// delete direct concept
		List<ConceptDTO> concepts = conceptService.findAllByCode( code.getId());
		for( ConceptDTO concept: concepts) {
			version.getConcepts().remove(concept);
			conceptService.delete( concept.getId());
		}
		// clear title definition on that specific code
		code.setTitleDefinition(null, null, version.getLanguage());
		save(code);
	}

	@Override
	public List<CodeDTO> findByVocabularyAndVersionNumber(Long vocabularyId, String versionNumber) {
		log.debug("Request to get all ByVocabularyAndVersionNumber");
        return codeRepository.findByVocabularyAndVersionNumber(vocabularyId, versionNumber).stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public List<CodeDTO> findByVocabularyAndVersion(Long vocabularyId, Long versionId) {
		log.debug("Request to get all ByVocabularyAndVersion");
        return codeRepository.findByVocabularyAndVersion(vocabularyId, versionId).stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public List<CodeDTO> findArchivedByVocabularyAndVersionNumber(Long vocabularyId, String versionNumber) {
		log.debug("Request to get all ArchivedByVocabularyAndVersionNumber");
        return codeRepository.findByArchivedByVocabularyAndVersionNumber(vocabularyId, versionNumber).stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public List<CodeDTO> findArchivedByVocabularyAndVersion(Long vocabularyId, Long versionId) {
		log.debug("Request to get all ArchivedByVocabularyAndVersion");
        return codeRepository.findByArchivedVocabularyAndVersion(vocabularyId, versionId).stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public List<CodeDTO> findWorkflowCodesByVocabulary(Long vocabularyId) {
		log.debug("Request to get all WorkflowCodesByVocabulary");
        return codeRepository.findWorkflowCodesByVocabulary(vocabularyId).stream()
            .map(codeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public void storeCodeTree( TreeData<CodeDTO> codeTree, Set<ConceptDTO> concepts){
		int position = 0;
		for(CodeDTO codeRoot: codeTree.getRootItems()) {
			codeRoot.setPosition(position);
			codeRoot.setParent( null );
			// update concept as well
			Optional<ConceptDTO> conceptOpt = concepts.stream().filter( p -> codeRoot.getId().equals( p.getCodeId() )).findFirst();
			if( conceptOpt.isPresent() ) {
				ConceptDTO concept = conceptOpt.get();
				concept.setNotation( codeRoot.getNotation());
				concept.setPosition(position);
				concept.setParent( null );
				conceptService.save(concept);
				// get TL concepts as well (change position)
				List<ConceptDTO> otherConcepts = conceptService.findAllByCode( codeRoot.getId());
				for(ConceptDTO otherConcept: otherConcepts) {
					if( otherConcept.getPosition() == null || otherConcept.getPosition() != position) {
						otherConcept.setPosition(position);
						otherConcept.setParent( null );
						conceptService.save(otherConcept);
					}
				}
			}
			// store top code
			save(codeRoot);
			position++;
			if( !codeTree.getChildren( codeRoot).isEmpty())
				position = traverseChildCode(  codeTree, codeRoot, codeTree.getChildren(codeRoot), position, concepts );
		}
	}
	
	private int traverseChildCode( TreeData<CodeDTO> codeTree,
			CodeDTO codeParent, List<CodeDTO> codesChild, Integer position, Set<ConceptDTO> concepts) {
		for(CodeDTO code: codesChild) {
			code.setPosition(position);
			code.setParent( codeParent.getNotation());
			// update concept as well
			Optional<ConceptDTO> conceptOpt = concepts.stream().filter( p -> code.getId().equals( p.getCodeId() )).findFirst();
			if( conceptOpt.isPresent() ) {
				ConceptDTO concept = conceptOpt.get();
				concept.setNotation( code.getNotation());
				concept.setParent( code.getParent() );
				concept.setPosition(position);
				conceptService.save(concept);
				// get TL concepts as well (change position)
				List<ConceptDTO> otherConcepts = conceptService.findAllByCode( code.getId());
				for(ConceptDTO otherConcept: otherConcepts) {
					if( otherConcept.getPosition().equals( position)) {
						otherConcept.setPosition(position);
						otherConcept.setParent( code.getParent() );
						conceptService.save(otherConcept);
					}
				}
			}
			// store code
			save(code);
			position++;
			if( !codeTree.getChildren( code ).isEmpty())
				position = traverseChildCode(  codeTree, code, codeTree.getChildren( code ), position, concepts );
		}
		return position;
	}
}
