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

import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for the entity {@link Vocabulary} and its DTO {@link VocabularyDTO}.
 */
@Mapper(componentModel = "spring", uses = {VersionMapper.class})
public interface VocabularyMapper extends EntityMapper<VocabularyDTO, Vocabulary> {

    @Mapping(target = "statusByVocabularySnippet", ignore = true)
    @Mapping(target = "versionNumberByVocabularySnippet", ignore = true)
    @Mapping(target = "contentByVocabularySnippet", ignore = true)
    @Mapping(target = "agencyLink", ignore = true)
    @Mapping(target = "codes", ignore = true)
    @Mapping(target = "removeVersion", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "languagesPublished", ignore = true)
    @Mapping(target = "statuses", ignore = true)
    VocabularyDTO toDto(Vocabulary vocabulary);

    @Mapping( target = "removeVersion", ignore = true )
    Vocabulary toEntity( VocabularyDTO vocabularyDTO);

    @AfterMapping
    default void setTitleAll(@MappingTarget VocabularyDTO vocabularyDTO) {
        vocabularyDTO.setTitleAll();
    }

    static Vocabulary fromId(Long id) {
        if (id == null) {
            return null;
        }
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setId(id);
        return vocabulary;
    }
}
