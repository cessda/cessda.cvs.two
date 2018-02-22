package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.UserAgencyRoleDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserAgencyRole and its DTO UserAgencyRoleDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, RoleMapper.class, UserAgencyMapper.class})
public interface UserAgencyRoleMapper extends EntityMapper<UserAgencyRoleDTO, UserAgencyRole> {
	@Mappings({
	    @Mapping(source = "user.id", target = "userId"),
	    @Mapping(source = "role.id", target = "roleId"),
	    @Mapping(source = "userAgency.id", target = "userAgencyId"),
	    @Mapping(source = "user.firstName", target = "firstName"),
	    @Mapping(source = "user.lastName", target = "lastName"),
	    @Mapping(source = "userAgency.user.firstName", target = "uaFirstName"),
	    @Mapping(source = "userAgency.user.lastName", target = "uaLastName"),
	    @Mapping(source = "userAgency.agency.name", target = "agency"),
	    @Mapping(source = "role.name", target = "role")
	})
    UserAgencyRoleDTO toDto(UserAgencyRole userAgencyRole);

	@Mappings({
	    @Mapping(source = "userId", target = "user"),
	    @Mapping(source = "roleId", target = "role"),
	    @Mapping(source = "userAgencyId", target = "userAgency")
	})
    UserAgencyRole toEntity(UserAgencyRoleDTO userAgencyRoleDTO);

    default UserAgencyRole fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserAgencyRole userAgencyRole = new UserAgencyRole();
        userAgencyRole.setId(id);
        return userAgencyRole;
    }
}
