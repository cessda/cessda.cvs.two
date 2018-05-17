package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.ConceptChangeService;
import eu.cessda.cvmanager.domain.ConceptChange;
import eu.cessda.cvmanager.repository.ConceptChangeRepository;
import eu.cessda.cvmanager.service.dto.ConceptChangeDTO;
import eu.cessda.cvmanager.service.mapper.ConceptChangeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ConceptChange.
 */
@Service
@Transactional
public class ConceptChangeServiceImpl implements ConceptChangeService {

    private final Logger log = LoggerFactory.getLogger(ConceptChangeServiceImpl.class);

    private final ConceptChangeRepository conceptChangeRepository;

    private final ConceptChangeMapper conceptChangeMapper;


    public ConceptChangeServiceImpl(ConceptChangeRepository conceptChangeRepository, ConceptChangeMapper conceptChangeMapper) {
        this.conceptChangeRepository = conceptChangeRepository;
        this.conceptChangeMapper = conceptChangeMapper;
    }

    /**
     * Save a conceptChange.
     *
     * @param conceptChangeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ConceptChangeDTO save(ConceptChangeDTO conceptChangeDTO) {
        log.debug("Request to save ConceptChange : {}", conceptChangeDTO);
        ConceptChange conceptChange = conceptChangeMapper.toEntity(conceptChangeDTO);
        conceptChange = conceptChangeRepository.save(conceptChange);
        ConceptChangeDTO result = conceptChangeMapper.toDto(conceptChange);
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
    public Page<ConceptChangeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ConceptChanges");
        return conceptChangeRepository.findAll(pageable)
            .map(conceptChangeMapper::toDto);
    }

    /**
     * Get one conceptChange by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ConceptChangeDTO findOne(Long id) {
        log.debug("Request to get ConceptChange : {}", id);
        ConceptChange conceptChange = conceptChangeRepository.getOne(id);
        return conceptChangeMapper.toDto(conceptChange);
    }

    /**
     * Delete the conceptChange by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ConceptChange : {}", id);
        conceptChangeRepository.deleteById(id);
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
    public Page<ConceptChangeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ConceptChanges for query {}", query);
//        Page<ConceptChange> result = conceptChangeSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(conceptChangeMapper::toDto);
    }
}
