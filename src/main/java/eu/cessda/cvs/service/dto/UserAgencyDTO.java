/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.domain.UserAgency;
import eu.cessda.cvs.domain.enumeration.AgencyRole;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.UserAgency} entity.
 */
public class UserAgencyDTO implements Serializable {

    private Long id;

    private AgencyRole agencyRole;

    private String language;

    private Long userId;

    private Long agencyId;

    private String agencyName;

    public UserAgencyDTO(){
        // Empty constructor needed for Jackson
    }

    public UserAgencyDTO(UserAgency userAgency) {
        this.id = userAgency.getId();
        this.agencyRole = userAgency.getAgencyRole();
        this.language = userAgency.getLanguage();
        this.agencyId = userAgency.getAgency().getId();
        this.agencyName = userAgency.getAgency().getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgencyRole getAgencyRole() {
        return agencyRole;
    }

    public void setAgencyRole(AgencyRole agencyRole) {
        this.agencyRole = agencyRole;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserAgencyDTO userAgencyDTO = (UserAgencyDTO) o;
        if (userAgencyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userAgencyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserAgencyDTO{" +
            "id=" + getId() +
            ", agencyRole='" + getAgencyRole() + "'" +
            ", language='" + getLanguage() + "'" +
            ", agencyId=" + getAgencyId() +
            "}";
    }
}
