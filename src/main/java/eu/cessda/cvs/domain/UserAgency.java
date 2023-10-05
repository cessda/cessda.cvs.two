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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eu.cessda.cvs.domain.enumeration.AgencyRole;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A UserAgency.
 */
@Entity
@Table(name = "user_agency")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserAgency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "agency_role")
    private AgencyRole agencyRole;

    @Column(name = "language")
    private String language;

    @ManyToOne
    @JsonIgnoreProperties("userAgencies")
    private User user;

    @ManyToOne
    @JsonIgnoreProperties("userAgencies")
    private Agency agency;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgencyRole getAgencyRole() {
        return agencyRole;
    }

    public UserAgency agencyRole(AgencyRole agencyRole) {
        this.agencyRole = agencyRole;
        return this;
    }

    public void setAgencyRole(AgencyRole agencyRole) {
        this.agencyRole = agencyRole;
    }

    public String getLanguage() {
        return language;
    }

    public UserAgency language(String language) {
        this.language = language;
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public User getUser() {
        return user;
    }

    public UserAgency user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Agency getAgency() {
        return agency;
    }

    public UserAgency agency(Agency agency) {
        this.agency = agency;
        return this;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public void setAgencyName(String agencyName) {
        this.agency.setName(agencyName);
    }
    
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAgency)) {
            return false;
        }
        return id != null && id.equals(((UserAgency) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UserAgency{" +
            "id=" + getId() +
            ", agencyRole='" + getAgencyRole() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
