/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.domain;

import eu.cessda.cvs.domain.enumeration.ResolverType;
import eu.cessda.cvs.domain.enumeration.ResourceType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A Resolver.
 */
@Entity
@Table(name = "resolver")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
    @Column(name = "resource_url", nullable = false)
    private String resourceUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolver_type")
    private ResolverType resolverType;

    @NotNull
    @Column(name = "resolver_uri", nullable = false)
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

    public String getResourceUrl() {
        return resourceUrl;
    }

    public Resolver resourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        return this;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
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
        if (!(o instanceof Resolver)) {
            return false;
        }
        return id != null && id.equals(((Resolver) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Resolver{" +
            "id=" + getId() +
            ", resourceId='" + getResourceId() + "'" +
            ", resourceType='" + getResourceType() + "'" +
            ", resourceUrl='" + getResourceUrl() + "'" +
            ", resolverType='" + getResolverType() + "'" +
            ", resolverURI='" + getResolverURI() + "'" +
            "}";
    }
}
