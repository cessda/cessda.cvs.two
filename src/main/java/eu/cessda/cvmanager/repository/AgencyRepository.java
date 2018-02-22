package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.Agency;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Agency entity.
 */
@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {

	@Query( "select a from Agency a where a.name LIKE %:keyword% OR a.description LIKE %:keyword%" )
	public List<Agency> findByKeyword(@Param("keyword") String keyword);
}
