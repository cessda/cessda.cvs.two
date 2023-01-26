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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Licence.
 */
@Entity
@Table( name = "license" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Licence implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "link", length = 255)
    private String link;

    @Size(max = 255)
    @Column(name = "logo_link", length = 255)
    private String logoLink;

    @Size(max = 255)
    @Column(name = "abbr", length = 255)
    private String abbr;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Licence name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public Licence link(String link) {
        this.link = link;
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public Licence logoLink(String logoLink) {
        this.logoLink = logoLink;
        return this;
    }

    public void setLogoLink(String logoLink) {
        this.logoLink = logoLink;
    }

    public String getAbbr() {
        return abbr;
    }

    public Licence abbr(String abbr) {
        this.abbr = abbr;
        return this;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Licence)) {
            return false;
        }
        return id != null && id.equals(((Licence) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Licence{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", link='" + getLink() + "'" +
            ", logoLink='" + getLogoLink() + "'" +
            ", abbr='" + getAbbr() + "'" +
            "}";
    }
}
