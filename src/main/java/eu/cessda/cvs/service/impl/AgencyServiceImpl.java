package eu.cessda.cvs.service.impl;

import eu.cessda.cvs.domain.Agency;
import eu.cessda.cvs.domain.search.AgencyStat;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.repository.search.AgencyStatSearchRepository;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.service.mapper.AgencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Agency}.
 */
@Service
@Transactional
public class AgencyServiceImpl implements AgencyService {

    private final Logger log = LoggerFactory.getLogger(AgencyServiceImpl.class);

    private final AgencyRepository agencyRepository;

    private final VocabularyRepository vocabularyRepository;

    private final AgencyMapper agencyMapper;

    private final AgencyStatSearchRepository agencyStatSearchRepository;

    public AgencyServiceImpl(AgencyRepository agencyRepository, VocabularyRepository vocabularyRepository,
                             AgencyMapper agencyMapper, AgencyStatSearchRepository agencyStatSearchRepository) {
        this.agencyRepository = agencyRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.agencyMapper = agencyMapper;
        this.agencyStatSearchRepository = agencyStatSearchRepository;
    }

    /**
     * Save a agency.
     *
     * @param agencyDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public AgencyDTO save(AgencyDTO agencyDTO) {
        log.debug("Request to save Agency : {}", agencyDTO);
        Agency agency = agencyMapper.toEntity(agencyDTO);
        agency = agencyRepository.save(agency);
        return agencyMapper.toDto(agency);
    }

    /**
     * Get all the agencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AgencyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Agencies");

        Page<AgencyDTO> agencyDTOS = agencyRepository.findAll(pageable)
            .map(agencyMapper::toDto);

        if( SecurityUtils.isAuthenticated() && SecurityUtils.isAdminContent() ){
            agencyDTOS.get().forEach( agencyDTO -> agencyDTO.setDeletable( !vocabularyRepository.existsByAgencyId(agencyDTO.getId())));
        }

        return agencyDTOS;
    }


    /**
     * Get one agency by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AgencyDTO> findOne(Long id) {
        log.debug("Request to get Agency : {}", id);
        return agencyRepository.findById(id)
            .map(agencyMapper::toDto);
    }

    /**
     * Delete the agency by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Agency : {}", id);
        agencyRepository.deleteById(id);
    }

    @Override
    public Page<AgencyStat> search(String query, Pageable pageable) {
        log.debug("Request to search Agency : {}", query);
        if ( query.equals( "match_all" ))
            return agencyStatSearchRepository.findAll(pageable);

        return null;
    }

    @Override
    public void index(AgencyDTO agencyDTO) {
        AgencyStat agencyEs = new AgencyStat( agencyDTO );
        agencyStatSearchRepository.save(agencyEs );
    }

    @Override
    public void indexAll() {
        int page = 0;
        int size = 50;
        final long agencyCount = agencyRepository.count();
        do{
            findAll( PageRequest.of(page, size) ).get()
                .forEach(this::index);
            page++;
        } while ( agencyCount > (long) page * size);
    }
}
