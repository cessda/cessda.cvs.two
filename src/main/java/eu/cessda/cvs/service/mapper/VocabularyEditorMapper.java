package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.search.VocabularyEditor;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link VocabularyEditor} and its DTO {@link VocabularyDTO}.
 */
@Mapper(componentModel = "spring", uses = {CodeMapper.class})
public interface VocabularyEditorMapper extends EntityMapper<VocabularyDTO, VocabularyEditor> {

    @Mapping(source = "codes", target = "codes")
    @Mapping(source = "languages", target = "languages")
    @Mapping(source = "statuses", target = "statuses")
    VocabularyDTO toDto(VocabularyEditor vocabularyEditor);

    @Mapping(source = "codes", target = "codes")
    @Mapping(source = "languages", target = "languages")
    @Mapping(source = "statuses", target = "statuses")
    VocabularyEditor toEntity(VocabularyDTO vocabularyDTO);

    default VocabularyEditor fromId(Long id) {
        if (id == null) {
            return null;
        }
        VocabularyEditor vocabulary = new VocabularyEditor();
        vocabulary.setId(id);
        return vocabulary;
    }
}
