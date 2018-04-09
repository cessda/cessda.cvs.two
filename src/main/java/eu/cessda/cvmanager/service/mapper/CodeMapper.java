package eu.cessda.cvmanager.service.mapper;

import org.gesis.wts.domain.*;
import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

import eu.cessda.cvmanager.domain.Code;
import eu.cessda.cvmanager.service.dto.CodeDTO;

/**
 * Mapper for the entity Code and its DTO CodeDTO.
 */
@Mapper(componentModel = "spring", uses = {VocabularyMapper.class})
public interface CodeMapper extends EntityMapper<CodeDTO, Code> {
	@Mappings({
		@Mapping(source = "languages", target = "languages"),
		@Mapping(source = "vocabulary.id", target = "vocabularyId")
	})
    CodeDTO toDto(Code code);

	@Mappings({
		@Mapping(target = "languages"),
		@Mapping(source = "vocabularyId", target = "vocabulary")
	})
    Code toEntity(CodeDTO codeDTO);

    default Code fromId(Long id) {
        if (id == null) {
            return null;
        }
        Code code = new Code();
        code.setId(id);
        return code;
    }
}
