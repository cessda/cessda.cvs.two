package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.LicenceDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity Licence and its DTO LicenceDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LicenceMapper extends EntityMapper<LicenceDTO, Licence> {



    default Licence fromId(Long id) {
        if (id == null) {
            return null;
        }
        Licence licence = new Licence();
        licence.setId(id);
        return licence;
    }
}
