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

package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.UserAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the UserAgency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAgencyRepository extends JpaRepository<UserAgency, Long> {
    @Query( "select ua from UserAgency ua where ua.user.firstName LIKE %:keyword% OR ua.user.lastName LIKE %:keyword% OR ua.agency.name LIKE %:keyword%" )
    List<UserAgency> findAll(@Param("keyword") String keyword);

    @Query( "select ua from UserAgency ua where ua.user.id = :userId" )
    List<UserAgency> findByUser(@Param("userId") Long userId);

    @Query( "select ua from UserAgency ua where ua.agency.id = :agencyId" )
    List<UserAgency> findByAgency(@Param("agencyId") Long agencyId);

    @Query( "select ua from UserAgency ua where ua.agency.id = :agencyId AND (ua.user.firstName LIKE %:keyword% OR ua.user.lastName LIKE %:keyword% OR ua.agency.name LIKE %:keyword%)" )
    List<UserAgency> findByAgency(@Param("keyword") String keyword, @Param("agencyId") Long agencyId);
}
