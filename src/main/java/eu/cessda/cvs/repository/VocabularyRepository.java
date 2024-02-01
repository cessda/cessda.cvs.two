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
package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data JPA repository for the Vocabulary entity.
 */
@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long>
{
    List<Vocabulary> findByAgencyId( Long agencyId );

    List<Vocabulary> findByNotation( String notation );

    boolean existsByNotation( String notation );

    boolean existsByAgencyId( Long agencyId );

    @Query( "select DISTINCT v from Vocabulary v where v.withdrawn IS true" )
    Page<Vocabulary> findAllWithdrawn(Pageable pageable );

    @Query( "select DISTINCT v from Vocabulary v where v.agencyId = :agencyId AND v.withdrawn IS true" )
    Page<Vocabulary> findAllWithdrawn(@Param( "agencyId" ) Long agencyId, Pageable pageable );
}
