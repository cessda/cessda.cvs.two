package eu.cessda.cvmanager.service.dto;

import javax.validation.constraints.*;

import eu.cessda.cvmanager.domain.enumeration.ObjectType;

import java.io.Serializable;

import javax.persistence.Lob;

/**
 * A DTO for the MetadataField entity.
 */
public class MetadataFieldDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull
    @Size(max = 240)
    private String metadataKey;

    @Lob
    private String description;

    private ObjectType objectType;
    

    @Override
    public String toString() {
        return "ConceptDTO{" +
            "id=" + getId() +
            ", metadataKey='" + getMetadataKey() + "'" +
            ", description='" + getDescription() + "'" +
            ", objectType='" + getObjectType().toString() + "'" +
            "}";
    }


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getMetadataKey() {
		return metadataKey;
	}


	public void setMetadataKey(String metadataKey) {
		this.metadataKey = metadataKey;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public ObjectType getObjectType() {
		return objectType;
	}


	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}
    
	
}
