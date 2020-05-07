package eu.cessda.cvs.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import eu.cessda.cvs.domain.enumeration.ResourceType;
import eu.cessda.cvs.domain.enumeration.ResolverType;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.Resolver} entity.
 */
public class ResolverDTO implements Serializable {
    
    private Long id;

    private String resourceId;

    private ResourceType resourceType;

    @NotNull
    private String resourceUrl;

    private ResolverType resolverType;

    @NotNull
    private String resolverURI;

    
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

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
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
        if (resolverDTO.getId() == null || getId() == null) {
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
            ", resourceId='" + getResourceId() + "'" +
            ", resourceType='" + getResourceType() + "'" +
            ", resourceUrl='" + getResourceUrl() + "'" +
            ", resolverType='" + getResolverType() + "'" +
            ", resolverURI='" + getResolverURI() + "'" +
            "}";
    }
}
