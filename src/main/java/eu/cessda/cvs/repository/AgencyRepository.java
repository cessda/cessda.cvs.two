package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.Agency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Agency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {
    @Query( "select DISTINCT a from Agency a where a.name LIKE %:keyword% OR a.description LIKE %:keyword% order by a.name" )
    List<Agency> findByKeyword(@Param("keyword") String keyword);

    @Query( "select DISTINCT a from Agency a where a.name LIKE %:keyword% OR a.description LIKE %:keyword% order by a.name" )
    Page<Agency> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query( "SELECT DISTINCT a FROM Agency a LEFT JOIN a.userAgencies ua where ua.user.id = :userId" )
    List<Agency> findByUserId(@Param("userId") Long userId);

    @Query( "select a from Agency a where a.name = :name" )
    Agency getByName(@Param("name") String name);

    Page<Agency> findByName(String name, Pageable pageable);
}
