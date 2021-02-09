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

package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.VocabularyChange;
import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link VocabularyChange} and its DTO {@link VocabularyChangeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface VocabularyChangeMapper extends EntityMapper<VocabularyChangeDTO, VocabularyChange> {



    default VocabularyChange fromId(Long id) {
        if (id == null) {
            return null;
        }
        VocabularyChange vocabularyChange = new VocabularyChange();
        vocabularyChange.setId(id);
        return vocabularyChange;
    }
}
