package eu.cessda.cvmanager.security;

import java.util.Collection;

//import org.dspace.rest.common.StatusFull;
import org.springframework.security.core.GrantedAuthority;

public class UserDetails extends org.springframework.security.core.userdetails.User{

	private static final long serialVersionUID = 6854748916727048778L;
//	private StatusFull statusFull;
	private String firstName;
	private String lastName;
	private String affiliation;

	public UserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

//	public StatusFull getStatusFull() {
//		return statusFull;
//	}
//
//	public void setStatusFull(StatusFull statusFull) {
//		this.statusFull = statusFull;
//	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	
}
