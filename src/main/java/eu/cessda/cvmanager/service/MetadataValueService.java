package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.domain.enumeration.ObjectType;
import eu.cessda.cvmanager.service.dto.MetadataValueDTO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing MetadataValue.
 */
public interface MetadataValueService {

    /**
     * Save a metadataValue.
     *
     * @param metadataValueDTO the entity to save
     * @return the persisted entity
     */
    MetadataValueDTO save(MetadataValueDTO metadataValueDTO);

    /**
     * Get all the metadataValues.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MetadataValueDTO> findAll(Pageable pageable);

    /**
     * Get the "id" metadataValue.
     *
     * @param id the id of the entity
     * @return the entity
     */
    MetadataValueDTO findOne(Long id);

    /**
     * Delete the "id" metadataValue.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the metadataValue corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MetadataValueDTO> search(String query, Pageable pageable);
    
    List<MetadataValueDTO> findByMetadataField(String fieldKey, ObjectType objectType);
}
