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
package eu.cessda.cvs.service.impl;

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.domain.search.AgencyStat;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.LicenceRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.repository.search.AgencyStatSearchRepository;
import eu.cessda.cvs.repository.search.VocabularyEditorSearchRepository;
import eu.cessda.cvs.repository.search.VocabularyPublishSearchRepository;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.UserDetails;
import eu.cessda.cvs.service.*;
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.service.mapper.VocabularyEditorMapper;
import eu.cessda.cvs.service.mapper.VocabularyMapper;
import eu.cessda.cvs.service.mapper.VocabularyPublishMapper;
import eu.cessda.cvs.utils.VersionNumber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the workflow, permission checks and error paths of {@link VocabularyServiceImpl}.
 * All collaborators are mocked.
 */
class VocabularyServiceImplWorkflowTest
{
    private static final Long AGENCY_ID = 1L;
    private static final Long VOCABULARY_ID = 10L;
    private static final String NOTATION = "TestCV";
    private static final String SOURCE_LANGUAGE = "en";
    private static final String TARGET_LANGUAGE = "de";

    @TempDir
    Path tempDir;

    private AgencyRepository agencyRepository;
    private ConceptService conceptService;
    private ExportService exportService;
    private LicenceRepository licenceRepository;
    private VersionService versionService;
    private VocabularyChangeService vocabularyChangeService;
    private VocabularyRepository vocabularyRepository;
    private VocabularyMapper vocabularyMapper;
    private VocabularyEditorMapper vocabularyEditorMapper;
    private VocabularyPublishMapper vocabularyPublishMapper;
    private VocabularyEditorSearchRepository vocabularyEditorSearchRepository;
    private VocabularyPublishSearchRepository vocabularyPublishSearchRepository;
    private AgencyStatSearchRepository agencyStatSearchRepository;
    private ApplicationProperties applicationProperties;

    private VocabularyServiceImpl vocabularyService;

    @BeforeEach
    void setUp()
    {
        agencyRepository = mock( AgencyRepository.class );
        conceptService = mock( ConceptService.class );
        exportService = mock( ExportService.class );
        licenceRepository = mock( LicenceRepository.class );
        versionService = mock( VersionService.class );
        vocabularyChangeService = mock( VocabularyChangeService.class );
        vocabularyRepository = mock( VocabularyRepository.class );
        vocabularyMapper = mock( VocabularyMapper.class );
        vocabularyEditorMapper = mock( VocabularyEditorMapper.class );
        vocabularyPublishMapper = mock( VocabularyPublishMapper.class );
        vocabularyEditorSearchRepository = mock( VocabularyEditorSearchRepository.class );
        vocabularyPublishSearchRepository = mock( VocabularyPublishSearchRepository.class );
        agencyStatSearchRepository = mock( AgencyStatSearchRepository.class );
        applicationProperties = new ApplicationProperties( tempDir );

        vocabularyService = new VocabularyServiceImpl(
            agencyRepository,
            conceptService,
            exportService,
            licenceRepository,
            versionService,
            vocabularyChangeService,
            vocabularyRepository,
            vocabularyMapper,
            vocabularyEditorMapper,
            vocabularyPublishMapper,
            vocabularyEditorSearchRepository,
            vocabularyPublishSearchRepository,
            mock( ElasticsearchOperations.class ),
            applicationProperties,
            agencyStatSearchRepository
        );

        authenticateAsContentAdmin();
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------------------------

    private static void authenticateAsContentAdmin()
    {
        authenticate( Collections.singleton( "ROLE_ADMIN_CONTENT" ), Collections.emptySet() );
    }

    private static void authenticateWithAgencyRole( AgencyRole agencyRole, String language )
    {
        UserAgencyDTO userAgency = new UserAgencyDTO();
        userAgency.setAgencyId( AGENCY_ID );
        userAgency.setAgencyRole( agencyRole );
        userAgency.setLanguage( language );
        authenticate( Collections.singleton( "ROLE_USER" ), Collections.singleton( userAgency ) );
    }

    private static void authenticate( Set<String> authorities, Set<UserAgencyDTO> userAgencies )
    {
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

    private static VersionDTO version( Long id, ItemType itemType, String language, Status status, String number )
    {
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

    private static VocabularyDTO vocabulary( String versionNumber, VersionDTO... versions )
    {
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        vocabularyDTO.setId( VOCABULARY_ID );
        vocabularyDTO.setAgencyId( AGENCY_ID );
        vocabularyDTO.setNotation( NOTATION );
        vocabularyDTO.setSourceLanguage( SOURCE_LANGUAGE );
        vocabularyDTO.setVersionNumber( VersionNumber.fromString( versionNumber ) );
        for ( VersionDTO versionDTO : versions )
        {
            vocabularyDTO.addVersion( versionDTO );
        }
        return vocabularyDTO;
    }

    private static ConceptDTO concept( Long id, Long versionId, String notation, String parent, int position )
    {
        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setId( id );
        conceptDTO.setVersionId( versionId );
        conceptDTO.setNotation( notation );
        conceptDTO.setParent( parent );
        conceptDTO.setPosition( position );
        conceptDTO.setTitle( "Title " + notation );
        return conceptDTO;
    }

    private static CodeSnippet codeSnippet( ActionType actionType, Long versionId )
    {
        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setActionType( actionType );
        codeSnippet.setVersionId( versionId );
        return codeSnippet;
    }

    private static VocabularySnippet vocabularySnippet( ActionType actionType, Long versionId )
    {
        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setActionType( actionType );
        vocabularySnippet.setAgencyId( AGENCY_ID );
        vocabularySnippet.setVocabularyId( VOCABULARY_ID );
        vocabularySnippet.setVersionId( versionId );
        vocabularySnippet.setLanguage( SOURCE_LANGUAGE );
        vocabularySnippet.setItemType( ItemType.SL );
        vocabularySnippet.setNotation( NOTATION );
        return vocabularySnippet;
    }

    private static Agency agency( String name )
    {
        return new Agency()
            .name( name )
            .link( "https://agency.example" )
            .uri( "https://vocabularies.example/[VOCABULARY]?v=[VERSION]" )
            .uriCode( "https://vocabularies.example/[VOCABULARY]_[CODE]?v=[VERSION]" )
            .canonicalUri( "urn:example:cv:" );
    }

    /**
     * Make {@link VocabularyServiceImpl#findOne(Long)} return the given vocabulary and make
     * {@link VocabularyServiceImpl#save(VocabularyDTO)} return it again after saving.
     */
    private Vocabulary givenStoredVocabulary( VocabularyDTO vocabularyDTO )
    {
        Vocabulary entity = new Vocabulary();
        entity.setId( vocabularyDTO.getId() );
        when( vocabularyRepository.findById( vocabularyDTO.getId() ) ).thenReturn( Optional.of( entity ) );
        when( vocabularyMapper.toDto( entity ) ).thenReturn( vocabularyDTO );
        when( vocabularyMapper.toEntity( any( VocabularyDTO.class ) ) ).thenReturn( entity );
        when( vocabularyRepository.save( entity ) ).thenReturn( entity );
        return entity;
    }

    /**
     * Make {@link VersionService#save(VersionDTO)} assign IDs to versions and return them.
     */
    private void givenVersionSaveAssignsIds( long firstId )
    {
        AtomicLong nextId = new AtomicLong( firstId );
        when( versionService.save( any( VersionDTO.class ) ) ).thenAnswer( invocation -> {
            VersionDTO saved = invocation.getArgument( 0 );
            if ( saved.getId() == null )
            {
                saved.setId( nextId.getAndIncrement() );
            }
            return saved;
        } );
    }

    // ------------------------------------------------------------------------------------------
    // saveVocabulary
    // ------------------------------------------------------------------------------------------

    @Test
    void saveVocabularyShouldRejectEditWithoutVocabularyId()
    {
        VocabularySnippet snippet = vocabularySnippet( ActionType.EDIT_CV, 100L );
        snippet.setVocabularyId( null );

        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( MissingIdentifierException.class )
            .hasMessage( "Vocabulary ID must not be null" );
        verifyNoInteractions( vocabularyRepository );
    }

    @Test
    void saveVocabularyShouldRejectEditWithoutVersionId()
    {
        VocabularySnippet snippet = vocabularySnippet( ActionType.EDIT_NOTE_CV, null );

        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( MissingIdentifierException.class )
            .hasMessage( "Version ID must not be null" );
        verifyNoInteractions( vocabularyRepository );
    }

    @Test
    void saveVocabularyShouldFailForUnknownVocabulary()
    {
        when( vocabularyRepository.findById( VOCABULARY_ID ) ).thenReturn( Optional.empty() );

        VocabularySnippet snippet = vocabularySnippet( ActionType.EDIT_CV, 100L );
        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( EntityNotFoundException.class );
        verify( vocabularyRepository, never() ).save( any() );
    }

    @Test
    void saveVocabularyShouldFailWhenVersionDoesNotBelongToVocabulary()
    {
        givenStoredVocabulary( vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" ) ) );

        VocabularySnippet snippet = vocabularySnippet( ActionType.EDIT_DDI_CV, 999L );
        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( EntityNotFoundException.class )
            .hasMessageContaining( "999" );
        verify( vocabularyRepository, never() ).save( any() );
    }

    @Test
    void saveVocabularyShouldRejectUnsupportedActionType()
    {
        VocabularySnippet snippet = vocabularySnippet( ActionType.DELETE_CV, 100L );
        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( IllegalActionTypeException.class );
        verifyNoInteractions( vocabularyRepository );
    }

    @Test
    void saveVocabularyShouldRejectUserWithoutAgencyAuthority()
    {
        authenticateWithAgencyRole( AgencyRole.VIEW, SOURCE_LANGUAGE );

        VocabularySnippet snippet = vocabularySnippet( ActionType.EDIT_CV, 100L );
        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( InsufficientVocabularyAuthorityException.class );
        verifyNoInteractions( vocabularyRepository, vocabularyChangeService );
    }

    @Test
    void saveVocabularyShouldRejectNewVocabularyWithId()
    {
        VocabularySnippet snippet = vocabularySnippet( ActionType.CREATE_CV, null );

        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( UnexpectedIdentifierException.class );
        verifyNoInteractions( vocabularyRepository );
    }

    @Test
    void editIdentityOfPublishedVersionShouldUpdateTranslatorAndRegeneratePublishedJson() throws IllegalActionTypeException
    {
        VersionDTO versionDTO = version( 100L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        VersionDTO slVersion = version( 99L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", slVersion, versionDTO );
        givenStoredVocabulary( vocabularyDTO );

        VocabularySnippet snippet = vocabularySnippet( ActionType.EDIT_IDENTITY_CV, 100L );
        snippet.setLanguage( TARGET_LANGUAGE );
        snippet.setTranslateAgency( "Translator" );
        snippet.setTranslateAgencyLink( "https://translator.example" );

        vocabularyService.saveVocabulary( snippet );

        assertThat( versionDTO.getTranslateAgency() ).isEqualTo( "Translator" );
        assertThat( versionDTO.getTranslateAgencyLink() ).isEqualTo( "https://translator.example" );
        verify( vocabularyRepository ).save( any( Vocabulary.class ) );
        // published JSON is regenerated for published versions
        verify( versionService ).findAllPublishedByVocabulary( VOCABULARY_ID );
    }

    // ------------------------------------------------------------------------------------------
    // createNewVersion
    // ------------------------------------------------------------------------------------------

    @Test
    void createNewVersionShouldFailForUnknownVersion()
    {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        assertThatThrownBy( () -> vocabularyService.createNewVersion( 100L ) )
            .isInstanceOf( EntityNotFoundException.class )
            .hasMessage( VocabularyServiceImpl.UNABLE_FIND_VERSION + 100 );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void createNewVersionShouldRejectUnpublishedVersion()
    {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.REVIEW, "1.0" ) ) );

        assertThatThrownBy( () -> vocabularyService.createNewVersion( 100L ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "version is not yet PUBLISHED" );
        verify( versionService, never() ).save( any() );
        verifyNoInteractions( vocabularyRepository );
    }

    @Test
    void createNewVersionShouldFailWhenVocabularyIsMissing()
    {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" ) ) );
        when( vocabularyRepository.findById( VOCABULARY_ID ) ).thenReturn( Optional.empty() );

        assertThatThrownBy( () -> vocabularyService.createNewVersion( 100L ) )
            .isInstanceOf( EntityNotFoundException.class )
            .hasMessage( VocabularyServiceImpl.UNABLE_FIND_VOCABULARY + VOCABULARY_ID );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void createNewVersionShouldFailWhenCurrentSlVersionIsMissing()
    {
        VersionDTO slVersion = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( slVersion ) );
        // the vocabulary points to an SL version number that does not exist
        givenStoredVocabulary( vocabulary( "2.0", slVersion ) );

        assertThatThrownBy( () -> vocabularyService.createNewVersion( 100L ) )
            .isInstanceOf( EntityNotFoundException.class )
            .hasMessageContaining( "SL version number 2.0" );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void createNewSlVersionShouldRejectTranslatorAdmin()
    {
        authenticateWithAgencyRole( AgencyRole.ADMIN_TL, SOURCE_LANGUAGE );
        VersionDTO slVersion = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( slVersion ) );
        givenStoredVocabulary( vocabulary( "1.0", slVersion ) );

        assertThatThrownBy( () -> vocabularyService.createNewVersion( 100L ) )
            .isInstanceOf( InsufficientVocabularyAuthorityException.class )
            .hasMessageContaining( ActionType.CREATE_NEW_CV_SL_VERSION.name() );
        verify( versionService, never() ).save( any() );
        verify( vocabularyRepository, never() ).save( any() );
    }

    @Test
    void createNewTlVersionShouldRejectSourceLanguageAdmin()
    {
        authenticateWithAgencyRole( AgencyRole.ADMIN_SL, TARGET_LANGUAGE );
        VersionDTO slVersion = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO tlVersion = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        when( versionService.findOne( 101L ) ).thenReturn( Optional.of( tlVersion ) );
        givenStoredVocabulary( vocabulary( "1.0", slVersion, tlVersion ) );

        assertThatThrownBy( () -> vocabularyService.createNewVersion( 101L ) )
            .isInstanceOf( InsufficientVocabularyAuthorityException.class )
            .hasMessageContaining( ActionType.CREATE_NEW_CV_TL_VERSION.name() );
        verify( versionService, never() ).save( any() );
    }

    // ------------------------------------------------------------------------------------------
    // saveVocabulary - ADD_TL_CV
    // ------------------------------------------------------------------------------------------

    @Test
    void addTranslationShouldFailForUnknownVocabulary()
    {
        when( vocabularyRepository.findById( VOCABULARY_ID ) ).thenReturn( Optional.empty() );
        VocabularySnippet snippet = vocabularySnippet( ActionType.ADD_TL_CV, null );
        snippet.setLanguage( TARGET_LANGUAGE );

        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( EntityNotFoundException.class );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void addTranslationShouldFailForUnknownSlVersion()
    {
        givenStoredVocabulary( vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" ) ) );
        VocabularySnippet snippet = vocabularySnippet( ActionType.ADD_TL_CV, null );
        snippet.setLanguage( TARGET_LANGUAGE );
        snippet.setVersionSlId( 555L );

        assertThatThrownBy( () -> vocabularyService.saveVocabulary( snippet ) )
            .isInstanceOf( EntityNotFoundException.class )
            .hasMessageContaining( "555" );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void addTranslationShouldContinueFromPreviousTranslation() throws IllegalActionTypeException
    {
        VersionDTO slVersion = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.1" );
        slVersion.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        slVersion.addConcept( concept( 1001L, 100L, "B", null, 1 ) );
        VersionDTO previousTl = version( 90L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        previousTl.setInitialVersion( 80L );
        ConceptDTO previousConcept = concept( 900L, 90L, "A", null, 0 );
        previousConcept.setTitle( "Titel A" );
        previousTl.addConcept( previousConcept );
        VocabularyDTO vocabularyDTO = vocabulary( "1.1", slVersion, previousTl );
        givenStoredVocabulary( vocabularyDTO );
        givenVersionSaveAssignsIds( 200L );

        VocabularySnippet snippet = vocabularySnippet( ActionType.ADD_TL_CV, null );
        snippet.setItemType( ItemType.TL );
        snippet.setLanguage( TARGET_LANGUAGE );
        snippet.setVersionSlId( 100L );
        snippet.setVersionNumber( VersionNumber.fromString( "1.1.1" ) );
        snippet.setTitle( "Titel" );

        vocabularyService.saveVocabulary( snippet );

        ArgumentCaptor<VersionDTO> versionCaptor = ArgumentCaptor.forClass( VersionDTO.class );
        verify( versionService ).save( versionCaptor.capture() );
        VersionDTO newTl = versionCaptor.getValue();
        assertThat( newTl.getPreviousVersion() ).isEqualTo( 90L );
        assertThat( newTl.getInitialVersion() ).isEqualTo( 80L );
        assertThat( newTl.getConcepts() ).hasSize( 2 );
        ConceptDTO translatedA = newTl.findConceptByNotation( "A" );
        assertThat( translatedA.getTitle() ).isEqualTo( "Titel A" );
        assertThat( translatedA.getPreviousConcept() ).isEqualTo( 900L );
        verify( vocabularyMapper ).toEntity( vocabularyDTO );
        verify( vocabularyRepository ).save( any( Vocabulary.class ) );
    }

    // ------------------------------------------------------------------------------------------
    // saveCode
    // ------------------------------------------------------------------------------------------

    @Test
    void saveCodeShouldFailForUnknownVersion()
    {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.empty() );

        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        assertThatThrownBy( () -> vocabularyService.saveCode( snippet ) )
            .isInstanceOf( EntityNotFoundException.class );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void saveCodeShouldFailForUnknownVocabulary()
    {
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" ) ) );
        when( vocabularyRepository.findById( VOCABULARY_ID ) ).thenReturn( Optional.empty() );

        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        assertThatThrownBy( () -> vocabularyService.saveCode( snippet ) )
            .isInstanceOf( EntityNotFoundException.class );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void saveCodeShouldRejectUserWithoutAuthority()
    {
        authenticateWithAgencyRole( AgencyRole.ADMIN_TL, SOURCE_LANGUAGE );
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );
        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "A" );

        assertThatThrownBy( () -> vocabularyService.saveCode( snippet ) )
            .isInstanceOf( InsufficientVocabularyAuthorityException.class );
        assertThat( versionDTO.getConcepts() ).isEmpty();
        verify( versionService, never() ).save( any() );
    }

    @Test
    void saveCodeShouldRejectChangesToPublishedVersion()
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );
        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "A" );

        assertThatThrownBy( () -> vocabularyService.saveCode( snippet ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "Unable to add Code A, Version is already PUBLISHED" );
        assertThat( versionDTO.getConcepts() ).isEmpty();
        verify( versionService, never() ).save( any() );
    }

    @Test
    void saveCodeShouldRejectUnsupportedActionType()
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );

        CodeSnippet snippet = codeSnippet( ActionType.REORDER_CODE, 100L );
        assertThatThrownBy( () -> vocabularyService.saveCode( snippet ) )
            .isInstanceOf( IllegalActionTypeException.class );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void editCodeShouldFailForUnknownConcept()
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );
        CodeSnippet snippet = codeSnippet( ActionType.EDIT_CODE, 100L );
        snippet.setConceptId( 9999L );

        assertThatThrownBy( () -> vocabularyService.saveCode( snippet ) )
            .isInstanceOf( EntityNotFoundException.class )
            .hasMessageContaining( "9999" );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void editCodeShouldRejectRenamingToExistingNotation()
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        ConceptDTO conceptA = concept( 1000L, 100L, "A", null, 0 );
        versionDTO.addConcept( conceptA );
        versionDTO.addConcept( concept( 1001L, 100L, "B", null, 1 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );
        CodeSnippet snippet = codeSnippet( ActionType.EDIT_CODE, 100L );
        snippet.setConceptId( 1000L );
        snippet.setNotation( "B" );

        assertThatThrownBy( () -> vocabularyService.saveCode( snippet ) )
            .isInstanceOf( CodeAlreadyExistException.class );
        assertThat( conceptA.getNotation() ).isEqualTo( "A" );
        verify( versionService, never() ).save( any() );
    }

    @Test
    void editCodeShouldRenameChildrenAndRecordChangeInNonInitialVersion() throws IllegalActionTypeException
    {
        VersionDTO versionDTO = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        versionDTO.setInitialVersion( 100L );
        ConceptDTO conceptA = concept( 1000L, 200L, "A", null, 0 );
        ConceptDTO childA1 = concept( 1001L, 200L, "A.1", "A", 1 );
        ConceptDTO conceptB = concept( 1002L, 200L, "B", null, 2 );
        versionDTO.addConcept( conceptA );
        versionDTO.addConcept( childA1 );
        versionDTO.addConcept( conceptB );
        when( versionService.findOne( 200L ) ).thenReturn( Optional.of( versionDTO ) );
        when( versionService.save( any( VersionDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );
        givenStoredVocabulary( vocabulary( "1.1", versionDTO ) );

        CodeSnippet snippet = codeSnippet( ActionType.EDIT_CODE, 200L );
        snippet.setConceptId( 1000L );
        snippet.setNotation( "C" );
        snippet.setTitle( "Renamed" );
        snippet.setChangeType( "Code value changed" );

        ConceptDTO result = vocabularyService.saveCode( snippet );

        assertThat( result.getNotation() ).isEqualTo( "C" );
        assertThat( result.getTitle() ).isEqualTo( "Renamed" );
        assertThat( childA1.getNotation() ).isEqualTo( "C.1" );
        assertThat( childA1.getParent() ).isEqualTo( "C" );
        assertThat( conceptB.getNotation() ).isEqualTo( "B" );
        verify( vocabularyChangeService ).save( any( VocabularyChangeDTO.class ) );
    }

    @Test
    void editCodeInInitialVersionShouldNotRecordChange() throws IllegalActionTypeException
    {
        VersionDTO versionDTO = version( 100L, ItemType.TL, TARGET_LANGUAGE, Status.DRAFT, "1.0.1" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( versionService.save( any( VersionDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );

        CodeSnippet snippet = codeSnippet( ActionType.DELETE_TL_CODE, 100L );
        snippet.setConceptId( 1000L );
        snippet.setNotation( "A" );
        snippet.setChangeType( "Translation deleted" );

        ConceptDTO result = vocabularyService.saveCode( snippet );

        assertThat( result.getTitle() ).isNull();
        assertThat( result.getDefinition() ).isNull();
        verifyNoInteractions( vocabularyChangeService );
    }

    @Test
    void createCodeShouldInsertAfterReferenceConcept() throws IllegalActionTypeException
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        ConceptDTO conceptA = concept( 1000L, 100L, "A", null, 0 );
        ConceptDTO conceptB = concept( 1001L, 100L, "B", null, 1 );
        versionDTO.addConcept( conceptA );
        versionDTO.addConcept( conceptB );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( versionService.save( any( VersionDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );

        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "AB" );
        snippet.setInsertionRefConceptId( 1000L );
        // values greater than one are limited to "directly after the reference concept"
        snippet.setRelPosToRefConcept( 5 );

        ConceptDTO result = vocabularyService.saveCode( snippet );

        assertThat( result.getNotation() ).isEqualTo( "AB" );
        assertThat( result.getPosition() ).isEqualTo( 1 );
        assertThat( conceptA.getPosition() ).isZero();
        assertThat( conceptB.getPosition() ).isEqualTo( 2 );
        assertThat( versionDTO.getConcepts() ).extracting( ConceptDTO::getNotation ).containsExactly( "A", "AB", "B" );
    }

    @Test
    void createCodeShouldInsertBeforeReferenceConceptWhenNoRelativePositionIsGiven() throws IllegalActionTypeException
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        versionDTO.addConcept( concept( 1001L, 100L, "B", null, 1 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( versionService.save( any( VersionDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );

        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "AA" );
        snippet.setInsertionRefConceptId( 1001L );

        ConceptDTO result = vocabularyService.saveCode( snippet );

        assertThat( result.getPosition() ).isEqualTo( 1 );
        assertThat( versionDTO.getConcepts() ).extracting( ConceptDTO::getNotation ).containsExactly( "A", "AA", "B" );
    }

    @Test
    void createCodeShouldAppendWhenReferenceConceptIsUnknown() throws IllegalActionTypeException
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        versionDTO.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        when( versionService.findOne( 100L ) ).thenReturn( Optional.of( versionDTO ) );
        when( versionService.save( any( VersionDTO.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );

        CodeSnippet snippet = codeSnippet( ActionType.CREATE_CODE, 100L );
        snippet.setNotation( "Z" );
        snippet.setInsertionRefConceptId( 9999L );

        ConceptDTO result = vocabularyService.saveCode( snippet );

        assertThat( result.getPosition() ).isEqualTo( 1 );
        assertThat( versionDTO.getConcepts() ).extracting( ConceptDTO::getNotation ).containsExactly( "A", "Z" );
    }

    // ------------------------------------------------------------------------------------------
    // delete and file handling
    // ------------------------------------------------------------------------------------------

    @Test
    void deleteShouldFailForUnknownVocabulary()
    {
        when( vocabularyRepository.findById( VOCABULARY_ID ) ).thenReturn( Optional.empty() );

        assertThatThrownBy( () -> vocabularyService.delete( VOCABULARY_ID ) )
            .isInstanceOf( EntityNotFoundException.class );
        verify( vocabularyRepository, never() ).deleteById( any() );
        verifyNoInteractions( vocabularyEditorSearchRepository, vocabularyPublishSearchRepository );
    }

    @Test
    void deleteShouldRemoveVocabularyIndexesStatisticsAndJsonFiles() throws IOException
    {
        givenStoredVocabulary( vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" ) ) );
        AgencyStat agencyStat = mock( AgencyStat.class );
        when( agencyStatSearchRepository.findById( AGENCY_ID ) ).thenReturn( Optional.of( agencyStat ) );
        Path jsonDirectory = applicationProperties.getVocabJsonPath().resolve( NOTATION );
        Files.createDirectories( jsonDirectory.resolve( "1.0" ) );
        Files.writeString( jsonDirectory.resolve( NOTATION + ".json" ), "{}" );

        vocabularyService.delete( VOCABULARY_ID );

        verify( agencyStat ).deleteVocabStat( NOTATION );
        verify( agencyStatSearchRepository ).save( agencyStat );
        verify( vocabularyRepository ).deleteById( VOCABULARY_ID );
        verify( vocabularyEditorSearchRepository ).deleteById( VOCABULARY_ID );
        verify( vocabularyPublishSearchRepository ).deleteById( VOCABULARY_ID );
        assertThat( jsonDirectory ).doesNotExist();
    }

    @Test
    void getPublishedCvPathsShouldReturnEmptyListWhenDirectoryIsMissing()
    {
        // the vocabulary JSON directory was never created in the temporary directory
        assertThat( vocabularyService.getPublishedCvPaths() ).isEmpty();
    }

    // ------------------------------------------------------------------------------------------
    // getByNotation / getVocabularyByNotationAndVersion
    // ------------------------------------------------------------------------------------------

    @Test
    void getByNotationShouldRejectMissingNotation()
    {
        assertThatThrownBy( () -> vocabularyService.getByNotation( null ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> vocabularyService.getByNotation( "" ) ).isInstanceOf( IllegalArgumentException.class );
        verifyNoInteractions( vocabularyRepository );
    }

    @Test
    void getByNotationShouldThrowResourceNotFoundForUnknownNotation()
    {
        when( vocabularyRepository.findAllByNotation( "Unknown" ) ).thenReturn( Collections.emptyList() );

        assertThatThrownBy( () -> vocabularyService.getByNotation( "Unknown" ) )
            .isInstanceOf( ResourceNotFoundException.class )
            .hasMessageContaining( "Unknown" );
    }

    private VocabularyDTO givenVocabularyByNotation( VocabularyDTO vocabularyDTO )
    {
        Vocabulary entity = new Vocabulary();
        when( vocabularyRepository.findAllByNotation( NOTATION ) ).thenReturn( Collections.singletonList( entity ) );
        when( vocabularyMapper.toDto( entity ) ).thenReturn( vocabularyDTO );
        when( agencyRepository.getReferenceById( AGENCY_ID ) ).thenReturn( agency( "Agency" ) );
        return vocabularyDTO;
    }

    @Test
    void getVocabularyWithAllVersionsShouldReturnEveryVersion()
    {
        VersionDTO sl10 = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO sl11 = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        givenVocabularyByNotation( vocabulary( "1.1", sl10, sl11 ) );

        VocabularyDTO result = vocabularyService.getVocabularyByNotationAndVersion( NOTATION, VocabularyServiceImpl.ALL, false );

        assertThat( result.getVersions() ).containsExactlyInAnyOrder( sl10, sl11 );
        assertThat( result.getAgencyLink() ).isEqualTo( "https://agency.example" );
        verifyNoInteractions( versionService );
    }

    @Test
    void getLatestEditorVocabularyShouldReturnOneVersionPerLanguageWithLicence()
    {
        VersionDTO sl10 = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO sl11 = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        sl11.setLicenseId( 2L );
        givenVocabularyByNotation( vocabulary( "1.1", sl10, sl11 ) );

        VersionDTO newestDe = version( 202L, ItemType.TL, TARGET_LANGUAGE, Status.DRAFT, "1.1.2" );
        VersionDTO olderDe = version( 201L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.1.1" );
        when( versionService.findAllByVocabularyAndVersionSl( eq( VOCABULARY_ID ), any( VersionNumber.class ) ) )
            .thenReturn( Arrays.asList( sl11, newestDe, olderDe ) );

        Licence other = new Licence();
        other.setId( 1L );
        Licence ccBy = new Licence();
        ccBy.setId( 2L );
        ccBy.setAbbr( "CC BY 4.0" );
        ccBy.setName( "Creative Commons Attribution 4.0" );
        ccBy.setLink( "https://creativecommons.org/licenses/by/4.0/" );
        ccBy.setLogoLink( "cc-by.png" );
        when( licenceRepository.findAll() ).thenReturn( Arrays.asList( other, ccBy ) );

        VocabularyDTO result = vocabularyService.getVocabularyByNotationAndVersion( NOTATION, VocabularyServiceImpl.LATEST, false );

        // only the first version of each language is kept
        assertThat( result.getVersions() ).containsExactly( sl11, newestDe );
        assertThat( sl11.getLicense() ).isEqualTo( "CC BY 4.0" );
        assertThat( sl11.getLicenseName() ).isEqualTo( "Creative Commons Attribution 4.0" );
        assertThat( sl11.getLicenseLink() ).isEqualTo( "https://creativecommons.org/licenses/by/4.0/" );
        assertThat( sl11.getLicenseLogo() ).isEqualTo( "cc-by.png" );
        assertThat( newestDe.getLicense() ).isNull();
    }

    @Test
    void getLatestPublishedVocabularyShouldAddLatestVersionsOfOtherLanguagesAndHistory()
    {
        VersionDTO sl10 = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO sl11 = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" );
        givenVocabularyByNotation( vocabulary( "1.1", sl10, sl11 ) );

        VersionDTO de = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        VersionDTO slAgain = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        when( versionService.findAllPublishedByVocabularyAndVersionSl( VOCABULARY_ID, VersionNumber.fromString( "1.0" ) ) )
            .thenReturn( new LinkedHashSet<>( Collections.singleton( sl10 ) ) );
        when( versionService.findAllPublishedByVocabulary( VOCABULARY_ID ) ).thenReturn( Arrays.asList( slAgain, de ) );

        // an older published version referencing itself as previous version
        VersionDTO older = version( 50L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "0.9" );
        older.setPreviousVersion( 50L );
        when( versionService.findOlderPublishedByVocabularyLanguageId( VOCABULARY_ID, SOURCE_LANGUAGE, 100L ) )
            .thenReturn( Collections.singletonList( older ) );

        VocabularyDTO result = vocabularyService.getVocabularyByNotationAndVersion( NOTATION, VocabularyServiceImpl.LATEST, true );

        assertThat( result.getVersions() ).extracting( VersionDTO::getId ).containsExactlyInAnyOrder( 100L, 101L );
        assertThat( sl10.getVersionHistories() ).hasSize( 1 );
        assertThat( sl10.getVersionHistories().get( 0 ).getId() ).isEqualTo( 50L );
        assertThat( sl10.getVersionHistories().get( 0 ).getPreviousVersion() ).isNull();
    }

    // ------------------------------------------------------------------------------------------
    // indexing
    // ------------------------------------------------------------------------------------------

    @Test
    void indexAllEditorAndPublishedShouldSkipWithdrawnVocabularies()
    {
        VocabularyDTO withdrawn = vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" ) );
        withdrawn.setWithdrawn( true );
        VocabularyDTO active = vocabulary( "1.0", version( 300L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" ) );
        active.setId( 30L );
        Vocabulary withdrawnEntity = new Vocabulary();
        Vocabulary activeEntity = new Vocabulary();
        when( vocabularyRepository.findAll() ).thenReturn( Arrays.asList( withdrawnEntity, activeEntity ) );
        when( vocabularyMapper.toDto( withdrawnEntity ) ).thenReturn( withdrawn );
        when( vocabularyMapper.toDto( activeEntity ) ).thenReturn( active );

        vocabularyService.indexAllEditor();
        vocabularyService.indexAllPublished();

        verify( vocabularyEditorSearchRepository ).deleteAll();
        verify( vocabularyEditorMapper ).toEntity( active );
        verify( vocabularyEditorMapper, never() ).toEntity( withdrawn );
        verify( vocabularyEditorSearchRepository, times( 1 ) ).save( any() );
        // the only non-withdrawn vocabulary has no published version, so nothing is indexed
        verify( vocabularyPublishSearchRepository ).deleteAll();
        verify( vocabularyPublishSearchRepository, never() ).save( any() );
    }

    @Test
    void indexAllAgencyStatsShouldUpdateStatisticsOfNonWithdrawnVocabularies()
    {
        VocabularyDTO withdrawn = vocabulary( "1.0" );
        withdrawn.setWithdrawn( true );
        VocabularyDTO active = vocabulary( "1.0" );
        active.setId( 11L );
        Vocabulary withdrawnEntity = new Vocabulary();
        Vocabulary activeEntity = new Vocabulary();
        when( vocabularyRepository.findAll() ).thenReturn( Arrays.asList( withdrawnEntity, activeEntity ) );
        when( vocabularyMapper.toDto( withdrawnEntity ) ).thenReturn( withdrawn );
        when( vocabularyMapper.toDto( activeEntity ) ).thenReturn( active );
        AgencyStat agencyStat = mock( AgencyStat.class );
        when( agencyStatSearchRepository.findById( AGENCY_ID ) ).thenReturn( Optional.of( agencyStat ) );

        vocabularyService.indexAllAgencyStats();

        verify( agencyStat ).updateVocabStat( active );
        verify( agencyStat, never() ).updateVocabStat( withdrawn );
        verify( agencyStatSearchRepository, times( 1 ) ).save( agencyStat );
    }

    @Test
    void indexEditorShouldUpdateAgencyStatistics()
    {
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.1" ) );
        AgencyStat agencyStat = mock( AgencyStat.class );
        when( agencyStatSearchRepository.findById( AGENCY_ID ) ).thenReturn( Optional.of( agencyStat ) );

        vocabularyService.indexEditor( vocabularyDTO );

        // the editor index always shows the latest SL version
        assertThat( vocabularyDTO.getVersionNumber() ).isEqualTo( VersionNumber.fromString( "1.1" ) );
        verify( agencyStat ).updateVocabStat( vocabularyDTO );
        verify( agencyStatSearchRepository ).save( agencyStat );
        verify( vocabularyEditorSearchRepository ).save( any() );
    }

    @Test
    void indexEditorShouldSkipVocabularyWithoutSourceVersion()
    {
        vocabularyService.indexEditor( vocabulary( "1.0", version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.DRAFT, "1.0.1" ) ) );

        verifyNoInteractions( vocabularyEditorMapper, vocabularyEditorSearchRepository, agencyStatSearchRepository );
    }

    @Test
    void indexPublishedShouldIgnoreWithdrawnVocabulary()
    {
        VocabularyDTO withdrawn = vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" ) );
        withdrawn.setWithdrawn( true );

        vocabularyService.indexPublished( withdrawn );

        verifyNoInteractions( versionService, vocabularyPublishMapper, vocabularyPublishSearchRepository );
    }

    // ------------------------------------------------------------------------------------------
    // JSON / file generation
    // ------------------------------------------------------------------------------------------

    @Test
    void generateJsonShouldFailForTranslationWithoutSourceVersion()
    {
        VocabularyDTO vocabularyDTO = vocabulary( "1.0" );
        VersionDTO orphanTl = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        when( versionService.findAllPublishedByVocabulary( VOCABULARY_ID ) ).thenReturn( Collections.singletonList( orphanTl ) );

        assertThatThrownBy( () -> vocabularyService.generateJsonVocabularyPublish( vocabularyDTO ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "SL version missing" );
        assertThat( applicationProperties.getVocabJsonPath().resolve( NOTATION ) ).doesNotExist();
    }

    @Test
    void generateJsonShouldSkipVocabularyWithoutPublishedVersions()
    {
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" ) );
        when( versionService.findAllPublishedByVocabulary( VOCABULARY_ID ) ).thenReturn( Collections.emptyList() );

        String output = vocabularyService.generateJsonVocabularyPublish( vocabularyDTO );

        assertThat( output ).contains( NOTATION );
        assertThat( applicationProperties.getVocabJsonPath().resolve( NOTATION ) ).doesNotExist();
        verifyNoInteractions( conceptService );
    }

    @Test
    void generateJsonAllShouldRemoveExistingFilesAndRegenerate() throws IOException
    {
        Path staleFile = applicationProperties.getVocabJsonPath().resolve( "Stale" ).resolve( "Stale.json" );
        Files.createDirectories( staleFile.getParent() );
        Files.writeString( staleFile, "{}" );
        Vocabulary entity = new Vocabulary();
        when( vocabularyRepository.findAll() ).thenReturn( Collections.singletonList( entity ) );
        when( vocabularyMapper.toDto( entity ) ).thenReturn( vocabulary( "1.0" ) );
        when( versionService.findAllPublishedByVocabulary( VOCABULARY_ID ) ).thenReturn( Collections.emptyList() );

        String output = vocabularyService.generateJsonAllVocabularyPublish();

        assertThat( output ).contains( "All existing JSON files are deleted", "Generate JSON files for Vocabulary " + NOTATION );
        assertThat( applicationProperties.getVocabJsonPath() ).doesNotExist();
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, Object> captureExportMap( String expectedTemplate, ExportService.DownloadType type ) throws Exception
    {
        ArgumentCaptor<Map<String, Object>> mapCaptor = ArgumentCaptor.forClass( Map.class );
        verify( exportService ).generateFileByThymeleafTemplate( eq( expectedTemplate ), mapCaptor.capture(), eq( type ), any( OutputStream.class ) );
        return mapCaptor.getValue();
    }

    @Test
    void skosDownloadForDdiAllianceShouldUseDdiTemplateAndFallbackValues() throws Exception
    {
        VersionDTO sl = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        sl.setUri( "https://vocabularies.example/TestCV?v=1.0" );
        VersionDTO olderSl = version( 90L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "0.9" );
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", olderSl, sl );
        vocabularyDTO.setAgencyName( "DDI Alliance" );
        vocabularyDTO.setLastModified( ZonedDateTime.parse( "2019-05-01T10:00:00Z" ) );
        givenVocabularyByNotation( vocabularyDTO );

        Path file = vocabularyService.generateVocabularyFileDownload( NOTATION, VocabularyServiceImpl.ALL, null,
            ExportService.DownloadType.SKOS, "https://example", false );

        assertThat( file.getFileName() ).hasToString( NOTATION + "-all.rdf" );
        Map<String, Object> map = captureExportMap( "export-ddi", ExportService.DownloadType.SKOS );
        assertThat( map )
            // without an SL URI the version URI is used as document ID
            .containsEntry( "docId", "https://vocabularies.example/TestCV?v=1.0" )
            // without a publication date the year of the last modification is used
            .containsEntry( "year", 2019 )
            .containsEntry( "baseUrl", "https://example" )
            // SKOS contains only the latest version of each language
            .containsEntry( "docVersion", sl.getNumber().toString() );
        assertThat( new ArrayList<Object>( (Collection<?>) map.get( "versions" ) ) ).containsExactly( sl );
    }

    @Test
    void pdfDownloadShouldEmbedAgencyLogoTolerateMissingLicenceLogoAndExposeUrn() throws Exception
    {
        VersionDTO sl = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        sl.setLicenseLogo( "missing-licence-logo.png" );
        sl.setCanonicalUri( "urn:ddi:int.ddi.cv:TestCV:1.0" );
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", sl );
        vocabularyDTO.setAgencyName( "Agency" );
        vocabularyDTO.setPublicationDate( LocalDate.of( 2021, Month.MARCH, 1 ) );
        givenVocabularyByNotation( vocabularyDTO );
        // the agency logo is taken from the agency and embedded as a data URI
        when( agencyRepository.getReferenceById( AGENCY_ID ) ).thenReturn( agency( "Agency" ).logopath( "logo.png" ) );
        Files.createDirectories( applicationProperties.getAgencyImagePath() );
        Files.write( applicationProperties.getAgencyImagePath().resolve( "logo.png" ), new byte[]{ 1, 2, 3 } );

        vocabularyService.generateVocabularyFileDownload( NOTATION, VocabularyServiceImpl.ALL, "en-1.0",
            ExportService.DownloadType.PDF, "https://example", false );

        Map<String, Object> map = captureExportMap( "export", ExportService.DownloadType.PDF );
        assertThat( map )
            .containsEntry( "cvUrn", "urn:ddi:int.ddi.cv:TestCV" )
            .containsEntry( "year", 2021 );
        assertThat( ( (AgencyDTO) map.get( "agency" ) ).getLogo() ).startsWith( "data:" ).endsWith( ";base64,AQID" );
        // a missing licence logo does not fail the export
        assertThat( sl.getLicenseLogo() ).isNull();
    }

    @Test
    void downloadShouldReturnPreviouslyGeneratedFile() throws Exception
    {
        Path existing = applicationProperties.getExportFilePath().resolve( NOTATION + "-1.0_en-1.0.pdf" );
        Files.createDirectories( existing.getParent() );
        Files.writeString( existing, "cached" );

        Path file = vocabularyService.generateVocabularyFileDownload( NOTATION, "1.0", "en-1.0",
            ExportService.DownloadType.PDF, "https://example", true );

        assertThat( file ).isEqualTo( existing ).hasContent( "cached" );
        verifyNoInteractions( exportService, vocabularyRepository );
    }

    @Test
    void downloadShouldWrapExportFailures() throws Exception
    {
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" ) );
        vocabularyDTO.setAgencyName( "Agency" );
        givenVocabularyByNotation( vocabularyDTO );
        doThrow( new IOException( "disk full" ) ).when( exportService )
            .generateFileByThymeleafTemplate( anyString(), anyMap(), any(), any( OutputStream.class ) );

        assertThatThrownBy( () -> vocabularyService.generateVocabularyFileDownload( NOTATION, VocabularyServiceImpl.ALL, null,
            ExportService.DownloadType.HTML, "https://example", false ) )
            .isInstanceOf( VocabularyFileGenerationFailedException.class )
            .hasRootCauseInstanceOf( IOException.class );
    }

    @Test
    void filterOutVocabularyVersionsShouldIgnoreMalformedAndUnknownEntries()
    {
        VersionDTO en = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO de = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", en, de );

        Set<VersionDTO> result = vocabularyService.filterOutVocabularyVersions( "en-1.0_malformed_de-9.9.9", vocabularyDTO );

        assertThat( result ).containsExactly( en );
        assertThat( vocabularyService.filterOutVocabularyVersions( null, vocabularyDTO ) ).containsExactlyInAnyOrder( en, de );
    }

    // ------------------------------------------------------------------------------------------
    // agency updates
    // ------------------------------------------------------------------------------------------

    @Test
    void updateVocabularyUriShouldRegenerateUrisOnlyForPublishedVersions()
    {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setId( VOCABULARY_ID );
        vocabulary.setNotation( NOTATION );
        vocabulary.setSourceLanguage( SOURCE_LANGUAGE );

        Version published = new Version();
        published.setId( 100L );
        published.setStatus( Status.PUBLISHED );
        published.setNumber( VersionNumber.fromString( "1.0" ) );
        published.setItemType( ItemType.SL );
        published.setLanguage( SOURCE_LANGUAGE );
        published.setUriSl( "old" );
        Concept publishedConcept = new Concept();
        publishedConcept.setId( 1000L );
        publishedConcept.setNotation( "A" );
        published.addConcept( publishedConcept );

        Version draft = new Version();
        draft.setId( 200L );
        draft.setStatus( Status.DRAFT );
        draft.setNumber( VersionNumber.fromString( "1.1" ) );
        draft.setItemType( ItemType.SL );
        draft.setLanguage( SOURCE_LANGUAGE );
        draft.setUri( "stale" );
        Concept draftConcept = new Concept();
        draftConcept.setId( 2000L );
        draftConcept.setNotation( "A" );
        draftConcept.setUri( "stale" );
        draft.addConcept( draftConcept );

        vocabulary.addVersion( published );
        vocabulary.addVersion( draft );
        when( vocabularyRepository.findAllByAgencyId( AGENCY_ID ) ).thenReturn( Collections.singletonList( vocabulary ) );

        vocabularyService.updateVocabularyUri( AGENCY_ID, "https://new.example/[VOCABULARY]?v=[VERSION]",
            "https://new.example/[VOCABULARY]_[CODE]?v=[VERSION]", "Agency" );

        assertThat( vocabulary.getUri() ).isEqualTo( "https://new.example/" + NOTATION );
        assertThat( published.getUri() ).isEqualTo( "https://new.example/" + NOTATION + "?v=" + published.getNumber() );
        assertThat( published.getUriSl() ).isEqualTo( "https://new.example/" + NOTATION + "?v=" + published.getNumber() );
        assertThat( publishedConcept.getUri() ).isEqualTo( "https://new.example/" + NOTATION + "_A?v=" + published.getNumber() );
        assertThat( draft.getUri() ).isNull();
        assertThat( draft.getUriSl() ).isNull();
        assertThat( draftConcept.getUri() ).isNull();
        verify( vocabularyRepository ).save( vocabulary );
    }

    @Test
    void updateVocabularyLogoShouldOnlyUpdateAndReindexChangedVocabularies()
    {
        Vocabulary unchanged = new Vocabulary();
        unchanged.setId( 11L );
        unchanged.setAgencyLogo( "logo.png" );
        Vocabulary changed = new Vocabulary();
        changed.setId( 12L );
        changed.setAgencyLogo( "old.png" );
        Vocabulary withoutLogo = new Vocabulary();
        withoutLogo.setId( 13L );
        when( vocabularyRepository.findAllByAgencyId( AGENCY_ID ) ).thenReturn( Arrays.asList( unchanged, changed, withoutLogo ) );

        VocabularyDTO unchangedDto = vocabulary( "1.0" );
        unchangedDto.setId( 11L );
        VocabularyDTO changedDto = vocabulary( "1.0" );
        changedDto.setId( 12L );
        when( vocabularyRepository.findAll() ).thenReturn( Arrays.asList( unchanged, changed ) );
        when( vocabularyMapper.toDto( unchanged ) ).thenReturn( unchangedDto );
        when( vocabularyMapper.toDto( changed ) ).thenReturn( changedDto );

        vocabularyService.updateVocabularyLogo( AGENCY_ID, "logo.png" );

        assertThat( changed.getAgencyLogo() ).isEqualTo( "logo.png" );
        assertThat( withoutLogo.getAgencyLogo() ).isEqualTo( "logo.png" );
        verify( vocabularyRepository ).save( changed );
        verify( vocabularyRepository ).save( withoutLogo );
        verify( vocabularyRepository, never() ).save( unchanged );
        verify( vocabularyEditorSearchRepository ).deleteById( 12L );
        verify( vocabularyPublishSearchRepository ).deleteById( 12L );
        verify( vocabularyEditorSearchRepository, never() ).deleteById( 11L );
    }

    // ------------------------------------------------------------------------------------------
    // forwardStatus
    // ------------------------------------------------------------------------------------------

    @Test
    void forwardStatusShouldRejectMissingIdentifiers()
    {
        VocabularySnippet withoutVocabulary = vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_REVIEW, 100L );
        withoutVocabulary.setVocabularyId( null );
        VocabularySnippet withoutVersion = vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_REVIEW, null );

        assertThatThrownBy( () -> vocabularyService.forwardStatus( withoutVocabulary ) )
            .isInstanceOf( MissingIdentifierException.class )
            .hasMessage( "Vocabulary ID must not be null" );
        assertThatThrownBy( () -> vocabularyService.forwardStatus( withoutVersion ) )
            .isInstanceOf( MissingIdentifierException.class )
            .hasMessage( "Version ID must not be null" );
        verifyNoInteractions( vocabularyRepository );
    }

    @Test
    void forwardStatusShouldReturnNotFoundForUnknownVocabularyOrVersion()
    {
        when( vocabularyRepository.findById( VOCABULARY_ID ) ).thenReturn( Optional.empty() );
        VocabularySnippet unknownVocabulary = vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_REVIEW, 100L );
        assertThatThrownBy( () -> vocabularyService.forwardStatus( unknownVocabulary ) )
            .isInstanceOf( NotFoundException.class )
            .hasMessageContaining( "VocabularyDTO" );

        givenStoredVocabulary( vocabulary( "1.0", version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" ) ) );
        VocabularySnippet unknownVersion = vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_REVIEW, 999L );
        assertThatThrownBy( () -> vocabularyService.forwardStatus( unknownVersion ) )
            .isInstanceOf( NotFoundException.class )
            .hasMessageContaining( "VersionDTO" );
        verify( vocabularyRepository, never() ).save( any() );
    }

    @Test
    void forwardStatusShouldRejectNonForwardActionType()
    {
        VersionDTO versionDTO = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.DRAFT, "1.0" );
        givenStoredVocabulary( vocabulary( "1.0", versionDTO ) );

        VocabularySnippet snippet = vocabularySnippet( ActionType.EDIT_CV, 100L );
        assertThatThrownBy( () -> vocabularyService.forwardStatus( snippet ) )
            .isInstanceOf( IllegalActionTypeException.class );
        assertThat( versionDTO.getStatus() ).isEqualTo( Status.DRAFT );
        verify( vocabularyRepository, never() ).save( any() );
    }

    @Test
    void forwardStatusShouldRejectUserWithoutAuthority()
    {
        authenticateWithAgencyRole( AgencyRole.ADMIN_TL, SOURCE_LANGUAGE );

        VocabularySnippet snippet = vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_PUBLISH, 100L );
        assertThatThrownBy( () -> vocabularyService.forwardStatus( snippet ) )
            .isInstanceOf( InsufficientVocabularyAuthorityException.class );
        verifyNoInteractions( vocabularyRepository, versionService );
    }

    @Test
    void republishingSlShouldCloneSlAndPublishedTlsAndMoveDraftTlsToNextBundle() throws IllegalActionTypeException
    {
        VersionDTO sl = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        sl.addConcept( concept( 1000L, 100L, "A", null, 0 ) );
        CommentDTO comment = new CommentDTO();
        comment.setId( 1L );
        comment.setContent( "Looks good" );
        sl.addComment( comment );
        VersionDTO publishedDe = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        VersionDTO draftFr = version( 102L, ItemType.TL, "fr", Status.DRAFT, "1.0.1" );
        VocabularyDTO vocabularyDTO = vocabulary( "1.0", sl, publishedDe, draftFr );
        Vocabulary entity = givenStoredVocabulary( vocabularyDTO );
        givenVersionSaveAssignsIds( 500L );
        when( agencyRepository.getReferenceById( AGENCY_ID ) ).thenReturn( agency( "Agency" ) );
        // indexing replaces the versions of the vocabulary, so remember what is saved
        List<VersionDTO> savedVersions = new ArrayList<>();
        String[] savedVersionDe = new String[1];
        when( vocabularyMapper.toEntity( any( VocabularyDTO.class ) ) ).thenAnswer( invocation -> {
            VocabularyDTO saved = invocation.getArgument( 0 );
            savedVersions.addAll( saved.getVersions() );
            savedVersionDe[0] = saved.getVersionDe();
            return entity;
        } );

        VersionDTO result = vocabularyService.forwardStatus( vocabularySnippet( ActionType.FORWARD_CV_SL_STATUS_PUBLISH, 100L ) );

        // a new SL version is created in the next patch bundle
        assertThat( result.getId() ).isNotEqualTo( 100L );
        assertThat( result.getPreviousVersion() ).isEqualTo( 100L );
        assertThat( result.getNumber() ).isEqualTo( VersionNumber.fromString( "1.0.1" ) );
        assertThat( result.getStatus() ).isEqualTo( Status.PUBLISHED );
        assertThat( result.getConcepts() ).singleElement().satisfies( c -> {
            assertThat( c.getPreviousConcept() ).isEqualTo( 1000L );
            assertThat( c.getVersionId() ).isEqualTo( result.getId() );
        } );
        assertThat( result.getComments() ).singleElement().satisfies( c -> {
            assertThat( c.getContent() ).isEqualTo( "Looks good" );
            assertThat( c.getVersionId() ).isEqualTo( result.getId() );
        } );

        // the published German translation is cloned into the new bundle
        assertThat( savedVersions ).hasSize( 5 ).anySatisfy( v -> {
            assertThat( v.getLanguage() ).isEqualTo( TARGET_LANGUAGE );
            assertThat( v.getPreviousVersion() ).isEqualTo( 101L );
            assertThat( v.getNumber() ).isEqualTo( VersionNumber.fromString( "1.0.1" ) );
        } );
        assertThat( savedVersionDe[0] ).isEqualTo( "1.0.1" );
        // the unpublished French translation is moved behind the new bundle
        assertThat( draftFr.getNumber() ).isEqualTo( VersionNumber.fromString( "1.0.2" ) );
        assertThat( draftFr.getStatus() ).isEqualTo( Status.DRAFT );
        // the original SL and TL versions are untouched
        assertThat( sl.getNumber() ).isEqualTo( VersionNumber.fromString( "1.0" ) );
        assertThat( publishedDe.getNumber() ).isEqualTo( VersionNumber.fromString( "1.0.1" ) );
        verify( versionService, times( 2 ) ).save( any( VersionDTO.class ) );
        verify( vocabularyRepository ).save( any( Vocabulary.class ) );
    }

    // ------------------------------------------------------------------------------------------
    // cloning
    // ------------------------------------------------------------------------------------------

    @Test
    void cloneTlShouldTakeTranslationsFromPreviousPublishedTlEvenIfSlNotationChanged()
    {
        VersionDTO previousSl = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        previousSl.addConcept( concept( 10L, 100L, "A", null, 0 ) );
        VersionDTO previousTl = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.PUBLISHED, "1.0.1" );
        ConceptDTO previousTlConcept = concept( 20L, 101L, "A", null, 0 );
        previousTlConcept.setTitle( "Titel A" );
        previousTlConcept.setDefinition( "Definition A" );
        previousTl.addConcept( previousTlConcept );

        VersionDTO currentSl = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.READY_TO_TRANSLATE, "1.1" );
        // the code A was renamed to A2 in the new SL version
        ConceptDTO renamed = concept( 30L, 200L, "A2", null, 0 );
        renamed.setPreviousConcept( 10L );
        currentSl.addConcept( renamed );
        // a code whose previous concept cannot be found in the previous SL
        ConceptDTO orphan = concept( 31L, 200L, "X", null, 1 );
        orphan.setPreviousConcept( 99L );
        currentSl.addConcept( orphan );
        // a code whose previous SL concept exists, but was never translated
        previousSl.addConcept( concept( 12L, 100L, "Q", null, 1 ) );
        ConceptDTO untranslated = concept( 32L, 200L, "Y", null, 2 );
        untranslated.setPreviousConcept( 12L );
        currentSl.addConcept( untranslated );

        givenVersionSaveAssignsIds( 300L );
        VocabularyChangeDTO change = new VocabularyChangeDTO();
        change.setVersionId( 200L );
        when( vocabularyChangeService.findByVersionId( 200L ) ).thenReturn( Collections.singletonList( change ) );

        VersionDTO newTl = vocabularyService.cloneTl( currentSl, previousSl, previousTl );

        assertThat( newTl.getId() ).isEqualTo( 300L );
        assertThat( newTl.getPreviousVersion() ).isEqualTo( 101L );
        ConceptDTO clonedRenamed = newTl.findConceptByNotation( "A2" );
        assertThat( clonedRenamed.getTitle() ).isEqualTo( "Titel A" );
        assertThat( clonedRenamed.getDefinition() ).isEqualTo( "Definition A" );
        assertThat( clonedRenamed.getPreviousConcept() ).isEqualTo( 20L );
        // no previous TL concept can be found for X, so it stays untranslated
        ConceptDTO clonedOrphan = newTl.findConceptByNotation( "X" );
        assertThat( clonedOrphan.getTitle() ).isNull();
        assertThat( clonedOrphan.getPreviousConcept() ).isNull();
        assertThat( newTl.findConceptByNotation( "Y" ).getTitle() ).isNull();
        // the change log of the SL version is moved to the new TL version
        assertThat( change.getVersionId() ).isEqualTo( 300L );
        verify( vocabularyChangeService ).save( change );
    }

    @Test
    void cloneTlFromUnpublishedTlShouldStartANewVersionHistory()
    {
        VersionDTO previousSl = version( 100L, ItemType.SL, SOURCE_LANGUAGE, Status.PUBLISHED, "1.0" );
        VersionDTO previousTl = version( 101L, ItemType.TL, TARGET_LANGUAGE, Status.DRAFT, "1.0.1" );
        previousTl.setInitialVersion( 90L );
        ConceptDTO previousTlConcept = concept( 20L, 101L, "A", null, 0 );
        previousTlConcept.setTitle( "Titel A" );
        previousTl.addConcept( previousTlConcept );
        VersionDTO currentSl = version( 200L, ItemType.SL, SOURCE_LANGUAGE, Status.READY_TO_TRANSLATE, "1.1" );
        ConceptDTO slConcept = concept( 30L, 200L, "A", null, 0 );
        slConcept.setPreviousConcept( 10L );
        currentSl.addConcept( slConcept );
        givenVersionSaveAssignsIds( 300L );

        VersionDTO newTl = vocabularyService.cloneTl( currentSl, previousSl, previousTl );

        assertThat( newTl.getPreviousVersion() ).isNull();
        assertThat( newTl.getInitialVersion() ).isNull();
        ConceptDTO cloned = newTl.findConceptByNotation( "A" );
        assertThat( cloned.getTitle() ).isEqualTo( "Titel A" );
        assertThat( cloned.getPreviousConcept() ).isNull();
    }

    @Test
    void cloneConceptShouldMoveValidityToNewVersionOnlyWhenValidUntilItself()
    {
        ConceptDTO selfValid = concept( 10L, 100L, "A", null, 0 );
        selfValid.setValidUntilVersionId( 10L );
        ConceptDTO otherValid = concept( 11L, 100L, "B", null, 1 );
        otherValid.setValidUntilVersionId( 50L );

        ConceptDTO clonedSelf = vocabularyService.cloneConcept( selfValid, 200L );
        ConceptDTO clonedOther = vocabularyService.cloneConcept( otherValid, 200L );

        assertThat( clonedSelf.getId() ).isNull();
        assertThat( clonedSelf.getVersionId() ).isEqualTo( 200L );
        assertThat( clonedSelf.getPreviousConcept() ).isEqualTo( 10L );
        assertThat( clonedSelf.getValidUntilVersionId() ).isEqualTo( 200L );
        assertThat( clonedOther.getValidUntilVersionId() ).isEqualTo( 50L );
    }
}
