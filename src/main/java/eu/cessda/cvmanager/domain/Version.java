package eu.cessda.cvmanager.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Version.
 */
@Entity
@Table(name = "version")
public class Version implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "status", length = 20, nullable = false)
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Column(name = "item_type", length = 20, nullable = false)
    @Field(type = FieldType.Keyword)
    private String itemType;
    
    @Size(max = 20)
    @Column(name = "language", length = 20)
    @Field(type = FieldType.Keyword)
    private String language;
    
    @Column(name = "last_modified")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime lastModified;
    
    @Column(name = "publication_date")
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate publicationDate;
    
    @Column(name = "number", length = 20)
    private String number;
    
    @Lob
    @Column(name = "summary")
    private String summary;
    
    @Column(name = "uri", length = 240)
    private String uri;
    
    // Start SL or TL Vocabulary
    @Column(name = "notation", length = 240)
    private String notation;
    
    @Lob
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "definition")
    private String definition;
    
    @Column(name = "previous_version")
    private Long previousVersion;
    
    @Column(name = "initial_version")
    private Long initialVersion;
    
    @Column(name = "creator")
    private Long creator;
    
    @Column(name = "publisher")
    private Long publisher;
    
    @Lob
    @Column(name = "version_notes")
    private String versionNotes;
    
    @Lob
    @Column(name = "discussion_notes")
    private String discussionNotes;
    
//    @ManyToMany(mappedBy = "versions")
//    @JsonIgnore
//    private List<Vocabulary> vocabularies;
    
    @ManyToOne
    private Vocabulary vocabulary;
    
    // in case only for SL type version
    @Column(name = "restrict_role")
    @ElementCollection( targetClass=String.class )
    @Field(type = FieldType.Keyword)
    private List<String> restrictRoles;
    
//    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH  })
//    @JoinTable(name = "version_concept",
//               joinColumns = @JoinColumn(name="version_id", referencedColumnName="id"),
//               inverseJoinColumns = @JoinColumn(name="concept_id", referencedColumnName="id"))
//    private Set<Concept> concepts = new HashSet<>();
    
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
    		mappedBy = "version", orphanRemoval = true)
    private Set<Concept> concepts = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getPreviousVersion() {
		return previousVersion;
	}

	public void setPreviousVersion(Long previousVersion) {
		this.previousVersion = previousVersion;
	}
	
	public Long getInitialVersion() {
		return initialVersion;
	}

	public void setInitialVersion(Long initialVersion) {
		this.initialVersion = initialVersion;
	}

	public Long getCreator() {
		return creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public Long getPublisher() {
		return publisher;
	}

	public void setPublisher(Long publisher) {
		this.publisher = publisher;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
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

//	public List<Vocabulary> getVocabularies() {
//		return vocabularies;
//	}
//
//	public void setVocabularies(List<Vocabulary> vocabularies) {
//		this.vocabularies = vocabularies;
//	}
	
	public List<String> getRestrictRoles() {
		return restrictRoles;
	}

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Vocabulary vocabulary) {
		this.vocabulary = vocabulary;
	}

	public void setRestrictRoles(List<String> restrictRoles) {
		this.restrictRoles = restrictRoles;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Set<Concept> getConcepts() {
		return concepts;
	}

	public void setConcepts(Set<Concept> concepts) {
		this.concepts = concepts;
	}
	
	public String getVersionNotes() {
		return versionNotes;
	}

	public void setVersionNotes(String versionNotes) {
		this.versionNotes = versionNotes;
	}
	
	public String getDiscussionNotes() {
		return discussionNotes;
	}

	public void setDiscussionNotes(String discussionNotes) {
		this.discussionNotes = discussionNotes;
	}

	@Override
    public String toString() {
        return "Version{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", itemType='" + getItemType() + "'" +
            ", language='" + getLanguage() + "'" +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", number='" + getNumber() + "'" +
            ", summary='" + getSummary() + "'" +
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", title='" + getTitle() + "'" +
            ", definition='" + getDefinition() + "'" +
            ", previousVersion=" + getPreviousVersion() +
            ", initialVersion=" + getInitialVersion() +
            ", creator=" + getCreator() +
            ", publisher=" + getPublisher() +
            "}";
    }
}
