package eu.cessda.cvmanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Role.
 */
@Entity
@Table(name = "role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<UserAgencyRole> userAgencyRoles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Role name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Role description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserAgencyRole> getUserAgencyRoles() {
        return userAgencyRoles;
    }

    public Role userAgencyRoles(Set<UserAgencyRole> userAgencyRoles) {
        this.userAgencyRoles = userAgencyRoles;
        return this;
    }

    public Role addUserAgencyRole(UserAgencyRole userAgencyRole) {
        this.userAgencyRoles.add(userAgencyRole);
        userAgencyRole.setRole(this);
        return this;
    }

    public Role removeUserAgencyRole(UserAgencyRole userAgencyRole) {
        this.userAgencyRoles.remove(userAgencyRole);
        userAgencyRole.setRole(null);
        return this;
    }

    public void setUserAgencyRoles(Set<UserAgencyRole> userAgencyRoles) {
        this.userAgencyRoles = userAgencyRoles;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        if (role.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Role{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
