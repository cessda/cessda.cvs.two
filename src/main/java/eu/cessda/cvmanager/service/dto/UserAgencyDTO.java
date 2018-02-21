package eu.cessda.cvmanager.service.dto;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the UserAgency entity.
 */
public class UserAgencyDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long agencyId;
    
    private String firstName;
    
    private String lastName;
    
    private String agencyName;

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

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
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

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserAgencyDTO userAgencyDTO = (UserAgencyDTO) o;
        if(userAgencyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userAgencyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserAgencyDTO{" +
            "id=" + getId() +
            "}";
    }
    
    public boolean isPersisted() {
		return id != null;
	}
}
