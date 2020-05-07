package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.Agency;
import eu.cessda.cvs.service.dto.AgencyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Agency} and its DTO {@link AgencyDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AgencyMapper extends EntityMapper<AgencyDTO, Agency> {


    @Mapping(target = "userAgencies", ignore = true)
    @Mapping(target = "removeUserAgency", ignore = true)
    Agency toEntity(AgencyDTO agencyDTO);

    default Agency fromId(Long id) {
        if (id == null) {
            return null;
        }
        Agency agency = new Agency();
        agency.setId(id);
        return agency;
    }
}
