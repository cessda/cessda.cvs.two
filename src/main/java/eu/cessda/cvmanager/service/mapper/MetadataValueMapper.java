package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.MetadataValueDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity MetadataValue and its DTO MetadataValueDTO.
 */
@Mapper(componentModel = "spring", uses = {MetadataFieldMapper.class})
public interface MetadataValueMapper extends EntityMapper<MetadataValueDTO, MetadataValue> {
	
	@Mappings({
		@Mapping(source = "metadataField.id", target = "metadataFieldId")
	})
	MetadataValueDTO toDto (MetadataValue metadataValue);
	
	@Mappings({
		@Mapping(source = "metadataFieldId", target = "metadataField.id")
	})
    MetadataValue toEntity(MetadataValueDTO metadataValueDTO);

    default MetadataValue fromId(Long id) {
        if (id == null) {
            return null;
        }
        MetadataValue metadataValue = new MetadataValue();
        metadataValue.setId(id);
        return metadataValue;
    }
}
