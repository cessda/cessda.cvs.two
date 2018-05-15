package eu.cessda.cvmanager.service;

import eu.cessda.cvmanager.service.dto.ActivityLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing ActivityLog.
 */
public interface ActivityLogService {

    /**
     * Save a activityLog.
     *
     * @param activityLogDTO the entity to save
     * @return the persisted entity
     */
    ActivityLogDTO save(ActivityLogDTO activityLogDTO);

    /**
     * Get all the activityLogs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ActivityLogDTO> findAll(Pageable pageable);

    /**
     * Get the "id" activityLog.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ActivityLogDTO findOne(Long id);

    /**
     * Delete the "id" activityLog.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the activityLog corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ActivityLogDTO> search(String query, Pageable pageable);
}
