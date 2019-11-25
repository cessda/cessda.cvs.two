package eu.cessda.cvmanager.domain;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

public class Cv
{

	// Optional for updating the Version entity
	private String uri;
	@NotNull // valid unique agency name
	private String agency;
	@NotNull // code of the CV in Camel-case
	private String code;
	@NotNull
	private String term;
	private String definition;
	@NotNull
	private String version;
	@NotNull
	private String type;
	@NotNull
	private String language;
	private String versionNotes;
	private String versionChanges;
	private LocalDate publicationDate;
	private CvCode[] cvCodes;

	public String getUri()
	{
		return uri;
	}

	public void setUri( String uri )
	{
		this.uri = uri;
	}

	public String getAgency()
	{
		return agency;
	}

	public void setAgency( String agency )
	{
		this.agency = agency;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode( String code )
	{
		this.code = code;
	}

	public String getTerm()
	{
		return term;
	}

	public void setTerm( String term )
	{
		this.term = term;
	}

	public String getDefinition()
	{
		return definition;
	}

	public void setDefinition( String definition )
	{
		this.definition = definition;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion( String version )
	{
		this.version = version;
	}

	public String getType()
	{
		return type;
	}

	public void setType( String type )
	{
		this.type = type;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage( String language )
	{
		this.language = language;
	}

	public String getVersionNotes()
	{
		return versionNotes;
	}

	public void setVersionNotes( String versionNotes )
	{
		this.versionNotes = versionNotes;
	}

	public String getVersionChanges()
	{
		return versionChanges;
	}

	public void setVersionChanges( String versionChanges )
	{
		this.versionChanges = versionChanges;
	}

	public LocalDate getPublicationDate()
	{
		return publicationDate;
	}

	public void setPublicationDate( LocalDate publicationDate )
	{
		this.publicationDate = publicationDate;
	}

	public CvCode[] getCvCodes()
	{
		return cvCodes;
	}

	public void setCvCodes( CvCode[] cvCodes )
	{
		this.cvCodes = cvCodes;
	}

}
