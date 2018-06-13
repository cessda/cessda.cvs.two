package eu.cessda.cvmanager.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Concept code.
 */
@Entity
@Table(name = "concept")
public class Concept implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull
    @Column(name = "notation", length = 240, nullable = false)
    private String notation;
    
    @Lob
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "definition")
    private String definition;
    
    @Column(name = "code_id")
    private Long codeId;
    
    @ManyToMany(mappedBy = "concepts")
    @JsonIgnore
    private Set<Version> versions = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

    public Set<Version> getVersions() {
        return versions;
    }

    public Concept versions(Set<Version> versions) {
        this.versions = versions;
        return this;
    }

    public Concept addVersion(Version version) {
        this.versions.add(version);
        version.getConcepts().add(this);
        return this;
    }

    public Concept removeVersion(Version version) {
        this.versions.remove(version);
        version.getConcepts().remove(this);
        return this;
    }

    public void setVersions(Set<Version> versions) {
        this.versions = versions;
    }

	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}
}
