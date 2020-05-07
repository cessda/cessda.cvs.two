package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.Concept;
import eu.cessda.cvs.service.dto.ConceptDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Concept} and its DTO {@link ConceptDTO}.
 */
@Mapper(componentModel = "spring", uses = {VersionMapper.class})
public interface ConceptMapper extends EntityMapper<ConceptDTO, Concept> {

    @Mapping(source = "version.id", target = "versionId")
    ConceptDTO toDto(Concept concept);

    @Mapping(source = "versionId", target = "version")
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
