package eu.cessda.cvmanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.gesis.wts.domain.Agency;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * A Vocabulary.
 */
@Entity
@Table(name = "vocabulary")
@Document(indexName = "vocabulary")
public class Vocabulary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "uri", nullable = false)
    private String uri;

    @NotNull
    @Size(max = 20)
    @Column(name = "version", length = 20, nullable = false)
    private String version;

    @Column(name = "archived")
    private Boolean archived;

    @Column(name = "withdrawn")
    private Boolean withdrawn;

    @Column(name = "discoverable")
    private Boolean discoverable;

    @Column(name = "languages")
    @ElementCollection( targetClass=String.class )
    @MultiField(
            mainField = @Field(type = FieldType.keyword, index = true),
            otherFields = {
            		@InnerField(suffix = "untouched", type = FieldType.keyword, store = true, index = false),
//            		@InnerField(suffix = "sort", type = FieldType.keyword, store = true, indexAnalyzer = "keyword")
            }
    )
    private Set<String> languages;
    
    @NotNull
    @Size(max = 20)
    @Column(name = "source_language", length = 20, nullable = false)
    private String sourceLanguage;
    
    @NotNull
    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    @NotNull
    @Column(name = "agency_name", nullable = false)
    private String agencyName;

    @Lob
    @Column(name = "title_cs")
    @Field(type = FieldType.text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String titleCs;

    @Lob
    @Column(name = "definition_cs")
    @Field(type = FieldType.text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String definitionCs;

    @Lob
    @Column(name = "title_da")
    @Field(type = FieldType.text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String titleDa;

    @Lob
    @Column(name = "definition_da")
    @Field(type = FieldType.text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String definitionDa;

    @Lob
    @Column(name = "title_nl")
    @Field(type = FieldType.text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String titleNl;

    @Lob
    @Column(name = "definition_nl")
    @Field(type = FieldType.text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String definitionNl;

    @Lob
    @Column(name = "title_en")
    @Field(type = FieldType.text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String titleEn;

    @Lob
    @Column(name = "definition_en")
    @Field(type = FieldType.text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String definitionEn;

    @Lob
    @Column(name = "title_fi")
    @Field(type = FieldType.text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String titleFi;

    @Lob
    @Column(name = "definition_fi")
    @Field(type = FieldType.text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String definitionFi;

    @Lob
    @Column(name = "title_fr")
    @Field(type = FieldType.text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String titleFr;

    @Lob
    @Column(name = "definition_fr")
    @Field(type = FieldType.text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String definitionFr;

    @Lob
    @Column(name = "title_de")
    @Field(type = FieldType.text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String titleDe;

    @Lob
    @Column(name = "definition_de")
    @Field(type = FieldType.text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String definitionDe;

    @Lob
    @Column(name = "title_el")
    @Field(type = FieldType.text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String titleEl;

    @Lob
    @Column(name = "definition_el")
    @Field(type = FieldType.text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String definitionEl;

    @Lob
    @Column(name = "title_hu")
    @Field(type = FieldType.text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String titleHu;

    @Lob
    @Column(name = "definition_hu")
    @Field(type = FieldType.text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String definitionHu;

    @Lob
    @Column(name = "title_lt")
    private String titleLt;

    @Lob
    @Column(name = "definition_lt")
    private String definitionLt;

    @Lob
    @Column(name = "title_no")
    @Field(type = FieldType.text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String titleNo;

    @Lob
    @Column(name = "definition_no")
    @Field(type = FieldType.text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String definitionNo;

    @Lob
    @Column(name = "title_pt")
    @Field(type = FieldType.text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String titlePt;

    @Lob
    @Column(name = "definition_pt")
    @Field(type = FieldType.text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String definitionPt;

    @Lob
    @Column(name = "title_ro")
    @Field(type = FieldType.text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String titleRo;

    @Lob
    @Column(name = "definition_ro")
    @Field(type = FieldType.text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String definitionRo;

    @Lob
    @Column(name = "title_sk")
    private String titleSk;

    @Lob
    @Column(name = "definition_sk")
    private String definitionSk;

    @Lob
    @Column(name = "title_sl")
    private String titleSl;

    @Lob
    @Column(name = "definition_sl")
    private String definitionSl;

    @Lob
    @Column(name = "title_es")
    @Field(type = FieldType.text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String titleEs;

    @Lob
    @Column(name = "definition_es")
    @Field(type = FieldType.text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String definitionEs;

    @Lob
    @Column(name = "title_se")
    @Field(type = FieldType.text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String titleSe;

    @Lob
    @Column(name = "definition_se")
    @Field(type = FieldType.text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String definitionSe;

    @OneToMany(mappedBy = "vocabulary")
    @JsonIgnore
    private Set<Code> codes = new HashSet<>();

    @Transient
    private Agency agency;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public Vocabulary uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public Vocabulary version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean isArchived() {
        return archived;
    }

    public Vocabulary archived(Boolean archived) {
        this.archived = archived;
        return this;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean isWithdrawn() {
        return withdrawn;
    }

    public Vocabulary withdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
        return this;
    }

    public void setWithdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public Boolean isDiscoverable() {
        return discoverable;
    }

    public Vocabulary discoverable(Boolean discoverable) {
        this.discoverable = discoverable;
        return this;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public Vocabulary sourceLanguage(String sourceLanguage) {
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

    public Vocabulary agencyName(String agencyName) {
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
	
	public Vocabulary addLanguage(String language) {
		this.languages.add(language);
		return this;
	}

    public String getTitleCs() {
        return titleCs;
    }

    public Vocabulary titleCs(String titleCs) {
        this.titleCs = titleCs;
        return this;
    }

    public void setTitleCs(String titleCs) {
        this.titleCs = titleCs;
    }

    public String getDefinitionCs() {
        return definitionCs;
    }

    public Vocabulary definitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
        return this;
    }

    public void setDefinitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
    }

    public String getTitleDa() {
        return titleDa;
    }

    public Vocabulary titleDa(String titleDa) {
        this.titleDa = titleDa;
        return this;
    }

    public void setTitleDa(String titleDa) {
        this.titleDa = titleDa;
    }

    public String getDefinitionDa() {
        return definitionDa;
    }

    public Vocabulary definitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
        return this;
    }

    public void setDefinitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public Vocabulary titleNl(String titleNl) {
        this.titleNl = titleNl;
        return this;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getDefinitionNl() {
        return definitionNl;
    }

    public Vocabulary definitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
        return this;
    }

    public void setDefinitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public Vocabulary titleEn(String titleEn) {
        this.titleEn = titleEn;
        return this;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDefinitionEn() {
        return definitionEn;
    }

    public Vocabulary definitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
        return this;
    }

    public void setDefinitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
    }

    public String getTitleFi() {
        return titleFi;
    }

    public Vocabulary titleFi(String titleFi) {
        this.titleFi = titleFi;
        return this;
    }

    public void setTitleFi(String titleFi) {
        this.titleFi = titleFi;
    }

    public String getDefinitionFi() {
        return definitionFi;
    }

    public Vocabulary definitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
        return this;
    }

    public void setDefinitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public Vocabulary titleFr(String titleFr) {
        this.titleFr = titleFr;
        return this;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getDefinitionFr() {
        return definitionFr;
    }

    public Vocabulary definitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
        return this;
    }

    public void setDefinitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
    }

    public String getTitleDe() {
        return titleDe;
    }

    public Vocabulary titleDe(String titleDe) {
        this.titleDe = titleDe;
        return this;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getDefinitionDe() {
        return definitionDe;
    }

    public Vocabulary definitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
        return this;
    }

    public void setDefinitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
    }

    public String getTitleEl() {
        return titleEl;
    }

    public Vocabulary titleEl(String titleEl) {
        this.titleEl = titleEl;
        return this;
    }

    public void setTitleEl(String titleEl) {
        this.titleEl = titleEl;
    }

    public String getDefinitionEl() {
        return definitionEl;
    }

    public Vocabulary definitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
        return this;
    }

    public void setDefinitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
    }

    public String getTitleHu() {
        return titleHu;
    }

    public Vocabulary titleHu(String titleHu) {
        this.titleHu = titleHu;
        return this;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getDefinitionHu() {
        return definitionHu;
    }

    public Vocabulary definitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
        return this;
    }

    public void setDefinitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
    }

    public String getTitleLt() {
        return titleLt;
    }

    public Vocabulary titleLt(String titleLt) {
        this.titleLt = titleLt;
        return this;
    }

    public void setTitleLt(String titleLt) {
        this.titleLt = titleLt;
    }

    public String getDefinitionLt() {
        return definitionLt;
    }

    public Vocabulary definitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
        return this;
    }

    public void setDefinitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
    }

    public String getTitleNo() {
        return titleNo;
    }

    public Vocabulary titleNo(String titleNo) {
        this.titleNo = titleNo;
        return this;
    }

    public void setTitleNo(String titleNo) {
        this.titleNo = titleNo;
    }

    public String getDefinitionNo() {
        return definitionNo;
    }

    public Vocabulary definitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
        return this;
    }

    public void setDefinitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public Vocabulary titlePt(String titlePt) {
        this.titlePt = titlePt;
        return this;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getDefinitionPt() {
        return definitionPt;
    }

    public Vocabulary definitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
        return this;
    }

    public void setDefinitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
    }

    public String getTitleRo() {
        return titleRo;
    }

    public Vocabulary titleRo(String titleRo) {
        this.titleRo = titleRo;
        return this;
    }

    public void setTitleRo(String titleRo) {
        this.titleRo = titleRo;
    }

    public String getDefinitionRo() {
        return definitionRo;
    }

    public Vocabulary definitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
        return this;
    }

    public void setDefinitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
    }

    public String getTitleSk() {
        return titleSk;
    }

    public Vocabulary titleSk(String titleSk) {
        this.titleSk = titleSk;
        return this;
    }

    public void setTitleSk(String titleSk) {
        this.titleSk = titleSk;
    }

    public String getDefinitionSk() {
        return definitionSk;
    }

    public Vocabulary definitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
        return this;
    }

    public void setDefinitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
    }

    public String getTitleSl() {
        return titleSl;
    }

    public Vocabulary titleSl(String titleSl) {
        this.titleSl = titleSl;
        return this;
    }

    public void setTitleSl(String titleSl) {
        this.titleSl = titleSl;
    }

    public String getDefinitionSl() {
        return definitionSl;
    }

    public Vocabulary definitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
        return this;
    }

    public void setDefinitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
    }

    public String getTitleEs() {
        return titleEs;
    }

    public Vocabulary titleEs(String titleEs) {
        this.titleEs = titleEs;
        return this;
    }

    public void setTitleEs(String titleEs) {
        this.titleEs = titleEs;
    }

    public String getDefinitionEs() {
        return definitionEs;
    }

    public Vocabulary definitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
        return this;
    }

    public void setDefinitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
    }

    public String getTitleSe() {
        return titleSe;
    }

    public Vocabulary titleSe(String titleSe) {
        this.titleSe = titleSe;
        return this;
    }

    public void setTitleSe(String titleSe) {
        this.titleSe = titleSe;
    }

    public String getDefinitionSe() {
        return definitionSe;
    }

    public Vocabulary definitionSe(String definitionSe) {
        this.definitionSe = definitionSe;
        return this;
    }

    public void setDefinitionSe(String definitionSe) {
        this.definitionSe = definitionSe;
    }

    public Set<Code> getCodes() {
        return codes;
    }

    public Vocabulary codes(Set<Code> codes) {
        this.codes = codes;
        return this;
    }

    public Vocabulary addCode(Code code) {
        this.codes.add(code);
        code.setVocabulary(this);
        return this;
    }

    public Vocabulary removeCode(Code code) {
        this.codes.remove(code);
        code.setVocabulary(null);
        return this;
    }

    public void setCodes(Set<Code> codes) {
        this.codes = codes;
    }

    public Agency getAgency() {
        return agency;
    }

    public Vocabulary agency(Agency agency) {
        this.agency = agency;
        return this;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vocabulary vocabulary = (Vocabulary) o;
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
}
