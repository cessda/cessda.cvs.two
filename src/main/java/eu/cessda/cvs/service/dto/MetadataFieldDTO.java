package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.domain.enumeration.ObjectType;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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

    private Set<MetadataValueDTO> metadataValues = new LinkedHashSet<>();

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

    public Set<MetadataValueDTO> getMetadataValues() {
        return metadataValues;
    }

    public void setMetadataValues(Set<MetadataValueDTO> metadataValues) {
        this.metadataValues = metadataValues;
    }

    public MetadataFieldDTO addMetadataValue(MetadataValueDTO metadataValue ) {
        this.metadataValues.add(metadataValue);
        return this;
    }

    public MetadataFieldDTO removeMetadataValue( MetadataValueDTO metadataValue ) {
        this.metadataValues.remove(metadataValue);
        return this;
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
