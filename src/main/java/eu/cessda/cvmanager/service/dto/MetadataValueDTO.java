package eu.cessda.cvmanager.service.dto;

import javax.validation.constraints.*;

import eu.cessda.cvmanager.domain.enumeration.ObjectType;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Lob;

/**
 * A DTO for the MetadataValue entity.
 */
public class MetadataValueDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
    @Lob
    private String value;

    private ObjectType objectType;
    
    private Long objectId;
    
    private Long metadataFieldId;
    
    @Override
    public String toString() {
        return "ConceptDTO{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", objectId='" + getObjectId() + "'" +
            ", objectType='" + getObjectType().toString() + "'" +
            "}";
    }


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public ObjectType getObjectType() {
		return objectType;
	}


	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public Long getObjectId() {
		return objectId;
	}


	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}


	public Long getMetadataFieldId() {
		return metadataFieldId;
	}


	public void setMetadataFieldId(Long metadataFieldId) {
		this.metadataFieldId = metadataFieldId;
	}
    
}
