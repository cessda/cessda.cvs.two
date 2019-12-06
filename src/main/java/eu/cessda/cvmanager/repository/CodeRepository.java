package eu.cessda.cvmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cessda.cvmanager.domain.Code;

/**
 * Spring Data JPA repository for the Code entity.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Long>
{

	@Query( "select c from Code c where c.uri = :uri" )
	Code getByUri( @Param( "uri" ) String uri );

	@Query( "select c from Code c where c.notation = :notation" )
	Code getByNotation( @Param( "notation" ) String notation );

	@Query( "select c from Code c where c.vocabularyId = :vocabularyId order by c.position" )
	List<Code> findAllByVocabulary( @Param( "vocabularyId" ) Long vocabularyId );

	@Query( "select c from Code c where c.vocabularyId = :vocabularyId AND c.versionNumber = :versionNumber order by position" )
	List<Code> findByVocabularyAndVersionNumber(
			@Param( "vocabularyId" ) Long vocabularyId,
			@Param( "versionNumber" ) String versionNumber );

	@Query( "select c from Code c where c.vocabularyId = :vocabularyId AND c.versionId = :versionId order by position" )
	List<Code> findByVocabularyAndVersion(
			@Param( "vocabularyId" ) Long vocabularyId,
			@Param( "versionId" ) Long versionId );

	@Query( "select c from Code c where c.vocabularyId = :vocabularyId AND c.versionNumber = :versionNumber AND c.archived = true order by position" )
	List<Code> findByArchivedByVocabularyAndVersionNumber(
			@Param( "vocabularyId" ) Long vocabularyId,
			@Param( "versionNumber" ) String versionNumber );

	@Query( "select c from Code c where c.vocabularyId = :vocabularyId AND c.versionId = :versionId AND c.archived = true order by position" )
	List<Code> findByArchivedVocabularyAndVersion(
			@Param( "vocabularyId" ) Long vocabularyId,
			@Param( "versionId" ) Long versionId );

	@Query( "select c from Code c where c.vocabularyId = :vocabularyId AND c.archived = false AND versionId IS NULL AND versionNumber IS NULL order by position" )
	List<Code> findWorkflowCodesByVocabulary( @Param( "vocabularyId" ) Long vocabularyId );

}
