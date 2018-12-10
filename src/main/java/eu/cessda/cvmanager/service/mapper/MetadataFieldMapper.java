package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.MetadataFieldDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity MetadataField and its DTO MetadataFieldDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MetadataFieldMapper extends EntityMapper<MetadataFieldDTO, MetadataField> {
	
	@Mappings({
		@Mapping(target = "metadataValues", ignore = true)
	})
    MetadataField toEntity(MetadataFieldDTO metadataFieldDTO);

    default MetadataField fromId(Long id) {
        if (id == null) {
            return null;
        }
        MetadataField metadataField = new MetadataField();
        metadataField.setId(id);
        return metadataField;
    }
}
