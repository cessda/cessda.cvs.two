package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.UserAgencyRole;
import eu.cessda.cvmanager.service.dto.UserAgencyRoleDTO;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the UserAgencyRole entity.
 */
@Repository
public interface UserAgencyRoleRepository extends JpaRepository<UserAgencyRole, Long> {

	@Query( "select uar from UserAgencyRole uar where uar.user.firstName LIKE %:keyword% OR uar.user.lastName LIKE %:keyword% OR "
			+ "uar.userAgency.user.firstName LIKE %:keyword% OR uar.userAgency.user.lastName LIKE %:keyword% OR uar.userAgency.agency.name LIKE %:keyword% OR "
			+ "uar.role.name LIKE %:keyword%" )
	List<UserAgencyRole> findAll(@Param("keyword") String keyword);

}
