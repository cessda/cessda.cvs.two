package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.UserAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the UserAgency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAgencyRepository extends JpaRepository<UserAgency, Long> {
    @Query( "select ua from UserAgency ua where ua.user.firstName LIKE %:keyword% OR ua.user.lastName LIKE %:keyword% OR ua.agency.name LIKE %:keyword%" )
    List<UserAgency> findAll(@Param("keyword") String keyword);

    @Query( "select ua from UserAgency ua where ua.user.id = :userId" )
    List<UserAgency> findByUser(@Param("userId") Long userId);

    @Query( "select ua from UserAgency ua where ua.agency.id = :agencyId" )
    List<UserAgency> findByAgency(@Param("agencyId") Long agencyId);

    @Query( "select ua from UserAgency ua where ua.agency.id = :agencyId AND (ua.user.firstName LIKE %:keyword% OR ua.user.lastName LIKE %:keyword% OR ua.agency.name LIKE %:keyword%)" )
    List<UserAgency> findByAgency(@Param("keyword") String keyword, @Param("agencyId") Long agencyId);
}
