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

import eu.cessda.cvs.domain.enumeration.ObjectType;

import javax.persistence.Lob;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link eu.cessda.cvs.domain.MetadataValue} entity.
 */
public class MetadataValueDTO implements Serializable {
    private static final long serialVersionUID = 504933053033683641L;

    private Long id;

    private String identifier;

    @Lob
    private String value;

    private ObjectType objectType;

    private Long objectId;

    private Long metadataFieldId;

    private String metadataKey;

    private Integer position = 0;

    public MetadataValueDTO(){}

    public MetadataValueDTO(String identifier, ObjectType objectType, Long metadataFieldId, String metadataKey, Integer position) {
        this.identifier = identifier;
        this.objectType = objectType;
        this.metadataFieldId = metadataFieldId;
        this.metadataKey = metadataKey;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getMetadataFieldId() {
        return metadataFieldId;
    }

    public void setMetadataFieldId(Long metadataFieldId) {
        this.metadataFieldId = metadataFieldId;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetadataValueDTO metadataValueDTO = (MetadataValueDTO) o;
        if (metadataValueDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), metadataValueDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MetadataValueDTO{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", position='" + getPosition() + "'" +
            ", value='" + getValue() + "'" +
            ", objectType='" + getObjectType() + "'" +
            ", objectId=" + getObjectId() +
            ", metadataFieldId=" + getMetadataFieldId() +
            "}";
    }
}
