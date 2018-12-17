package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.ui.view.importing.CsvRow;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity CsvRow and ConceptDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CsvRowToConceptDTOMapper extends EntityMapper<ConceptDTO, CsvRow> {
	
	@Mappings({
		@Mapping(source = "term", target = "title")
		
	})
	ConceptDTO toDto (CsvRow csvRow);
	
	@Mappings({
		@Mapping(source = "title", target = "term")
	})
	CsvRow toEntity(ConceptDTO conceptDTO);
}
