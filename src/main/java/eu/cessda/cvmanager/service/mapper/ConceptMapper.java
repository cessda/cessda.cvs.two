package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.Concept;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper for the entity Concept and its DTO ConceptDTO.
 */
@Mapper(componentModel = "spring", uses = {VersionMapper.class})
public interface ConceptMapper extends EntityMapper<ConceptDTO, Concept> {
	
	@Mappings({
		@Mapping(source = "version.id", target = "versionId")
	})
	ConceptDTO toDto (Concept concept);

    @Mappings({
		@Mapping(source = "versionId", target = "version")
	})
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
