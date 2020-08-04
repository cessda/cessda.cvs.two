package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.search.VocabularyPublish;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Vocabulary and its DTO VocabularyDTO.
 */
@Mapper(componentModel = "spring", uses = {CodeMapper.class})
public interface VocabularyPublishMapper extends EntityMapper<VocabularyDTO, VocabularyPublish> {
    @Mapping(source = "codes", target = "codes")
    @Mapping(source = "languagesPublished", target = "languagesPublished")
    VocabularyDTO toDto(VocabularyPublish vocabulary);

    @Mapping(source = "codes", target = "codes")
    @Mapping(source = "languagesPublished", target = "languagesPublished")
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
