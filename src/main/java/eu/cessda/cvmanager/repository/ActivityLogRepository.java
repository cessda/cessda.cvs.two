package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.ActivityLog;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ActivityLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

}
