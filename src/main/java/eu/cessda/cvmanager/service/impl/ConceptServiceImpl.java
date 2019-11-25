package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.domain.Concept;
import eu.cessda.cvmanager.repository.ConceptRepository;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.mapper.ConceptMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Concept.
 */
@Service
@Transactional
public class ConceptServiceImpl implements ConceptService {

    private final Logger log = LoggerFactory.getLogger(ConceptServiceImpl.class);

    private final ConceptRepository conceptRepository;

    private final ConceptMapper conceptMapper;


    public ConceptServiceImpl(ConceptRepository conceptRepository, ConceptMapper conceptMapper) {
        this.conceptRepository = conceptRepository;
        this.conceptMapper = conceptMapper;
    }

    /**
     * Save a concept.
     *
     * @param conceptDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ConceptDTO save(ConceptDTO conceptDTO) {
        log.debug("Request to save Concept : {}", conceptDTO);
        Concept concept = conceptMapper.toEntity(conceptDTO);
        concept = conceptRepository.save(concept);
        return conceptMapper.toDto(concept);
    }

    /**
     * Get all the concepts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ConceptDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Concepts");
        return conceptRepository.findAll(pageable)
            .map(conceptMapper::toDto);
    }

    /**
     * Get one concept by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ConceptDTO findOne(Long id) {
        log.debug("Request to get Concept : {}", id);
        Concept concept = conceptRepository.getOne(id);
        return conceptMapper.toDto(concept);
    }

    /**
     * Delete the concept by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Concept : {}", id);
        conceptRepository.deleteById(id);
    }

    /**
     * Search for the concept corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ConceptDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Concepts for query {}", query);
        // TODO: implement search from database
        return null;
    }

	@Override
	public List<ConceptDTO> findAllByCode(Long codeId) {
		
		return conceptRepository.findAllByCode( codeId ).stream()
	            .map(conceptMapper::toDto)
	            .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public ConceptDTO findOneByCodeNotationAndId(String notation, Long codeId) {
		Concept concept = conceptRepository.findOneByCodeNotationAndId(notation, codeId);
        return conceptMapper.toDto(concept);
	}

	@Override
	public List<ConceptDTO> findByVersion(Long versionId) {
		return conceptRepository.findByVersion( versionId ).stream()
	            .map(conceptMapper::toDto)
	            .collect(Collectors.toCollection(LinkedList::new));
	}
}
