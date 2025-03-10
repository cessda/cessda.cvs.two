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
package eu.cessda.cvs.service.impl;

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.UserDetails;
import eu.cessda.cvs.service.VocabularyChangeService;
import eu.cessda.cvs.service.dto.UserDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.mapper.VocabularyMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.Math.abs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class VocabularyServiceImplTest
{

    // Random number generator
    private final Random random = new Random();

    @Test
    void shouldStoreChangeTypeFromSnippet()
    {
        // Generate positive random numbers for the vocabulary and the version
        long vocabularyId = abs( random.nextLong() );
        long versionId = abs( random.nextLong() );

        // Create the vocabulary snippet
        VocabularySnippet snippet = new VocabularySnippet();
        snippet.setChangeType( "edit" );
        snippet.setActionType( ActionType.EDIT_CV );
        snippet.setVocabularyId( vocabularyId );
        snippet.setVersionId( versionId );

        // Create a VersionDTO
        VersionDTO dto = new VersionDTO();
        dto.setId( versionId );
        dto.setVocabularyId( vocabularyId );
        dto.setLanguage( "en" );
        dto.setItemType( null );

        // Create a Vocabulary entity, this exists as an intermediary only
        Vocabulary vocabulary = new Vocabulary();

        // Create a VocabularyDTO to hold the VersionDTO
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        vocabularyDTO.setVersions( Collections.singleton( dto ) );
        vocabularyDTO.setId( vocabularyId );

        // Create mocks
        VocabularyChangeService changeService = mock( VocabularyChangeService.class );
        VocabularyRepository repository = mock( VocabularyRepository.class );
        VocabularyMapper mapper = mock( VocabularyMapper.class );

        when( repository.findById( vocabularyId ) ).thenReturn( Optional.of( vocabulary ) );
        when( mapper.toDto( vocabulary ) ).thenReturn( vocabularyDTO );
        when( mapper.toEntity( vocabularyDTO ) ).thenReturn( vocabulary );
        when( repository.save( vocabulary ) ).thenReturn( vocabulary );

        try
        {
            // Set security permissions
            SecurityContextHolder.getContext().setAuthentication( new Authentication()
            {
                private static final long serialVersionUID = -6715451985734174674L;

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities()
                {
                    return Collections.singleton( (GrantedAuthority) () -> "ROLE_ADMIN_CONTENT" );
                }

                @Override
                public Object getCredentials()
                {
                    return null;
                }

                @Override
                public Object getDetails()
                {
                    return null;
                }

                @Override
                public Object getPrincipal()
                {
                    UserDetails userDetails = new UserDetails( "user", "pass", Collections.emptyList() );
                    UserDTO userDTO = new UserDTO();
                    userDTO.setAuthorities( Collections.singleton( "ROLE_ADMIN_CONTENT" ) );
                    userDetails.setUser( userDTO );
                    return userDetails;
                }

                @Override
                public boolean isAuthenticated()
                {
                    return true;
                }

                @Override
                public void setAuthenticated( boolean isAuthenticated ) throws IllegalArgumentException
                {

                }

                @Override
                public String getName()
                {
                    return null;
                }
            } );

            // Run
            new VocabularyServiceImpl( null,
                null,
                null,
                null,
                null,
                changeService,
                repository,
                mapper,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            ).saveVocabulary( snippet );

            // Verify the vocabulary was updated
            Mockito.verify( changeService, times( 1 ) ).save( any( VocabularyChangeDTO.class ) );
        }
        finally
        {
            // Reset the security context
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void shouldThrowIfSlVersionNumberIsEmpty() {

        // Setup
        VocabularyServiceImpl vocabularyService = new VocabularyServiceImpl( null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );


        // Run
        assertThatThrownBy(() ->
            vocabularyService.getVocabularyByNotationAndVersion( null, "", false )
        ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void shouldGetPublishedCVPaths() throws IOException, URISyntaxException
    {
        // Setup
        URL content = Objects.requireNonNull( this.getClass().getResource( "/static/content" ) );
        ApplicationProperties applicationProperties = new ApplicationProperties( Path.of( content.toURI() ) );
        VocabularyServiceImpl vocabularyService = new VocabularyServiceImpl( null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            applicationProperties,
            null
        );

        // Run
        assertThat(vocabularyService.getPublishedCvPaths()).isNotEmpty()
            // All results should be files
            .allMatch( Files::isRegularFile );
    }
}
