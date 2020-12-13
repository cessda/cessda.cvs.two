package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.VocabularySnippet;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.VocabularyChange} entity.
 */
public class VocabularyChangeDTO implements Serializable {

    private Long id;

    private Long vocabularyId;

    private Long versionId;

    @NotNull
    @Size(max = 60)
    private String changeType;

    @Lob
    private String description;

    private Long userId;

    @Size(max = 120)
    private String userName;

    private LocalDate date;

    public VocabularyChangeDTO(){
        this.changeType = "DEFAULT_CHANGE_TYPE";
    }

    public VocabularyChangeDTO(CodeSnippet codeSnippet, UserDTO currentUser, Long vocabularyId) {
        this.vocabularyId = vocabularyId;
        this.versionId = codeSnippet.getVersionId();
        this.changeType = codeSnippet.getChangeType();
        this.description = codeSnippet.getChangeDesc() != null ? codeSnippet.getChangeDesc() : "";
        this.date = LocalDate.now();
        if( currentUser != null ) {
            this.userId = currentUser.getId();
            this.userName = currentUser.getName();
        }
    }

    public VocabularyChangeDTO(VocabularySnippet vocabularySnippet, UserDTO currentUser, Long vocabularyId) {
        this.vocabularyId = vocabularyId;
        this.versionId = vocabularySnippet.getVersionId();
        this.changeType = vocabularySnippet.getChangeType();
        this.description = vocabularySnippet.getChangeDesc() != null ? vocabularySnippet.getChangeDesc() : "";
        this.date = LocalDate.now();
        if( currentUser != null ) {
            this.userId = currentUser.getId();
            this.userName = currentUser.getName();
        }
    }

    public VocabularyChangeDTO(UserDTO currentUser, Long vocabularyId, Long versionId, String changeType, String description) {
        this.vocabularyId = vocabularyId;
        this.versionId = versionId;
        this.changeType = changeType;
        this.description = description != null ? description : "";
        this.date = LocalDate.now();
        if( currentUser != null ) {
            this.userId = currentUser.getId();
            this.userName = currentUser.getName();
        }
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
        if (vocabularyChangeDTO.getId() == null || getId() == null) {
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
            ", vocabularyId=" + getVocabularyId() +
            ", versionId=" + getVersionId() +
            ", changeType='" + getChangeType() + "'" +
            ", description='" + getDescription() + "'" +
            ", userId=" + getUserId() +
            ", userName='" + getUserName() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
