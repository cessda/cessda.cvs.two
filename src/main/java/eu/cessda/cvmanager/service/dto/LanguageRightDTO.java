package eu.cessda.cvmanager.service.dto;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import eu.cessda.cvmanager.domain.enumeration.LanguageType;
import eu.cessda.cvmanager.domain.enumeration.Language;

/**
 * A DTO for the LanguageRight entity.
 */
public class LanguageRightDTO implements Serializable {

    private Long id;

    private LanguageType languageType;

    private Language language;

    private Long userAgencyId;
    
    private String firstName;
    
    private String lastName;
    
    private String agencyName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LanguageType getLanguageType() {
        return languageType;
    }

    public void setLanguageType(LanguageType languageType) {
        this.languageType = languageType;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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

        LanguageRightDTO languageRightDTO = (LanguageRightDTO) o;
        if(languageRightDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), languageRightDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LanguageRightDTO{" +
            "id=" + getId() +
            ", languageType='" + getLanguageType() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
    
    public boolean isPersisted() {
		return id != null;
	}
}
