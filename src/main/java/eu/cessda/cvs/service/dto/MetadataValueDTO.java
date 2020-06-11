package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.domain.enumeration.ObjectType;

import javax.persistence.Lob;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.MetadataValue} entity.
 */
public class MetadataValueDTO implements Serializable {

    private Long id;

    @Lob
    private String value;

    private ObjectType objectType;

    private Long objectId;

    private Long metadataFieldId;

    private String metadataKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
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

    public String getMetadataKey() {
        return metadataKey;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetadataValueDTO metadataValueDTO = (MetadataValueDTO) o;
        if (metadataValueDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), metadataValueDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MetadataValueDTO{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", objectType='" + getObjectType() + "'" +
            ", objectId=" + getObjectId() +
            ", metadataFieldId=" + getMetadataFieldId() +
            "}";
    }
}
