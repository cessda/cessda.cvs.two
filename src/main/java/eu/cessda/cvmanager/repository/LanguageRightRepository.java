package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.LanguageRight;
import eu.cessda.cvmanager.service.dto.LanguageRightDTO;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the LanguageRight entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LanguageRightRepository extends JpaRepository<LanguageRight, Long> {
	
	@Query( "select lr from LanguageRight lr where lr.userAgency.user.firstName LIKE %:keyword% OR lr.userAgency.user.lastName LIKE %:keyword% OR lr.userAgency.agency.name LIKE %:keyword%" )
	List<LanguageRight> findAll(@Param("keyword") String keyword);

}
