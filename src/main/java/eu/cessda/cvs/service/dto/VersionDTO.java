package eu.cessda.cvs.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.utils.VersionUtils;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * A DTO for the {@link Version} entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String status;

    @NotNull
    @Size(max = 20)
    private String itemType;

    @Size(max = 20)
    private String language;

    private LocalDate publicationDate;

    private ZonedDateTime lastModified;

    @Size(max = 20)
    private String number;

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
    private String notes;

    @Lob
    private String versionNotes;

    @Lob
    private String versionChanges;

    @Lob
    private String discussionNotes;

    private String copyright;

    private String license;

    private String licenseName;

    private String licenseLink;

    private String licenseLogo;

    private Long licenseId;

    @Lob
    private String citation;

    @Lob
    private String ddiUsage;

    private String translateAgency;

    private String translateAgencyLink;

    private Long vocabularyId;

    public VersionDTO(){}

    /**
     * create initial SL version with vocabularyDTO
     *
     * @param vocabularyDTO
     */
    public VersionDTO(VocabularyDTO vocabularyDTO) {
        this.status = Status.DRAFT.toString();
        this.itemType = ItemType.SL.toString();
        this.language = vocabularyDTO.getSourceLanguage();
        this.number = vocabularyDTO.getVersionNumber();
        this.uri = vocabularyDTO.getUri();
        this.notation = vocabularyDTO.getNotation();
        this.title = vocabularyDTO.getTitleByLanguage( this.language );
        this.definition = vocabularyDTO.getDefinitionByLanguage( this.language );
        this.notes = vocabularyDTO.getNotes();
    }

    /**
     * create initial TL version with vocabularySnippet and Version SL
     *
     * @param vocabularySnippet a snippet/partial container for VocabunaryDTO
     * @param versionSlDTO the SL version of want to be created TL version
     */
    public VersionDTO(VocabularySnippet vocabularySnippet, VersionDTO versionSlDTO) {
        this.vocabularyId = versionSlDTO.getVocabularyId();
        this.status = Status.DRAFT.toString();
        this.itemType = ItemType.TL.toString();
        this.language = vocabularySnippet.getLanguage();
        this.number = vocabularySnippet.getVersionNumber();
        this.uriSl = versionSlDTO.getUri();
        this.uri = VersionUtils.getBaseVersionUri( this.uriSl, vocabularySnippet.getNotation() )
            + "/" + vocabularySnippet.getNotation() + "/" + vocabularySnippet.getLanguage();
        this.notation = vocabularySnippet.getNotation();
        this.title = vocabularySnippet.getTitle();
        this.definition = vocabularySnippet.getDefinition();
        this.notes = vocabularySnippet.getNotes();
        this.translateAgency = vocabularySnippet.getTranslateAgency();
        this.translateAgencyLink = vocabularySnippet.getTranslateAgencyLink();
    }

    /**
     * create new version from previous version
     * @param prevVersion the previous version to be cloned
     * @param currentSlVersion the current SL version (for TL version cloning), for SL version cloning this will be null
     */
    public VersionDTO( VersionDTO prevVersion, VersionDTO currentSlVersion) {
        this.vocabularyId = prevVersion.getVocabularyId();
        this.status = Status.DRAFT.toString();
        this.itemType = prevVersion.getItemType();
        this.language = prevVersion.getLanguage();
        this.notation = prevVersion.getNotation();
        this.title = prevVersion.getTitle();
        this.definition = prevVersion.getDefinition();
        this.notes = prevVersion.getNotes();
        this.ddiUsage = prevVersion.getDdiUsage();

        this.previousVersion = prevVersion.getId();
        this.initialVersion = prevVersion.getInitialVersion() == null ? prevVersion.getId() : prevVersion.getInitialVersion();

        // copy licence properties
        this.licenseId = prevVersion.getLicenseId();
        this.license = prevVersion.getLicense();

        this.uri = VersionUtils.getBaseVersionUri( prevVersion.getUri(), this.getNotation() )
            + "/" + this.getNotation() + "/" + this.getLanguage();

        // differentiate VersionNumber, uriSl between SL and TL version cloning
        if ( this.itemType.equals( ItemType.SL.toString())) {
            this.number = VersionUtils.increaseSlVersionByOne(prevVersion.getNumber());
        } else {
            this.number = VersionUtils.increaseTlVersionByOne(prevVersion.getNumber(), currentSlVersion.getNumber());
            this.translateAgency = prevVersion.getTranslateAgency();
            this.translateAgencyLink = prevVersion.getTranslateAgencyLink();
            this.uriSl = currentSlVersion.getUri();
        }

    }

    private Set<ConceptDTO> concepts = new LinkedHashSet<>();

    private Set<CommentDTO> comments = new LinkedHashSet<>();

    private List<Map<String, String>> versionHistories = new ArrayList<>();

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

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCanonicalUri() {
        if( canonicalUri == null )
            return null;

        String formattedUrn = removeLanguageInformation(canonicalUri);
        // format any TL URN to SL
        int index = canonicalUri.lastIndexOf(':');
        if( formattedUrn.substring(index + 1).chars().filter( ch -> ch == '.').count() == 2L) {
            // remove last dot
            index = canonicalUri.lastIndexOf('.');
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getLicenseName() {
        return licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseLink() {
        return licenseLink;
    }

    public void setLicenseLink(String licenseLink) {
        this.licenseLink = licenseLink;
    }

    public String getLicenseLogo() {
        return licenseLogo;
    }

    public void setLicenseLogo(String licenseLogo) {
        this.licenseLogo = licenseLogo;
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

    public Long getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public Set<ConceptDTO> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<ConceptDTO> concepts) {
        this.concepts = concepts;
    }

    public VersionDTO addConcept( ConceptDTO concept ) {
        this.concepts.add(concept);
        return this;
    }

    public VersionDTO removeConcept( ConceptDTO concept ) {
        this.concepts.remove(concept);
        return this;
    }


    public VersionDTO addConceptAt( ConceptDTO concept, Integer position ) {
        List conceptLists = new ArrayList( this.concepts );
        if( position == null || position >= concepts.size()) {
            conceptLists.add(concept);
        }
        else {
            conceptLists.add(position, concept);
            // update concept position information after this index
            Iterator<ConceptDTO> conceptsIterator = conceptLists.iterator();
            while ( conceptsIterator.hasNext() ) {
                ConceptDTO c = conceptsIterator.next();
                if( c.getPosition() >= position && !c.equals(concept)) {
                    c.setPosition( c.getPosition() + 1);
                }
            }
        }
        setConcepts( new LinkedHashSet<>(conceptLists ));
        return this;
    }

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDTO> comments) {
        this.comments = comments;
    }

    public VersionDTO addComment( CommentDTO comment ) {
        this.comments.add(comment);
        return this;
    }

    public VersionDTO removeComment( CommentDTO comment ) {
        this.comments.remove(comment);
        return this;
    }

    public List<Map<String, String>> getVersionHistories() {
        return versionHistories;
    }

    public void setVersionHistories(List<Map<String, String>> versionHistories) {
        this.versionHistories = versionHistories;
    }

    public VersionDTO addVersionHistory( Map<String, String> vHistory ) {
        if( this.versionHistories == null )
            this.versionHistories = new ArrayList<>();

        this.versionHistories.add(vHistory);
        return this;
    }

    public boolean isInitialVersion() {
        boolean isInitialVersion = false;
        if( initialVersion == null )
            isInitialVersion = true;
        else {
            if( isPersisted() && initialVersion.equals( id ) ){
                isInitialVersion = true;
            }
        }
        return isInitialVersion;
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
        if (versionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), versionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VersionDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", itemType='" + getItemType() + "'" +
            ", language='" + getLanguage() + "'" +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", number='" + getNumber() + "'" +
            ", uri='" + getUri() + "'" +
            ", canonicalUri='" + getCanonicalUri() + "'" +
            ", uriSl='" + getUriSl() + "'" +
            ", notation='" + getNotation() + "'" +
            ", title='" + getTitle() + "'" +
            ", definition='" + getDefinition() + "'" +
            ", previousVersion=" + getPreviousVersion() +
            ", initialVersion=" + getInitialVersion() +
            ", creator=" + getCreator() +
            ", publisher=" + getPublisher() +
            ", notes='" + getNotes() + "'" +
//            ", versionNotes='" + getVersionNotes() + "'" +  // commented to shorter debug
//            ", versionChanges='" + getVersionChanges() + "'" + // commented to shorter debug
//            ", discussionNotes='" + getDiscussionNotes() + "'" + // commented to shorter debug
            ", copyright='" + getCopyright() + "'" +
            ", license='" + getLicense() + "'" +
            ", licenseId=" + getLicenseId() +
            ", citation='" + getCitation() + "'" +
//            ", ddiUsage='" + getDdiUsage() + "'" + // commented to shorter debug
            ", translateAgency='" + getTranslateAgency() + "'" +
            ", translateAgencyLink='" + getTranslateAgencyLink() + "'" +
            ", vocabularyId=" + getVocabularyId() +
            "}";
    }

    public void setContentByVocabularySnippet(VocabularySnippet vocabularySnippet) {
        this.title = vocabularySnippet.getTitle();
        this.definition = vocabularySnippet.getDefinition();
        this.notes = vocabularySnippet.getNotes();
    }

    public void prepareSlPublishing(VocabularySnippet vocabularySnippet, LicenceDTO licenceDTO, AgencyDTO agencyDTO) {
        preparePublishing(vocabularySnippet, licenceDTO, agencyDTO);

        StringBuilder citationSb = new StringBuilder();
        citationSb.append( agencyDTO.getName() + ". " );
        citationSb
            .append( "(" + this.publicationDate.getYear() + "). ")
            .append( this.title + " (Version " + this.number + ") [Controlled vocabulary]. ");
        if( !agencyDTO.getName().toLowerCase().contains("cessda")) {
            citationSb.append( "CESSDA. ");
        }
        citationSb.append( this.canonicalUri+ ". ");
        this.citation = citationSb.toString();
    }

    public void prepareTlPublishing(VocabularySnippet vocabularySnippet, LicenceDTO licenceDTO, AgencyDTO agencyDTO) {
        preparePublishing(vocabularySnippet, licenceDTO, agencyDTO);

        StringBuilder citationSb = new StringBuilder();
        citationSb.append( agencyDTO.getName() + ". " );
        citationSb
            .append( "(" + this.publicationDate.getYear() + "). ")
            .append( this.notation + "[" + this.title + "] (Version " + this.number + ") [Controlled vocabulary]. ");
        if( !agencyDTO.getName().toLowerCase().contains("cessda")) {
            citationSb.append( "CESSDA. ");
        }
        citationSb.append( this.canonicalUri+ ". ");
        this.citation = citationSb.toString();
    }

    private void preparePublishing(VocabularySnippet vocabularySnippet, LicenceDTO licenceDTO, AgencyDTO agencyDTO) {
        this.number = vocabularySnippet.getVersionNumber();
        this.publicationDate = LocalDate.now();
        this.licenseId = licenceDTO.getId();
        this.license = licenceDTO.getName();
        this.uri =  this.uri+ "/" + this.number;
        this.canonicalUri = agencyDTO.getCanonicalUri() + this.notation + ":" + this.number;
        this.concepts.forEach(c -> c.setUri( agencyDTO.getUri() + this.notation + "#" + c.getNotation() + "/" + this.language + "/" + this.number));
    }

    /**
     * Get formated Language enum. DO not remove this method, since it is used inside Thymeleaf template
     * @return formated language lise English (en)
     */
    @JsonIgnore
    public String getFormattedLanguage(){
        return Language.getByIso(this.language ).getFormatted();
    }

    @JsonIgnore
    public boolean isPersisted() {
        return id != null;
    }

    private static String removeLanguageInformation(String canonicalUrlInput) {
        if( canonicalUrlInput == null )
            return null;
        // find last dash from canonicalURI
        int lastDashPosition = canonicalUrlInput.lastIndexOf( '-' );
        // if found and
        if(lastDashPosition < 20)
            return canonicalUrlInput;
        return canonicalUrlInput.substring(0, lastDashPosition);
    }

    public ConceptDTO findConceptByNotation(String notation) {
        Optional<ConceptDTO> conceptDTOOptional = concepts.stream().filter(c -> c.getNotation().equals(notation)).findFirst();
        return conceptDTOOptional.isPresent() ? conceptDTOOptional.get() : null;
    }
}
