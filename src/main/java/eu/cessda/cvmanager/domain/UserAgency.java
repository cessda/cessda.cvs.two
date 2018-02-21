package eu.cessda.cvmanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A UserAgency.
 */
@Entity
@Table(name = "user_agency")
public class UserAgency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "userAgency")
    @JsonIgnore
    private Set<LanguageRight> languageRights = new HashSet<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private Agency agency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<LanguageRight> getLanguageRights() {
        return languageRights;
    }

    public UserAgency languageRights(Set<LanguageRight> languageRights) {
        this.languageRights = languageRights;
        return this;
    }

    public UserAgency addLanguageRight(LanguageRight languageRight) {
        this.languageRights.add(languageRight);
        languageRight.setUserAgency(this);
        return this;
    }

    public UserAgency removeLanguageRight(LanguageRight languageRight) {
        this.languageRights.remove(languageRight);
        languageRight.setUserAgency(null);
        return this;
    }

    public void setLanguageRights(Set<LanguageRight> languageRights) {
        this.languageRights = languageRights;
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
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAgency userAgency = (UserAgency) o;
        if (userAgency.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userAgency.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserAgency{" +
            "id=" + getId() +
            "}";
    }
}
