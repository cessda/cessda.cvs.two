package eu.cessda.cvs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eu.cessda.cvs.domain.enumeration.ObjectType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A MetadataValue.
 */
@Entity
@Table(name = "metadata_value")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MetadataValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identifier")
    private String identifier;

    @Lob
    @Column(name = "value")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type")
    private ObjectType objectType;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "position")
    private Integer position = 0;

    @ManyToOne
    @JsonIgnoreProperties("metadataValues")
    private MetadataField metadataField;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public MetadataValue value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public MetadataValue objectType(ObjectType objectType) {
        this.objectType = objectType;
        return this;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public MetadataValue objectId(Long objectId) {
        this.objectId = objectId;
        return this;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public MetadataField getMetadataField() {
        return metadataField;
    }

    public MetadataValue metadataField(MetadataField metadataField) {
        this.metadataField = metadataField;
        return this;
    }

    public void setMetadataField(MetadataField metadataField) {
        this.metadataField = metadataField;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public MetadataValue identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public MetadataValue position(Integer position) {
        this.position = position;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetadataValue)) {
            return false;
        }
        return id != null && id.equals(((MetadataValue) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "MetadataValue{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", position='" + getPosition() + "'" +
            ", value='" + getValue() + "'" +
            ", objectType='" + getObjectType() + "'" +
            ", objectId=" + getObjectId() +
            "}";
    }
}
