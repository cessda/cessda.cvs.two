package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.Comment;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.version.id =:versionId order by c.dateTime ASC")
    List<Comment> findAllByVersion(@Param("versionId") Long versionId);
}
