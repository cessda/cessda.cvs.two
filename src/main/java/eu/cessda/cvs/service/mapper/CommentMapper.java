package eu.cessda.cvs.service.mapper;


import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.service.dto.CommentDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring", uses = {VersionMapper.class})
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {

    @Mapping(source = "version.id", target = "versionId")
    CommentDTO toDto(Comment comment);

    @Mapping(source = "versionId", target = "version")
    Comment toEntity(CommentDTO commentDTO);

    default Comment fromId(Long id) {
        if (id == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }
}
