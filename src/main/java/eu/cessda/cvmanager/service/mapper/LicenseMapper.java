package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.LicenseDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity License and its DTO LicenseDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LicenseMapper extends EntityMapper<LicenseDTO, License> {



    default License fromId(Long id) {
        if (id == null) {
            return null;
        }
        License license = new License();
        license.setId(id);
        return license;
    }
}
