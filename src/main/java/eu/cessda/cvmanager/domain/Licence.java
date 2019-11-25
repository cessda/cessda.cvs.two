package eu.cessda.cvmanager.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A LicenceDTO.
 */
@Entity
@Table( name = "license" )
public class Licence implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@NotNull
	@Size( max = 255 )
	@Column( name = "name", length = 255, nullable = false )
	private String name;

	@Size( max = 255 )
	@Column( name = "link", length = 255 )
	private String link;

	@Size( max = 255 )
	@Column( name = "logo_link", length = 255 )
	private String logoLink;

	public Long getId()
	{
		return id;
	}

	public void setId( Long id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink( String link )
	{
		this.link = link;
	}

	public String getLogoLink()
	{
		return logoLink;
	}

	public void setLogoLink( String logoLink )
	{
		this.logoLink = logoLink;
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
		Licence licanse = (Licence) o;
		if ( licanse.getId() == null || getId() == null )
		{
			return false;
		}
		return Objects.equals( getId(), licanse.getId() );
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode( getId() );
	}

	@Override
	public String toString()
	{
		return "ActivityLog{" +
				"id=" + getId() +
				", name='" + getName() + "'" +
				", link='" + getLink() + "'" +
				", logoLink=" + getLogoLink() +
				"}";
	}
}
