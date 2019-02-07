package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.ResolverDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Resolver.
 */
public interface ResolverService {

    /**
     * Save a resolver.
     *
     * @param resolverDTO the entity to save
     * @return the persisted entity
     */
    ResolverDTO save(ResolverDTO resolverDTO);

    /**
     * Get all the resolvers.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ResolverDTO> findAll(Pageable pageable);

    /**
     * Get the "id" resolver.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ResolverDTO findOne(Long id);

    /**
     * Delete the "id" resolver.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
