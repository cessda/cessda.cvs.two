/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A VocabularyChange.
 */
@Entity
@Table(name = "vocabulary_change")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VocabularyChange implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vocabulary_id")
    private Long vocabularyId;

    @Column(name = "version_id")
    private Long versionId;

    @NotNull
    @Size(max = 60)
    @Column(name = "change_type", length = 60, nullable = false)
    private String changeType;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private Long userId;

    @Size(max = 120)
    @Column(name = "user_name", length = 120)
    private String userName;

    @Column(name = "date")
    private LocalDate date;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVocabularyId() {
        return vocabularyId;
    }

    public VocabularyChange vocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
        return this;
    }

    public void setVocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public VocabularyChange versionId(Long versionId) {
        this.versionId = versionId;
        return this;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getChangeType() {
        return changeType;
    }

    public VocabularyChange changeType(String changeType) {
        this.changeType = changeType;
        return this;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getDescription() {
        return description;
    }

    public VocabularyChange description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public VocabularyChange userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public VocabularyChange userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getDate() {
        return date;
    }

    public VocabularyChange date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VocabularyChange)) {
            return false;
        }
        return id != null && id.equals(((VocabularyChange) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "VocabularyChange{" +
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
