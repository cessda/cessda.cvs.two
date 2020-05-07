package eu.cessda.cvs.domain;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * A Code.
 */
public class Code implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String uri;

    private String notation;

    private Boolean archived;

    private Boolean withdrawn;

    private Boolean discoverable;

    @Field( type = FieldType.Keyword )
    private Set<String> languages;

    @Field( type = FieldType.Keyword )
    private String sourceLanguage;

    private String parent;

    private Integer position;

    @Field( type = FieldType.Date, format = DateFormat.date )
    private LocalDate publicationDate;

    @Field( type = FieldType.Date, format = DateFormat.date_hour_minute_second )
    private ZonedDateTime lastModified;

    private Long vocabularyId;

    private Long versionId;

    private String versionNumber;

    @Field( type = FieldType.Text, store = true )
    private String titleSq;

    @Field( type = FieldType.Text, store = true )
    private String definitionSq;

    @Field( type = FieldType.Text, store = true )
    private String titleBs;

    @Field( type = FieldType.Text, store = true )
    private String definitionBs;

    @Field( type = FieldType.Text, store = true, analyzer = "bulgarian", searchAnalyzer = "bulgarian" )
    private String titleBg;

    @Field( type = FieldType.Text, store = true, analyzer = "bulgarian", searchAnalyzer = "bulgarian" )
    private String definitionBg;

    @Field( type = FieldType.Text, store = true )
    private String titleHr;

    @Field( type = FieldType.Text, store = true )
    private String definitionHr;

    @Field( type = FieldType.Text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String titleCs;

    @Field( type = FieldType.Text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String definitionCs;

    @Field( type = FieldType.Text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String titleDa;

    @Field( type = FieldType.Text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String definitionDa;

    @Field( type = FieldType.Text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String titleNl;

    @Field( type = FieldType.Text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String definitionNl;

    @Field( type = FieldType.Text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String titleEn;

    @Field( type = FieldType.Text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String definitionEn;

    @Field( type = FieldType.Text, store = true )
    private String titleEt;

    @Field( type = FieldType.Text, store = true )
    private String definitionEt;

    @Field( type = FieldType.Text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String titleFi;

    @Field( type = FieldType.Text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String definitionFi;

    @Field( type = FieldType.Text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String titleFr;

    @Field( type = FieldType.Text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String definitionFr;

    @Field( type = FieldType.Text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String titleDe;

    @Field( type = FieldType.Text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String definitionDe;

    @Field( type = FieldType.Text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String titleEl;

    @Field( type = FieldType.Text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String definitionEl;

    @Field( type = FieldType.Text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String titleHu;

    @Field( type = FieldType.Text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String definitionHu;

    @Field( type = FieldType.Text, store = true, analyzer = "italian", searchAnalyzer = "italian" )
    private String titleIt;

    @Field( type = FieldType.Text, store = true, analyzer = "italian", searchAnalyzer = "italian" )
    private String definitionIt;

    @Field( type = FieldType.Text, store = true )
    private String titleJa;

    @Field( type = FieldType.Text, store = true )
    private String definitionJa;

    @Field( type = FieldType.Text, store = true )
    private String titleLt;

    @Field( type = FieldType.Text, store = true )
    private String definitionLt;

    @Field( type = FieldType.Text, store = true )
    private String titleMk;

    @Field( type = FieldType.Text, store = true )
    private String definitionMk;

    @Field( type = FieldType.Text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String titleNo;

    @Field( type = FieldType.Text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String definitionNo;

    @Field( type = FieldType.Text, store = true )
    private String titlePl;

    @Field( type = FieldType.Text, store = true )
    private String definitionPl;

    @Field( type = FieldType.Text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String titlePt;

    @Field( type = FieldType.Text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String definitionPt;

    @Field( type = FieldType.Text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String titleRo;

    @Field( type = FieldType.Text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String definitionRo;

    @Field( type = FieldType.Text, store = true, analyzer = "russian", searchAnalyzer = "russian" )
    private String titleRu;

    @Field( type = FieldType.Text, store = true, analyzer = "russian", searchAnalyzer = "russian" )
    private String definitionRu;

    @Field( type = FieldType.Text, store = true )
    private String titleSr;

    @Field( type = FieldType.Text, store = true )
    private String definitionSr;

    @Field( type = FieldType.Text, store = true )
    private String titleSk;

    @Field( type = FieldType.Text, store = true )
    private String definitionSk;

    @Field( type = FieldType.Text, store = true )
    private String titleSl;

    @Field( type = FieldType.Text, store = true )
    private String definitionSl;

    @Field( type = FieldType.Text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String titleEs;

    @Field( type = FieldType.Text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String definitionEs;

    @Field( type = FieldType.Text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String titleSv;

    @Field( type = FieldType.Text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
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

    public Code uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNotation() {
        return notation;
    }

    public Code notation(String notation) {
        this.notation = notation;
        return this;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public Boolean isArchived() {
        return archived;
    }

    public Code archived(Boolean archived) {
        this.archived = archived;
        return this;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean isWithdrawn() {
        return withdrawn;
    }

    public Code withdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
        return this;
    }

    public void setWithdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public Boolean isDiscoverable() {
        return discoverable;
    }

    public Code discoverable(Boolean discoverable) {
        this.discoverable = discoverable;
        return this;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public Set<String> getLanguages()
    {
        return languages;
    }

    public void setLanguages( Set<String> languages )
    {
        this.languages = languages;
    }

    public Code addLanguage(String language )
    {
        this.languages.add( language );
        return this;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public Code sourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
        return this;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getParent() {
        return parent;
    }

    public Code parent(String parent) {
        this.parent = parent;
        return this;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public Code position(Integer position) {
        this.position = position;
        return this;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public Code publicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
        return this;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public Code lastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Long getVocabularyId()
    {
        return vocabularyId;
    }

    public void setVocabularyId( Long vocabularyId )
    {
        this.vocabularyId = vocabularyId;
    }

    public Long getVersionId()
    {
        return versionId;
    }

    public void setVersionId( Long versionId )
    {
        this.versionId = versionId;
    }

    public String getVersionNumber()
    {
        return versionNumber;
    }

    public void setVersionNumber( String versionNumber )
    {
        this.versionNumber = versionNumber;
    }

    public String getTitleSq() {
        return titleSq;
    }

    public Code titleSq(String titleSq) {
        this.titleSq = titleSq;
        return this;
    }

    public void setTitleSq(String titleSq) {
        this.titleSq = titleSq;
    }

    public String getDefinitionSq() {
        return definitionSq;
    }

    public Code definitionSq(String definitionSq) {
        this.definitionSq = definitionSq;
        return this;
    }

    public void setDefinitionSq(String definitionSq) {
        this.definitionSq = definitionSq;
    }

    public String getTitleBs() {
        return titleBs;
    }

    public Code titleBs(String titleBs) {
        this.titleBs = titleBs;
        return this;
    }

    public void setTitleBs(String titleBs) {
        this.titleBs = titleBs;
    }

    public String getDefinitionBs() {
        return definitionBs;
    }

    public Code definitionBs(String definitionBs) {
        this.definitionBs = definitionBs;
        return this;
    }

    public void setDefinitionBs(String definitionBs) {
        this.definitionBs = definitionBs;
    }

    public String getTitleBg() {
        return titleBg;
    }

    public Code titleBg(String titleBg) {
        this.titleBg = titleBg;
        return this;
    }

    public void setTitleBg(String titleBg) {
        this.titleBg = titleBg;
    }

    public String getDefinitionBg() {
        return definitionBg;
    }

    public Code definitionBg(String definitionBg) {
        this.definitionBg = definitionBg;
        return this;
    }

    public void setDefinitionBg(String definitionBg) {
        this.definitionBg = definitionBg;
    }

    public String getTitleHr() {
        return titleHr;
    }

    public Code titleHr(String titleHr) {
        this.titleHr = titleHr;
        return this;
    }

    public void setTitleHr(String titleHr) {
        this.titleHr = titleHr;
    }

    public String getDefinitionHr() {
        return definitionHr;
    }

    public Code definitionHr(String definitionHr) {
        this.definitionHr = definitionHr;
        return this;
    }

    public void setDefinitionHr(String definitionHr) {
        this.definitionHr = definitionHr;
    }

    public String getTitleCs() {
        return titleCs;
    }

    public Code titleCs(String titleCs) {
        this.titleCs = titleCs;
        return this;
    }

    public void setTitleCs(String titleCs) {
        this.titleCs = titleCs;
    }

    public String getDefinitionCs() {
        return definitionCs;
    }

    public Code definitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
        return this;
    }

    public void setDefinitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
    }

    public String getTitleDa() {
        return titleDa;
    }

    public Code titleDa(String titleDa) {
        this.titleDa = titleDa;
        return this;
    }

    public void setTitleDa(String titleDa) {
        this.titleDa = titleDa;
    }

    public String getDefinitionDa() {
        return definitionDa;
    }

    public Code definitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
        return this;
    }

    public void setDefinitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public Code titleNl(String titleNl) {
        this.titleNl = titleNl;
        return this;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getDefinitionNl() {
        return definitionNl;
    }

    public Code definitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
        return this;
    }

    public void setDefinitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public Code titleEn(String titleEn) {
        this.titleEn = titleEn;
        return this;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDefinitionEn() {
        return definitionEn;
    }

    public Code definitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
        return this;
    }

    public void setDefinitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
    }

    public String getTitleEt() {
        return titleEt;
    }

    public Code titleEt(String titleEt) {
        this.titleEt = titleEt;
        return this;
    }

    public void setTitleEt(String titleEt) {
        this.titleEt = titleEt;
    }

    public String getDefinitionEt() {
        return definitionEt;
    }

    public Code definitionEt(String definitionEt) {
        this.definitionEt = definitionEt;
        return this;
    }

    public void setDefinitionEt(String definitionEt) {
        this.definitionEt = definitionEt;
    }

    public String getTitleFi() {
        return titleFi;
    }

    public Code titleFi(String titleFi) {
        this.titleFi = titleFi;
        return this;
    }

    public void setTitleFi(String titleFi) {
        this.titleFi = titleFi;
    }

    public String getDefinitionFi() {
        return definitionFi;
    }

    public Code definitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
        return this;
    }

    public void setDefinitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public Code titleFr(String titleFr) {
        this.titleFr = titleFr;
        return this;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getDefinitionFr() {
        return definitionFr;
    }

    public Code definitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
        return this;
    }

    public void setDefinitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
    }

    public String getTitleDe() {
        return titleDe;
    }

    public Code titleDe(String titleDe) {
        this.titleDe = titleDe;
        return this;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getDefinitionDe() {
        return definitionDe;
    }

    public Code definitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
        return this;
    }

    public void setDefinitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
    }

    public String getTitleEl() {
        return titleEl;
    }

    public Code titleEl(String titleEl) {
        this.titleEl = titleEl;
        return this;
    }

    public void setTitleEl(String titleEl) {
        this.titleEl = titleEl;
    }

    public String getDefinitionEl() {
        return definitionEl;
    }

    public Code definitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
        return this;
    }

    public void setDefinitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
    }

    public String getTitleHu() {
        return titleHu;
    }

    public Code titleHu(String titleHu) {
        this.titleHu = titleHu;
        return this;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getDefinitionHu() {
        return definitionHu;
    }

    public Code definitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
        return this;
    }

    public void setDefinitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
    }

    public String getTitleIt() {
        return titleIt;
    }

    public Code titleIt(String titleIt) {
        this.titleIt = titleIt;
        return this;
    }

    public void setTitleIt(String titleIt) {
        this.titleIt = titleIt;
    }

    public String getDefinitionIt() {
        return definitionIt;
    }

    public Code definitionIt(String definitionIt) {
        this.definitionIt = definitionIt;
        return this;
    }

    public void setDefinitionIt(String definitionIt) {
        this.definitionIt = definitionIt;
    }

    public String getTitleJa() {
        return titleJa;
    }

    public Code titleJa(String titleJa) {
        this.titleJa = titleJa;
        return this;
    }

    public void setTitleJa(String titleJa) {
        this.titleJa = titleJa;
    }

    public String getDefinitionJa() {
        return definitionJa;
    }

    public Code definitionJa(String definitionJa) {
        this.definitionJa = definitionJa;
        return this;
    }

    public void setDefinitionJa(String definitionJa) {
        this.definitionJa = definitionJa;
    }

    public String getTitleLt() {
        return titleLt;
    }

    public Code titleLt(String titleLt) {
        this.titleLt = titleLt;
        return this;
    }

    public void setTitleLt(String titleLt) {
        this.titleLt = titleLt;
    }

    public String getDefinitionLt() {
        return definitionLt;
    }

    public Code definitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
        return this;
    }

    public void setDefinitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
    }

    public String getTitleMk() {
        return titleMk;
    }

    public Code titleMk(String titleMk) {
        this.titleMk = titleMk;
        return this;
    }

    public void setTitleMk(String titleMk) {
        this.titleMk = titleMk;
    }

    public String getDefinitionMk() {
        return definitionMk;
    }

    public Code definitionMk(String definitionMk) {
        this.definitionMk = definitionMk;
        return this;
    }

    public void setDefinitionMk(String definitionMk) {
        this.definitionMk = definitionMk;
    }

    public String getTitleNo() {
        return titleNo;
    }

    public Code titleNo(String titleNo) {
        this.titleNo = titleNo;
        return this;
    }

    public void setTitleNo(String titleNo) {
        this.titleNo = titleNo;
    }

    public String getDefinitionNo() {
        return definitionNo;
    }

    public Code definitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
        return this;
    }

    public void setDefinitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
    }

    public String getTitlePl() {
        return titlePl;
    }

    public Code titlePl(String titlePl) {
        this.titlePl = titlePl;
        return this;
    }

    public void setTitlePl(String titlePl) {
        this.titlePl = titlePl;
    }

    public String getDefinitionPl() {
        return definitionPl;
    }

    public Code definitionPl(String definitionPl) {
        this.definitionPl = definitionPl;
        return this;
    }

    public void setDefinitionPl(String definitionPl) {
        this.definitionPl = definitionPl;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public Code titlePt(String titlePt) {
        this.titlePt = titlePt;
        return this;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getDefinitionPt() {
        return definitionPt;
    }

    public Code definitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
        return this;
    }

    public void setDefinitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
    }

    public String getTitleRo() {
        return titleRo;
    }

    public Code titleRo(String titleRo) {
        this.titleRo = titleRo;
        return this;
    }

    public void setTitleRo(String titleRo) {
        this.titleRo = titleRo;
    }

    public String getDefinitionRo() {
        return definitionRo;
    }

    public Code definitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
        return this;
    }

    public void setDefinitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public Code titleRu(String titleRu) {
        this.titleRu = titleRu;
        return this;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public String getDefinitionRu() {
        return definitionRu;
    }

    public Code definitionRu(String definitionRu) {
        this.definitionRu = definitionRu;
        return this;
    }

    public void setDefinitionRu(String definitionRu) {
        this.definitionRu = definitionRu;
    }

    public String getTitleSr() {
        return titleSr;
    }

    public Code titleSr(String titleSr) {
        this.titleSr = titleSr;
        return this;
    }

    public void setTitleSr(String titleSr) {
        this.titleSr = titleSr;
    }

    public String getDefinitionSr() {
        return definitionSr;
    }

    public Code definitionSr(String definitionSr) {
        this.definitionSr = definitionSr;
        return this;
    }

    public void setDefinitionSr(String definitionSr) {
        this.definitionSr = definitionSr;
    }

    public String getTitleSk() {
        return titleSk;
    }

    public Code titleSk(String titleSk) {
        this.titleSk = titleSk;
        return this;
    }

    public void setTitleSk(String titleSk) {
        this.titleSk = titleSk;
    }

    public String getDefinitionSk() {
        return definitionSk;
    }

    public Code definitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
        return this;
    }

    public void setDefinitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
    }

    public String getTitleSl() {
        return titleSl;
    }

    public Code titleSl(String titleSl) {
        this.titleSl = titleSl;
        return this;
    }

    public void setTitleSl(String titleSl) {
        this.titleSl = titleSl;
    }

    public String getDefinitionSl() {
        return definitionSl;
    }

    public Code definitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
        return this;
    }

    public void setDefinitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
    }

    public String getTitleEs() {
        return titleEs;
    }

    public Code titleEs(String titleEs) {
        this.titleEs = titleEs;
        return this;
    }

    public void setTitleEs(String titleEs) {
        this.titleEs = titleEs;
    }

    public String getDefinitionEs() {
        return definitionEs;
    }

    public Code definitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
        return this;
    }

    public void setDefinitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
    }

    public String getTitleSv() {
        return titleSv;
    }

    public Code titleSv(String titleSv) {
        this.titleSv = titleSv;
        return this;
    }

    public void setTitleSv(String titleSv) {
        this.titleSv = titleSv;
    }

    public String getDefinitionSv() {
        return definitionSv;
    }

    public Code definitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
        return this;
    }

    public void setDefinitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Code)) {
            return false;
        }
        return id != null && id.equals(((Code) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Code{" +
            "id=" + getId() +
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", archived='" + isArchived() + "'" +
            ", withdrawn='" + isWithdrawn() + "'" +
            ", discoverable='" + isDiscoverable() + "'" +
            ", sourceLanguage='" + getSourceLanguage() + "'" +
            ", parent='" + getParent() + "'" +
            ", position=" + getPosition() +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", titleSq='" + getTitleSq() + "'" +
            ", definitionSq='" + getDefinitionSq() + "'" +
            ", titleBs='" + getTitleBs() + "'" +
            ", definitionBs='" + getDefinitionBs() + "'" +
            ", titleBg='" + getTitleBg() + "'" +
            ", definitionBg='" + getDefinitionBg() + "'" +
            ", titleHr='" + getTitleHr() + "'" +
            ", definitionHr='" + getDefinitionHr() + "'" +
            ", titleCs='" + getTitleCs() + "'" +
            ", definitionCs='" + getDefinitionCs() + "'" +
            ", titleDa='" + getTitleDa() + "'" +
            ", definitionDa='" + getDefinitionDa() + "'" +
            ", titleNl='" + getTitleNl() + "'" +
            ", definitionNl='" + getDefinitionNl() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            ", definitionEn='" + getDefinitionEn() + "'" +
            ", titleEt='" + getTitleEt() + "'" +
            ", definitionEt='" + getDefinitionEt() + "'" +
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
            ", titleIt='" + getTitleIt() + "'" +
            ", definitionIt='" + getDefinitionIt() + "'" +
            ", titleJa='" + getTitleJa() + "'" +
            ", definitionJa='" + getDefinitionJa() + "'" +
            ", titleLt='" + getTitleLt() + "'" +
            ", definitionLt='" + getDefinitionLt() + "'" +
            ", titleMk='" + getTitleMk() + "'" +
            ", definitionMk='" + getDefinitionMk() + "'" +
            ", titleNo='" + getTitleNo() + "'" +
            ", definitionNo='" + getDefinitionNo() + "'" +
            ", titlePl='" + getTitlePl() + "'" +
            ", definitionPl='" + getDefinitionPl() + "'" +
            ", titlePt='" + getTitlePt() + "'" +
            ", definitionPt='" + getDefinitionPt() + "'" +
            ", titleRo='" + getTitleRo() + "'" +
            ", definitionRo='" + getDefinitionRo() + "'" +
            ", titleRu='" + getTitleRu() + "'" +
            ", definitionRu='" + getDefinitionRu() + "'" +
            ", titleSr='" + getTitleSr() + "'" +
            ", definitionSr='" + getDefinitionSr() + "'" +
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
