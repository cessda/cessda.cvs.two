package eu.cessda.cvs.service.impl;

import eu.cessda.cvs.domain.Resolver;
import eu.cessda.cvs.repository.ResolverRepository;
import eu.cessda.cvs.service.ResolverService;
import eu.cessda.cvs.service.dto.ResolverDTO;
import eu.cessda.cvs.service.mapper.ResolverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Resolver}.
 */
@Service
@Transactional
public class ResolverServiceImpl implements ResolverService {

    private final Logger log = LoggerFactory.getLogger(ResolverServiceImpl.class);

    private final ResolverRepository resolverRepository;

    private final ResolverMapper resolverMapper;

    public ResolverServiceImpl(ResolverRepository resolverRepository, ResolverMapper resolverMapper) {
        this.resolverRepository = resolverRepository;
        this.resolverMapper = resolverMapper;
    }

    /**
     * Save a resolver.
     *
     * @param resolverDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ResolverDTO save(ResolverDTO resolverDTO) {
        log.debug("Request to save Resolver : {}", resolverDTO);
        Resolver resolver = resolverMapper.toEntity(resolverDTO);
        resolver = resolverRepository.save(resolver);
        return resolverMapper.toDto(resolver);
    }

    /**
     * Get all the resolvers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ResolverDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Resolvers");
        return resolverRepository.findAll(pageable)
            .map(resolverMapper::toDto);
    }


    /**
     * Get one resolver by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ResolverDTO> findOne(Long id) {
        log.debug("Request to get Resolver : {}", id);
        return resolverRepository.findById(id)
            .map(resolverMapper::toDto);
    }

    /**
     * Delete the resolver by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Resolver : {}", id);
        resolverRepository.deleteById(id);
    }

    @Override
    public Page<ResolverDTO> search(String query, Pageable pageable) {
        return null;
    }
}
