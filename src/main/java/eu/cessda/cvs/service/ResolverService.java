package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.ResolverDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Resolver}.
 */
public interface ResolverService {

    /**
     * Save a resolver.
     *
     * @param resolverDTO the entity to save.
     * @return the persisted entity.
     */
    ResolverDTO save(ResolverDTO resolverDTO);

    /**
     * Get all the resolvers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ResolverDTO> findAll(Pageable pageable);

    /**
     * Get the "id" resolver.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ResolverDTO> findOne(Long id);

    /**
     * Delete the "id" resolver.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the resolver corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ResolverDTO> search(String query, Pageable pageable);
}
