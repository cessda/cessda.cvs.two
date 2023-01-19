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

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import eu.cessda.cvs.utils.VersionNumber;

/**
 * A VocabularyBase.
 */
@MappedSuperclass
public class VocabularyBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    @Field( type = FieldType.Keyword )
    private String status;

    @Size(max = 240)
    @Column(name = "uri", length = 240)
    @Field( type = FieldType.Keyword )
    private String uri;

    @NotNull
    @Size(max = 240)
    @Column(name = "notation", length = 240, nullable = false)
    @Field( type = FieldType.Keyword )
    private String notation;

    @NotNull
    @Column(name = "version_number", nullable = false)
    @Field( type = FieldType.Keyword )
    @Type( type = "eu.cessda.cvs.utils.VersionNumberType")
    private VersionNumber versionNumber;

    @Column(name = "initial_publication")
    private Long initialPublication;

    @Column(name = "previous_publication")
    private Long previousPublication;

    @Column(name = "archived")
    private Boolean archived=false;

    @Column(name = "withdrawn")
    private Boolean withdrawn=false;

    @Column(name = "discoverable")
    private Boolean discoverable=true;

    @NotNull
    @Size(max = 20)
    @Column(name = "source_language", length = 20, nullable = false)
    @Field( type = FieldType.Keyword )
    private String sourceLanguage;

    @NotNull
    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    @NotNull
    @Column(name = "agency_name", nullable = false)
    @Field( type = FieldType.Keyword )
    private String agencyName;

    @Column(name = "agency_logo")
    @Field( type = FieldType.Keyword )
    private String agencyLogo;

    @Transient
    private String selectedLang;

    @Column(name = "publication_date")
    @Field( type = FieldType.Date, format = DateFormat.date )
    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" )
    private LocalDate publicationDate;

    @Column(name = "last_modified")
    @Field( type = FieldType.Date, format = DateFormat.date_hour_minute_second )
    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss" )
    private ZonedDateTime lastModified;

    @Lob
    @Column(name = "notes")
    @Field( type = FieldType.Text )
    private String notes;

    @Lob
    @Column(name = "title_all")
    @Mapping(mappingPath = "/mappings/titleAll.json")
	private String titleAll;

    @Size(max = 20)
    @Column(name = "version_sq", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionSq;

    @Lob
    @Column(name = "title_sq")
    @Mapping(mappingPath = "/mappings/titleSq.json")
	private String titleSq;

    @Lob
    @Column(name = "definition_sq")
    @Field( type = FieldType.Text, store = true )
    private String definitionSq;

    @Size(max = 20)
    @Column(name = "version_bs", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionBs;

    @Lob
    @Column(name = "title_bs")
    @Mapping(mappingPath = "/mappings/titleBs.json")
	private String titleBs;

    @Lob
    @Column(name = "definition_bs")
    @Field( type = FieldType.Text, store = true )
    private String definitionBs;

    @Size(max = 20)
    @Column(name = "version_bg", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionBg;

    @Lob
    @Column(name = "title_bg")
    @Mapping(mappingPath = "/mappings/titleBg.json")
	private String titleBg;

    @Lob
    @Column(name = "definition_bg")
    @Field( type = FieldType.Text, store = true, analyzer = "bulgarian", searchAnalyzer = "bulgarian" )
    private String definitionBg;

    @Size(max = 20)
    @Column(name = "version_hr", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionHr;

    @Lob
    @Column(name = "title_hr")
    @Mapping(mappingPath = "/mappings/titleHr.json")
	private String titleHr;

    @Lob
    @Column(name = "definition_hr")
    @Field( type = FieldType.Text, store = true )
    private String definitionHr;

    @Size(max = 20)
    @Column(name = "version_cs", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionCs;

    @Lob
    @Column(name = "title_cs")
    @Mapping(mappingPath = "/mappings/titleCs.json")
	private String titleCs;

    @Lob
    @Column(name = "definition_cs")
    @Field( type = FieldType.Text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String definitionCs;

    @Size(max = 20)
    @Column(name = "version_da", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionDa;

    @Lob
    @Column(name = "title_da")
    @Mapping(mappingPath = "/mappings/titleDa.json")
	private String titleDa;

    @Lob
    @Column(name = "definition_da")
    @Field( type = FieldType.Text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String definitionDa;

    @Size(max = 20)
    @Column(name = "version_nl", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionNl;

    @Lob
    @Column(name = "title_nl")
    @Mapping(mappingPath = "/mappings/titleNl.json")
	private String titleNl;

    @Lob
    @Column(name = "definition_nl")
    @Field( type = FieldType.Text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String definitionNl;

    @Size(max = 20)
    @Column(name = "version_en", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionEn;

    @Lob
    @Column(name = "title_en")
    @Mapping(mappingPath = "/mappings/titleEn.json")
	private String titleEn;

    @Lob
    @Column(name = "definition_en")
    @Field( type = FieldType.Text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String definitionEn;

    @Size(max = 20)
    @Column(name = "version_et", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionEt;

    @Lob
    @Column(name = "title_et")
    @Mapping(mappingPath = "/mappings/titleEt.json")
	private String titleEt;

    @Lob
    @Column(name = "definition_et")
    @Field( type = FieldType.Text, store = true )
    private String definitionEt;

    @Size(max = 20)
    @Column(name = "version_fi", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionFi;

    @Lob
    @Column(name = "title_fi")
    @Mapping(mappingPath = "/mappings/titleFi.json")
	private String titleFi;

    @Lob
    @Column(name = "definition_fi")
    @Field( type = FieldType.Text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String definitionFi;

    @Size(max = 20)
    @Column(name = "version_fr", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionFr;

    @Lob
    @Column(name = "title_fr")
    @Mapping(mappingPath = "/mappings/titleFr.json")
	private String titleFr;

    @Lob
    @Column(name = "definition_fr")
    @Field( type = FieldType.Text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String definitionFr;

    @Size(max = 20)
    @Column(name = "version_de", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionDe;

    @Lob
    @Column(name = "title_de")
    @Mapping(mappingPath = "/mappings/titleDe.json")
	private String titleDe;

    @Lob
    @Column(name = "definition_de")
    @Field( type = FieldType.Text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String definitionDe;

    @Size(max = 20)
    @Column(name = "version_el", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionEl;

    @Lob
    @Column(name = "title_el")
    @Mapping(mappingPath = "/mappings/titleEl.json")
	private String titleEl;

    @Lob
    @Column(name = "definition_el")
    @Field( type = FieldType.Text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String definitionEl;

    @Size(max = 20)
    @Column(name = "version_hu", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionHu;

    @Lob
    @Column(name = "title_hu")
    @Mapping(mappingPath = "/mappings/titleHu.json")
	private String titleHu;

    @Lob
    @Column(name = "definition_hu")
    @Field( type = FieldType.Text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String definitionHu;

    @Size(max = 20)
    @Column(name = "version_it", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionIt;

    @Lob
    @Column(name = "title_it")
    @Mapping(mappingPath = "/mappings/titleIt.json")
	private String titleIt;

    @Lob
    @Column(name = "definition_it")
    @Field( type = FieldType.Text, store = true, analyzer = "italian", searchAnalyzer = "italian" )
    private String definitionIt;

    @Size(max = 20)
    @Column(name = "version_ja", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionJa;

    @Lob
    @Column(name = "title_ja")
    @Mapping(mappingPath = "/mappings/titleJa.json")
	private String titleJa;

    @Lob
    @Column(name = "definition_ja")
    @Field( type = FieldType.Text, store = true )
    private String definitionJa;

    @Size(max = 20)
    @Column(name = "version_lt", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionLt;

    @Lob
    @Column(name = "title_lt")
    @Mapping(mappingPath = "/mappings/titleLt.json")
	private String titleLt;

    @Lob
    @Column(name = "definition_lt")
    @Field( type = FieldType.Text, store = true )
    private String definitionLt;

    @Size(max = 20)
    @Column(name = "version_mk", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionMk;

    @Lob
    @Column(name = "title_mk")
    @Mapping(mappingPath = "/mappings/titleMk.json")
	private String titleMk;

    @Lob
    @Column(name = "definition_mk")
    @Field( type = FieldType.Text, store = true )
    private String definitionMk;

    @Size(max = 20)
    @Column(name = "version_no", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionNo;

    @Lob
    @Column(name = "title_no")
    @Mapping(mappingPath = "/mappings/titleNo.json")
	private String titleNo;

    @Lob
    @Column(name = "definition_no")
    @Field( type = FieldType.Text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String definitionNo;

    @Size(max = 20)
    @Column(name = "version_pl", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionPl;

    @Lob
    @Column(name = "title_pl")
    @Mapping(mappingPath = "/mappings/titlePl.json")
	private String titlePl;

    @Lob
    @Column(name = "definition_pl")
    @Field( type = FieldType.Text, store = true )
    private String definitionPl;

    @Size(max = 20)
    @Column(name = "version_pt", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionPt;

    @Lob
    @Column(name = "title_pt")
    @Mapping(mappingPath = "/mappings/titlePt.json")
	private String titlePt;

    @Lob
    @Column(name = "definition_pt")
    @Field( type = FieldType.Text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String definitionPt;

    @Size(max = 20)
    @Column(name = "version_ro", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionRo;

    @Lob
    @Column(name = "title_ro")
    @Mapping(mappingPath = "/mappings/titleRo.json")
	private String titleRo;

    @Lob
    @Column(name = "definition_ro")
    @Field( type = FieldType.Text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String definitionRo;

    @Size(max = 20)
    @Column(name = "version_ru", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionRu;

    @Lob
    @Column(name = "title_ru")
    @Mapping(mappingPath = "/mappings/titleRu.json")
	private String titleRu;

    @Lob
    @Column(name = "definition_ru")
    @Field( type = FieldType.Text, store = true, analyzer = "russian", searchAnalyzer = "russian" )
    private String definitionRu;

    @Size(max = 20)
    @Column(name = "version_sr", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionSr;

    @Lob
    @Column(name = "title_sr")
    @Mapping(mappingPath = "/mappings/titleSr.json")
	private String titleSr;

    @Lob
    @Column(name = "definition_sr")
    @Field( type = FieldType.Text, store = true )
    private String definitionSr;

    @Size(max = 20)
    @Column(name = "version_sk", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionSk;

    @Lob
    @Column(name = "title_sk")
    @Mapping(mappingPath = "/mappings/titleSk.json")
	private String titleSk;

    @Lob
    @Column(name = "definition_sk")
    @Field( type = FieldType.Text, store = true )
    private String definitionSk;

    @Size(max = 20)
    @Column(name = "version_sl", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionSl;

    @Lob
    @Column(name = "title_sl")
    @Mapping(mappingPath = "/mappings/titleSl.json")
	private String titleSl;

    @Lob
    @Column(name = "definition_sl")
    @Field( type = FieldType.Text, store = true )
    private String definitionSl;

    @Size(max = 20)
    @Column(name = "version_es", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionEs;

    @Lob
    @Column(name = "title_es")
    @Mapping(mappingPath = "/mappings/titleEs.json")
	private String titleEs;

    @Lob
    @Column(name = "definition_es")
    @Field( type = FieldType.Text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String definitionEs;

    @Size(max = 20)
    @Column(name = "version_sv", length = 20)
    @Field( type = FieldType.Keyword )
    private String versionSv;

    @Lob
    @Column(name = "title_sv")
    @Mapping(mappingPath = "/mappings/titleSv.json")
	private String titleSv;

    @Lob
    @Column(name = "definition_sv")
    @Field( type = FieldType.Text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String definitionSv;

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

    public VocabularyBase status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUri() {
        return uri;
    }

    public VocabularyBase uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNotation() {
        return notation;
    }

    public VocabularyBase notation(String notation) {
        this.notation = notation;
        return this;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    @JsonIgnore
    public VersionNumber getVersionNumber() {
        return versionNumber;
    }

    @JsonGetter("versionNumber")
    public String getVersionNumberAsString() {
        return VersionNumber.toString(versionNumber);
    }
    
    public VocabularyBase versionNumber(VersionNumber versionNumber) {
        this.versionNumber = versionNumber;
        return this;
    }

    @JsonIgnore
    public void setVersionNumber(VersionNumber versionNumber) {
        this.versionNumber = versionNumber;
    }

    @JsonSetter("versionNumber")
    public void setVersionNumber(String str) {
        setVersionNumber(VersionNumber.fromString(str));
    }

    public Long getInitialPublication() {
        return initialPublication;
    }

    public VocabularyBase initialPublication(Long initialPublication) {
        this.initialPublication = initialPublication;
        return this;
    }

    public void setInitialPublication(Long initialPublication) {
        this.initialPublication = initialPublication;
    }

    public Long getPreviousPublication() {
        return previousPublication;
    }

    public VocabularyBase previousPublication(Long previousPublication) {
        this.previousPublication = previousPublication;
        return this;
    }

    public void setPreviousPublication(Long previousPublication) {
        this.previousPublication = previousPublication;
    }

    public Boolean isArchived() {
        return archived;
    }

    public VocabularyBase archived(Boolean archived) {
        this.archived = archived;
        return this;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean isWithdrawn() {
        return withdrawn;
    }

    public VocabularyBase withdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
        return this;
    }

    public void setWithdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public Boolean isDiscoverable() {
        return discoverable;
    }

    public VocabularyBase discoverable(Boolean discoverable) {
        this.discoverable = discoverable;
        return this;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public VocabularyBase sourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
        return this;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public VocabularyBase agencyId(Long agencyId) {
        this.agencyId = agencyId;
        return this;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public VocabularyBase agencyName(String agencyName) {
        this.agencyName = agencyName;
        return this;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyLogo() {
        return agencyLogo;
    }

    public VocabularyBase agencyLogo(String agencyLogo) {
        this.agencyLogo = agencyLogo;
        return this;
    }

    public void setAgencyLogo(String agencyLogo) {
        this.agencyLogo = agencyLogo;
    }

    public String getSelectedLang()
    {
        return selectedLang;
    }

    public void setSelectedLang( String selectedLang )
    {
        this.selectedLang = selectedLang;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public VocabularyBase publicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
        return this;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public VocabularyBase lastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getNotes() {
        return notes;
    }

    public VocabularyBase notes(String notes) {
        this.notes = notes;
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTitleAll() {
        return titleAll;
    }

    public void setTitleAll(String titleAll) {
        this.titleAll = titleAll;
    }

    public String getVersionSq() {
        return versionSq;
    }

    public VocabularyBase versionSq(String versionSq) {
        this.versionSq = versionSq;
        return this;
    }

    public void setVersionSq(String versionSq) {
        this.versionSq = versionSq;
    }

    public String getTitleSq() {
        return titleSq;
    }

    public VocabularyBase titleSq(String titleSq) {
        this.titleSq = titleSq;
        return this;
    }

    public void setTitleSq(String titleSq) {
        this.titleSq = titleSq;
    }

    public String getDefinitionSq() {
        return definitionSq;
    }

    public VocabularyBase definitionSq(String definitionSq) {
        this.definitionSq = definitionSq;
        return this;
    }

    public void setDefinitionSq(String definitionSq) {
        this.definitionSq = definitionSq;
    }

    public String getVersionBs() {
        return versionBs;
    }

    public VocabularyBase versionBs(String versionBs) {
        this.versionBs = versionBs;
        return this;
    }

    public void setVersionBs(String versionBs) {
        this.versionBs = versionBs;
    }

    public String getTitleBs() {
        return titleBs;
    }

    public VocabularyBase titleBs(String titleBs) {
        this.titleBs = titleBs;
        return this;
    }

    public void setTitleBs(String titleBs) {
        this.titleBs = titleBs;
    }

    public String getDefinitionBs() {
        return definitionBs;
    }

    public VocabularyBase definitionBs(String definitionBs) {
        this.definitionBs = definitionBs;
        return this;
    }

    public void setDefinitionBs(String definitionBs) {
        this.definitionBs = definitionBs;
    }

    public String getVersionBg() {
        return versionBg;
    }

    public VocabularyBase versionBg(String versionBg) {
        this.versionBg = versionBg;
        return this;
    }

    public void setVersionBg(String versionBg) {
        this.versionBg = versionBg;
    }

    public String getTitleBg() {
        return titleBg;
    }

    public VocabularyBase titleBg(String titleBg) {
        this.titleBg = titleBg;
        return this;
    }

    public void setTitleBg(String titleBg) {
        this.titleBg = titleBg;
    }

    public String getDefinitionBg() {
        return definitionBg;
    }

    public VocabularyBase definitionBg(String definitionBg) {
        this.definitionBg = definitionBg;
        return this;
    }

    public void setDefinitionBg(String definitionBg) {
        this.definitionBg = definitionBg;
    }

    public String getVersionHr() {
        return versionHr;
    }

    public VocabularyBase versionHr(String versionHr) {
        this.versionHr = versionHr;
        return this;
    }

    public void setVersionHr(String versionHr) {
        this.versionHr = versionHr;
    }

    public String getTitleHr() {
        return titleHr;
    }

    public VocabularyBase titleHr(String titleHr) {
        this.titleHr = titleHr;
        return this;
    }

    public void setTitleHr(String titleHr) {
        this.titleHr = titleHr;
    }

    public String getDefinitionHr() {
        return definitionHr;
    }

    public VocabularyBase definitionHr(String definitionHr) {
        this.definitionHr = definitionHr;
        return this;
    }

    public void setDefinitionHr(String definitionHr) {
        this.definitionHr = definitionHr;
    }

    public String getVersionCs() {
        return versionCs;
    }

    public VocabularyBase versionCs(String versionCs) {
        this.versionCs = versionCs;
        return this;
    }

    public void setVersionCs(String versionCs) {
        this.versionCs = versionCs;
    }

    public String getTitleCs() {
        return titleCs;
    }

    public VocabularyBase titleCs(String titleCs) {
        this.titleCs = titleCs;
        return this;
    }

    public void setTitleCs(String titleCs) {
        this.titleCs = titleCs;
    }

    public String getDefinitionCs() {
        return definitionCs;
    }

    public VocabularyBase definitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
        return this;
    }

    public void setDefinitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
    }

    public String getVersionDa() {
        return versionDa;
    }

    public VocabularyBase versionDa(String versionDa) {
        this.versionDa = versionDa;
        return this;
    }

    public void setVersionDa(String versionDa) {
        this.versionDa = versionDa;
    }

    public String getTitleDa() {
        return titleDa;
    }

    public VocabularyBase titleDa(String titleDa) {
        this.titleDa = titleDa;
        return this;
    }

    public void setTitleDa(String titleDa) {
        this.titleDa = titleDa;
    }

    public String getDefinitionDa() {
        return definitionDa;
    }

    public VocabularyBase definitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
        return this;
    }

    public void setDefinitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
    }

    public String getVersionNl() {
        return versionNl;
    }

    public VocabularyBase versionNl(String versionNl) {
        this.versionNl = versionNl;
        return this;
    }

    public void setVersionNl(String versionNl) {
        this.versionNl = versionNl;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public VocabularyBase titleNl(String titleNl) {
        this.titleNl = titleNl;
        return this;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getDefinitionNl() {
        return definitionNl;
    }

    public VocabularyBase definitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
        return this;
    }

    public void setDefinitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
    }

    public String getVersionEn() {
        return versionEn;
    }

    public VocabularyBase versionEn(String versionEn) {
        this.versionEn = versionEn;
        return this;
    }

    public void setVersionEn(String versionEn) {
        this.versionEn = versionEn;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public VocabularyBase titleEn(String titleEn) {
        this.titleEn = titleEn;
        return this;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDefinitionEn() {
        return definitionEn;
    }

    public VocabularyBase definitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
        return this;
    }

    public void setDefinitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
    }

    public String getVersionEt() {
        return versionEt;
    }

    public VocabularyBase versionEt(String versionEt) {
        this.versionEt = versionEt;
        return this;
    }

    public void setVersionEt(String versionEt) {
        this.versionEt = versionEt;
    }

    public String getTitleEt() {
        return titleEt;
    }

    public VocabularyBase titleEt(String titleEt) {
        this.titleEt = titleEt;
        return this;
    }

    public void setTitleEt(String titleEt) {
        this.titleEt = titleEt;
    }

    public String getDefinitionEt() {
        return definitionEt;
    }

    public VocabularyBase definitionEt(String definitionEt) {
        this.definitionEt = definitionEt;
        return this;
    }

    public void setDefinitionEt(String definitionEt) {
        this.definitionEt = definitionEt;
    }

    public String getVersionFi() {
        return versionFi;
    }

    public VocabularyBase versionFi(String versionFi) {
        this.versionFi = versionFi;
        return this;
    }

    public void setVersionFi(String versionFi) {
        this.versionFi = versionFi;
    }

    public String getTitleFi() {
        return titleFi;
    }

    public VocabularyBase titleFi(String titleFi) {
        this.titleFi = titleFi;
        return this;
    }

    public void setTitleFi(String titleFi) {
        this.titleFi = titleFi;
    }

    public String getDefinitionFi() {
        return definitionFi;
    }

    public VocabularyBase definitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
        return this;
    }

    public void setDefinitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
    }

    public String getVersionFr() {
        return versionFr;
    }

    public VocabularyBase versionFr(String versionFr) {
        this.versionFr = versionFr;
        return this;
    }

    public void setVersionFr(String versionFr) {
        this.versionFr = versionFr;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public VocabularyBase titleFr(String titleFr) {
        this.titleFr = titleFr;
        return this;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getDefinitionFr() {
        return definitionFr;
    }

    public VocabularyBase definitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
        return this;
    }

    public void setDefinitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
    }

    public String getVersionDe() {
        return versionDe;
    }

    public VocabularyBase versionDe(String versionDe) {
        this.versionDe = versionDe;
        return this;
    }

    public void setVersionDe(String versionDe) {
        this.versionDe = versionDe;
    }

    public String getTitleDe() {
        return titleDe;
    }

    public VocabularyBase titleDe(String titleDe) {
        this.titleDe = titleDe;
        return this;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getDefinitionDe() {
        return definitionDe;
    }

    public VocabularyBase definitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
        return this;
    }

    public void setDefinitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
    }

    public String getVersionEl() {
        return versionEl;
    }

    public VocabularyBase versionEl(String versionEl) {
        this.versionEl = versionEl;
        return this;
    }

    public void setVersionEl(String versionEl) {
        this.versionEl = versionEl;
    }

    public String getTitleEl() {
        return titleEl;
    }

    public VocabularyBase titleEl(String titleEl) {
        this.titleEl = titleEl;
        return this;
    }

    public void setTitleEl(String titleEl) {
        this.titleEl = titleEl;
    }

    public String getDefinitionEl() {
        return definitionEl;
    }

    public VocabularyBase definitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
        return this;
    }

    public void setDefinitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
    }

    public String getVersionHu() {
        return versionHu;
    }

    public VocabularyBase versionHu(String versionHu) {
        this.versionHu = versionHu;
        return this;
    }

    public void setVersionHu(String versionHu) {
        this.versionHu = versionHu;
    }

    public String getTitleHu() {
        return titleHu;
    }

    public VocabularyBase titleHu(String titleHu) {
        this.titleHu = titleHu;
        return this;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getDefinitionHu() {
        return definitionHu;
    }

    public VocabularyBase definitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
        return this;
    }

    public void setDefinitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
    }

    public String getVersionIt() {
        return versionIt;
    }

    public VocabularyBase versionIt(String versionIt) {
        this.versionIt = versionIt;
        return this;
    }

    public void setVersionIt(String versionIt) {
        this.versionIt = versionIt;
    }

    public String getTitleIt() {
        return titleIt;
    }

    public VocabularyBase titleIt(String titleIt) {
        this.titleIt = titleIt;
        return this;
    }

    public void setTitleIt(String titleIt) {
        this.titleIt = titleIt;
    }

    public String getDefinitionIt() {
        return definitionIt;
    }

    public VocabularyBase definitionIt(String definitionIt) {
        this.definitionIt = definitionIt;
        return this;
    }

    public void setDefinitionIt(String definitionIt) {
        this.definitionIt = definitionIt;
    }

    public String getVersionJa() {
        return versionJa;
    }

    public VocabularyBase versionJa(String versionJa) {
        this.versionJa = versionJa;
        return this;
    }

    public void setVersionJa(String versionJa) {
        this.versionJa = versionJa;
    }

    public String getTitleJa() {
        return titleJa;
    }

    public VocabularyBase titleJa(String titleJa) {
        this.titleJa = titleJa;
        return this;
    }

    public void setTitleJa(String titleJa) {
        this.titleJa = titleJa;
    }

    public String getDefinitionJa() {
        return definitionJa;
    }

    public VocabularyBase definitionJa(String definitionJa) {
        this.definitionJa = definitionJa;
        return this;
    }

    public void setDefinitionJa(String definitionJa) {
        this.definitionJa = definitionJa;
    }

    public String getVersionLt() {
        return versionLt;
    }

    public VocabularyBase versionLt(String versionLt) {
        this.versionLt = versionLt;
        return this;
    }

    public void setVersionLt(String versionLt) {
        this.versionLt = versionLt;
    }

    public String getTitleLt() {
        return titleLt;
    }

    public VocabularyBase titleLt(String titleLt) {
        this.titleLt = titleLt;
        return this;
    }

    public void setTitleLt(String titleLt) {
        this.titleLt = titleLt;
    }

    public String getDefinitionLt() {
        return definitionLt;
    }

    public VocabularyBase definitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
        return this;
    }

    public void setDefinitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
    }

    public String getVersionMk() {
        return versionMk;
    }

    public VocabularyBase versionMk(String versionMk) {
        this.versionMk = versionMk;
        return this;
    }

    public void setVersionMk(String versionMk) {
        this.versionMk = versionMk;
    }

    public String getTitleMk() {
        return titleMk;
    }

    public VocabularyBase titleMk(String titleMk) {
        this.titleMk = titleMk;
        return this;
    }

    public void setTitleMk(String titleMk) {
        this.titleMk = titleMk;
    }

    public String getDefinitionMk() {
        return definitionMk;
    }

    public VocabularyBase definitionMk(String definitionMk) {
        this.definitionMk = definitionMk;
        return this;
    }

    public void setDefinitionMk(String definitionMk) {
        this.definitionMk = definitionMk;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public VocabularyBase versionNo(String versionNo) {
        this.versionNo = versionNo;
        return this;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getTitleNo() {
        return titleNo;
    }

    public VocabularyBase titleNo(String titleNo) {
        this.titleNo = titleNo;
        return this;
    }

    public void setTitleNo(String titleNo) {
        this.titleNo = titleNo;
    }

    public String getDefinitionNo() {
        return definitionNo;
    }

    public VocabularyBase definitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
        return this;
    }

    public void setDefinitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
    }

    public String getVersionPl() {
        return versionPl;
    }

    public VocabularyBase versionPl(String versionPl) {
        this.versionPl = versionPl;
        return this;
    }

    public void setVersionPl(String versionPl) {
        this.versionPl = versionPl;
    }

    public String getTitlePl() {
        return titlePl;
    }

    public VocabularyBase titlePl(String titlePl) {
        this.titlePl = titlePl;
        return this;
    }

    public void setTitlePl(String titlePl) {
        this.titlePl = titlePl;
    }

    public String getDefinitionPl() {
        return definitionPl;
    }

    public VocabularyBase definitionPl(String definitionPl) {
        this.definitionPl = definitionPl;
        return this;
    }

    public void setDefinitionPl(String definitionPl) {
        this.definitionPl = definitionPl;
    }

    public String getVersionPt() {
        return versionPt;
    }

    public VocabularyBase versionPt(String versionPt) {
        this.versionPt = versionPt;
        return this;
    }

    public void setVersionPt(String versionPt) {
        this.versionPt = versionPt;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public VocabularyBase titlePt(String titlePt) {
        this.titlePt = titlePt;
        return this;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getDefinitionPt() {
        return definitionPt;
    }

    public VocabularyBase definitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
        return this;
    }

    public void setDefinitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
    }

    public String getVersionRo() {
        return versionRo;
    }

    public VocabularyBase versionRo(String versionRo) {
        this.versionRo = versionRo;
        return this;
    }

    public void setVersionRo(String versionRo) {
        this.versionRo = versionRo;
    }

    public String getTitleRo() {
        return titleRo;
    }

    public VocabularyBase titleRo(String titleRo) {
        this.titleRo = titleRo;
        return this;
    }

    public void setTitleRo(String titleRo) {
        this.titleRo = titleRo;
    }

    public String getDefinitionRo() {
        return definitionRo;
    }

    public VocabularyBase definitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
        return this;
    }

    public void setDefinitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
    }

    public String getVersionRu() {
        return versionRu;
    }

    public VocabularyBase versionRu(String versionRu) {
        this.versionRu = versionRu;
        return this;
    }

    public void setVersionRu(String versionRu) {
        this.versionRu = versionRu;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public VocabularyBase titleRu(String titleRu) {
        this.titleRu = titleRu;
        return this;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public String getDefinitionRu() {
        return definitionRu;
    }

    public VocabularyBase definitionRu(String definitionRu) {
        this.definitionRu = definitionRu;
        return this;
    }

    public void setDefinitionRu(String definitionRu) {
        this.definitionRu = definitionRu;
    }

    public String getVersionSr() {
        return versionSr;
    }

    public VocabularyBase versionSr(String versionSr) {
        this.versionSr = versionSr;
        return this;
    }

    public void setVersionSr(String versionSr) {
        this.versionSr = versionSr;
    }

    public String getTitleSr() {
        return titleSr;
    }

    public VocabularyBase titleSr(String titleSr) {
        this.titleSr = titleSr;
        return this;
    }

    public void setTitleSr(String titleSr) {
        this.titleSr = titleSr;
    }

    public String getDefinitionSr() {
        return definitionSr;
    }

    public VocabularyBase definitionSr(String definitionSr) {
        this.definitionSr = definitionSr;
        return this;
    }

    public void setDefinitionSr(String definitionSr) {
        this.definitionSr = definitionSr;
    }

    public String getVersionSk() {
        return versionSk;
    }

    public VocabularyBase versionSk(String versionSk) {
        this.versionSk = versionSk;
        return this;
    }

    public void setVersionSk(String versionSk) {
        this.versionSk = versionSk;
    }

    public String getTitleSk() {
        return titleSk;
    }

    public VocabularyBase titleSk(String titleSk) {
        this.titleSk = titleSk;
        return this;
    }

    public void setTitleSk(String titleSk) {
        this.titleSk = titleSk;
    }

    public String getDefinitionSk() {
        return definitionSk;
    }

    public VocabularyBase definitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
        return this;
    }

    public void setDefinitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
    }

    public String getVersionSl() {
        return versionSl;
    }

    public VocabularyBase versionSl(String versionSl) {
        this.versionSl = versionSl;
        return this;
    }

    public void setVersionSl(String versionSl) {
        this.versionSl = versionSl;
    }

    public String getTitleSl() {
        return titleSl;
    }

    public VocabularyBase titleSl(String titleSl) {
        this.titleSl = titleSl;
        return this;
    }

    public void setTitleSl(String titleSl) {
        this.titleSl = titleSl;
    }

    public String getDefinitionSl() {
        return definitionSl;
    }

    public VocabularyBase definitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
        return this;
    }

    public void setDefinitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
    }

    public String getVersionEs() {
        return versionEs;
    }

    public VocabularyBase versionEs(String versionEs) {
        this.versionEs = versionEs;
        return this;
    }

    public void setVersionEs(String versionEs) {
        this.versionEs = versionEs;
    }

    public String getTitleEs() {
        return titleEs;
    }

    public VocabularyBase titleEs(String titleEs) {
        this.titleEs = titleEs;
        return this;
    }

    public void setTitleEs(String titleEs) {
        this.titleEs = titleEs;
    }

    public String getDefinitionEs() {
        return definitionEs;
    }

    public VocabularyBase definitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
        return this;
    }

    public void setDefinitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
    }

    public String getVersionSv() {
        return versionSv;
    }

    public VocabularyBase versionSv(String versionSv) {
        this.versionSv = versionSv;
        return this;
    }

    public void setVersionSv(String versionSv) {
        this.versionSv = versionSv;
    }

    public String getTitleSv() {
        return titleSv;
    }

    public VocabularyBase titleSv(String titleSv) {
        this.titleSv = titleSv;
        return this;
    }

    public void setTitleSv(String titleSv) {
        this.titleSv = titleSv;
    }

    public String getDefinitionSv() {
        return definitionSv;
    }

    public VocabularyBase definitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
        return this;
    }

    public void setDefinitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        VocabularyBase vocabulary = (VocabularyBase) o;
        if ( vocabulary.getId() == null || getId() == null )
        {
            return false;
        }
        return Objects.equals( getId(), vocabulary.getId() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( getId() );
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", versionNumber='" + getVersionNumber() + "'" +
            ", initialPublication=" + getInitialPublication() +
            ", previousPublication=" + getPreviousPublication() +
            ", archived='" + isArchived() + "'" +
            ", withdrawn='" + isWithdrawn() + "'" +
            ", discoverable='" + isDiscoverable() + "'" +
            ", sourceLanguage='" + getSourceLanguage() + "'" +
            ", agencyId='" + getAgencyId() + "'" +
            ", agencyName='" + getAgencyName() + "'" +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", notes='" + getNotes() + "'" +
            ", titleAll='" + getTitleAll() + "'" +
            ", versionSq='" + getVersionSq() + "'" +
            ", titleSq='" + getTitleSq() + "'" +
            ", definitionSq='" + getDefinitionSq() + "'" +
            ", versionBs='" + getVersionBs() + "'" +
            ", titleBs='" + getTitleBs() + "'" +
            ", definitionBs='" + getDefinitionBs() + "'" +
            ", versionBg='" + getVersionBg() + "'" +
            ", titleBg='" + getTitleBg() + "'" +
            ", definitionBg='" + getDefinitionBg() + "'" +
            ", versionHr='" + getVersionHr() + "'" +
            ", titleHr='" + getTitleHr() + "'" +
            ", definitionHr='" + getDefinitionHr() + "'" +
            ", versionCs='" + getVersionCs() + "'" +
            ", titleCs='" + getTitleCs() + "'" +
            ", definitionCs='" + getDefinitionCs() + "'" +
            ", versionDa='" + getVersionDa() + "'" +
            ", titleDa='" + getTitleDa() + "'" +
            ", definitionDa='" + getDefinitionDa() + "'" +
            ", versionNl='" + getVersionNl() + "'" +
            ", titleNl='" + getTitleNl() + "'" +
            ", definitionNl='" + getDefinitionNl() + "'" +
            ", versionEn='" + getVersionEn() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            ", definitionEn='" + getDefinitionEn() + "'" +
            ", versionEt='" + getVersionEt() + "'" +
            ", titleEt='" + getTitleEt() + "'" +
            ", definitionEt='" + getDefinitionEt() + "'" +
            ", versionFi='" + getVersionFi() + "'" +
            ", titleFi='" + getTitleFi() + "'" +
            ", definitionFi='" + getDefinitionFi() + "'" +
            ", versionFr='" + getVersionFr() + "'" +
            ", titleFr='" + getTitleFr() + "'" +
            ", definitionFr='" + getDefinitionFr() + "'" +
            ", versionDe='" + getVersionDe() + "'" +
            ", titleDe='" + getTitleDe() + "'" +
            ", definitionDe='" + getDefinitionDe() + "'" +
            ", versionEl='" + getVersionEl() + "'" +
            ", titleEl='" + getTitleEl() + "'" +
            ", definitionEl='" + getDefinitionEl() + "'" +
            ", versionHu='" + getVersionHu() + "'" +
            ", titleHu='" + getTitleHu() + "'" +
            ", definitionHu='" + getDefinitionHu() + "'" +
            ", versionIt='" + getVersionIt() + "'" +
            ", titleIt='" + getTitleIt() + "'" +
            ", definitionIt='" + getDefinitionIt() + "'" +
            ", versionJa='" + getVersionJa() + "'" +
            ", titleJa='" + getTitleJa() + "'" +
            ", definitionJa='" + getDefinitionJa() + "'" +
            ", versionLt='" + getVersionLt() + "'" +
            ", titleLt='" + getTitleLt() + "'" +
            ", definitionLt='" + getDefinitionLt() + "'" +
            ", versionMk='" + getVersionMk() + "'" +
            ", titleMk='" + getTitleMk() + "'" +
            ", definitionMk='" + getDefinitionMk() + "'" +
            ", versionNo='" + getVersionNo() + "'" +
            ", titleNo='" + getTitleNo() + "'" +
            ", definitionNo='" + getDefinitionNo() + "'" +
            ", versionPl='" + getVersionPl() + "'" +
            ", titlePl='" + getTitlePl() + "'" +
            ", definitionPl='" + getDefinitionPl() + "'" +
            ", versionPt='" + getVersionPt() + "'" +
            ", titlePt='" + getTitlePt() + "'" +
            ", definitionPt='" + getDefinitionPt() + "'" +
            ", versionRo='" + getVersionRo() + "'" +
            ", titleRo='" + getTitleRo() + "'" +
            ", definitionRo='" + getDefinitionRo() + "'" +
            ", versionRu='" + getVersionRu() + "'" +
            ", titleRu='" + getTitleRu() + "'" +
            ", definitionRu='" + getDefinitionRu() + "'" +
            ", versionSr='" + getVersionSr() + "'" +
            ", titleSr='" + getTitleSr() + "'" +
            ", definitionSr='" + getDefinitionSr() + "'" +
            ", versionSk='" + getVersionSk() + "'" +
            ", titleSk='" + getTitleSk() + "'" +
            ", definitionSk='" + getDefinitionSk() + "'" +
            ", versionSl='" + getVersionSl() + "'" +
            ", titleSl='" + getTitleSl() + "'" +
            ", definitionSl='" + getDefinitionSl() + "'" +
            ", versionEs='" + getVersionEs() + "'" +
            ", titleEs='" + getTitleEs() + "'" +
            ", definitionEs='" + getDefinitionEs() + "'" +
            ", versionSv='" + getVersionSv() + "'" +
            ", titleSv='" + getTitleSv() + "'" +
            ", definitionSv='" + getDefinitionSv() + "'" +
            "}";
    }
}
