package eu.cessda.cvmanager.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.gesis.wts.domain.enumeration.Language;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.cessda.cvmanager.domain.enumeration.Status;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * A Vocabulary.
 */
@MappedSuperclass
@Document(indexName = "vocabulary-basic")
public class BasicVocabulary implements Serializable {
    
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // only for SL status
    @Column(name = "status", length = 20, nullable = false)
    @Field(type = FieldType.keyword)
    private String status;
    
    @NotNull
    @Column(name = "uri", length = 240, nullable = false, unique = true)
    private String uri;
    
    @NotNull
    @Column(name = "notation", length = 240, nullable = false)
    @Field(type = FieldType.text, fielddata = true)
    private String notation;

    @NotNull
    @Size(max = 20)
    @Column(name = "version_number", length = 20, nullable = false)
    private String versionNumber;
    
    @Column(name = "initial_publication")
    private Long initialPublication;
    
    @Column(name = "previous_publication")
    private Long previousPublication;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "withdrawn")
    private Boolean withdrawn = false;

    @Column(name = "discoverable")
    private Boolean discoverable = false;

    @Column(name = "languages")
    @ElementCollection( targetClass=String.class )
    @Field(type = FieldType.keyword)
    private Set<String> languages;
    
    @NotNull
    @Size(max = 20)
    @Column(name = "source_language", length = 20, nullable = false)
    @Field(type = FieldType.keyword)
    private String sourceLanguage;
    
    @NotNull
    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    @NotNull
    @Column(name = "agency_name", nullable = false)
    @Field(type = FieldType.keyword)
    private String agencyName;
    
//  @JsonBackReference
//  @OneToMany(mappedBy = "vocabulary")
    @Transient
    @Field(type = FieldType.Nested, store = true)
    private Set<Code> codes = new HashSet<>();
    
    @Transient
    @Field(type = FieldType.Nested, store = true)
    private Set<Version> vers = new HashSet<>();
	  
    @Transient
    private Language selectedLang;
	  
    @Column(name = "publication_date")
    @Field(type = FieldType.Date, format = DateFormat.date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;
    
    @Column(name = "last_modified")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastModified;
    
    @Size(max = 20)
    @Column(name = "version_cs", length = 20)
    private String versionCs;

    @Lob
    @Column(name = "title_cs")
    @Field(type = FieldType.text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String titleCs;

    @Lob
    @Column(name = "definition_cs")
    @Field(type = FieldType.text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String definitionCs;
    
    @Size(max = 20)
    @Column(name = "version_da", length = 20)
    private String versionDa;

    @Lob
    @Column(name = "title_da")
    @Field(type = FieldType.text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String titleDa;

    @Lob
    @Column(name = "definition_da")
    @Field(type = FieldType.text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String definitionDa;
    
    @Size(max = 20)
    @Column(name = "version_nl", length = 20)
    private String versionNl;

    @Lob
    @Column(name = "title_nl")
    @Field(type = FieldType.text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String titleNl;

    @Lob
    @Column(name = "definition_nl")
    @Field(type = FieldType.text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String definitionNl;
    
    @Size(max = 20)
    @Column(name = "version_en", length = 20)
    private String versionEn;

    @Lob
    @Column(name = "title_en")
    @Field(type = FieldType.text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String titleEn;

    @Lob
    @Column(name = "definition_en")
    @Field(type = FieldType.text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String definitionEn;
    
    @Size(max = 20)
    @Column(name = "version_fi", length = 20)
    private String versionFi;

    @Lob
    @Column(name = "title_fi")
    @Field(type = FieldType.text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String titleFi;

    @Lob
    @Column(name = "definition_fi")
    @Field(type = FieldType.text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String definitionFi;

    @Size(max = 20)
    @Column(name = "version_fr", length = 20)
    private String versionFr;
    
    @Lob
    @Column(name = "title_fr")
    @Field(type = FieldType.text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String titleFr;

    @Lob
    @Column(name = "definition_fr")
    @Field(type = FieldType.text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String definitionFr;
    
    @Size(max = 20)
    @Column(name = "version_de", length = 20)
    private String versionDe;

    @Lob
    @Column(name = "title_de")
    @Field(type = FieldType.text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String titleDe;

    @Lob
    @Column(name = "definition_de")
    @Field(type = FieldType.text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String definitionDe;

    @Size(max = 20)
    @Column(name = "version_el", length = 20)
    private String versionEl;
    
    @Lob
    @Column(name = "title_el")
    @Field(type = FieldType.text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String titleEl;

    @Lob
    @Column(name = "definition_el")
    @Field(type = FieldType.text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String definitionEl;

    @Size(max = 20)
    @Column(name = "version_hu", length = 20)
    private String versionHu;
    
    @Lob
    @Column(name = "title_hu")
    @Field(type = FieldType.text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String titleHu;

    @Lob
    @Column(name = "definition_hu")
    @Field(type = FieldType.text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String definitionHu;

    @Size(max = 20)
    @Column(name = "version_lt", length = 20)
    private String versionLt;
    
    @Lob
    @Column(name = "title_lt")
    @Field(type = FieldType.text, store = true )
    private String titleLt;

    @Lob
    @Column(name = "definition_lt")
    @Field(type = FieldType.text, store = true )
    private String definitionLt;

    @Size(max = 20)
    @Column(name = "version_no", length = 20)
    private String versionNo;
    
    @Lob
    @Column(name = "title_no")
    @Field(type = FieldType.text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String titleNo;

    @Lob
    @Column(name = "definition_no")
    @Field(type = FieldType.text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String definitionNo;

    @Size(max = 20)
    @Column(name = "version_pt", length = 20)
    private String versionPt;
    
    @Lob
    @Column(name = "title_pt")
    @Field(type = FieldType.text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String titlePt;

    @Lob
    @Column(name = "definition_pt")
    @Field(type = FieldType.text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String definitionPt;
    
    @Size(max = 20)
    @Column(name = "version_ro", length = 20)
    private String versionRo;

    @Lob
    @Column(name = "title_ro")
    @Field(type = FieldType.text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String titleRo;

    @Lob
    @Column(name = "definition_ro")
    @Field(type = FieldType.text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String definitionRo;

    @Size(max = 20)
    @Column(name = "version_sk", length = 20)
    private String versionSk;
    
    @Lob
    @Column(name = "title_sk")
    @Field(type = FieldType.text, store = true )
    private String titleSk;

    @Lob
    @Column(name = "definition_sk")
    @Field(type = FieldType.text, store = true )
    private String definitionSk;

    @Size(max = 20)
    @Column(name = "version_sl", length = 20)
    private String versionSl;
    
    @Lob
    @Column(name = "title_sl")
    @Field(type = FieldType.text, store = true )
    private String titleSl;

    @Lob
    @Column(name = "definition_sl")
    @Field(type = FieldType.text, store = true )
    private String definitionSl;

    @Size(max = 20)
    @Column(name = "version_es", length = 20)
    private String versionEs;
    
    @Lob
    @Column(name = "title_es")
    @Field(type = FieldType.text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String titleEs;

    @Lob
    @Column(name = "definition_es")
    @Field(type = FieldType.text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String definitionEs;

    @Size(max = 20)
    @Column(name = "version_sv", length = 20)
    private String versionSv;
    
    @Lob
    @Column(name = "title_sv")
    @Field(type = FieldType.text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String titleSv;

    @Lob
    @Column(name = "definition_sv")
    @Field(type = FieldType.text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String definitionSv;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public BasicVocabulary uri(String uri) {
        this.uri = uri;
        return this;
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

    public BasicVocabulary versionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
        return this;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Boolean isArchived() {
        return archived;
    }

    public BasicVocabulary archived(Boolean archived) {
        this.archived = archived;
        return this;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean isWithdrawn() {
        return withdrawn;
    }

    public BasicVocabulary withdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
        return this;
    }

    public void setWithdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public Boolean isDiscoverable() {
        return discoverable;
    }

    public BasicVocabulary discoverable(Boolean discoverable) {
        this.discoverable = discoverable;
        return this;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public BasicVocabulary sourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
        return this;
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

    public BasicVocabulary agencyName(String agencyName) {
        this.agencyName = agencyName;
        return this;
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
	
	public BasicVocabulary addLanguage(String language) {
		this.languages.add(language);
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
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitleCs() {
        return titleCs;
    }

    public BasicVocabulary titleCs(String titleCs) {
        this.titleCs = titleCs;
        return this;
    }

    public void setTitleCs(String titleCs) {
        this.titleCs = titleCs;
    }

    public String getDefinitionCs() {
        return definitionCs;
    }

    public BasicVocabulary definitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
        return this;
    }

    public void setDefinitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
    }

    public String getTitleDa() {
        return titleDa;
    }

    public BasicVocabulary titleDa(String titleDa) {
        this.titleDa = titleDa;
        return this;
    }

    public void setTitleDa(String titleDa) {
        this.titleDa = titleDa;
    }

    public String getDefinitionDa() {
        return definitionDa;
    }

    public BasicVocabulary definitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
        return this;
    }

    public void setDefinitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public BasicVocabulary titleNl(String titleNl) {
        this.titleNl = titleNl;
        return this;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getDefinitionNl() {
        return definitionNl;
    }

    public BasicVocabulary definitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
        return this;
    }

    public void setDefinitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public BasicVocabulary titleEn(String titleEn) {
        this.titleEn = titleEn;
        return this;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDefinitionEn() {
        return definitionEn;
    }

    public BasicVocabulary definitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
        return this;
    }

    public void setDefinitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
    }

    public String getTitleFi() {
        return titleFi;
    }

    public BasicVocabulary titleFi(String titleFi) {
        this.titleFi = titleFi;
        return this;
    }

    public void setTitleFi(String titleFi) {
        this.titleFi = titleFi;
    }

    public String getDefinitionFi() {
        return definitionFi;
    }

    public BasicVocabulary definitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
        return this;
    }

    public void setDefinitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public BasicVocabulary titleFr(String titleFr) {
        this.titleFr = titleFr;
        return this;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getDefinitionFr() {
        return definitionFr;
    }

    public BasicVocabulary definitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
        return this;
    }

    public void setDefinitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
    }

    public String getTitleDe() {
        return titleDe;
    }

    public BasicVocabulary titleDe(String titleDe) {
        this.titleDe = titleDe;
        return this;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getDefinitionDe() {
        return definitionDe;
    }

    public BasicVocabulary definitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
        return this;
    }

    public void setDefinitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
    }

    public String getTitleEl() {
        return titleEl;
    }

    public BasicVocabulary titleEl(String titleEl) {
        this.titleEl = titleEl;
        return this;
    }

    public void setTitleEl(String titleEl) {
        this.titleEl = titleEl;
    }

    public String getDefinitionEl() {
        return definitionEl;
    }

    public BasicVocabulary definitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
        return this;
    }

    public void setDefinitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
    }

    public String getTitleHu() {
        return titleHu;
    }

    public BasicVocabulary titleHu(String titleHu) {
        this.titleHu = titleHu;
        return this;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getDefinitionHu() {
        return definitionHu;
    }

    public BasicVocabulary definitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
        return this;
    }

    public void setDefinitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
    }

    public String getTitleLt() {
        return titleLt;
    }

    public BasicVocabulary titleLt(String titleLt) {
        this.titleLt = titleLt;
        return this;
    }

    public void setTitleLt(String titleLt) {
        this.titleLt = titleLt;
    }

    public String getDefinitionLt() {
        return definitionLt;
    }

    public BasicVocabulary definitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
        return this;
    }

    public void setDefinitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
    }

    public String getTitleNo() {
        return titleNo;
    }

    public BasicVocabulary titleNo(String titleNo) {
        this.titleNo = titleNo;
        return this;
    }

    public void setTitleNo(String titleNo) {
        this.titleNo = titleNo;
    }

    public String getDefinitionNo() {
        return definitionNo;
    }

    public BasicVocabulary definitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
        return this;
    }

    public void setDefinitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public BasicVocabulary titlePt(String titlePt) {
        this.titlePt = titlePt;
        return this;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getDefinitionPt() {
        return definitionPt;
    }

    public BasicVocabulary definitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
        return this;
    }

    public void setDefinitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
    }

    public String getTitleRo() {
        return titleRo;
    }

    public BasicVocabulary titleRo(String titleRo) {
        this.titleRo = titleRo;
        return this;
    }

    public void setTitleRo(String titleRo) {
        this.titleRo = titleRo;
    }

    public String getDefinitionRo() {
        return definitionRo;
    }

    public BasicVocabulary definitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
        return this;
    }

    public void setDefinitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
    }

    public String getTitleSk() {
        return titleSk;
    }

    public BasicVocabulary titleSk(String titleSk) {
        this.titleSk = titleSk;
        return this;
    }

    public void setTitleSk(String titleSk) {
        this.titleSk = titleSk;
    }

    public String getDefinitionSk() {
        return definitionSk;
    }

    public BasicVocabulary definitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
        return this;
    }

    public void setDefinitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
    }

    public String getTitleSl() {
        return titleSl;
    }

    public BasicVocabulary titleSl(String titleSl) {
        this.titleSl = titleSl;
        return this;
    }

    public void setTitleSl(String titleSl) {
        this.titleSl = titleSl;
    }

    public String getDefinitionSl() {
        return definitionSl;
    }

    public BasicVocabulary definitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
        return this;
    }

    public void setDefinitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
    }

    public String getTitleEs() {
        return titleEs;
    }

    public BasicVocabulary titleEs(String titleEs) {
        this.titleEs = titleEs;
        return this;
    }

    public void setTitleEs(String titleEs) {
        this.titleEs = titleEs;
    }

    public String getDefinitionEs() {
        return definitionEs;
    }

    public BasicVocabulary definitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
        return this;
    }

    public void setDefinitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
    }

    public String getTitleSv() {
        return titleSv;
    }

    public BasicVocabulary titleSv(String titleSv) {
        this.titleSv = titleSv;
        return this;
    }

    public void setTitleSv(String titleSv) {
        this.titleSv = titleSv;
    }

    public String getDefinitionSv() {
        return definitionSv;
    }

    public BasicVocabulary definitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
        return this;
    }

    public void setDefinitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
    }

    public Set<Code> getCodes() {
        return codes;
    }

    public BasicVocabulary codes(Set<Code> codes) {
        this.codes = codes;
        return this;
    }

    public BasicVocabulary addCode(Code code) {
        this.codes.add(code);
//        code.setVocabulary(this);
        return this;
    }

    public BasicVocabulary removeCode(Code code) {
        this.codes.remove(code);
//        code.setVocabulary(null);
        return this;
    }
    
	public void setCodes(Set<Code> codes) {
        this.codes = codes;
    }

    public Language getSelectedLang() {
		return selectedLang;
	}

	public void setSelectedLang(Language selectedLang) {
		this.selectedLang = selectedLang;
	}

	public Long getPreviousPublication() {
		return previousPublication;
	}

	public void setPreviousPublication(Long previousPublication) {
		this.previousPublication = previousPublication;
	}

	public Long getInitialPublication() {
		return initialPublication;
	}

	public void setInitialPublication(Long initialPublication) {
		this.initialPublication = initialPublication;
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

	public Set<Version> getVers() {
		return vers;
	}

	public void setVers(Set<Version> vers) {
		this.vers = vers;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicVocabulary vocabulary = (BasicVocabulary) o;
        if (vocabulary.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vocabulary.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
            "id=" + getId() +
            ", uri='" + getUri() + "'" +
            ", versionNumber='" + getVersionNumber() + "'" +
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
}
