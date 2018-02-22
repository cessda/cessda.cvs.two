package eu.cessda.cvmanager.service.dto;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the UserAgencyRole entity.
 */
public class UserAgencyRoleDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long roleId;

    private Long userAgencyId;
    
    private String firstName;
    
    private String lastName;
    
    private String uaFirstName;
    
    private String uaLastName;

    private String agency;
    
    private String role;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserAgencyId() {
        return userAgencyId;
    }

    public void setUserAgencyId(Long userAgencyId) {
        this.userAgencyId = userAgencyId;
    }

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

	public String getUaFirstName() {
		return uaFirstName;
	}

	public void setUaFirstName(String uaFirstName) {
		this.uaFirstName = uaFirstName;
	}

	public String getUaLastName() {
		return uaLastName;
	}

	public void setUaLastName(String uaLastName) {
		this.uaLastName = uaLastName;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserAgencyRoleDTO userAgencyRoleDTO = (UserAgencyRoleDTO) o;
        if(userAgencyRoleDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userAgencyRoleDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserAgencyRoleDTO{" +
            "id=" + getId() +
            "}";
    }

    public boolean isPersisted() {
		return id != null;
	}
}
