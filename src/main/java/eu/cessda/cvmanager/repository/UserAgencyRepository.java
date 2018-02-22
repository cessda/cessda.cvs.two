package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.UserAgency;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the UserAgency entity.
 */
@Repository
public interface UserAgencyRepository extends JpaRepository<UserAgency, Long> {
	
	@Query( "select ua from UserAgency ua where ua.user.firstName LIKE %:keyword% OR ua.user.lastName LIKE %:keyword% OR ua.agency.name LIKE %:keyword%" )
	List<UserAgency> findAll(@Param("keyword") String keyword);

}
