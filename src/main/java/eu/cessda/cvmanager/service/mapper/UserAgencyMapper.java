package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.UserAgencyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserAgency and its DTO UserAgencyDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, AgencyMapper.class})
public interface UserAgencyMapper extends EntityMapper<UserAgencyDTO, UserAgency> {

	@Mappings({
	    @Mapping(source = "user.id", target = "userId"),
	    @Mapping(source = "agency.id", target = "agencyId"),
	    @Mapping(source = "user.firstName", target = "firstName"),
	    @Mapping(source = "user.lastName", target = "lastName"),
	    @Mapping(source = "agency.name", target = "agencyName")
    })
	UserAgencyDTO toDto(UserAgency userAgency);
	
	@Mappings({
	    @Mapping(target = "languageRights", ignore = true),
	    @Mapping(source = "userId", target = "user"),
	    @Mapping(source = "agencyId", target = "agency")
    })
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
