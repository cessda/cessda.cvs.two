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
package eu.cessda.cvs.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.cessda.cvs.domain.Agency;

import javax.persistence.Lob;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Agency} entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgencyDTO implements Serializable {

    private Long id;

    @Size(max = 240)
    private String name;

    @Lob
    private String description;

    @Size(max = 255)
    private String link;

    @Size(max = 255)
    private String logopath;

    @Lob
    private String copyright;

    @Size(max = 240)
    private String license;

    private Long licenseId;

    @Size(max = 255)
    private String uri;

    @Size(max = 255)
    private String uriCode;

    @Size(max = 255)
    private String canonicalUri;

    @Lob
    private String logo;

    private Boolean deletable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLogopath() {
        return logopath;
    }

    public void setLogopath(String logopath) {
        this.logopath = logopath;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Long licenseId) {
        this.licenseId = licenseId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCanonicalUri() {
        return canonicalUri;
    }

    public void setCanonicalUri(String canonicalUri) {
        this.canonicalUri = canonicalUri;
    }

    public String getUriCode() {
        return uriCode;
    }

    public void setUriCode(String uriCode) {
        this.uriCode = uriCode;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AgencyDTO agencyDTO = (AgencyDTO) o;
        if (agencyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), agencyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AgencyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", link='" + getLink() + "'" +
            ", description='" + getDescription() + "'" +
            ", logopath='" + getLogopath() + "'" +
            ", copyright='" + getCopyright() + "'" +
            ", license='" + getLicense() + "'" +
            ", licenseId=" + getLicenseId() +
            ", uri='" + getUri() + "'" +
            ", uriCode='" + getUriCode() + "'" +
            ", canonicalUri='" + getCanonicalUri() + "'" +
            ", logo='" + getLogo() + "'" +
            "}";
    }
}
