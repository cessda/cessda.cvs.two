package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.MetadataFieldDTO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing MetadataField.
 */
public interface MetadataFieldService {

    /**
     * Save a metadataField.
     *
     * @param metadataFieldDTO the entity to save
     * @return the persisted entity
     */
    MetadataFieldDTO save(MetadataFieldDTO metadataFieldDTO);

    /**
     * Get all the metadataFields.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MetadataFieldDTO> findAll(Pageable pageable);

    /**
     * Get the "id" metadataField.
     *
     * @param id the id of the entity
     * @return the entity
     */
    MetadataFieldDTO findOne(Long id);

    /**
     * Delete the "id" metadataField.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the metadataField corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MetadataFieldDTO> search(String query, Pageable pageable);
    
    boolean existsByMetadataKey(String metadataKey);

	MetadataFieldDTO findByMetadataKey(String metadataKey);
}
