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

import eu.cessda.cvs.domain.MetadataField;
import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link MetadataField} and its DTO {@link MetadataFieldDTO}.
 */
@Mapper(componentModel = "spring", uses = {MetadataValueMapper.class})
public interface MetadataFieldMapper extends EntityMapper<MetadataFieldDTO, MetadataField> {

    @Mapping( target = "removeMetadataValue", ignore = true )
    MetadataField toEntity( MetadataFieldDTO dto );

    @Mapping( target = "removeMetadataValue", ignore = true )
    MetadataFieldDTO toDto( MetadataField entity );

    static MetadataField fromId( Long id) {
        if (id == null) {
            return null;
        }
        MetadataField metadataField = new MetadataField();
        metadataField.setId(id);
        return metadataField;
    }
}
