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

import eu.cessda.cvs.domain.Agency;
import eu.cessda.cvs.service.dto.AgencyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Agency} and its DTO {@link AgencyDTO}.
 */
@Mapper(componentModel = "spring" )
public interface AgencyMapper extends EntityMapper<AgencyDTO, Agency> {


    @Mapping(target = "userAgencies", ignore = true)
    @Mapping(target = "removeUserAgency", ignore = true)
    Agency toEntity(AgencyDTO agencyDTO);

    @Mapping( target = "logo", ignore = true )
    @Mapping( target = "deletable", ignore = true )
    @Mapping( target = "copyright", ignore = true )
    AgencyDTO toDto( Agency entity );

    static Agency fromId( Long id) {
        if (id == null) {
            return null;
        }
        Agency agency = new Agency();
        agency.setId(id);
        return agency;
    }
}
