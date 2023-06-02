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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.utils.VersionNumber;

import java.io.Serializable;

public class VocabularySnippet implements Serializable {

    private ActionType actionType;
    private Long agencyId;
    private String language;
    private Long vocabularyId;
    private Long versionId;
    private Long versionSlId;
    private Long licenseId;
    private ItemType itemType;
    private String notation;
    private VersionNumber versionNumber;
    private String status;
    private String title;
    private String definition;
    private String notes;
    private String versionNotes;
    private String versionChanges;
    private String discussionNotes;
    private String ddiUsage;
    private String translateAgency;
    private String translateAgencyLink;
    private String changeType;
    private String changeDesc;

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public Long getVersionSlId() {
        return versionSlId;
    }

    public void setVersionSlId(Long versionSlId) {
        this.versionSlId = versionSlId;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Long licenseId) {
        this.licenseId = licenseId;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    @JsonIgnore
    public VersionNumber getVersionNumber() {
        return versionNumber;
    }

    @JsonGetter("versionNumber")
    public String getVersionNumberAsString() {
        if (versionNumber != null) {
            return versionNumber.toString();
        }
        return null;
    }

    @JsonIgnore
    public void setVersionNumber(VersionNumber versionNumber) {
        this.versionNumber = versionNumber;
    }

    @JsonSetter("versionNumber")
    public void setVersionNumberFromString(String str) {
        setVersionNumber(str != null ? VersionNumber.fromString(str) : null);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVersionNotes() {
        return versionNotes;
    }

    public void setVersionNotes(String versionNotes) {
        this.versionNotes = versionNotes;
    }

    public String getVersionChanges() {
        return versionChanges;
    }

    public void setVersionChanges(String versionChanges) {
        this.versionChanges = versionChanges;
    }

    public String getDiscussionNotes() {
        return discussionNotes;
    }

    public void setDiscussionNotes(String discussionNotes) {
        this.discussionNotes = discussionNotes;
    }

    public String getDdiUsage() {
        return ddiUsage;
    }

    public void setDdiUsage(String ddiUsage) {
        this.ddiUsage = ddiUsage;
    }

    public String getTranslateAgency() {
        return translateAgency;
    }

    public void setTranslateAgency(String translateAgency) {
        this.translateAgency = translateAgency;
    }

    public String getTranslateAgencyLink() {
        return translateAgencyLink;
    }

    public void setTranslateAgencyLink(String translateAgencyLink) {
        this.translateAgencyLink = translateAgencyLink;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getChangeDesc() {
        return changeDesc;
    }

    public void setChangeDesc(String changeDesc) {
        this.changeDesc = changeDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VocabularySnippet)) {
            return false;
        }
        return vocabularyId != null && vocabularyId.equals(((VocabularySnippet) o).vocabularyId);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "VocabularyChange{" +
            "actionType='" + getActionType() + "'" +
            ", vocabularyId=" + getVocabularyId() +
            ", versionId=" + getVersionId() +
            ", agencyId='" + getAgencyId() + "'" +
            ", language='" + getLanguage() + "'" +
            ", itemType=" + getItemType() +
            ", notation='" + getNotation() + "'" +
            ", title='" + getTitle() + "'" +
            ", versionNumber='" + getVersionNumber() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
