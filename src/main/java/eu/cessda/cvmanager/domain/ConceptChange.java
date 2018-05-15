package eu.cessda.cvmanager.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A ConceptChange.
 */
@Entity
@Table(name = "concept_change")
@Document(indexName = "conceptchange")
public class ConceptChange implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column(name = "change_type", length = 60, nullable = false)
    private String changeType;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    private Concept concept;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChangeType() {
        return changeType;
    }

    public ConceptChange changeType(String changeType) {
        this.changeType = changeType;
        return this;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getDescription() {
        return description;
    }

    public ConceptChange description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public ConceptChange userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Concept getConcept() {
        return concept;
    }

    public ConceptChange concept(Concept concept) {
        this.concept = concept;
        return this;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
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
        ConceptChange conceptChange = (ConceptChange) o;
        if (conceptChange.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), conceptChange.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ConceptChange{" +
            "id=" + getId() +
            ", changeType='" + getChangeType() + "'" +
            ", description='" + getDescription() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}