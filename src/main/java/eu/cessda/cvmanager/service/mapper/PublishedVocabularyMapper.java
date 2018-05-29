package eu.cessda.cvmanager.service.mapper;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

import eu.cessda.cvmanager.domain.PublishedVocabulary;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

/**
 * Mapper for the entity Vocabulary and its DTO VocabularyDTO.
 */
@Mapper(componentModel = "spring", uses = {CodeMapper.class, VersionMapper.class})
public interface PublishedVocabularyMapper extends EntityMapper<VocabularyDTO, PublishedVocabulary> {
		
	@Mappings({
		@Mapping(source = "codes", target = "codes"),
		@Mapping(source = "languages", target = "languages"),
	})
    VocabularyDTO toDto(PublishedVocabulary vocabulary);

    @Mappings({
	    @Mapping(source = "codes", target = "codes"),
	    @Mapping(source = "languages", target = "languages"),
    })
    PublishedVocabulary toEntity(VocabularyDTO vocabularyDTO);

    default PublishedVocabulary fromId(Long id) {
        if (id == null) {
            return null;
        }
        PublishedVocabulary vocabulary = new PublishedVocabulary();
        vocabulary.setId(id);
        return vocabulary;
    }
}
