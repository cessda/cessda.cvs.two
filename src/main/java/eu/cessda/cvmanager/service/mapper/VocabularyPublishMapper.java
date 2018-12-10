package eu.cessda.cvmanager.service.mapper;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

import eu.cessda.cvmanager.domain.VocabularyPublish;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

/**
 * Mapper for the entity Vocabulary and its DTO VocabularyDTO.
 */
@Mapper(componentModel = "spring", uses = {CodeMapper.class, VersionMapper.class})
public interface VocabularyPublishMapper extends EntityMapper<VocabularyDTO, VocabularyPublish> {
		
	@Mappings({
		@Mapping(source = "vers", target = "vers"),
		@Mapping(source = "codes", target = "codes"),
		@Mapping(source = "languages", target = "languages"),
		@Mapping(source = "languagesPublished", target = "languagesPublished")
	})
    VocabularyDTO toDto(VocabularyPublish vocabulary);

    @Mappings({
    	@Mapping(source = "vers", target = "vers"),
	    @Mapping(source = "codes", target = "codes"),
	    @Mapping(source = "languages", target = "languages"),
		@Mapping(source = "languagesPublished", target = "languagesPublished")
    })
    VocabularyPublish toEntity(VocabularyDTO vocabularyDTO);

    default VocabularyPublish fromId(Long id) {
        if (id == null) {
            return null;
        }
        VocabularyPublish vocabulary = new VocabularyPublish();
        vocabulary.setId(id);
        return vocabulary;
    }
}