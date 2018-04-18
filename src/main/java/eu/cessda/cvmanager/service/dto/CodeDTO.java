package eu.cessda.cvmanager.service.dto;


import javax.persistence.Lob;
import javax.validation.constraints.*;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.wts.domain.enumeration.Language;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Code entity.
 */
public class CodeDTO implements Serializable {
	
	public CodeDTO() {
		archived = false;
		withdrawn = false;
		discoverable = false;
	}

    private Long id;

    @NotNull
    private String uri;
    
    @NotNull
    private String notation;

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

    private String parent;
    
    private Integer position;
    
    private Long vocabularyId;
    
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
    private String titleSv;

    @Lob
    private String definitionSv;
    
    public CodeDTO setTitleDefinition( String title, String definition, String language) {
    	return setTitleDefinition(title, definition, Language.getEnum(language));
    }

    public CodeDTO setTitleDefinition( String title, String definition, Language language) {
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
    			setTitleNo(title);
    			setDefinitionNo(definition);
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
    
    public Set<String> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<String> languages) {
		this.languages = languages;
	}
	
	public CodeDTO addLanguage(String language) {
		if(languages == null)
			languages = new HashSet<>();
//		this.languages.add(language);
		return this;
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

    public Long getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CodeDTO codeDTO = (CodeDTO) o;
        if(codeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), codeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CodeDTO{" +
            "id=" + getId() +
            ", uri='" + getUri() + "'" +
            ", version='" + getVersion() + "'" +
            ", archived='" + isArchived() + "'" +
            ", withdrawn='" + isWithdrawn() + "'" +
            ", discoverable='" + isDiscoverable() + "'" +
            ", sourceLanguage='" + getSourceLanguage() + "'" +
            ", vocabularyId='" + getVocabularyId() + "'" +
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
    
    public static CodeDTO generateFromCVConcept ( CodeDTO code, CVConcept cvConcept) {
    	if( code == null) {
    		code = new CodeDTO();
    		code.setUri(cvConcept.getId());
    	}
		
		return extractCVSchemeToCodeDTO(cvConcept, code);
    }
    
    public static CodeDTO generateFromCVConcept ( CVConcept cvConcept) {
    	return generateFromCVConcept(null, cvConcept);
    }

	private static CodeDTO extractCVSchemeToCodeDTO(CVConcept cvConcept, CodeDTO code) {
		//TODO: need to change hard coded value
		code.setVersion( "1.0" );
		code.setSourceLanguage( Language.ENGLISH.name().toLowerCase());
				
		code.setNotation( cvConcept.getNotation());
		code.setLanguages( Language.getLanguagesFromIso(cvConcept.getLanguagesByPrefLabel()));
		Set<String> langs = Language.getEnumAsSetString();
		
		cvConcept.getLanguagesByPrefLabel().forEach( lang -> {
			if( !langs.contains( lang ))
				return;
				
			if( cvConcept.getPrefLabelByLanguage(lang) != null && cvConcept.getDescriptionByLanguage(lang) != null ){
				String title = cvConcept.getPrefLabelByLanguage(lang);
				String definition =cvConcept.getDescriptionByLanguage(lang);
				switch ( Language.getEnum(lang) ) {
	    		case CZECH:
	    			code.setTitleCs(title);
	    			code.setDefinitionCs(definition);
	    			break;
	    		case DANISH:
	    			code.setTitleDa(title);
	    			code.setDefinitionDa(definition);
	    			break;
	    		case DUTCH:
	    			code.setTitleNl(title);
	    			code.setDefinitionNl(definition);
	    			break;
	    		case ENGLISH:
	    			code.setTitleEn(title);
	    			code.setDefinitionEn(definition);
	    			break;
	    		case FINNISH:
	    			code.setTitleFi(title);
	    			code.setDefinitionFi(definition);
	    			break;
	    		case FRENCH:
	    			code.setTitleFr(title);
	    			code.setDefinitionFr(definition);
	    			break;
	    		case GERMAN:
	    			code.setTitleDe(title);
	    			code.setDefinitionDe(definition);
	    			break;
	    		case GREEK:
	    			code.setTitleEl(title);
	    			code.setDefinitionEl(definition);
	    			break;
	    		case HUNGARIAN:
	    			code.setTitleHu(title);
	    			code.setDefinitionHu(definition);
	    			break;
	    		case LITHUANIAN:
	    			code.setTitleLt(title);
	    			code.setDefinitionLt(definition);
	    			break;
	    		case NORWEGIAN:
	    			code.setTitleNo(title);
	    			code.setDefinitionNo(definition);
	    			break;
	    		case PORTUGUESE:
	    			code.setTitleNo(title);
	    			code.setDefinitionNo(definition);
	    			break;
	    		case ROMANIAN:
	    			code.setTitleRo(title);
	    			code.setDefinitionRo(definition);
	    			break;
	    		case SLOVAK:
	    			code.setTitleSk(title);
	    			code.setDefinitionSk(definition);
	    			break;
	    		case SLOVENIAN:
	    			code.setTitleSl(title);
	    			code.setDefinitionSl(definition);
	    			break;
	    		case SPANISH:
	    			code.setTitleEs(title);
	    			code.setDefinitionEs(definition);
	    			break;
	    		case SWEDISH:
	    			code.setTitleSv(title);
	    			code.setDefinitionSv(definition);
	    			break;
				}
			}
		});
		
		return code;
	}
}
