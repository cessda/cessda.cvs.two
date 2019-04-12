package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.Version;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the Version entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    @Query("select distinct version from Version version left join fetch version.concepts")
    List<Version> findAllWithEagerRelationships();

    @Query("select version from Version version left join fetch version.concepts where version.id =:id")
    Version findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId order by v.itemType, v.language, v.number DESC")
    List<Version> findAllByVocabulary(@Param("vocabularyId") Long vocabularyId);

    @Query( "select v from Version v where v.uri = :cvUri" )
	Version findByUri(@Param("cvUri") String cvUri);

    @Query( "select v from Version v where v.notation = :notation and v.language = :languageIso and v.number = :versionNumber" )
	Version findOneByNotationLangVersion(@Param("notation")String notation, @Param("languageIso")String languageIso, @Param("versionNumber")String versionNumber);

    @Query("select v from Version v where v.uriSl =:uriSl")
	List<Version> findAllByUriSl(@Param("uriSl")String uriSl);
}
