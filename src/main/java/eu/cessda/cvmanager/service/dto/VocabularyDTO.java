package eu.cessda.cvmanager.service.dto;


import javax.persistence.Lob;
import javax.validation.constraints.*;

import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;

import eu.cessda.cvmanager.domain.Concept;
import eu.cessda.cvmanager.domain.Version;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A DTO for the Vocabulary entity.
 */
public class VocabularyDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	public VocabularyDTO() {
		archived = false;
		withdrawn = false;
		discoverable = false;
	}
	
    private Long id;
    
	private String status;
	

    @NotNull
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
    
    private Set<String> languages;
    
    private Set<String> statuses;
    
    @NotNull
    @Size(max = 20)
    private String sourceLanguage;
    
    @NotNull
    private Long agencyId;
    
    @NotNull
    private String agencyName;
    
    private Set<CodeDTO> codes = new HashSet<>();
    
    private Set<VersionDTO> vers = new HashSet<>();
    
    private Set<VersionDTO> versions = new HashSet<>();
    
    private Language selectedLang;
    
    private LocalDate publicationDate;
    
    private LocalDateTime lastModified;
    
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
    private String versionLt;

    @Lob
    private String titleLt;

    @Lob
    private String definitionLt;

    @Size(max = 20)
    private String versionNo;

    @Lob
    private String titleNo;

    @Lob
    private String definitionNo;

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
    	}
    	return null;
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
    	}
    	return null;
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
    	}
    	return null;
    }
    
    public VocabularyDTO setTitleDefinition( String title, String definition, String language) {
    	return setTitleDefinition(title, definition, Language.getEnum(language));
    }

    public VocabularyDTO setTitleDefinition( String title, String definition, Language language) {
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
    	}
    	addLanguage(language.name().toLowerCase());
    	return this;
    }
    
	public VocabularyDTO setStatusByLanguage(Language language, String status) {
		switch (language) {
			case CZECH:
				setVersionCs(status);
				break;
			case DANISH:
				setVersionDa(status);
				break;
			case DUTCH:
				setVersionNl(status);
				break;
			case ENGLISH:
				setVersionEn(status);
				break;
			case FINNISH:
				setVersionFi(status);
				break;
			case FRENCH:
				setVersionFr(status);
				break;
			case GERMAN:
				setVersionDe(status);
				break;
			case GREEK:
				setVersionEl(status);
				break;
			case HUNGARIAN:
				setVersionHu(status);
				break;
			case LITHUANIAN:
				setVersionLt(status);
				break;
			case NORWEGIAN:
				setVersionNo(status);
				break;
			case PORTUGUESE:
				setVersionPt(status);
				break;
			case ROMANIAN:
				setVersionRo(status);
				break;
			case SLOVAK:
				setVersionSk(status);
				break;
			case SLOVENIAN:
				setVersionSl(status);
				break;
			case SPANISH:
				setVersionEs(status);
				break;
			case SWEDISH:
				setVersionSv(status);
				break;
		}
		
		return this;
	}
    
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
		if(languages == null)
			languages = new HashSet<>();
		this.languages.add(language);
		return this;
	}
	
	public Set<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(Set<String> statuses) {
		this.statuses = statuses;
	}
	
	public VocabularyDTO addStatus(String status) {
		if(this.statuses == null)
			this.statuses = new HashSet<>();
		this.statuses.add( status );
		return this;
	}
	
    public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}
		
	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
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
    
    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }
    
    public Set<CodeDTO> getCodes() {
        return codes;
    }

    public VocabularyDTO codes(Set<CodeDTO> codes) {
        this.codes = codes;
        return this;
    }

    public VocabularyDTO addCode(CodeDTO code) {
    	if( this.codes == null )
    		this.codes = new HashSet<>();
        this.codes.add(code);
        return this;
    }

    public VocabularyDTO removeCode(CodeDTO code) {
        this.codes.remove(code);
        return this;
    }

    public void setCodes(Set<CodeDTO> codes) {
        this.codes = codes;
    }
    
	public Set<VersionDTO> getVers() {
		return vers;
	}

	public void setVers(Set<VersionDTO> vers) {
		this.vers = vers;
	}
	
	public VocabularyDTO addVers(VersionDTO ver) {
    	if( this.vers == null )
    		this.vers  = new HashSet<>();
        this.vers.add( ver );
        return this;
    }
    
	public Set<VersionDTO> getVersions() {
		return versions;
	}

	public void setVersions(Set<VersionDTO> versions) {
		this.versions = versions;
	}
	
	public VocabularyDTO addVersions(VersionDTO version) {
    	if( this.versions == null )
    		this.versions  = new HashSet<>();
        this.versions.add( version );
        return this;
    }

	public Language getSelectedLang() {
		return selectedLang;
	}

	public void setSelectedLang(Language selectedLang) {
		this.selectedLang = selectedLang;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getVersionCs() {
		return versionCs;
	}

	public void setVersionCs(String versionCs) {
		this.versionCs = versionCs;
	}

	public String getVersionDa() {
		return versionDa;
	}

	public void setVersionDa(String versionDa) {
		this.versionDa = versionDa;
	}

	public String getVersionNl() {
		return versionNl;
	}

	public void setVersionNl(String versionNl) {
		this.versionNl = versionNl;
	}

	public String getVersionEn() {
		return versionEn;
	}

	public void setVersionEn(String versionEn) {
		this.versionEn = versionEn;
	}

	public String getVersionFi() {
		return versionFi;
	}

	public void setVersionFi(String versionFi) {
		this.versionFi = versionFi;
	}

	public String getVersionFr() {
		return versionFr;
	}

	public void setVersionFr(String versionFr) {
		this.versionFr = versionFr;
	}

	public String getVersionDe() {
		return versionDe;
	}

	public void setVersionDe(String versionDe) {
		this.versionDe = versionDe;
	}

	public String getVersionEl() {
		return versionEl;
	}

	public void setVersionEl(String versionEl) {
		this.versionEl = versionEl;
	}

	public String getVersionHu() {
		return versionHu;
	}

	public void setVersionHu(String versionHu) {
		this.versionHu = versionHu;
	}

	public String getVersionLt() {
		return versionLt;
	}

	public void setVersionLt(String versionLt) {
		this.versionLt = versionLt;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getVersionPt() {
		return versionPt;
	}

	public void setVersionPt(String versionPt) {
		this.versionPt = versionPt;
	}

	public String getVersionRo() {
		return versionRo;
	}

	public void setVersionRo(String versionRo) {
		this.versionRo = versionRo;
	}

	public String getVersionSk() {
		return versionSk;
	}

	public void setVersionSk(String versionSk) {
		this.versionSk = versionSk;
	}

	public String getVersionSl() {
		return versionSl;
	}

	public void setVersionSl(String versionSl) {
		this.versionSl = versionSl;
	}

	public String getVersionEs() {
		return versionEs;
	}

	public void setVersionEs(String versionEs) {
		this.versionEs = versionEs;
	}

	public String getVersionSv() {
		return versionSv;
	}

	public void setVersionSv(String versionSv) {
		this.versionSv = versionSv;
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
            ", version='" + getVersionNumber() + "'" +
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
            ", titleSv='" + getTitleSv() + "'" +
            ", definitionSv='" + getDefinitionSv() + "'" +
            "}";
    }
    
    public boolean isPersisted() {
		return id != null;
	}
    public static VocabularyDTO generateFromCVScheme ( VocabularyDTO vocabulary, CVScheme cvScheme) {
    	if( vocabulary == null ) {
    		vocabulary = new VocabularyDTO();
			vocabulary.setUri(cvScheme.getId());
    	}
    	
    	return extractCVSchemeToVocabularyDTO(cvScheme, vocabulary);
    }
    
    public static VocabularyDTO generateFromCVScheme ( CVScheme cvScheme) {
    	return generateFromCVScheme( null, cvScheme);
    }

	private static VocabularyDTO extractCVSchemeToVocabularyDTO(CVScheme cvScheme, VocabularyDTO vocabulary) {
		//TODO: need to change hard coded value
		vocabulary.setUri( cvScheme.getId() );
		vocabulary.setVersionNumber( "1.0" );
		vocabulary.setSourceLanguage( Language.ENGLISH.name().toLowerCase());
		
		vocabulary.setNotation( cvScheme.getCode());
		vocabulary.setLanguages( Language.getLanguagesFromIso(cvScheme.getLanguagesByTitle()));
		Set<String> langs = Language.getEnumAsSetString();
		
		String versionTL = "0.0.1";
		String versionSL = "1.0";
		
		cvScheme.getLanguagesByTitle().forEach( lang -> {
			if( !langs.contains( lang ))
				return;
				
			if( cvScheme.getTitleByLanguage(lang) != null && cvScheme.getDescriptionByLanguage(lang) != null ){
				Language langEnum = Language.getEnum(lang);
				String title = cvScheme.getTitleByLanguage(lang);
				String definition =cvScheme.getDescriptionByLanguage(lang);
				
				
				switch ( langEnum ) {
	    		case CZECH:
	    			vocabulary.setTitleCs(title);
	    			vocabulary.setDefinitionCs(definition);
	    			vocabulary.setVersionCs(versionTL);
	    			break;
	    		case DANISH:
	    			vocabulary.setTitleDa(title);
	    			vocabulary.setDefinitionDa(definition);
	    			vocabulary.setVersionDa(versionTL);
	    			break;
	    		case DUTCH:
	    			vocabulary.setTitleNl(title);
	    			vocabulary.setDefinitionNl(definition);
	    			vocabulary.setVersionNl(versionTL);
	    			break;
	    		case ENGLISH:
	    			vocabulary.setTitleEn(title);
	    			vocabulary.setDefinitionEn(definition);
	    			vocabulary.setVersionEn(versionSL);
	    			break;
	    		case FINNISH:
	    			vocabulary.setTitleFi(title);
	    			vocabulary.setDefinitionFi(definition);
	    			vocabulary.setVersionFi(versionTL);
	    			break;
	    		case FRENCH:
	    			vocabulary.setTitleFr(title);
	    			vocabulary.setDefinitionFr(definition);
	    			vocabulary.setVersionFr(versionTL);
	    			break;
	    		case GERMAN:
	    			vocabulary.setTitleDe(title);
	    			vocabulary.setDefinitionDe(definition);
	    			vocabulary.setVersionDe(versionTL);
	    			break;
	    		case GREEK:
	    			vocabulary.setTitleEl(title);
	    			vocabulary.setDefinitionEl(definition);
	    			vocabulary.setVersionEl(versionTL);
	    			break;
	    		case HUNGARIAN:
	    			vocabulary.setTitleHu(title);
	    			vocabulary.setDefinitionHu(definition);
	    			vocabulary.setVersionHu(versionTL);
	    			break;
	    		case LITHUANIAN:
	    			vocabulary.setTitleLt(title);
	    			vocabulary.setDefinitionLt(definition);
	    			vocabulary.setVersionLt(versionTL);
	    			break;
	    		case NORWEGIAN:
	    			vocabulary.setTitleNo(title);
	    			vocabulary.setDefinitionNo(definition);
	    			vocabulary.setVersionNo(versionTL);
	    			break;
	    		case PORTUGUESE:
	    			vocabulary.setTitlePt(title);
	    			vocabulary.setDefinitionPt(definition);
	    			vocabulary.setVersionPt(versionTL);
	    			break;
	    		case ROMANIAN:
	    			vocabulary.setTitleRo(title);
	    			vocabulary.setDefinitionRo(definition);
	    			vocabulary.setVersionRo(versionTL);
	    			break;
	    		case SLOVAK:
	    			vocabulary.setTitleSk(title);
	    			vocabulary.setDefinitionSk(definition);
	    			vocabulary.setVersionSk(versionTL);
	    			break;
	    		case SLOVENIAN:
	    			vocabulary.setTitleSl(title);
	    			vocabulary.setDefinitionSl(definition);
	    			vocabulary.setVersionSl(versionTL);
	    			break;
	    		case SPANISH:
	    			vocabulary.setTitleEs(title);
	    			vocabulary.setDefinitionEs(definition);
	    			vocabulary.setVersionEs(versionTL);
	    			break;
	    		case SWEDISH:
	    			vocabulary.setTitleSv(title);
	    			vocabulary.setDefinitionSv(definition);
	    			vocabulary.setVersionSv(versionTL);
	    			break;
				}
				
			}
		});
		
		return vocabulary;
	}
	
	public Set<String> getLatestStatuses(){
		Set<String> latestStatuses = new HashSet<>();
		getLatestVersions().forEach( v -> latestStatuses.add(v.getStatus()));
		return latestStatuses;
	}
	
	public Set<VersionDTO> getLatestVersions(){
		return getLatestVersions( null );
	}
	
	public Set<VersionDTO> getLatestVersions(String status){
		Set<VersionDTO> versionDTOs = new HashSet<>();
		for(String lang: languages) {
			getLatestVersionByLanguage(lang, status).ifPresent( v -> versionDTOs.add(v));
		}
		return versionDTOs;
	}
	
	public Optional<VersionDTO> getLatestVersionByLanguage(Language language) {
		return getLatestVersionByLanguage(language.name().toLowerCase());
	}
	
	public Optional<VersionDTO> getLatestVersionByLanguage(String language) {
		return getLatestVersionByLanguage(language, null);
	}
	
	public Optional<VersionDTO> getLatestVersionByLanguage(String language, String status) {
		
		if( status == null ) {
			return versions
					.stream()
					.sorted( ( v1, v2) -> v2.getPreviousVersion().compareTo( v1.getPreviousVersion() ))
					.filter( p -> language.equalsIgnoreCase( p.getLanguage() ))
					.findFirst();
		} else {
			return versions
					.stream()
					.sorted( ( v1, v2) -> v2.getPreviousVersion().compareTo( v1.getPreviousVersion() ))
					.filter( p -> language.equalsIgnoreCase( p.getLanguage() ))
					.filter( p -> status.equalsIgnoreCase( p.getStatus() ))
					.findFirst();
		}
	}
	
	public static Optional<VocabularyDTO> findByIdFromList(List<VocabularyDTO> vocabs, String docId) {
		if( docId == null )
			return Optional.empty();
		return vocabs.stream().filter( voc -> voc.getId() == Long.parseLong(docId)).findFirst();
	}
	
	public Set<CodeDTO> generateCodesFromLatestVersion(){
		// get from latest version from all status
		return extractCodeFromVersionConcept( null );
	}
	
	public Set<CodeDTO> generateCodesFromLatestPublishedVersion(){
		return extractCodeFromVersionConcept( Status.PUBLISHED.toString() );
	}

	private Set<CodeDTO> extractCodeFromVersionConcept( String status) {
		Map<String, CodeDTO> codeMap = new HashMap<>();
		for(String lang: languages) {
			Language langEnum = Language.getEnumByName(lang);
			getLatestVersionByLanguage(lang,status).ifPresent( versionDTO -> {
				// get codes
				for( ConceptDTO concept : versionDTO.getConcepts()){
					CodeDTO targetCode = codeMap.get( concept.getNotation());
					if( targetCode == null ) {
						CodeDTO newCode = new CodeDTO();
						newCode.setNotation( concept.getNotation());
						newCode.setTitleDefinition( concept.getTitle(), concept.getDefinition(), langEnum);
						codeMap.put(concept.getNotation(), newCode);
					} else {
						targetCode.setTitleDefinition( concept.getTitle(), concept.getDefinition(), langEnum);
					}
				};
			});
		}
		return codeMap
				.values()
				.stream()
				.collect( Collectors.toSet());
	}
	

	
}
