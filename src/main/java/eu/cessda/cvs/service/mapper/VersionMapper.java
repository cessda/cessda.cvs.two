package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.service.dto.VersionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Version} and its DTO {@link VersionDTO}.
 */
@Mapper(componentModel = "spring", uses = {VocabularyMapper.class, ConceptMapper.class})
public interface VersionMapper extends EntityMapper<VersionDTO, Version> {

    @Mapping(source = "vocabulary.id", target = "vocabularyId")
    VersionDTO toDto(Version version);

    @Mapping(target = "removeConcept", ignore = true)
    @Mapping(source = "vocabularyId", target = "vocabulary")
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
