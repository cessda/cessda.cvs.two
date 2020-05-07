package eu.cessda.cvs.service.dto;

import java.io.Serializable;
import java.util.Objects;

import eu.cessda.cvs.domain.UserAgency;
import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.domain.enumeration.Language;

import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.UserAgency} entity.
 */
public class UserAgencyDTO implements Serializable {

    private Long id;

    private AgencyRole agencyRole;

    private String language;

    @NotNull
    private Long userId;

    @NotNull
    private Long agencyId;

    private String agencyName;

    public UserAgencyDTO(){
        // Empty constructor needed for Jackson
    }

    public UserAgencyDTO(UserAgency userAgency) {
        this.id = userAgency.getId();
        this.agencyRole = userAgency.getAgencyRole();
        this.language = userAgency.getLanguage();
        this.agencyId = userAgency.getAgency().getId();
        this.agencyName = userAgency.getAgency().getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgencyRole getAgencyRole() {
        return agencyRole;
    }

    public void setAgencyRole(AgencyRole agencyRole) {
        this.agencyRole = agencyRole;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        if (userAgencyDTO.getId() == null || getId() == null) {
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
            ", agencyRole='" + getAgencyRole() + "'" +
            ", language='" + getLanguage() + "'" +
            ", agencyId=" + getAgencyId() +
            "}";
    }
}
