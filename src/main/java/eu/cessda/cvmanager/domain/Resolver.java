package eu.cessda.cvmanager.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

import eu.cessda.cvmanager.domain.enumeration.ResourceType;

import eu.cessda.cvmanager.domain.enumeration.ResolverType;

/**
 * A Resolver.
 */
@Entity
@Table(name = "resolver")
public class Resolver implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id")
    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;

    @NotNull
    @Column(name = "resource_url", nullable = false, unique = true)
    private String resourceURL;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolver_type")
    private ResolverType resolverType;

    @NotNull
    @Column(name = "resolver_uri", nullable = false, unique = true)
    private String resolverURI;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Resolver resourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Resolver resourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public Resolver resourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
        return this;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    public ResolverType getResolverType() {
        return resolverType;
    }

    public Resolver resolverType(ResolverType resolverType) {
        this.resolverType = resolverType;
        return this;
    }

    public void setResolverType(ResolverType resolverType) {
        this.resolverType = resolverType;
    }

    public String getResolverURI() {
        return resolverURI;
    }

    public Resolver resolverURI(String resolverURI) {
        this.resolverURI = resolverURI;
        return this;
    }

    public void setResolverURI(String resolverURI) {
        this.resolverURI = resolverURI;
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
        Resolver resolver = (Resolver) o;
        if (resolver.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resolver.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Resolver{" +
            "id=" + getId() +
            ", resourceId=" + getResourceId() +
            ", resourceType='" + getResourceType() + "'" +
            ", resourceURL='" + getResourceURL() + "'" +
            ", resolverType='" + getResolverType() + "'" +
            ", resolverURI='" + getResolverURI() + "'" +
            "}";
    }
}
