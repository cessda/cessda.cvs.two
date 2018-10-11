package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.service.dto.VersionDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity Cv and entity VersionDTO.
 */
@Mapper(componentModel = "spring", uses = {CvCodeMapper.class})
public interface CvMapper extends EntityMapper<VersionDTO, Cv> {
	
	@Mappings({
		@Mapping(source = "code", target = "notation"),
	    @Mapping(source = "term", target = "title"),
	    @Mapping(source = "version", target = "number"),
	    @Mapping(source = "type", target = "itemType"),
	})
	VersionDTO toDto( Cv cv);

	@Mappings({
		@Mapping(source = "notation", target = "code"),
	    @Mapping(source = "title", target = "term"),
	    @Mapping(source = "number", target = "version"),
	    @Mapping(source = "itemType", target = "type"),
	})
    Cv toEntity(VersionDTO versionDTO);
}
