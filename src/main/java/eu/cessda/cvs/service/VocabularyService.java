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
package eu.cessda.cvs.service;

import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Vocabulary}.
 */
public interface VocabularyService {

    /**
     * Save a vocabulary.
     *
     * @param vocabularyDTO the entity to save.
     * @return the persisted entity.
     */
    VocabularyDTO save(VocabularyDTO vocabularyDTO);

    /**
     * Save a vocabulary by vocabularySnippet.
     *
     * @param vocabularySnippet the snippet of Vocabulary to save.
     * @return the persisted entity.
     * @throws IllegalActionTypeException if {@link VocabularySnippet#getActionType()} is not supported.
     * @throws InsufficientVocabularyAuthorityException if the user does not have permission to perform the action.
     * @throws MissingIdentifierException if the {@link ActionType} edits an existing vocabulary and the vocabulary ID is not provided.
     */
    VocabularyDTO saveVocabulary(VocabularySnippet vocabularySnippet) throws IllegalActionTypeException;

    /**
     * Create new Version either SL or TL
     *
     * @param prevVersionId the ID of version to be cloned/created to a new version
     * @return
     */
    VersionDTO createNewVersion(Long prevVersionId);

    /**
     * Save a concept by codeSnippet
     *
     * @param codeSnippet
     * @return
     */
    ConceptDTO saveCode(CodeSnippet codeSnippet) throws IllegalActionTypeException;

    /**
     * save any vocabularyChange from codeSNippet if any
     *
     * @param codeSnippet
     * @param versionDTO
     */
    void storeChangeType(CodeSnippet codeSnippet, VersionDTO versionDTO);

    /**
     * Get all the vocabularies.
     *
     * @return the list of entities
     */
    List<VocabularyDTO> findAll();

    /**
     * Get all the vocabularies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VocabularyDTO> findAll(Pageable pageable);


    /**
     * Get the "id" vocabulary.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VocabularyDTO> findOne(Long id);

    /**
     * Delete the "id" vocabulary.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete the entire CV publish JSON directory given path
     * @param path of the JSON CV published files
     */
    void deleteCvJsonDirectoryAndContent(Path path);

    /**
     * get Vocabulary by notation
     *
     * @param notation the vocabulary notation
     * @return the vocabulary
     */
    VocabularyDTO getByNotation(String notation);

    /**
     * CLone TL version during Source Language (SL) publication if any
     * @param currentVersionSl, the main reference/blueprint for the code structure
     * @param prevVersionSl, the second reference/blueprint for the code structure, in case code notation changed
     * @param prevVersionTl, the TL to be cloned
     * @return
     */
    VersionDTO cloneTl(VersionDTO currentVersionSl, VersionDTO prevVersionSl, VersionDTO prevVersionTl);

    /**
     * get Vocabulary by notation with the specific versions
     *
     * @param notation the vocabulary notation
     * @param slVersionNumber the version number of available SL version
     * @return the vocabulary with versions based on slVersionNumber
     */
    VocabularyDTO getVocabularyByNotationAndVersion(String notation, String slVersionNumber, boolean onlyPublished);

    /**
     * Perform indexing in all vocabularies in the Editor
     */
    void indexAllEditor();

    /**
     * Perform indexing in a vocabulary in the Editor
     *
     * @param vocabulary the VocabularyDTO needs to be re-indexed
     */
    void indexEditor( VocabularyDTO vocabulary );


    /**
     * Perform indexing in all agency statistic
     */
    void indexAllAgencyStats();

    /**
     * Perform indexing in agency statistic
     *
     *  @param vocabulary the VocabularyDTO needs to be re-indexed
     */
    void indexAgencyStats( VocabularyDTO vocabulary );

    /**
     * Get all of the published CVs JSON path
     */
    List<Path> getPublishedCvPaths();

    /**
     * Perform indexing in all published vocabularies
     */
    void indexAllPublished();

    /**
     * Perform indexing in a published vocabulary for the publication
     *
     * @param vocabulary the VocabularyDTO needs to be re-indexed
     */
    void indexPublished( VocabularyDTO vocabulary );

    /**
     * Find all vocabularies given ElasticSearch (ES) query
     *
     * @param esQueryResultDetail contains ES query details
     * @return esQueryResultDetail contains ES query details and results
     */
    EsQueryResultDetail search (EsQueryResultDetail esQueryResultDetail);

    /**
     * Find all vocabularies code given ElasticSearch (ES) query
     *
     * @param esQueryResultDetail contains ES query details
     * @return esQueryResultDetail contains ES query details and results
     */
    EsQueryResultDetail searchCode (EsQueryResultDetail esQueryResultDetail);

    /**
     * Generate JSON files for all published vocabularies.
     *
     */
    String generateJsonAllVocabularyPublish();

    /**
     * Generate JSON files for published vocabularies
     *
     * @param vocabularies the published vocabularyDTOs array
     */
    String generateJsonVocabularyPublish( VocabularyDTO... vocabularies );

    /**
     * Filter-out vocabularyDTO.versions based on versionList e.g. (en-1.0, fr-1.0.1). Includes all if versionList is null.
     *
     * @param versionList the list of versions to filter
     * @param vocabularyDTO the vocabulary to filter
     * @return a set of filtered versions.
     */
    Set<VersionDTO> filterOutVocabularyVersions(String versionList, VocabularyDTO vocabularyDTO);


    /**
     * Generate files to be exported for specific vocabulary
     *
     * @param vocabularyNotation the vocabulary notation
     * @param versionSl the Source Language version
     * @param versionList combination of language and version number, e.g. en_1.0
     * @param downloadType one of the following file types PDF, DOCX, HTML, RDF
     * @param requestURL request URL for statistical purposes
     * @param onlyPublished only select published vocabularies
     * @return the generated file's resolved path
     */
    Path generateVocabularyFileDownload(
        String vocabularyNotation,
        String versionSl,
        String versionList,
        ExportService.DownloadType downloadType,
        String requestURL,
        boolean onlyPublished);

    /**
     *
     * @param agencyId
     * @param agencyUri
     * @param agencyUriCode
     */
    //void updateVocabularyUri( Long agencyId, String agencyUri, String agencyUriCode );

    /**
     *
     * @param agencyId
     * @param agencyLogoPath
     */
    void updateVocabularyLogo( Long agencyId, String agencyLogoPath );

    /**
     * Forward the status of certain Vocabulary version (DRAFT -> REVIEW -> READY_TO_TRANSLATE -> PUBLISH) for SL
     * Forward the status of certain Vocabulary version (DRAFT -> REVIEW -> READY_TO_PUBLISH) for TL
     * @param vocabularySnippet
     * @return
     */
    VersionDTO forwardStatus(VocabularySnippet vocabularySnippet) throws IllegalActionTypeException;

    /**
     *
     * @param agencyId
     * @param agencyUri
     * @param agencyUriCode
     * @param agencyName
     */
    void updateVocabularyUri(Long agencyId, String agencyUri, String agencyUriCode, String agencyName);
}
