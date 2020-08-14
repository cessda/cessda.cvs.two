package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Version entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    @Query("select v from Version v where v.vocabulary.id =:vocabularyId order by v.itemType, v.language, v.number DESC")
    List<Version> findAllByVocabulary(@Param("vocabularyId") Long vocabularyId);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId and v.status = 'PUBLISHED' order by v.itemType ASC, v.language ASC, v.number DESC")
    List<Version> findAllPublishedByVocabulary(@Param("vocabularyId") Long vocabularyId);

    @Query( "select v from Version v where v.notation = :notation and v.language = :languageIso and v.number = :versionNumber" )
    Version findOneByNotationLangVersion(@Param("notation")String notation, @Param("languageIso")String languageIso, @Param("versionNumber")String versionNumber);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId and v.status = 'PUBLISHED' and v.language =:languageIso and v.id <:versionId order by v.itemType ASC, v.number DESC")
    List<Version> findOlderPublishedByVocabularyLanguageId(@Param("vocabularyId") Long vocabularyId, @Param("languageIso") String languageIso, @Param("versionId") Long versionId);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId and v.number like :versionNumberSl% order by v.itemType ASC, v.language ASC, v.number DESC")
    List<Version> findAllByVocabularyIdAndVersionNumberSl(@Param("vocabularyId") Long vocabularyId, @Param("versionNumberSl") String versionNumberSl);

    List<Version> findByCanonicalUri(String canonicalUri);

    List<Version> findByCanonicalUriStartingWith(String canonicalUri);
}
