package eu.cessda.cvmanager.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import eu.cessda.cvmanager.domain.Vocabulary;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Vocabulary entity.
 */
@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

	@Query( "select v from Vocabulary v where v.uri = :cvUri" )
	Vocabulary findByUri(@Param("cvUri") String cvUri);

	@Query( "select v from Vocabulary v where v.agencyId = :agencyId" )
	List<Vocabulary> findByAgency(@Param("agencyId") Long agencyId);

}
