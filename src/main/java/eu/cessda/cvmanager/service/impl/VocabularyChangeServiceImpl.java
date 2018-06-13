package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.domain.VocabularyChange;
import eu.cessda.cvmanager.repository.VocabularyChangeRepository;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyChangeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing VocabularyChange.
 */
@Service
@Transactional
public class VocabularyChangeServiceImpl implements VocabularyChangeService {

    private final Logger log = LoggerFactory.getLogger(VocabularyChangeServiceImpl.class);

    private final VocabularyChangeRepository vocabularyChangeRepository;

    private final VocabularyChangeMapper vocabularyChangeMapper;


    public VocabularyChangeServiceImpl(VocabularyChangeRepository vocabularyChangeRepository, VocabularyChangeMapper vocabularyChangeMapper) {
        this.vocabularyChangeRepository = vocabularyChangeRepository;
        this.vocabularyChangeMapper = vocabularyChangeMapper;
    }

    /**
     * Save a conceptChange.
     *
     * @param vocabularyChangeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public VocabularyChangeDTO save(VocabularyChangeDTO vocabularyChangeDTO) {
        log.debug("Request to save VocabularyChange : {}", vocabularyChangeDTO);
        VocabularyChange vocabularyChange = vocabularyChangeMapper.toEntity(vocabularyChangeDTO);
        vocabularyChange = vocabularyChangeRepository.save(vocabularyChange);
        VocabularyChangeDTO result = vocabularyChangeMapper.toDto(vocabularyChange);
//        conceptChangeSearchRepository.save(conceptChange);
        return result;
    }

    /**
     * Get all the conceptChanges.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyChangeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ConceptChanges");
        return vocabularyChangeRepository.findAll(pageable)
            .map(vocabularyChangeMapper::toDto);
    }

    /**
     * Get one conceptChange by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public VocabularyChangeDTO findOne(Long id) {
        log.debug("Request to get VocabularyChange : {}", id);
        VocabularyChange vocabularyChange = vocabularyChangeRepository.getOne(id);
        return vocabularyChangeMapper.toDto(vocabularyChange);
    }

    /**
     * Delete the conceptChange by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete VocabularyChange : {}", id);
        vocabularyChangeRepository.deleteById(id);
//        conceptChangeSearchRepository.delete(id);
    }

    /**
     * Search for the conceptChange corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyChangeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ConceptChanges for query {}", query);
//        Page<VocabularyChange> result = conceptChangeSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(vocabularyChangeMapper::toDto);
    }
}
