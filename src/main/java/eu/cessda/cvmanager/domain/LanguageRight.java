package eu.cessda.cvmanager.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

import eu.cessda.cvmanager.domain.enumeration.LanguageType;

import eu.cessda.cvmanager.domain.enumeration.Language;

/**
 * A LanguageRight.
 */
@Entity
@Table(name = "language_right")
public class LanguageRight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_type")
    private LanguageType languageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @ManyToOne
    private UserAgency userAgency;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LanguageType getLanguageType() {
        return languageType;
    }

    public LanguageRight languageType(LanguageType languageType) {
        this.languageType = languageType;
        return this;
    }

    public void setLanguageType(LanguageType languageType) {
        this.languageType = languageType;
    }

    public Language getLanguage() {
        return language;
    }

    public LanguageRight language(Language language) {
        this.language = language;
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public UserAgency getUserAgency() {
        return userAgency;
    }

    public LanguageRight userAgency(UserAgency userAgency) {
        this.userAgency = userAgency;
        return this;
    }

    public void setUserAgency(UserAgency userAgency) {
        this.userAgency = userAgency;
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
        LanguageRight languageRight = (LanguageRight) o;
        if (languageRight.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), languageRight.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LanguageRight{" +
            "id=" + getId() +
            ", languageType='" + getLanguageType() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
