package eu.cessda.cvmanager.service.dto;


import javax.persistence.Lob;
import javax.validation.constraints.*;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;

import com.vaadin.data.TreeData;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Optional;

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
    
    private Boolean archived;

    private Boolean withdrawn;

    private Boolean discoverable;
    
    private Set<String> languages;

    @NotNull
    @Size(max = 20)
    private String sourceLanguage;

    private String parent;
    
    private Integer position;
    
    private LocalDate publicationDate;
    
    private LocalDateTime lastModified;
    
    private Long vocabularyId;
    
    private Long versionId;
    
    private String versionNumber;
    
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
    
    public CodeDTO setTitleByLanguage( String title, Language language) {
    	switch (language) {
    		case CZECH:
    			setTitleCs(title);
    			break;
    		case DANISH:
    			setTitleDa(title);
    			break;
    		case DUTCH:
    			setTitleNl(title);
    			break;
    		case ENGLISH:
    			setTitleEn(title);
    			break;
    		case FINNISH:
    			setTitleFi(title);
    			break;
    		case FRENCH:
    			setTitleFr(title);
    			break;
    		case GERMAN:
    			setTitleDe(title);
    			break;
    		case GREEK:
    			setTitleEl(title);
    			break;
    		case HUNGARIAN:
    			setTitleHu(title);
    			break;
    		case LITHUANIAN:
    			setTitleLt(title);
    			break;
    		case NORWEGIAN:
    			setTitleNo(title);
    			break;
    		case PORTUGUESE:
    			setTitlePt(title);
    			break;
    		case ROMANIAN:
    			setTitleRo(title);
    			break;
    		case SLOVAK:
    			setTitleSk(title);
    			break;
    		case SLOVENIAN:
    			setTitleSl(title);
    			break;
    		case SPANISH:
    			setTitleEs(title);
    			break;
    		case SWEDISH:
    			setTitleSv(title);
    			break;
    	}
    	return this;
    }
    
    public CodeDTO setDefinitionByLanguage( String definition, Language language) {
    	switch (language) {
    		case CZECH:
    			setDefinitionCs(definition);
    			break;
    		case DANISH:
    			setDefinitionDa(definition);
    			break;
    		case DUTCH:
    			setDefinitionNl(definition);
    			break;
    		case ENGLISH:
    			setDefinitionEn(definition);
    			break;
    		case FINNISH:
    			setDefinitionFi(definition);
    			break;
    		case FRENCH:
    			setDefinitionFr(definition);
    			break;
    		case GERMAN:
    			setDefinitionDe(definition);
    			break;
    		case GREEK:
    			setDefinitionEl(definition);
    			break;
    		case HUNGARIAN:
    			setDefinitionHu(definition);
    			break;
    		case LITHUANIAN:
    			setDefinitionLt(definition);
    			break;
    		case NORWEGIAN:
    			setDefinitionNo(definition);
    			break;
    		case PORTUGUESE:
    			setDefinitionPt(definition);
    			break;
    		case ROMANIAN:
    			setDefinitionRo(definition);
    			break;
    		case SLOVAK:
    			setDefinitionSk(definition);
    			break;
    		case SLOVENIAN:
    			setDefinitionSl(definition);
    			break;
    		case SPANISH:
    			setDefinitionEs(definition);
    			break;
    		case SWEDISH:
    			setDefinitionSv(definition);
    			break;
    	}
    	addLanguage(language.toString());
    	return this;
    }
    
    public CodeDTO setTitleDefinition( String title, String definition, Language language) {
    	return setTitleDefinition(title, definition, language, false);
    }
    
    public CodeDTO setTitleDefinition( String title, String definition, String language) {
    	return setTitleDefinition(title, definition, Language.getEnum(language), false);
    }

    public CodeDTO setTitleDefinition( String title, String definition, Language language, boolean isRemoveLanguage) {
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
    	if(isRemoveLanguage)
    		removeLanguage(language.toString());
    	else
    		addLanguage(language.toString());
    	return this;
    }
    
    public void clearCode() {
    	setTitleCs(null);
		setDefinitionCs(null);
		setTitleDa(null);
		setDefinitionDa(null);
		setTitleNl(null);
		setDefinitionNl(null);
		setTitleEn(null);
		setDefinitionEn(null);
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
    
    public Set<String> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<String> languages) {
		this.languages = languages;
	}
	
	public CodeDTO addLanguage(String language) {
		if(languages == null)
			languages = new HashSet<>();
		this.languages.add(language);
		return this;
	}
	
	public void removeLanguage(String language) {
		if(languages == null)
			return;
		this.languages.remove(language);
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

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	
//	public static List<CodeDTO> mergeWithVersionConcepts(List<CodeDTO> codeDTOs, Set<ConceptDTO> concepts){
//		List<CodeDTO> codes = new ArrayList<>();
//		for( CodeDTO )
//		
//		return codes;
//	}

	public static CodeDTO generateFromCVConcept ( CodeDTO code, CVConcept cvConcept) {
    	if( code == null) {
    		code = new CodeDTO();
    		code.setUri(cvConcept.getId());
    	}
		
		return extractCVConceptToCodeDTO(cvConcept, code);
    }
    
    public static CodeDTO generateFromCVConcept ( CVConcept cvConcept) {
    	return generateFromCVConcept(null, cvConcept);
    }

	private static CodeDTO extractCVConceptToCodeDTO(CVConcept cvConcept, CodeDTO code) {
		//TODO: need to change hard coded value
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
	    			code.setTitlePt(title);
	    			code.setDefinitionPt(definition);
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
	
	public static Optional<CodeDTO> findByIdFromList(Set<CodeDTO> codes, int docId) {
		return codes.stream().filter( voc -> voc.getId() == docId).findFirst();
	}
	
	public static Set<ConceptDTO> getConceptsFromCodes( List<CodeDTO> codes, Language lang){
		return getConceptsFromCodes(codes, lang, null);
	}
	
	public static Set<ConceptDTO> getConceptsFromCodes( List<CodeDTO> codes, Language lang, Long versionId){
		Set<ConceptDTO> concepts = new HashSet<>();
		
		codes.forEach( code -> {
			String cTitle = code.getTitleByLanguage(lang);
			if( cTitle != null && !cTitle.isEmpty()) {
				ConceptDTO concept = new ConceptDTO();
				concept.setNotation( code.getNotation());
				concept.setTitle(cTitle);
				concept.setDefinition( code.getDefinitionByLanguage(lang));
				concept.setCodeId( code.getId());
				if(versionId != null )
					concept.setVersionId(versionId);
				
				concepts.add(concept);
			}
		});
		
		return concepts;
	}
	
	public static Map<String, CodeDTO> getCodeAsMap( List<CodeDTO> codes) {
		return codes.stream()
				.collect(Collectors.toMap( CodeDTO::getNotation, Function.identity()));
	}
	
	public static CodeDTO clone (CodeDTO code) {
		CodeDTO clonedCode = new CodeDTO();
		clonedCode.setUri(code.getUri());
		clonedCode.setNotation( code.getNotation());
		clonedCode.setArchived( code.isArchived() );
		clonedCode.setWithdrawn( code.isWithdrawn() );
		clonedCode.setDiscoverable( code.isDiscoverable());
		clonedCode.setLanguages( code.getLanguages());
		clonedCode.setSourceLanguage( code.getSourceLanguage());
		clonedCode.setParent( code.getParent() );
		clonedCode.setPosition( code.getPosition() );
//		clonedCode.setPublicationDate( code.getPublicationDate() );
//		clonedCode.setLastModified( code.getLastModified() );
		clonedCode.setVocabularyId( code.getVocabularyId() );
//		clonedCode.setVersionId( code.getVersionId() );
//		clonedCode.setVersionNumber( code.getVersionNumber() );
		clonedCode.setTitleCs( code.getTitleCs());
		clonedCode.setTitleDa( code.getTitleDa());
		clonedCode.setTitleNl( code.getTitleNl());
		clonedCode.setTitleEn( code.getTitleEn());
		clonedCode.setTitleFi( code.getTitleFi());
		clonedCode.setTitleFr( code.getTitleFr());
		clonedCode.setTitleDe( code.getTitleDe());
		clonedCode.setTitleEl( code.getTitleEl());
		clonedCode.setTitleHu( code.getTitleHu());
		clonedCode.setTitleLt( code.getTitleLt());
		clonedCode.setTitleNo( code.getTitleNo());
		clonedCode.setTitlePt( code.getTitlePt());
		clonedCode.setTitleRo( code.getTitleRo());
		clonedCode.setTitleSk( code.getTitleSk());
		clonedCode.setTitleSl( code.getTitleSl());
		clonedCode.setTitleEs( code.getTitleEs());
		clonedCode.setTitleSv( code.getTitleSv());
		clonedCode.setDefinitionCs( code.getDefinitionCs());
		clonedCode.setDefinitionDa( code.getDefinitionDa());
		clonedCode.setDefinitionNl( code.getDefinitionNl());
		clonedCode.setDefinitionEn( code.getDefinitionEn());
		clonedCode.setDefinitionFi( code.getDefinitionFi());
		clonedCode.setDefinitionFr( code.getDefinitionFr());
		clonedCode.setDefinitionDe( code.getDefinitionDe());
		clonedCode.setDefinitionEl( code.getDefinitionEl());
		clonedCode.setDefinitionHu( code.getDefinitionHu());
		clonedCode.setDefinitionLt( code.getDefinitionLt());
		clonedCode.setDefinitionNo( code.getDefinitionNo());
		clonedCode.setDefinitionPt( code.getDefinitionPt());
		clonedCode.setDefinitionRo( code.getDefinitionRo());
		clonedCode.setDefinitionSk( code.getDefinitionSk());
		clonedCode.setDefinitionSl( code.getDefinitionSl());
		clonedCode.setDefinitionEs( code.getDefinitionEs());
		clonedCode.setDefinitionSv( code.getDefinitionSv());
		
		return clonedCode;
	}
	
}















