package eu.cessda.cvs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Agency.
 */
@Entity
@Table(name = "agency")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Agency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 240)
    @Column(name = "name", length = 240)
    private String name;

    @Size(max = 255)
    @Column(name = "link", length = 255)
    private String link;

    @Lob
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "logopath", length = 255)
    private String logopath;

    @Size(max = 240)
    @Column(name = "license", length = 240)
    private String license;

    @Column(name = "license_id")
    private Long licenseId;

    @Size(max = 255)
    @Column(name = "uri", length = 255)
    private String uri;

    @Size(max = 255)
    @Column(name = "canonical_uri", length = 255)
    private String canonicalUri;

    @OneToMany(mappedBy = "agency")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<UserAgency> userAgencies = new HashSet<>();

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

    public Agency name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public Agency link(String link) {
        this.link = link;
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public Agency description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogopath() {
        return logopath;
    }

    public Agency logopath(String logopath) {
        this.logopath = logopath;
        return this;
    }

    public void setLogopath(String logopath) {
        this.logopath = logopath;
    }

    public String getLicense() {
        return license;
    }

    public Agency license(String license) {
        this.license = license;
        return this;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public Agency licenseId(Long licenseId) {
        this.licenseId = licenseId;
        return this;
    }

    public void setLicenseId(Long licenseId) {
        this.licenseId = licenseId;
    }

    public String getUri() {
        return uri;
    }

    public Agency uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCanonicalUri() {
        return canonicalUri;
    }

    public Agency canonicalUri(String canonicalUri) {
        this.canonicalUri = canonicalUri;
        return this;
    }

    public void setCanonicalUri(String canonicalUri) {
        this.canonicalUri = canonicalUri;
    }

    public Set<UserAgency> getUserAgencies() {
        return userAgencies;
    }

    public Agency userAgencies(Set<UserAgency> userAgencies) {
        this.userAgencies = userAgencies;
        return this;
    }

    public Agency addUserAgency(UserAgency userAgency) {
        this.userAgencies.add(userAgency);
        userAgency.setAgency(this);
        return this;
    }

    public Agency removeUserAgency(UserAgency userAgency) {
        this.userAgencies.remove(userAgency);
        userAgency.setAgency(null);
        return this;
    }

    public void setUserAgencies(Set<UserAgency> userAgencies) {
        this.userAgencies = userAgencies;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agency)) {
            return false;
        }
        return id != null && id.equals(((Agency) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Agency{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", link='" + getLink() + "'" +
            ", description='" + getDescription() + "'" +
            ", logopath='" + getLogopath() + "'" +
            ", license='" + getLicense() + "'" +
            ", licenseId=" + getLicenseId() +
            ", uri='" + getUri() + "'" +
            ", canonicalUri='" + getCanonicalUri() + "'" +
            "}";
    }
}
