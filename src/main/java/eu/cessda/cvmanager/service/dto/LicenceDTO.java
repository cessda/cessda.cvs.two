package eu.cessda.cvmanager.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A LicenceDTO.
 */
public class LicenceDTO implements Serializable {

    private static final long serialVersionUID = 3723292021697018348L;

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String link;
    
    @NotNull
    @Size(max = 255)
    private String logoLink;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLogoLink() {
		return logoLink;
	}

	public void setLogoLink(String logoLink) {
		this.logoLink = logoLink;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LicenceDTO licanse = (LicenceDTO) o;
        if (licanse.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), licanse.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", link='" + getLink() + "'" +
            ", logoLink=" + getLogoLink() +
            "}";
    }

	public boolean isPersisted() {
		return getId() != null;
	}
}

