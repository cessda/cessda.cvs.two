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

import eu.cessda.cvs.domain.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;


/**
 * Spring Data  repository for the Version entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    @Query("select v from Version v where v.vocabulary.id =:vocabularyId order by v.itemType, v.language, v.number DESC")
    Stream<Version> findByVocabulary(@Param("vocabularyId") Long vocabularyId);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId and v.status = 'PUBLISHED' order by v.itemType ASC, v.language ASC, v.number DESC")
    Stream<Version> findPublishedByVocabulary(@Param("vocabularyId") Long vocabularyId);

    @Query( "select v from Version v where v.notation = :notation and v.language = :languageIso and v.number = :versionNumber" )
    Version findOneByNotationLangVersion(@Param("notation")String notation, @Param("languageIso")String languageIso, @Param("versionNumber")String versionNumber);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId and v.status = 'PUBLISHED' and v.language =:languageIso and v.id <:versionId order by v.itemType ASC, v.number DESC")
    Stream<Version> findOlderPublishedByVocabularyLanguageId(@Param("vocabularyId") Long vocabularyId, @Param("languageIso") String languageIso, @Param("versionId") Long versionId);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId and v.number like CONCAT(:versionNumberSl, '%') order by v.itemType ASC, v.language ASC, v.number DESC")
    Stream<Version> findAllByVocabularyIdAndVersionNumberSl(@Param("vocabularyId") Long vocabularyId, @Param("versionNumberSl") String versionNumberSl);

    @Query("select v from Version v where v.vocabulary.id =:vocabularyId and v.status = 'PUBLISHED' and v.number like CONCAT(:versionNumberSl, '%') order by v.itemType ASC, v.language ASC, v.number DESC")
    Stream<Version> findAllPublishedByVocabularyIdAndVersionNumberSl(@Param("vocabularyId") Long vocabularyId, @Param("versionNumberSl") String versionNumberSl);

    Stream<Version> findByCanonicalUri(String canonicalUri);

    Stream<Version> findByCanonicalUriStartingWith(String canonicalUri);

    @Query("select distinct(language) from Version v where v.status in (:status) order by v.language ASC")
    Stream<String> findLanguagesByStatus(@Param("status") List<String> status);
}
