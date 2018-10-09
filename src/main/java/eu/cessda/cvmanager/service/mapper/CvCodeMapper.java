package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.service.dto.ConceptDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity CvCode to entity ConceptDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CvCodeMapper extends EntityMapper<ConceptDTO, CvCode> {
	
	@Mappings({
		@Mapping(source = "code", target = "notation"),
		@Mapping(source = "term", target = "title")
	})
	ConceptDTO toDto (CvCode cvCode);
	
	@Mappings({
		@Mapping(source = "notation", target = "code"),
		@Mapping(source = "title", target = "term")
	})
	CvCode toEntity(ConceptDTO conceptDTO);
}
