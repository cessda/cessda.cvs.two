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
package eu.cessda.cvs.security;

import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the {@link SecurityUtils} utility class.
 */
class SecurityUtilsUnitTest {

    @Test
    void testGetCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        assertThat(login).contains("admin");
    }

    @Test
    void testgetCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "token"));
        SecurityContextHolder.setContext(securityContext);
        Optional<String> jwt = SecurityUtils.getCurrentUserJWT();
        assertThat(jwt).contains("token");
    }

    @Test
    void testIsAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void testAnonymousIsNotAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Set<GrantedAuthority> authorities = Collections.singleton( ( new SimpleGrantedAuthority( AuthoritiesConstants.ANONYMOUS ) ) );
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    void testIsCurrentUserInRole() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Set<GrantedAuthority> authorities = Collections.singleton( new SimpleGrantedAuthority( AuthoritiesConstants.USER ) );
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        assertThat(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.USER)).isTrue();
        assertThat(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)).isFalse();
    }

    @Test
    void testCurrentUserDetails() {
        UserDetails principle = new UserDetails( "user", "user", Collections.emptyList() );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principle, "user"));
        SecurityContextHolder.setContext(securityContext);

        Optional<UserDetails> userDetails = SecurityUtils.getCurrentUserDetails();
        assertThat( userDetails ).contains( principle );
    }

    @Test
    void testCurrentUser() {
        UserDTO userDTO = new UserDTO();

        long userId = new Random().nextLong();
        userDTO.setId( userId );
        UserDetails principle = new UserDetails( "user", "user", Collections.emptyList() );
        principle.setUser( userDTO );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principle, "user"));
        SecurityContextHolder.setContext(securityContext);

        assertThat( SecurityUtils.getCurrentUser() ).isEqualTo( userDTO );
        assertThat( SecurityUtils.getCurrentUserId() ).isEqualTo( userId );
    }

    @Test
    void testCurrentUserAgencyRoles() {

        UserAgencyDTO viewUserAgency = new UserAgencyDTO();
        viewUserAgency.setAgencyRole( AgencyRole.VIEW );

        UserAgencyDTO adminUserAgency = new UserAgencyDTO();
        adminUserAgency.setAgencyRole( AgencyRole.ADMIN );

        // Add the user agencies to the user
        UserDTO userDTO = new UserDTO();
        userDTO.setUserAgencies( Set.of( viewUserAgency, adminUserAgency ) );
        UserDetails principle = new UserDetails( "user", "user", Collections.emptyList() );
        principle.setUser( userDTO );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principle, "user"));
        SecurityContextHolder.setContext(securityContext);

        // CREATE_CODE requires the ADMIN role, which will be satisfied by adminUserAgency
        assertThat( SecurityUtils.hasAnyAgencyAuthority( ActionType.CREATE_CODE, null, null ) ).isTrue();
    }

    @Test
    void testHasAgencyRole() {
        long agencyId = 1L;
        String agencyLang = "en";

        UserAgencyDTO viewUserAgency = new UserAgencyDTO();
        viewUserAgency.setAgencyRole( AgencyRole.VIEW );

        UserAgencyDTO adminUserAgency = new UserAgencyDTO();
        adminUserAgency.setAgencyRole( AgencyRole.ADMIN );
        adminUserAgency.setAgencyId( agencyId );
        adminUserAgency.setLanguage( agencyLang );

        // should pass as adminUserAgency has the ADMIN role
        assertThat( SecurityUtils.hasAgencyRole( Set.of( viewUserAgency, adminUserAgency ), null, Set.of( AgencyRole.ADMIN ), null ) ).isTrue();

        // should fail as the role is different
        assertThat( SecurityUtils.hasAgencyRole( Set.of( viewUserAgency, adminUserAgency ), null, Set.of( AgencyRole.ADMIN_SL ), null ) ).isFalse();

        // should fail as the user agency id and language are different event though the role is correct
        assertThat( SecurityUtils.hasAgencyRole( Set.of( viewUserAgency ), agencyId, Set.of( AgencyRole.VIEW ), agencyLang ) ).isFalse();

        // should pass as adminUserAgency has the correct agency id, agency language and role
        assertThat( SecurityUtils.hasAgencyRole( Set.of( viewUserAgency, adminUserAgency ), agencyId, Set.of( AgencyRole.ADMIN ), agencyLang ) ).isTrue();
    }

}
