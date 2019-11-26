package eu.cessda.cvmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cessda.cvmanager.domain.VocabularyChange;

/**
 * Spring Data JPA repository for the VocabularyChange entity.
 */
@SuppressWarnings( "unused" )
@Repository
public interface VocabularyChangeRepository extends JpaRepository<VocabularyChange, Long>
{

	@Query( "select v from VocabularyChange v where v.vocabularyId = :vocabularyId and v.versionId = :versionId" )
	List<VocabularyChange> findAllByVocabularyVersionId(
			@Param( "vocabularyId" ) Long vocabularyId,
			@Param( "versionId" ) Long versionId );
}
