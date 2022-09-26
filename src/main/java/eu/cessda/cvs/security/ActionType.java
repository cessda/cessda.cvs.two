/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

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
    FORWARD_CV_SL_STATUS_REVIEW( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.CONTRIBUTOR_SL))),
    FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    FORWARD_CV_SL_STATUS_PUBLISH( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL))),
    FORWARD_CV_TL_STATUS_REVIEW( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.CONTRIBUTOR_TL))),
    FORWARD_CV_TL_STATUS_READY_TO_PUBLISH( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_TL))),
    // FORWARD_CV_TL_STATUS_PUBLISH( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL))),
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
