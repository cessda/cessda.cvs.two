package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.domain.Version;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.repository.VersionRepository;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.mapper.VersionMapper;

import org.gesis.wts.domain.enumeration.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Version.
 */
@Service
@Transactional
public class VersionServiceImpl implements VersionService {

    private final Logger log = LoggerFactory.getLogger(VersionServiceImpl.class);

    private final VersionRepository versionRepository;

    private final VersionMapper versionMapper;

    public VersionServiceImpl(VersionRepository versionRepository, VersionMapper versionMapper) {
        this.versionRepository = versionRepository;
        this.versionMapper = versionMapper;
    }

    /**
     * Save a version.
     *
     * @param versionDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public VersionDTO save(VersionDTO versionDTO) {
        log.debug("Request to save Version : {}", versionDTO);
        Version version = versionMapper.toEntity(versionDTO);
        
        version = versionRepository.save(version);
        return versionMapper.toDto(version);
    }

    /**
     * Get all the versions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VersionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Versions");
        return versionRepository.findAll(pageable)
            .map(versionMapper::toDto);
    }

    /**
     * Get one version by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public VersionDTO findOne(Long id) {
        log.debug("Request to get Version : {}", id);
        Version version = versionRepository.findOneWithEagerRelationships(id);
        return versionMapper.toDto(version);
    }

    /**
     * Delete the version by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Version : {}", id);
        versionRepository.deleteById(id);
    }

    /**
     * Search for the version corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VersionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Versions for query {}", query);
        return null;
    }

	@Override
	public Map<String, List<VersionDTO>> getOrderedLanguageVersionMap(Long vocabularyId) {
		return getOrderedLanguageSpecificVersionMap(vocabularyId, null );
	}
	
	public Map<String, List<VersionDTO>> getOrderedLanguageVersionMap(Long vocabularyId, String status) {
		return getOrderedLanguageSpecificVersionMap(vocabularyId, null , status);
	}
	
	@Override
	public Map<String, List<VersionDTO>> getOrderedLanguageSpecificVersionMap(Long vocabularyId, Language language) {
		List<VersionDTO> versionDTOs = findAllByVocabulary(vocabularyId);
		
		// create map based on language
		Map<String, List<VersionDTO>> langVersionMap = new LinkedHashMap<>();
		for( VersionDTO version : versionDTOs) {
			if( language != null && !language.toString().equalsIgnoreCase( version.getLanguage() ))
					continue;
			
			List<VersionDTO> versions = langVersionMap.get( version.getLanguage() );
			if( versions == null ) {
				versions = new ArrayList<>();
				langVersionMap.put( version.getLanguage(), versions);
			}
			versions.add(version);
		}
		return langVersionMap;
	}
	
	public Map<String, List<VersionDTO>> getOrderedLanguageSpecificVersionMap(Long vocabularyId, Language language, String status) {
		List<VersionDTO> versionDTOs = findAllByVocabulary(vocabularyId);
		
		// create map based on language
		Map<String, List<VersionDTO>> langVersionMap = new LinkedHashMap<>();
		for( VersionDTO version : versionDTOs) {
			if( language != null && !language.toString().equalsIgnoreCase( version.getLanguage() ))
					continue;
			
			if( status != null && !status.equals( version.getStatus() ))
					continue;

			List<VersionDTO> versions = langVersionMap.get( version.getLanguage() );
			if( versions == null ) {
				versions = new ArrayList<>();
				langVersionMap.put( version.getLanguage(), versions);
			}
			versions.add(version);
		}
		return langVersionMap;
	}

	@Override
	public List<VersionDTO> findAllByVocabulary(Long vocabularyId) {
		// TODO Auto-generated method stub
		return versionRepository.findAllByVocabulary( vocabularyId ).stream()
	        .map(versionMapper::toDto)
	        .collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public VersionDTO getByUri(String cvUri) {
		Version version = versionRepository.findByUri( cvUri );
		return versionMapper.toDto( version );
	}

	@Override
	public VersionDTO findOneByNotationLangVersion(String notation, String languageIso, String versionNumber) {
		Version version = versionRepository.findOneByNotationLangVersion(notation, languageIso, versionNumber);
		return versionMapper.toDto( version );
	}

	@Override
	public List<VersionDTO> getTLVersionBySLVersion(VersionDTO slVersion) {
		if( !slVersion.getItemType().equals( ItemType.SL.toString() ))
			return Collections.emptyList();
		
		return versionRepository.findAllByUriSl( slVersion.getUri() ).stream()
		        .map(versionMapper::toDto)
		        .collect(Collectors.toCollection(LinkedList::new));
	}


}
