package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.VocabularyChange;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;


/**
 * Spring Data JPA repository for the VocabularyChange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VocabularyChangeRepository extends JpaRepository<VocabularyChange, Long> {

	@Query( "select v from VocabularyChange v where v.vocabularyId = :vocabularyId and v.versionId = :versionId" )
	List<VocabularyChange> findAllByVocabularyVersionId(@Param("vocabularyId") Long vocabularyId, @Param("versionId") Long versionId);
}
