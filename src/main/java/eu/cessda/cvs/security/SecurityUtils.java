package eu.cessda.cvs.security;

import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.service.InsufficientVocabularyAuthorityException;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;
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
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                    return springSecurityUser.getUsername();
                } else if (authentication.getPrincipal() instanceof String) {
                    return (String) authentication.getPrincipal();
                }
                return null;
            });
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .filter(authentication -> authentication.getCredentials() instanceof String)
            .map(authentication -> (String) authentication.getCredentials());
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

    public static Optional<eu.cessda.cvs.security.UserDetails> getCurrectUserDetails(){
        return Optional.ofNullable( SecurityContextHolder.getContext().getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof eu.cessda.cvs.security.UserDetails) {
                    return (eu.cessda.cvs.security.UserDetails) authentication.getPrincipal();
                }
                return null;
            });
    }

    public static UserDTO getCurrentUser(){
        Optional<eu.cessda.cvs.security.UserDetails> currectUserDetailsOpt = getCurrectUserDetails();
        if( currectUserDetailsOpt.isPresent() )
            return currectUserDetailsOpt.get().getUser();
        return null;
    }

    public static Long getCurrentUserId(){
        UserDTO currentUser = getCurrentUser();
        if( currentUser == null )
            return null;
        return currentUser.getId();
    }

    public static boolean hasAnyAgencyAuthority(ActionType actionType, Long agencyId, Set<AgencyRole> agencyRoles, String language){
        UserDTO currentUser = getCurrentUser();
        if( currentUser == null )
            return false;

        // if user content admin
        if( isCurrentUserInRole("ROLE_ADMIN") || isCurrentUserInRole("ROLE_ADMIN_CONTENT" ) )
            return true;

        // check based on actionType and UserAgencyRole
        switch ( actionType ) {
            case CREATE_CV:
            case EDIT_CV:
            case DELETE_CV:
            case WITHDRAWN_CV:
            case CREATE_CODE:
            case EDIT_CODE:
            case DELETE_CODE:
                return hasAgencyRole(currentUser.getUserAgencies(), agencyId, agencyRoles, language);
            default:
                return false;
        }
    }

    public static void checkResourceAuthorization(ActionType actionType, Long agencyId, Set<AgencyRole> agencyRoles, String language){
        if (!SecurityUtils.hasAnyAgencyAuthority(actionType, agencyId, agencyRoles, language)) {
            throw new InsufficientVocabularyAuthorityException();
        }
    }

    public static boolean hasAgencyRole(Long agencyId, Set<AgencyRole> agencyRoles, String language){
        getCurrectUserDetails().ifPresent(
            userDetails -> hasAgencyRole(userDetails.getUser().getUserAgencies(), agencyId, agencyRoles, language)
        );
        return false;
    }

    public static boolean hasAgencyRole(Set<UserAgencyDTO> userAgencies, Long agencyId, Set<AgencyRole> agencyRoles, String language){
        boolean hasAuth = false;
        if ( agencyId == null ) { // only check for agencyRoles
            for (AgencyRole agencyRole : agencyRoles) {
                if( userAgencies.stream().anyMatch(ua -> ua.getAgencyRole().equals(agencyRole))){
                    hasAuth = true;
                    break;
                }
            }
        }
        for (UserAgencyDTO userAgency : userAgencies) {
            if( userAgency.getAgencyId().equals(agencyId) && agencyRoles.contains( userAgency.getAgencyRole() ) &&
                ( language == null || language.equals( userAgency.getLanguage() ))) {
                hasAuth = true;
                break;
            }
        }
        return hasAuth;
    }
}
