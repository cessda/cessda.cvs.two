package eu.cessda.cvmanager.service.mapper;

import eu.cessda.cvmanager.domain.*;
import eu.cessda.cvmanager.service.dto.ActivityLogDTO;

import org.gesis.wts.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity ActivityLog and its DTO ActivityLogDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ActivityLogMapper extends EntityMapper<ActivityLogDTO, ActivityLog> {



    default ActivityLog fromId(Long id) {
        if (id == null) {
            return null;
        }
        ActivityLog activityLog = new ActivityLog();
        activityLog.setId(id);
        return activityLog;
    }
}
