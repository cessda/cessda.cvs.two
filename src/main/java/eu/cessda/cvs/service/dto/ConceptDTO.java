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

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.cessda.cvs.domain.CodeSnippet;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.Concept} entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConceptDTO implements Serializable {


    private Long id;

    private String uri;

    @NotNull
    @Size(max = 240)
    private String notation;

    @Lob
    private String title;

    @Lob
    private String definition;

    private Long previousConcept;

    private Long slConcept;

    @Size(max = 240)
    private String parent;

    private Long versionId;

    private Long introducedInVersionId;
    private Long validUntilVersionId;

    private Integer position;

    private boolean deprecated;

    private Long replacedById;
    private String replacedByUri;
    private String replacedByNotation;

    private LocalDate validFrom;
    private LocalDate validUntil;

    public ConceptDTO(){
        this.notation = "NEW_NOTATION";
    }

    /**
     * Constructor to generate new concept, defined by Rest API
     * @param codeSnippet the partial information of concept
     */
    public ConceptDTO(CodeSnippet codeSnippet) {
        this.parent = codeSnippet.getParent();
        this.notation = codeSnippet.getNotation();
        this.title = codeSnippet.getTitle();
        this.definition = codeSnippet.getDefinition();
        this.position = codeSnippet.getPosition();
        this.deprecated = codeSnippet.getDeprecated();
        this.replacedById = codeSnippet.getReplacedById();
        this.versionId = codeSnippet.getVersionId();
        this.introducedInVersionId = codeSnippet.getIntroducedInVersionId();
        this.validUntilVersionId = codeSnippet.getValidUntilVersionId();
        this.validFrom = codeSnippet.getValidFrom();
        this.validUntil = codeSnippet.getValidUntil();
    }

    /**
     * Constructor to generate new TL concept based on SL concept, without any title and definition
     * @param conceptSlDTO the SL concept to be copied
     */
    public ConceptDTO(ConceptDTO conceptSlDTO) {
        this.notation = conceptSlDTO.getNotation();
        this.parent = conceptSlDTO.getParent();
        this.position = conceptSlDTO.getPosition();
        this.deprecated = conceptSlDTO.getDeprecated();
        this.replacedById = conceptSlDTO.getReplacedById();
        this.replacedByUri = conceptSlDTO.getReplacedByUri();
        this.replacedByNotation = conceptSlDTO.getReplacedByNotation();
        this.slConcept = conceptSlDTO.getId();
        this.introducedInVersionId = conceptSlDTO.getIntroducedInVersionId();
        this.validUntilVersionId = conceptSlDTO.getValidUntilVersionId();
        this.validFrom = conceptSlDTO.getValidFrom();
        this.validUntil = conceptSlDTO.getValidUntil();

        if (this.deprecated) {
            this.uri = conceptSlDTO.getUri();
        }
    }

    /**
     *
     * @param conceptSlDTO, the based concept to be copied, defined the concept structure
     * @param prevConceptDTO, the previous concept, define the concept information in specific language
     */
    public ConceptDTO( ConceptDTO conceptSlDTO, ConceptDTO prevConceptDTO, Long newVersionId ) {
        this.versionId = newVersionId;
        this.introducedInVersionId = conceptSlDTO.getIntroducedInVersionId();
        this.validUntilVersionId = conceptSlDTO.getValidUntilVersionId();
        this.notation = conceptSlDTO.getNotation();
        this.parent = conceptSlDTO.getParent();
        this.position = conceptSlDTO.getPosition();
        this.deprecated = conceptSlDTO.getDeprecated();
        this.replacedById = conceptSlDTO.getReplacedById();
        this.replacedByUri = conceptSlDTO.getReplacedByUri();
        this.replacedByNotation = conceptSlDTO.getReplacedByNotation();
        this.slConcept = conceptSlDTO.getId();
        // prevConceptDTO is null "only happened in cloning TL concept". it means that new concept is added in SL concept,
        // or the concepts are reordered in SL version, which makes it is not possible to link the TL concept
        // form previous version with tha current SL version via concept notation
        if( prevConceptDTO != null ) {
            this.title = prevConceptDTO.getTitle();
            this.definition = prevConceptDTO.getDefinition();
            this.previousConcept = prevConceptDTO.getId();
        }
        // if cloning TL. (conceptSlDTO = prevConceptDTO, means clone SL concept)
        if( !conceptSlDTO.equals( prevConceptDTO )) {
            this.slConcept = conceptSlDTO.getId();
        }
        // the this.uri is skipped, since it is assigned during publication except for the deprecated codes
        if (this.deprecated) {
            this.uri = conceptSlDTO.getUri();
        }
        this.validFrom = conceptSlDTO.getValidFrom();
        this.validUntil = conceptSlDTO.getValidUntil();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Long getPreviousConcept() {
        return previousConcept;
    }

    public void setPreviousConcept(Long previousConcept) {
        this.previousConcept = previousConcept;
    }

    public Long getSlConcept() {
        return slConcept;
    }

    public void setSlConcept(Long slConcept) {
        this.slConcept = slConcept;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean getDeprecated() {
        return this.deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Long getReplacedById() {
        return this.replacedById;
    }

    public void setReplacedById(Long replacedById) {
        this.replacedById = replacedById;
    }

    public String getReplacedByUri() {
        return this.replacedByUri;
    }

    public void setReplacedByUri(String replacedByURI) {
        this.replacedByUri = replacedByURI;
    }

    public String getReplacedByNotation() {
        return this.replacedByNotation;
    }

    public void setReplacedByNotation(String replacedByNotation) {
        this.replacedByNotation = replacedByNotation;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getIntroducedInVersionId() {
        return introducedInVersionId;
    }

    public void setIntroducedInVersionId(Long introducedInVersionId) {
        this.introducedInVersionId = introducedInVersionId;
    }

    public Long getValidUntilVersionId() {
        return validUntilVersionId;
    }

    public void setValidUntilVersionId(Long validUntilVersionId) {
        this.validUntilVersionId = validUntilVersionId;
    }

    public LocalDate getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return this.validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public String toString() {
        return "ConceptDTO{" +
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
            ", replacedById=" + getReplacedById() +
            ", replacedByUri=" + getReplacedByUri() +
            ", replacedByNotation=" + getReplacedByNotation() +
            ", versionId=" + getVersionId() +
            ", introducedInVersionId=" + getIntroducedInVersionId() +
            ", validUntilVersionId=" + getValidUntilVersionId() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConceptDTO conceptDTO = (ConceptDTO) o;
        if (conceptDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), conceptDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
