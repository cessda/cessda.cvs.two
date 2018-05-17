package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.ConceptChangeDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity ConceptChange and its DTO ConceptChangeDTO.
 */
@Mapper(componentModel = "spring", uses = {ConceptMapper.class})
public interface ConceptChangeMapper extends EntityMapper<ConceptChangeDTO, ConceptChange> {

    @Mapping(source = "concept.id", target = "conceptId")
    ConceptChangeDTO toDto(ConceptChange conceptChange);

    @Mapping(source = "conceptId", target = "concept")
    ConceptChange toEntity(ConceptChangeDTO conceptChangeDTO);

    default ConceptChange fromId(Long id) {
        if (id == null) {
            return null;
        }
        ConceptChange conceptChange = new ConceptChange();
        conceptChange.setId(id);
        return conceptChange;
    }
}
