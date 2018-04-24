package eu.cessda.cvmanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.repository.VocabularyRepository;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Vocabulary.
 */
@Service
@Transactional
public class VocabularyServiceImpl implements VocabularyService {

    private final Logger log = LoggerFactory.getLogger(VocabularyServiceImpl.class);

    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;

    private final VocabularySearchRepository vocabularySearchRepository;

    public VocabularyServiceImpl(VocabularyRepository vocabularyRepository, VocabularyMapper vocabularyMapper, VocabularySearchRepository vocabularySearchRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyMapper = vocabularyMapper;
        this.vocabularySearchRepository = vocabularySearchRepository;
    }

    /**
     * Save a vocabulary.
     *
     * @param vocabularyDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public VocabularyDTO save(VocabularyDTO vocabularyDTO) {
        log.debug("Request to save Vocabulary : {}", vocabularyDTO);
        Vocabulary vocabulary = vocabularyMapper.toEntity(vocabularyDTO);
        vocabulary = vocabularyRepository.save(vocabulary);
        VocabularyDTO result = vocabularyMapper.toDto(vocabulary);
//        if( vocabulary.isDiscoverable() != null && vocabulary.isDiscoverable())
//        	vocabularySearchRepository.save(vocabulary);
        return result;
    }

    /**
     * Get all the vocabularies.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<VocabularyDTO> findAll() {
        log.debug("Request to get all Vocabularies");
        return vocabularyRepository.findAll().stream()
            .map(vocabularyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    /**
     * Get all the vocabularies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Vocabularies");
        return vocabularyRepository.findAll(pageable)
            .map(vocabularyMapper::toDto);
    }


    /**
     * Get one vocabulary by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public VocabularyDTO findOne(Long id) {
        log.debug("Request to get Vocabulary : {}", id);
        Vocabulary vocabulary = vocabularyRepository.getOne(id);
        return vocabularyMapper.toDto(vocabulary);
    }

    /**
     * Delete the vocabulary by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vocabulary : {}", id);
        vocabularyRepository.deleteById(id);
        
    }
    
	@Override
	public void delete(VocabularyDTO vocabulary) {
		if(  vocabulary.isDiscoverable() != null && vocabulary.isDiscoverable() )
			vocabularySearchRepository.deleteById( vocabulary.getId() );
		
		delete( vocabulary.getId() );
	}

    /**
     * Search for the vocabulary corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Vocabularies for query {}", query);
        Page<Vocabulary> result = vocabularySearchRepository.search(queryStringQuery(query), pageable);
        return result.map(vocabularyMapper::toDto);
    }

	@Override
	public VocabularyDTO getByUri(String cvUri) {
		Vocabulary vocabulary = vocabularyRepository.findByUri( cvUri );
        return vocabularyMapper.toDto(vocabulary);
	}

	@Override
	public List<VocabularyDTO> findByAgency(Long agencyId) {
		 log.debug("Request to get all Vocabularies by agency");
	        return vocabularyRepository.findByAgency(agencyId).stream()
	            .map(vocabularyMapper::toDto)
	            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public VocabularyDTO getByNotation(String notation) {
		Vocabulary vocabulary = vocabularyRepository.findByNotation( notation );
        return vocabularyMapper.toDto(vocabulary);
	}
}
