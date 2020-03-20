package eu.cessda.cvmanager.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Code.
 */
@Entity
@Table( name = "code" )
// @Document(indexName = "codes")
public class Code implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@NotNull
	@Column( name = "uri", length = 240 )
	private String uri;

	@NotNull
	@Column( name = "notation", length = 240, nullable = false )
	private String notation;

	@Column( name = "archived" )
	private Boolean archived;

	@Column( name = "withdrawn" )
	private Boolean withdrawn;

	@Column( name = "discoverable" )
	private Boolean discoverable;

	@Column( name = "languages" )
	@ElementCollection( targetClass = String.class )
	@Field( type = FieldType.Keyword )
	private Set<String> languages;

	@NotNull
	@Size( max = 20 )
	@Column( name = "source_language", length = 20, nullable = false )
	@Field( type = FieldType.Keyword )
	private String sourceLanguage;

	// if null, then it is top concept
	@Column( name = "parent", length = 240 )
	private String parent;

	@Column( name = "\"position\"" )
	private Integer position;

	@Column( name = "publication_date" )
	@Field( type = FieldType.Date, format = DateFormat.date )
	private LocalDate publicationDate;

	@Column( name = "last_modified" )
	@Field( type = FieldType.Date, format = DateFormat.date_hour_minute_second )
	private LocalDateTime lastModified;

	@Lob
	@Column( name = "title_cs" )
	@Field( type = FieldType.Text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
	private String titleCs;

	@Lob
	@Column( name = "definition_cs" )
	@Field( type = FieldType.Text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
	private String definitionCs;

	@Lob
	@Column( name = "title_da" )
	@Field( type = FieldType.Text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
	private String titleDa;

	@Lob
	@Column( name = "definition_da" )
	@Field( type = FieldType.Text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
	private String definitionDa;

	@Lob
	@Column( name = "title_nl" )
	@Field( type = FieldType.Text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
	private String titleNl;

	@Lob
	@Column( name = "definition_nl" )
	@Field( type = FieldType.Text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
	private String definitionNl;

	@Lob
	@Column( name = "title_en" )
	@Field( type = FieldType.Text, store = true, analyzer = "english", searchAnalyzer = "english" )
	private String titleEn;

	@Lob
	@Column( name = "definition_en" )
	@Field( type = FieldType.Text, store = true, analyzer = "english", searchAnalyzer = "english" )
	private String definitionEn;

	@Lob
	@Column(name = "title_et")
	@Field( type = FieldType.Text, store = true )
	private String titleEt;

	@Lob
	@Column(name = "definition_et")
	@Field( type = FieldType.Text, store = true )
	private String definitionEt;

	@Lob
	@Column( name = "title_fi" )
	@Field( type = FieldType.Text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
	private String titleFi;

	@Lob
	@Column( name = "definition_fi" )
	@Field( type = FieldType.Text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
	private String definitionFi;

	@Lob
	@Column( name = "title_fr" )
	@Field( type = FieldType.Text, store = true, analyzer = "french", searchAnalyzer = "french" )
	private String titleFr;

	@Lob
	@Column( name = "definition_fr" )
	@Field( type = FieldType.Text, store = true, analyzer = "french", searchAnalyzer = "french" )
	private String definitionFr;

	@Lob
	@Column( name = "title_de" )
	@Field( type = FieldType.Text, store = true, analyzer = "german", searchAnalyzer = "german" )
	private String titleDe;

	@Lob
	@Column( name = "definition_de" )
	@Field( type = FieldType.Text, store = true, analyzer = "german", searchAnalyzer = "german" )
	private String definitionDe;

	@Lob
	@Column( name = "title_el" )
	@Field( type = FieldType.Text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
	private String titleEl;

	@Lob
	@Column( name = "definition_el" )
	@Field( type = FieldType.Text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
	private String definitionEl;

	@Lob
	@Column( name = "title_hu" )
	@Field( type = FieldType.Text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
	private String titleHu;

	@Lob
	@Column( name = "definition_hu" )
	@Field( type = FieldType.Text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
	private String definitionHu;

	@Lob
	@Column( name = "title_it" )
	@Field( type = FieldType.Text, store = true, analyzer = "italian", searchAnalyzer = "italian" )
	private String titleIt;

	@Lob
	@Column( name = "definition_it" )
	@Field( type = FieldType.Text, store = true, analyzer = "italian", searchAnalyzer = "italian" )
	private String definitionIt;

	@Lob
	@Column(name = "title_ja")
	@Field( type = FieldType.Text, store = true )
	private String titleJa;

	@Lob
	@Column(name = "definition_ja")
	@Field( type = FieldType.Text, store = true )
	private String definitionJa;

	@Lob
	@Column( name = "title_lt" )
	@Field( type = FieldType.Text, store = true )
	private String titleLt;

	@Lob
	@Column( name = "definition_lt" )
	@Field( type = FieldType.Text, store = true )
	private String definitionLt;

	@Lob
	@Column( name = "title_no" )
	@Field( type = FieldType.Text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
	private String titleNo;

	@Lob
	@Column( name = "definition_no" )
	@Field( type = FieldType.Text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
	private String definitionNo;

	@Lob
	@Column( name = "title_pt" )
	@Field( type = FieldType.Text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
	private String titlePt;

	@Lob
	@Column( name = "definition_pt" )
	@Field( type = FieldType.Text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
	private String definitionPt;

	@Lob
	@Column( name = "title_ro" )
	@Field( type = FieldType.Text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
	private String titleRo;

	@Lob
	@Column( name = "definition_ro" )
	@Field( type = FieldType.Text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
	private String definitionRo;

	@Lob
	@Column( name = "title_sk" )
	@Field( type = FieldType.Text, store = true )
	private String titleSk;

	@Lob
	@Column( name = "definition_sk" )
	@Field( type = FieldType.Text, store = true )
	private String definitionSk;

	@Lob
	@Column( name = "title_sl" )
	@Field( type = FieldType.Text, store = true )
	private String titleSl;

	@Lob
	@Column( name = "definition_sl" )
	@Field( type = FieldType.Text, store = true )
	private String definitionSl;

	@Lob
	@Column( name = "title_es" )
	@Field( type = FieldType.Text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
	private String titleEs;

	@Lob
	@Column( name = "definition_es" )
	@Field( type = FieldType.Text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
	private String definitionEs;

	@Lob
	@Column( name = "title_sv" )
	@Field( type = FieldType.Text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
	private String titleSv;

	@Lob
	@Column( name = "definition_sv" )
	@Field( type = FieldType.Text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
	private String definitionSv;

	// additional languages

	@Lob
	@Column( name = "title_sq" )
	@Field( type = FieldType.Text, store = true )
	private String titleSq;

	@Lob
	@Column( name = "definition_sq" )
	@Field( type = FieldType.Text, store = true )
	private String definitionSq;

	public String getTitleSq()
	{
		return titleSq;
	}

	public void setTitleSq( String titleSq )
	{
		this.titleSq = titleSq;
	}

	public String getDefinitionSq()
	{
		return definitionSq;
	}

	public void setDefinitionSq( String definitionSq )
	{
		this.definitionSq = definitionSq;
	}

	@Lob
	@Column( name = "title_bs" )
	@Field( type = FieldType.Text, store = true )
	private String titleBs;

	@Lob
	@Column( name = "definition_bs" )
	@Field( type = FieldType.Text, store = true )
	private String definitionBs;

	public String getTitleBs()
	{
		return titleBs;
	}

	public void setTitleBs( String titleBs )
	{
		this.titleBs = titleBs;
	}

	public String getDefinitionBs()
	{
		return definitionBs;
	}

	public void setDefinitionBs( String definitionBs )
	{
		this.definitionBs = definitionBs;
	}

	@Lob
	@Column( name = "title_bg" )
	@Field( type = FieldType.Text, store = true, analyzer = "bulgarian", searchAnalyzer = "bulgarian" )
	private String titleBg;

	@Lob
	@Column( name = "definition_bg" )
	@Field( type = FieldType.Text, store = true, analyzer = "bulgarian", searchAnalyzer = "bulgarian" )
	private String definitionBg;

	public String getTitleBg()
	{
		return titleBg;
	}

	public void setTitleBg( String titleBg )
	{
		this.titleBg = titleBg;
	}

	public String getDefinitionBg()
	{
		return definitionBg;
	}

	public void setDefinitionBg( String definitionBg )
	{
		this.definitionBg = definitionBg;
	}

	@Lob
	@Column( name = "title_hr" )
	@Field( type = FieldType.Text, store = true )
	private String titleHr;

	@Lob
	@Column( name = "definition_hr" )
	@Field( type = FieldType.Text, store = true )
	private String definitionHr;

	public String getTitleHr()
	{
		return titleHr;
	}

	public void setTitleHr( String titleHr )
	{
		this.titleHr = titleHr;
	}

	public String getDefinitionHr()
	{
		return definitionHr;
	}

	public void setDefinitionHr( String definitionHr )
	{
		this.definitionHr = definitionHr;
	}

	@Lob
	@Column( name = "title_mk" )
	@Field( type = FieldType.Text, store = true )
	private String titleMk;

	@Lob
	@Column( name = "definition_mk" )
	@Field( type = FieldType.Text, store = true )
	private String definitionMk;

	public String getTitleMk()
	{
		return titleMk;
	}

	public void setTitleMk( String titleMk )
	{
		this.titleMk = titleMk;
	}

	public String getDefinitionMk()
	{
		return definitionMk;
	}

	public void setDefinitionMk( String definitionMk )
	{
		this.definitionMk = definitionMk;
	}

	@Lob
	@Column( name = "title_pl" )
	@Field( type = FieldType.Text, store = true )
	private String titlePl;

	@Lob
	@Column( name = "definition_pl" )
	@Field( type = FieldType.Text, store = true )
	private String definitionPl;

	public String getTitlePl()
	{
		return titlePl;
	}

	public void setTitlePl( String titlePl )
	{
		this.titlePl = titlePl;
	}

	public String getDefinitionPl()
	{
		return definitionPl;
	}

	public void setDefinitionPl( String definitionPl )
	{
		this.definitionPl = definitionPl;
	}

	@Lob
	@Column( name = "title_sr" )
	@Field( type = FieldType.Text, store = true )
	private String titleSr;

	@Lob
	@Column( name = "definition_sr" )
	@Field( type = FieldType.Text, store = true )
	private String definitionSr;

	public String getTitleSr()
	{
		return titleSr;
	}

	public void setTitleSr( String titleSr )
	{
		this.titleSr = titleSr;
	}

	public String getDefinitionSr()
	{
		return definitionSr;
	}

	public void setDefinitionSr( String definitionSr )
	{
		this.definitionSr = definitionSr;
	}

	@Lob
	@Column( name = "title_ru" )
	@Field( type = FieldType.Text, store = true, analyzer = "russian", searchAnalyzer = "russian" )
	private String titleRu;

	@Lob
	@Column( name = "definition_ru" )
	@Field( type = FieldType.Text, store = true, analyzer = "russian", searchAnalyzer = "russian" )
	private String definitionRu;

	public String getTitleRu()
	{
		return titleRu;
	}

	public void setTitleRu( String titleRu )
	{
		this.titleRu = titleRu;
	}

	public String getDefinitionRu()
	{
		return definitionRu;
	}

	public void setDefinitionRu( String definitionRu )
	{
		this.definitionRu = definitionRu;
	}

	@Column( name = "vocabulary_id" )
	private Long vocabularyId;

	@Column( name = "version_id" )
	private Long versionId;

	@Column( name = "version_number" )
	private String versionNumber;

	public Long getId()
	{
		return id;
	}

	public void setId( Long id )
	{
		this.id = id;
	}

	public String getUri()
	{
		return uri;
	}

	public Code uri( String uri )
	{
		this.uri = uri;
		return this;
	}

	public void setUri( String uri )
	{
		this.uri = uri;
	}

	public String getNotation()
	{
		return notation;
	}

	public void setNotation( String notation )
	{
		this.notation = notation;
	}

	public Boolean isArchived()
	{
		return archived;
	}

	public Code archived( Boolean archived )
	{
		this.archived = archived;
		return this;
	}

	public void setArchived( Boolean archived )
	{
		this.archived = archived;
	}

	public Boolean isWithdrawn()
	{
		return withdrawn;
	}

	public Code withdrawn( Boolean withdrawn )
	{
		this.withdrawn = withdrawn;
		return this;
	}

	public void setWithdrawn( Boolean withdrawn )
	{
		this.withdrawn = withdrawn;
	}

	public Boolean isDiscoverable()
	{
		return discoverable;
	}

	public Code discoverable( Boolean discoverable )
	{
		this.discoverable = discoverable;
		return this;
	}

	public void setDiscoverable( Boolean discoverable )
	{
		this.discoverable = discoverable;
	}

	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	public Code sourceLanguage( String sourceLanguage )
	{
		this.sourceLanguage = sourceLanguage;
		return this;
	}

	public void setSourceLanguage( String sourceLanguage )
	{
		this.sourceLanguage = sourceLanguage;
	}

	public Set<String> getLanguages()
	{
		return languages;
	}

	public void setLanguages( Set<String> languages )
	{
		this.languages = languages;
	}

	public Code addLanguage( String language )
	{
		this.languages.add( language );
		return this;
	}

	public String getParent()
	{
		return parent;
	}

	public void setParent( String parent )
	{
		this.parent = parent;
	}

	public Integer getPosition()
	{
		return position;
	}

	public void setPosition( Integer position )
	{
		this.position = position;
	}

	public String getTitleCs()
	{
		return titleCs;
	}

	public Code titleCs( String titleCs )
	{
		this.titleCs = titleCs;
		return this;
	}

	public void setTitleCs( String titleCs )
	{
		this.titleCs = titleCs;
	}

	public String getDefinitionCs()
	{
		return definitionCs;
	}

	public Code definitionCs( String definitionCs )
	{
		this.definitionCs = definitionCs;
		return this;
	}

	public void setDefinitionCs( String definitionCs )
	{
		this.definitionCs = definitionCs;
	}

	public String getTitleDa()
	{
		return titleDa;
	}

	public void setTitleDa( String titleDa )
	{
		this.titleDa = titleDa;
	}

	public String getDefinitionDa()
	{
		return definitionDa;
	}

	public void setDefinitionDa( String definitionDa )
	{
		this.definitionDa = definitionDa;
	}

	public String getTitleNl()
	{
		return titleNl;
	}

	public void setTitleNl( String titleNl )
	{
		this.titleNl = titleNl;
	}

	public String getDefinitionNl()
	{
		return definitionNl;
	}

	public void setDefinitionNl( String definitionNl )
	{
		this.definitionNl = definitionNl;
	}

	public String getTitleEn()
	{
		return titleEn;
	}

	public void setTitleEn( String titleEn )
	{
		this.titleEn = titleEn;
	}

	public String getDefinitionEn()
	{
		return definitionEn;
	}

	public void setDefinitionEn( String definitionEn )
	{
		this.definitionEn = definitionEn;
	}

	public String getTitleEt() {
		return titleEt;
	}

	public void setTitleEt(String titleEt) {
		this.titleEt = titleEt;
	}

	public String getTitleFi()
	{
		return titleFi;
	}

	public void setTitleFi( String titleFi )
	{
		this.titleFi = titleFi;
	}

	public String getDefinitionFi()
	{
		return definitionFi;
	}

	public void setDefinitionFi( String definitionFi )
	{
		this.definitionFi = definitionFi;
	}

	public String getTitleFr()
	{
		return titleFr;
	}

	public void setTitleFr( String titleFr )
	{
		this.titleFr = titleFr;
	}

	public String getDefinitionFr()
	{
		return definitionFr;
	}

	public void setDefinitionFr( String definitionFr )
	{
		this.definitionFr = definitionFr;
	}

	public String getTitleDe()
	{
		return titleDe;
	}

	public void setTitleDe( String titleDe )
	{
		this.titleDe = titleDe;
	}

	public String getDefinitionDe()
	{
		return definitionDe;
	}

	public void setDefinitionDe( String definitionDe )
	{
		this.definitionDe = definitionDe;
	}

	public String getTitleEl()
	{
		return titleEl;
	}

	public void setTitleEl( String titleEl )
	{
		this.titleEl = titleEl;
	}

	public String getDefinitionEl()
	{
		return definitionEl;
	}

	public void setDefinitionEl( String definitionEl )
	{
		this.definitionEl = definitionEl;
	}

	public String getTitleHu()
	{
		return titleHu;
	}

	public void setTitleHu( String titleHu )
	{
		this.titleHu = titleHu;
	}

	public String getDefinitionHu()
	{
		return definitionHu;
	}

	public void setDefinitionHu( String definitionHu )
	{
		this.definitionHu = definitionHu;
	}

	public String getTitleIt()
	{
		return titleIt;
	}

	public void setTitleIt( String titleIt )
	{
		this.titleIt = titleIt;
	}

	public String getDefinitionIt()
	{
		return definitionIt;
	}

	public void setDefinitionIt( String definitionIt )
	{
		this.definitionIt = definitionIt;
	}

	public String getTitleJa() {
		return titleJa;
	}

	public void setTitleJa(String titleJa) {
		this.titleJa = titleJa;
	}

	public String getTitleLt()
	{
		return titleLt;
	}

	public void setTitleLt( String titleLt )
	{
		this.titleLt = titleLt;
	}

	public String getDefinitionLt()
	{
		return definitionLt;
	}

	public void setDefinitionLt( String definitionLt )
	{
		this.definitionLt = definitionLt;
	}

	public String getTitleNo()
	{
		return titleNo;
	}

	public void setTitleNo( String titleNo )
	{
		this.titleNo = titleNo;
	}

	public String getDefinitionNo()
	{
		return definitionNo;
	}

	public void setDefinitionNo( String definitionNo )
	{
		this.definitionNo = definitionNo;
	}

	public String getTitlePt()
	{
		return titlePt;
	}

	public void setTitlePt( String titlePt )
	{
		this.titlePt = titlePt;
	}

	public String getDefinitionPt()
	{
		return definitionPt;
	}

	public void setDefinitionPt( String definitionPt )
	{
		this.definitionPt = definitionPt;
	}

	public String getTitleRo()
	{
		return titleRo;
	}

	public void setTitleRo( String titleRo )
	{
		this.titleRo = titleRo;
	}

	public String getDefinitionRo()
	{
		return definitionRo;
	}

	public void setDefinitionRo( String definitionRo )
	{
		this.definitionRo = definitionRo;
	}

	public String getTitleSk()
	{
		return titleSk;
	}

	public void setTitleSk( String titleSk )
	{
		this.titleSk = titleSk;
	}

	public String getDefinitionSk()
	{
		return definitionSk;
	}

	public void setDefinitionSk( String definitionSk )
	{
		this.definitionSk = definitionSk;
	}

	public String getTitleSl()
	{
		return titleSl;
	}

	public Code titleSl( String titleSl )
	{
		this.titleSl = titleSl;
		return this;
	}

	public void setTitleSl( String titleSl )
	{
		this.titleSl = titleSl;
	}

	public String getDefinitionSl()
	{
		return definitionSl;
	}

	public void setDefinitionSl( String definitionSl )
	{
		this.definitionSl = definitionSl;
	}

	public String getTitleEs()
	{
		return titleEs;
	}

	public Code titleEs( String titleEs )
	{
		this.titleEs = titleEs;
		return this;
	}

	public void setTitleEs( String titleEs )
	{
		this.titleEs = titleEs;
	}

	public String getDefinitionEs()
	{
		return definitionEs;
	}

	public void setDefinitionEs( String definitionEs )
	{
		this.definitionEs = definitionEs;
	}

	public String getTitleSv()
	{
		return titleSv;
	}

	public void setTitleSv( String titleSv )
	{
		this.titleSv = titleSv;
	}

	public String getDefinitionSv()
	{
		return definitionSv;
	}

	public void setDefinitionSv( String definitionSv )
	{
		this.definitionSv = definitionSv;
	}

	public Long getVocabularyId()
	{
		return vocabularyId;
	}

	public void setVocabularyId( Long vocabularyId )
	{
		this.vocabularyId = vocabularyId;
	}

	public LocalDate getPublicationDate()
	{
		return publicationDate;
	}

	public void setPublicationDate( LocalDate publicationDate )
	{
		this.publicationDate = publicationDate;
	}

	public LocalDateTime getLastModified()
	{
		return lastModified;
	}

	public void setLastModified( LocalDateTime lastModified )
	{
		this.lastModified = lastModified;
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
		Code code = (Code) o;
		if ( code.getId() == null || getId() == null )
		{
			return false;
		}
		return Objects.equals( getId(), code.getId() );
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode( getId() );
	}

	@Override
	public String toString()
	{
		return "Vocabulary{" +
				"id=" + getId() +
				", uri='" + getUri() + "'" +
				", archived='" + isArchived() + "'" +
				", withdrawn='" + isWithdrawn() + "'" +
				", discoverable='" + isDiscoverable() + "'" +
				", sourceLanguage='" + getSourceLanguage() + "'" +
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
				", titleIt='" + getTitleIt() + "'" +
				", definitionIt='" + getDefinitionIt() + "'" +
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
				", titleSq='" + getTitleSq() + "'" +
				", definitionSq='" + getDefinitionSq() + "'" +
				", titleBs='" + getTitleBs() + "'" +
				", definitionBs='" + getDefinitionBs() + "'" +
				", titleBg='" + getTitleBg() + "'" +
				", definitionBg='" + getDefinitionBg() + "'" +
				", titleHr='" + getTitleHr() + "'" +
				", definitionHr='" + getDefinitionHr() + "'" +
				", titleMk='" + getTitleMk() + "'" +
				", definitionMk='" + getDefinitionMk() + "'" +
				", titlePl='" + getTitlePl() + "'" +
				", definitionPl='" + getDefinitionPl() + "'" +
				", titleRu='" + getTitleRu() + "'" +
				", definitionRu='" + getDefinitionRu() + "'" +
				", titleSr='" + getTitleSr() + "'" +
				", definitionSr='" + getDefinitionSr() + "'" +
				"}";
	}
}
