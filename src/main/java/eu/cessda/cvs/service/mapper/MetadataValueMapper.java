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

import eu.cessda.cvs.domain.MetadataValue;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link MetadataValue} and its DTO {@link MetadataValueDTO}.
 */
@Mapper(componentModel = "spring", uses = {MetadataFieldMapper.class})
public interface MetadataValueMapper extends EntityMapper<MetadataValueDTO, MetadataValue> {

    @Mapping(source = "metadataField.id", target = "metadataFieldId")
    @Mapping(source = "metadataField.metadataKey", target = "metadataKey")
    MetadataValueDTO toDto(MetadataValue metadataValue);

    @Mapping(source = "metadataFieldId", target = "metadataField")
    MetadataValue toEntity(MetadataValueDTO metadataValueDTO);

    static MetadataValue fromId(Long id) {
        if (id == null) {
            return null;
        }
        MetadataValue metadataValue = new MetadataValue();
        metadataValue.setId(id);
        return metadataValue;
    }
}
