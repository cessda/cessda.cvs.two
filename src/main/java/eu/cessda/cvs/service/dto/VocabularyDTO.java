package eu.cessda.cvs.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.utils.VocabularyUtils;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A DTO for the {@link Vocabulary} entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VocabularyDTO implements Serializable {

    private Long id;

    public VocabularyDTO() {
        this.archived = false;
        this.withdrawn = false;
        this.discoverable = true;
        this.status = Status.DRAFT.toString();
        this.notation = "NEW_VOCABULARY";
        this.versionNumber = "1.0";
        this.sourceLanguage = "en";
        this.agencyId = 0L;
        this.agencyName = "DEFAULT_AGENCY";
    }

    public VocabularyDTO(VocabularySnippet vocabularySnippet) {
        this();
        this.agencyId = vocabularySnippet.getAgencyId();
        this.notation = vocabularySnippet.getNotation();
        if( vocabularySnippet.getItemType().equals(ItemType.SL))
            this.sourceLanguage = vocabularySnippet.getLanguage();

        setStatusByVocabularySnippet(vocabularySnippet);
        setVersionNumberByVocabularySnippet(vocabularySnippet);
        setContentByVocabularySnippet( vocabularySnippet );
    }

    public static void cleanUpContentForApi(VocabularyDTO vocab) {
        vocab.setArchived( null );
        vocab.setWithdrawn( null );
        vocab.setDiscoverable( null );
        vocab.setAgencyLogo( null );
        vocab.setSelectedLang( null );
    }

    public void setStatusByVocabularySnippet(VocabularySnippet vocabularySnippet){
        this.status = vocabularySnippet.getStatus();
    }
    public void setVersionNumberByVocabularySnippet(VocabularySnippet vocabularySnippet){
        this.versionNumber = vocabularySnippet.getVersionNumber();
    }

    public void setContentByVocabularySnippet(VocabularySnippet vocabularySnippet){
        setVersionByLanguage(vocabularySnippet.getLanguage(), vocabularySnippet.getVersionNumber());
        setTitleDefinition(vocabularySnippet.getTitle(), vocabularySnippet.getDefinition(), vocabularySnippet.getLanguage(), false);
        this.notes = vocabularySnippet.getNotes();
    }

    @NotNull
    @Size(max = 20)
    private String status;

    @Size(max = 240)
    private String uri;

    @NotNull
    @Size(max = 240)
    private String notation;

    @NotNull
    @Size(max = 20)
    private String versionNumber;

    private Long initialPublication;

    private Long previousPublication;

    private Boolean archived;

    private Boolean withdrawn;

    private Boolean discoverable;

    @NotNull
    @Size(max = 20)
    private String sourceLanguage;

    @NotNull
    private Long agencyId;

    @NotNull
    private String agencyName;

    private String agencyLogo;

    private String agencyLink;

    private LocalDate publicationDate;

    private ZonedDateTime lastModified;

    @Lob
    private String notes;

    private Set<String> languages;

    private Set<String> languagesPublished;

    private Set<String> statuses;

    private String selectedLang;

    private Set<CodeDTO> codes = new LinkedHashSet<>();

    private Set<VersionDTO> versions = new LinkedHashSet<>();

    @Size(max = 20)
    private String versionSq;

    @Lob
    private String titleSq;

    @Lob
    private String definitionSq;

    @Size(max = 20)
    private String versionBs;

    @Lob
    private String titleBs;

    @Lob
    private String definitionBs;

    @Size(max = 20)
    private String versionBg;

    @Lob
    private String titleBg;

    @Lob
    private String definitionBg;

    @Size(max = 20)
    private String versionHr;

    @Lob
    private String titleHr;

    @Lob
    private String definitionHr;

    @Size(max = 20)
    private String versionCs;

    @Lob
    private String titleCs;

    @Lob
    private String definitionCs;

    @Size(max = 20)
    private String versionDa;

    @Lob
    private String titleDa;

    @Lob
    private String definitionDa;

    @Size(max = 20)
    private String versionNl;

    @Lob
    private String titleNl;

    @Lob
    private String definitionNl;

    @Size(max = 20)
    private String versionEn;

    @Lob
    private String titleEn;

    @Lob
    private String definitionEn;

    @Size(max = 20)
    private String versionEt;

    @Lob
    private String titleEt;

    @Lob
    private String definitionEt;

    @Size(max = 20)
    private String versionFi;

    @Lob
    private String titleFi;

    @Lob
    private String definitionFi;

    @Size(max = 20)
    private String versionFr;

    @Lob
    private String titleFr;

    @Lob
    private String definitionFr;

    @Size(max = 20)
    private String versionDe;

    @Lob
    private String titleDe;

    @Lob
    private String definitionDe;

    @Size(max = 20)
    private String versionEl;

    @Lob
    private String titleEl;

    @Lob
    private String definitionEl;

    @Size(max = 20)
    private String versionHu;

    @Lob
    private String titleHu;

    @Lob
    private String definitionHu;

    @Size(max = 20)
    private String versionIt;

    @Lob
    private String titleIt;

    @Lob
    private String definitionIt;

    @Size(max = 20)
    private String versionJa;

    @Lob
    private String titleJa;

    @Lob
    private String definitionJa;

    @Size(max = 20)
    private String versionLt;

    @Lob
    private String titleLt;

    @Lob
    private String definitionLt;

    @Size(max = 20)
    private String versionMk;

    @Lob
    private String titleMk;

    @Lob
    private String definitionMk;

    @Size(max = 20)
    private String versionNo;

    @Lob
    private String titleNo;

    @Lob
    private String definitionNo;

    @Size(max = 20)
    private String versionPl;

    @Lob
    private String titlePl;

    @Lob
    private String definitionPl;

    @Size(max = 20)
    private String versionPt;

    @Lob
    private String titlePt;

    @Lob
    private String definitionPt;

    @Size(max = 20)
    private String versionRo;

    @Lob
    private String titleRo;

    @Lob
    private String definitionRo;

    @Size(max = 20)
    private String versionRu;

    @Lob
    private String titleRu;

    @Lob
    private String definitionRu;

    @Size(max = 20)
    private String versionSr;

    @Lob
    private String titleSr;

    @Lob
    private String definitionSr;

    @Size(max = 20)
    private String versionSk;

    @Lob
    private String titleSk;

    @Lob
    private String definitionSk;

    @Size(max = 20)
    private String versionSl;

    @Lob
    private String titleSl;

    @Lob
    private String definitionSl;

    @Size(max = 20)
    private String versionEs;

    @Lob
    private String titleEs;

    @Lob
    private String definitionEs;

    @Size(max = 20)
    private String versionSv;

    @Lob
    private String titleSv;

    @Lob
    private String definitionSv;

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

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Long getInitialPublication() {
        return initialPublication;
    }

    public void setInitialPublication(Long initialPublication) {
        this.initialPublication = initialPublication;
    }

    public Long getPreviousPublication() {
        return previousPublication;
    }

    public void setPreviousPublication(Long previousPublication) {
        this.previousPublication = previousPublication;
    }

    public Boolean isArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean isWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public Boolean isDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyLogo() {
        return agencyLogo;
    }

    public void setAgencyLogo(String agencyLogo) {
        this.agencyLogo = agencyLogo;
    }

    public String getAgencyLink() {
        return agencyLink;
    }

    public void setAgencyLink(String agencyLink) {
        this.agencyLink = agencyLink;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVersionSq() {
        return versionSq;
    }

    public void setVersionSq(String versionSq) {
        this.versionSq = versionSq;
    }

    public String getTitleSq() {
        return titleSq;
    }

    public void setTitleSq(String titleSq) {
        this.titleSq = titleSq;
    }

    public String getDefinitionSq() {
        return definitionSq;
    }

    public void setDefinitionSq(String definitionSq) {
        this.definitionSq = definitionSq;
    }

    public String getVersionBs() {
        return versionBs;
    }

    public void setVersionBs(String versionBs) {
        this.versionBs = versionBs;
    }

    public String getTitleBs() {
        return titleBs;
    }

    public void setTitleBs(String titleBs) {
        this.titleBs = titleBs;
    }

    public String getDefinitionBs() {
        return definitionBs;
    }

    public void setDefinitionBs(String definitionBs) {
        this.definitionBs = definitionBs;
    }

    public String getVersionBg() {
        return versionBg;
    }

    public void setVersionBg(String versionBg) {
        this.versionBg = versionBg;
    }

    public String getTitleBg() {
        return titleBg;
    }

    public void setTitleBg(String titleBg) {
        this.titleBg = titleBg;
    }

    public String getDefinitionBg() {
        return definitionBg;
    }

    public void setDefinitionBg(String definitionBg) {
        this.definitionBg = definitionBg;
    }

    public String getVersionHr() {
        return versionHr;
    }

    public void setVersionHr(String versionHr) {
        this.versionHr = versionHr;
    }

    public String getTitleHr() {
        return titleHr;
    }

    public void setTitleHr(String titleHr) {
        this.titleHr = titleHr;
    }

    public String getDefinitionHr() {
        return definitionHr;
    }

    public void setDefinitionHr(String definitionHr) {
        this.definitionHr = definitionHr;
    }

    public String getVersionCs() {
        return versionCs;
    }

    public void setVersionCs(String versionCs) {
        this.versionCs = versionCs;
    }

    public String getTitleCs() {
        return titleCs;
    }

    public void setTitleCs(String titleCs) {
        this.titleCs = titleCs;
    }

    public String getDefinitionCs() {
        return definitionCs;
    }

    public void setDefinitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
    }

    public String getVersionDa() {
        return versionDa;
    }

    public void setVersionDa(String versionDa) {
        this.versionDa = versionDa;
    }

    public String getTitleDa() {
        return titleDa;
    }

    public void setTitleDa(String titleDa) {
        this.titleDa = titleDa;
    }

    public String getDefinitionDa() {
        return definitionDa;
    }

    public void setDefinitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
    }

    public String getVersionNl() {
        return versionNl;
    }

    public void setVersionNl(String versionNl) {
        this.versionNl = versionNl;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getDefinitionNl() {
        return definitionNl;
    }

    public void setDefinitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
    }

    public String getVersionEn() {
        return versionEn;
    }

    public void setVersionEn(String versionEn) {
        this.versionEn = versionEn;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDefinitionEn() {
        return definitionEn;
    }

    public void setDefinitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
    }

    public String getVersionEt() {
        return versionEt;
    }

    public void setVersionEt(String versionEt) {
        this.versionEt = versionEt;
    }

    public String getTitleEt() {
        return titleEt;
    }

    public void setTitleEt(String titleEt) {
        this.titleEt = titleEt;
    }

    public String getDefinitionEt() {
        return definitionEt;
    }

    public void setDefinitionEt(String definitionEt) {
        this.definitionEt = definitionEt;
    }

    public String getVersionFi() {
        return versionFi;
    }

    public void setVersionFi(String versionFi) {
        this.versionFi = versionFi;
    }

    public String getTitleFi() {
        return titleFi;
    }

    public void setTitleFi(String titleFi) {
        this.titleFi = titleFi;
    }

    public String getDefinitionFi() {
        return definitionFi;
    }

    public void setDefinitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
    }

    public String getVersionFr() {
        return versionFr;
    }

    public void setVersionFr(String versionFr) {
        this.versionFr = versionFr;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getDefinitionFr() {
        return definitionFr;
    }

    public void setDefinitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
    }

    public String getVersionDe() {
        return versionDe;
    }

    public void setVersionDe(String versionDe) {
        this.versionDe = versionDe;
    }

    public String getTitleDe() {
        return titleDe;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getDefinitionDe() {
        return definitionDe;
    }

    public void setDefinitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
    }

    public String getVersionEl() {
        return versionEl;
    }

    public void setVersionEl(String versionEl) {
        this.versionEl = versionEl;
    }

    public String getTitleEl() {
        return titleEl;
    }

    public void setTitleEl(String titleEl) {
        this.titleEl = titleEl;
    }

    public String getDefinitionEl() {
        return definitionEl;
    }

    public void setDefinitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
    }

    public String getVersionHu() {
        return versionHu;
    }

    public void setVersionHu(String versionHu) {
        this.versionHu = versionHu;
    }

    public String getTitleHu() {
        return titleHu;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getDefinitionHu() {
        return definitionHu;
    }

    public void setDefinitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
    }

    public String getVersionIt() {
        return versionIt;
    }

    public void setVersionIt(String versionIt) {
        this.versionIt = versionIt;
    }

    public String getTitleIt() {
        return titleIt;
    }

    public void setTitleIt(String titleIt) {
        this.titleIt = titleIt;
    }

    public String getDefinitionIt() {
        return definitionIt;
    }

    public void setDefinitionIt(String definitionIt) {
        this.definitionIt = definitionIt;
    }

    public String getVersionJa() {
        return versionJa;
    }

    public void setVersionJa(String versionJa) {
        this.versionJa = versionJa;
    }

    public String getTitleJa() {
        return titleJa;
    }

    public void setTitleJa(String titleJa) {
        this.titleJa = titleJa;
    }

    public String getDefinitionJa() {
        return definitionJa;
    }

    public void setDefinitionJa(String definitionJa) {
        this.definitionJa = definitionJa;
    }

    public String getVersionLt() {
        return versionLt;
    }

    public void setVersionLt(String versionLt) {
        this.versionLt = versionLt;
    }

    public String getTitleLt() {
        return titleLt;
    }

    public void setTitleLt(String titleLt) {
        this.titleLt = titleLt;
    }

    public String getDefinitionLt() {
        return definitionLt;
    }

    public void setDefinitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
    }

    public String getVersionMk() {
        return versionMk;
    }

    public void setVersionMk(String versionMk) {
        this.versionMk = versionMk;
    }

    public String getTitleMk() {
        return titleMk;
    }

    public void setTitleMk(String titleMk) {
        this.titleMk = titleMk;
    }

    public String getDefinitionMk() {
        return definitionMk;
    }

    public void setDefinitionMk(String definitionMk) {
        this.definitionMk = definitionMk;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getTitleNo() {
        return titleNo;
    }

    public void setTitleNo(String titleNo) {
        this.titleNo = titleNo;
    }

    public String getDefinitionNo() {
        return definitionNo;
    }

    public void setDefinitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
    }

    public String getVersionPl() {
        return versionPl;
    }

    public void setVersionPl(String versionPl) {
        this.versionPl = versionPl;
    }

    public String getTitlePl() {
        return titlePl;
    }

    public void setTitlePl(String titlePl) {
        this.titlePl = titlePl;
    }

    public String getDefinitionPl() {
        return definitionPl;
    }

    public void setDefinitionPl(String definitionPl) {
        this.definitionPl = definitionPl;
    }

    public String getVersionPt() {
        return versionPt;
    }

    public void setVersionPt(String versionPt) {
        this.versionPt = versionPt;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getDefinitionPt() {
        return definitionPt;
    }

    public void setDefinitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
    }

    public String getVersionRo() {
        return versionRo;
    }

    public void setVersionRo(String versionRo) {
        this.versionRo = versionRo;
    }

    public String getTitleRo() {
        return titleRo;
    }

    public void setTitleRo(String titleRo) {
        this.titleRo = titleRo;
    }

    public String getDefinitionRo() {
        return definitionRo;
    }

    public void setDefinitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
    }

    public String getVersionRu() {
        return versionRu;
    }

    public void setVersionRu(String versionRu) {
        this.versionRu = versionRu;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public String getDefinitionRu() {
        return definitionRu;
    }

    public void setDefinitionRu(String definitionRu) {
        this.definitionRu = definitionRu;
    }

    public String getVersionSr() {
        return versionSr;
    }

    public void setVersionSr(String versionSr) {
        this.versionSr = versionSr;
    }

    public String getTitleSr() {
        return titleSr;
    }

    public void setTitleSr(String titleSr) {
        this.titleSr = titleSr;
    }

    public String getDefinitionSr() {
        return definitionSr;
    }

    public void setDefinitionSr(String definitionSr) {
        this.definitionSr = definitionSr;
    }

    public String getVersionSk() {
        return versionSk;
    }

    public void setVersionSk(String versionSk) {
        this.versionSk = versionSk;
    }

    public String getTitleSk() {
        return titleSk;
    }

    public void setTitleSk(String titleSk) {
        this.titleSk = titleSk;
    }

    public String getDefinitionSk() {
        return definitionSk;
    }

    public void setDefinitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
    }

    public String getVersionSl() {
        return versionSl;
    }

    public void setVersionSl(String versionSl) {
        this.versionSl = versionSl;
    }

    public String getTitleSl() {
        return titleSl;
    }

    public void setTitleSl(String titleSl) {
        this.titleSl = titleSl;
    }

    public String getDefinitionSl() {
        return definitionSl;
    }

    public void setDefinitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
    }

    public String getVersionEs() {
        return versionEs;
    }

    public void setVersionEs(String versionEs) {
        this.versionEs = versionEs;
    }

    public String getTitleEs() {
        return titleEs;
    }

    public void setTitleEs(String titleEs) {
        this.titleEs = titleEs;
    }

    public String getDefinitionEs() {
        return definitionEs;
    }

    public void setDefinitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
    }

    public String getVersionSv() {
        return versionSv;
    }

    public void setVersionSv(String versionSv) {
        this.versionSv = versionSv;
    }

    public String getTitleSv() {
        return titleSv;
    }

    public void setTitleSv(String titleSv) {
        this.titleSv = titleSv;
    }

    public String getDefinitionSv() {
        return definitionSv;
    }

    public void setDefinitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VocabularyDTO vocabularyDTO = (VocabularyDTO) o;
        if (vocabularyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vocabularyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VocabularyDTO{" +
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

    @JsonIgnore
    public boolean isPersisted() {
        return id != null;
    }

    public Set<CodeDTO> getCodes() {
        return codes;
    }

    public void setCodes(Set<CodeDTO> codes) {
        this.codes = codes;
    }

    public Set<VersionDTO> getVersions() {
        return versions;
    }

    public void setVersions(Set<VersionDTO> versions) {
        this.versions = versions;
    }

    public VocabularyDTO addVersion(VersionDTO version) {
        if( this.versions == null )
            this.versions  = new LinkedHashSet<>();
        this.versions.add( version );
        return this;
    }

    public VocabularyDTO removeVersion(VersionDTO version) {
        if( this.versions == null )
            return this;
        this.versions.remove( version );
        return this;
    }

    public List<VersionDTO> getVersionByGroup( String slVersionNumber, boolean noSameLanguage){
        List<VersionDTO> versionGroups = new ArrayList<>();
        List<VersionDTO> vGroups = this.versions.stream()
            .filter(v -> v.getNumber().startsWith(slVersionNumber))
            .sorted(VocabularyUtils.versionDtoComparator())
            .collect(Collectors.toList());

        if( noSameLanguage ) {
            Set<String> langs = new HashSet<>();
            for (VersionDTO vg : vGroups) {
                if ( langs.contains( vg.getLanguage()))
                    continue;
                langs.add(vg.getLanguage());
                versionGroups.add(vg);
            }
        } else {
            versionGroups = vGroups;
        }

        return versionGroups;
    }

    public String getSelectedLang() {
        return selectedLang;
    }

    public void setSelectedLang(String selectedLang) {
        this.selectedLang = selectedLang;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public VocabularyDTO addLanguage(String language) {
        if(languages == null)
            languages = new HashSet<>();
        this.languages.add(language);
        return this;
    }

    public Set<String> getLanguagesPublished() {
        return languagesPublished;
    }

    public void setLanguagesPublished(Set<String> languagesPublished) {
        this.languagesPublished = languagesPublished;
    }

    public VocabularyDTO addLanguagePublished(String language) {
        if(languagesPublished == null)
            languagesPublished = new HashSet<>();
        this.languagesPublished.add(language);
        return this;
    }

    public Set<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(Set<String> statuses) {
        this.statuses = statuses;
    }

    public VocabularyDTO addStatuses(String status) {
        if(this.statuses == null)
            this.statuses = new HashSet<>();
        this.statuses.add( status );
        return this;
    }

    public String getVersionByLanguage( String languageIso ) {
        return getVersionByLanguage(Language.getByIso(languageIso ));
    }

    public String getVersionByLanguage( Language language ) {
        switch (language) {
            case CZECH:
                return versionCs;
            case DANISH:
                return versionDa;
            case DUTCH:
                return versionNl;
            case ENGLISH:
                return versionEn;
            case ESTONIAN:
                return versionEt;
            case FINNISH:
                return versionFi;
            case FRENCH:
                return versionFr;
            case GERMAN:
                return versionDe;
            case GREEK:
                return versionEl;
            case HUNGARIAN:
                return versionHu;
            case ITALIAN:
                return versionIt;
            case JAPANESE:
                return versionJa;
            case LITHUANIAN:
                return versionLt;
            case NORWEGIAN:
                return versionNo;
            case PORTUGUESE:
                return versionPt;
            case ROMANIAN:
                return versionRo;
            case SLOVAK:
                return versionSk;
            case SLOVENIAN:
                return versionSl;
            case SPANISH:
                return versionEs;
            case SWEDISH:
                return versionSv;
            case ALBANIAN:
                return versionSq;
            case BOSNIAN:
                return versionBs;
            case BULGARIAN:
                return versionBg;
            case CROATIAN:
                return versionHr;
            case MACEDONIAN:
                return versionMk;
            case POLISH:
                return versionPl;
            case RUSSIAN:
                return versionRu;
            case SERBIAN:
                return versionSr;
            default:
                return null;
        }
    }

    public String getTitleByLanguage( String languageIso ) {
        return getTitleByLanguage(Language.getByIso(languageIso.toLowerCase() ));
    }

    public String getTitleByLanguage( Language language ) {
        switch (language) {
            case CZECH:
                return titleCs;
            case DANISH:
                return titleDa;
            case DUTCH:
                return titleNl;
            case ENGLISH:
                return titleEn;
            case ESTONIAN:
                return titleEt;
            case FINNISH:
                return titleFi;
            case FRENCH:
                return titleFr;
            case GERMAN:
                return titleDe;
            case GREEK:
                return titleEl;
            case HUNGARIAN:
                return titleHu;
            case ITALIAN:
                return titleIt;
            case JAPANESE:
                return titleJa;
            case LITHUANIAN:
                return titleLt;
            case NORWEGIAN:
                return titleNo;
            case PORTUGUESE:
                return titlePt;
            case ROMANIAN:
                return titleRo;
            case SLOVAK:
                return titleSk;
            case SLOVENIAN:
                return titleSl;
            case SPANISH:
                return titleEs;
            case SWEDISH:
                return titleSv;
            case ALBANIAN:
                return titleSq;
            case BOSNIAN:
                return titleBs;
            case BULGARIAN:
                return titleBg;
            case CROATIAN:
                return titleHr;
            case MACEDONIAN:
                return titleMk;
            case POLISH:
                return titlePl;
            case RUSSIAN:
                return titleRu;
            case SERBIAN:
                return titleSr;
            default:
                return null;
        }
    }

    public String getDefinitionByLanguage( String languageIso ) {
        return getDefinitionByLanguage( Language.getByIso(languageIso.toLowerCase()));
    }

    public String getDefinitionByLanguage( Language language ) {
        switch (language) {
            case CZECH:
                return definitionCs;
            case DANISH:
                return definitionDa;
            case DUTCH:
                return definitionNl;
            case ENGLISH:
                return definitionEn;
            case ESTONIAN:
                return definitionEt;
            case FINNISH:
                return definitionFi;
            case FRENCH:
                return definitionFr;
            case GERMAN:
                return definitionDe;
            case GREEK:
                return definitionEl;
            case HUNGARIAN:
                return definitionHu;
            case ITALIAN:
                return definitionIt;
            case JAPANESE:
                return definitionJa;
            case LITHUANIAN:
                return definitionLt;
            case NORWEGIAN:
                return definitionNo;
            case PORTUGUESE:
                return definitionPt;
            case ROMANIAN:
                return definitionRo;
            case SLOVAK:
                return definitionSk;
            case SLOVENIAN:
                return definitionSl;
            case SPANISH:
                return definitionEs;
            case SWEDISH:
                return definitionSv;
            case ALBANIAN:
                return definitionSq;
            case BOSNIAN:
                return definitionBs;
            case BULGARIAN:
                return definitionBg;
            case CROATIAN:
                return definitionHr;
            case MACEDONIAN:
                return definitionMk;
            case POLISH:
                return definitionPl;
            case RUSSIAN:
                return definitionRu;
            case SERBIAN:
                return definitionSr;
            default:
                return null;
        }
    }

    public VocabularyDTO setTitleDefinition( String title, String definition, String language, boolean ignoreNullValue) {
        return setTitleDefinition(title, definition, Language.getByIso(language.toLowerCase()), ignoreNullValue);
    }

    public VocabularyDTO setTitleDefinition( String title, String definition, Language language, boolean ignoreNullValue) {
        if( ignoreNullValue ) {
            if( definition == null )
                definition = getDefinitionByLanguage( language );
            if( title == null )
                title = getTitleByLanguage( language );
        }

        switch (language) {
            case CZECH:
                setTitleCs(title);
                setDefinitionCs(definition);
                break;
            case DANISH:
                setTitleDa(title);
                setDefinitionDa(definition);
                break;
            case DUTCH:
                setTitleNl(title);
                setDefinitionNl(definition);
                break;
            case ENGLISH:
                setTitleEn(title);
                setDefinitionEn(definition);
                break;
            case ESTONIAN:
                setTitleEt(title);
                setDefinitionEt(definition);
                break;
            case FINNISH:
                setTitleFi(title);
                setDefinitionFi(definition);
                break;
            case FRENCH:
                setTitleFr(title);
                setDefinitionFr(definition);
                break;
            case GERMAN:
                setTitleDe(title);
                setDefinitionDe(definition);
                break;
            case GREEK:
                setTitleEl(title);
                setDefinitionEl(definition);
                break;
            case HUNGARIAN:
                setTitleHu(title);
                setDefinitionHu(definition);
                break;
            case ITALIAN:
                setTitleIt(title);
                setDefinitionIt(definition);
                break;
            case JAPANESE:
                setTitleJa(title);
                setDefinitionJa(definition);
                break;
            case LITHUANIAN:
                setTitleLt(title);
                setDefinitionLt(definition);
                break;
            case NORWEGIAN:
                setTitleNo(title);
                setDefinitionNo(definition);
                break;
            case PORTUGUESE:
                setTitlePt(title);
                setDefinitionPt(definition);
                break;
            case ROMANIAN:
                setTitleRo(title);
                setDefinitionRo(definition);
                break;
            case SLOVAK:
                setTitleSk(title);
                setDefinitionSk(definition);
                break;
            case SLOVENIAN:
                setTitleSl(title);
                setDefinitionSl(definition);
                break;
            case SPANISH:
                setTitleEs(title);
                setDefinitionEs(definition);
                break;
            case SWEDISH:
                setTitleSv(title);
                setDefinitionSv(definition);
                break;
            case ALBANIAN:
                setTitleSq(title);
                setDefinitionSq(definition);
                break;
            case BOSNIAN:
                setTitleBs(title);
                setDefinitionBs(definition);
                break;
            case BULGARIAN:
                setTitleBg(title);
                setDefinitionBg(definition);
                break;
            case CROATIAN:
                setTitleHr(title);
                setDefinitionHr(definition);
                break;
            case MACEDONIAN:
                setTitleMk(title);
                setDefinitionMk(definition);
                break;
            case POLISH:
                setTitlePl(title);
                setDefinitionPl(definition);
                break;
            case RUSSIAN:
                setTitleRu(title);
                setDefinitionRu(definition);
                break;
            case SERBIAN:
                setTitleSr(title);
                setDefinitionSr(definition);
                break;
            default:
                break;
        }
        return this;
    }

    public VocabularyDTO setVersionByLanguage(String langIso, String versionNumber) {
        return setVersionByLanguage(Language.getByIso( langIso.toLowerCase() ), versionNumber);
    }

    public VocabularyDTO setVersionByLanguage(Language language, String versionNumber) {
        switch (language) {
            case CZECH:
                setVersionCs(versionNumber);
                break;
            case DANISH:
                setVersionDa(versionNumber);
                break;
            case DUTCH:
                setVersionNl(versionNumber);
                break;
            case ENGLISH:
                setVersionEn(versionNumber);
                break;
            case ESTONIAN:
                setVersionEt(versionNumber);
                break;
            case FINNISH:
                setVersionFi(versionNumber);
                break;
            case FRENCH:
                setVersionFr(versionNumber);
                break;
            case GERMAN:
                setVersionDe(versionNumber);
                break;
            case GREEK:
                setVersionEl(versionNumber);
                break;
            case HUNGARIAN:
                setVersionHu(versionNumber);
                break;
            case ITALIAN:
                setVersionIt(versionNumber);
                break;
            case JAPANESE:
                setVersionJa(versionNumber);
                break;
            case LITHUANIAN:
                setVersionLt(versionNumber);
                break;
            case NORWEGIAN:
                setVersionNo(versionNumber);
                break;
            case PORTUGUESE:
                setVersionPt(versionNumber);
                break;
            case ROMANIAN:
                setVersionRo(versionNumber);
                break;
            case SLOVAK:
                setVersionSk(versionNumber);
                break;
            case SLOVENIAN:
                setVersionSl(versionNumber);
                break;
            case SPANISH:
                setVersionEs(versionNumber);
                break;
            case SWEDISH:
                setVersionSv(versionNumber);
                break;
            case ALBANIAN:
                setVersionSq(versionNumber);
                break;
            case BOSNIAN:
                setVersionBs(versionNumber);
                break;
            case BULGARIAN:
                setVersionBg(versionNumber);
                break;
            case CROATIAN:
                setVersionHr(versionNumber);
                break;
            case MACEDONIAN:
                setVersionMk(versionNumber);
                break;
            case POLISH:
                setVersionPl(versionNumber);
                break;
            case RUSSIAN:
                setVersionRu(versionNumber);
                break;
            case SERBIAN:
                setVersionSr(versionNumber);
                break;
            default:
                break;
        }

        return this;
    }

    public void clearContent() {
        setVersionCs(null);
        setVersionDa(null);
        setVersionNl(null);
        setVersionEn(null);
        setVersionEt(null);
        setVersionFi(null);
        setVersionFr(null);
        setVersionDe(null);
        setVersionEl(null);
        setVersionHu(null);
        setVersionIt(null);
        setVersionJa(null);
        setVersionLt(null);
        setVersionNo(null);
        setVersionPt(null);
        setVersionRo(null);
        setVersionSk(null);
        setVersionSl(null);
        setVersionEs(null);
        setVersionSv(null);
        setVersionSq(null);
        setVersionBs(null);
        setVersionBg(null);
        setVersionHr(null);
        setVersionMk(null);
        setVersionPl(null);
        setVersionRu(null);
        setVersionSr(null);
        setLanguages( null );
        setLanguagesPublished( null );
        setStatuses( null );
        setTitleCs(null);
        setDefinitionCs(null);
        setTitleDa(null);
        setDefinitionDa(null);
        setTitleNl(null);
        setDefinitionNl(null);
        setTitleEn(null);
        setDefinitionEn(null);
        setTitleEt(null);
        setDefinitionEt(null);
        setTitleFi(null);
        setDefinitionFi(null);
        setTitleFr(null);
        setDefinitionFr(null);
        setTitleDe(null);
        setDefinitionDe(null);
        setTitleEl(null);
        setDefinitionEl(null);
        setTitleHu(null);
        setDefinitionHu(null);
        setTitleIt(null);
        setDefinitionIt(null);
        setTitleJa(null);
        setDefinitionJa(null);
        setTitleLt(null);
        setDefinitionLt(null);
        setTitleNo(null);
        setDefinitionNo(null);
        setTitlePt(null);
        setDefinitionPt(null);
        setTitleRo(null);
        setDefinitionRo(null);
        setTitleSk(null);
        setDefinitionSk(null);
        setTitleSl(null);
        setDefinitionSl(null);
        setTitleEs(null);
        setDefinitionEs(null);
        setTitleSv(null);
        setDefinitionSv(null);
        setTitleSq(null);
        setTitleBs(null);
        setTitleBg(null);
        setTitleHr(null);
        setTitleMk(null);
        setTitlePl(null);
        setTitleRu(null);
        setTitleSr(null);
        setDefinitionSq(null);
        setDefinitionBs(null);
        setDefinitionBg(null);
        setDefinitionHr(null);
        setDefinitionMk(null);
        setDefinitionPl(null);
        setDefinitionRu(null);
        setDefinitionSr(null);
    }

    public void prepareSlPublishing( VersionDTO versionDTO) {
        this.status =  Status.PUBLISHED.toString();
        this.versionNumber = versionDTO.getNumber();
        this.uri = this.uri + "/" + this.versionNumber;
        this.publicationDate = versionDTO.getPublicationDate();
        this.archived = true;
    }

    public static Optional<VocabularyDTO> findByIdFromList(List<VocabularyDTO> vocabs, String docId) {
        if( docId == null )
            return Optional.empty();
        return vocabs.stream().filter( voc -> voc.getId().equals( Long.parseLong(docId))).findFirst();
    }

    public static void fillVocabularyByVersions( VocabularyDTO vocab, Set<VersionDTO> versions ) {
        // use to ignore version with same lang, eg. FRv2.0.2 and FRv2.0.1 only FRv.2.0.2 will be chosen
        Set<String> versionLangs = new HashSet<>();
        for (VersionDTO version : versions) {
            if( versionLangs.contains( version.getLanguage()) )
                continue;
            versionLangs.add( version.getLanguage());
            // fill vocabulary
            vocab.setTitleDefinition(version.getTitle(), version.getDefinition(), version.getLanguage(), false);
            if( version.getStatus().equals( Status.PUBLISHED.toString())) {
                vocab.addLanguagePublished(version.getLanguage());
                vocab.setVersionByLanguage(version.getLanguage(), version.getNumber());
            } else {
                vocab.setVersionByLanguage(version.getLanguage(), version.getNumber() + "_" + version.getStatus());
            }
            vocab.addLanguage( version.getLanguage() );
            vocab.addStatuses( version.getStatus() );
        }

    }
}
