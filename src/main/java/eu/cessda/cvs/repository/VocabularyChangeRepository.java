package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.VocabularyChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the VocabularyChange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VocabularyChangeRepository extends JpaRepository<VocabularyChange, Long> {

    @Query( "select v from VocabularyChange v where v.vocabularyId = :vocabularyId and v.versionId = :versionId" )
    List<VocabularyChange> findAllByVocabularyVersionId(
        @Param( "vocabularyId" ) Long vocabularyId,
        @Param( "versionId" ) Long versionId );

    List<VocabularyChange> findByVersionId(Long versionId);
}
