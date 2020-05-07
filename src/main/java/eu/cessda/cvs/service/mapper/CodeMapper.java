package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.Code;
import eu.cessda.cvs.service.dto.CodeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Code} and its DTO {@link CodeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CodeMapper extends EntityMapper<CodeDTO, Code> {

    @Mapping(source = "languages", target = "languages")
    CodeDTO toDto(Code code);

    @Mapping(source = "languages", target = "languages")
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
