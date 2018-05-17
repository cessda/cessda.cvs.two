package eu.cessda.cvmanager.service.impl;

import eu.cessda.cvmanager.service.ActivityLogService;
import eu.cessda.cvmanager.domain.ActivityLog;
import eu.cessda.cvmanager.repository.ActivityLogRepository;
import eu.cessda.cvmanager.service.dto.ActivityLogDTO;
import eu.cessda.cvmanager.service.mapper.ActivityLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ActivityLog.
 */
@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final Logger log = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    private final ActivityLogRepository activityLogRepository;

    private final ActivityLogMapper activityLogMapper;


    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, ActivityLogMapper activityLogMapper) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    /**
     * Save a activityLog.
     *
     * @param activityLogDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ActivityLogDTO save(ActivityLogDTO activityLogDTO) {
        log.debug("Request to save ActivityLog : {}", activityLogDTO);
        ActivityLog activityLog = activityLogMapper.toEntity(activityLogDTO);
        activityLog = activityLogRepository.save(activityLog);
        ActivityLogDTO result = activityLogMapper.toDto(activityLog);
//        activityLogSearchRepository.save(activityLog);
        return result;
    }

    /**
     * Get all the activityLogs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ActivityLogs");
        return activityLogRepository.findAll(pageable)
            .map(activityLogMapper::toDto);
    }

    /**
     * Get one activityLog by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ActivityLogDTO findOne(Long id) {
        log.debug("Request to get ActivityLog : {}", id);
        ActivityLog activityLog = activityLogRepository.getOne(id);
        return activityLogMapper.toDto(activityLog);
    }

    /**
     * Delete the activityLog by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ActivityLog : {}", id);
        activityLogRepository.deleteById(id);
//        activityLogSearchRepository.delete(id);
    }

    /**
     * Search for the activityLog corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ActivityLogs for query {}", query);
//        Page<ActivityLog> result = activityLogSearchRepository.search(queryStringQuery(query), pageable);
        return null;//result.map(activityLogMapper::toDto);
    }
}
