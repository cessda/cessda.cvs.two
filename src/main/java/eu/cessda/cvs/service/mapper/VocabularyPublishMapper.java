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

import eu.cessda.cvs.domain.search.VocabularyPublish;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Vocabulary and its DTO VocabularyDTO.
 */
@Mapper(componentModel = "spring", uses = {CodeMapper.class})
public interface VocabularyPublishMapper extends EntityMapper<VocabularyDTO, VocabularyPublish> {

    @Mapping(source = "codes", target = "codes")
    @Mapping(source = "languagesPublished", target = "languagesPublished")
    @Mapping(target = "statusByVocabularySnippet", ignore = true)
    @Mapping(target = "versionNumberByVocabularySnippet", ignore = true)
    @Mapping(target = "agencyLink", ignore = true)
    @Mapping(target = "versions", ignore = true)
    @Mapping(target = "removeVersion", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "statuses", ignore = true)
    @Mapping(target = "contentByVocabularySnippet", ignore = true)
    VocabularyDTO toDto(VocabularyPublish vocabulary);

    @Mapping(source = "codes", target = "codes")
    @Mapping(source = "languagesPublished", target = "languagesPublished")
    VocabularyPublish toEntity(VocabularyDTO vocabularyDTO);

    default VocabularyPublish fromId(Long id) {
        if (id == null) {
            return null;
        }
        VocabularyPublish vocabulary = new VocabularyPublish();
        vocabulary.setId(id);
        return vocabulary;
    }
}
