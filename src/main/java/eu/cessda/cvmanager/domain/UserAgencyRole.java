package eu.cessda.cvmanager.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A UserAgencyRole.
 */
@Entity
@Table(name = "user_agency_role")
public class UserAgencyRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;

    @ManyToOne
    private UserAgency userAgency;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public UserAgencyRole user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public UserAgencyRole role(Role role) {
        this.role = role;
        return this;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserAgency getUserAgency() {
        return userAgency;
    }

    public UserAgencyRole userAgency(UserAgency userAgency) {
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
        UserAgencyRole userAgencyRole = (UserAgencyRole) o;
        if (userAgencyRole.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userAgencyRole.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserAgencyRole{" +
            "id=" + getId() +
            "}";
    }
}
