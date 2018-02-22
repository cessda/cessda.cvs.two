package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.AgencyService;
import eu.cessda.cvmanager.domain.Agency;
import eu.cessda.cvmanager.repository.AgencyRepository;
import eu.cessda.cvmanager.service.dto.AgencyDTO;
import eu.cessda.cvmanager.service.mapper.AgencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Agency.
 */
@Service
@Transactional
public class AgencyServiceImpl implements AgencyService {

    private final Logger log = LoggerFactory.getLogger(AgencyServiceImpl.class);

    private final AgencyRepository agencyRepository;

    private final AgencyMapper agencyMapper;

    public AgencyServiceImpl(AgencyRepository agencyRepository, AgencyMapper agencyMapper) {
        this.agencyRepository = agencyRepository;
        this.agencyMapper = agencyMapper;
    }

    /**
     * Save a agency.
     *
     * @param agencyDTO the entity to save
     * @return the persisted entity
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
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<AgencyDTO> findAll() {
        log.debug("Request to get all Agencies");
        return agencyRepository.findAll().stream()
            .map(agencyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one agency by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public AgencyDTO findOne(Long id) {
        log.debug("Request to get Agency : {}", id);
        Agency agency = agencyRepository.findOne(id);
        return agencyMapper.toDto(agency);
    }

    /**
     * Delete the agency by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Agency : {}", id);
        agencyRepository.delete(id);
    }

	@Override
	@Transactional(readOnly = true)
	public List<AgencyDTO> findAll(String keyword) {
		if( keyword.isEmpty())
			return findAll();
		log.debug("Request to get all Agencies with specific name or keyword");
        return agencyRepository.findByKeyword( keyword ).stream()
            .map(agencyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}
}
