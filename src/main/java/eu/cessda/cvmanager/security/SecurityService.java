package eu.cessda.cvmanager.security;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.domain.User;
import eu.cessda.cvmanager.ui.view.HomeView;

/**
 * This class presents some auxiliary methods related to the Security stuff.
 * 
 * @author Karam
 */
@Service
public class SecurityService {
	/**
	 * The cookie name that will save remember-me information
	 */
	public static final String REST_DSPACE_TOKEN = "rest-dspace-token";
	public static final String REMEMBERME_COOKIE_NAME = "remember_me";
	
//	@Autowired
//    private CustomAuthenticationProvider authProvider;
	
	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	DBservices db;
	
	@Autowired
	UserDetailesServiceImpl uds;
	
	/**
	 * @return the username of the logged in user if exists, or null otherwise.
	 */
	public String getLoggedInUsername(){
		//Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(userDetails instanceof UserDetails) return ((UserDetails)userDetails).getUsername();
		else return null;
	}
	
	/**
	 * Determine whether authentication object exist
	 * @return
	 */
	public boolean isAuthenticate(){
		return SecurityContextHolder.getContext().getAuthentication() != null;
	}

	/**
	 * Get Logged Principal as user details
	 * @return
	 */
	public UserDetails getUserDetails(){
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if( userDetails != null )
			return (UserDetails) userDetails;
		else
			return null;
	}
	
//	/**
//	 * Get role details
//	 */
//	public List<UserRole> getUserRole( RoleType roleType){
//		if( getUserDetails() == null || getUserDetails().getStatusFull() == null)
//			return Collections.emptyList();
//		
//		List<UserRole> userRoles = getUserDetails().getStatusFull().getUserRoles();
//		
//		if( userRoles == null || userRoles.isEmpty())
//			return Collections.emptyList();
//
//		return userRoles.stream()
//				.filter(userRole -> roleType.equals( userRole.getRoleType()))
//				.collect(Collectors.toList());
//	}
//	
	/**
	 * Register a new user in the current security context.
	 * @param username: the username of the user to be registered.
	 * @param password: its password.
	 * @return true if the user is successfully logged in, or false otherwise.
	 */
	public boolean login(String username, String password, boolean rememberMe) {
        try {
        	// initialize custom authentication provider
        	//Authentication authentication = authProvider.authenticate( new UsernamePasswordAuthenticationToken(username, password));
        	
        	Authentication authentication = manager.authenticate( new UsernamePasswordAuthenticationToken(username, password));
        	
//        	if( authentication == null )
//        		return false;
        	
        	// Reinitialize the session to protect against session fixation attacks. This does not work with websocket communication.
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            SecurityContextHolder.getContext().setAuthentication( authentication );
            
            //set cookies if remember-me is checked
            if(rememberMe){
            	addRememberMeCookie(username);
            }
            
            return true;
        }
        catch(DisabledException ex){
        	Notification.show("User account is DISABLED ...");
        	return false;
        }
        catch(LockedException ex){
        	Notification.show("User account is LOCKED ...");
        	return false;
        }
        catch(BadCredentialsException ex){
        	Notification.show("Your password is INCORRECT ...");
        	return false;
        }
        catch(InternalAuthenticationServiceException ex){
        	//ex.printStackTrace();
        	Notification.show("User does not exist ...");
        	return false;
        }
        catch (AuthenticationException ex) {
        	Notification.show("Invalid username or password ...");
        	ex.printStackTrace();
            return false;
        }
    }
	
	/**
	 * Register a remember-me user in the current security context.
	 * We get the information from cookies.
	 * @param dspaceRestToken
	 * @return
	 */
	private boolean reLoginRememberdUser(String randomUsername) {
        try {
    		//Authentication authentication = authProvider.authenticate( dspaceRestToken );
        	
        	//if( authentication == null )
        		//return false;
        	
        	//get the user
        	User u = db.getUserByRandomUsername(randomUsername);
        	
        	if(u != null){
        		//"remember-me-gesis" is just a key to validate
				RememberMeAuthenticationProvider provider = new RememberMeAuthenticationProvider("remember-me-gesis");
				
				UserDetails userDetails = (UserDetails) uds.loadUserByUsername(u.getUsername());
				RememberMeAuthenticationToken rememberToken = new RememberMeAuthenticationToken("remember-me-gesis", userDetails, userDetails.getAuthorities());
        	
				Authentication authentication = provider.authenticate(rememberToken);
	            // Reinitialize the session to protect against session fixation attacks. This does not work with websocket communication.
	            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
	            SecurityContextHolder.getContext().setAuthentication( authentication );
	            
	            return true;
        	}
        	return false;
        }
        catch(DisabledException ex){
        	Notification.show("User account is DISABLED ...");
        	return false;
        }
        catch(LockedException ex){
        	Notification.show("User account is LOCKED ...");
        	return false;
        }
        catch(BadCredentialsException ex){
        	Notification.show("Your password is INCORRECT ...");
        	return false;
        }
        catch(InternalAuthenticationServiceException ex){
        	Notification.show("User does not exist ...");
        	return false;
        }
        catch (AuthenticationException ex) {
        	Notification.show("Invalid username or password ...");
        	ex.printStackTrace();
            return false;
        }
    }
	
	/**
	 * Logout the current user and destroy the current Vaadin session.
	 * @param viewName: the Bean name of the view that must navigate to after the logout.
	 */
	public void logout(String viewName){
		//remove the remember-me cookie
		if(getRememberMeCookie() != null){
			removeRememberMeCookie();
		}
		
		UI.getCurrent().getNavigator().navigateTo( HomeView.VIEW_NAME);
		VaadinSession s = UI.getCurrent().getSession();
		s.close();
		UI.getCurrent().getPage().reload();
	}
	
	/**
	 * This method add a cookie for remember-me functionalities
	 * @param username
	 */
	private void addRememberMeCookie(String username){
		
    	// get token from session
//    	UserDetails userDetails = getUserDetails();
//    	
//    	if(userDetails == null)
//    		return;
//    	
//    	Cookie cookie = new Cookie(SecurityService.REST_DSPACE_TOKEN, userDetails.getStatusFull().getToken());
//    	cookie.setPath("/");
//    	cookie.setMaxAge(60 * 60 * 24 * 30); //30 days
//    	VaadinService.getCurrentResponse().addCookie(cookie);
		
		//get the user from db
    	User u = db.getUserByUsername(username);
    	
    	Cookie cookie = new Cookie(SecurityService.REMEMBERME_COOKIE_NAME, u.getRandomUsername());
    	cookie.setPath("/");
    	cookie.setMaxAge(60 * 60 * 24 * 30); //30 days
    	VaadinService.getCurrentResponse().addCookie(cookie);
    	
	}
	
	/**
	 * This method removes the cookie of the remember-me functionalities
	 */
	private void removeRememberMeCookie(){
    	
    	Cookie cookie = new Cookie(SecurityService.REMEMBERME_COOKIE_NAME, "");
    	cookie.setPath("/");
    	cookie.setMaxAge(0);
    	VaadinService.getCurrentResponse().addCookie(cookie);
    	
	}
	
	/**
	 * This method returns the cookie of the remember-me functionalities
	 * @return the saved cookie or null otherwise
	 */
	private Cookie getRememberMeCookie(){
    	
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		for (Cookie cookie : cookies) {
			if(cookie.getName().equalsIgnoreCase(SecurityService.REMEMBERME_COOKIE_NAME))
				return cookie;
		}
    	return null;
    	
	}
	
	/**
	 * Login a user if the cookie is existed
	 * @return true if the authentication is correctly done or false otherwise
	 */
	public boolean rememberMeLogin(){
		
		Cookie c = getRememberMeCookie();
		if(c != null){
			return reLoginRememberdUser(c.getValue());
		}
		
		return false;
	}
}
