/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Version.
 */
@Entity
@Table(name = "version")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Version implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @NotNull
    @Size(max = 20)
    @Column(name = "item_type", length = 20, nullable = false)
    private String itemType;

    @Size(max = 20)
    @Column(name = "language", length = 20)
    private String language;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "last_modified")
    private ZonedDateTime lastModified;

    @Size(max = 20)
    @Column(name = "number", length = 20)
    private String number;

    @Column(name = "uri")
    private String uri;

    @Column(name = "canonical_uri")
    private String canonicalUri;

    @Column(name = "uri_sl")
    private String uriSl;

    @Size(max = 240)
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
    @Column(name = "notes")
    private String notes;

    @Lob
    @Column(name = "version_notes")
    private String versionNotes;

    @Lob
    @Column(name = "version_changes")
    private String versionChanges;

    @Lob
    @Column(name = "discussion_notes")
    private String discussionNotes;

    @Column(name = "license")
    private String license;

    @Column(name = "license_id")
    private Long licenseId;

    @Lob
    @Column(name = "citation")
    private String citation;

    @Lob
    @Column(name = "ddi_usage")
    private String ddiUsage;

    @Column(name = "translate_agency")
    private String translateAgency;

    @Column(name = "translate_agency_link")
    private String translateAgencyLink;

    @Column(name = "last_status_change_date")
    private LocalDate lastStatusChangeDate;

    @OneToMany(mappedBy = "version", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Concept> concepts = new HashSet<>();

    @OneToMany(mappedBy = "version", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("versions")
    private Vocabulary vocabulary;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public Version status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemType() {
        return itemType;
    }

    public Version itemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getLanguage() {
        return language;
    }

    public Version language(String language) {
        this.language = language;
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Version creationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public Version publicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
        return this;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public Version lastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getNumber() {
        return number;
    }

    public Version number(String number) {
        this.number = number;
        return this;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUri() {
        return uri;
    }

    public Version uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCanonicalUri() {
        return canonicalUri;
    }

    public Version canonicalUri(String canonicalUri) {
        this.canonicalUri = canonicalUri;
        return this;
    }

    public void setCanonicalUri(String canonicalUri) {
        this.canonicalUri = canonicalUri;
    }

    public String getUriSl() {
        return uriSl;
    }

    public Version uriSl(String uriSl) {
        this.uriSl = uriSl;
        return this;
    }

    public void setUriSl(String uriSl) {
        this.uriSl = uriSl;
    }

    public String getNotation() {
        return notation;
    }

    public Version notation(String notation) {
        this.notation = notation;
        return this;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getTitle() {
        return title;
    }

    public Version title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefinition() {
        return definition;
    }

    public Version definition(String definition) {
        this.definition = definition;
        return this;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Long getPreviousVersion() {
        return previousVersion;
    }

    public Version previousVersion(Long previousVersion) {
        this.previousVersion = previousVersion;
        return this;
    }

    public void setPreviousVersion(Long previousVersion) {
        this.previousVersion = previousVersion;
    }

    public Long getInitialVersion() {
        return initialVersion;
    }

    public Version initialVersion(Long initialVersion) {
        this.initialVersion = initialVersion;
        return this;
    }

    public void setInitialVersion(Long initialVersion) {
        this.initialVersion = initialVersion;
    }

    public Long getCreator() {
        return creator;
    }

    public Version creator(Long creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Long getPublisher() {
        return publisher;
    }

    public Version publisher(Long publisher) {
        this.publisher = publisher;
        return this;
    }

    public void setPublisher(Long publisher) {
        this.publisher = publisher;
    }

    public String getNotes() {
        return notes;
    }

    public Version notes(String notes) {
        this.notes = notes;
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVersionNotes() {
        return versionNotes;
    }

    public Version versionNotes(String versionNotes) {
        this.versionNotes = versionNotes;
        return this;
    }

    public void setVersionNotes(String versionNotes) {
        this.versionNotes = versionNotes;
    }

    public String getVersionChanges() {
        return versionChanges;
    }

    public Version versionChanges(String versionChanges) {
        this.versionChanges = versionChanges;
        return this;
    }

    public void setVersionChanges(String versionChanges) {
        this.versionChanges = versionChanges;
    }

    public String getDiscussionNotes() {
        return discussionNotes;
    }

    public Version discussionNotes(String discussionNotes) {
        this.discussionNotes = discussionNotes;
        return this;
    }

    public void setDiscussionNotes(String discussionNotes) {
        this.discussionNotes = discussionNotes;
    }

    public String getLicense() {
        return license;
    }

    public Version license(String license) {
        this.license = license;
        return this;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public Version licenseId(Long licenseId) {
        this.licenseId = licenseId;
        return this;
    }

    public void setLicenseId(Long licenseId) {
        this.licenseId = licenseId;
    }

    public String getCitation() {
        return citation;
    }

    public Version citation(String citation) {
        this.citation = citation;
        return this;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getDdiUsage() {
        return ddiUsage;
    }

    public Version ddiUsage(String ddiUsage) {
        this.ddiUsage = ddiUsage;
        return this;
    }

    public void setDdiUsage(String ddiUsage) {
        this.ddiUsage = ddiUsage;
    }

    public String getTranslateAgency() {
        return translateAgency;
    }

    public Version translateAgency(String translateAgency) {
        this.translateAgency = translateAgency;
        return this;
    }

    public void setTranslateAgency(String translateAgency) {
        this.translateAgency = translateAgency;
    }

    public String getTranslateAgencyLink() {
        return translateAgencyLink;
    }

    public Version translateAgencyLink(String translateAgencyLink) {
        this.translateAgencyLink = translateAgencyLink;
        return this;
    }

    public void setTranslateAgencyLink(String translateAgencyLink) {
        this.translateAgencyLink = translateAgencyLink;
    }

    public LocalDate getLastStatusChangeDate() {
        return lastStatusChangeDate;
    }

    public void setLastStatusChangeDate(LocalDate lastStatusChangeDate) {
        this.lastStatusChangeDate = lastStatusChangeDate;
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }

    public Version concepts(Set<Concept> concepts) {
        this.concepts = concepts;
        return this;
    }

    public Version addConcept(Concept concept) {
        this.concepts.add(concept);
        concept.setVersion(this);
        return this;
    }

    public Version removeConcept(Concept concept) {
        this.concepts.remove(concept);
        concept.setVersion(null);
        return this;
    }

    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Version comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Version addComment(Comment comment) {
        this.comments.add(comment);
        comment.setVersion(this);
        return this;
    }

    public Version removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setVersion(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public Version vocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        return this;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version)) {
            return false;
        }
        return id != null && id.equals(((Version) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Version{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", itemType='" + getItemType() + "'" +
            ", language='" + getLanguage() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
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
            ", license='" + getLicense() + "'" +
            ", licenseId=" + getLicenseId() +
            ", citation='" + getCitation() + "'" +
            ", translateAgency='" + getTranslateAgency() + "'" +
            ", translateAgencyLink='" + getTranslateAgencyLink() + "'" +
            "}";
    }
}
