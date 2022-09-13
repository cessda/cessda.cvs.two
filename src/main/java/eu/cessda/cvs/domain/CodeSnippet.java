/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.domain;

import eu.cessda.cvs.security.ActionType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CodeSnippet implements Serializable {
    @NotNull
    private ActionType actionType;
    @NotNull
    private Long versionId;
    private Long introducedInVersionId;
    private Long validUntilVersionId;
    private Long conceptId;
    private Long conceptSlId;

    @Size(max = 240)
    private String parent;

    @Size(max = 240)
    private String title;
    private String notation;
    private String definition;
    private Integer position;
    private boolean deprecated;
    private Long replacedById;
    private Long insertionRefConceptId;
    private Integer relPosToRefConcept;
    private String changeType;
    private String changeDesc;
    private List<String> conceptStructures = new ArrayList<>();
    private List<Long> conceptStructureIds = new ArrayList<>();
    private LocalDate validFrom;
    private LocalDate validUntil;

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getIntroducedInVersionId() {
        return introducedInVersionId;
    }

    public void setIntroducedInVersionId(Long introducedInVersionId) {
        this.introducedInVersionId = introducedInVersionId;
    }

    public Long getValidUntilVersionId() {
        return validUntilVersionId;
    }

    public void setValidUntilVersionId(Long validUntilVersionId) {
        this.validUntilVersionId = validUntilVersionId;
    }

    public Long getConceptId() {
        return conceptId;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }

    public Long getConceptSlId() {
        return conceptSlId;
    }

    public void setConceptSlId(Long conceptSlId) {
        this.conceptSlId = conceptSlId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean getDeprecated() {
        return this.deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Long getReplacedById() {
        return this.replacedById;
    }

    public void setReplacedById(Long replacedById) {
        this.replacedById = replacedById;
    }

    public Long getInsertionRefConceptId() {
        return insertionRefConceptId;
    }

    public void setInsertionRefConceptId(Long insertionRefConceptId) {
        this.insertionRefConceptId = insertionRefConceptId;
    }

    public Integer getRelPosToRefConcept() {
        return relPosToRefConcept;
    }

    public void setRelPosToRefConcept(Integer relPosToRefConcept) {
        this.relPosToRefConcept = relPosToRefConcept;
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

    public List<String> getConceptStructures() {
        return conceptStructures;
    }

    public void setConceptStructures(List<String> conceptStructures) {
        this.conceptStructures = conceptStructures;
    }

    public List<Long> getConceptStructureIds() {
        return conceptStructureIds;
    }

    public void setConceptStructureIds(List<Long> conceptStructureIds) {
        this.conceptStructureIds = conceptStructureIds;
    }

    public LocalDate getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return this.validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodeSnippet)) {
            return false;
        }
        return versionId != null && versionId.equals(((CodeSnippet) o).versionId);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "VocabularyChange{" +
            "actionType='" + getActionType() + "'" +
            ", versionId=" + getVersionId() +
            ", introducedInVersionId=" + getIntroducedInVersionId() +
            ", validUntilVersionId=" + getValidUntilVersionId() +
            ", conceptId='" + getConceptId() + "'" +
            ", conceptSlId='" + getConceptSlId() + "'" +
            ", parent=" + getParent() +
            ", title='" + getTitle() + "'" +
            ", notation='" + getNotation() + "'" +
            ", definition='" + getDefinition() + "'" +
            ", position='" + getPosition() + "'" +
            ", deprecated='" + getDeprecated() + "'" +
            ", replacedById='" + getReplacedById() + "'" +
            ", insertionRefConceptId='" + getInsertionRefConceptId() + "'" +
            ", relPosToRefConcept='" + getRelPosToRefConcept() + "'" +
            ", changeType='" + getChangeType() + "'" +
            ", changeDesc='" + getChangeDesc() + "'" +
            ", conceptStructures='" + getConceptStructures() + "'" +
            ", conceptStructureIds='" + getConceptStructureIds() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            "}";
    }
}
