package eu.cessda.cvs.domain;

import eu.cessda.cvs.security.ActionType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CodeSnippet implements Serializable {
    @NotNull
    private ActionType actionType;
    @NotNull
    private Long versionId;
    private Long conceptId;
    private Long conceptSlId;

    @Size(max = 240)
    private String parent;

    @Size(max = 240)
    private String notation;
    private String title;
    private String definition;
    private Integer position;
    private String changeType;
    private String changeDesc;
    private List<String> conceptStructures = new ArrayList<>();
    private List<Long> conceptStructureIds = new ArrayList<>();

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

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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
            ", conceptId='" + getConceptId() + "'" +
            ", conceptSlId='" + getConceptSlId() + "'" +
            ", parent=" + getParent() +
            ", notation='" + getNotation() + "'" +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
