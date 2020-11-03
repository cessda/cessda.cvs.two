package eu.cessda.cvs.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.cessda.cvs.domain.CodeSnippet;

import javax.persistence.Lob;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.Concept} entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConceptDTO implements Serializable {


    private Long id;

    private String uri;

    @Size(max = 240)
    private String notation;

    @Lob
    private String title;

    @Lob
    private String definition;

    private Long previousConcept;

    private Long slConcept;

    @Size(max = 240)
    private String parent;

    private Integer position;

    private Long versionId;

    public ConceptDTO(){}

    /**
     * Constructor to generate new concept, defined by Rest API
     * @param codeSnippet the partial information of concept
     */
    public ConceptDTO(CodeSnippet codeSnippet) {
        this.parent = codeSnippet.getParent();
        this.notation = codeSnippet.getNotation();
        this.title = codeSnippet.getTitle();
        this.definition = codeSnippet.getDefinition();
        this.position = codeSnippet.getPosition();
        this.versionId = codeSnippet.getVersionId();
    }

    /**
     * Constructor to generate new TL concept based on SL concept, without any title and definition
     * @param conceptSlDTO the SL concept to be copied
     */
    public ConceptDTO(ConceptDTO conceptSlDTO) {
        this.notation = conceptSlDTO.getNotation();
        this.parent = conceptSlDTO.getParent();
        this.position = conceptSlDTO.getPosition();
        this.slConcept = conceptSlDTO.getId();
    }

    /**
     *
     * @param conceptSlDTO, the based concept to be copied, defined the concept structure
     * @param prevConceptDTO, the previous concept, define the concept information in specific language
     */
    public ConceptDTO( ConceptDTO conceptSlDTO, ConceptDTO prevConceptDTO, Long newVersionId ) {
        this.versionId = newVersionId;
        this.notation = conceptSlDTO.getNotation();
        this.parent = conceptSlDTO.getParent();
        this.position = conceptSlDTO.getPosition();
        this.slConcept = conceptSlDTO.getId();
        // prevConceptDTO is null "only happened in cloning TL concept". it means that new concept is added in SL concept,
        // or the concepts are reordered in SL version, which makes it is not possible to link the TL concept
        // form previous version with tha current SL version via concept notation
        if( prevConceptDTO != null ) {
            this.title = prevConceptDTO.getTitle();
            this.definition = prevConceptDTO.getDefinition();
            this.previousConcept = prevConceptDTO.getId();
        }
        // if cloning TL. (conceptSlDTO = prevConceptDTO, means clone SL concept)
        if( !conceptSlDTO.equals( prevConceptDTO )) {
            this.slConcept = conceptSlDTO.getId();
        }
        // the this.uri is skipped, since it is assigned during publication
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public Long getPreviousConcept() {
        return previousConcept;
    }

    public void setPreviousConcept(Long previousConcept) {
        this.previousConcept = previousConcept;
    }

    public Long getSlConcept() {
        return slConcept;
    }

    public void setSlConcept(Long slConcept) {
        this.slConcept = slConcept;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
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
        if (conceptDTO.getId() == null || getId() == null) {
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
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", title='" + getTitle() + "'" +
            ", definition='" + getDefinition() + "'" +
            ", previousConcept=" + getPreviousConcept() +
            ", slConcept=" + getSlConcept() +
            ", parent='" + getParent() + "'" +
            ", position=" + getPosition() +
            ", versionId=" + getVersionId() +
            "}";
    }
}
