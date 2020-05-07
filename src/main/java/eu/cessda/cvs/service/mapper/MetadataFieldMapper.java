package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.MetadataField;
import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link MetadataField} and its DTO {@link MetadataFieldDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MetadataFieldMapper extends EntityMapper<MetadataFieldDTO, MetadataField> {


    @Mapping(target = "metadataValues", ignore = true)
    @Mapping(target = "removeMetadataValue", ignore = true)
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
