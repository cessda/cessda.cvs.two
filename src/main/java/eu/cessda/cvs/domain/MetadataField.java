package eu.cessda.cvs.domain;

import eu.cessda.cvs.domain.enumeration.ObjectType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A MetadataField.
 */
@Entity
@Table(name = "metadata_field")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MetadataField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 240)
    @Column(name = "metadata_key", length = 240, nullable = false, unique = true)
    private String metadataKey;

    @Lob
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type")
    private ObjectType objectType;

    @OneToMany(mappedBy = "metadataField", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<MetadataValue> metadataValues = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public MetadataField metadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
        return this;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    public String getDescription() {
        return description;
    }

    public MetadataField description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public MetadataField objectType(ObjectType objectType) {
        this.objectType = objectType;
        return this;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Set<MetadataValue> getMetadataValues() {
        return metadataValues;
    }

    public MetadataField metadataValues(Set<MetadataValue> metadataValues) {
        this.metadataValues = metadataValues;
        return this;
    }

    public MetadataField addMetadataValue(MetadataValue metadataValue) {
        this.metadataValues.add(metadataValue);
        metadataValue.setMetadataField(this);
        return this;
    }

    public MetadataField removeMetadataValue(MetadataValue metadataValue) {
        this.metadataValues.remove(metadataValue);
        metadataValue.setMetadataField(null);
        return this;
    }

    public void setMetadataValues(Set<MetadataValue> metadataValues) {
        this.metadataValues = metadataValues;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetadataField)) {
            return false;
        }
        return id != null && id.equals(((MetadataField) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "MetadataField{" +
            "id=" + getId() +
            ", metadataKey='" + getMetadataKey() + "'" +
            ", description='" + getDescription() + "'" +
            ", objectType='" + getObjectType() + "'" +
            "}";
    }
}
