package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.VocabularyChange;
import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link VocabularyChange} and its DTO {@link VocabularyChangeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface VocabularyChangeMapper extends EntityMapper<VocabularyChangeDTO, VocabularyChange> {



    default VocabularyChange fromId(Long id) {
        if (id == null) {
            return null;
        }
        VocabularyChange vocabularyChange = new VocabularyChange();
        vocabularyChange.setId(id);
        return vocabularyChange;
    }
}
