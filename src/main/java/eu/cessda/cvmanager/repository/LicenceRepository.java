package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.Licence;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Licence entity.
 */
@Repository
public interface LicenceRepository extends JpaRepository<Licence, Long> {

}
