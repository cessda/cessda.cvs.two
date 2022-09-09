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

package eu.cessda.cvs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Concept.
 */
@Entity
@Table(name = "concept")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Concept implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uri")
    private String uri;

    @NotNull
    @Size(max = 240)
    @Column(name = "notation", length = 240, nullable = false)
    private String notation;

    @Lob
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "definition")
    private String definition;

    @Column(name = "previous_concept")
    private Long previousConcept;

    @Column(name = "sl_concept")
    private Long slConcept;

    @Size(max = 240)
    @Column(name = "parent", length = 240)
    private String parent;

    @Column(name = "position")
    private Integer position;

    @NotNull
    @Column(nullable = false)
    private boolean deprecated = false;

    @Column(name = "replaced_by")
    private Long replacedBy;

    @ManyToOne
    @JsonIgnoreProperties("concepts")
    private Version version;

    @ManyToOne
    @JsonIgnoreProperties("concepts")
    private Version introducedInVersion;

    @ManyToOne
    @JsonIgnoreProperties("concepts")
    private Version validUntilVersion;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public Concept uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNotation() {
        return notation;
    }

    public Concept notation(String notation) {
        this.notation = notation;
        return this;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getTitle() {
        return title;
    }

    public Concept title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefinition() {
        return definition;
    }

    public Concept definition(String definition) {
        this.definition = definition;
        return this;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Long getPreviousConcept() {
        return previousConcept;
    }

    public Concept previousConcept(Long previousConcept) {
        this.previousConcept = previousConcept;
        return this;
    }

    public void setPreviousConcept(Long previousConcept) {
        this.previousConcept = previousConcept;
    }

    public Long getSlConcept() {
        return slConcept;
    }

    public Concept slConcept(Long slConcept) {
        this.slConcept = slConcept;
        return this;
    }

    public void setSlConcept(Long slConcept) {
        this.slConcept = slConcept;
    }

    public String getParent() {
        return parent;
    }

    public Concept parent(String parent) {
        this.parent = parent;
        return this;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public Concept position(Integer position) {
        this.position = position;
        return this;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean getDeprecated() {
        return this.deprecated;
    }

    public Concept deprecated(boolean deprecated) {
        this.deprecated = deprecated;
        return this;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Long getReplacedBy() {
        return this.replacedBy;
    }

    public Concept replacedBy(Long replacedBy) {
        this.replacedBy = replacedBy;
        return this;
    }

    public void setReplacedBy(Long replacedBy) {
        this.replacedBy = replacedBy;
    }

    public Version getVersion() {
        return version;
    }

    public Concept version(Version version) {
        this.version = version;
        return this;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Version getIntroducedInVersion() {
        return introducedInVersion;
    }

    public Concept introducedInVersion(Version introducedInVersion) {
        this.introducedInVersion = introducedInVersion;
        return this;
    }

    public void setIntroducedInVersion(Version introducedInVersion) {
        this.introducedInVersion = introducedInVersion;
    }

    public Version getValidUntilVersion() {
        return validUntilVersion;
    }

    public Concept validUntilVersion(Version validUntilVersion) {
        this.validUntilVersion = validUntilVersion;
        return this;
    }

    public void setValidUntilVersion(Version validUntilVersion) {
        this.validUntilVersion = validUntilVersion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Concept)) {
            return false;
        }
        return id != null && id.equals(((Concept) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Concept{" +
            "id=" + getId() +
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", title='" + getTitle() + "'" +
            ", definition='" + getDefinition() + "'" +
            ", previousConcept=" + getPreviousConcept() +
            ", slConcept=" + getSlConcept() +
            ", parent='" + getParent() + "'" +
            ", position=" + getPosition() +
            ", deprecated=" + getDeprecated() +
            ", replacedBy=" + getReplacedBy() +
            "}";
    }
}
