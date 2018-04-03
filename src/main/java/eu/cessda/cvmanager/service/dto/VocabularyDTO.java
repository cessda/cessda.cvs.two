package eu.cessda.cvmanager.service.dto;


import javax.persistence.Lob;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Vocabulary entity.
 */
public class VocabularyDTO implements Serializable {

    private Long id;

    @NotNull
    private String uri;

    @NotNull
    @Size(max = 20)
    private String version;

    private Boolean archived;

    private Boolean withdrawn;

    private Boolean discoverable;
    
    private Set<String> languages;

    @NotNull
    @Size(max = 20)
    private String sourceLanguage;

    @NotNull
    private Long agencyId;
    
    @NotNull
    private String agencyName;
    
    @Lob
    private String titleCs;

    @Lob
    private String definitionCs;

    @Lob
    private String titleDa;

    @Lob
    private String definitionDa;

    @Lob
    private String titleNl;

    @Lob
    private String definitionNl;

    @Lob
    private String titleEn;

    @Lob
    private String definitionEn;

    @Lob
    private String titleFi;

    @Lob
    private String definitionFi;

    @Lob
    private String titleFr;

    @Lob
    private String definitionFr;

    @Lob
    private String titleDe;

    @Lob
    private String definitionDe;

    @Lob
    private String titleEl;

    @Lob
    private String definitionEl;

    @Lob
    private String titleHu;

    @Lob
    private String definitionHu;

    @Lob
    private String titleLt;

    @Lob
    private String definitionLt;

    @Lob
    private String titleNo;

    @Lob
    private String definitionNo;

    @Lob
    private String titlePt;

    @Lob
    private String definitionPt;

    @Lob
    private String titleRo;

    @Lob
    private String definitionRo;

    @Lob
    private String titleSk;

    @Lob
    private String definitionSk;

    @Lob
    private String titleSl;

    @Lob
    private String definitionSl;

    @Lob
    private String titleEs;

    @Lob
    private String definitionEs;

    @Lob
    private String titleSe;

    @Lob
    private String definitionSe;
        
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }
    
    public Set<String> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<String> languages) {
		this.languages = languages;
	}
	
	public VocabularyDTO addLanguage(String language) {
		this.languages.add(language);
		return this;
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

    public String getTitleSe() {
        return titleSe;
    }

    public void setTitleSe(String titleSe) {
        this.titleSe = titleSe;
    }

    public String getDefinitionSe() {
        return definitionSe;
    }

    public void setDefinitionSe(String definitionSe) {
        this.definitionSe = definitionSe;
    }
    
    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
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
        if(vocabularyDTO.getId() == null || getId() == null) {
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
            ", uri='" + getUri() + "'" +
            ", version='" + getVersion() + "'" +
            ", archived='" + isArchived() + "'" +
            ", withdrawn='" + isWithdrawn() + "'" +
            ", discoverable='" + isDiscoverable() + "'" +
            ", sourceLanguage='" + getSourceLanguage() + "'" +
            ", agencyName='" + getAgencyName() + "'" +
            ", titleCs='" + getTitleCs() + "'" +
            ", definitionCs='" + getDefinitionCs() + "'" +
            ", titleDa='" + getTitleDa() + "'" +
            ", definitionDa='" + getDefinitionDa() + "'" +
            ", titleNl='" + getTitleNl() + "'" +
            ", definitionNl='" + getDefinitionNl() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            ", definitionEn='" + getDefinitionEn() + "'" +
            ", titleFi='" + getTitleFi() + "'" +
            ", definitionFi='" + getDefinitionFi() + "'" +
            ", titleFr='" + getTitleFr() + "'" +
            ", definitionFr='" + getDefinitionFr() + "'" +
            ", titleDe='" + getTitleDe() + "'" +
            ", definitionDe='" + getDefinitionDe() + "'" +
            ", titleEl='" + getTitleEl() + "'" +
            ", definitionEl='" + getDefinitionEl() + "'" +
            ", titleHu='" + getTitleHu() + "'" +
            ", definitionHu='" + getDefinitionHu() + "'" +
            ", titleLt='" + getTitleLt() + "'" +
            ", definitionLt='" + getDefinitionLt() + "'" +
            ", titleNo='" + getTitleNo() + "'" +
            ", definitionNo='" + getDefinitionNo() + "'" +
            ", titlePt='" + getTitlePt() + "'" +
            ", definitionPt='" + getDefinitionPt() + "'" +
            ", titleRo='" + getTitleRo() + "'" +
            ", definitionRo='" + getDefinitionRo() + "'" +
            ", titleSk='" + getTitleSk() + "'" +
            ", definitionSk='" + getDefinitionSk() + "'" +
            ", titleSl='" + getTitleSl() + "'" +
            ", definitionSl='" + getDefinitionSl() + "'" +
            ", titleEs='" + getTitleEs() + "'" +
            ", definitionEs='" + getDefinitionEs() + "'" +
            ", titleSe='" + getTitleSe() + "'" +
            ", definitionSe='" + getDefinitionSe() + "'" +
            "}";
    }
    
    public boolean isPersisted() {
		return id != null;
	}
}
