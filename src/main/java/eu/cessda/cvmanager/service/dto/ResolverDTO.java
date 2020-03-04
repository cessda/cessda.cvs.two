package eu.cessda.cvmanager.service.dto;


import eu.cessda.cvmanager.domain.enumeration.ResolverType;
import eu.cessda.cvmanager.domain.enumeration.ResourceType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Resolver entity.
 */
public class ResolverDTO implements Serializable
{

    private static final long serialVersionUID = 9188921541673634043L;

    private Long id;

    private String resourceId;

    private ResourceType resourceType;

    @NotNull
    private String resourceURL;

    private ResolverType resolverType;

    @NotNull
    private String resolverURI;
    
    public static ResolverDTO createUrnResolver() {
    	return new ResolverDTO()
    		.withResourceType( ResourceType.VOCABULARY )
    		.withResolverType( ResolverType.URN );
    }
    
    public ResolverDTO withResourceId( String resourceId ) {
    	this.resourceId = resourceId;
    	return this;
    }
    
    public ResolverDTO withResourceType( ResourceType resourceType ) {
    	this.resourceType = resourceType;
    	return this;
    }
    
    public ResolverDTO withResourceURL( String resourceUrl ) {
    	this.resourceURL = resourceUrl;
    	return this;
    }
    
    public ResolverDTO withResolverType( ResolverType resolverType ) {
    	this.resolverType = resolverType;
    	return this;
    }
    
    public ResolverDTO withResolverURI( String resolverUri ) {
    	this.resolverURI = resolverUri;
    	return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    public ResolverType getResolverType() {
        return resolverType;
    }

    public void setResolverType(ResolverType resolverType) {
        this.resolverType = resolverType;
    }

    public String getResolverURI() {
        return resolverURI;
    }

    public void setResolverURI(String resolverURI) {
        this.resolverURI = resolverURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResolverDTO resolverDTO = (ResolverDTO) o;
        if(resolverDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resolverDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ResolverDTO{" +
            "id=" + getId() +
            ", resourceId=" + getResourceId() +
            ", resourceType='" + getResourceType() + "'" +
            ", resourceURL='" + getResourceURL() + "'" +
            ", resolverType='" + getResolverType() + "'" +
            ", resolverURI='" + getResolverURI() + "'" +
            "}";
    }

	public boolean isPersisted() {
        return getId() != null;
	}
}
