package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.domain.MetadataField;
import eu.cessda.cvmanager.repository.MetadataFieldRepository;
import eu.cessda.cvmanager.service.dto.MetadataFieldDTO;
import eu.cessda.cvmanager.service.mapper.MetadataFieldMapper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing MetadataField.
 */
@Service
@Transactional
public class MetadataFieldServiceImpl implements MetadataFieldService {

    private final Logger log = LoggerFactory.getLogger(MetadataFieldServiceImpl.class);

    private final MetadataFieldRepository metadataFieldRepository;

    private final MetadataFieldMapper metadataFieldMapper;


    public MetadataFieldServiceImpl(MetadataFieldRepository metadataFieldRepository, MetadataFieldMapper metadataFieldMapper) {
        this.metadataFieldRepository = metadataFieldRepository;
        this.metadataFieldMapper = metadataFieldMapper;
    }

    /**
     * Save a metadataField.
     *
     * @param metadataFieldDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public MetadataFieldDTO save(MetadataFieldDTO metadataFieldDTO) {
        log.debug("Request to save MetadataField : {}", metadataFieldDTO);
        MetadataField metadataField = metadataFieldMapper.toEntity(metadataFieldDTO);
        metadataField = metadataFieldRepository.save(metadataField);
        MetadataFieldDTO result = metadataFieldMapper.toDto(metadataField);
//        metadataFieldSearchRepository.save(metadataField);
        return result;
    }

    /**
     * Get all the metadataFields.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MetadataFieldDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MetadataFields");
        return metadataFieldRepository.findAll(pageable)
            .map(metadataFieldMapper::toDto);
    }

    /**
     * Get one metadataField by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public MetadataFieldDTO findOne(Long id) {
        log.debug("Request to get MetadataField : {}", id);
        MetadataField metadataField = metadataFieldRepository.getOne(id);
        return metadataFieldMapper.toDto(metadataField);
    }

    /**
     * Delete the metadataField by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MetadataField : {}", id);
        metadataFieldRepository.deleteById(id);
//        metadataFieldSearchRepository.delete(id);
    }

    /**
     * Search for the metadataField corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MetadataFieldDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MetadataFields for query {}", query);
//        Page<MetadataField> result = metadataFieldSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(metadataFieldMapper::toDto);
    }

	@Override
	public boolean existsByMetadataKey(String metadataKey) {
		return metadataFieldRepository.existsByMetadataKey(metadataKey);
	}

	@Override
	public MetadataFieldDTO findByMetadataKey(String metadataKey) {
		Optional<MetadataField> findByMetadataKey = metadataFieldRepository.findByMetadataKey(metadataKey);
        if( findByMetadataKey.isPresent() ) {
        	MetadataField resolver = findByMetadataKey.get();
        	return metadataFieldMapper.toDto(resolver);
        }
        return null;
	}
}
