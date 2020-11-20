package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.repository.VersionRepository;
import eu.cessda.cvs.service.VersionService;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.mapper.VersionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.*;
import java.util.List;

import static eu.cessda.cvs.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link VersionResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class VersionResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PUBLICATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PUBLICATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_URI = "AAAAAAAAAA";
    private static final String UPDATED_URI = "BBBBBBBBBB";

    private static final String DEFAULT_CANONICAL_URI = "AAAAAAAAAA";
    private static final String UPDATED_CANONICAL_URI = "BBBBBBBBBB";

    private static final String DEFAULT_URI_SL = "AAAAAAAAAA";
    private static final String UPDATED_URI_SL = "BBBBBBBBBB";

    private static final String DEFAULT_NOTATION = "AAAAAAAAAA";
    private static final String UPDATED_NOTATION = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION = "BBBBBBBBBB";

    private static final Long DEFAULT_PREVIOUS_VERSION = 1L;
    private static final Long UPDATED_PREVIOUS_VERSION = 2L;

    private static final Long DEFAULT_INITIAL_VERSION = 1L;
    private static final Long UPDATED_INITIAL_VERSION = 2L;

    private static final Long DEFAULT_CREATOR = 1L;
    private static final Long UPDATED_CREATOR = 2L;

    private static final Long DEFAULT_PUBLISHER = 1L;
    private static final Long UPDATED_PUBLISHER = 2L;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_NOTES = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_CHANGES = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_CHANGES = "BBBBBBBBBB";

    private static final String DEFAULT_DISCUSSION_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_DISCUSSION_NOTES = "BBBBBBBBBB";

    private static final String DEFAULT_LICENSE = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE = "BBBBBBBBBB";

    private static final Long DEFAULT_LICENSE_ID = 1L;
    private static final Long UPDATED_LICENSE_ID = 2L;

    private static final String DEFAULT_CITATION = "AAAAAAAAAA";
    private static final String UPDATED_CITATION = "BBBBBBBBBB";

    private static final String DEFAULT_DDI_USAGE = "AAAAAAAAAA";
    private static final String UPDATED_DDI_USAGE = "BBBBBBBBBB";

    private static final String DEFAULT_TRANSLATE_AGENCY = "AAAAAAAAAA";
    private static final String UPDATED_TRANSLATE_AGENCY = "BBBBBBBBBB";

    private static final String DEFAULT_TRANSLATE_AGENCY_LINK = "AAAAAAAAAA";
    private static final String UPDATED_TRANSLATE_AGENCY_LINK = "BBBBBBBBBB";

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private VersionMapper versionMapper;

    @Autowired
    private VersionService versionService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVersionMockMvc;

    private Version version;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Version createEntity(EntityManager em) {
        Version version = new Version()
            .status(DEFAULT_STATUS)
            .itemType(DEFAULT_ITEM_TYPE)
            .language(DEFAULT_LANGUAGE)
            .creationDate(DEFAULT_CREATION_DATE)
            .publicationDate(DEFAULT_PUBLICATION_DATE)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .number(DEFAULT_NUMBER)
            .uri(DEFAULT_URI)
            .canonicalUri(DEFAULT_CANONICAL_URI)
            .uriSl(DEFAULT_URI_SL)
            .notation(DEFAULT_NOTATION)
            .title(DEFAULT_TITLE)
            .definition(DEFAULT_DEFINITION)
            .previousVersion(DEFAULT_PREVIOUS_VERSION)
            .initialVersion(DEFAULT_INITIAL_VERSION)
            .creator(DEFAULT_CREATOR)
            .publisher(DEFAULT_PUBLISHER)
            .notes(DEFAULT_NOTES)
            .versionNotes(DEFAULT_VERSION_NOTES)
            .versionChanges(DEFAULT_VERSION_CHANGES)
            .discussionNotes(DEFAULT_DISCUSSION_NOTES)
            .license(DEFAULT_LICENSE)
            .licenseId(DEFAULT_LICENSE_ID)
            .citation(DEFAULT_CITATION)
            .ddiUsage(DEFAULT_DDI_USAGE)
            .translateAgency(DEFAULT_TRANSLATE_AGENCY)
            .translateAgencyLink(DEFAULT_TRANSLATE_AGENCY_LINK);
        return version;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Version createUpdatedEntity(EntityManager em) {
        Version version = new Version()
            .status(UPDATED_STATUS)
            .itemType(UPDATED_ITEM_TYPE)
            .language(UPDATED_LANGUAGE)
            .creationDate(UPDATED_CREATION_DATE)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .number(UPDATED_NUMBER)
            .uri(UPDATED_URI)
            .canonicalUri(UPDATED_CANONICAL_URI)
            .uriSl(UPDATED_URI_SL)
            .notation(UPDATED_NOTATION)
            .title(UPDATED_TITLE)
            .definition(UPDATED_DEFINITION)
            .previousVersion(UPDATED_PREVIOUS_VERSION)
            .initialVersion(UPDATED_INITIAL_VERSION)
            .creator(UPDATED_CREATOR)
            .publisher(UPDATED_PUBLISHER)
            .notes(UPDATED_NOTES)
            .versionNotes(UPDATED_VERSION_NOTES)
            .versionChanges(UPDATED_VERSION_CHANGES)
            .discussionNotes(UPDATED_DISCUSSION_NOTES)
            .license(UPDATED_LICENSE)
            .licenseId(UPDATED_LICENSE_ID)
            .citation(UPDATED_CITATION)
            .ddiUsage(UPDATED_DDI_USAGE)
            .translateAgency(UPDATED_TRANSLATE_AGENCY)
            .translateAgencyLink(UPDATED_TRANSLATE_AGENCY_LINK);
        return version;
    }

    @BeforeEach
    public void initTest() {
        version = createEntity(em);
    }

    @Test
    @Transactional
    public void createVersion() throws Exception {
        int databaseSizeBeforeCreate = versionRepository.findAll().size();

        // Create the Version
        VersionDTO versionDTO = versionMapper.toDto(version);
        restVersionMockMvc.perform(post("/api/versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(versionDTO)))
            .andExpect(status().isCreated());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeCreate + 1);
        Version testVersion = versionList.get(versionList.size() - 1);
        assertThat(testVersion.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testVersion.getItemType()).isEqualTo(DEFAULT_ITEM_TYPE);
        assertThat(testVersion.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testVersion.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testVersion.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
        assertThat(testVersion.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testVersion.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testVersion.getUri()).isEqualTo(DEFAULT_URI);
        assertThat(testVersion.getCanonicalUri()).isEqualTo(DEFAULT_CANONICAL_URI);
        assertThat(testVersion.getUriSl()).isEqualTo(DEFAULT_URI_SL);
        assertThat(testVersion.getNotation()).isEqualTo(DEFAULT_NOTATION);
        assertThat(testVersion.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testVersion.getDefinition()).isEqualTo(DEFAULT_DEFINITION);
        assertThat(testVersion.getPreviousVersion()).isEqualTo(DEFAULT_PREVIOUS_VERSION);
        assertThat(testVersion.getInitialVersion()).isEqualTo(DEFAULT_INITIAL_VERSION);
        assertThat(testVersion.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testVersion.getPublisher()).isEqualTo(DEFAULT_PUBLISHER);
        assertThat(testVersion.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testVersion.getVersionNotes()).isEqualTo(DEFAULT_VERSION_NOTES);
        assertThat(testVersion.getVersionChanges()).isEqualTo(DEFAULT_VERSION_CHANGES);
        assertThat(testVersion.getDiscussionNotes()).isEqualTo(DEFAULT_DISCUSSION_NOTES);
        assertThat(testVersion.getLicense()).isEqualTo(DEFAULT_LICENSE);
        assertThat(testVersion.getLicenseId()).isEqualTo(DEFAULT_LICENSE_ID);
        assertThat(testVersion.getCitation()).isEqualTo(DEFAULT_CITATION);
        assertThat(testVersion.getDdiUsage()).isEqualTo(DEFAULT_DDI_USAGE);
        assertThat(testVersion.getTranslateAgency()).isEqualTo(DEFAULT_TRANSLATE_AGENCY);
        assertThat(testVersion.getTranslateAgencyLink()).isEqualTo(DEFAULT_TRANSLATE_AGENCY_LINK);
    }

    @Test
    @Transactional
    public void createVersionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = versionRepository.findAll().size();

        // Create the Version with an existing ID
        version.setId(1L);
        VersionDTO versionDTO = versionMapper.toDto(version);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVersionMockMvc.perform(post("/api/versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(versionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllVersions() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        // Get all the versionList
        restVersionMockMvc.perform(get("/api/versions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(version.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].itemType").value(hasItem(DEFAULT_ITEM_TYPE)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(DEFAULT_PUBLICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED))))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].uri").value(hasItem(DEFAULT_URI)))
            .andExpect(jsonPath("$.[*].canonicalUri").value(hasItem(DEFAULT_CANONICAL_URI)))
            .andExpect(jsonPath("$.[*].uriSl").value(hasItem(DEFAULT_URI_SL)))
            .andExpect(jsonPath("$.[*].notation").value(hasItem(DEFAULT_NOTATION)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].definition").value(hasItem(DEFAULT_DEFINITION.toString())))
            .andExpect(jsonPath("$.[*].previousVersion").value(hasItem(DEFAULT_PREVIOUS_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].initialVersion").value(hasItem(DEFAULT_INITIAL_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.intValue())))
            .andExpect(jsonPath("$.[*].publisher").value(hasItem(DEFAULT_PUBLISHER.intValue())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())))
            .andExpect(jsonPath("$.[*].versionNotes").value(hasItem(DEFAULT_VERSION_NOTES.toString())))
            .andExpect(jsonPath("$.[*].versionChanges").value(hasItem(DEFAULT_VERSION_CHANGES.toString())))
            .andExpect(jsonPath("$.[*].discussionNotes").value(hasItem(DEFAULT_DISCUSSION_NOTES.toString())))
            .andExpect(jsonPath("$.[*].license").value(hasItem(DEFAULT_LICENSE)))
            .andExpect(jsonPath("$.[*].licenseId").value(hasItem(DEFAULT_LICENSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].citation").value(hasItem(DEFAULT_CITATION.toString())))
            .andExpect(jsonPath("$.[*].ddiUsage").value(hasItem(DEFAULT_DDI_USAGE.toString())))
            .andExpect(jsonPath("$.[*].translateAgency").value(hasItem(DEFAULT_TRANSLATE_AGENCY)))
            .andExpect(jsonPath("$.[*].translateAgencyLink").value(hasItem(DEFAULT_TRANSLATE_AGENCY_LINK)));
    }

    @Test
    @Transactional
    public void getVersion() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        // Get the version
        restVersionMockMvc.perform(get("/api/versions/{id}", version.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(version.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.itemType").value(DEFAULT_ITEM_TYPE))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.publicationDate").value(DEFAULT_PUBLICATION_DATE.toString()))
            .andExpect(jsonPath("$.lastModified").value(sameInstant(DEFAULT_LAST_MODIFIED)))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.uri").value(DEFAULT_URI))
            .andExpect(jsonPath("$.canonicalUri").value(DEFAULT_CANONICAL_URI))
            .andExpect(jsonPath("$.uriSl").value(DEFAULT_URI_SL))
            .andExpect(jsonPath("$.notation").value(DEFAULT_NOTATION))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.definition").value(DEFAULT_DEFINITION.toString()))
            .andExpect(jsonPath("$.previousVersion").value(DEFAULT_PREVIOUS_VERSION.intValue()))
            .andExpect(jsonPath("$.initialVersion").value(DEFAULT_INITIAL_VERSION.intValue()))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.intValue()))
            .andExpect(jsonPath("$.publisher").value(DEFAULT_PUBLISHER.intValue()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES.toString()))
            .andExpect(jsonPath("$.versionNotes").value(DEFAULT_VERSION_NOTES.toString()))
            .andExpect(jsonPath("$.versionChanges").value(DEFAULT_VERSION_CHANGES.toString()))
            .andExpect(jsonPath("$.discussionNotes").value(DEFAULT_DISCUSSION_NOTES.toString()))
            .andExpect(jsonPath("$.license").value(DEFAULT_LICENSE))
            .andExpect(jsonPath("$.licenseId").value(DEFAULT_LICENSE_ID.intValue()))
            .andExpect(jsonPath("$.citation").value(DEFAULT_CITATION.toString()))
            .andExpect(jsonPath("$.ddiUsage").value(DEFAULT_DDI_USAGE.toString()))
            .andExpect(jsonPath("$.translateAgency").value(DEFAULT_TRANSLATE_AGENCY))
            .andExpect(jsonPath("$.translateAgencyLink").value(DEFAULT_TRANSLATE_AGENCY_LINK));
    }

    @Test
    @Transactional
    public void getNonExistingVersion() throws Exception {
        // Get the version
        restVersionMockMvc.perform(get("/api/versions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVersion() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        int databaseSizeBeforeUpdate = versionRepository.findAll().size();

        // Update the version
        Version updatedVersion = versionRepository.findById(version.getId()).get();
        // Disconnect from session so that the updates on updatedVersion are not directly saved in db
        em.detach(updatedVersion);
        updatedVersion
            .status(UPDATED_STATUS)
            .itemType(UPDATED_ITEM_TYPE)
            .language(UPDATED_LANGUAGE)
            .creationDate(UPDATED_CREATION_DATE)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .number(UPDATED_NUMBER)
            .uri(UPDATED_URI)
            .canonicalUri(UPDATED_CANONICAL_URI)
            .uriSl(UPDATED_URI_SL)
            .notation(UPDATED_NOTATION)
            .title(UPDATED_TITLE)
            .definition(UPDATED_DEFINITION)
            .previousVersion(UPDATED_PREVIOUS_VERSION)
            .initialVersion(UPDATED_INITIAL_VERSION)
            .creator(UPDATED_CREATOR)
            .publisher(UPDATED_PUBLISHER)
            .notes(UPDATED_NOTES)
            .versionNotes(UPDATED_VERSION_NOTES)
            .versionChanges(UPDATED_VERSION_CHANGES)
            .discussionNotes(UPDATED_DISCUSSION_NOTES)
            .license(UPDATED_LICENSE)
            .licenseId(UPDATED_LICENSE_ID)
            .citation(UPDATED_CITATION)
            .ddiUsage(UPDATED_DDI_USAGE)
            .translateAgency(UPDATED_TRANSLATE_AGENCY)
            .translateAgencyLink(UPDATED_TRANSLATE_AGENCY_LINK);
        VersionDTO versionDTO = versionMapper.toDto(updatedVersion);

        restVersionMockMvc.perform(put("/api/versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(versionDTO)))
            .andExpect(status().isOk());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
        Version testVersion = versionList.get(versionList.size() - 1);
        assertThat(testVersion.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVersion.getItemType()).isEqualTo(UPDATED_ITEM_TYPE);
        assertThat(testVersion.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testVersion.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testVersion.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
        assertThat(testVersion.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testVersion.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testVersion.getUri()).isEqualTo(UPDATED_URI);
        assertThat(testVersion.getCanonicalUri()).isEqualTo(UPDATED_CANONICAL_URI);
        assertThat(testVersion.getUriSl()).isEqualTo(UPDATED_URI_SL);
        assertThat(testVersion.getNotation()).isEqualTo(UPDATED_NOTATION);
        assertThat(testVersion.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testVersion.getDefinition()).isEqualTo(UPDATED_DEFINITION);
        assertThat(testVersion.getPreviousVersion()).isEqualTo(UPDATED_PREVIOUS_VERSION);
        assertThat(testVersion.getInitialVersion()).isEqualTo(UPDATED_INITIAL_VERSION);
        assertThat(testVersion.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testVersion.getPublisher()).isEqualTo(UPDATED_PUBLISHER);
        assertThat(testVersion.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testVersion.getVersionNotes()).isEqualTo(UPDATED_VERSION_NOTES);
        assertThat(testVersion.getVersionChanges()).isEqualTo(UPDATED_VERSION_CHANGES);
        assertThat(testVersion.getDiscussionNotes()).isEqualTo(UPDATED_DISCUSSION_NOTES);
        assertThat(testVersion.getLicense()).isEqualTo(UPDATED_LICENSE);
        assertThat(testVersion.getLicenseId()).isEqualTo(UPDATED_LICENSE_ID);
        assertThat(testVersion.getCitation()).isEqualTo(UPDATED_CITATION);
        assertThat(testVersion.getDdiUsage()).isEqualTo(UPDATED_DDI_USAGE);
        assertThat(testVersion.getTranslateAgency()).isEqualTo(UPDATED_TRANSLATE_AGENCY);
        assertThat(testVersion.getTranslateAgencyLink()).isEqualTo(UPDATED_TRANSLATE_AGENCY_LINK);
    }

    @Test
    @Transactional
    public void updateNonExistingVersion() throws Exception {
        int databaseSizeBeforeUpdate = versionRepository.findAll().size();

        // Create the Version
        VersionDTO versionDTO = versionMapper.toDto(version);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVersionMockMvc.perform(put("/api/versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(versionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteVersion() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        int databaseSizeBeforeDelete = versionRepository.findAll().size();

        // Delete the version
        restVersionMockMvc.perform(delete("/api/versions/{id}", version.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
