package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.UserAgencyRoleService;
import eu.cessda.cvmanager.domain.UserAgencyRole;
import eu.cessda.cvmanager.repository.UserAgencyRoleRepository;
import eu.cessda.cvmanager.service.dto.UserAgencyRoleDTO;
import eu.cessda.cvmanager.service.mapper.UserAgencyRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing UserAgencyRole.
 */
@Service
@Transactional
public class UserAgencyRoleServiceImpl implements UserAgencyRoleService {

    private final Logger log = LoggerFactory.getLogger(UserAgencyRoleServiceImpl.class);

    private final UserAgencyRoleRepository userAgencyRoleRepository;

    private final UserAgencyRoleMapper userAgencyRoleMapper;

    public UserAgencyRoleServiceImpl(UserAgencyRoleRepository userAgencyRoleRepository, UserAgencyRoleMapper userAgencyRoleMapper) {
        this.userAgencyRoleRepository = userAgencyRoleRepository;
        this.userAgencyRoleMapper = userAgencyRoleMapper;
    }

    /**
     * Save a userAgencyRole.
     *
     * @param userAgencyRoleDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UserAgencyRoleDTO save(UserAgencyRoleDTO userAgencyRoleDTO) {
        log.debug("Request to save UserAgencyRole : {}", userAgencyRoleDTO);
        UserAgencyRole userAgencyRole = userAgencyRoleMapper.toEntity(userAgencyRoleDTO);
        userAgencyRole = userAgencyRoleRepository.save(userAgencyRole);
        return userAgencyRoleMapper.toDto(userAgencyRole);
    }

    /**
     * Get all the userAgencyRoles.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserAgencyRoleDTO> findAll() {
        log.debug("Request to get all UserAgencyRoles");
        return userAgencyRoleRepository.findAll().stream()
            .map(userAgencyRoleMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one userAgencyRole by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public UserAgencyRoleDTO findOne(Long id) {
        log.debug("Request to get UserAgencyRole : {}", id);
        UserAgencyRole userAgencyRole = userAgencyRoleRepository.findOne(id);
        return userAgencyRoleMapper.toDto(userAgencyRole);
    }

    /**
     * Delete the userAgencyRole by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserAgencyRole : {}", id);
        userAgencyRoleRepository.delete(id);
    }

	@Override
	public List<UserAgencyRoleDTO> findAll(String keyword) {
		 log.debug("Request to get all UserAgencyRoles");
	        return userAgencyRoleRepository.findAll( keyword ).stream()
	            .map(userAgencyRoleMapper::toDto)
	            .collect(Collectors.toCollection(LinkedList::new));
	}
}
