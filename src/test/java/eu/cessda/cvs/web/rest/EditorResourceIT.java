package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.AuthorityRepository;
import eu.cessda.cvs.repository.UserRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link EditorResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EditorResourceIT {

    private static final String INIT_STATUS = "DRAFT";
    private static final String EDIT_STATUS = "REVIEW";

    private static final String INIT_URI = "AAAAAAAAAA";
    private static final String EDIT_URI = "BBBBBBBBBB";

    private static final String NOTATION = "AAAAAAAAAA";

    private static final String INIT_VERSION_NUMBER = "1.0";
    private static final String EDIT_VERSION_NUMBER = "2.0";

    private static final String SOURCE_LANGUAGE = "en";
    private static final String TARGET_LANGUAGE = "de";

    private static final ItemType ITEM_TYPE_SL = ItemType.SL;
    private static final ItemType ITEM_TYPE_TL = ItemType.TL;

    private static final String INIT_AGENCY_NAME = "AAAAAAAAAA";
    private static final String EDIT_AGENCY_NAME = "BBBBBBBBBB";

    private static final String INIT_AGENCY_LOGO = "AAAAAAAAAA";
    private static final String EDIT_AGENCY_LOGO = "BBBBBBBBBB";

    private static final String INIT_NOTES = "AAAAAAAAAA";
    private static final String EDIT_NOTES = "BBBBBBBBBB";

    private static final String INIT_TITLE_EN = "AAAAAAAAAA";
    private static final String EDIT_TITLE_EN = "BBBBBBBBBB";

    private static final String INIT_DEFINITION_EN = "AAAAAAAAAA";
    private static final String EDIT_DEFINITION_EN = "BBBBBBBBBB";

    private static final String INIT_TITLE_DE = "AAAAAAAAAA";
    private static final String EDIT_TITLE_DE = "BBBBBBBBBB";

    private static final String INIT_DEFINITION_DE = "AAAAAAAAAA";
    private static final String EDIT_DEFINITION_DE = "BBBBBBBBBB";

    private static final String INIT_NAME = "AAAAAAAAAA";

    private static final String INIT_LINK = "AAAAAAAAAA";

    private static final String INIT_DESCRIPTION = "AAAAAAAAAA";

    private static final String INIT_LOGOPATH = "AAAAAAAAAA";

    private static final String INIT_LICENSE = "AAAAAAAAAA";

    private static final Long INIT_LICENSE_ID = 1L;

    private static final String INIT_CANONICAL_URI = "AAAAAAAAAA";

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private MockMvc restVocabularyMockMvc;

    private VocabularySnippet vocabularySnippet;

    private Agency agency;

    private String jwt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VocabularySnippet createVocabularySnippet() {
        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setLanguage(SOURCE_LANGUAGE);
        vocabularySnippet.setItemType(ITEM_TYPE_SL);
        vocabularySnippet.setNotation(NOTATION);
        vocabularySnippet.setVersionNumber( INIT_VERSION_NUMBER );
        vocabularySnippet.setStatus( INIT_STATUS );
        vocabularySnippet.setTitle( INIT_TITLE_EN );
        vocabularySnippet.setDefinition( INIT_DEFINITION_EN );
        vocabularySnippet.setNotes( INIT_NOTES );
        return vocabularySnippet;
    }

    /**
     * Create an update entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VocabularySnippet updateVocabularySnippet() {
        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setLanguage( SOURCE_LANGUAGE) ;
        vocabularySnippet.setItemType( ITEM_TYPE_SL);
        vocabularySnippet.setNotation(NOTATION);
        vocabularySnippet.setTitle( EDIT_TITLE_EN );
        vocabularySnippet.setDefinition( EDIT_DEFINITION_EN );
        vocabularySnippet.setNotes( EDIT_NOTES );
        return vocabularySnippet;
    }


    /**
     * Create agency entity for this test.
     */
    public static Agency createAgencyEntity() {
        Agency agency = new Agency()
            .name(INIT_NAME)
            .link(INIT_LINK)
            .description(INIT_DESCRIPTION)
            .logopath(INIT_LOGOPATH)
            .license(INIT_LICENSE)
            .licenseId(INIT_LICENSE_ID)
            .uri(INIT_URI)
            .canonicalUri(INIT_CANONICAL_URI);
        return agency;
    }

    @BeforeEach
    public void initTest() {
        if( agency == null ) {
            agency = createAgencyEntity();
            agency = agencyRepository.saveAndFlush(agency);
        }
        vocabularySnippet = createVocabularySnippet();
        vocabularySnippet.setAgencyId(agency.getId());

        // need to mock user due to authorization
        if( jwt == null ) {
            // Mock user login as admin
            Authority authority = new Authority();
            authority.setName(AuthoritiesConstants.ADMIN);
            authorityRepository.saveAndFlush(authority );

            Set<Authority> authorities = new HashSet<>();
            authorities.add(authority);

            User user = new User();
            user.setLogin("user-jwt-controller");
            user.setEmail("user-jwt-controller@example.com");
            user.setActivated(true);
            user.setPassword(passwordEncoder.encode("test"));
            user.setAuthorities(authorities);

            userRepository.saveAndFlush(user);

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getLogin(), "test");

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            jwt = tokenProvider.createToken(authentication, false);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @Test
    @Transactional
    void createVocabulary() throws Exception {
        int databaseSizeBeforeCreate = vocabularyRepository.findAll().size();
        createVocabularyAndSlInitialVersion();

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeCreate + 1);
        Vocabulary testVocabulary = vocabularyList.get(vocabularyList.size() - 1);
        assertThat(testVocabulary.getStatus()).isEqualTo(INIT_STATUS);
        assertThat(testVocabulary.getNotation()).isEqualTo(NOTATION);
        assertThat(testVocabulary.getVersionNumber()).isEqualTo(INIT_VERSION_NUMBER);
        assertThat(testVocabulary.getSourceLanguage()).isEqualTo(SOURCE_LANGUAGE);
        assertThat(testVocabulary.getAgencyName()).isEqualTo(INIT_AGENCY_NAME);
        assertThat(testVocabulary.getAgencyLogo()).isEqualTo(INIT_AGENCY_LOGO);
        assertThat(testVocabulary.getNotes()).isEqualTo(INIT_NOTES);
        assertThat(testVocabulary.getTitleEn()).isEqualTo(INIT_TITLE_EN);
        assertThat(testVocabulary.getDefinitionEn()).isEqualTo(INIT_DEFINITION_EN);

        assertThat(testVocabulary.getVersions().size()).isEqualTo(1);
        final Version testVersion = testVocabulary.getVersions().iterator().next();
        assertThat(testVersion.getStatus()).isEqualTo(INIT_STATUS);
        assertThat(testVersion.getNotation()).isEqualTo(NOTATION);
        assertThat(testVersion.getNumber()).isEqualTo(INIT_VERSION_NUMBER);
        assertThat(testVersion.getLanguage()).isEqualTo(SOURCE_LANGUAGE);
        assertThat(testVersion.getItemType()).isEqualTo(ITEM_TYPE_SL.toString());
        assertThat(testVersion.getNotes()).isEqualTo(INIT_NOTES);
        assertThat(testVersion.getTitle()).isEqualTo(INIT_TITLE_EN);
        assertThat(testVersion.getDefinition()).isEqualTo(INIT_DEFINITION_EN);
    }

    private void createVocabularyAndSlInitialVersion() throws Exception {
        vocabularySnippet.setActionType( ActionType.CREATE_CV );
        // Create the Vocabulary with code snippet
        restVocabularyMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
            .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void updateVocabulary() throws Exception {
        createVocabularyAndSlInitialVersion();
        int databaseSize = vocabularyRepository.findAll().size();
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        Vocabulary vocabulary = vocabularyList.get(vocabularyList.size() - 1);
        // prepare to update
        final VocabularySnippet vocabularySnippet = updateVocabularySnippet();
        vocabularySnippet.setActionType( ActionType.EDIT_CV);
        vocabularySnippet.setVocabularyId( vocabulary.getId() );
        vocabularySnippet.setVersionId( vocabulary.getVersions().iterator().next().getId()); // there is only one version

        // update
        restVocabularyMockMvc.perform(put("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
            .andExpect(status().isOk());

        vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSize);
        Vocabulary testVocabulary = vocabularyList.get(vocabularyList.size() - 1);
        assertThat(testVocabulary.getTitleEn()).isEqualTo(EDIT_TITLE_EN);
        assertThat(testVocabulary.getDefinitionEn()).isEqualTo(EDIT_DEFINITION_EN);

        assertThat(testVocabulary.getVersions().size()).isEqualTo(1);
        final Version testVersion = testVocabulary.getVersions().iterator().next();
        assertThat(testVersion.getNotes()).isEqualTo(EDIT_NOTES);
        assertThat(testVersion.getTitle()).isEqualTo(EDIT_TITLE_EN);
        assertThat(testVersion.getDefinition()).isEqualTo(EDIT_DEFINITION_EN);
    }

//    @Test
//    @Transactional
//    void createNewVocabularyVersion() throws Exception {
//        createVocabularyAndSlInitialVersion();
//        int databaseSizeBeforeCreate = vocabularyRepository.findAll().size();
//        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
//        Vocabulary testVocabulary = vocabularyList.get(vocabularyList.size() - 1);
//
//        // create new version for added vocabulary
//        restVocabularyMockMvc.perform(post("/editors/vocabularies/new-version/" + testVocabulary.getId())
//            .header("Authorization", jwt))
//            .andExpect(status().isCreated());
//
//        // Validate the Vocabulary in the database
//        vocabularyList = vocabularyRepository.findAll();
//        assertThat(vocabularyList).hasSize(databaseSizeBeforeCreate + 1);
//        assertThat(testVocabulary.getVersions().size()).isEqualTo(2);
//    }
}
