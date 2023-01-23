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

import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.service.dto.VersionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Version} and its DTO {@link VersionDTO}.
 */
@Mapper(componentModel = "spring", uses = {VocabularyMapper.class, ConceptMapper.class, CommentMapper.class})
public interface VersionMapper extends EntityMapper<VersionDTO, Version> {

    @Mapping(source = "vocabulary.id", target = "vocabularyId")
    VersionDTO toDto(Version version);

    @Mapping(target = "removeConcept", ignore = true)
    @Mapping(source = "vocabularyId", target = "vocabulary")
    Version toEntity(VersionDTO versionDTO);

    default Version fromId(Long id) {
        if (id == null) {
            return null;
        }
        Version version = new Version();
        version.setId(id);
        return version;
    }
}
