package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.ResolverDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity Resolver and its DTO ResolverDTO.
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
