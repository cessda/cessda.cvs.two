package eu.cessda.cvs.security;

import eu.cessda.cvs.domain.enumeration.AgencyRole;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The ActionType enumeration.
 * Each enums contains, types of AgencyRole allowed to perform the action
 */
public enum ActionType {
    CREATE_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    EDIT_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL))),
    EDIT_DDI_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL))),
    EDIT_VERSION_INFO_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL))),
    EDIT_NOTE_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL, AgencyRole.CONTRIBUTOR_SL, AgencyRole.CONTRIBUTOR_TL))),
    DELETE_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL))),
    ADD_USAGE_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.CONTRIBUTOR_SL, AgencyRole.ADMIN_TL, AgencyRole.CONTRIBUTOR_TL))),
    ADD_TL_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL))),
    WITHDRAWN_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    FORWARD_CV_SL_STATUS_REVIEW( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.CONTRIBUTOR_SL))),
    FORWARD_CV_SL_STATUS_PUBLISHED( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    FORWARD_CV_TL_STATUS_REVIEW( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.CONTRIBUTOR_TL))),
    FORWARD_CV_TL_STATUS_PUBLISHED( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL))),
    CREATE_CODE( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.CONTRIBUTOR_SL))),
    EDIT_CODE( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.CONTRIBUTOR_SL))),
    REORDER_CODE(new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    DELETE_CODE(new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    ADD_TL_CODE(new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.CONTRIBUTOR_TL))),
    EDIT_TL_CODE(new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.CONTRIBUTOR_TL))),
    DELETE_TL_CODE(new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.CONTRIBUTOR_TL))),
    CREATE_NEW_CV_SL_VERSION(new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    CREATE_NEW_CV_TL_VERSION(new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL)));

    private final Set<AgencyRole> agencyRoles;

    ActionType(Set<AgencyRole> agencyRoles) {
        this.agencyRoles = Collections.unmodifiableSet(agencyRoles);
    }

    /**
     * Returns a {@link Set} of agency roles allowed to perform the action.
     */
    public Set<AgencyRole> getAgencyRoles() {
        return agencyRoles;
    }
}
