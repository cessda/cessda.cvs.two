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

import eu.cessda.cvs.domain.UserAgency;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link UserAgency} and its DTO {@link UserAgencyDTO}.
 */
@Mapper(componentModel = "spring", uses = {AgencyMapper.class})
public interface UserAgencyMapper extends EntityMapper<UserAgencyDTO, UserAgency> {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    UserAgencyDTO toDto(UserAgency userAgency);

    @Mapping(source = "agencyId", target = "agency")
    UserAgency toEntity(UserAgencyDTO userAgencyDTO);

    default UserAgency fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserAgency userAgency = new UserAgency();
        userAgency.setId(id);
        return userAgency;
    }
}
