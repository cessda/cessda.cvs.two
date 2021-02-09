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

import eu.cessda.cvs.domain.VocabularyChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the VocabularyChange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VocabularyChangeRepository extends JpaRepository<VocabularyChange, Long> {

    @Query( "select v from VocabularyChange v where v.vocabularyId = :vocabularyId and v.versionId = :versionId" )
    List<VocabularyChange> findAllByVocabularyVersionId(
        @Param( "vocabularyId" ) Long vocabularyId,
        @Param( "versionId" ) Long versionId );

    List<VocabularyChange> findByVersionId(Long versionId);
}
