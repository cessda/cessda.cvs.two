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
