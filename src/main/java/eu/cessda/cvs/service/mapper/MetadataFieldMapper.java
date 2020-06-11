package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.MetadataField;
import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link MetadataField} and its DTO {@link MetadataFieldDTO}.
 */
@Mapper(componentModel = "spring", uses = {MetadataValueMapper.class})
public interface MetadataFieldMapper extends EntityMapper<MetadataFieldDTO, MetadataField> {

    default MetadataField fromId(Long id) {
        if (id == null) {
            return null;
        }
        MetadataField metadataField = new MetadataField();
        metadataField.setId(id);
        return metadataField;
    }
}
