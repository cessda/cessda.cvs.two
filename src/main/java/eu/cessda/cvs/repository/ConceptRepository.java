package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.Concept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Concept entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long> {

    @Query( "select c from Concept c where c.version.id = :versionId order by c.id asc, c.position asc")
    List<Concept> findByVersion(@Param( "versionId" ) Long versionId );
}
