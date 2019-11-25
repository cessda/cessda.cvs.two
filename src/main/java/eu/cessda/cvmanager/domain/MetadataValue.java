package eu.cessda.cvmanager.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import eu.cessda.cvmanager.domain.enumeration.ObjectType;

/**
 * A Metadata Value code.
 */
@Entity
@Table( name = "metadata_value" )
public class MetadataValue implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Lob
	@Column( name = "value" )
	private String value;

	@ManyToOne
	private MetadataField metadataField;

	@Enumerated( EnumType.STRING )
	@Column( name = "object_type" )
	private ObjectType objectType;

	@Column( name = "object_id" )
	private Long objectId;

	public Long getId()
	{
		return id;
	}

	public void setId( Long id )
	{
		this.id = id;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue( String value )
	{
		this.value = value;
	}

	public MetadataField getMetadataField()
	{
		return metadataField;
	}

	public void setMetadataField( MetadataField metadataField )
	{
		this.metadataField = metadataField;
	}

	public ObjectType getObjectType()
	{
		return objectType;
	}

	public void setObjectType( ObjectType objectType )
	{
		this.objectType = objectType;
	}

	public Long getObjectId()
	{
		return objectId;
	}

	public void setObjectId( Long objectId )
	{
		this.objectId = objectId;
	}
}
