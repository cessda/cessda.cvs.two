package eu.cessda.cvmanager.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the Concept entity.
 */
public class ConceptDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull
    @Size(max = 240)
    private String notation;

    @Lob
    private String title;

    @Lob
    private String definition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConceptDTO conceptDTO = (ConceptDTO) o;
        if(conceptDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), conceptDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ConceptDTO{" +
            "id=" + getId() +
            ", notation='" + getNotation() + "'" +
            ", title='" + getTitle() + "'" +
            ", definition='" + getDefinition() + "'" +
            "}";
    }
}
