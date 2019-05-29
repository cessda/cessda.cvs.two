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
import javax.persistence.ManyToOne;
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
	
	@Column(name = "uri", length = 255)
    private String uri;
	
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
    
    @Column(name = "previous_concept")
    private Long previousConcept;
    
    @Column(name = "sl_concept")
    private Long slConcept;
    
 // if null, then it is top concept
    @Column(name = "parent", length = 240)
    private String parent;
    
    @Column(name = "\"position\"")
    private Integer position;
    
    @ManyToOne
    private Version version;

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

	public Long getCodeId() {
		return codeId;
	}
	
	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}

	public Long getPreviousConcept() {
		return previousConcept;
	}

	public void setPreviousConcept(Long previousConcept) {
		this.previousConcept = previousConcept;
	}
	
	public Long getSlConcept() {
		return slConcept;
	}

	public void setSlConcept(Long slConcept) {
		this.slConcept = slConcept;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
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
}
