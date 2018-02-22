package eu.cessda.cvmanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A User.
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column( name = "id" )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true )
    private String username;

    @Column(name = "random_username", nullable = false, unique = true )
    private String randomUsername;

    @Column( name = "password", nullable = false )
    private String password;

    @Column( name = "first_name")
    private String firstName;

    @Column( name = "last_name", nullable = false )
    private String lastName;

    @Column( name = "enable", nullable = false )
    private Boolean enable;

    @Column( name = "token", unique = true)
	private String token;
	
	@Column( name = "forgottoken", unique = true)
	private String forgottoken;
	
	@Column( name = "locked", nullable = false )
    private Boolean locked;

	@OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserAgencyRole> userAgencyRoles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserAgency> userAgencies = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRandomUsername() {
        return randomUsername;
    }

    public User randomUsername(String randomUsername) {
        this.randomUsername = randomUsername;
        return this;
    }

    public void setRandomUsername(String randomUsername) {
        this.randomUsername = randomUsername;
    }

    public String getPassword() {
        return password;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public User firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public User lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean isEnable() {
        return enable;
    }

    public User enable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean isLocked() {
        return locked;
    }

    public User locked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Set<UserAgency> getUserAgencies() {
        return userAgencies == null ? Collections.emptySet() : userAgencies;
    }

    public User userAgencies(Set<UserAgency> userAgencies) {
        this.userAgencies = userAgencies;
        return this;
    }

    public User addUserAgency(UserAgency userAgency) {
        this.userAgencies.add(userAgency);
        userAgency.setUser(this);
        return this;
    }

    public User removeUserAgency(UserAgency userAgency) {
        this.userAgencies.remove(userAgency);
        userAgency.setUser(null);
        return this;
    }

    public void setUserAgencies(Set<UserAgency> userAgencies) {
        this.userAgencies = userAgencies;
    }

    public Set<UserAgencyRole> getUserAgencyRoles() {
        return userAgencyRoles == null ?  Collections.emptySet() : userAgencyRoles;
    }

    public User userAgencyRoles(Set<UserAgencyRole> userAgencyRoles) {
        this.userAgencyRoles = userAgencyRoles;
        return this;
    }

    public User addUserAgencyRole(UserAgencyRole userAgencyRole) {
        this.userAgencyRoles.add(userAgencyRole);
        userAgencyRole.setUser(this);
        return this;
    }

    public User removeUserAgencyRole(UserAgencyRole userAgencyRole) {
        this.userAgencyRoles.remove(userAgencyRole);
        userAgencyRole.setUser(null);
        return this;
    }

    public void setUserAgencyRoles(Set<UserAgencyRole> userAgencyRoles) {
        this.userAgencyRoles = userAgencyRoles;
    }
    
    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getForgottoken() {
		return forgottoken;
	}

	public void setForgottoken(String forgottoken) {
		this.forgottoken = forgottoken;
	}

	public Boolean getEnable() {
		return enable;
	}

	public Boolean getLocked() {
		return locked;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        if (user.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", randomUsername='" + getRandomUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", enable='" + isEnable() + "'" +
            ", locked='" + isLocked() + "'" +
            "}";
    }
}
