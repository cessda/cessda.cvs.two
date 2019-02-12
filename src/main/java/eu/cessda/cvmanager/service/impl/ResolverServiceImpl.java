package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.domain.Resolver;
import eu.cessda.cvmanager.repository.ResolverRepository;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.service.mapper.ResolverMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Resolver.
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
     * @param resolverDTO the entity to save
     * @return the persisted entity
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
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResolverDTO> findAll() {
        log.debug("Request to get all Resolvers");
        return resolverRepository.findAll().stream()
            .map(resolverMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the resolvers.
     *
     * @param pageable the pagination information
     * @return the list of entities
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
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ResolverDTO findOne(Long id) {
        log.debug("Request to get Resolver : {}", id);
        Optional<Resolver> findById = resolverRepository.findById(id);
        if( findById.isPresent() ) {
        	Resolver resolver = findById.get();
        	return resolverMapper.toDto(resolver);
        }
        return null;
    }

    /**
     * Delete the resolver by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Resolver : {}", id);
        resolverRepository.deleteById(id);
    }

	@Override
	public ResolverDTO findByResolverURI(String resolverUri) {
		log.debug("Request to get Resolver by URI: {}", resolverUri);
        Optional<Resolver> findById = resolverRepository.findByResolverURI(resolverUri);
        if( findById.isPresent() ) {
        	Resolver resolver = findById.get();
        	return resolverMapper.toDto(resolver);
        }
        return null;
	}
}
