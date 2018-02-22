package eu.cessda.cvmanager.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cessda.cvmanager.domain.Role;
import eu.cessda.cvmanager.domain.User;
import eu.cessda.cvmanager.domain.UserAgency;
import eu.cessda.cvmanager.domain.UserAgencyRole;


/**
 * This class is what used by Spring Security to look for users.
 * By this class, the place and the way of storing users are transparent to Spring Security.
 * 
 * @author Karam
 */
@Service
public class UserDetailesServiceImpl implements UserDetailsService{

	@Autowired
	private DBservices db;
	
	/**
	 * A redefinition of the "loadUserByUsername" method. Since we store users in a database, the implementation of this method
	 * is based on the "org.gesis.security.db.DBservices" class.
	 */
	@Override
	@Transactional( readOnly = true )
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		User u = db.getUserByUsername(username);
		Set<UserAgency> userAgencies = u.getUserAgencies();
		Set<UserAgencyRole> userAgencyRoles = u.getUserAgencyRoles();
				
		Set<GrantedAuthority> ga = new HashSet<>();
		
//		for(Role r : u.getRoles()){
//			ga.add(new SimpleGrantedAuthority(r.getName()));
//		}
		
		// get DSpace authority
//		org.dspace.rest.common.User dspaceUser = new org.dspace.rest.common.User();
//		dspaceUser.setEmail( u.getUsername() );
//		dspaceUser.setPassword( u.getDspacepassword() );
//		StatusFull full = DSpaceRestClient.login( dspaceUser );
//		
		// create userDetails
		eu.cessda.cvmanager.security.UserDetails userDetails = new eu.cessda.cvmanager.security.UserDetails(u.getUsername(), u.getPassword(), u.isEnable(), true, true, !u.isLocked(), ga);
//		userDetails.setStatusFull(full);
		userDetails.setFirstName(u.getFirstName());
		userDetails.setLastName(u.getLastName());
//		userDetails.setAffiliation(u.getAffiliation());
		
		return userDetails;
	}
}
