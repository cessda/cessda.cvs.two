package eu.cessda.cvmanager.service.mapper;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

/**
 * Mapper for the entity Vocabulary and its DTO VocabularyDTO.
 */
@Mapper(componentModel = "spring")
public interface VocabularyMapper extends EntityMapper<VocabularyDTO, Vocabulary> {
	
	@Mappings({
		@Mapping(source = "languages", target = "languages"),
	})
    VocabularyDTO toDto(Vocabulary vocabulary);

    @Mappings({
	    @Mapping(target = "codes", ignore = true),
	    @Mapping(target = "languages"),
    })
    Vocabulary toEntity(VocabularyDTO vocabularyDTO);

    default Vocabulary fromId(Long id) {
        if (id == null) {
            return null;
        }
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setId(id);
        return vocabulary;
    }
}
