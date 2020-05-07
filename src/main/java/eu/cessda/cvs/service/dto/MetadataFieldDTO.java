package eu.cessda.cvs.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import eu.cessda.cvs.domain.enumeration.ObjectType;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.MetadataField} entity.
 */
public class MetadataFieldDTO implements Serializable {
    
    private Long id;

    @NotNull
    @Size(max = 240)
    private String metadataKey;

    @Lob
    private String description;

    private ObjectType objectType;

    
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetadataFieldDTO metadataFieldDTO = (MetadataFieldDTO) o;
        if (metadataFieldDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), metadataFieldDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MetadataFieldDTO{" +
            "id=" + getId() +
            ", metadataKey='" + getMetadataKey() + "'" +
            ", description='" + getDescription() + "'" +
            ", objectType='" + getObjectType() + "'" +
            "}";
    }
}
