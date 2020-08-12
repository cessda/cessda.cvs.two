package eu.cessda.cvs.service.impl;

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.Agency;
import eu.cessda.cvs.domain.Concept;
import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.domain.search.AgencyPublish;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.repository.search.AgencyPublishSearchRepository;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.service.mapper.AgencyMapper;
import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final ApplicationProperties applicationProperties;

    private final JHipsterProperties jHipsterProperties;

    private final AgencyPublishSearchRepository agencyPublishSearchRepository;

    public AgencyServiceImpl(AgencyRepository agencyRepository, VocabularyRepository vocabularyRepository, AgencyMapper agencyMapper, ApplicationProperties applicationProperties, JHipsterProperties jHipsterProperties, AgencyPublishSearchRepository agencyPublishSearchRepository) {
        this.agencyRepository = agencyRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.agencyMapper = agencyMapper;
        this.applicationProperties = applicationProperties;
        this.jHipsterProperties = jHipsterProperties;
        this.agencyPublishSearchRepository = agencyPublishSearchRepository;
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
    public Page<AgencyDTO> search(String query, Pageable pageable) {
        return null;
    }

    @Override
    public void index(AgencyDTO agencyDTO) {
        AgencyPublish agencyEs = new AgencyPublish( agencyDTO );

        Comparator<Version> comparator = Comparator.comparing(Version::getItemType)
            .thenComparing(Version::getLanguage)
            .thenComparing(Version::getNumber, Comparator.reverseOrder());

        List<Vocabulary> vocabularies = vocabularyRepository.findAll();
        for (Vocabulary vocabulary : vocabularies) {
            final String vocabUrl = jHipsterProperties.getMail().getBaseUrl() + "/vocabulary/" + vocabulary.getNotation();

            final List<Version> publishedVersions = vocabulary.getVersions().stream().filter(v -> v.getStatus()
                .equals(Status.PUBLISHED.toString()))
                .sorted(comparator)
                .collect(Collectors.toList());

            final LinkedHashSet<String> languages = publishedVersions.stream().map(Version::getLanguage)
                .collect(Collectors.toCollection(LinkedHashSet::new));

            final AgencyPublish.Vocabulary vocabEs = agencyEs.addVocabulary(vocabulary.getUri(),
                vocabUrl, vocabulary.getSourceLanguage(), vocabulary.getNotation(), languages);


            for (Version version : publishedVersions) {
                vocabEs.addVersion(version.getCanonicalUri(), vocabUrl + "?v=" +
                        version.getCanonicalUri().substring(version.getCanonicalUri().lastIndexOf(':')) +
                        "&lang=" + version.getLanguage(), version.getNumber(), version.getItemType(),
                    version.getLanguage(), version.getPublicationDate(),
                    version.getConcepts().stream().map(Concept::getNotation).collect(Collectors.toList()));
            }
        }

        agencyPublishSearchRepository.save(agencyEs );
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
        } while ( agencyCount > page * size);
    }
}
