package eu.cessda.cvs.service;

import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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
     */
    VocabularyDTO saveVocabulary(VocabularySnippet vocabularySnippet);

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
    ConceptDTO saveCode(CodeSnippet codeSnippet);

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
    void deleteCvJsonDirectoryAndContent(String path);

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
    VocabularyDTO getWithVersionsByNotationAndVersion(String notation, String slVersionNumber);

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
     * Get all of the published CV JSON path in specific version by notation
     * @param notation
     * @param versionNumber
     * @return
     */
    Path getPublishedCvPath(String notation, String versionNumber);

    /**
     * Get all of the published CV JSON path (latest version by notation)
     * @param notation
     * @return
     */
    Path getPublishedCvPath(String notation);

    /**
     * Get all of the published CVs JSON path
     */
    List<Path> getPublishedCvPaths();

    /**
     * Perform indexing in all published vocabularies
     */
    void indexAllPublished();

    /**
     * Perform indexing in a published vocabulary given JSON file path
     *
     * @param jsonPath the VocabularyDTO JSON file path
     */
    void indexPublished( Path jsonPath );

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
     * @throws IOException
     */
    String generateJsonAllVocabularyPublish() throws IOException;

    /**
     * Generate JSON files for published vocabularies
     *
     * @param vocabularies the published vocabularyDTOs array
     * @throws IOException
     */
    String generateJsonVocabularyPublish( VocabularyDTO... vocabularies ) throws IOException;

    /**
     * Generate files to be exported for specific vocabulary
     *
     * @param vocabularyNotation the vocabulary notation
     * @param versionSl the Source Language version
     * @param languageVersion combination of language anf version number. e.g en_1.0
     * @param downloadType one of the following file types PDF, DOCX, HTML, RDF
     * @param request HttpServletRequest for statistical purpose
     * @return the generated file
     */
    File generateVocabularyPublishFileDownload(String vocabularyNotation, String versionSl, String languageVersion, ExportService.DownloadType downloadType, HttpServletRequest request);

    /**
     * Generate files to be exported for specific vocabulary from the editor
     *
     * @param vocabularyNotation the vocabulary notation
     * @param versionSl the Source Language version
     * @param languageVersion combination of language anf version number. e.g en_1.0
     * @param downloadType one of the following file types PDF, DOCX, HTML, RDF
     * @param request HttpServletRequest for statistical purpose
     * @return the generated file
     */
    File generateVocabularyEditorFileDownload(String vocabularyNotation, String versionSl, String languageVersion, ExportService.DownloadType downloadType, HttpServletRequest request);

    /**
     * Filter-out vocabularyDTO.versions based on versionList e.g. (en-1.0, fr-1.0.1). Includes all if versionList is null
     * @param versionList
     * @param vocabularyDTO
     * @return
     */
    Set<VersionDTO> filterOutVocabularyVersions(String versionList, VocabularyDTO vocabularyDTO);


    /**
     *
     * @param agencyId
     * @param agencyUri
     * @param agencyUriCode
     */
    void updateVocabularyUri( Long agencyId, String agencyUri, String agencyUriCode );
}
