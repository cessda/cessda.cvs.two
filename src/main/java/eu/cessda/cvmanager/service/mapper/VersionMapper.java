package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.VersionDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity Version and its DTO VersionDTO.
 */
@Mapper(componentModel = "spring", uses = {ConceptMapper.class, VocabularyMapper.class})
public interface VersionMapper extends EntityMapper<VersionDTO, Version> {
	
	@Mappings({
		@Mapping(source = "vocabulary.id", target = "vocabularyId"),
	    @Mapping(source = "discussionNotes", target = "discussionNotes")
	})
	VersionDTO toDto( Version version);

	@Mappings({
		@Mapping(source = "vocabularyId", target = "vocabulary"),
	    @Mapping(source = "discussionNotes", target = "discussionNotes")
	})
    Version toEntity(VersionDTO versionDTO);

    default Version fromId(Long id) {
        if (id == null) {
            return null;
        }
        Version version = new Version();
        version.setId(id);
        return version;
    }
}
