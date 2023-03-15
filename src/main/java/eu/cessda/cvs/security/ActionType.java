/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.security;

import eu.cessda.cvs.domain.enumeration.AgencyRole;

import java.util.EnumSet;
import java.util.Set;

/**
 * The ActionType enumeration.
 * Each enums contains, types of AgencyRole allowed to perform the action
 */
public enum ActionType {
    CREATE_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    EDIT_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    EDIT_DDI_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    EDIT_IDENTITY_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    EDIT_VERSION_INFO_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    EDIT_NOTE_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    DELETE_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    // ADD_USAGE_CV( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL))),
    ADD_TL_CV(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    FORWARD_CV_SL_STATUS_REVIEW(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    FORWARD_CV_SL_STATUS_PUBLISH(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    FORWARD_CV_TL_STATUS_REVIEW(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    FORWARD_CV_TL_STATUS_READY_TO_PUBLISH(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    // FORWARD_CV_TL_STATUS_PUBLISH( new HashSet<>(Arrays.asList(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_TL))),
    CREATE_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    EDIT_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    REORDER_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    DEPRECATE_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    DELETE_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    ADD_TL_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    EDIT_TL_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    DELETE_TL_CODE(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT)),
    CREATE_NEW_CV_SL_VERSION(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_SL, AgencyRole.ADMIN_CONTENT)),
    CREATE_NEW_CV_TL_VERSION(EnumSet.of(AgencyRole.ADMIN, AgencyRole.ADMIN_TL, AgencyRole.ADMIN_CONTENT));

    private final EnumSet<AgencyRole> agencyRoles;

    ActionType(EnumSet<AgencyRole> agencyRoles) {
        this.agencyRoles = agencyRoles;
    }

    /**
     * Returns a {@link Set} of agency roles allowed to perform the action.
     */
    public Set<AgencyRole> getAgencyRoles() {
        return agencyRoles;
    }
}
