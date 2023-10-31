/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.repository.*;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.TokenProvider;
import eu.cessda.cvs.service.dto.CommentDTO;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.service.mapper.CommentMapper;
import eu.cessda.cvs.service.mapper.MetadataValueMapper;
import eu.cessda.cvs.utils.VersionNumber;
import eu.cessda.cvs.utils.VocabularyUtils;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EditorResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class EditorResourceIT {

    private static final String INIT_STATUS = "DRAFT";
    private static final String EDIT_STATUS = "REVIEW";

    private static final String AGENCY_URI = "https://vocabularies-dev.cessda.eu/vocabulary/[VOCABULARY]?v=[VERSION]";
    private static final String AGENCY_URI_CODE = "https://vocabularies-dev.cessda.eu/vocabulary/[VOCABULARY]_[CODE]?v=[VERSION]";

    private static final String NOTATION = "AAAAAAAAAA";

    public static final String INIT_CODE_NOTATION = "AAAAA";
    private static final String EDIT_CODE_NOTATION = "BBBBB";
    private static final String INIT_CODE2_NOTATION = "CCCCC";
    private static final String INIT_CODE3_NOTATION = "DDDDD";
    private static final String INIT_CODE4_NOTATION = "EEEEE";
    private static final String INIT_CODE5 = "FFFFF";
    private static final String INIT_CODE6 = "GGGGG";

    public static final String INIT_CODE_TITLE = "TTTTT";
    private static final String EDIT_CODE_TITLE = "SSSSS";
    private static final String INIT_CODE2_TITLE = "UUUUU";
    private static final String INIT_CODE3_TITLE = "VVVVV";
    private static final String INIT_CODE4_TITLE = "WWWWW";

    public static final String INIT_CODE_DEF = "KKKKK";
    private static final String EDIT_CODE_DEF = "LLLLL";
    private static final String INIT_CODE2_DEF = "MMMMM";
    private static final String INIT_CODE3_DEF = "NNNNN";
    private static final String INIT_CODE4_DEF = "OOOOO";

    private static final String INIT_CODE_TL_TITLE = "AAAAA";
    private static final String EDIT_CODE_TL_TITLE = "BBBBB";
    private static final String INIT_CODE_TL_DEFINITION = "AAAAA";
    private static final String EDIT_CODE_TL_DEFINITION = "BBBBB";


    public static final Integer CODE_POSITION = 0;
    private static final Integer CODE4_POSITION = 1;
    private static final Integer CODE2_POSITION = 2;
    private static final Integer CODE3_POSITION = 3;

    public static final VersionNumber INIT_VERSION_NUMBER_SL = VersionNumber.fromString("1.0");
    private static final VersionNumber INIT_VERSION_NUMBER_TL = VersionNumber.fromString("1.0.1");

    private static final String INIT_VERSION_INFO = "AAAAA";
    private static final String INIT_VERSION_CHANGES = "BBBBB";

    public static final String SOURCE_LANGUAGE = "en";
    private static final String TARGET_LANGUAGE = "de";

    private static final ItemType ITEM_TYPE_SL = ItemType.SL;
    private static final ItemType ITEM_TYPE_TL = ItemType.TL;

    public static final String INIT_AGENCY_NAME = "AAAAAAAAAA";
    private static final String EDIT_AGENCY_NAME = "BBBBBBBBBB";

    private static final String INIT_AGENCY_LOGO = "AAAAAAAAAA";
    private static final String EDIT_AGENCY_LOGO = "BBBBBBBBBB";

    private static final String INIT_NOTES = "AAAAAAAAAA";
    private static final String EDIT_NOTES = "BBBBBBBBBB";

    private static final String INIT_DDI_USAGE = "AAAAAAAAAA";
    private static final String EDIT_DDI_USAGE = "BBBBBBBBBB";

    private static final String INIT_DISCUSSION_NOTES = "AAAAAAAAAA";
    private static final String EDIT_DISCUSSION_NOTES = "BBBBBBBBBB";

    public static final String INIT_TITLE_EN = "AAAAAAAAAA";
    private static final String EDIT_TITLE_EN = "BBBBBBBBBB";

    private static final String INIT_DEFINITION_EN = "AAAAAAAAAA";
    private static final String EDIT_DEFINITION_EN = "BBBBBBBBBB";

    private static final String INIT_TITLE_DE = "AAAAAAAAAADE";
    private static final String EDIT_TITLE_DE = "BBBBBBBBBBDE";

    private static final String INIT_DEFINITION_DE = "AAAAAAAAAADE";
    private static final String EDIT_DEFINITION_DE = "BBBBBBBBBBDE";

    private static final String INIT_TRANS_AGENCY = "TTTTTDE";
    private static final String EDIT_TRANS_AGENCY = "SSSSSDE";

    private static final String INIT_TRANS_AGENCY_LINK = "UUUUUDE";
    private static final String EDIT_TRANS_AGENCY_LINK = "UUUUUDE";

    public static final String INIT_NAME = "AAAAAAAAAA";

    public static final String INIT_LINK = "AAAAAAAAAA";

    private static final String INIT_DESCRIPTION = "AAAAAAAAAA";

    public static final String INIT_LOGOPATH = "AAAAAAAAAA";

    public static final String INIT_LICENSE = "AAAAAAAAAA";

    private static final Long INIT_LICENSE_ID = 1L;

    private static final String INIT_CANONICAL_URI = "AAAAAAAAAA";

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private LicenceRepository licenceRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VocabularyChangeRepository vocabularyChangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private MetadataValueRepository metadataValueRepository;

    @Autowired
    private MetadataValueMapper metadataValueMapper;

    @Autowired
    private MockMvc restMockMvc;

    private VocabularySnippet vocabularySnippetForEnSl;

    private Agency agency;

    private Licence license;

    private String jwt;

    public static VocabularySnippet createVocabularySnippetForSlEn() {
        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setLanguage(SOURCE_LANGUAGE);
        vocabularySnippet.setItemType(ITEM_TYPE_SL);
        vocabularySnippet.setNotation(NOTATION);
        vocabularySnippet.setVersionNumber(INIT_VERSION_NUMBER_SL);
        vocabularySnippet.setStatus( INIT_STATUS );
        vocabularySnippet.setTitle( INIT_TITLE_EN );
        vocabularySnippet.setDefinition( INIT_DEFINITION_EN );
        vocabularySnippet.setNotes( INIT_NOTES );
        vocabularySnippet.setDdiUsage(EDIT_DDI_USAGE);
        return vocabularySnippet;
    }

    private static VocabularySnippet createVocabularySnippetForTlDe() {
        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setLanguage(TARGET_LANGUAGE);
        vocabularySnippet.setItemType(ITEM_TYPE_TL);
        vocabularySnippet.setNotation(NOTATION);
        vocabularySnippet.setVersionNumber(INIT_VERSION_NUMBER_TL);
        vocabularySnippet.setStatus( INIT_STATUS );
        vocabularySnippet.setTitle( INIT_TITLE_DE );
        vocabularySnippet.setDefinition( INIT_DEFINITION_DE );
        vocabularySnippet.setNotes( INIT_NOTES );
        vocabularySnippet.setTranslateAgency( INIT_TRANS_AGENCY );
        vocabularySnippet.setTranslateAgencyLink( INIT_TRANS_AGENCY_LINK );
        return vocabularySnippet;
    }

    private static VocabularySnippet updateVocabularySnippetForSlEn() {
        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setLanguage( SOURCE_LANGUAGE) ;
        vocabularySnippet.setItemType( ITEM_TYPE_SL);
        vocabularySnippet.setNotation(NOTATION);
        vocabularySnippet.setTitle( EDIT_TITLE_EN );
        vocabularySnippet.setDefinition( EDIT_DEFINITION_EN );
        vocabularySnippet.setNotes( EDIT_NOTES );
        return vocabularySnippet;
    }

    public static CodeSnippet createCodeSnippet( String notation, String title, String definition, Integer position,
                                                  String changeType) {
        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setNotation( notation );
        codeSnippet.setTitle( title );
        codeSnippet.setDefinition( definition );
        if ( position != null)
            codeSnippet.setPosition( position );
        if ( changeType != null )
            codeSnippet.setChangeType( changeType );
        return codeSnippet;
    }

    private static CodeSnippet updateCodeSnippetForSlEn() {
        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setNotation(EDIT_CODE_NOTATION);
        codeSnippet.setTitle( EDIT_CODE_TITLE );
        codeSnippet.setDefinition( EDIT_CODE_DEF );
        codeSnippet.setPosition( CODE_POSITION );
        return codeSnippet;
    }

    public static Agency createAgencyEntity() {
        return new Agency()
            .name(INIT_AGENCY_NAME)
            .link(INIT_LINK)
            .description(INIT_DESCRIPTION)
            .logopath(INIT_LOGOPATH)
            .license(INIT_LICENSE)
            .licenseId(INIT_LICENSE_ID)
            .uri(AGENCY_URI)
            .uriCode(AGENCY_URI_CODE)
            .canonicalUri(INIT_CANONICAL_URI);
    }

    @BeforeEach
    public void initTest() {
        if( agency == null ) {
            agency = createAgencyEntity();
            agency = agencyRepository.saveAndFlush(agency);
        }
        vocabularySnippetForEnSl = createVocabularySnippetForSlEn();
        vocabularySnippetForEnSl.setAgencyId(agency.getId());

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

    /**
     * Since the CVS workflow is quite complex and some of the test needs existing object, e,g. (Creating TL needs Published SL)
     * all of Integration Tests for SL and TL workflow are defined here.
     * <p>
     * Each part of the test will be indicated by its ActionType.
     */
    @Test
    @Transactional
    void vocabularyWorkflowTest() throws Exception {
        // ActionType.CREATE_CV
        Version slVersion = createVocabularyTest();
        // Test fail if try to add existed CV
        createVocabularyWithSameNotationFailTest();
        // Updated notes ActionType.EDIT_NOTE_CV & update DDI-Usage (unpublished CV) ~ ActionType.EDIT_DDI_CV
        updateSlVersionNotesAndUsageTests(slVersion);
        // ActionType.EDIT_CV
        updateVocabularySlTest( slVersion );
        // ActionType.CREATE_CODE
        Concept slConceptRoot1 = createSlRootConceptTest(slVersion, false);
        Concept slConceptRoot2 = createSlRootConceptTest(slVersion, true);
        // ActionType.CREATE_CODE ~ will be fail due to same Code-name
        createFailedSlConceptTest(slVersion);
        // Add second code as child
        Concept slConcept2 = createSlConceptChildTest(slVersion, slConceptRoot1, false);
        // Add third code as child of second code
        Concept slConcept3 = createSlConceptChildTest(slVersion, slConcept2, true);
        // ActionType.REORDER_CODE ~ move the slConceptRoot1 children to slConceptRoot2
        reorderConceptsTest(slVersion, slConceptRoot1, slConceptRoot2, slConcept2, slConcept3);
        // ActionType.EDIT_CODE
        updateSlConceptTest(slVersion, slConceptRoot1);
        // ActionType.DELETE_CODE
        deleteCodeTest(slConceptRoot1, slConcept2);
        // forward status with incorrect action type
        forwardStatusWithIncorrectActionTypeTest(slVersion);
        // ActionType.FORWARD_CV_SL_STATUS_REVIEW
        forwardSlStatusReviewTest(slVersion);
        // ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE
        forwardSlStatusReadyToTranslateTest(slVersion, false);
        // ActionType.FORWARD_CV_SL_STATUS_PUBLISH
        forwardSlStatusPublishTest(slVersion, false);
        // ActionType.CREATE_CODE ~ will be fail due to vocabulary already in ready to publish
        createSlConceptOnPublishedCvTest(slVersion);
        // Test searching for CVs in the Editor and Publication
        searchingCvEditorAndPublication1Test(slVersion);
        // Test searching for CVs code
        searchingCodeTest(slVersion, slConceptRoot1);
        // ActionType.ADD_TL_CV
        Version tlVersion = addVocabularyTranslationTest( slVersion );
        // Code translate for slConceptRoot1 ~ ActionType.ADD_TL_CV
        Concept tlConcept1 = addTlConceptTest(tlVersion, slConceptRoot1, INIT_CODE_TL_TITLE, INIT_CODE_TL_DEFINITION);
        // Code translate for slConceptRoot2 ~ ActionType.ADD_TL_CV
        addTlConceptTest(tlVersion, slConceptRoot2, INIT_CODE_TL_TITLE, INIT_CODE_TL_DEFINITION);
        // ActionType.EDIT_TL_CODE
        editTlConceptTest(slConceptRoot1, tlVersion, tlConcept1);
        // ActionType.DELETE_TL_CODE
        deleteTlConceptTest(slConceptRoot1, tlVersion, tlConcept1);
        // Restore Code translate for slConceptRoot1 ~ ActionType.ADD_TL_CV
        addTlConceptTest(tlVersion, slConceptRoot1, INIT_CODE_TL_TITLE, INIT_CODE_TL_DEFINITION);
        // ActionType.FORWARD_CV_TL_STATUS_REVIEW
        forwardTlStatusReviewTest( tlVersion );
        // ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH
        forwardTlStatusReadyToPublishTest( tlVersion );
        // ActionType.FORWARD_CV_TL_STATUS_PUBLISH by using ActionType.FORWARD_CV_SL_STATUS_PUBLISH
        forwardTlStatusPublishTest( slVersion, tlVersion );
        // create new TL version
        Version newTlVersion = createNewVocabularyVersionTest(tlVersion, true);
        // new TL version ActionType.FORWARD_CV_TL_STATUS_REVIEW
        forwardTlStatusReviewTest( newTlVersion );
        // ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH
        forwardTlStatusReadyToPublishTest( newTlVersion );
        // new TL version ActionType.FORWARD_CV_TL_STATUS_PUBLISH by using ActionType.FORWARD_CV_SL_STATUS_PUBLISH
        forwardTlStatusPublishTest( slVersion, tlVersion );
        // create new TL version but not published
        Version newTlVersion2 = createNewVocabularyVersionTest(newTlVersion, true);
        // create new SL version
        Version newSlVersion = createNewVocabularyVersionTest(slVersion, false);
        // Import batch codes
        batchCodesImportTest(newSlVersion);
        // ActionType.FORWARD_CV_SL_STATUS_REVIEW
        forwardSlStatusReviewTest(newSlVersion);
        // ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE
        forwardSlStatusReadyToTranslateTest(newSlVersion, true);
        // Publish new SL version to trigger cloning TLs ~ ActionType.FORWARD_CV_SL_STATUS_PUBLISH
        forwardSlStatusPublishTest(newSlVersion, true);
        // since there are already 2 versions they can be compared
        compareVersionTest(slVersion, newSlVersion);
        // Updated version notes ~ ActionType.EDIT_VERSION_INFO_CV
        updateSlVersionInfoTest(newSlVersion);
        // create TL version with lang DE again, the prev draft DE should influence new Cv DE
        Version newTlVersion3 = addVocabularyTranslationInMiddleTest(newSlVersion);
        // try to download a vocabulary file
        getVocabularyInJsonLdTest(newSlVersion);
        // get vocabulary by notation and version
        getVocabularyByNotationAndVersion(newSlVersion);
        // Delete TL, SL and whole CV Test
        // Delete latest TL version
        deleteVocabularyOrVersion(newTlVersion3,false);
        // Delete latest SL version
        deleteVocabularyOrVersion(newSlVersion,false);
        // Delete whole Vocabulary, by deleting the SL initial version
        deleteVocabularyOrVersion(slVersion,true);
    }

    private void getVocabularyByNotationAndVersion(Version version) throws Exception {
        restMockMvc.perform(get("/api/vocabularies/" + version.getNotation()+ "/"+ version.getNumber()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.versions.[*].notation").value(hasItem(version.getNotation())))
            .andExpect(jsonPath("$.versions.[*].title").value(hasItem(version.getTitle())))
            .andExpect(jsonPath("$.versions.[*].definition").value(hasItem(version.getDefinition())));
    }

    @Test
    @Transactional
    void editorCommentsTest() throws Exception {
        // create initial vocabulary
        Version slVersion = createVocabularyTest();
        Comment comment = CommentResourceIT.createEntity();
        CommentDTO commentDTO = commentMapper.toDto(comment);
        commentDTO.setVersionId( slVersion.getId() );

        restMockMvc.perform(post("/api/editors/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isCreated());
        Comment savedComment = commentRepository.findAllByVersion(slVersion.getId()).stream().findFirst().orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo(CommentResourceIT.DEFAULT_CONTENT);
        assertThat(savedComment.getInfo()).isEqualTo(CommentResourceIT.DEFAULT_INFO);

        // update
        commentDTO.setId( savedComment.getId() );
        commentDTO.setContent(CommentResourceIT.UPDATED_CONTENT);
        restMockMvc.perform(put("/api/editors/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isOk());
        savedComment = commentRepository.findAllByVersion(slVersion.getId()).stream().findFirst().orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo(CommentResourceIT.UPDATED_CONTENT);

        // delete
        restMockMvc.perform(delete("/api/editors/comments/{id}", savedComment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        savedComment = commentRepository.findAllByVersion(slVersion.getId()).stream().findFirst().orElse(null);
        assertThat(savedComment).isNull();
    }

    @Test
    @Transactional
    void editorMetadataValuesTest() throws Exception {
        MetadataValue metadataValue = MetadataValueResourceIT.createEntity();
        MetadataValueDTO metadataValueDTO = metadataValueMapper.toDto(metadataValue);
        metadataValueDTO.setMetadataKey( "AAAAA" );

        restMockMvc.perform(post("/api/editors/metadatas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataValueDTO)))
            .andExpect(status().isCreated());
        MetadataValue savedMetadataValue = metadataValueRepository.findAll().stream()
            .filter( m -> m.getIdentifier().equals(MetadataValueResourceIT.DEFAULT_IDENTIFIER ))
            .findFirst().orElse(null);
        assertThat(savedMetadataValue).isNotNull();
        assertThat(savedMetadataValue.getValue()).isEqualTo(MetadataValueResourceIT.DEFAULT_VALUE);

        // update
        metadataValueDTO.setId( savedMetadataValue.getId() );
        metadataValueDTO.setMetadataFieldId( savedMetadataValue.getMetadataField().getId() );
        metadataValueDTO.setIdentifier(MetadataValueResourceIT.UPDATED_IDENTIFIER);
        metadataValueDTO.setValue(MetadataValueResourceIT.UPDATED_VALUE);
        restMockMvc.perform(put("/api/editors/metadatas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataValueDTO)))
            .andExpect(status().isOk());
        savedMetadataValue = metadataValueRepository.findAll().stream()
            .filter( m -> m.getIdentifier().equals(MetadataValueResourceIT.UPDATED_IDENTIFIER ))
            .findFirst().orElse(null);
        assertThat(savedMetadataValue).isNotNull();
        assertThat(savedMetadataValue.getValue()).isEqualTo(MetadataValueResourceIT.UPDATED_VALUE);

        // delete
        restMockMvc.perform(delete("/api/editors/metadatas/{id}", savedMetadataValue.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        savedMetadataValue = metadataValueRepository.findAll().stream()
            .filter( m -> m.getIdentifier().equals(MetadataValueResourceIT.UPDATED_IDENTIFIER ))
            .findFirst().orElse(null);
        assertThat(savedMetadataValue).isNull();
    }

    private void getVocabularyInJsonLdTest(Version version) throws Exception {
        // Code Search Test
        final ResultActions resultActions = restMockMvc.perform(get("/v2/vocabularies/" + version.getNotation() + "/" + version.getNumber()
            + "?languageVersion=" + version.getLanguage() + "-" + version.getNumber()));
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.notation").value(version.getNotation()));
    }

    private void updateSlVersionInfoTest(Version version) throws Exception {
        vocabularySnippetForEnSl.setActionType( ActionType.EDIT_VERSION_INFO_CV );
        vocabularySnippetForEnSl.setVocabularyId( version.getVocabulary().getId() );
        vocabularySnippetForEnSl.setVersionId( version.getId());
        vocabularySnippetForEnSl.setVersionNotes( INIT_VERSION_INFO );
        vocabularySnippetForEnSl.setVersionChanges(INIT_VERSION_CHANGES);
        vocabularySnippetForEnSl.setLicenseId( license.getId() );
        vocabularySnippetForEnSl.setVersionNumber(version.getNumber());
        Version versionUpdated = updateVersionInfoTest(version);
        assertThat(versionUpdated.getLicenseId()).isEqualTo(license.getId());
        assertThat(versionUpdated.getVersionNotes()).isEqualTo(INIT_VERSION_INFO);
        assertThat(versionUpdated.getVersionChanges()).isEqualTo(INIT_VERSION_CHANGES);
        // test with null version number and license id, should not change the licence and version number
        vocabularySnippetForEnSl.setLicenseId( null );
        vocabularySnippetForEnSl.setVersionNumberFromString(null);
        Version versionUpdatedAfterUpdate = updateVersionInfoTest(version);
        assertThat(versionUpdatedAfterUpdate.getLicenseId()).isEqualTo(license.getId());
        assertThat(versionUpdatedAfterUpdate.getNumber()).isEqualTo(version.getNumber());
    }

    private Version updateVersionInfoTest(Version version) throws Exception {
        restMockMvc.perform(put("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isOk());
        Vocabulary testVocabulary = vocabularyRepository.findByNotation( version.getNotation() ).findFirst().orElse(null);
        assertThat(testVocabulary).isNotNull();
        return getLatestVersionByNotationAndLang(vocabularyRepository.findAll(), version.getNotation(), version.getLanguage());
    }

    private void compareVersionTest(Version slVersion, Version newSlVersion) throws Exception {
        // Compare SL version with previous version that not exist
        restMockMvc.perform(get("/api/editors/vocabularies/compare-prev/" + slVersion.getId()))
            .andExpect(status().is5xxServerError());
        // Compare SL version with previous version
        restMockMvc.perform(get("/api/editors/vocabularies/compare-prev/" + newSlVersion.getId()))
            .andExpect(status().isOk());
    }

    private void batchCodesImportTest(Version newSlVersion) throws Exception {
        int conceptDbSize = conceptRepository.findAll().size();
        CodeSnippet codeSnippet5 = createCodeSnippet( INIT_CODE5, INIT_CODE5, INIT_CODE5, 3, "Code added" );
        CodeSnippet codeSnippet6 = createCodeSnippet( INIT_CODE5 + "." + INIT_CODE6, INIT_CODE6, INIT_CODE6, 4, "Code added" );
        codeSnippet5.setActionType(ActionType.CREATE_CODE);
        codeSnippet5.setVersionId(newSlVersion.getId());
        codeSnippet6.setActionType(ActionType.CREATE_CODE);
        codeSnippet6.setVersionId(newSlVersion.getId());
        codeSnippet6.setParent(INIT_CODE5);
        restMockMvc.perform(post("/api/editors/codes/batch")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(Arrays.asList( codeSnippet5, codeSnippet6))))
            .andExpect(status().isOk());
        final List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(conceptDbSize + 2); // 2 new added concepts
        Concept newConcept = conceptList.stream()
            .filter(c -> c.getNotation().equals(INIT_CODE5))
            .findFirst().orElse(null);
        assertThat( newConcept ).isNotNull();
    }

    private void deleteVocabularyOrVersion(Version versionToDelete, boolean isDeleteVocabulary) throws Exception {
        List<Vocabulary> vocabularies = vocabularyRepository.findAll();
        int vocabSizeBeforeDelete = vocabularies.size();

        // Delete version/vocab
        restMockMvc.perform(delete("/api/editors/vocabularies/{id}", versionToDelete.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        vocabularies = vocabularyRepository.findAll();
        if( isDeleteVocabulary ) {
            assertThat(vocabularies).hasSize(vocabSizeBeforeDelete - 1);
        } else {
            //TODO: add more assertion
            final Vocabulary vocabulary = vocabularies.stream().filter(v -> v.getNotation().equals(versionToDelete.getNotation())).findFirst().orElse(null);
            assertThat(vocabulary).isNotNull();
        }

    }

    private Version createNewVocabularyVersionTest( Version version, boolean isTlVersioning) throws Exception {
        // create new version for added vocabulary
        restMockMvc.perform(post("/api/editors/vocabularies/new-version/" + version.getId())
            .header("Authorization", jwt))
            .andExpect(status().isOk());
        final Vocabulary vocabulary = vocabularyRepository.findByNotation(version.getNotation()).findAny().orElseThrow();
        assertThat(vocabulary).isNotNull();
        // sort to make sure that the order is correct from latest to oldest
        final List<Version> versions = vocabulary.getVersions().stream().sorted(VocabularyUtils.VERSION_COMPARATOR).collect(Collectors.toList());

        final Version versionSl = versions.stream().filter(v -> v.getItemType().equals(ITEM_TYPE_SL.toString())).findFirst().orElse(null);
        assertThat(versionSl).isNotNull();
        Version newVersion = versionSl;
        if( isTlVersioning ) {
            final Version versionTl = versions.stream().filter(v -> v.getItemType().equals(ITEM_TYPE_TL.toString()) &&
                v.getLanguage().equals(version.getLanguage())).findFirst().orElse(null);
            assertThat(versionTl).isNotNull();
            assertThat(versionTl.getNumber()).isEqualTo(version.getNumber().increasePatch(versionSl.getNumber()));
            newVersion = versionTl;
        } else {
            assertThat(versionSl.getNumber()).isEqualTo(version.getNumber().increaseMinorNumber());
        }
        return newVersion;
    }

    private void forwardTlStatusReadyToPublishTest(Version tlVersion) throws Exception {
        // prepare licence
        if( license == null ) {
            license = new Licence()
                .name(INIT_LICENSE)
                .link(INIT_LINK)
                .logoLink(INIT_LOGOPATH)
                .abbr(INIT_NAME);
            license = licenceRepository.saveAndFlush(license);
        }
        int databaseSize = vocabularyRepository.findAll().size();
        VocabularySnippet vocabularySnippet = EditorResourceIT.createVocabularySnippetForTlDe();
        vocabularySnippet.setActionType( ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH );
        vocabularySnippet.setVocabularyId( tlVersion.getVocabulary().getId() );
        vocabularySnippet.setAgencyId( agency.getId());
        vocabularySnippet.setVersionId( tlVersion.getId());
        vocabularySnippet.setLicenseId( license.getId() );
        vocabularySnippet.setVersionNumber( tlVersion.getNumber() );
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
            .andExpect(status().isOk());

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSize);
        tlVersion = getLatestVersionByNotationAndLang(vocabularyList, tlVersion.getNotation(), tlVersion.getLanguage());
        assertThat( tlVersion.getStatus()).isEqualTo(Status.READY_TO_PUBLISH.toString());
    }

    private void forwardTlStatusPublishTest(Version slVersion, Version tlVersion) throws Exception {
        int databaseSize = vocabularyRepository.findAll().size();
        vocabularySnippetForEnSl.setActionType( ActionType.FORWARD_CV_SL_STATUS_PUBLISH );
        vocabularySnippetForEnSl.setVersionId( slVersion.getId());
        vocabularySnippetForEnSl.setLicenseId( license.getId() );
        vocabularySnippetForEnSl.setVocabularyId( slVersion.getVocabulary().getId() );

        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isOk());

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSize);
        tlVersion = getLatestVersionByNotationAndLang(vocabularyList, tlVersion.getNotation(), tlVersion.getLanguage());
        assertThat( tlVersion.getStatus()).isEqualTo(Status.PUBLISHED.toString());
    }

    // private void forwardTlStatusPublishTest(Version tlVersion) throws Exception {
    //     // prepare licence
    //     if( license == null ) {
    //         license = new Licence()
    //             .name(INIT_LICENSE)
    //             .link(INIT_LINK)
    //             .logoLink(INIT_LOGOPATH)
    //             .abbr(INIT_NAME);
    //         license = licenceRepository.saveAndFlush(license);
    //     }
    //     int databaseSize = vocabularyRepository.findAll().size();
    //     VocabularySnippet vocabularySnippet = EditorResourceIT.createVocabularySnippetForTlDe();
    //     vocabularySnippet.setActionType( ActionType.FORWARD_CV_TL_STATUS_PUBLISH );
    //     vocabularySnippet.setVocabularyId( tlVersion.getVocabulary().getId() );
    //     vocabularySnippet.setAgencyId( agency.getId());
    //     vocabularySnippet.setVersionId( tlVersion.getId());
    //     vocabularySnippet.setLicenseId( license.getId() );
    //     restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
    //         .header("Authorization", jwt)
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
    //         .andExpect(status().isOk());

    //     List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
    //     assertThat(vocabularyList).hasSize(databaseSize);
    //     tlVersion = getLatestVersionByNotationAndLang(vocabularyList, tlVersion.getNotation(), tlVersion.getLanguage());
    //     assertThat( tlVersion.getStatus()).isEqualTo(Status.PUBLISHED.toString());
    // }

    private void forwardTlStatusReviewTest(Version tlVersion) throws Exception {
        int databaseSize = vocabularyRepository.findAll().size();
        VocabularySnippet vocabularySnippet = EditorResourceIT.createVocabularySnippetForTlDe();
        vocabularySnippet.setActionType( ActionType.FORWARD_CV_TL_STATUS_REVIEW );
        vocabularySnippet.setVocabularyId( tlVersion.getVocabulary().getId() );
        vocabularySnippet.setAgencyId( agency.getId());
        vocabularySnippet.setVersionId( tlVersion.getId());
        vocabularySnippet.setVersionNumber( tlVersion.getNumber() );
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
            .andExpect(status().isOk());

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSize);
        tlVersion = getLatestVersionByNotationAndLang(vocabularyList, tlVersion.getNotation(), tlVersion.getLanguage());
        assertThat( tlVersion.getStatus()).isEqualTo(Status.REVIEW.toString());
    }

    private void deleteTlConceptTest(Concept slConceptRoot1, Version tlVersion, Concept tlConcept1) throws Exception {
        CodeSnippet codeSnippetForDeTl = new CodeSnippet();
        codeSnippetForDeTl.setActionType( ActionType.DELETE_TL_CODE );
        codeSnippetForDeTl.setVersionId( tlVersion.getId() );
        codeSnippetForDeTl.setConceptId( tlConcept1.getId() );
        codeSnippetForDeTl.setTitle(null);
        codeSnippetForDeTl.setDefinition(null);
        restMockMvc.perform(put("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetForDeTl)))
            .andExpect(status().isOk());
        tlConcept1 = getConceptFromVocabulary(slConceptRoot1);
        assertThat(tlConcept1.getTitle()).isNull();
        assertThat(tlConcept1.getDefinition()).isNull();
    }

    private void editTlConceptTest(Concept slConceptRoot1, Version tlVersion, Concept tlConcept1) throws Exception {
        CodeSnippet codeSnippetForDeTl = new CodeSnippet();
        codeSnippetForDeTl.setActionType( ActionType.EDIT_TL_CODE );
        codeSnippetForDeTl.setVersionId( tlVersion.getId() );
        codeSnippetForDeTl.setConceptId( tlConcept1.getId() );
        codeSnippetForDeTl.setTitle(EDIT_CODE_TL_TITLE);
        codeSnippetForDeTl.setDefinition(EDIT_CODE_TL_DEFINITION);
        restMockMvc.perform(put("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetForDeTl)))
            .andExpect(status().isOk());
        tlConcept1 = getConceptFromVocabulary(slConceptRoot1);
        assertThat(tlConcept1.getTitle()).isEqualTo(EDIT_CODE_TL_TITLE);
        assertThat(tlConcept1.getDefinition()).isEqualTo(EDIT_CODE_TL_DEFINITION);
    }

    private Concept addTlConceptTest(Version tlVersion, Concept slConcept, String title, String definition) throws Exception {
        CodeSnippet codeSnippetForDeTl = new CodeSnippet();
        codeSnippetForDeTl.setActionType( ActionType.ADD_TL_CODE );
        Concept tlConcept = tlVersion.getConcepts().stream().filter(c -> c.getNotation().equals(slConcept.getNotation()))
            .findFirst().orElseThrow();
        assertThat(tlVersion).isNotNull();
        codeSnippetForDeTl.setVersionId( tlVersion.getId() );
        codeSnippetForDeTl.setConceptId( tlConcept.getId() );
        codeSnippetForDeTl.setTitle(title);
        codeSnippetForDeTl.setDefinition(definition);
        restMockMvc.perform(put("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetForDeTl)))
            .andExpect(status().isOk());
        tlConcept = getConceptFromVocabulary(slConcept);
        assertThat(tlConcept).isNotNull();
        assertThat(tlConcept.getTitle()).isEqualTo(title);
        assertThat(tlConcept.getDefinition()).isEqualTo(definition);
        return tlConcept;
    }

    private Concept getConceptFromVocabulary(Concept slConcept) {
        Version tlVersion;
        Concept tlConcept;
        Vocabulary vocabulary = vocabularyRepository.findAll().iterator().next();
        tlVersion = vocabulary.getVersions().stream().filter(v -> v.getItemType().equals(ITEM_TYPE_TL.toString()))
            .findFirst().orElse(null);
        assertThat(tlVersion).isNotNull();
        tlConcept = tlVersion.getConcepts().stream().filter( c -> c.getNotation().equals(slConcept.getNotation()))
            .findFirst().orElse(null);
        return tlConcept;
    }

    private Version addVocabularyTranslationInMiddleTest(Version slVersion) throws Exception {
        VocabularySnippet vocabularySnippetForTlDe = EditorResourceIT.createVocabularySnippetForTlDe();
        vocabularySnippetForTlDe.setActionType( ActionType.ADD_TL_CV );
        vocabularySnippetForTlDe.setVocabularyId( slVersion.getVocabulary().getId());
        vocabularySnippetForTlDe.setVersionSlId( slVersion.getId() );
        // Create the Vocabulary with code snippet
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForTlDe)))
            .andExpect(status().isCreated());

        final Version tlVersion = getLatestVersionByNotationAndLang(vocabularyRepository.findAll(), slVersion.getNotation(), vocabularySnippetForTlDe.getLanguage());

        // must be generated equal TL concepts to SL concepts
        assertThat(tlVersion.getConcepts().size()).isEqualTo(slVersion.getConcepts().size());
        assertTrue(tlVersion.getNumber().equalMinorVersionNumber(slVersion.getNumber()));
        // TL ddi-usage must be the same with SL
        assertThat(tlVersion.getDdiUsage()).isEqualTo(slVersion.getDdiUsage());

        return tlVersion;
    }

    private Version addVocabularyTranslationTest(Version slVersion) throws Exception {
        VocabularySnippet vocabularySnippetForTlDe = EditorResourceIT.createVocabularySnippetForTlDe();
        vocabularySnippetForTlDe.setActionType( ActionType.ADD_TL_CV );
        vocabularySnippetForTlDe.setVocabularyId( slVersion.getVocabulary().getId());
        vocabularySnippetForTlDe.setVersionSlId( slVersion.getId() );
        // Create the Vocabulary with code snippet
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForTlDe)))
            .andExpect(status().isCreated());

        // only contains 1 vocab
        Vocabulary testVocabulary = vocabularyRepository.findAll().iterator().next();
        final Version tlVersion = testVocabulary.getVersions().stream()
            .filter( v -> v.getItemType().equals(ITEM_TYPE_TL.toString())).findFirst().orElse(null);
        assertThat(tlVersion).isNotNull();
        // must be generated equal TL concepts to SL concepts
        assertThat(tlVersion.getConcepts().size()).isEqualTo(slVersion.getConcepts().size());
        assertThat(tlVersion.getStatus()).isEqualTo(INIT_STATUS);
        assertThat(tlVersion.getNotation()).isEqualTo(NOTATION);
        assertThat(tlVersion.getNumber()).isEqualTo(INIT_VERSION_NUMBER_TL);
        assertThat(tlVersion.getLanguage()).isEqualTo(TARGET_LANGUAGE);
        assertThat(tlVersion.getItemType()).isEqualTo(ITEM_TYPE_TL.toString());
        assertThat(tlVersion.getNotes()).isEqualTo(INIT_NOTES);
        assertThat(tlVersion.getTitle()).isEqualTo(INIT_TITLE_DE);
        assertThat(tlVersion.getDefinition()).isEqualTo(INIT_DEFINITION_DE);
        assertThat(tlVersion.getTranslateAgency()).isEqualTo(INIT_TRANS_AGENCY);
        assertThat(tlVersion.getTranslateAgencyLink()).isEqualTo(INIT_TRANS_AGENCY_LINK);
        // TL ddi-usage must be the same with SL
        assertThat(tlVersion.getDdiUsage()).isEqualTo(INIT_DDI_USAGE);

        return tlVersion;
    }

    private void updateSlVersionNotesAndUsageTests(Version slVersion) throws Exception {
        vocabularySnippetForEnSl.setActionType( ActionType.EDIT_NOTE_CV );
        vocabularySnippetForEnSl.setVocabularyId( slVersion.getVocabulary().getId() );
        vocabularySnippetForEnSl.setVersionId( slVersion.getId());
        vocabularySnippetForEnSl.setNotes(EDIT_NOTES);
        restMockMvc.perform(put("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isOk());
        vocabularySnippetForEnSl.setActionType( ActionType.EDIT_DDI_CV );
        vocabularySnippetForEnSl.setDdiUsage(INIT_DDI_USAGE);
        restMockMvc.perform(put("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isOk());
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        Vocabulary testVocabulary = vocabularyList.get(vocabularyList.size() - 1);
        final Version testVersion = testVocabulary.getVersions().iterator().next();
        assertThat(testVersion.getNotes()).isEqualTo(EDIT_NOTES);
        assertThat(testVersion.getDdiUsage()).isEqualTo(INIT_DDI_USAGE);
    }

    private void deleteCodeTest(Concept slConcept, Concept slConcept2) throws Exception {
        int conceptDbSize = conceptRepository.findAll().size();
        restMockMvc.perform(delete("/api/editors/codes/{id}", slConcept2.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        final List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(conceptDbSize - 2); // slConcepts 2 & 3 are deleted
        Concept conceptDelete = conceptList.stream()
            .filter(c -> c.getNotation().equals(slConcept2.getNotation()))
            .findFirst().orElse(null);
        assertThat( conceptDelete ).isNull();
        Concept concept = conceptList.stream()
            .filter(c -> c.getNotation().equals(slConcept.getNotation()))
            .findFirst().orElse(null);
        assertThat( concept ).isNotNull();
        assertThat( concept.getPosition() ).isEqualTo(CODE_POSITION);
    }

    private void reorderConceptsTest(Version slVersion, Concept slConceptRoot1, Concept slConceptRoot2,
                                     Concept slConcept2, Concept slConcept3) throws Exception {
        CodeSnippet codeSnippetCodeMove = new CodeSnippet();
        codeSnippetCodeMove.setConceptId(slConceptRoot1.getId());
        codeSnippetCodeMove.setActionType( ActionType.REORDER_CODE);
        codeSnippetCodeMove.setVersionId( slVersion.getId() );
        codeSnippetCodeMove.setConceptStructureIds(new LinkedList<>(
            Arrays.asList(
                slConceptRoot1.getId(),
                slConceptRoot2.getId(),
                slConcept2.getId(),
                slConcept3.getId()
                ))
        );
        codeSnippetCodeMove.setConceptStructures(new LinkedList<>(
            Arrays.asList(
                slConceptRoot1.getNotation(),
                slConceptRoot2.getNotation(),
                slConceptRoot2.getNotation() + "." +INIT_CODE2_NOTATION,
                slConceptRoot2.getNotation() + "." + INIT_CODE2_NOTATION + "." + INIT_CODE3_NOTATION
                ))
        );

        restMockMvc.perform(post("/api/editors/codes/reorder")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetCodeMove)))
            .andExpect(status().isOk());

        final List<Concept> conceptList = conceptRepository.findAll();
        Concept concept1 = conceptList.stream()
            .filter(c -> c.getNotation().equals(slConceptRoot2.getNotation() + "." +INIT_CODE2_NOTATION))
            .findFirst().orElse(null);
        assertThat( concept1 ).isNotNull();
        assertThat( concept1.getParent() ).isEqualTo(slConceptRoot2.getNotation());
        assertThat( concept1.getPosition() ).isEqualTo(CODE2_POSITION);
        Concept concept2 = conceptList.stream()
            .filter(c -> c.getNotation().equals(slConceptRoot1.getNotation()))
            .findFirst().orElse(null);
        assertThat( concept2 ).isNotNull();
        assertThat( concept2.getPosition() ).isEqualTo(CODE_POSITION);
    }

    private Concept createSlConceptChildTest(Version slVersion, Concept slConceptParent, boolean isThirdCode) throws Exception {
        int vocabChangeDbSize = vocabularyChangeRepository.findAll().size();
        int conceptDbSize = conceptRepository.findAll().size();
        CodeSnippet codeSnippet2ForEnSl = createCodeSnippet(
            isThirdCode ? INIT_CODE3_NOTATION: INIT_CODE2_NOTATION,
            isThirdCode ? INIT_CODE3_TITLE: INIT_CODE2_TITLE,
            isThirdCode ? INIT_CODE3_DEF: INIT_CODE2_DEF,
            isThirdCode ? CODE3_POSITION: CODE2_POSITION, null );
        codeSnippet2ForEnSl.setActionType( ActionType.CREATE_CODE );
        codeSnippet2ForEnSl.setVersionId( slVersion.getId() );
        codeSnippet2ForEnSl.setParent( slConceptParent.getNotation() );
        codeSnippet2ForEnSl.setNotation( slConceptParent.getNotation() + "." + (isThirdCode ? INIT_CODE3_NOTATION: INIT_CODE2_NOTATION));
        codeSnippet2ForEnSl.setChangeDesc( codeSnippet2ForEnSl.getNotation() );
        restMockMvc.perform(post("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippet2ForEnSl)))
            .andExpect(status().isCreated());
        final List<VocabularyChange> vocabularyChangeList = vocabularyChangeRepository.findAll();
        assertThat(vocabularyChangeList).hasSize(vocabChangeDbSize); // no record size change due to initial version
        final List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(conceptDbSize + 1);
        Concept childConcept = conceptList.stream()
            .filter(c -> c.getNotation().equals(slConceptParent.getNotation() + "." + (isThirdCode ? INIT_CODE3_NOTATION: INIT_CODE2_NOTATION)))
            .findFirst().orElse(null);
        assertThat( childConcept ).isNotNull();
        assertThat( childConcept.getPosition() ).isEqualTo(isThirdCode ? CODE3_POSITION: CODE2_POSITION);
        return childConcept;
    }

    private void updateSlConceptTest(Version slVersion, Concept slConcept) throws Exception {
        int conceptDatabaseSize = conceptRepository.findAll().size();
        CodeSnippet codeSnippetForEnSl = updateCodeSnippetForSlEn();
        codeSnippetForEnSl.setActionType( ActionType.EDIT_CODE );
        codeSnippetForEnSl.setVersionId( slVersion.getId() );
        codeSnippetForEnSl.setConceptId( slConcept.getId() );
        // Create the code with code snippet
        restMockMvc.perform(put("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetForEnSl)))
            .andExpect(status().isOk());
        final List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(conceptDatabaseSize);
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        slVersion = getLatestVersionByNotationAndLang(vocabularyList, slVersion.getNotation(), slVersion.getLanguage());
        final Concept updatedSlConcept = slVersion.getConcepts().iterator().next();
        assertThat(updatedSlConcept.getNotation()).isEqualTo(EDIT_CODE_NOTATION);
        assertThat(updatedSlConcept.getTitle()).isEqualTo(EDIT_CODE_TITLE);
        assertThat(updatedSlConcept.getDefinition()).isEqualTo(EDIT_CODE_DEF);
    }

    private Concept createSlRootConceptTest(Version slVersion, boolean isSecondRootConcept) throws Exception {
        int conceptDatabaseSize = conceptRepository.findAll().size();
        CodeSnippet codeSnippetForEnSl = createCodeSnippet(
            isSecondRootConcept ? INIT_CODE4_NOTATION: INIT_CODE_NOTATION,
            isSecondRootConcept ? INIT_CODE4_TITLE : INIT_CODE_TITLE,
            isSecondRootConcept ? INIT_CODE4_DEF: INIT_CODE_DEF,
            null,
            null );
        codeSnippetForEnSl.setActionType( ActionType.CREATE_CODE );
        codeSnippetForEnSl.setVersionId( slVersion.getId() );
        // Create the code with code snippet
        restMockMvc.perform(post("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetForEnSl)))
            .andExpect(status().isCreated());
        final List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(conceptDatabaseSize + 1);
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        slVersion = getLatestVersionByNotationAndLang(vocabularyList, slVersion.getNotation(), slVersion.getLanguage());
        final Concept slConcept = slVersion.getConcepts().stream().filter(c -> c.getNotation()
            .equals(isSecondRootConcept ? INIT_CODE4_NOTATION: INIT_CODE_NOTATION)).findFirst().orElseThrow();
        assertThat(slConcept.getParent()).isNull();
        assertThat(slConcept.getPreviousConcept()).isNull();
        assertThat(slConcept.getPosition()).isEqualTo(isSecondRootConcept ? CODE4_POSITION : CODE_POSITION);
        assertThat(slConcept.getNotation()).isEqualTo(isSecondRootConcept ? INIT_CODE4_NOTATION: INIT_CODE_NOTATION);
        assertThat(slConcept.getTitle()).isEqualTo(isSecondRootConcept ? INIT_CODE4_TITLE : INIT_CODE_TITLE);
        assertThat(slConcept.getDefinition()).isEqualTo(isSecondRootConcept ? INIT_CODE4_DEF : INIT_CODE_DEF);
        return slConcept;
    }

    private void createSlConceptOnPublishedCvTest(Version slVersion) throws Exception {
        CodeSnippet codeSnippetForEnSl = createCodeSnippet( EDIT_CODE_NOTATION, EDIT_CODE_TITLE, EDIT_CODE_DEF, CODE_POSITION, null );
        codeSnippetForEnSl.setActionType( ActionType.CREATE_CODE );
        codeSnippetForEnSl.setVersionId( slVersion.getId() );
        restMockMvc.perform(post("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetForEnSl)))
            .andExpect(status().is5xxServerError());
    }

    private void createFailedSlConceptTest(Version slVersion) throws Exception {
        CodeSnippet codeSnippetForEnSl = createCodeSnippet( INIT_CODE_NOTATION, INIT_CODE_TITLE, INIT_CODE_DEF, CODE_POSITION, null );
        codeSnippetForEnSl.setActionType( ActionType.CREATE_CODE );
        codeSnippetForEnSl.setVersionId( slVersion.getId() );
        restMockMvc.perform(post("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetForEnSl)))
            .andExpect(status().isBadRequest());
    }

    private void searchingCodeTest(Version slVersion, Concept slConceptRoot1) throws Exception {
        // Code Search Test
        restMockMvc.perform(get("/v2/search/codes?agency=" + INIT_AGENCY_NAME + "&lang=" +
            slVersion.getLanguage() + "&query=" + slConceptRoot1.getNotation()+ "&vocab=" +
            slVersion.getNotation()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(VocabularyResourceV2.JSONLD_TYPE))
            .andExpect(jsonPath("$.results.[*].notation").value(hasItem(slConceptRoot1.getNotation())))
            .andExpect(jsonPath("$.results.[*].prefLabel").value(hasItem(slConceptRoot1.getTitle())))
            .andExpect(jsonPath("$.results.[*].definition").value(hasItem(slConceptRoot1.getDefinition())));
        // Code Search Test  with wildcard
        restMockMvc.perform(get("/v2/search/codes?agency=" + INIT_AGENCY_NAME + "&lang=" +
            slVersion.getLanguage() + "&query=" + slConceptRoot1.getNotation().substring(3) + "*"+ "&vocab=" +
            slVersion.getNotation()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(VocabularyResourceV2.JSONLD_TYPE))
            .andExpect(jsonPath("$.results.[*].notation").value(hasItem(slConceptRoot1.getNotation())))
            .andExpect(jsonPath("$.results.[*].prefLabel").value(hasItem(slConceptRoot1.getTitle())))
            .andExpect(jsonPath("$.results.[*].definition").value(hasItem(slConceptRoot1.getDefinition())));
    }

    private void searchingCvEditorAndPublication1Test(Version slVersion) throws Exception {
        // Elasticsearch Search Test ~ Editor search match all
        restMockMvc.perform(get("/api/editors/search?page=0&size=30&sort=code,asc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.vocabularies.[*].agencyName").value(hasItem(INIT_AGENCY_NAME)))
            .andExpect(jsonPath("$.vocabularies.[*].notation").value(hasItem(slVersion.getNotation())))
            .andExpect(jsonPath("$.vocabularies.[*].titleEn").value(hasItem(slVersion.getTitle())))
            .andExpect(jsonPath("$.vocabularies.[*].definitionEn").value(hasItem(slVersion.getDefinition())));
        // Elasticsearch Search Test ~ Editor search with query and filters
        restMockMvc.perform(get("/api/editors/search?page=0&" +
            "q=" + slVersion.getTitle() + "&f=agency:"+ INIT_AGENCY_NAME + ";language:" + slVersion.getLanguage() + ";status:PUBLISHED&" +
            "sort=relevance"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.vocabularies.[*].agencyName").value(hasItem(INIT_AGENCY_NAME)))
            .andExpect(jsonPath("$.vocabularies.[*].notation").value(hasItem(slVersion.getNotation())));
        // Elasticsearch Search Test ~ Publication search match all
        restMockMvc.perform(get("/v2/search?page=0&size=30&sort=code,asc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.vocabularies.[*].agencyName").value(hasItem(INIT_AGENCY_NAME)))
            .andExpect(jsonPath("$.vocabularies.[*].notation").value(hasItem(slVersion.getNotation())))
            .andExpect(jsonPath("$.vocabularies.[*].titleEn").value(hasItem(slVersion.getTitle())))
            .andExpect(jsonPath("$.vocabularies.[*].definitionEn").value(hasItem(slVersion.getDefinition())));
        // Elasticsearch Search Test ~ Publication search with query and filters
        restMockMvc.perform(get("/v2/search?page=0&" +
            "q=" + slVersion.getTitle() + "&f=agency:"+ INIT_AGENCY_NAME + ";language:" + slVersion.getLanguage() + ";status:PUBLISHED&" +
            "sort=relevance"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.vocabularies.[*].agencyName").value(hasItem(INIT_AGENCY_NAME)))
            .andExpect(jsonPath("$.vocabularies.[*].notation").value(hasItem(slVersion.getNotation())));
    }

    private void forwardSlStatusReadyToTranslateTest(Version slVersion, boolean secondPublish) throws Exception {
        // prepare licence
        if( license == null ) {
            license = new Licence()
                .name(INIT_LICENSE)
                .link(INIT_LINK)
                .logoLink(INIT_LOGOPATH)
                .abbr(INIT_NAME);
            license = licenceRepository.saveAndFlush(license);
        }
        int databaseSize = vocabularyRepository.findAll().size();
        vocabularySnippetForEnSl.setActionType( ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE );
        vocabularySnippetForEnSl.setVersionId( slVersion.getId());
        vocabularySnippetForEnSl.setLicenseId( license.getId() );
        vocabularySnippetForEnSl.setVocabularyId( slVersion.getVocabulary().getId() );
        if( secondPublish )
            vocabularySnippetForEnSl.setVersionNumber(VersionNumber.fromString("1.1"));
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isOk());

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSize);
        slVersion = getLatestVersionByNotationAndLang(vocabularyList, slVersion.getNotation(), slVersion.getLanguage());
        assertThat( slVersion.getStatus()).isEqualTo(Status.READY_TO_TRANSLATE.toString());
    }

    private void forwardSlStatusPublishTest(Version slVersion, boolean secondPublish) throws Exception {
        // prepare licence
        if( license == null ) {
            license = new Licence()
                .name(INIT_LICENSE)
                .link(INIT_LINK)
                .logoLink(INIT_LOGOPATH)
                .abbr(INIT_NAME);
            license = licenceRepository.saveAndFlush(license);
        }
        int databaseSize = vocabularyRepository.findAll().size();
        vocabularySnippetForEnSl.setActionType( ActionType.FORWARD_CV_SL_STATUS_PUBLISH );
        vocabularySnippetForEnSl.setVersionId( slVersion.getId());
        vocabularySnippetForEnSl.setLicenseId( license.getId() );
        vocabularySnippetForEnSl.setVocabularyId( slVersion.getVocabulary().getId() );
        if( secondPublish )
            vocabularySnippetForEnSl.setVersionNumber(VersionNumber.fromString("1.1"));
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isOk());

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSize);
        slVersion = getLatestVersionByNotationAndLang(vocabularyList, slVersion.getNotation(), slVersion.getLanguage());
        assertThat( slVersion.getStatus()).isEqualTo(Status.PUBLISHED.toString());
    }

    public void forwardStatusWithIncorrectActionTypeTest(Version slVersion) throws Exception {
        vocabularySnippetForEnSl.setActionType( ActionType.CREATE_CODE );
        vocabularySnippetForEnSl.setVocabularyId( slVersion.getVocabulary().getId() );
        vocabularySnippetForEnSl.setVersionId( slVersion.getId());
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().is5xxServerError());
    }

    private void forwardSlStatusReviewTest(Version slVersion) throws Exception {
        int databaseSize = vocabularyRepository.findAll().size();
        vocabularySnippetForEnSl.setActionType( ActionType.FORWARD_CV_SL_STATUS_REVIEW );
        vocabularySnippetForEnSl.setVocabularyId( slVersion.getVocabulary().getId() );
        vocabularySnippetForEnSl.setVersionId( slVersion.getId());
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isOk());

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSize);
        slVersion = getLatestVersionByNotationAndLang(vocabularyList, slVersion.getNotation(), slVersion.getLanguage());
        assertThat( slVersion.getStatus()).isEqualTo(Status.REVIEW.toString());
    }

    private Version getLatestVersionByNotationAndLang(List<Vocabulary> vocabularyList, String notation, String lang) {
        Version version;
        Vocabulary testVocabulary = vocabularyList.stream().filter(v -> v.getNotation().equals( notation ))
            .findFirst().orElse(null);
        assertThat(testVocabulary).isNotNull();
        // sort to make sure that the order is correct from latest to oldest
        final List<Version> versions = testVocabulary.getVersions().stream()
            .sorted(VocabularyUtils.VERSION_COMPARATOR).collect(Collectors.toList());

        version = versions.stream().filter( v -> v.getLanguage().equals( lang))
            .findFirst().orElse(null);
        assertThat(version).isNotNull();
        return version;
    }

    private void updateVocabularySlTest(Version slVersion) throws Exception {
        int databaseSize = vocabularyRepository.findAll().size();
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        Vocabulary vocabulary = vocabularyList.get(vocabularyList.size() - 1);
        // prepare to update
        final VocabularySnippet vocabularySnippet = updateVocabularySnippetForSlEn();
        vocabularySnippet.setActionType( ActionType.EDIT_CV );
        vocabularySnippet.setVocabularyId( vocabulary.getId() );
        vocabularySnippet.setVersionId( slVersion.getId());

        // update
        restMockMvc.perform(put("/api/editors/vocabularies")
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
        slVersion = testVocabulary.getVersions().iterator().next();
        assertThat(slVersion.getNotes()).isEqualTo(EDIT_NOTES);
        assertThat(slVersion.getTitle()).isEqualTo(EDIT_TITLE_EN);
        assertThat(slVersion.getDefinition()).isEqualTo(EDIT_DEFINITION_EN);
    }

    private void createVocabularyAndSlInitialVersion() throws Exception {
        vocabularySnippetForEnSl.setActionType( ActionType.CREATE_CV );
        // Create the Vocabulary with code snippet
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isCreated());
    }

    private void createVocabularyWithSameNotationFailTest() throws Exception {
        // Create the Vocabulary with existing notation
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isBadRequest());
    }

    private Version createVocabularyTest() throws Exception {
        int databaseSizeBeforeCreate = vocabularyRepository.findAll().size();
        createVocabularyAndSlInitialVersion();

        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeCreate + 1);
        Vocabulary testVocabulary = vocabularyList.get(vocabularyList.size() - 1);
        assertThat(testVocabulary.getStatus()).isEqualTo(INIT_STATUS);
        assertThat(testVocabulary.getNotation()).isEqualTo(NOTATION);
        assertThat(testVocabulary.getVersionNumber()).isEqualTo(INIT_VERSION_NUMBER_SL);
        assertThat(testVocabulary.getSourceLanguage()).isEqualTo(SOURCE_LANGUAGE);
        assertThat(testVocabulary.getAgencyName()).isEqualTo(INIT_AGENCY_NAME);
        assertThat(testVocabulary.getAgencyLogo()).isEqualTo(INIT_AGENCY_LOGO);
        assertThat(testVocabulary.getNotes()).isEqualTo(INIT_NOTES);
        assertThat(testVocabulary.getTitleEn()).isEqualTo(INIT_TITLE_EN);
        assertThat(testVocabulary.getDefinitionEn()).isEqualTo(INIT_DEFINITION_EN);

        assertThat(testVocabulary.getVersions().size()).isEqualTo(1);
        final Version slVersion = testVocabulary.getVersions().iterator().next();
        assertThat(slVersion.getStatus()).isEqualTo(INIT_STATUS);
        assertThat(slVersion.getNotation()).isEqualTo(NOTATION);
        assertThat(slVersion.getNumber()).isEqualTo(INIT_VERSION_NUMBER_SL);
        assertThat(slVersion.getLanguage()).isEqualTo(SOURCE_LANGUAGE);
        assertThat(slVersion.getItemType()).isEqualTo(ITEM_TYPE_SL.toString());
        assertThat(slVersion.getNotes()).isEqualTo(INIT_NOTES);
        assertThat(slVersion.getTitle()).isEqualTo(INIT_TITLE_EN);
        assertThat(slVersion.getDefinition()).isEqualTo(INIT_DEFINITION_EN);

        return slVersion;
    }

    @Test
    @Transactional
    void createVocabularyWithGivenIdTest() throws Exception {
        int databaseSizeBeforeUpdate = vocabularyRepository.findAll().size();

        vocabularySnippetForEnSl.setActionType( ActionType.CREATE_CV );
        // add random ID, so the post will fail
        vocabularySnippetForEnSl.setVocabularyId(1L);
        // Create the Vocabulary with code snippet
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isBadRequest());

        // Validate the Vocabulary in the database
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void updateVocabularyWithVersionIdNull() throws Exception {
        vocabularySnippetForEnSl.setActionType( ActionType.EDIT_CV );
        // vocabularySnippet versionId is NULL
        restMockMvc.perform(put("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void createCodeWithExistedId() throws Exception {
        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setActionType( ActionType.CREATE_CODE );
        codeSnippet.setConceptId( 1L );
        // vocabularySnippet versionId is NULL
        restMockMvc.perform(post("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippet)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void forwardStatusWithVersionIdNull() throws Exception {
        // vocabularySnippet versionId is NULL
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @Transactional
    void forwardStatusWithActionNull() throws Exception {
        // vocabularySnippet Action is NULL
        vocabularySnippetForEnSl.setVersionId(1L);
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @Transactional
    void createCodeWithInvalidAction() throws Exception {
        vocabularySnippetForEnSl.setActionType( ActionType.REORDER_CODE );
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @Transactional
    void reorderCodeWithInvalidAction() throws Exception {
        CodeSnippet codeSnippetInvalidAction = new CodeSnippet();
        codeSnippetInvalidAction.setActionType( ActionType.CREATE_CODE );
        restMockMvc.perform(post("/api/editors/codes/reorder")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetInvalidAction)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    void putCodeWithInvalidAction() throws Exception {
        CodeSnippet codeSnippetInvalidAction = new CodeSnippet();
        codeSnippetInvalidAction.setActionType( ActionType.CREATE_CODE );
        restMockMvc.perform(put("/api/editors/codes")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(codeSnippetInvalidAction)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    void createVocabularyActionNull() throws Exception {
        // vocabularySnippet Action is NULL
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @Transactional
    void updateVocabularyActionIncorrectTest() throws Exception {
        // vocabularySnippet Action is NULL
        vocabularySnippetForEnSl.setVocabularyId(1L);
        vocabularySnippetForEnSl.setActionType(ActionType.CREATE_CV);
        restMockMvc.perform(put("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippetForEnSl)))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @Transactional
    void createNewCvVersionNonPublishedTest() throws Exception {
        // create initial vocabulary
        Version slVersion = createVocabularyTest();
        // will be failed to create new version due to Cv still on DRAFT
        restMockMvc.perform(post("/api/editors/vocabularies/new-version/" + slVersion.getId())
            .header("Authorization", jwt))
            .andExpect(status().is5xxServerError());
    }

}
