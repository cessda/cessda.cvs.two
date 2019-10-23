package eu.cessda.cvmanager.utils;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.gesis.wts.domain.enumeration.AgencyRole;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.AuthoritiesConstants;
import org.gesis.wts.security.RoleTypeCvs;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.dto.VersionDTO;

public final class CvManagerSecurityUtils {
	private CvManagerSecurityUtils() {}
	
	/**
     * Get the login of the current user.
     *
     * @return the login of the current user
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
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> authentication.getAuthorities().stream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)))
            .orElse(false);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the isUserInRole() method in the Servlet API
     *
     * @param authority the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
            .orElse(false);
    }
    
    public static boolean isCurrentUserInRole(RoleTypeCvs roleType) {
    	return isCurrentUserInRole( roleType.toString() );
    }
    
    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static Optional<org.gesis.wts.security.UserDetails> getCurrentUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof org.gesis.wts.security.UserDetails) {
                	return (org.gesis.wts.security.UserDetails) authentication.getPrincipal();
                } 
                return null;
            });
    }
    
    public static org.gesis.wts.security.UserDetails getLoggedUser() {
		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if( currentUserDetails.isPresent() )
    		return currentUserDetails.get();
        return null;
    }
    
    /**
     * Get list of agencies from logged user
     * @return
     */
    public static Optional<List<AgencyDTO>> getCurrentUserAgencies(){
		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if( currentUserDetails.isPresent() )
    		return Optional.ofNullable( currentUserDetails.get())
    	    	.map( userdetail -> userdetail.getAgencyDtos().stream().collect( Collectors.toList()));
		return Optional.empty();
    }
    
    public static boolean isUserAdmin() {
    	return isCurrentUserInRole( RoleTypeCvs.ROLE_ADMIN );
    }
    
    public static boolean isUserAdminContent() {
    	return isCurrentUserInRole( RoleTypeCvs.ROLE_ADMIN_CONTENT );
    }
    
    public static boolean isCurrentUserSystemAdmin() {
    	return ( isUserAdmin() || isUserAdminContent() );
    }
    
    
    /**
     * Check if current logged user has admin access to particular agency
     * @param agency
     * @return
     */
    public static boolean isCurrentUserAgencyAdmin( AgencyDTO agency ) {
    	// user is admin skip authorization
    	if( isCurrentUserSystemAdmin() )
    		return true;

		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if( currentUserDetails.isPresent() )
			return Optional.ofNullable( currentUserDetails.get())
				.map( userdetail -> {
					if (userdetail.getAgencyDtos().contains(agency)) {
						for( UserAgencyDTO userAgency : getUserAgencyByAgency(agency).get())
							if( userAgency.getAgencyRole().equals(AgencyRole.ADMIN))
								return true;
						return false;
					}
					else
						return false;
				})
				.orElse(false);
		return false;
    }
    
	public static boolean isCurrentUserAgencyAdmin() {
		if (!isAuthenticated())
			return false;
		// user is admin skip authorization
		if (isUserAdmin() || isUserAdminContent())
			return true;

		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if (currentUserDetails.isPresent())
			return Optional.ofNullable(currentUserDetails.get())
					.map(userdetail -> {
						for (UserAgencyDTO userAgency : userdetail.getUserAgencyDtos()) {
							if (userAgency.getAgencyRole().equals(AgencyRole.ADMIN))
								return true;
						}
						return false;
					})
					.orElse(false);
		return false;
	}
	
	/**
     * Get list of user agencies from logged user
     * @return
     */
    public static Optional<List<UserAgencyDTO>> getCurrentUserUserAgencies(){

		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if (currentUserDetails.isPresent())
			return Optional.ofNullable( currentUserDetails.get() )
    	    	.map( userdetail -> userdetail.getUserAgencyDtos().stream()
    	    				.sorted( (a1, a2) -> a1.getAgencyName().compareTo( a2.getAgencyName()))
    	    				.collect( Collectors.toList()));
		return Optional.empty();

    }
    
    /**
     * Get list of userAgency roles of logged user in specific agency
     * @param agency
     * @return
     */
    public static Optional<List<UserAgencyDTO>> getUserAgencyByAgency( AgencyDTO agency ) {	
    	if( agency == null || !getCurrentUserDetails().isPresent())
    		return Optional.empty();

		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if (currentUserDetails.isPresent())
    		return Optional.ofNullable( currentUserDetails.get() )
    	    	.map( userdetail -> userdetail.getUserAgencyDtos().stream()
    	    				.filter( p -> p.getAgencyId().equals( agency.getId()))
    	    				.collect( Collectors.toList()));
		return Optional.empty();
    }
    
    public static Optional<List<Language>> getCurrentUserLanguageSlByAgency( AgencyDTO agency ){
    	if( !isCurrentUserAllowCreateCvSl(agency) )
    		return Optional.of( Collections.emptyList());

		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if (currentUserDetails.isPresent())
    		return Optional.ofNullable( currentUserDetails.get() )
    	    	.map( userdetail -> userdetail.getUserAgencyDtos().stream()
    	    			.filter( p -> p.getAgencyId().equals( agency.getId()))
    	    			.filter( p -> p.getAgencyRole().equals( AgencyRole.ADMIN_SL) || p.getAgencyRole().equals( AgencyRole.CONTRIBUTOR_SL))
    	    			.map(UserAgencyDTO::getLanguage)
    	    			.sorted( (l1, l2) -> l1.toString().compareTo( l2.toString()))
    	    			.collect( Collectors.toList()));
		return Optional.empty();
    }
    
    public static Optional<List<Language>> getCurrentUserLanguageTlByAgency( AgencyDTO agency ){
    	if( !isCurrentUserAllowCreateCvTl(agency) )
    		return Optional.of( Collections.emptyList());

		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if (currentUserDetails.isPresent())
    		return Optional.ofNullable( currentUserDetails.get() )
    	    	.map( userdetail -> userdetail.getUserAgencyDtos().stream()
    	    			.filter( p -> p.getAgencyId().equals( agency.getId()))
    	    			.filter( p -> p.getAgencyRole().equals( AgencyRole.ADMIN_TL) || p.getAgencyRole().equals( AgencyRole.CONTRIBUTOR_TL))
    	    			.map(UserAgencyDTO::getLanguage)
    	    			.sorted( (l1, l2) -> l1.toString().compareTo( l2.toString()))
    	    			.collect( Collectors.toList()));
		return Optional.empty();
    }
    
    /**
     * Determine whether user allowed to create CV, user needs SL role, regardless agency
     * @return
     */
    public static boolean isCurrentUserAllowCreateCvSl() {
    	if( !isAuthenticated())
    		return false;
    	// system admin
    	if( isCurrentUserSystemAdmin())
    		return true;
		Optional<List<UserAgencyDTO>> currentUserUserAgencies = getCurrentUserUserAgencies();
		if( currentUserUserAgencies.isPresent() ) {
    		for( UserAgencyDTO userAgency :  currentUserUserAgencies.get()) {
				if( userAgency.getAgencyRole().equals(AgencyRole.ADMIN) ||  userAgency.getAgencyRole().equals(AgencyRole.ADMIN_SL))
					return true;
			}
    	}
		return false;
    }
    
    /**
     * Determine whether user allowed to create CV, user needs SL role in specific agency
     * @param agency
     * @return
     */
    public static boolean isCurrentUserAllowCreateCvSl( AgencyDTO agency ) {
    	if( agency == null || !isAuthenticated())
    		return false;
    	// admin
    	if( isCurrentUserAgencyAdmin(agency))
    		return true;
    	
    	if( !getUserAgencyByAgency(agency).isPresent() )
    		return false;
    	
    	// check for SL role
		Optional<List<UserAgencyDTO>> userAgencyByAgency = getUserAgencyByAgency(agency);
		if( userAgencyByAgency.isPresent() )
			return Optional.of( userAgencyByAgency.get() )
				.map( uas -> {
					for( UserAgencyDTO userAgency : uas) {
						if( userAgency.getAgencyRole().equals(AgencyRole.ADMIN_SL))
							return true;
					}
					return false;
				}).orElse( false);
		return false;
    }
    
    /**
     * Determine whether user allowed to add CV translation, user needs TL role in any agencies
     * @return
     */
    public static boolean isCurrentUserAllowCreateCvTl()  {
    	if( !isAuthenticated())
    		return false;
    	// admin
    	if(isCurrentUserSystemAdmin())
    		return true;
    	
    	// check for TL role
    	getCurrentUserUserAgencies().ifPresent( userAgencies ->
    		Optional.of( userAgencies )
    		.map( uas -> {
    			for( UserAgencyDTO userAgency : uas) {
    				if( userAgency.getAgencyRole().equals(AgencyRole.ADMIN) ||  userAgency.getAgencyRole().equals(AgencyRole.ADMIN_TL))
    					return true;
    			}
    			return false;
    		}));
		return false;
    }
    
    /**
     * Determine whether user allowed to add CV translation, user needs TL role in specific agency
     * @param agency
     * @return
     */
    public static boolean isCurrentUserAllowCreateCvTl( AgencyDTO agency ) {
    	if( agency == null || !isAuthenticated())
    		return false;
    	// admin
    	if( isCurrentUserAgencyAdmin(agency))
    		return true;
    	
    	if(!getUserAgencyByAgency(agency).isPresent())
    		return false;
    	
    	// check for TL role
		Optional<List<UserAgencyDTO>> userAgencyByAgency = getUserAgencyByAgency(agency);
		if( userAgencyByAgency.isPresent() )
			return Optional.of( userAgencyByAgency.get() )
        		.map( uas -> {
        			for( UserAgencyDTO userAgency : uas) {
        				if( userAgency.getAgencyRole().equals(AgencyRole.ADMIN_TL))
        					return true;
        			}
    				return false;
        		}).orElse( false);
		return false;
    }
    
    
    public static boolean isCurrentUserAllowEditCv( AgencyDTO agency, Language language) {
    	if( agency == null || !isAuthenticated() )
    		return false;
    	// admin
    	if( isCurrentUserAgencyAdmin(agency))
    		return true;
    	
    	if( !getUserAgencyByAgency(agency).isPresent() )
    		return false;
    	
    	// check for SL role
		Optional<List<UserAgencyDTO>> userAgencyByAgency = getUserAgencyByAgency(agency);
		if( userAgencyByAgency.isPresent() )
			return Optional.of( userAgencyByAgency.get() )
    		.map( uas -> {
    			for( UserAgencyDTO userAgency : uas) {
    				if( (userAgency.getAgencyRole().equals(AgencyRole.ADMIN_SL) || userAgency.getAgencyRole().equals(AgencyRole.CONTRIBUTOR_SL) ||
    						userAgency.getAgencyRole().equals(AgencyRole.ADMIN_TL) || userAgency.getAgencyRole().equals(AgencyRole.CONTRIBUTOR_TL))
							&&  userAgency.getLanguage().equals( language ))
    						return true;
    			}
				return false;
    		}).orElse( false);
		return false;
    }
    
    public static Optional<List<UserAgencyDTO>> getUserAgencyByAgencyAndLanguage( AgencyDTO agency, String langIso) {	
    	return getUserAgencyByAgencyAndLanguage(agency, Language.valueOfEnum(langIso));
    }
    
    public static Optional<List<UserAgencyDTO>> getUserAgencyByAgencyAndLanguage( AgencyDTO agency, Language language) {	
    	if( agency == null || !getCurrentUserDetails().isPresent())
    		return Optional.empty();

		Optional<org.gesis.wts.security.UserDetails> currentUserDetails = getCurrentUserDetails();
		if (currentUserDetails.isPresent())
			return Optional.ofNullable( currentUserDetails.get() )
    	    	.map( userdetail ->
    	    		userdetail.getUserAgencyDtos().stream()
    	    				.filter( p -> p.getAgencyId().equals( agency.getId() ))
    	    				.filter( p -> p.getAgencyRole().equals( AgencyRole.ADMIN_SL) || p.getAgencyRole().equals( AgencyRole.ADMIN_TL) || 
    	    						p.getAgencyRole().equals( AgencyRole.CONTRIBUTOR_SL) || p.getAgencyRole().equals( AgencyRole.CONTRIBUTOR_TL) )
    	    				.filter( p -> p.getLanguage().equals(language))
    	    				.collect( Collectors.toList()));
		return Optional.empty();
    }
    
    /**
     * Determine whether user allowed to add CV translation, user needs TL role in specific agency
     * @param agency
     * @return
     */
    public static boolean isCurrentUserAllowToManageCv( AgencyDTO agency, VersionDTO version ) {
    	if( agency == null || !isAuthenticated())
    		return false;
    	// admin
    	if( isCurrentUserAgencyAdmin(agency))
    		return true;
    	
    	Optional<List<UserAgencyDTO>> userAgencyByAgencyAndLanguage = getUserAgencyByAgencyAndLanguage(agency, version.getLanguage());
    	
    	if(!userAgencyByAgencyAndLanguage.isPresent())
    		return false;
    	
		// check for role and current status
    	return Optional.of( userAgencyByAgencyAndLanguage.get() )
        		.map( uas -> {
        			for( UserAgencyDTO userAgency : uas) {
        				if(  version.getStatus().equals(Status.DRAFT.toString())) {
        					return true;
        				}
        				else if(  version.getStatus().equals(Status.INITIAL_REVIEW.toString())) {
        					return true;
        				}
        				else if(  version.getStatus().equals(Status.FINAL_REVIEW.toString()) && ( userAgency.getAgencyRole().equals( AgencyRole.ADMIN_SL) || userAgency.getAgencyRole().equals( AgencyRole.ADMIN_TL)))
        					return true;
        			}
    				return false;
        		}).orElse( false);
    }
    
    /**
     * Determine whether user allowed to edit Agency Specific Metadata on the Cv, user needs TL role in specific agency
     * @param agency
     * @return
     */
    public static boolean isCurrentUserAllowToEditMetadata( AgencyDTO agency, VersionDTO version ) {
    	if( agency == null || !isAuthenticated())
    		return false;
    	// admin
    	if( isCurrentUserAgencyAdmin(agency))
    		return true;
    	
    	Optional<List<UserAgencyDTO>> userAgencyByAgencyAndLanguage = getUserAgencyByAgencyAndLanguage(agency, version.getLanguage());
    	
    	if(!userAgencyByAgencyAndLanguage.isPresent())
    		return false;
    	    	
    	// check for role and current status
    	return Optional.of( userAgencyByAgencyAndLanguage.get() )
        		.map( uas -> {
        			for( UserAgencyDTO userAgency : uas) {
    					if( userAgency.getAgencyRole().equals( AgencyRole.ADMIN_SL) || userAgency.getAgencyRole().equals( AgencyRole.ADMIN_TL))
    						return true;
        			}
    				return false;
        		}).orElse( false);
    }
    
    public static String generateSecureRandomPassword(int randomStrLength) {
    	char[] possibleCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    	return RandomStringUtils.random( randomStrLength, 0, possibleCharacters.length-1, false, false, possibleCharacters, new SecureRandom() );
    }
 
}
