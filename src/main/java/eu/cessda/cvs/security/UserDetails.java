package eu.cessda.cvs.security;

import eu.cessda.cvs.service.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserDetails extends org.springframework.security.core.userdetails.User{

	private static final long serialVersionUID = 6854748916727048778L;
	private UserDTO user;

	public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
