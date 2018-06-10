package eu.cessda.cvmanager.repository;

import org.springframework.stereotype.Repository;

import eu.cessda.cvmanager.domain.Code;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Code entity.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

	@Query( "select c from Code c where c.uri = :uri" )
	Code getByUri(@Param("uri") String uri);

	@Query( "select c from Code c where c.notation = :notation" )
	Code getByNotation(@Param("notation")String notation);

	@Query( "select c from Code c where c.vocabularyId = :vocabularyId order by position" )
	List<Code> findAllByVocabulary(@Param("vocabularyId")Long vocabularyId);

}
