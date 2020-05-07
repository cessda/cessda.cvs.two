package eu.cessda.cvs.service.mapper;

import eu.cessda.cvs.domain.Resolver;
import eu.cessda.cvs.service.dto.ResolverDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Resolver} and its DTO {@link ResolverDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ResolverMapper extends EntityMapper<ResolverDTO, Resolver> {



    default Resolver fromId(Long id) {
        if (id == null) {
            return null;
        }
        Resolver resolver = new Resolver();
        resolver.setId(id);
        return resolver;
    }
}
