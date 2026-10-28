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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.cessda.cvs.config.audit.AuditEventPublisher;
import eu.cessda.cvs.domain.User;
import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.repository.UserAgencyRepository;
import eu.cessda.cvs.repository.UserRepository;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.UserDetails;
import eu.cessda.cvs.service.MailService;
import eu.cessda.cvs.service.UserAgencyService;
import eu.cessda.cvs.service.UserService;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.dto.UserDTO;
import eu.cessda.cvs.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link UserResource#updateUser}, the administrator endpoint that updates a user.
 * All collaborators are mocked and the controller is exercised through a standalone {@link MockMvc}
 * with the application {@link ExceptionTranslator}, so the HTTP status codes are the same as in the
 * running application.
 */
class UserResourceTest {

    private static final String APP_NAME = "cvsApp";
    private static final Long ADMIN_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long OTHER_USER_ID = 3L;
    private static final String LOGIN = "jdoe";
    private static final String EMAIL = "jdoe@example.com";

    private AuditEventPublisher auditPublisher;
    private UserService userService;
    private UserRepository userRepository;
    private UserAgencyService userAgencyService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        auditPublisher = mock( AuditEventPublisher.class );
        userService = mock( UserService.class );
        userRepository = mock( UserRepository.class );
        userAgencyService = mock( UserAgencyService.class );

        UserResource userResource = new UserResource( auditPublisher, userService, userRepository,
            mock( UserAgencyRepository.class ), userAgencyService, mock( MailService.class ) );
        ReflectionTestUtils.setField( userResource, "applicationName", APP_NAME );

        ExceptionTranslator exceptionTranslator = new ExceptionTranslator();
        ReflectionTestUtils.setField( exceptionTranslator, "applicationName", APP_NAME );

        objectMapper = JsonMapper.builder()
            .disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES )
            .addModule( new JavaTimeModule() )
            .addModule( new ProblemModule() )
            .addModule( new ConstraintViolationProblemModule() )
            .build();

        mockMvc = MockMvcBuilders.standaloneSetup( userResource )
            .setControllerAdvice( exceptionTranslator )
            .setMessageConverters( new MappingJackson2HttpMessageConverter( objectMapper ) )
            .build();

        // By default nobody else uses the email or the login, and the update succeeds
        when( userRepository.findOneByEmailIgnoreCase( anyString() ) ).thenReturn( Optional.empty() );
        when( userRepository.findOneByLogin( anyString() ) ).thenReturn( Optional.empty() );
        when( userService.updateUser( any( UserDTO.class ) ) ).thenAnswer( invocation -> Optional.of( invocation.getArgument( 0 ) ) );

        authenticate( ADMIN_ID, "admin", Collections.singleton( AuthoritiesConstants.ADMIN ) );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------------------------

    private static void authenticate( Long id, String login, Set<String> authorities ) {
        List<GrantedAuthority> grantedAuthorities = authorities.stream()
            .map( SimpleGrantedAuthority::new )
            .collect( Collectors.toList() );

        UserDTO currentUser = new UserDTO();
        currentUser.setId( id );
        currentUser.setLogin( login );
        currentUser.setAuthorities( authorities );

        UserDetails userDetails = new UserDetails( login, "password", grantedAuthorities );
        userDetails.setUser( currentUser );

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken( userDetails, "password", grantedAuthorities ) );
    }

    private static UserDTO userToUpdate( Long id, Set<String> authorities ) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId( id );
        userDTO.setLogin( LOGIN );
        userDTO.setFirstName( "John" );
        userDTO.setLastName( "Doe" );
        userDTO.setEmail( EMAIL );
        userDTO.setLangKey( "en" );
        userDTO.setActivated( true );
        userDTO.setAuthorities( authorities );
        return userDTO;
    }

    private static UserDTO userToUpdate() {
        return userToUpdate( USER_ID, Collections.singleton( AuthoritiesConstants.USER ) );
    }

    private static User existingUser( Long id ) {
        User user = new User();
        user.setId( id );
        return user;
    }

    private static UserAgencyDTO userAgency( Long id, AgencyRole role ) {
        UserAgencyDTO userAgency = new UserAgencyDTO();
        userAgency.setId( id );
        userAgency.setAgencyId( 10L );
        userAgency.setAgencyRole( role );
        userAgency.setUserId( USER_ID );
        return userAgency;
    }

    private ResultActions performUpdate( UserDTO userDTO ) throws Exception {
        return mockMvc.perform( put( "/api/users" )
            .contentType( MediaType.APPLICATION_JSON )
            .content( objectMapper.writeValueAsBytes( userDTO ) ) );
    }

    private UserDTO updatedUser() {
        ArgumentCaptor<UserDTO> captor = ArgumentCaptor.forClass( UserDTO.class );
        verify( userService ).updateUser( captor.capture() );
        return captor.getValue();
    }

    // ------------------------------------------------------------------------------------------
    // Successful updates
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldUpdateTheUserAndPublishAnAuditEvent() throws Exception {
        performUpdate( userToUpdate() )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.id" ).value( USER_ID ) )
            .andExpect( jsonPath( "$.login" ).value( LOGIN ) )
            .andExpect( jsonPath( "$.authorities[0]" ).value( AuthoritiesConstants.USER ) )
            .andExpect( header().string( "X-" + APP_NAME + "-alert", "userManagement.updated" ) )
            .andExpect( header().string( "X-" + APP_NAME + "-params", LOGIN ) );

        assertThat( updatedUser() ).extracting( UserDTO::getId, UserDTO::getEmail ).containsExactly( USER_ID, EMAIL );
        verify( auditPublisher ).publish( eq( "admin" ), any( UserDTO.class ), isNull(), eq( "USER_UPDATED" ) );
    }

    @Test
    void shouldAllowTheUserToKeepItsOwnEmailAndLogin() throws Exception {
        when( userRepository.findOneByEmailIgnoreCase( EMAIL ) ).thenReturn( Optional.of( existingUser( USER_ID ) ) );
        when( userRepository.findOneByLogin( LOGIN ) ).thenReturn( Optional.of( existingUser( USER_ID ) ) );

        performUpdate( userToUpdate() ).andExpect( status().isOk() );

        verify( userService ).updateUser( any( UserDTO.class ) );
    }

    @Test
    void shouldLowercaseTheLoginBeforeCheckingIfItIsUsed() throws Exception {
        UserDTO userDTO = userToUpdate();
        userDTO.setLogin( "JDoe" );

        performUpdate( userDTO ).andExpect( status().isOk() );

        verify( userRepository ).findOneByLogin( LOGIN );
    }

    @Test
    void shouldReturnNotFoundWhenTheUserDoesNotExist() throws Exception {
        when( userService.updateUser( any( UserDTO.class ) ) ).thenReturn( Optional.empty() );

        performUpdate( userToUpdate() ).andExpect( status().isNotFound() );
    }

    @Test
    void shouldPublishTheAuditEventWithAnEmptyUserWhenNotLoggedIn() throws Exception {
        SecurityContextHolder.clearContext();

        performUpdate( userToUpdate() ).andExpect( status().isOk() );

        verify( auditPublisher ).publish( eq( "" ), any( UserDTO.class ), isNull(), eq( "USER_UPDATED" ) );
    }

    // ------------------------------------------------------------------------------------------
    // Email and login already used
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldRejectAnEmailUsedByAnotherUser() throws Exception {
        when( userRepository.findOneByEmailIgnoreCase( EMAIL ) ).thenReturn( Optional.of( existingUser( OTHER_USER_ID ) ) );

        performUpdate( userToUpdate() )
            .andExpect( status().isBadRequest() )
            .andExpect( jsonPath( "$.message" ).value( "error.emailexists" ) );

        verify( userService, never() ).updateUser( any() );
        verifyNoInteractions( auditPublisher );
    }

    @Test
    void shouldRejectALoginUsedByAnotherUser() throws Exception {
        when( userRepository.findOneByLogin( LOGIN ) ).thenReturn( Optional.of( existingUser( OTHER_USER_ID ) ) );

        performUpdate( userToUpdate() )
            .andExpect( status().isBadRequest() )
            .andExpect( jsonPath( "$.message" ).value( "error.userexists" ) );

        verify( userService, never() ).updateUser( any() );
        verifyNoInteractions( auditPublisher );
    }

    // ------------------------------------------------------------------------------------------
    // Agencies
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldSaveNewUserAgenciesAndKeepTheExistingOnes() throws Exception {
        UserAgencyDTO existing = userAgency( 100L, AgencyRole.ADMIN_SL );
        UserAgencyDTO added = userAgency( null, AgencyRole.VIEW );
        UserAgencyDTO saved = userAgency( 101L, AgencyRole.VIEW );
        when( userAgencyService.save( any( UserAgencyDTO.class ) ) ).thenReturn( saved );

        UserDTO userDTO = userToUpdate();
        userDTO.setUserAgencies( new HashSet<>( List.of( existing, added ) ) );

        performUpdate( userDTO ).andExpect( status().isOk() );

        ArgumentCaptor<UserAgencyDTO> savedAgency = ArgumentCaptor.forClass( UserAgencyDTO.class );
        verify( userAgencyService ).save( savedAgency.capture() );
        assertThat( savedAgency.getValue() ).extracting( UserAgencyDTO::getId, UserAgencyDTO::getAgencyRole )
            .containsExactly( null, AgencyRole.VIEW );
        assertThat( updatedUser().getUserAgencies() )
            .extracting( UserAgencyDTO::getId )
            .containsExactlyInAnyOrder( 100L, 101L );
    }

    @Test
    void shouldNotSaveAgenciesThatAlreadyExist() throws Exception {
        UserDTO userDTO = userToUpdate();
        userDTO.setUserAgencies( new HashSet<>( List.of( userAgency( 100L, AgencyRole.ADMIN_TL ) ) ) );

        performUpdate( userDTO ).andExpect( status().isOk() );

        verifyNoInteractions( userAgencyService );
        assertThat( updatedUser().getUserAgencies() )
            .extracting( UserAgencyDTO::getId )
            .containsExactly( 100L );
    }

    @Test
    void shouldNotTouchAgenciesWhenNoneAreSent() throws Exception {
        performUpdate( userToUpdate() ).andExpect( status().isOk() );

        verifyNoInteractions( userAgencyService );
        assertThat( updatedUser().getUserAgencies() ).isNull();
    }

    // ------------------------------------------------------------------------------------------
    // Authorities
    // ------------------------------------------------------------------------------------------

    @Test
    void shouldNotAllowAUserToChangeItsOwnRoles() throws Exception {
        UserDTO self = userToUpdate( ADMIN_ID, Collections.singleton( AuthoritiesConstants.USER ) );

        performUpdate( self )
            .andExpect( status().isBadRequest() )
            .andExpect( jsonPath( "$.message" ).value( "error.user_changed_own_roles" ) );

        verify( userService, never() ).updateUser( any() );
        verifyNoInteractions( auditPublisher );
    }

    @Test
    void shouldAllowAUserToUpdateItselfWithTheSameRoles() throws Exception {
        UserDTO self = userToUpdate( ADMIN_ID, Collections.singleton( AuthoritiesConstants.ADMIN ) );

        performUpdate( self ).andExpect( status().isOk() );

        assertThat( updatedUser().getAuthorities() ).containsExactly( AuthoritiesConstants.ADMIN );
    }

    @Test
    void shouldAllowAnAdministratorToChangeTheRolesOfAnotherUser() throws Exception {
        Set<String> authorities = new HashSet<>( List.of( AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER ) );

        performUpdate( userToUpdate( USER_ID, authorities ) ).andExpect( status().isOk() );

        assertThat( updatedUser().getAuthorities() )
            .containsExactlyInAnyOrder( AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER );
    }
}
