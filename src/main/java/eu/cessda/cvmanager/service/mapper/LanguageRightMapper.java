package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.LanguageRightDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity LanguageRight and its DTO LanguageRightDTO.
 */
@Mapper(componentModel = "spring", uses = {UserAgencyMapper.class})
public interface LanguageRightMapper extends EntityMapper<LanguageRightDTO, LanguageRight> {

	@Mappings({
		@Mapping(source = "userAgency.id", target = "userAgencyId"),
		@Mapping(source = "userAgency.user.firstName", target = "firstName"),
	    @Mapping(source = "userAgency.user.lastName", target = "lastName"),
	    @Mapping(source = "userAgency.agency.name", target = "agencyName")
	})
	LanguageRightDTO toDto(LanguageRight languageRight);

    @Mapping(source = "userAgencyId", target = "userAgency")
    LanguageRight toEntity(LanguageRightDTO languageRightDTO);

    default LanguageRight fromId(Long id) {
        if (id == null) {
            return null;
        }
        LanguageRight languageRight = new LanguageRight();
        languageRight.setId(id);
        return languageRight;
    }
}
