package eu.cessda.cvmanager.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.cessda.cvmanager.domain.enumeration.ObjectType;

/**
 * A Metadata Field code.
 */
@Entity
@Table(name = "metadata_field")
public class MetadataField implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull
    @Column(name = "metadata_key", length = 240, nullable = false, unique = true)
    private String metadataKey;
    
    @Lob
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "object_type")
    private ObjectType objectType;
    
    @OneToMany(fetch=FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
    		mappedBy = "metadataField", orphanRemoval = true)
    private Set<MetadataValue> metadataValues = new HashSet<>();

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

	public Set<MetadataValue> getMetadataValues() {
		return metadataValues;
	}

	public void setMetadataValues(Set<MetadataValue> metadataValues) {
		this.metadataValues = metadataValues;
	}
	
	
}
