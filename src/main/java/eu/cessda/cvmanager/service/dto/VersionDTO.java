package eu.cessda.cvmanager.service.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.*;

import org.gesis.wts.domain.enumeration.Language;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
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

    @Size(max = 20)
    private String number;

    @Lob
    private String summary;

    private String uri;

    private String canonicalUri;

    private String uriSl;

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

    private String versionChanges;

    @Lob
    private String discussionNotes;

    private String copyright;

    private String license;

    private Long licenseId;

    private String citation;

    private String ddiUsage;

    private String translateAgency;

    private String translateAgencyLink;

    private Long vocabularyId;

    @JsonIgnore
    private Set<ConceptDTO> concepts = new HashSet<>();

    public VersionDTO() {}

    public static VersionDTO createDraft() {
		return new VersionDTO().withStatus(Status.DRAFT);
	}

	public VersionDTO withStatus(Status status) {
		this.status = status.toString();
		return this;
	}

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
    
    public String getDetailLanguage() {
        return Language.valueOfEnum( language ).toStringCapitalized();
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
    
    public String getSkosUri() {
    	String skosUri = uri;
    	if( itemType.equals( ItemType.TL.toString() ))
    		skosUri = uriSl;
    	if(skosUri == null )
    		return"";
    	
    	int index = skosUri.lastIndexOf("/");
    	String versionInfo = skosUri.substring( index + 1 );
    	index = skosUri.substring( 0, index ).lastIndexOf("/");
    	
    	return skosUri.substring(0, index) + "_" + versionInfo;
    }

	public String getCanonicalUri() {
		if( canonicalUri == null )
			return null;
		
		String formattedUrn = removeLanguageInformation(canonicalUri);
		// format any TL URN to SL
		int index = canonicalUri.lastIndexOf(":");
		if( formattedUrn.substring(index + 1).chars().filter( ch -> ch == '.').count() == 2L) {
			// remove last dot
			index = canonicalUri.lastIndexOf(".");
			formattedUrn = formattedUrn.substring(0, index);
		}
		return formattedUrn;
	}

	public void setCanonicalUri(String canonicalUri) {
		this.canonicalUri = canonicalUri;
	}

    public String getUriSl() {
		return uriSl;
	}

	public void setUriSl(String uriSl) {
		this.uriSl = uriSl;
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

	public boolean isConceptExist(String newConcept) {
		Optional<ConceptDTO> findFirst = concepts.stream().filter( p -> p.getNotation().equals(newConcept)).findFirst();
		if( findFirst.isPresent())
			return true;
		return false;
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

	public String getVersionChanges() {
		return versionChanges;
	}

	public void setVersionChanges(String versionChanges) {
		this.versionChanges = versionChanges;
	}

	public String getDiscussionNotes() {
		return discussionNotes;
	}

	public void setDiscussionNotes(String discussionNotes) {
		this.discussionNotes = discussionNotes;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	public String getCitation() {
		return citation;
	}
	
	public String getCitationPreview() {
		if( citation == null)
			return "";
		return citation.replaceFirst(title, "<i>" + title + "</i>");
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public String getDdiUsage() {
		return ddiUsage;
	}

	public void setDdiUsage(String ddiUsage) {
		this.ddiUsage = ddiUsage;
	}

	public String getTranslateAgency() {
		return translateAgency;
	}

	public void setTranslateAgency(String translateAgency) {
		this.translateAgency = translateAgency;
	}

	public String getTranslateAgencyLink() {
		return translateAgencyLink;
	}

	public void setTranslateAgencyLink(String translateAgencyLink) {
		this.translateAgencyLink = translateAgencyLink;
	}

	public boolean isInitialVersion() {
		if(isPersisted()) {
			if( initialVersion != null && initialVersion.equals( id ))
				return true;
		}
		return false;
	}

	public List<ConceptDTO> getSortedConcepts(){
		if( concepts == null )
			return null;
		return concepts.stream().sorted(Comparator.nullsLast(( c1, c2) -> {
			if( c1.getPosition() == null && c2.getPosition() == null )
				return 0;
			else if( c1.getPosition() == null  )
				return -1;
			else if( c2.getPosition() == null  )
				return 1;
			return c1.getPosition().compareTo( c2.getPosition() );
		})).collect( Collectors.toList());
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

		if( status != null ) {
			return versionDTOs.stream()
					.sorted( ( v1, v2) -> v2.getPreviousVersion().compareTo( v1.getPreviousVersion() ))
					.filter( p -> language.equalsIgnoreCase( p.language ))
					.filter( p -> status.equalsIgnoreCase( p.status ))
					.findFirst();
		}else
			return versionDTOs
					.stream()
					.sorted( ( v1, v2) -> v2.getPreviousVersion().compareTo( v1.getPreviousVersion() ))
					.filter( p -> language.equalsIgnoreCase( p.language )).findFirst();

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
						.sorted( (v1, v2) -> v2.getPreviousVersion().compareTo( v1.getPreviousVersion()))
						.collect( Collectors.toList());
			versionMap.put( eachVersions.getKey(), eachValues);
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
	
	public static VersionDTO clone (VersionDTO targetVersion, VersionDTO slVersion, Long userId, String versionNumber, Long agencylicenseId, String agencyUri,
			String ddiUsage) {
		return clone(new VersionDTO(), targetVersion, slVersion, userId, versionNumber, agencylicenseId, agencyUri, ddiUsage);
	}

	public static VersionDTO clone (VersionDTO newVersion, VersionDTO targetVersion, VersionDTO slVersion, Long userId, String versionNumber, Long agencylicenseId, String agencyUri,
			String ddiUsage) {
		// generate uri
		newVersion.setUri( agencyUri + targetVersion.getNotation() + "/" + targetVersion.getLanguage());

		newVersion.setStatus( Status.DRAFT.toString());
		newVersion.setItemType( targetVersion.getItemType() );
		newVersion.setLanguage( targetVersion.getLanguage() );
//		newVersion.setLastModified( LocalDateTime.now());
		newVersion.setNumber(versionNumber);
		newVersion.setNotation( targetVersion.getNotation() );
		newVersion.setTitle( targetVersion.getTitle() );
		newVersion.setDefinition( targetVersion.getDefinition() );
		newVersion.setPreviousVersion( targetVersion.getId() );
		newVersion.setInitialVersion( targetVersion.getInitialVersion() );
		newVersion.setCreator( userId );
		newVersion.setVocabularyId( targetVersion.getVocabularyId());
		newVersion.setSummary( targetVersion.getSummary());
		newVersion.setLicenseId(agencylicenseId);
		if( ddiUsage != null )
			newVersion.setDdiUsage( ddiUsage );
		else
			newVersion.setDdiUsage( targetVersion.getDdiUsage() );
		newVersion.setTranslateAgency( targetVersion.getTranslateAgency() );
		newVersion.setTranslateAgencyLink( targetVersion.getTranslateAgencyLink() );
		// get Sl-concepts if the cloning is for TL
		Map<Long, ConceptDTO> slConceptMap = null;
		if( targetVersion.getItemType().equals( ItemType.TL.toString()) && slVersion != null) {
			slConceptMap = slVersion.getConceptWithKeyPreviousConceptAsMap();
		}
			
		// clone concepts as well
		for(ConceptDTO targetConcept: targetVersion.getConcepts()) {
			ConceptDTO slConcept = null;
			ConceptDTO newConcept = null;
			
			if( targetVersion.getItemType().equals( ItemType.TL.toString()) && slConceptMap != null) {
				slConcept = slConceptMap.get( targetConcept.getSlConcept());
				if( slConcept != null ) {
					
					newConcept = ConceptDTO.clone(targetConcept, 
							agencyUri + targetVersion.getNotation() + "#" + slConcept.getNotation() + "/" + targetVersion.getLanguage()
							);
					newConcept.setNotation(slConcept.getNotation());
					newConcept.setSlConcept( slConcept.getId());
					newConcept.setPosition( slConcept.getPosition());
					newConcept.setParent( slConcept.getParent());
				} else
					newConcept = ConceptDTO.clone(targetConcept, 
						agencyUri + targetVersion.getNotation() + "#" + targetConcept.getNotation() + "/" + targetVersion.getLanguage()
						);
			} else {

				newConcept = ConceptDTO.clone(targetConcept, 
					agencyUri + targetVersion.getNotation() + "#" + targetConcept.getNotation() + "/" + targetVersion.getLanguage()
					);
			}
			
			newVersion.addConcept(newConcept);
		}

		return newVersion;
	}

	public static String generateCitation(VersionDTO versionDto, VersionDTO versionDtoSl, String agencyName) {
		StringBuilder citation = new StringBuilder();
		citation.append( agencyName + ". " );
		if( versionDto.getItemType().equals( ItemType.SL.toString()) ) {
			citation.append( "(" + versionDto.getPublicationDate().getYear() + "). ");
			citation.append( versionDto.getTitle() + " (Version " + versionDto.getNumber() + ") [Controlled vocabulary]. ");
			if( !agencyName.toLowerCase().contains("cessda")) {
				citation.append( "CESSDA. ");
			}
			citation.append( versionDto.getCanonicalUri() + ". ");
		}
		else {
			citation.append( "(" + versionDto.getPublicationDate().getYear() + "). ");
			citation.append( versionDto.getTitle() + " [" + versionDtoSl.getTitle()+ "]" + " (Version " + versionDto.getNumber() +
			(versionDto.getTranslateAgency() != null && !versionDto.getTranslateAgency().isEmpty() ? "; " + versionDto.getTranslateAgency() + ", Transl." : "") + ") [Controlled vocabulary]. ");
			if( !agencyName.toLowerCase().contains("cessda")) {
				citation.append( "CESSDA. ");
			}
			citation.append( versionDtoSl.getCanonicalUri() + ". ");
		}

		return citation.toString();
	}

	public Status getEnumStatus() {
		return Status.valueOf( getStatus().toUpperCase());
	}

	public void createSummary( String versionChanges ) {
		setSummary(
			"<strong>" + getNumber() + "</strong>"+
			" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication:" + getPublicationDate() +
			"<br/>Notes:<br/>" + getVersionNotes() + (versionChanges != null && !versionChanges.isEmpty() ? "<br/>Changes:<br/>" + getVersionChanges() : "") 
			+ "<br/><br/>" +
			(getSummary() == null ? "":getSummary().replaceAll("(\r\n|\n)", "<br />")) 
		);
	}
	
	private static String removeLanguageInformation(String canonicalUrlInput) {
		if( canonicalUrlInput == null )
			return null;
		// find last dash from canonicalURI
		int lastDashPosition = canonicalUrlInput.lastIndexOf( "-" );
		// if found and
		if( lastDashPosition == -1 || lastDashPosition < 20)
			return canonicalUrlInput;
		return canonicalUrlInput.substring(0, lastDashPosition);
	}
	
	public Map<String, ConceptDTO> getConceptAsMap(){
		return this.concepts.stream().collect( Collectors.toMap( ConceptDTO::getNotation, Function.identity()));
	}
	public Map<Long, ConceptDTO> getConceptWithKeyPreviousConceptAsMap(){
		Map<Long, ConceptDTO> maps = new LinkedHashMap<>();
		for(ConceptDTO eachConcept : this.concepts ) {
			maps.put( eachConcept.getPreviousConcept(), eachConcept);
		}
		return maps;
	}
}
