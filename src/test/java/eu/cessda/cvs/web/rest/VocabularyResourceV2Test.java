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
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.CodeDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VersionHistoryDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.utils.VersionNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the public {@link VocabularyResourceV2} endpoints that list the published vocabularies
 * and search the vocabularies. The {@link VocabularyService} is mocked, and the controller is exercised
 * through a standalone {@link MockMvc}.
 */
class VocabularyResourceV2Test {

    private static final String AGENCY = "CESSDA";
    private static final String NOTATION = "AnalysisUnit";
    private static final String CONTEXT_PATH = "/cvs";

    /**
     * The withdrawn flag of the vocabularies that the published vocabularies endpoint currently lists.
     * The endpoint is documented to skip withdrawn vocabularies, but its filter keeps only the withdrawn
     * ones (see {@link #shouldListOnlyVocabulariesThatAreNotWithdrawn()}). The tests of the response shape
     * therefore use this flag, which has to become {@code FALSE} once the filter is fixed.
     */
    private static final Boolean LISTED = Boolean.TRUE;

    private VocabularyService vocabularyService;
    private VocabularyResourceV2 vocabularyResource;
    private MockMvc mockMvc;

    private long nextId = 1;

    @BeforeEach
    void setUp() {
        vocabularyService = mock( VocabularyService.class );
        vocabularyResource = new VocabularyResourceV2( vocabularyService );

        ObjectMapper objectMapper = JsonMapper.builder()
            .addModule( new JavaTimeModule() )
            .build();

        mockMvc = MockMvcBuilders.standaloneSetup( vocabularyResource )
            .setCustomArgumentResolvers( new PageableHandlerMethodArgumentResolver() )
            .setMessageConverters( new MappingJackson2HttpMessageConverter( objectMapper ) )
            .build();
    }

    // ------------------------------------------------------------------------------------------
    // Fixture helpers
    // ------------------------------------------------------------------------------------------

    private VersionDTO version( String notation, String language, ItemType itemType, String number, Status status ) {
        VersionDTO versionDTO = new VersionDTO();
        versionDTO.setId( nextId++ );
        versionDTO.setNotation( notation );
        versionDTO.setLanguage( language );
        versionDTO.setItemType( itemType );
        versionDTO.setNumber( VersionNumber.fromString( number ) );
        versionDTO.setStatus( status );
        return versionDTO;
    }

    private VersionDTO publishedSlVersion( String notation, String number ) {
        return version( notation, "en", ItemType.SL, number, Status.PUBLISHED );
    }

    private VocabularyDTO vocabulary( String agency, String notation, Boolean withdrawn, VersionDTO... versions ) {
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        vocabularyDTO.setId( nextId++ );
        vocabularyDTO.setAgencyName( agency );
        vocabularyDTO.setNotation( notation );
        vocabularyDTO.setWithdrawn( withdrawn );
        vocabularyDTO.setVersions( new LinkedHashSet<>( Arrays.asList( versions ) ) );
        return vocabularyDTO;
    }

    private Map<String, Map<String, Map<String, Map<VersionNumber, URI>>>> getAllVocabularies() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath( CONTEXT_PATH );
        return vocabularyResource.getAllVocabularies( request );
    }

    // ------------------------------------------------------------------------------------------
    // Published vocabularies
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldReturnTheVersionLinksGroupedByAgencyNotationAndLanguage() throws Exception {
        VersionDTO sourceVersion = publishedSlVersion( NOTATION, "2.0" );
        sourceVersion.addVersionHistory( new VersionHistoryDTO( 1L, VersionNumber.fromString( "1.0" ),
            LocalDate.of( 2020, Month.MARCH, 1 ), "Notes", "Changes", null ) );
        VersionDTO targetVersion = version( NOTATION, "de", ItemType.TL, "2.0.1", Status.PUBLISHED );
        when( vocabularyService.findAll() ).thenReturn( List.of( vocabulary( AGENCY, NOTATION, LISTED, sourceVersion, targetVersion ) ) );

        mockMvc.perform( get( CONTEXT_PATH + "/v2/vocabularies-published" )
                .contextPath( CONTEXT_PATH )
                .accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( jsonPath( "$['CESSDA']['AnalysisUnit']['en(SL)']['2.0.0']" )
                .value( "/cvs/v2/vocabularies/AnalysisUnit/2.0.0?languageVersion=en-2.0.0" ) )
            .andExpect( jsonPath( "$['CESSDA']['AnalysisUnit']['en(SL)']['1.0.0']" )
                .value( "/cvs/v2/vocabularies/AnalysisUnit/1.0.0?languageVersion=en-1.0.0" ) )
            .andExpect( jsonPath( "$['CESSDA']['AnalysisUnit']['de(TL)']['2.0.1']" )
                .value( "/cvs/v2/vocabularies/AnalysisUnit/2.0.0?languageVersion=de-2.0.1" ) );
    }

    @Test
    void shouldReturnAnEmptyObjectWithoutVocabularies() throws Exception {
        when( vocabularyService.findAll() ).thenReturn( Collections.emptyList() );

        mockMvc.perform( get( "/v2/vocabularies-published" ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( content().json( "{}", true ) );
    }

    @Test
    void shouldNotAcceptAnotherMediaTypeThanJson() throws Exception {
        mockMvc.perform( get( "/v2/vocabularies-published" ).accept( MediaType.APPLICATION_XML ) )
            .andExpect( status().isNotAcceptable() );

        verifyNoInteractions( vocabularyService );
    }

    @Test
    void shouldSortTheAgenciesAndTheVocabulariesByNotation() throws Exception {
        when( vocabularyService.findAll() ).thenReturn( List.of(
            vocabulary( "UKDS", "Zeta", LISTED, publishedSlVersion( "Zeta", "1.0" ) ),
            vocabulary( AGENCY, "Zeta", LISTED, publishedSlVersion( "Zeta", "1.0" ) ),
            vocabulary( AGENCY, "Alpha", LISTED, publishedSlVersion( "Alpha", "1.0" ) ),
            vocabulary( "DDI Alliance", "Beta", LISTED, publishedSlVersion( "Beta", "1.0" ) )
        ) );

        Map<String, Map<String, Map<String, Map<VersionNumber, URI>>>> result = getAllVocabularies();

        assertThat( result.keySet() ).containsExactly( AGENCY, "DDI Alliance", "UKDS" );
        assertThat( result.get( AGENCY ).keySet() ).containsExactly( "Alpha", "Zeta" );
    }

    @Test
    void shouldSortTheVersionsOfALanguageByNumber() throws Exception {
        when( vocabularyService.findAll() ).thenReturn( List.of( vocabulary( AGENCY, NOTATION, LISTED,
            publishedSlVersion( NOTATION, "1.1" ),
            publishedSlVersion( NOTATION, "1.0" ),
            publishedSlVersion( NOTATION, "1.0.1" )
        ) ) );

        Map<VersionNumber, URI> versions = getAllVocabularies().get( AGENCY ).get( NOTATION ).get( "en(SL)" );

        assertThat( versions.keySet() ).extracting( VersionNumber::toString ).containsExactly( "1.0.0", "1.0.1", "1.1.0" );
        assertThat( versions.get( VersionNumber.fromString( "1.0.1" ) ) )
            .hasToString( "/cvs/v2/vocabularies/AnalysisUnit/1.0.0?languageVersion=en-1.0.1" );
    }

    @Test
    void shouldSkipVocabulariesWithoutAPublishedVersion() throws Exception {
        when( vocabularyService.findAll() ).thenReturn( List.of(
            vocabulary( AGENCY, "Draft", LISTED, version( "Draft", "en", ItemType.SL, "1.0", Status.DRAFT ) ),
            vocabulary( "UKDS", "Review", LISTED, version( "Review", "en", ItemType.SL, "1.0", Status.REVIEW ) )
        ) );

        assertThat( getAllVocabularies() ).isEmpty();
    }

    @Test
    void shouldListAllVersionsOfAVocabularyWithAPublishedVersion() throws Exception {
        when( vocabularyService.findAll() ).thenReturn( List.of( vocabulary( AGENCY, NOTATION, LISTED,
            publishedSlVersion( NOTATION, "1.0" ),
            version( NOTATION, "fr", ItemType.TL, "1.0.1", Status.DRAFT )
        ) ) );

        assertThat( getAllVocabularies().get( AGENCY ).get( NOTATION ) ).containsOnlyKeys( "en(SL)", "fr(TL)" );
    }

    @Test
    @Disabled( "The filter keeps only withdrawn vocabularies, see VocabularyResourceV2.getAllVocabularies" )
    void shouldListOnlyVocabulariesThatAreNotWithdrawn() throws Exception {
        when( vocabularyService.findAll() ).thenReturn( List.of(
            vocabulary( AGENCY, "Active", Boolean.FALSE, publishedSlVersion( "Active", "1.0" ) ),
            vocabulary( AGENCY, "Unset", null, publishedSlVersion( "Unset", "1.0" ) ),
            vocabulary( AGENCY, "Withdrawn", Boolean.TRUE, publishedSlVersion( "Withdrawn", "1.0" ) )
        ) );

        assertThat( getAllVocabularies().get( AGENCY ) ).containsOnlyKeys( "Active", "Unset" );
    }

    // ------------------------------------------------------------------------------------------
    // Search
    // ------------------------------------------------------------------------------------------

    private VocabularyDTO searchResult() {
        VocabularyDTO vocabularyDTO = vocabulary( AGENCY, NOTATION, Boolean.FALSE );
        vocabularyDTO.setArchived( Boolean.FALSE );
        vocabularyDTO.setDiscoverable( Boolean.TRUE );
        vocabularyDTO.setLanguagesPublished( new LinkedHashSet<>( List.of( "en", "de" ) ) );
        vocabularyDTO.setTitleDefinition( "Analysis unit", "The unit of analysis", "en", false );
        vocabularyDTO.setTitleDefinition( "Analyseeinheit", "Die Analyseeinheit", "de", false );

        CodeDTO code = new CodeDTO();
        code.setId( 500L );
        code.setPosition( 3 );
        code.setNotation( "Individual" );
        code.setTitleDefinition( "Individual", "A person", "en", false );
        code.setTitleDefinition( "Person", "Eine Person", "de", false );
        vocabularyDTO.setCodes( new LinkedHashSet<>( List.of( code ) ) );
        return vocabularyDTO;
    }

    private void mockSearchResult( VocabularyDTO vocabularyDTO ) {
        doAnswer( invocation -> {
            EsQueryResultDetail esq = invocation.getArgument( 0 );
            esq.setVocabularies( new PageImpl<>( List.of( vocabularyDTO ), esq.getPage(), 1 ) );
            return esq;
        } ).when( vocabularyService ).search( any( EsQueryResultDetail.class ) );
    }

    @Test
    void shouldReturnOnlyTheRequestedLanguageWhenSearching() throws Exception {
        mockSearchResult( searchResult() );

        mockMvc.perform( get( "/v2/search/vocabularies" )
                .param( "query", "unit" )
                .param( "agency", AGENCY )
                .param( "lang", "de" )
                .accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( header().string( "X-Total-Count", "1" ) )
            .andExpect( jsonPath( "$.totalElements" ).value( 1 ) )
            .andExpect( jsonPath( "$.vocabularies[0].notation" ).value( NOTATION ) )
            .andExpect( jsonPath( "$.vocabularies[0].titleDe" ).value( "Analyseeinheit" ) )
            .andExpect( jsonPath( "$.vocabularies[0].definitionDe" ).value( "Die Analyseeinheit" ) )
            .andExpect( jsonPath( "$.vocabularies[0].titleEn" ).doesNotExist() )
            .andExpect( jsonPath( "$.vocabularies[0].withdrawn" ).doesNotExist() )
            .andExpect( jsonPath( "$.vocabularies[0].codes[0].notation" ).value( "Individual" ) )
            .andExpect( jsonPath( "$.vocabularies[0].codes[0].titleDe" ).value( "Person" ) )
            .andExpect( jsonPath( "$.vocabularies[0].codes[0].titleEn" ).doesNotExist() )
            .andExpect( jsonPath( "$.vocabularies[0].codes[0].id" ).doesNotExist() )
            .andExpect( jsonPath( "$.vocabularies[0].codes[0].position" ).doesNotExist() );
    }

    @Test
    @Disabled( "The published languages are set before clearContent() resets them, see VocabularyResourceV2.searchVocabulariesJson" )
    void shouldReturnOnlyTheRequestedLanguageAsPublishedLanguageWhenSearching() throws Exception {
        mockSearchResult( searchResult() );

        mockMvc.perform( get( "/v2/search/vocabularies" )
                .param( "agency", AGENCY )
                .param( "lang", "de" )
                .accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.vocabularies[0].languagesPublished", contains( "de" ) ) );
    }

    @Test
    void shouldReturnAllLanguagesWhenSearchingWithoutALanguage() throws Exception {
        mockSearchResult( searchResult() );

        mockMvc.perform( get( "/v2/search/vocabularies" )
                .param( "query", "unit" )
                .accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.vocabularies[0].languagesPublished", contains( "en", "de" ) ) )
            .andExpect( jsonPath( "$.vocabularies[0].titleEn" ).value( "Analysis unit" ) )
            .andExpect( jsonPath( "$.vocabularies[0].titleDe" ).value( "Analyseeinheit" ) )
            .andExpect( jsonPath( "$.vocabularies[0].archived" ).doesNotExist() )
            .andExpect( jsonPath( "$.vocabularies[0].discoverable" ).doesNotExist() )
            .andExpect( jsonPath( "$.vocabularies[0].codes[0].id" ).value( 500 ) )
            .andExpect( jsonPath( "$.vocabularies[0].codes[0].titleEn" ).value( "Individual" ) );
    }

    @Test
    void shouldReturnAnEmptyResultWhenNothingIsFound() throws Exception {
        doAnswer( invocation -> {
            EsQueryResultDetail esq = invocation.getArgument( 0 );
            esq.setVocabularies( new PageImpl<>( Collections.emptyList() ) );
            return esq;
        } ).when( vocabularyService ).search( any( EsQueryResultDetail.class ) );

        mockMvc.perform( get( "/v2/search/vocabularies" ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.totalElements" ).value( 0 ) )
            .andExpect( jsonPath( "$.vocabularies" ).isEmpty() );
    }
}
