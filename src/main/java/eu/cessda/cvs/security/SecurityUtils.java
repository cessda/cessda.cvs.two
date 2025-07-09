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

import eu.cessda.cvs.service.InsufficientVocabularyAuthorityException;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable( securityContext.getAuthentication() )
            .map( Authentication::getPrincipal )
            .map( principal ->
            {
                if ( principal instanceof UserDetails )
                {
                    UserDetails springSecurityUser = (UserDetails) principal;
                    return springSecurityUser.getUsername();
                }
                else if ( principal instanceof String )
                {
                    return (String) principal;
                }
                return null;
            } );
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map( Authentication::getCredentials )
            .filter( String.class::isInstance )
            .map( String.class::cast );
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
            getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the {@code isUserInRole()} method in the Servlet API.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static boolean isCurrentUserInRole(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
            getAuthorities(authentication).anyMatch(authority::equals);
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority);
    }

    public static Optional<eu.cessda.cvs.security.UserDetails> getCurrentUserDetails(){
        return Optional.ofNullable( SecurityContextHolder.getContext().getAuthentication() )
            .map( Authentication::getPrincipal )
            .filter( eu.cessda.cvs.security.UserDetails.class::isInstance )
            .map( eu.cessda.cvs.security.UserDetails.class::cast );
    }

    public static UserDTO getCurrentUser(){
        Optional<eu.cessda.cvs.security.UserDetails> currentUserDetailsOpt = getCurrentUserDetails();
        return currentUserDetailsOpt.map(eu.cessda.cvs.security.UserDetails::getUser).orElse(null);
    }

    public static Long getCurrentUserId(){
        UserDTO currentUser = getCurrentUser();
        return currentUser == null ? null : currentUser.getId();
    }

    // TODO: use Spring Security
    public static boolean isAdminContent() {
        // if user content admin
        return isCurrentUserInRole("ROLE_ADMIN") || isCurrentUserInRole("ROLE_ADMIN_CONTENT" );
    }

    /**
     * Determine whether the current user has the authority to perform the
     * specified action against a given agency.
     *
     * @param actionType the action to perform.
     * @param agencyId the agency.
     * @return {@code true} if the user has permission, {@code false} otherwise.
     */
    public static boolean hasAgencyAuthority(ActionType actionType, Long agencyId) {
        Objects.requireNonNull(actionType, "actionType is null");

        UserDTO currentUser = getCurrentUser();

        if (currentUser == null)
        {
            return false;
        }

        if (isAdminContent())
        {
            return true;
        }

        for ( var userAgency : currentUser.getUserAgencies() )
        {
            if ( Objects.equals( userAgency.getAgencyId(), agencyId ) )
            {
                return hasAgencyRole( userAgency, actionType );
            }
        }

        return false;
    }

    public static boolean hasAnyAgencyAuthority(ActionType actionType, Long agencyId, String language) {
        Objects.requireNonNull(actionType, "actionType is null");

        UserDTO currentUser = getCurrentUser();

        if (currentUser == null)
        {
            return false;
        }

        if (isAdminContent())
        {
            return true;
        }

        // check based on UserAgencyRole
        if ( agencyId != null )
        {
            for ( var userAgency : currentUser.getUserAgencies() )
            {
                if (hasAgencyRole( userAgency, agencyId, actionType, language ))
                {
                    return true;
                }
            }
        }
        else
        {
            for ( var userAgency : currentUser.getUserAgencies() )
            {
                if ( hasAgencyRole( userAgency, actionType ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static void checkResourceAuthorization(ActionType actionType, Long agencyId, String language) {
        if (!SecurityUtils.hasAnyAgencyAuthority(actionType, agencyId, language)) {
            throw new InsufficientVocabularyAuthorityException(actionType);
        }
    }

    public static boolean hasAgencyRole( UserAgencyDTO userAgency, Long agencyId, ActionType actionType, String language )
    {
        return Objects.equals( agencyId, userAgency.getAgencyId() )
            && language.equals( userAgency.getLanguage() )
            && actionType.hasPermission( userAgency.getAgencyRole() );
    }

    public static boolean hasAgencyRole( UserAgencyDTO userAgency, ActionType actionType )
    {
        // only check for agencyRoles
        return actionType.hasPermission( userAgency.getAgencyRole() );
    }
}
