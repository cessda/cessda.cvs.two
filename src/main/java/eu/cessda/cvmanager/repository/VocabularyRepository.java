package eu.cessda.cvmanager.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import eu.cessda.cvmanager.domain.Vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;


/**
 * Spring Data JPA repository for the Vocabulary entity.
 */
@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

	@Query( "select v from Vocabulary v where v.uri = :cvUri" )
	Vocabulary findByUri(@Param("cvUri") String cvUri);

	@Query( "select v from Vocabulary v where v.agencyId = :agencyId" )
	List<Vocabulary> findByAgency(@Param("agencyId") Long agencyId);

	@Query( "select v from Vocabulary v where v.notation = :notation" )
	Vocabulary findByNotation(@Param("notation") String notation);

	boolean existsByNotation(String notation);
	
	@Query( "select DISTINCT v from Vocabulary v where v.withdrawn IS true" )
	Page<Vocabulary> findAllWithdrawn(Pageable pageable);

	@Query( "select DISTINCT v from Vocabulary v where v.agencyId = :agencyId AND v.withdrawn IS true" )
	Page<Vocabulary> findAllWithdrawn(@Param("agencyId") Long agencyId, Pageable pageable);

}
