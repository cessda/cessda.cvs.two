package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.MetadataValue;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link MetadataValue} and its DTO {@link MetadataValueDTO}.
 */
@Mapper(componentModel = "spring", uses = {MetadataFieldMapper.class})
public interface MetadataValueMapper extends EntityMapper<MetadataValueDTO, MetadataValue> {

    @Mapping(source = "metadataField.id", target = "metadataFieldId")
    MetadataValueDTO toDto(MetadataValue metadataValue);

    @Mapping(source = "metadataFieldId", target = "metadataField")
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
