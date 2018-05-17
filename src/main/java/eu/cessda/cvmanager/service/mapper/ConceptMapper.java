package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.ConceptDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity Concept and its DTO ConceptDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ConceptMapper extends EntityMapper<ConceptDTO, Concept> {


    @Mapping(target = "conceptChanges", ignore = true)
    @Mapping(target = "versions", ignore = true)
    Concept toEntity(ConceptDTO conceptDTO);

    default Concept fromId(Long id) {
        if (id == null) {
            return null;
        }
        Concept concept = new Concept();
        concept.setId(id);
        return concept;
    }
}
