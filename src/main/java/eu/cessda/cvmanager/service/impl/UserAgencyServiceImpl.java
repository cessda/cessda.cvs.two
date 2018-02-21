package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.UserAgencyService;
import eu.cessda.cvmanager.domain.UserAgency;
import eu.cessda.cvmanager.repository.UserAgencyRepository;
import eu.cessda.cvmanager.service.dto.UserAgencyDTO;
import eu.cessda.cvmanager.service.mapper.UserAgencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing UserAgency.
 */
@Service
@Transactional
public class UserAgencyServiceImpl implements UserAgencyService {

    private final Logger log = LoggerFactory.getLogger(UserAgencyServiceImpl.class);

    private final UserAgencyRepository userAgencyRepository;

    private final UserAgencyMapper userAgencyMapper;

    public UserAgencyServiceImpl(UserAgencyRepository userAgencyRepository, UserAgencyMapper userAgencyMapper) {
        this.userAgencyRepository = userAgencyRepository;
        this.userAgencyMapper = userAgencyMapper;
    }

    /**
     * Save a userAgency.
     *
     * @param userAgencyDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UserAgencyDTO save(UserAgencyDTO userAgencyDTO) {
        log.debug("Request to save UserAgency : {}", userAgencyDTO);
        UserAgency userAgency = userAgencyMapper.toEntity(userAgencyDTO);
        userAgency = userAgencyRepository.save(userAgency);
        return userAgencyMapper.toDto(userAgency);
    }

    /**
     * Get all the userAgencies.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserAgencyDTO> findAll() {
        log.debug("Request to get all UserAgencies");
        return userAgencyRepository.findAll().stream()
            .map(userAgencyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one userAgency by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public UserAgencyDTO findOne(Long id) {
        log.debug("Request to get UserAgency : {}", id);
        UserAgency userAgency = userAgencyRepository.findOne(id);
        return userAgencyMapper.toDto(userAgency);
    }

    /**
     * Delete the userAgency by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserAgency : {}", id);
        userAgencyRepository.delete(id);
    }

	@Override
	@Transactional(readOnly = true)
	public List<UserAgencyDTO> findAll(String keyword) {
		log.debug("Request to get all UserAgencies");
        return userAgencyRepository.findAll( keyword ).stream()
            .map(userAgencyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}
}
