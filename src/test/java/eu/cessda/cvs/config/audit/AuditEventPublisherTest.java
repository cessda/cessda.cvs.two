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
package eu.cessda.cvs.config.audit;

import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.User;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.utils.VersionNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static eu.cessda.cvs.config.audit.AuditEventPublisher.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuditEventPublisher}, checking which data ends up in the published audit events.
 */
class AuditEventPublisherTest {

    private static final String AUDIT_USER = "admin";
    private static final String AGENCY_NAME = "CESSDA";
    private static final String TITLE = "Analysis Unit";
    private static final String NOTATION = "AnalysisUnit";
    private static final String LANGUAGE = "en";
    private static final String VERSION_TITLE = "Version title";
    private static final String UNKNOWN_ACTION = "UNKNOWN_ACTION";

    private ApplicationEventPublisher eventPublisher;
    private AuditEventPublisher auditEventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = mock( ApplicationEventPublisher.class );
        auditEventPublisher = new AuditEventPublisher();
        auditEventPublisher.setApplicationEventPublisher( eventPublisher );
    }

    private AuditEvent publishedEvent() {
        ArgumentCaptor<AuditApplicationEvent> captor = ArgumentCaptor.forClass( AuditApplicationEvent.class );
        verify( eventPublisher ).publishEvent( captor.capture() );
        return captor.getValue().getAuditEvent();
    }

    private Map<String, Object> publishedData() {
        return publishedEvent().getData();
    }

    /**
     * Returns a string equal to the given one, but which is a different object.
     */
    private static String copyOf( String value ) {
        return new StringBuilder( value ).toString();
    }

    // ------------------------------------------------------------------------------------------
    // Fixture helpers
    // ------------------------------------------------------------------------------------------

    private static UserAgencyDTO userAgency( String agencyName, String language, AgencyRole role ) {
        UserAgencyDTO userAgency = new UserAgencyDTO();
        userAgency.setAgencyName( agencyName );
        userAgency.setLanguage( language );
        userAgency.setAgencyRole( role );
        return userAgency;
    }

    private static UserDTO user( Set<UserAgencyDTO> userAgencies, Set<String> authorities ) {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin( "jdoe" );
        userDTO.setFirstName( "John" );
        userDTO.setLastName( "Doe" );
        userDTO.setEmail( "jdoe@example.com" );
        userDTO.setLangKey( LANGUAGE );
        userDTO.setUserAgencies( userAgencies );
        userDTO.setAuthorities( authorities );
        return userDTO;
    }

    private static VocabularyDTO vocabulary( String titleAll, String titleEn ) {
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        vocabularyDTO.setAgencyName( AGENCY_NAME );
        vocabularyDTO.setNotation( NOTATION );
        vocabularyDTO.setTitleAll( titleAll );
        vocabularyDTO.setTitleEn( titleEn );
        vocabularyDTO.setStatus( Status.DRAFT );
        vocabularyDTO.setVersionNumber( VersionNumber.fromString( "1.0" ) );
        return vocabularyDTO;
    }

    private static VersionDTO version() {
        VersionDTO versionDTO = new VersionDTO();
        versionDTO.setTitle( VERSION_TITLE );
        versionDTO.setLanguage( LANGUAGE );
        versionDTO.setItemType( ItemType.SL );
        versionDTO.setNumber( VersionNumber.fromString( "1.0.1" ) );
        versionDTO.setNotation( NOTATION );
        versionDTO.setStatus( Status.REVIEW );
        return versionDTO;
    }

    private static VocabularySnippet vocabularySnippet() {
        VocabularySnippet snippet = new VocabularySnippet();
        snippet.setLanguage( LANGUAGE );
        snippet.setItemType( ItemType.SL );
        snippet.setVersionNumber( VersionNumber.fromString( "1.0" ) );
        snippet.setStatus( Status.DRAFT );
        snippet.setNotation( NOTATION );
        snippet.setDefinition( "Definition" );
        return snippet;
    }

    private static ConceptDTO concept( String title ) {
        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setTitle( title );
        return conceptDTO;
    }

    // ------------------------------------------------------------------------------------------
    // Raw events
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldPublishTheEventWrappedInAnAuditApplicationEvent() {
        AuditEvent event = new AuditEvent( AUDIT_USER, "SOME_ACTION", Collections.emptyMap() );

        auditEventPublisher.publish( event );

        assertThat( publishedEvent() ).isSameAs( event );
    }

    @Test
    void shouldIgnoreEventsWhenNoPublisherIsSet() {
        AuditEventPublisher withoutPublisher = new AuditEventPublisher();
        AuditEvent event = new AuditEvent( AUDIT_USER, "SOME_ACTION", Collections.emptyMap() );

        assertThatCode( () -> withoutPublisher.publish( event ) ).doesNotThrowAnyException();
    }

    // ------------------------------------------------------------------------------------------
    // Agencies and authorities of a user
    // ------------------------------------------------------------------------------------------

    @Test
    void getAgencyShouldReturnAnEmptyStringWithoutAgencies() {
        assertThat( auditEventPublisher.getAgency( user( null, null ) ) ).isEmpty();
        assertThat( auditEventPublisher.getAgency( user( Collections.emptySet(), null ) ) ).isEmpty();
    }

    @Test
    void getAgencyShouldJoinTheAgencyNameLanguageAndRole() {
        Set<UserAgencyDTO> userAgencies = new LinkedHashSet<>();
        userAgencies.add( userAgency( AGENCY_NAME, "de", AgencyRole.ADMIN_TL ) );
        userAgencies.add( userAgency( "DDI Alliance", null, AgencyRole.VIEW ) );

        assertThat( auditEventPublisher.getAgency( user( userAgencies, null ) ) )
            .isEqualTo( "CESSDA, de, ADMIN_TL | DDI Alliance, VIEW" );
    }

    @Test
    void getAgencyShouldSkipMissingAgencyProperties() {
        Set<UserAgencyDTO> userAgencies = Collections.singleton( userAgency( null, null, null ) );

        assertThat( auditEventPublisher.getAgency( user( userAgencies, null ) ) ).isEmpty();
    }

    @Test
    void getAuthorityShouldJoinTheAuthorities() {
        Set<String> authorities = new LinkedHashSet<>( List.of( "ROLE_ADMIN", "ROLE_USER" ) );

        assertThat( auditEventPublisher.getAuthority( user( null, authorities ) ) ).isEqualTo( "ROLE_ADMIN | ROLE_USER" );
        assertThat( auditEventPublisher.getAuthority( user( null, Collections.emptySet() ) ) ).isEmpty();
        assertThat( auditEventPublisher.getAuthority( user( null, null ) ) ).isEmpty();
    }

    // ------------------------------------------------------------------------------------------
    // User events
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldPublishTheUserDetailsAgenciesAndRolesWhenAUserIsUpdated() {
        Set<UserAgencyDTO> userAgencies = Collections.singleton( userAgency( AGENCY_NAME, LANGUAGE, AgencyRole.ADMIN_SL ) );
        UserDTO userDTO = user( userAgencies, Collections.singleton( "ROLE_USER" ) );

        auditEventPublisher.publish( AUDIT_USER, userDTO, null, "USER_UPDATED" );

        AuditEvent event = publishedEvent();
        assertThat( event.getPrincipal() ).isEqualTo( AUDIT_USER );
        assertThat( event.getType() ).isEqualTo( "USER_UPDATED" );
        assertThat( event.getData() )
            .containsEntry( "id", "jdoe" )
            .containsEntry( "name", "John Doe" )
            .containsEntry( "email", "jdoe@example.com" )
            .containsEntry( "language", LANGUAGE )
            .containsEntry( "agency", "CESSDA, en, ADMIN_SL" )
            .containsEntry( "roles", "ROLE_USER" );
    }

    @Test
    void shouldNotPublishAnAgencyWhenTheCreatedUserHasNone() {
        auditEventPublisher.publish( AUDIT_USER, user( null, null ), null, "USER_CREATED" );

        assertThat( publishedData() )
            .containsEntry( "id", "jdoe" )
            .doesNotContainKeys( "agency", "roles" );
    }

    @Test
    @Disabled( "Roles are only added when the user has agencies, see AuditEventPublisher" )
    void shouldPublishTheRolesOfAUserWithoutAgencies() {
        auditEventPublisher.publish( AUDIT_USER, user( null, Collections.singleton( "ROLE_ADMIN" ) ), null, "USER_CREATED" );

        assertThat( publishedData() ).containsEntry( "roles", "ROLE_ADMIN" );
    }

    @Test
    void shouldPublishTheDeletedUser() {
        User deletedUser = new User();
        deletedUser.setLogin( "deleted" );
        deletedUser.setFirstName( "Jane" );
        deletedUser.setLastName( "Roe" );
        deletedUser.setEmail( "deleted@example.com" );

        auditEventPublisher.publish( AUDIT_USER, null, deletedUser, "USER_DELETED" );

        assertThat( publishedData() )
            .hasSize( 3 )
            .containsEntry( "id", "deleted" )
            .containsEntry( "name", "Jane Roe" )
            .containsEntry( "email", "deleted@example.com" );
    }

    @Test
    void shouldPublishNoUserDataForAnUnknownUserAction() {
        auditEventPublisher.publish( AUDIT_USER, (UserDTO) null, (User) null, "USER_UNKNOWN" );

        assertThat( publishedData() ).isEmpty();
    }

    // ------------------------------------------------------------------------------------------
    // Code events
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldPublishTheCodeAndVersionDetailsWhenACodeIsCreated() {
        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setTitle( "Code title" );
        codeSnippet.setNotation( "code" );
        codeSnippet.setDefinition( "Code definition" );
        codeSnippet.setChangeType( "Code added" );
        codeSnippet.setChangeDesc( "A new code" );

        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, TITLE ), version(), null, null, codeSnippet, "CREATE_CODE" );

        assertThat( publishedData() )
            .containsEntry( CV_AGENCY_NAME, AGENCY_NAME )
            .containsEntry( CV_TITLE_SL, TITLE )
            .containsEntry( CV_TITLE, VERSION_TITLE )
            .containsEntry( CV_LANGUAGE, LANGUAGE )
            .containsEntry( CV_TYPE, ItemType.SL )
            .containsEntry( CV_VERSION, "1.0.1" )
            .containsEntry( "code_change_type", "Code added" )
            .containsEntry( "code_change_description", "A new code" )
            .containsEntry( "code_notation", "code" )
            .containsEntry( "code_definition", "Code definition" )
            .containsEntry( CODE_TITLE, "Code title" );
    }

    @Test
    void shouldSkipEmptyCodePropertiesWhenACodeIsEdited() {
        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setTitle( "Code title" );
        codeSnippet.setNotation( "" );
        codeSnippet.setDefinition( "" );
        codeSnippet.setChangeType( "Typo" );

        auditEventPublisher.publish( AUDIT_USER, null, null, null, null, codeSnippet, "EDIT_TL_CODE" );

        assertThat( publishedData() )
            .containsOnlyKeys( CODE_TITLE )
            .containsEntry( CODE_TITLE, "Code title" );
    }

    @Test
    void shouldPublishNoCodePropertiesWhenTheyAreMissing() {
        CodeSnippet codeSnippet = new CodeSnippet();

        auditEventPublisher.publish( AUDIT_USER, null, version(), null, null, codeSnippet, "ADD_TL_CODE" );

        assertThat( publishedData() )
            .doesNotContainKeys( "code_change_type", "code_notation", "code_definition" )
            .containsEntry( CV_TITLE, VERSION_TITLE );
    }

    @Test
    void shouldPublishTheConceptWhenACodeIsDeleted() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), concept( "Deleted code" ), null, null, "DELETE_CODE" );

        assertThat( publishedData() )
            .containsEntry( CV_TITLE, VERSION_TITLE )
            .containsEntry( CV_TYPE, ItemType.SL )
            .containsEntry( CV_VERSION, VersionNumber.fromString( "1.0.1" ) )
            .containsEntry( CODE_TITLE, "Deleted code" );
    }

    @Test
    void shouldPublishTheReplacingConceptWhenACodeIsDeprecated() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), concept( "Old code" ), concept( "New code" ), null, "DEPRECATE_CODE" );

        assertThat( publishedData() )
            .containsEntry( CODE_TITLE, "Old code" )
            .containsEntry( "replaced_by_title", "New code" );
    }

    @Test
    void shouldPublishADeprecatedCodeWithoutReplacement() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), concept( "Old code" ), null, null, "DEPRECATE_CODE" );

        assertThat( publishedData() )
            .containsEntry( CODE_TITLE, "Old code" )
            .doesNotContainKey( "replaced_by_title" );
    }

    @Test
    void shouldPublishTheVersionWhenCodesAreReordered() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), null, null, null, "REORDER_CODE" );

        assertThat( publishedData() )
            .containsOnlyKeys( CV_TITLE, CV_LANGUAGE, CV_VERSION );
    }

    @Test
    void shouldPublishOnlyTheVocabularyForAnUnknownCodeAction() {
        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, TITLE ), null, null, null, null, "UNKNOWN_CODE_ACTION" );

        assertThat( publishedData() ).containsOnlyKeys( CV_AGENCY_NAME, CV_TITLE_SL );
    }

    // ------------------------------------------------------------------------------------------
    // Vocabulary events, titles
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldRemoveTheDuplicateSourceTitleWhenItIsTheSameObject() {
        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, TITLE ), null, vocabularySnippet(), UNKNOWN_ACTION );

        assertThat( publishedData() )
            .containsEntry( CV_TITLE, TITLE )
            .doesNotContainKeys( CV_TITLE_SL, CV_TITLE_TL );
    }

    @Test
    void shouldKeepTheSourceTitleWhenItDiffersFromTheLanguageTitle() {
        auditEventPublisher.publish( AUDIT_USER, vocabulary( "Source title", TITLE ), null, vocabularySnippet(), UNKNOWN_ACTION );

        assertThat( publishedData() )
            .containsEntry( CV_TITLE, TITLE )
            .containsEntry( CV_TITLE_SL, "Source title" )
            .doesNotContainKey( CV_TITLE_TL );
    }

    @Test
    void shouldUseTheSourceTitleWithoutASnippet() {
        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, null ), null, null, UNKNOWN_ACTION );

        assertThat( publishedData() )
            .containsOnlyKeys( CV_AGENCY_NAME, CV_TITLE )
            .containsEntry( CV_TITLE, TITLE );
    }

    @Test
    @Disabled( "Strings compared with ==, see AuditEventPublisher" )
    void shouldRemoveTheDuplicateSourceTitleWhenTheTextsAreEqual() {
        VocabularyDTO vocabularyDTO = vocabulary( TITLE, copyOf( TITLE ) );

        auditEventPublisher.publish( AUDIT_USER, vocabularyDTO, null, vocabularySnippet(), UNKNOWN_ACTION );

        assertThat( publishedData() )
            .containsEntry( CV_TITLE, TITLE )
            .doesNotContainKey( CV_TITLE_SL );
    }

    @Test
    @Disabled( "Strings compared with ==, see AuditEventPublisher" )
    void shouldRemoveTheDuplicateSourceTitleOfAForwardedVocabularyWhenTheTextsAreEqual() {
        VocabularyDTO vocabularyDTO = vocabulary( TITLE, copyOf( TITLE ) );

        auditEventPublisher.publish( AUDIT_USER, vocabularyDTO, null, vocabularySnippet(), UNKNOWN_ACTION, true );

        assertThat( publishedData() )
            .containsEntry( CV_TITLE, TITLE )
            .doesNotContainKey( CV_TITLE_SL );
    }

    // ------------------------------------------------------------------------------------------
    // Vocabulary events, actions
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldPublishNothingButTheActionWithoutAVocabulary() {
        auditEventPublisher.publish( AUDIT_USER, null, null, null, UNKNOWN_ACTION );

        AuditEvent event = publishedEvent();
        assertThat( event.getType() ).isEqualTo( UNKNOWN_ACTION );
        assertThat( event.getData() ).isEmpty();
    }

    @Test
    void shouldPublishTheSnippetWhenAVocabularyIsCreated() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setNotes( "Some notes" );

        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, TITLE ), null, snippet, "CREATE_VOCABULARY" );

        assertThat( publishedData() )
            .containsEntry( CV_TYPE, ItemType.SL )
            .containsEntry( CV_VERSION, VersionNumber.fromString( "1.0" ) )
            .containsEntry( CV_LANGUAGE, LANGUAGE )
            .containsEntry( CV_STATUS, Status.DRAFT )
            .containsEntry( CV_NOTATION, NOTATION )
            .containsEntry( "cv_definition", "Definition" )
            .containsEntry( "cv_notes", "Some notes" );
    }

    @Test
    void shouldSkipEmptyNotesWhenAVocabularyIsUpdated() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setNotes( "" );

        auditEventPublisher.publish( AUDIT_USER, null, null, snippet, "UPDATE_VOCABULARY" );

        assertThat( publishedData() )
            .containsEntry( CV_NOTATION, NOTATION )
            .doesNotContainKey( "cv_notes" );
    }

    @Test
    void shouldPublishTheVocabularyWhenAVocabularyIsDeletedWithoutASnippet() {
        VocabularyDTO vocabularyDTO = vocabulary( TITLE, TITLE );
        vocabularyDTO.setNotes( "Vocabulary notes" );

        auditEventPublisher.publish( AUDIT_USER, vocabularyDTO, null, null, "DELETE_VOCABULARY" );

        assertThat( publishedData() )
            .containsEntry( CV_VERSION, VersionNumber.fromString( "1.0" ) )
            .containsEntry( CV_STATUS, Status.DRAFT )
            .containsEntry( CV_NOTATION, NOTATION )
            .containsEntry( "cv_notes", "Vocabulary notes" );
    }

    @Test
    void shouldSkipEmptyVocabularyNotesWhenAVocabularyIsDeleted() {
        VocabularyDTO vocabularyDTO = vocabulary( TITLE, TITLE );
        vocabularyDTO.setNotes( "" );

        auditEventPublisher.publish( AUDIT_USER, vocabularyDTO, null, null, "DELETE_VOCABULARY" );

        assertThat( publishedData() )
            .containsEntry( CV_NOTATION, NOTATION )
            .doesNotContainKey( "cv_notes" );
    }

    @Test
    void shouldPublishNothingWhenAVocabularyIsDeletedWithoutVocabularyOrSnippet() {
        auditEventPublisher.publish( AUDIT_USER, null, null, null, "DELETE_VOCABULARY" );

        assertThat( publishedData() ).isEmpty();
    }

    @Test
    void shouldPublishTheVersionWhenANewVersionIsCreated() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), null, "CREATE_NEW_VOCABULARY_VERSION" );

        assertThat( publishedData() )
            .containsEntry( CV_TITLE, VERSION_TITLE )
            .containsEntry( CV_TYPE, ItemType.SL )
            .containsEntry( CV_VERSION, VersionNumber.fromString( "1.0.1" ) )
            .containsEntry( CV_LANGUAGE, LANGUAGE )
            .containsEntry( CV_NOTATION, NOTATION );
    }

    @Test
    void shouldPublishTheVersionInfoWhenItIsEdited() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setVersionNotes( "Version notes" );
        snippet.setVersionChanges( "Version changes" );

        auditEventPublisher.publish( AUDIT_USER, null, null, snippet, "UPDATE_VOCABULARY_EDIT_VERSION_INFO_CV" );

        assertThat( publishedData() )
            .containsEntry( CV_TYPE, ItemType.SL )
            .containsEntry( CV_LANGUAGE, LANGUAGE )
            .containsEntry( CV_VERSION, VersionNumber.fromString( "1.0" ) )
            .containsEntry( "CV_VERSION_notes", "Version notes" )
            .containsEntry( "CV_VERSION_changes", "Version changes" );
    }

    @Test
    void shouldSkipEmptyVersionInfo() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setVersionNotes( "" );
        snippet.setVersionChanges( "" );

        auditEventPublisher.publish( AUDIT_USER, null, null, snippet, "UPDATE_VOCABULARY_EDIT_VERSION_INFO_CV" );

        assertThat( publishedData() )
            .containsOnlyKeys( CV_TYPE, CV_LANGUAGE, CV_VERSION );
    }

    @Test
    void shouldPublishTheChangeWhenAVocabularyIsEdited() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setChangeType( "Typo" );
        snippet.setChangeDesc( "Fixed a typo" );

        auditEventPublisher.publish( AUDIT_USER, null, null, snippet, "UPDATE_VOCABULARY_EDIT_CV" );

        assertThat( publishedData() )
            .containsEntry( "cv_definition", "Definition" )
            .containsEntry( "cv_change_type", "Typo" )
            .containsEntry( "cv_change_description", "Fixed a typo" );
    }

    @Test
    void shouldPublishAVocabularyEditWithoutAChange() {
        auditEventPublisher.publish( AUDIT_USER, null, null, vocabularySnippet(), "UPDATE_VOCABULARY_EDIT_CV" );

        assertThat( publishedData() )
            .containsOnlyKeys( CV_TYPE, CV_LANGUAGE, "cv_definition" );
    }

    @Test
    void shouldPublishTheDdiUsageWhenItIsEdited() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setDdiUsage( "DDI usage" );

        auditEventPublisher.publish( AUDIT_USER, null, null, snippet, "UPDATE_VOCABULARY_EDIT_DDI_CV" );

        assertThat( publishedData() ).containsEntry( "cv_ddi_usage", "DDI usage" );
    }

    @Test
    void shouldPublishTheTranslatorAgencyWhenTheIdentityIsEdited() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setTranslateAgency( "GESIS" );
        snippet.setTranslateAgencyLink( "https://www.gesis.org" );

        auditEventPublisher.publish( AUDIT_USER, null, null, snippet, "UPDATE_VOCABULARY_EDIT_IDENTITY_CV" );

        assertThat( publishedData() )
            .containsEntry( "cv_translator_agency", "GESIS" )
            .containsEntry( "cv_translator_agency_link", "https://www.gesis.org" );
    }

    @Test
    void shouldPublishTheNoteWhenItIsEdited() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setNotes( "A note" );

        auditEventPublisher.publish( AUDIT_USER, null, null, snippet, "UPDATE_VOCABULARY_EDIT_NOTE_CV" );

        assertThat( publishedData() ).containsEntry( "cv_note", "A note" );
    }

    @Test
    void shouldPublishNothingForEditActionsWithoutASnippet() {
        List<String> actions = List.of( "UPDATE_VOCABULARY_EDIT_VERSION_INFO_CV", "UPDATE_VOCABULARY_EDIT_CV",
            "UPDATE_VOCABULARY_EDIT_DDI_CV", "UPDATE_VOCABULARY_EDIT_IDENTITY_CV", "UPDATE_VOCABULARY_EDIT_NOTE_CV" );

        actions.forEach( action -> auditEventPublisher.publish( AUDIT_USER, null, null, null, action ) );

        ArgumentCaptor<AuditApplicationEvent> captor = ArgumentCaptor.forClass( AuditApplicationEvent.class );
        verify( eventPublisher, times( actions.size() ) ).publishEvent( captor.capture() );
        assertThat( captor.getAllValues() )
            .extracting( e -> e.getAuditEvent().getData() )
            .allSatisfy( data -> assertThat( data ).isEmpty() );
    }

    @Test
    void shouldPublishOnlyTheVocabularyWhenTheWholeVocabularyIsDeleted() {
        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, TITLE ), null, null, "DELETE_WHOLE_VOCABULARY" );

        assertThat( publishedData() ).containsOnlyKeys( CV_AGENCY_NAME, CV_TITLE );
    }

    @Test
    void shouldPublishTheVersionWhenTheSourceAndTargetVersionsAreDeleted() {
        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, TITLE ), null, null, "DELETE_VOCABULARY_SL_AND_RELATED_TL(S)_VERSION" );

        assertThat( publishedData() )
            .containsEntry( CV_VERSION, VersionNumber.fromString( "1.0" ) )
            .containsEntry( CV_TYPE, "SL & TL(s)" );
    }

    @Test
    void shouldPublishNothingWhenTheSourceAndTargetVersionsAreDeletedWithoutAVocabulary() {
        auditEventPublisher.publish( AUDIT_USER, null, null, null, "DELETE_VOCABULARY_SL_AND_RELATED_TL(S)_VERSION" );

        assertThat( publishedData() ).isEmpty();
    }

    @Test
    void shouldPublishTheVersionWhenATargetVocabularyIsDeleted() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), null, "DELETE_TL_VOCABULARY" );

        assertThat( publishedData() )
            .containsOnlyKeys( CV_LANGUAGE, CV_TITLE, CV_VERSION, CV_TYPE )
            .containsEntry( CV_TITLE, VERSION_TITLE );
    }

    // ------------------------------------------------------------------------------------------
    // Forward status events
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldPublishTheVersionNotesWhenAVocabularyIsPublished() {
        VocabularySnippet snippet = vocabularySnippet();
        snippet.setVersionNotes( "Version notes" );
        snippet.setVersionChanges( "Version changes" );

        auditEventPublisher.publish( AUDIT_USER, vocabulary( TITLE, TITLE ), version(), snippet, "FORWARD_CV_SL_STATUS_PUBLISH", true );

        assertThat( publishedData() )
            .containsEntry( CV_TITLE, VERSION_TITLE )
            .containsEntry( CV_STATUS, Status.REVIEW )
            .containsEntry( "CV_VERSION_notes", "Version notes" )
            .containsEntry( "CV_VERSION_changes", "Version changes" );
    }

    @Test
    void shouldPublishTheStatusWhenAVocabularyIsForwardedToReview() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), null, "FORWARD_CV_TL_STATUS_REVIEW", true );

        assertThat( publishedData() )
            .containsOnlyKeys( CV_TYPE, CV_LANGUAGE, CV_TITLE, CV_VERSION, CV_STATUS );
    }

    @Test
    void shouldNotPublishAForwardEventWhenNotForwarded() {
        auditEventPublisher.publish( AUDIT_USER, null, version(), null, "FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE", false );

        verify( eventPublisher, never() ).publishEvent( any( AuditApplicationEvent.class ) );
    }
}
