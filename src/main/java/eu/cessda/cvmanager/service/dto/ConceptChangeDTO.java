package eu.cessda.cvmanager.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Lob;

/**
 * A DTO for the ConceptChange entity.
 */
public class ConceptChangeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull
    @Size(max = 60)
    private String changeType;

    @Lob
    private String description;

    private Long userId;
    
    private LocalDateTime date;

    private Long conceptId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConceptId() {
        return conceptId;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }
    
    public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConceptChangeDTO conceptChangeDTO = (ConceptChangeDTO) o;
        if(conceptChangeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), conceptChangeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ConceptChangeDTO{" +
            "id=" + getId() +
            ", changeType='" + getChangeType() + "'" +
            ", description='" + getDescription() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
