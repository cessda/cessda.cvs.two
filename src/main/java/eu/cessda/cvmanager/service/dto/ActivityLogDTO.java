package eu.cessda.cvmanager.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the ActivityLog entity.
 */
public class ActivityLogDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull
    @Size(max = 60)
    private String changeType;

    @Lob
    private String description;

    private Long userId;
    
    private LocalDateTime date;

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

        ActivityLogDTO activityLogDTO = (ActivityLogDTO) o;
        if(activityLogDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activityLogDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ActivityLogDTO{" +
            "id=" + getId() +
            ", changeType='" + getChangeType() + "'" +
            ", description='" + getDescription() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
