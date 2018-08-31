package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.License;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the License entity.
 */
@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

}
