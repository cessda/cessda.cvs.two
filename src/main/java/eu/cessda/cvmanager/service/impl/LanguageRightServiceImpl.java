package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.LanguageRightService;
import eu.cessda.cvmanager.domain.LanguageRight;
import eu.cessda.cvmanager.repository.LanguageRightRepository;
import eu.cessda.cvmanager.service.dto.LanguageRightDTO;
import eu.cessda.cvmanager.service.mapper.LanguageRightMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing LanguageRight.
 */
@Service
@Transactional
public class LanguageRightServiceImpl implements LanguageRightService {

    private final Logger log = LoggerFactory.getLogger(LanguageRightServiceImpl.class);

    private final LanguageRightRepository languageRightRepository;

    private final LanguageRightMapper languageRightMapper;

    public LanguageRightServiceImpl(LanguageRightRepository languageRightRepository, LanguageRightMapper languageRightMapper) {
        this.languageRightRepository = languageRightRepository;
        this.languageRightMapper = languageRightMapper;
    }

    /**
     * Save a languageRight.
     *
     * @param languageRightDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public LanguageRightDTO save(LanguageRightDTO languageRightDTO) {
        log.debug("Request to save LanguageRight : {}", languageRightDTO);
        LanguageRight languageRight = languageRightMapper.toEntity(languageRightDTO);
        languageRight = languageRightRepository.save(languageRight);
        return languageRightMapper.toDto(languageRight);
    }

    /**
     * Get all the languageRights.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<LanguageRightDTO> findAll() {
        log.debug("Request to get all LanguageRights");
        return languageRightRepository.findAll().stream()
            .map(languageRightMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one languageRight by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public LanguageRightDTO findOne(Long id) {
        log.debug("Request to get LanguageRight : {}", id);
        LanguageRight languageRight = languageRightRepository.findOne(id);
        return languageRightMapper.toDto(languageRight);
    }

    /**
     * Delete the languageRight by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete LanguageRight : {}", id);
        languageRightRepository.delete(id);
    }

	@Override
	public List<LanguageRightDTO> findAll(String keyword) {
		log.debug("Request to get all LanguageRights");
        return languageRightRepository.findAll( keyword ).stream()
            .map(languageRightMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
	}
}
