/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.config.audit.AuditEventPublisher;
import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.UserDetails;
import eu.cessda.cvs.service.*;
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.utils.VersionNumber;
import eu.cessda.cvs.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link EditorResource} REST controller, focusing on the error paths and
 * permission checks. All services are mocked, the controller is exercised through a standalone
 * {@link MockMvc} with the application {@link ExceptionTranslator}, so the HTTP status codes
 * are the same as in the running application.
 */
class EditorResourceTest {

    private static final String APP_NAME = "cvsApp";
    private static final Long AGENCY_ID = 1L;
    private static final Long VOCABULARY_ID = 10L;
    private static final String NOTATION = "TestCV";
    private static final String SOURCE_LANGUAGE = "en";
    private static final String TARGET_LANGUAGE = "de";

    private AuditEventPublisher auditPublisher;
    private VocabularyService vocabularyService;
    private VersionService versionService;
    private ConceptService conceptService;
    private CommentService commentService;
    private MetadataFieldService metadataFieldService;
    private MetadataValueService metadataValueService;
    private VocabularyChangeService vocabularyChangeService;
    private ApplicationProperties applicationProperties;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        auditPublisher = mock( AuditEventPublisher.class );
        vocabularyService = mock( VocabularyService.class );
        versionService = mock( VersionService.class );
        conceptService = mock( ConceptService.class );
        commentService = mock( CommentService.class );
        metadataFieldService = mock( MetadataFieldService.class );
        metadataValueService = mock( MetadataValueService.class );
        vocabularyChangeService = mock( VocabularyChangeService.class );
        applicationProperties = new ApplicationProperties( Path.of( "target", "editor-resource-test" ) );

        EditorResource editorResource = new EditorResource( auditPublisher, vocabularyService, versionService, conceptService,
            commentService, metadataFieldService, metadataValueService, applicationProperties, vocabularyChangeService );
        ReflectionTestUtils.setField( editorResource, "applicationName", APP_NAME );

        ExceptionTranslator exceptionTranslator = new ExceptionTranslator();
        ReflectionTestUtils.setField( exceptionTranslator, "applicationName", APP_NAME );

        ObjectMapper objectMapper = JsonMapper.builder()
            .addModule( new JavaTimeModule() )
            .addModule( new ProblemModule() )
            .addModule( new ConstraintViolationProblemModule() )
            .build();

        mockMvc = MockMvcBuilders.standaloneSetup( editorResource )
            .setControllerAdvice( exceptionTranslator )
            .setMessageConverters( new MappingJackson2HttpMessageConverter( objectMapper ), new ResourceHttpMessageConverter() )
            .build();

        // most tests run as a content administrator, which is allowed to do everything
        authenticateAsContentAdmin();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------------------------------------
    // Security helpers
    // ------------------------------------------------------------------------------------------

    private static void authenticateAsContentAdmin() {
        authenticate( Collections.singleton( "ROLE_ADMIN_CONTENT" ), Collections.emptySet() );
    }

    /**
     * Authenticate as a user that has only the given agency role for the test agency.
     */
    private static void authenticateWithAgencyRole( AgencyRole agencyRole, String language ) {
        UserAgencyDTO userAgency = new UserAgencyDTO();
        userAgency.setAgencyId( AGENCY_ID );
        userAgency.setAgencyRole( agencyRole );
        userAgency.setLanguage( language );
        authenticate( Collections.singleton( "ROLE_USER" ), Collections.singleton( userAgency ) );
    }

    private static void authenticate( Set<String> authorities, Set<UserAgencyDTO> userAgencies ) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        authorities.forEach( a -> grantedAuthorities.add( new SimpleGrantedAuthority( a ) ) );

        UserDTO userDTO = new UserDTO();
        userDTO.setId( 42L );
        userDTO.setLogin( "editor" );
        userDTO.setAuthorities( authorities );
        userDTO.setUserAgencies( new HashSet<>( userAgencies ) );

        UserDetails userDetails = new UserDetails( "editor", "password", grantedAuthorities );
        userDetails.setUser( userDTO );

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken( userDetails, "password", grantedAuthorities ) );
    }

    // ------------------------------------------------------------------------------------------
    // Fixture helpers
    // ------------------------------------------------------------------------------------------

    private static VersionDTO version( Long id, ItemType itemType, String language, Status status, String number ) {
        VersionDTO versionDTO = new VersionDTO();
        versionDTO.setId( id );
        versionDTO.setVocabularyId( VOCABULARY_ID );
        versionDTO.setItemType( itemType );
        versionDTO.setLanguage( language );
        versionDTO.setStatus( status );
        versionDTO.setNumber( VersionNumber.fromString( number ) );
        versionDTO.setNotation( NOTATION );
        versionDTO.setTitle( "Title " + language + " " + number );
        versionDTO.setDefinition( "Definition " + language + " " + number );
        return versionDTO;
    }

    private static VocabularyDTO vocabulary( VersionDTO... versions ) {
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        vocabularyDTO.setId( VOCABULARY_ID );
        vocabularyDTO.setAgencyId( AGENCY_ID );
        vocabularyDTO.setNotation( NOTATION );
        vocabularyDTO.setSourceLanguage( SOURCE_LANGUAGE );
        vocabularyDTO.setVersionNumber( VersionNumber.fromString( "1.0" ) );
        for ( VersionDTO versionDTO : versions ) {
            vocabularyDTO.addVersion( versionDTO );
        }
        return vocabularyDTO;
    }

    private static ConceptDTO concept( Long id, Long versionId, String notation, String parent, int position ) {
        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setId( id );
        conceptDTO.setVersionId( versionId );
        conceptDTO.setNotation( notation );
        conceptDTO.setParent( parent );
        conceptDTO.setPosition( position );
        conceptDTO.setTitle( "Title " + notation );
        return conceptDTO;
    }

    private static CodeSnippet codeSnippet( ActionType actionType, Long versionId ) {
        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setActionType( actionType );
        codeSnippet.setVersionId( versionId );
        return codeSnippet;
    }

    private static VocabularySnippet vocabularySnippet( ActionType actionType ) {
        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setActionType( actionType );
        vocabularySnippet.setAgencyId( AGENCY_ID );
        vocabularySnippet.setVocabularyId( VOCABULARY_ID );
        vocabularySnippet.setVersionId( 100L );
        vocabularySnippet.setLanguage( SOURCE_LANGUAGE );
        vocabularySnippet.setItemType( ItemType.SL );
        vocabularySnippet.setNotation( NOTATION );
        return vocabularySnippet;
    }

    private static byte[] json( Object object ) throws Exception {
        return TestUtil.convertObjectToJsonBytes( object );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the GET /editors/vocabularies/{notation}/{versionNumber} endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void getVocabularyByNotationAndVersionShouldReturnVocabulary() throws Exception {
        when( vocabularyService.getVocabularyByNotationAndVersion( NOTATION, "1.0", false ) ).thenReturn( vocabulary() );

        mockMvc.perform( get( "/api/editors/vocabularies/{notation}/{version}", NOTATION, "1.0" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.notation" ).value( NOTATION ) );
    }

    @Test
    void getVocabularyByNotationAndVersionShouldReturnNotFoundWhenMissing() throws Exception {
        when( vocabularyService.getVocabularyByNotationAndVersion( NOTATION, "9.9", false ) ).thenReturn( null );

        mockMvc.perform( get( "/api/editors/vocabularies/{notation}/{version}", NOTATION, "9.9" ) )
            .andExpect( status().isNotFound() );
    }

    // ------------------------------------------------------------------------------------------
    // POST/PUT /editors/vocabularies
    // ------------------------------------------------------------------------------------------

    @Test
    void createVocabularyShouldReturnForbiddenWhenServiceRejectsAuthority() throws Exception {
        VocabularySnippet snippet = vocabularySnippet( ActionType.CREATE_CV );
        snippet.setVocabularyId( null );
        when( vocabularyService.saveVocabulary( any( VocabularySnippet.class ) ) )
            .thenThrow( new InsufficientVocabularyAuthorityException( ActionType.CREATE_CV ) );

        mockMvc.perform( post( "/api/editors/vocabularies" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isForbidden() );

        verifyNoInteractions( auditPublisher );
    }

    @Test
    void createVocabularyShouldAuditWithEmptyUserWhenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();
        VocabularySnippet snippet = vocabularySnippet( ActionType.ADD_TL_CV );
        VocabularyDTO saved = vocabulary();
        when( vocabularyService.saveVocabulary( any( VocabularySnippet.class ) ) ).thenReturn( saved );

        mockMvc.perform( post( "/api/editors/vocabularies" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isCreated() )
            .andExpect( header().string( "Location", "/api/vocabularies/" + VOCABULARY_ID ) );

        verify( auditPublisher ).publish( eq( "" ), eq( saved ), isNull(), any( VocabularySnippet.class ), eq( "CREATE_VOCABULARY" ) );
    }

    @ParameterizedTest
    @EnumSource( value = ActionType.class, names = { "EDIT_CV", "EDIT_DDI_CV", "EDIT_IDENTITY_CV", "EDIT_VERSION_INFO_CV", "EDIT_NOTE_CV" } )
    void updateVocabularyShouldAcceptAllEditActionTypes( ActionType actionType ) throws Exception {
        VocabularyDTO vocabularyDTO = vocabulary();
        when( vocabularyService.saveVocabulary( any( VocabularySnippet.class ) ) ).thenReturn( vocabularyDTO );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        mockMvc.perform( put( "/api/editors/vocabularies" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( vocabularySnippet( actionType ) ) ) )
            .andExpect( status().isOk() );

        verify( auditPublisher ).publish( eq( "editor" ), eq( vocabularyDTO ), isNull(), any( VocabularySnippet.class ),
            eq( "UPDATE_VOCABULARY_" + actionType ) );
    }

    @Test
    void updateVocabularyShouldRejectNonEditActionType() throws Exception {
        mockMvc.perform( put( "/api/editors/vocabularies" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( vocabularySnippet( ActionType.DELETE_CV ) ) ) )
            .andExpect( status().isBadRequest() );

        verify( vocabularyService, never() ).saveVocabulary( any() );
    }

    @Test
    void updateVocabularyShouldFailWhenVocabularyDisappearsBeforeAuditing() throws Exception {
        when( vocabularyService.saveVocabulary( any( VocabularySnippet.class ) ) ).thenReturn( vocabulary() );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.empty() );

        mockMvc.perform( put( "/api/editors/vocabularies" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( vocabularySnippet( ActionType.EDIT_NOTE_CV ) ) ) )
            .andExpect( status().isInternalServerError() );

        verifyNoInteractions( auditPublisher );
    }

    @Test
    void updateVocabularyShouldReturnForbiddenWhenServiceRejectsAuthority() throws Exception {
        when( vocabularyService.saveVocabulary( any( VocabularySnippet.class ) ) )
            .thenThrow( new InsufficientVocabularyAuthorityException( ActionType.EDIT_CV ) );

        mockMvc.perform( put( "/api/editors/vocabularies" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( vocabularySnippet( ActionType.EDIT_CV ) ) ) )
            .andExpect( status().isForbidden() );

        verifyNoInteractions( auditPublisher );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the POST /editors/vocabularies/new-version/{id} endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void createNewVersionShouldReturnForbiddenWhenServiceRejectsAuthority() throws Exception {
        when( vocabularyService.createNewVersion( 100L ) )
            .thenThrow( new InsufficientVocabularyAuthorityException( ActionType.CREATE_NEW_CV_SL_VERSION ) );

        mockMvc.perform( post( "/api/editors/vocabularies/new-version/{id}", 100L ) )
            .andExpect( status().isForbidden() );

        verifyNoInteractions( auditPublisher );
    }

    @Test
    void createNewVersionShouldFailWhenPreviousVersionIsNotPublished() throws Exception {
        when( vocabularyService.createNewVersion( 100L ) )
            .thenThrow( new IllegalArgumentException( "Unable to create new version from version with ID 100, version is not yet PUBLISHED" ) );

        mockMvc.perform( post( "/api/editors/vocabularies/new-version/{id}", 100L ) )
            .andExpect( status().isInternalServerError() );

        verifyNoInteractions( auditPublisher );
    }

    @Test
    void createNewVersionShouldAuditWithEmptyUserWhenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();
        VersionDTO newVersion = version( 101L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        when( vocabularyService.createNewVersion( 100L ) ).thenReturn( newVersion );

        mockMvc.perform( post( "/api/editors/vocabularies/new-version/{id}", 100L ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.id" ).value( 101 ) );

        verify( auditPublisher ).publish( eq( "" ), isNull(), eq( newVersion ), isNull(), eq( "CREATE_NEW_VOCABULARY_VERSION" ) );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the PUT /editors/vocabularies/forward-status endpoint
    // ------------------------------------------------------------------------------------------

    @ParameterizedTest
    @EnumSource( value = ActionType.class, names = { "FORWARD_CV_SL_STATUS_REVIEW", "FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE",
        "FORWARD_CV_SL_STATUS_PUBLISH", "FORWARD_CV_TL_STATUS_REVIEW", "FORWARD_CV_TL_STATUS_READY_TO_PUBLISH" } )
    void forwardStatusShouldAcceptAllForwardActionTypes( ActionType actionType ) throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.REVIEW, "1.0" );
        VocabularyDTO vocabularyDTO = vocabulary( versionDTO );
        when( vocabularyService.forwardStatus( any( VocabularySnippet.class ) ) ).thenReturn( versionDTO );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        mockMvc.perform( put( "/api/editors/vocabularies/forward-status" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( vocabularySnippet( actionType ) ) ) )
            .andExpect( status().isOk() );

        verify( auditPublisher ).publish( eq( "editor" ), eq( vocabularyDTO ), eq( versionDTO ), any( VocabularySnippet.class ),
            eq( actionType.name() ), eq( true ) );
    }

    @Test
    void forwardStatusShouldAuditWithEmptyUserWhenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.REVIEW, "1.0" );
        VocabularyDTO vocabularyDTO = vocabulary( versionDTO );
        when( vocabularyService.forwardStatus( any( VocabularySnippet.class ) ) ).thenReturn( versionDTO );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        mockMvc.perform( put( "/api/editors/vocabularies/forward-status" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_REVIEW ) ) ) )
            .andExpect( status().isOk() );

        verify( auditPublisher ).publish( eq( "" ), eq( vocabularyDTO ), eq( versionDTO ), any( VocabularySnippet.class ),
            eq( "FORWARD_CV_SL_STATUS_REVIEW" ), eq( true ) );
    }

    @Test
    void forwardStatusShouldReturnForbiddenWhenServiceRejectsAuthority() throws Exception {
        when( vocabularyService.forwardStatus( any( VocabularySnippet.class ) ) )
            .thenThrow( new InsufficientVocabularyAuthorityException( ActionType.FORWARD_CV_SL_STATUS_PUBLISH ) );

        mockMvc.perform( put( "/api/editors/vocabularies/forward-status" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_PUBLISH ) ) ) )
            .andExpect( status().isForbidden() );

        verifyNoInteractions( auditPublisher );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the GET /editors/vocabularies/compare-prev/{id} endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void comparePrevShouldFailWhenVersionDoesNotExist() throws Exception {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( get( "/api/editors/vocabularies/compare-prev/{id}", 100L ) )
            .andExpect( status().is5xxServerError() );
    }

    @Test
    void comparePrevShouldFailWhenThereIsNoPreviousVersion() throws Exception {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" ) ) );

        mockMvc.perform( get( "/api/editors/vocabularies/compare-prev/{id}", 100L ) )
            .andExpect( status().is5xxServerError() )
            .andExpect( jsonPath( "$.detail" ).value( containsString( "previous version is missing" ) ) );
    }

    @Test
    void comparePrevShouldFailWhenPreviousVersionDoesNotExist() throws Exception {
        VersionDTO versionDTO = version( 101L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.1" );
        versionDTO.setPreviousVersion( 100L );
        when( versionService.findOne( 101L ) ).thenReturn( Optional.of( versionDTO ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( get( "/api/editors/vocabularies/compare-prev/{id}", 101L ) )
            .andExpect( status().is5xxServerError() );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the DELETE /editors/vocabularies/{id} endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void deleteVocabularyShouldReturnNotFoundWhenVersionDoesNotExist() throws Exception {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 100L ) )
            .andExpect( status().isNotFound() );

        verify( vocabularyService, never() ).delete( anyLong() );
    }

    @Test
    void deleteVocabularyShouldFailWhenVocabularyDoesNotReferenceVersion() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        // the vocabulary does not contain the version
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary() ) );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 100L ) )
            .andExpect( status().isInternalServerError() )
            .andExpect( jsonPath( "$.detail" ).value( containsString( "doesn't reference version" ) ) );

        verify( vocabularyService, never() ).delete( anyLong() );
        verify( vocabularyService, never() ).save( any() );
    }

    @Test
    void deleteVocabularyShouldReturnForbiddenWithoutDeleteAuthority() throws Exception {
        authenticateWithAgencyRole( AgencyRole.VIEW, SOURCE_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 100L ) )
            .andExpect( status().isForbidden() );

        verify( vocabularyService, never() ).delete( anyLong() );
        verify( vocabularyService, never() ).save( any() );
        verifyNoInteractions( auditPublisher );
    }

    @Test
    void deleteVocabularyShouldReturnForbiddenForAgencyAdminOfAnotherLanguage() throws Exception {
        // ADMIN_TL for German must not be allowed to delete the English source version
        authenticateWithAgencyRole( AgencyRole.ADMIN_TL, TARGET_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 100L ) )
            .andExpect( status().isForbidden() );

        verify( vocabularyService, never() ).delete( anyLong() );
    }

    @Test
    void deleteInitialSlVersionShouldDeleteWholeVocabulary() throws Exception {
        authenticateWithAgencyRole( AgencyRole.ADMIN_SL, SOURCE_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        VocabularyDTO vocabularyDTO = vocabulary( versionDTO );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 100L ) )
            .andExpect( status().isNoContent() );

        verify( vocabularyService ).delete( VOCABULARY_ID );
        verify( auditPublisher ).publish( eq( "editor" ), eq( vocabularyDTO ), eq( versionDTO ), isNull(), eq( "DELETE_WHOLE_VOCABULARY" ) );
    }

    @Test
    void deleteUnpublishedSlVersionShouldRevertToPreviousVersionWithoutRepublishing() throws Exception {
        VersionDTO previousSl = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO currentSl = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        currentSl.setInitialVersion( 100L );
        currentSl.setPreviousVersion( 100L );
        VocabularyDTO vocabularyDTO = vocabulary( previousSl, currentSl );
        vocabularyDTO.setVersionNumber( VersionNumber.fromString( "1.1" ) );

        when( versionService.findOne( 200L ) ).thenReturn( Optional.of( currentSl ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );
        when( vocabularyService.save( any( VocabularyDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 200L ) )
            .andExpect( status().isNoContent() );

        ArgumentCaptor<VocabularyDTO> savedCaptor = ArgumentCaptor.forClass( VocabularyDTO.class );
        verify( vocabularyService ).save( savedCaptor.capture() );
        VocabularyDTO saved = savedCaptor.getValue();
        assertThat( saved.getVersions() ).containsExactly( previousSl );
        assertThat( saved.getVersionNumber() ).isEqualTo( VersionNumber.fromString( "1.0" ) );

        verify( vocabularyService ).indexEditor( saved );
        verify( vocabularyService, never() ).delete( anyLong() );
        // the deleted version was not published, so the published content must not be touched
        verify( vocabularyService, never() ).deleteCvJsonDirectoryAndContent( any() );
        verify( vocabularyService, never() ).generateJsonVocabularyPublish( any() );
        verify( vocabularyService, never() ).indexPublished( any() );
    }

    @Test
    void deleteInitialPublishedTlVersionShouldRemoveTranslationAndRepublish() throws Exception {
        VersionDTO slVersion = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO tlVersion = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        VocabularyDTO vocabularyDTO = vocabulary( slVersion, tlVersion );
        vocabularyDTO.setTitleDefinition( "Titel", "Definition", TARGET_LANGUAGE, false );
        vocabularyDTO.setVersionByLanguage( TARGET_LANGUAGE, "1.0.1" );

        when( versionService.findOne( 101L ) ).thenReturn( Optional.of( tlVersion ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );
        when( vocabularyService.save( any( VocabularyDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 101L ) )
            .andExpect( status().isNoContent() );

        ArgumentCaptor<VocabularyDTO> savedCaptor = ArgumentCaptor.forClass( VocabularyDTO.class );
        verify( vocabularyService ).save( savedCaptor.capture() );
        VocabularyDTO saved = savedCaptor.getValue();
        assertThat( saved.getVersions() ).containsExactly( slVersion );
        assertThat( saved.getTitleDe() ).isNull();
        assertThat( saved.getVersionDe() ).isNull();

        verify( vocabularyService ).indexEditor( saved );
        verify( vocabularyService ).deleteCvJsonDirectoryAndContent( applicationProperties.getVocabJsonPath().resolve( NOTATION ) );
        verify( vocabularyService ).generateJsonVocabularyPublish( saved );
        verify( vocabularyService ).indexPublished( saved );
        verify( vocabularyService, never() ).delete( anyLong() );
        verify( auditPublisher ).publish( eq( "editor" ), eq( vocabularyDTO ), eq( tlVersion ), isNull(), eq( "DELETE_TL_VOCABULARY" ) );
    }

    @Test
    void deletePublishedTlVersionShouldRestorePreviousTranslation() throws Exception {
        VersionDTO slVersion = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO previousTl = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        previousTl.setTitle( "Alter Titel" );
        VersionDTO currentTl = version( 102L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.2" );
        currentTl.setInitialVersion( 101L );
        currentTl.setPreviousVersion( 101L );
        VocabularyDTO vocabularyDTO = vocabulary( slVersion, previousTl, currentTl );

        when( versionService.findOne( 102L ) ).thenReturn( Optional.of( currentTl ) );
        when( versionService.findOne( 101L ) ).thenReturn( Optional.of( previousTl ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );
        when( vocabularyService.save( any( VocabularyDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 102L ) )
            .andExpect( status().isNoContent() );

        ArgumentCaptor<VocabularyDTO> savedCaptor = ArgumentCaptor.forClass( VocabularyDTO.class );
        verify( vocabularyService ).save( savedCaptor.capture() );
        VocabularyDTO saved = savedCaptor.getValue();
        assertThat( saved.getVersions() ).containsExactlyInAnyOrder( slVersion, previousTl );
        assertThat( saved.getTitleDe() ).isEqualTo( "Alter Titel" );
        assertThat( saved.getVersionDe() ).isEqualTo( "1.0.1" );
        verify( vocabularyService ).generateJsonVocabularyPublish( saved );
    }

    @Test
    void deleteUnpublishedTlVersionShouldNotRepublish() throws Exception {
        VersionDTO slVersion = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO tlVersion = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.DRAFT, "1.0.1" );
        VocabularyDTO vocabularyDTO = vocabulary( slVersion, tlVersion );

        when( versionService.findOne( 101L ) ).thenReturn( Optional.of( tlVersion ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );
        when( vocabularyService.save( any( VocabularyDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        mockMvc.perform( delete( "/api/editors/vocabularies/{id}", 101L ) )
            .andExpect( status().isNoContent() );

        verify( vocabularyService ).save( vocabularyDTO );
        assertThat( vocabularyDTO.getVersions() ).containsExactly( slVersion );
        verify( vocabularyService, never() ).generateJsonVocabularyPublish( any() );
        verify( vocabularyService, never() ).indexPublished( any() );
    }

    // ------------------------------------------------------------------------------------------
    // POST/PUT /editors/codes
    // ------------------------------------------------------------------------------------------

    @Test
    void createCodeShouldRejectWrongActionType() throws Exception {
        mockMvc.perform( post( "/api/editors/codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( codeSnippet( ActionType.EDIT_CODE, 100L ) ) ) )
            .andExpect( status().isBadRequest() );

        verify( vocabularyService, never() ).saveCode( any() );
    }

    @Test
    void createCodeShouldRejectSnippetWithConceptId() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setConceptId( 1000L );

        mockMvc.perform( post( "/api/editors/codes" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isBadRequest() );

        verify( vocabularyService, never() ).saveCode( any() );
    }

    @Test
    void createCodeShouldReturnForbiddenWhenServiceRejectsAuthority() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "A" );
        when( vocabularyService.saveCode( any( CodeSnippet.class ) ) )
            .thenThrow( new InsufficientVocabularyAuthorityException( ActionType.CREATE_CODE ) );

        mockMvc.perform( post( "/api/editors/codes" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isForbidden() );

        verifyNoInteractions( auditPublisher );
    }

    @Test
    void updateCodeShouldRejectWrongActionType() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setConceptId( 1000L );

        mockMvc.perform( put( "/api/editors/codes" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isBadRequest() );

        verify( vocabularyService, never() ).saveCode( any() );
    }

    @Test
    void updateCodeShouldRejectSnippetWithoutConceptId() throws Exception {
        mockMvc.perform( put( "/api/editors/codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( codeSnippet( ActionType.EDIT_CODE, 100L ) ) ) )
            .andExpect( status().isBadRequest() );

        verify( vocabularyService, never() ).saveCode( any() );
    }

    @Test
    void updateCodeShouldAuditWithEmptyUserWhenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();
        CodeSnippet snippet = codeSnippet( ActionType.EDIT_CODE, 100L );
        snippet.setConceptId( 1000L );
        ConceptDTO updated = concept( 1000L, 100L, "A", null, 0 );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( vocabularyService.saveCode( any( CodeSnippet.class ) ) ).thenReturn( updated );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );

        mockMvc.perform( put( "/api/editors/codes" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.notation" ).value( "A" ) );

        verify( auditPublisher ).publish( eq( "" ), isNull(), eq( versionDTO ), eq( updated ), isNull(), any( CodeSnippet.class ),
            eq( "EDIT_CODE" ) );
    }

    @Test
    void updateCodeShouldFailWhenVersionDisappearsBeforeAuditing() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.EDIT_TL_CODE, 100L );
        snippet.setConceptId( 1000L );
        when( vocabularyService.saveCode( any( CodeSnippet.class ) ) ).thenReturn( concept( 1000L, 100L, "A", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( put( "/api/editors/codes" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isInternalServerError() );

        verifyNoInteractions( auditPublisher );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the POST /editors/codes/batch endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void createBatchCodeShouldRejectEmptyArray() throws Exception {
        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON ).content( "[]" ) )
            .andExpect( status().isInternalServerError() )
            .andExpect( jsonPath( "$.detail" ).value( containsString( "can not be empty" ) ) );

        verifyNoInteractions( versionService );
    }

    @Test
    void createBatchCodeShouldFailWhenVersionDoesNotExist() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "A" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( new CodeSnippet[]{ snippet } ) ) )
            .andExpect( status().isInternalServerError() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void createBatchCodeShouldReturnForbiddenAndSaveNothingWhenAnySnippetIsUnauthorized() throws Exception {
        authenticateWithAgencyRole( AgencyRole.VIEW, SOURCE_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        CodeSnippet first = codeSnippet( ActionType.CREATE_CODE, 100L );
        first.setNotation( "A" );
        CodeSnippet second = codeSnippet( ActionType.CREATE_CODE, 100L );
        second.setNotation( "B" );

        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( new CodeSnippet[]{ first, second } ) ) )
            .andExpect( status().isForbidden() );

        verify( versionService, never() ).save( any() );
        verify( vocabularyService, never() ).indexEditor( any() );
        verifyNoInteractions( auditPublisher );
        assertThat( versionDTO.getConcepts() ).isEmpty();
    }

    @Test
    void createBatchCodeShouldRejectPublishedVersion() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "A" );

        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( new CodeSnippet[]{ snippet } ) ) )
            .andExpect( status().isInternalServerError() )
            .andExpect( jsonPath( "$.detail" ).value( containsString( "Version is already PUBLISHED" ) ) );

        verify( versionService, never() ).save( any() );
        assertThat( versionDTO.getConcepts() ).isEmpty();
    }

    @Test
    void createBatchCodeShouldRejectDuplicateNotation() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "A" );

        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( new CodeSnippet[]{ snippet } ) ) )
            .andExpect( status().isBadRequest() );

        verify( versionService, never() ).save( any() );
        assertThat( versionDTO.getConcepts() ).hasSize( 1 );
    }

    @Test
    void createBatchCodeShouldAddCodesAndAbbreviateTheAlertParameter() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "Existing", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( versionService.save( any( VersionDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );
        VocabularyDTO vocabularyDTO = vocabulary( versionDTO );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        CodeSnippet first = codeSnippet( ActionType.CREATE_CODE, 100L );
        first.setNotation( "FirstLongNotation" );
        first.setTitle( "First" );
        first.setPosition( 1 );
        // no position given, the position is derived from the number of codes in the version
        CodeSnippet second = codeSnippet( ActionType.CREATE_CODE, 100L );
        second.setNotation( "SecondLongNotation" );
        second.setTitle( "Second" );

        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( new CodeSnippet[]{ first, second } ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$", hasSize( 2 ) ) )
            .andExpect( header().string( "import-status", "Successfully importing 2 codes" ) )
            // the alert parameter is URL encoded by HeaderUtil
            .andExpect( header().string( "X-" + APP_NAME + "-params", "FirstLongNotationSec...+%282+new+codes+added%29" ) );

        assertThat( versionDTO.getConcepts() ).extracting( ConceptDTO::getNotation )
            .contains( "Existing", "FirstLongNotation", "SecondLongNotation" );
        verify( versionService, times( 2 ) ).save( versionDTO );
        verify( vocabularyService, times( 2 ) ).storeChangeType( any( CodeSnippet.class ), eq( versionDTO ) );
        verify( vocabularyService, times( 2 ) ).indexEditor( vocabularyDTO );
    }

    @Test
    void createBatchCodeShouldIgnoreUnsupportedActionTypes() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        CodeSnippet snippet = codeSnippet( ActionType.EDIT_CODE, 100L );
        snippet.setConceptId( 1000L );
        snippet.setNotation( "A" );
        snippet.setTitle( "Changed" );

        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( new CodeSnippet[]{ snippet } ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$", hasSize( 0 ) ) )
            .andExpect( header().string( "import-status", "Successfully importing 0 codes" ) );

        assertThat( versionDTO.findConceptByNotation( "A" ).getTitle() ).isEqualTo( "Title A" );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void createBatchCodeShouldTranslateExistingCodes() throws Exception {
        VersionDTO tlVersion = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.DRAFT, "1.0.1" );
        tlVersion.addConcept( concept( 1000L, 101L, "A", null, 0 ) );
        when( versionService.findOne( 101L ) ).thenReturn( Optional.of( tlVersion ) );
        when( versionService.save( any( VersionDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );
        VocabularyDTO vocabularyDTO = vocabulary( tlVersion );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        CodeSnippet translated = codeSnippet( ActionType.ADD_TL_CODE, 101L );
        translated.setConceptId( 1000L );
        translated.setNotation( "A" );
        translated.setTitle( "Titel A" );
        translated.setDefinition( "Definition A" );
        // a translation for a concept that does not exist in the version is ignored
        CodeSnippet unknown = codeSnippet( ActionType.ADD_TL_CODE, 101L );
        unknown.setConceptId( 9999L );
        unknown.setNotation( "Z" );

        mockMvc.perform( post( "/api/editors/codes/batch" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( new CodeSnippet[]{ translated, unknown } ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$", hasSize( 1 ) ) )
            .andExpect( jsonPath( "$[0].title" ).value( "Titel A" ) )
            .andExpect( header().string( "import-status", "Successfully importing 1 codes" ) );

        ConceptDTO stored = tlVersion.findConceptByNotation( "A" );
        assertThat( stored.getTitle() ).isEqualTo( "Titel A" );
        assertThat( stored.getDefinition() ).isEqualTo( "Definition A" );
        verify( versionService, times( 1 ) ).save( tlVersion );
        verify( auditPublisher ).publish( eq( "editor" ), eq( vocabularyDTO ), eq( tlVersion ), eq( stored ), isNull(),
            any( CodeSnippet.class ), eq( "ADD_TL_CODE" ) );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the POST /editors/codes/deprecate endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void deprecateCodeShouldRejectWrongActionType() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.DELETE_CODE, 100L );
        snippet.setConceptId( 1000L );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( conceptService );
    }

    @Test
    void deprecateCodeShouldRejectSnippetWithoutConceptId() throws Exception {
        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( codeSnippet( ActionType.DEPRECATE_CODE, 100L ) ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( conceptService );
    }

    @Test
    void deprecateCodeShouldReturnNotFoundWhenConceptDoesNotExist() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.DEPRECATE_CODE, 100L );
        snippet.setConceptId( 1000L );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isNotFound() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void deprecateCodeShouldFailWhenConceptReferencesMissingVersion() throws Exception {
        CodeSnippet snippet = codeSnippet( ActionType.DEPRECATE_CODE, 100L );
        snippet.setConceptId( 1000L );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 100L, "A", null, 0 ) ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isInternalServerError() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void deprecateCodeShouldReturnForbiddenWithoutAuthority() throws Exception {
        authenticateWithAgencyRole( AgencyRole.ADMIN_TL, SOURCE_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        ConceptDTO conceptA = concept( 1000L, 100L, "A", null, 0 );
        versionDTO.addConcept( conceptA );
        CodeSnippet snippet = codeSnippet( ActionType.DEPRECATE_CODE, 100L );
        snippet.setConceptId( 1000L );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 100L, "A", null, 0 ) ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isForbidden() );

        assertThat( conceptA.getDeprecated() ).isFalse();
        verify( versionService, never() ).save( any() );
        verifyNoInteractions( vocabularyChangeService, auditPublisher );
    }

    @Test
    void deprecateCodeShouldReturnNotFoundWhenReplacingConceptDoesNotExist() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        CodeSnippet snippet = codeSnippet( ActionType.DEPRECATE_CODE, 100L );
        snippet.setConceptId( 1000L );
        snippet.setReplacedById( 2000L );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 100L, "A", null, 0 ) ) );
        when( conceptService.findOne( 2000L ) ).thenReturn( Optional.empty() );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isNotFound() );

        verify( versionService, never() ).save( any() );
        verifyNoInteractions( vocabularyChangeService );
    }

    @Test
    void deprecateCodeShouldDeprecateCodeAndChildrenAndRecordChangesInNonInitialVersion() throws Exception {
        VersionDTO versionDTO = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        versionDTO.setInitialVersion( 100L );
        ConceptDTO conceptA = concept( 1000L, 200L, "A", null, 0 );
        ConceptDTO childA1 = concept( 1001L, 200L, "A.1", "A", 1 );
        ConceptDTO conceptB = concept( 1002L, 200L, "B", null, 2 );
        versionDTO.addConcept( conceptA );
        versionDTO.addConcept( childA1 );
        versionDTO.addConcept( conceptB );
        VocabularyDTO vocabularyDTO = vocabulary( versionDTO );

        CodeSnippet snippet = codeSnippet( ActionType.DEPRECATE_CODE, 200L );
        snippet.setConceptId( 1000L );
        snippet.setReplacedById( 1002L );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 200L, "A", null, 0 ) ) );
        when( conceptService.findOne( 1002L ) ).thenReturn( Optional.of( concept( 1002L, 200L, "B", null, 2 ) ) );
        when( versionService.findOne( 200L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isNoContent() )
            .andExpect( header().string( "X-" + APP_NAME + "-alert", "alert.code.deprecated" ) );

        assertThat( conceptA.getDeprecated() ).isTrue();
        assertThat( conceptA.getReplacedById() ).isEqualTo( 1002L );
        assertThat( childA1.getDeprecated() ).isTrue();
        assertThat( conceptB.getDeprecated() ).isFalse();

        ArgumentCaptor<VocabularyChangeDTO> changeCaptor = ArgumentCaptor.forClass( VocabularyChangeDTO.class );
        verify( vocabularyChangeService, times( 2 ) ).save( changeCaptor.capture() );
        assertThat( changeCaptor.getAllValues() ).extracting( VocabularyChangeDTO::getChangeType ).containsOnly( "Code deprecated" );
        assertThat( changeCaptor.getAllValues() ).extracting( VocabularyChangeDTO::getDescription )
            .containsExactlyInAnyOrder( "A, replaced by B", "A.1" );

        verify( versionService ).save( versionDTO );
        verify( vocabularyService ).indexEditor( vocabularyDTO );
    }

    @Test
    void deprecateCodeShouldIgnoreNegativeReplacingConceptId() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        ConceptDTO conceptA = concept( 1000L, 100L, "A", null, 0 );
        versionDTO.addConcept( conceptA );
        CodeSnippet snippet = codeSnippet( ActionType.DEPRECATE_CODE, 100L );
        snippet.setConceptId( 1000L );
        snippet.setReplacedById( -1L );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 100L, "A", null, 0 ) ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isNoContent() );

        assertThat( conceptA.getDeprecated() ).isTrue();
        verify( conceptService, never() ).findOne( -1L );
        verify( versionService ).save( versionDTO );
    }

    @Test
    void deprecateCodeInInitialVersionShouldNotRecordChanges() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        ConceptDTO conceptA = concept( 1000L, 100L, "A", null, 0 );
        versionDTO.addConcept( conceptA );
        CodeSnippet snippet = codeSnippet( ActionType.DEPRECATE_CODE, 100L );
        snippet.setConceptId( 1000L );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 100L, "A", null, 0 ) ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( post( "/api/editors/codes/deprecate" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isNoContent() );

        assertThat( conceptA.getDeprecated() ).isTrue();
        assertThat( conceptA.getReplacedById() ).isNull();
        verifyNoInteractions( vocabularyChangeService );
        verify( versionService ).save( versionDTO );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the DELETE /editors/codes/{id} endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void deleteCodeShouldReturnNotFoundWhenConceptDoesNotExist() throws Exception {
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( delete( "/api/editors/codes/{id}", 1000L ) )
            .andExpect( status().isNotFound() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void deleteCodeShouldReturnForbiddenWithoutAuthority() throws Exception {
        authenticateWithAgencyRole( AgencyRole.VIEW, SOURCE_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 100L, "A", null, 0 ) ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( delete( "/api/editors/codes/{id}", 1000L ) )
            .andExpect( status().isForbidden() );

        assertThat( versionDTO.getConcepts() ).hasSize( 1 );
        verify( versionService, never() ).save( any() );
        verifyNoInteractions( vocabularyChangeService, auditPublisher );
    }

    @Test
    void deleteCodeShouldRemoveChildrenAndRecordChangesInNonInitialVersion() throws Exception {
        VersionDTO versionDTO = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        versionDTO.setInitialVersion( 100L );
        versionDTO.addConcept( concept( 1000L, 200L, "A", null, 0 ) );
        versionDTO.addConcept( concept( 1001L, 200L, "A.1", "A", 1 ) );
        versionDTO.addConcept( concept( 1002L, 200L, "B", null, 2 ) );
        VocabularyDTO vocabularyDTO = vocabulary( versionDTO );
        when( conceptService.findOne( 1000L ) ).thenReturn( Optional.of( concept( 1000L, 200L, "A", null, 0 ) ) );
        when( versionService.findOne( 200L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabularyDTO ) );

        mockMvc.perform( delete( "/api/editors/codes/{id}", 1000L ) )
            .andExpect( status().isNoContent() );

        assertThat( versionDTO.getConcepts() ).extracting( ConceptDTO::getNotation ).containsExactly( "B" );
        ArgumentCaptor<VocabularyChangeDTO> changeCaptor = ArgumentCaptor.forClass( VocabularyChangeDTO.class );
        verify( vocabularyChangeService, times( 2 ) ).save( changeCaptor.capture() );
        assertThat( changeCaptor.getAllValues() ).extracting( VocabularyChangeDTO::getChangeType ).containsOnly( "Code deleted" );
        assertThat( changeCaptor.getAllValues() ).extracting( VocabularyChangeDTO::getDescription ).containsExactlyInAnyOrder( "A", "A.1" );
        verify( versionService ).save( versionDTO );
        verify( vocabularyService ).indexEditor( vocabularyDTO );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the POST /editors/codes/reorder endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void reorderCodeShouldRejectWrongActionType() throws Exception {
        mockMvc.perform( post( "/api/editors/codes/reorder" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( codeSnippet( ActionType.EDIT_CODE, 100L ) ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( versionService );
    }

    @Test
    void reorderCodeShouldReturnNotFoundWhenVersionDoesNotExist() throws Exception {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( post( "/api/editors/codes/reorder" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( codeSnippet( ActionType.REORDER_CODE, 100L ) ) ) )
            .andExpect( status().isNotFound() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void reorderCodeShouldFailWhenVersionReferencesMissingVocabulary() throws Exception {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" ) ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.empty() );

        mockMvc.perform( post( "/api/editors/codes/reorder" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( codeSnippet( ActionType.REORDER_CODE, 100L ) ) ) )
            .andExpect( status().isInternalServerError() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void reorderCodeShouldReturnForbiddenWithoutAuthority() throws Exception {
        authenticateWithAgencyRole( AgencyRole.ADMIN_TL, SOURCE_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        mockMvc.perform( post( "/api/editors/codes/reorder" ).contentType( MediaType.APPLICATION_JSON )
                .content( json( codeSnippet( ActionType.REORDER_CODE, 100L ) ) ) )
            .andExpect( status().isForbidden() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void reorderCodeShouldReturnNotFoundForUnknownConceptAndSaveNothing() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( vocabularyService.findOne( VOCABULARY_ID ) ).thenReturn( Optional.of( vocabulary( versionDTO ) ) );

        CodeSnippet snippet = codeSnippet( ActionType.REORDER_CODE, 100L );
        snippet.setConceptStructureIds( Arrays.asList( 1000L, 9999L ) );
        snippet.setConceptStructures( Arrays.asList( "A", "A.B" ) );

        mockMvc.perform( post( "/api/editors/codes/reorder" ).contentType( MediaType.APPLICATION_JSON ).content( json( snippet ) ) )
            .andExpect( status().isNotFound() );

        verify( versionService, never() ).save( any() );
        verify( vocabularyService, never() ).indexEditor( any() );
    }

    // ------------------------------------------------------------------------------------------
    // Tests of the GET /editors/download/{cv}/{v} endpoint
    // ------------------------------------------------------------------------------------------

    @Test
    void downloadShouldReturnGeneratedFileAsAttachment( @TempDir Path tempDir ) throws Exception {
        Path file = Files.write( tempDir.resolve( NOTATION + "-1.0_en-1.0.pdf" ), new byte[]{ 37, 80, 68, 70 } );
        when( vocabularyService.generateVocabularyFileDownload( eq( NOTATION ), eq( "1.0" ), eq( "en-1.0" ),
            eq( ExportService.DownloadType.PDF ), anyString(), eq( false ) ) ).thenReturn( file );

        mockMvc.perform( get( "/api/editors/download/{cv}/{v}", NOTATION, "1.0" ).param( "lv", "en-1.0" )
                .accept( MediaType.APPLICATION_PDF ) )
            .andExpect( status().isOk() )
            .andExpect( header().string( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + NOTATION + "-1.0_en-1.0.pdf" ) )
            .andExpect( content().contentType( MediaType.APPLICATION_PDF ) )
            .andExpect( content().bytes( new byte[]{ 37, 80, 68, 70 } ) );
    }

    // ------------------------------------------------------------------------------------------
    // /editors/comments
    // ------------------------------------------------------------------------------------------

    @Test
    void createCommentShouldRejectCommentWithId() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId( 1L );
        commentDTO.setVersionId( 100L );

        mockMvc.perform( post( "/api/editors/comments" ).contentType( MediaType.APPLICATION_JSON ).content( json( commentDTO ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( versionService );
    }

    @Test
    void createCommentShouldRejectCommentWithoutVersion() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent( "comment" );

        mockMvc.perform( post( "/api/editors/comments" ).contentType( MediaType.APPLICATION_JSON ).content( json( commentDTO ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( versionService );
    }

    @Test
    void createCommentShouldReturnNotFoundForUnknownVersion() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setVersionId( 100L );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( post( "/api/editors/comments" ).contentType( MediaType.APPLICATION_JSON ).content( json( commentDTO ) ) )
            .andExpect( status().isNotFound() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void createCommentShouldAssignCurrentUserAndReturnStoredComment() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        // simulate persistence assigning an ID to the new comment
        when( versionService.save( any( VersionDTO.class ) ) ).thenAnswer( invocation -> {
            VersionDTO saved = invocation.getArgument( 0 );
            saved.getComments().forEach( c -> c.setId( 500L ) );
            return saved;
        } );

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setVersionId( 100L );
        commentDTO.setContent( "A comment" );

        mockMvc.perform( post( "/api/editors/comments" ).contentType( MediaType.APPLICATION_JSON ).content( json( commentDTO ) ) )
            .andExpect( status().isCreated() )
            .andExpect( header().string( "Location", "/api/comment/500" ) )
            .andExpect( jsonPath( "$.id" ).value( 500 ) )
            .andExpect( jsonPath( "$.userId" ).value( 42 ) );
    }

    @Test
    void updateCommentShouldRejectCommentWithoutId() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setVersionId( 100L );

        mockMvc.perform( put( "/api/editors/comments" ).contentType( MediaType.APPLICATION_JSON ).content( json( commentDTO ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( versionService );
    }

    @Test
    void updateCommentShouldReturnNotFoundForUnknownVersion() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId( 500L );
        commentDTO.setVersionId( 100L );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( put( "/api/editors/comments" ).contentType( MediaType.APPLICATION_JSON ).content( json( commentDTO ) ) )
            .andExpect( status().isNotFound() );

        verify( versionService, never() ).save( any() );
    }

    @Test
    void updateCommentShouldReturnNotFoundWhenCommentIsNotInVersion() throws Exception {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        CommentDTO existing = new CommentDTO();
        existing.setId( 501L );
        existing.setContent( "Original" );
        versionDTO.addComment( existing );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId( 500L );
        commentDTO.setVersionId( 100L );
        commentDTO.setContent( "Changed" );

        mockMvc.perform( put( "/api/editors/comments" ).contentType( MediaType.APPLICATION_JSON ).content( json( commentDTO ) ) )
            .andExpect( status().isNotFound() );

        assertThat( existing.getContent() ).isEqualTo( "Original" );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void deleteCommentShouldReturnNotFoundForUnknownComment() throws Exception {
        when( commentService.findOne( 500L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( delete( "/api/editors/comments/{id}", 500L ) )
            .andExpect( status().isNotFound() );

        verifyNoInteractions( versionService );
    }

    @Test
    void deleteCommentShouldFailWhenCommentReferencesMissingVersion() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId( 500L );
        commentDTO.setVersionId( 100L );
        when( commentService.findOne( 500L ) ).thenReturn( Optional.of( commentDTO ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( delete( "/api/editors/comments/{id}", 500L ) )
            .andExpect( status().isInternalServerError() );

        verify( versionService, never() ).save( any() );
    }

    // ------------------------------------------------------------------------------------------
    // /editors/metadatas
    // ------------------------------------------------------------------------------------------

    @Test
    void createAppMetadataShouldRejectValueWithId() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setId( 1L );
        metadataValueDTO.setMetadataKey( "key" );

        mockMvc.perform( post( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( metadataFieldService );
    }

    @Test
    void createAppMetadataShouldRejectValueWithoutKey() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setValue( "value" );

        mockMvc.perform( post( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isInternalServerError() )
            .andExpect( jsonPath( "$.detail" ).value( containsString( "requires a metadata key" ) ) );

        verifyNoInteractions( metadataFieldService );
    }

    @Test
    void createAppMetadataShouldFailWhenSavedFieldHasNoValues() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setMetadataKey( "key" );
        metadataValueDTO.setValue( "value" );

        MetadataFieldDTO existingField = new MetadataFieldDTO();
        existingField.setId( 7L );
        existingField.setMetadataKey( "key" );
        when( metadataFieldService.findByMetadataKey( "key" ) ).thenReturn( Optional.of( existingField ) );
        MetadataFieldDTO savedField = new MetadataFieldDTO();
        savedField.setId( 7L );
        savedField.setMetadataValues( new LinkedHashSet<>() );
        when( metadataFieldService.save( any( MetadataFieldDTO.class ) ) ).thenReturn( savedField );

        mockMvc.perform( post( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isInternalServerError() );

        verifyNoInteractions( auditPublisher );
    }

    @Test
    void createAppMetadataShouldCreateFieldAndReturnMatchingValue() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setMetadataKey( "newKey" );
        metadataValueDTO.setValue( "second" );

        when( metadataFieldService.findByMetadataKey( "newKey" ) ).thenReturn( Optional.empty() );

        MetadataValueDTO firstStored = new MetadataValueDTO();
        firstStored.setId( 1L );
        firstStored.setValue( "first" );
        MetadataValueDTO secondStored = new MetadataValueDTO();
        secondStored.setId( 2L );
        secondStored.setValue( "second" );
        MetadataFieldDTO savedField = new MetadataFieldDTO();
        savedField.setId( 7L );
        savedField.setMetadataKey( "newKey" );
        savedField.setMetadataValues( new LinkedHashSet<>( Arrays.asList( firstStored, secondStored ) ) );

        // the first save creates the field, the second one stores the value
        MetadataFieldDTO createdField = new MetadataFieldDTO();
        createdField.setId( 7L );
        createdField.setMetadataKey( "newKey" );
        when( metadataFieldService.save( any( MetadataFieldDTO.class ) ) ).thenReturn( createdField, savedField );

        mockMvc.perform( post( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isCreated() )
            .andExpect( header().string( "Location", "/api/metadata-values/2" ) )
            .andExpect( jsonPath( "$.value" ).value( "second" ) );

        verify( metadataFieldService, times( 2 ) ).save( any( MetadataFieldDTO.class ) );
    }

    @Test
    void updateAppMetadataShouldRejectValueWithoutId() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setMetadataFieldId( 7L );

        mockMvc.perform( put( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( metadataFieldService );
    }

    @Test
    void updateAppMetadataShouldRejectValueWithoutFieldId() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setId( 1L );

        mockMvc.perform( put( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isBadRequest() );

        verifyNoInteractions( metadataFieldService );
    }

    @Test
    void updateAppMetadataShouldReturnNotFoundForUnknownField() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setId( 1L );
        metadataValueDTO.setMetadataFieldId( 7L );
        when( metadataFieldService.findOne( 7L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( put( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isNotFound() );

        verify( metadataFieldService, never() ).save( any() );
    }

    @Test
    void updateAppMetadataShouldReturnNotFoundWhenValueIsNotInField() throws Exception {
        MetadataValueDTO stored = new MetadataValueDTO();
        stored.setId( 2L );
        stored.setValue( "stored" );
        MetadataFieldDTO field = new MetadataFieldDTO();
        field.setId( 7L );
        field.setMetadataValues( new LinkedHashSet<>( Collections.singleton( stored ) ) );
        when( metadataFieldService.findOne( 7L ) ).thenReturn( Optional.of( field ) );

        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setId( 1L );
        metadataValueDTO.setMetadataFieldId( 7L );
        metadataValueDTO.setValue( "changed" );

        mockMvc.perform( put( "/api/editors/metadatas" ).contentType( MediaType.APPLICATION_JSON ).content( json( metadataValueDTO ) ) )
            .andExpect( status().isNotFound() );

        assertThat( stored.getValue() ).isEqualTo( "stored" );
        verify( metadataFieldService, never() ).save( any() );
    }

    @Test
    void deleteAppMetadataShouldReturnNotFoundForUnknownValue() throws Exception {
        when( metadataValueService.findOne( 1L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( delete( "/api/editors/metadatas/{id}", 1L ) )
            .andExpect( status().isNotFound() );

        verifyNoInteractions( metadataFieldService );
    }

    @Test
    void deleteAppMetadataShouldReturnNotFoundForUnknownField() throws Exception {
        MetadataValueDTO metadataValueDTO = new MetadataValueDTO();
        metadataValueDTO.setId( 1L );
        metadataValueDTO.setMetadataFieldId( 7L );
        when( metadataValueService.findOne( 1L ) ).thenReturn( Optional.of( metadataValueDTO ) );
        when( metadataFieldService.findOne( 7L ) ).thenReturn( Optional.empty() );

        mockMvc.perform( delete( "/api/editors/metadatas/{id}", 1L ) )
            .andExpect( status().isNotFound() );

        verify( metadataFieldService, never() ).save( any() );
    }
}
