package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.AgencyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Agency and its DTO AgencyDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AgencyMapper extends EntityMapper<AgencyDTO, Agency> {


    @Mapping(target = "userAgencies", ignore = true)
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
