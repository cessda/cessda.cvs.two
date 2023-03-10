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
package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.Concept;
import eu.cessda.cvs.service.dto.ConceptDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Concept} and its DTO {@link ConceptDTO}.
 */
@Mapper(componentModel = "spring", uses = {VersionMapper.class})
public interface ConceptMapper extends EntityMapper<ConceptDTO, Concept> {

    @Mapping(source = "version.id", target = "versionId")
    @Mapping(source = "introducedInVersion.id", target = "introducedInVersionId")
    @Mapping(source = "introducedInVersion.publicationDate", target = "validFrom")
    @Mapping(source = "validUntilVersion.id", target = "validUntilVersionId")
    @Mapping(source = "validUntilVersion.publicationDate", target = "validUntil")
    @Mapping(source = "replacedBy.id", target = "replacedById")
    @Mapping(source = "replacedBy.notation", target = "replacedByNotation")
    @Mapping(source = "replacedBy.uri", target = "replacedByUri")
    ConceptDTO toDto(Concept concept);

    @Mapping(source = "versionId", target = "version")
    @Mapping(source = "introducedInVersionId", target = "introducedInVersion")
    @Mapping(source = "validUntilVersionId", target = "validUntilVersion")
    @Mapping(source = "replacedById", target = "replacedBy")
    Concept toEntity(ConceptDTO conceptDTO);

    default Concept fromId(Long id) {
        if (id == null) {
            return null;
        }
        Concept concept = new Concept();
        concept.setId(id);
        return concept;
    }
}
