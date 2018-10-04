package eu.cessda.cvmanager.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Lob;

/**
 * A DTO for the Concept entity.
 */
public class ConceptDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	@Size(max = 255)
	private String uri;

    @NotNull
    @Size(max = 240)
    private String notation;

    @Lob
    private String title;

    @Lob
    private String definition;
    
    private Long codeId;
    
    private Long versionId;
    
    private Long previousConcept;
    
    // if null, then it is top concept
    private String parent;
    
    private Integer position;
    
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
    
	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}
	
	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public Long getPreviousConcept() {
		return previousConcept;
	}

	public void setPreviousConcept(Long previousConcept) {
		this.previousConcept = previousConcept;
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

	public boolean isPersisted() {
		return id != null;
	}
	
	public static Optional<ConceptDTO> getConceptFromCode( Set<ConceptDTO> concepts, Long codeId){
		if( codeId == null)
			return Optional.empty();
		
		return concepts.stream().filter( c -> c.getCodeId().equals(codeId)).findFirst();
	}
	
	public static Optional<ConceptDTO> getConceptFromCode( Set<ConceptDTO> concepts, String notation){
		return concepts.stream().filter( c -> c.getNotation().equals(notation)).findFirst();
	}
	
	public static ConceptDTO clone(ConceptDTO targetConcept, String uri) {
		ConceptDTO newConcept = new ConceptDTO();
		newConcept.setUri(uri);
		newConcept.setNotation( targetConcept.getNotation());
		newConcept.setTitle( targetConcept.getTitle() );
		newConcept.setDefinition( targetConcept.getDefinition() );
		newConcept.setPreviousConcept( targetConcept.getId());
		newConcept.setPosition( targetConcept.getPosition());
		newConcept.setParent( targetConcept.getParent());
		// note the codeId and versionId need to be added later
		
		return newConcept;
	}
	
	public static ConceptDTO getConceptById( Set<ConceptDTO> concepts, Long conceptId) {
		Optional<ConceptDTO> findFirst = concepts.stream().filter( p -> p.getId().equals(conceptId)).findFirst();
		if(findFirst.isPresent())
			return findFirst.get();
		else
			return null;
	}
}
