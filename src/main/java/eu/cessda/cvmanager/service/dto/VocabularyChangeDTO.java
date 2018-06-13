package eu.cessda.cvmanager.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Lob;

/**
 * A DTO for the VocabularyChange entity.
 */
public class VocabularyChangeDTO implements Serializable {
	
	public static String[] cvChangeTypes = {
			"CV Short Name changed",
			"CV Long Name changed",
			"CV definition amended with meaning change",
			"CV definition rephrased"
	};
	
	public static String[] codeChangeTypes = {
			"Code added",
			"Code removed",
			"Code value changed",
			"Code descriptive term rephrased",
			"Code definition amended with meaning change",
			"Code definition rephrased"
	};

	private static final long serialVersionUID = 1L;

	private Long id;
	
    private Long vocabularyId;
    
    private Long versionId;

    @NotNull
    @Size(max = 60)
    private String changeType;

    @Lob
    private String description;

    private Long userId;
    
    private String userName;
    
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

        VocabularyChangeDTO vocabularyChangeDTO = (VocabularyChangeDTO) o;
        if(vocabularyChangeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vocabularyChangeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VocabularyChangeDTO{" +
            "id=" + getId() +
            ", changeType='" + getChangeType() + "'" +
            ", description='" + getDescription() + "'" +
            ", userId=" + getUserId() +
            "}";
    }

	public Long getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(Long vocabularyId) {
		this.vocabularyId = vocabularyId;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
    
    
}
