package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity VocabularyChange and its DTO VocabularyChangeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface VocabularyChangeMapper extends EntityMapper<VocabularyChangeDTO, VocabularyChange> {

	@Mappings({
		@Mapping(source = "vocabularyId", target = "vocabularyId"),
		@Mapping(source = "versionId", target = "versionId"),
		@Mapping(source = "userName", target = "userName")
	})
    VocabularyChangeDTO toDto(VocabularyChange vocabularyChange);

	@Mappings({
		@Mapping(source = "vocabularyId", target = "vocabularyId"),
		@Mapping(source = "versionId", target = "versionId"),
		@Mapping(source = "userName", target = "userName")
	})
    VocabularyChange toEntity(VocabularyChangeDTO vocabularyChangeDTO);

    default VocabularyChange fromId(Long id) {
        if (id == null) {
            return null;
        }
        VocabularyChange vocabularyChange = new VocabularyChange();
        vocabularyChange.setId(id);
        return vocabularyChange;
    }
}
