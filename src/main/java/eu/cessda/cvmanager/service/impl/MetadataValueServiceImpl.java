package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.MetadataValueService;
import eu.cessda.cvmanager.domain.MetadataValue;
import eu.cessda.cvmanager.domain.enumeration.ObjectType;
import eu.cessda.cvmanager.repository.MetadataValueRepository;
import eu.cessda.cvmanager.service.dto.MetadataValueDTO;
import eu.cessda.cvmanager.service.mapper.MetadataValueMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing MetadataValue.
 */
@Service
@Transactional
public class MetadataValueServiceImpl implements MetadataValueService {

    private final Logger log = LoggerFactory.getLogger(MetadataValueServiceImpl.class);

    private final MetadataValueRepository metadataValueRepository;

    private final MetadataValueMapper metadataValueMapper;


    public MetadataValueServiceImpl(MetadataValueRepository metadataValueRepository, MetadataValueMapper metadataValueMapper) {
        this.metadataValueRepository = metadataValueRepository;
        this.metadataValueMapper = metadataValueMapper;
    }

    /**
     * Save a metadataValue.
     *
     * @param metadataValueDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public MetadataValueDTO save(MetadataValueDTO metadataValueDTO) {
        log.debug("Request to save MetadataValue : {}", metadataValueDTO);
        MetadataValue metadataValue = metadataValueMapper.toEntity(metadataValueDTO);
        metadataValue = metadataValueRepository.save(metadataValue);
        MetadataValueDTO result = metadataValueMapper.toDto(metadataValue);
//        metadataValueSearchRepository.save(metadataValue);
        return result;
    }

    /**
     * Get all the metadataValues.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MetadataValueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MetadataValues");
        return metadataValueRepository.findAll(pageable)
            .map(metadataValueMapper::toDto);
    }

    /**
     * Get one metadataValue by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public MetadataValueDTO findOne(Long id) {
        log.debug("Request to get MetadataValue : {}", id);
        MetadataValue metadataValue = metadataValueRepository.getOne(id);
        return metadataValueMapper.toDto(metadataValue);
    }

    /**
     * Delete the metadataValue by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MetadataValue : {}", id);
        metadataValueRepository.deleteById(id);
//        metadataValueSearchRepository.delete(id);
    }

    /**
     * Search for the metadataValue corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MetadataValueDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MetadataValues for query {}", query);
//        Page<MetadataValue> result = metadataValueSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(metadataValueMapper::toDto);
    }

	@Override
	public List<MetadataValueDTO> findByMetadataField(String fieldKey, ObjectType objectType) {
		log.debug("Request to get all MetadataValueDTO findByMetadataField");
        return metadataValueRepository.findByMetadataField(fieldKey, objectType).stream()
            .map(metadataValueMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}
}
