package eu.cessda.cvmanager.service.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.*;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Lob;

/**
 * A DTO for the Version entity.
 */
public class VersionDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull
    private String status;

    @NotNull
    private String itemType;

    @Size(max = 20)
    private String language;
    
    private LocalDateTime lastModified;

    private LocalDate publicationDate;

    @NotNull
    @Size(max = 20)
    private String number;

    @Lob
    private String summary;

    private String uri;

    @Size(max = 240)
    private String notation;

    @Lob
    private String title;

    @Lob
    private String definition;

    private Long previousVersion;

    private Long initialVersion;

    private Long creator;

    private Long publisher;
    
    @Lob
    private String versionNotes;
    
    @Lob
    private String discussionNotes;
    
    private Long vocabularyId;

    private Set<ConceptDTO> concepts = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public Set<ConceptDTO> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<ConceptDTO> concepts) {
        this.concepts = concepts;
    }
    
    public VersionDTO addConcept( ConceptDTO concept ) {
    	if( this.concepts == null )
    		this.concepts = new HashSet<>();
    	
    	this.concepts.add(concept);
    	return this;
    }
    
    public Long getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(Long vocabularyId) {
		this.vocabularyId = vocabularyId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VersionDTO versionDTO = (VersionDTO) o;
        if(versionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), versionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
    
    public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
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
        return "VersionDTO{" +
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
	
	public static VersionDTO getLatestSourceVersion( Set<VersionDTO> versionDTOs) {
		Optional<VersionDTO> latestSourceVersion = versionDTOs
				.stream()
				.sorted( ( v1, v2) -> v2.getPreviousVersion().compareTo( v1.getPreviousVersion() ))
				.filter( p -> p.itemType.equals( ItemType.SL.toString()))
				.findFirst();
		
		if( latestSourceVersion.isPresent() )
			return latestSourceVersion.get();
		else
			return null;
	}
	
	public static Optional<VersionDTO> getLatestVersion( Set<VersionDTO> versionDTOs, String language, String status){
		if( versionDTOs == null || versionDTOs.isEmpty() || language == null)
			return Optional.empty();
		
		Stream<VersionDTO> sortedVersion = versionDTOs
				.stream()
				.sorted( ( v1, v2) -> v2.getPreviousVersion().compareTo( v1.getPreviousVersion() ))
				.filter( p -> language.equalsIgnoreCase( p.language ));
		
		if( status != null )
			sortedVersion
			.filter( p -> status.equalsIgnoreCase( p.status ));
		
		return sortedVersion.findFirst();
	}
		
	public boolean isPersisted() {
		return id != null;
	}
	
	public static Map<String,List<VersionDTO>> generateVersionMap(Set<VersionDTO> versionDTOs){
		Map<String,List<VersionDTO>> versionMap = new TreeMap<>();
		
		// first put into Map, sorted in natural key order
		for(VersionDTO eachVerDTO : versionDTOs) {
			String key = eachVerDTO.getItemType() + "_" + eachVerDTO.getLanguage();
			List<VersionDTO> versionDs = versionMap.get( key );
			if( versionDs  == null ) {
				versionDs = new ArrayList<>();
				versionMap.put( key , versionDs);
			}
			versionDs.add(eachVerDTO);
		}
		
		// sort version list
		for(Map.Entry<String,List<VersionDTO>> eachVersions : versionMap.entrySet()) {
			List<VersionDTO> eachValues = eachVersions.getValue();
			
			eachValues = eachValues.stream()
						.sorted( (v1, v2) -> v1.getPreviousVersion().compareTo( v2.getPreviousVersion()))
						.collect( Collectors.toList());
		}
		
		return versionMap;
		
	}
	
	public static String generateVersionInfo(List<VersionDTO> versionDTOs ) {
		StringBuilder sb = new StringBuilder();
		for(VersionDTO version: versionDTOs) {
			sb.append("<strong> V." + version.getNumber() + "</strong></br>");
			sb.append("Notes:</br>" + version.getVersionNotes() + "</br></br>");
		}
		
		return sb.toString();
	}
	
	public static VersionDTO clone (VersionDTO targetVersion, Long userId, String versionNumber) {
		VersionDTO newVersion = new VersionDTO();
		newVersion.setStatus( Status.DRAFT.toString());
		newVersion.setItemType( targetVersion.getItemType() );
		newVersion.setLanguage( targetVersion.getLanguage() );
		newVersion.setLastModified( LocalDateTime.now());
		newVersion.setNumber(versionNumber);
		newVersion.setNotation( targetVersion.getNotation() );
		newVersion.setTitle( targetVersion.getTitle() );
		newVersion.setDefinition( targetVersion.getDefinition() );
		newVersion.setPreviousVersion( targetVersion.getId() );
		newVersion.setInitialVersion( targetVersion.getInitialVersion() );
		newVersion.setCreator( userId );
		newVersion.setVocabularyId( targetVersion.getVocabularyId());
		newVersion.setVersionNotes( targetVersion.getVersionNotes());
		
		// clone concepts as well
		for(ConceptDTO targetConcept: targetVersion.getConcepts()) {
			ConceptDTO newConcept = ConceptDTO.clone(targetConcept);
			newVersion.addConcept(newConcept);
		}
		
		return newVersion;
	}
}
