/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.service.mapper;


import eu.cessda.cvs.domain.Comment;
import eu.cessda.cvs.service.dto.CommentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring", uses = {VersionMapper.class})
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {

    @Mapping(source = "version.id", target = "versionId")
    CommentDTO toDto(Comment comment);

    @Mapping(source = "versionId", target = "version")
    Comment toEntity(CommentDTO commentDTO);

    static Comment fromId(Long id) {
        if (id == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }
}
