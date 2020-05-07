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
     * get Vocabulary by notation
     *
     * @param notation the vocabulary notation
     * @return the vocabulary
     */
    VocabularyDTO getByNotation(String notation);

    /**
     * get Vocabulary by notation with the specific versions
     *
     * @param notation the vocabulary notation
     * @param slVersionNumber the version number of available SL version
     * @return the vocabulary with versions based on slVersionNumber
     */
    VocabularyDTO getWithVersionsByNotationAndVersion(String notation, String slVersionNumber);

    /**
     * Withdraw specific vocabulary
     *
     * @param vocabulary
     * @return
     */
    VocabularyDTO withdraw( VocabularyDTO vocabulary );

    /**
     * Restore vocabulary from withdraw state
     *
     * @param vocabulary
     * @return
     */
    VocabularyDTO restore( VocabularyDTO vocabulary );

    /**
     * Get all the withdrawn vocabularies.
     *
     * @param pageable
     * @return the list of withdrawn Vocabulary entities.
     */
    Page<VocabularyDTO> findAllWithdrawn(Pageable pageable);

    /**
     * Get all the withdrawn vocabularies in specific agency.
     *
     * @param agencyId the agencyId information.
     * @param pageable the pagination information.
     * @return the list of withdrawn Vocabulary entities.
     */
    Page<VocabularyDTO> findAllWithdrawn(Long agencyId, Pageable pageable);

    /**
     * Perform entire Vocabularies ElasticSearch indexing for both published and editor
     *
     */
    void doIndexingEditorAndPublicationCvs( boolean force );

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
     * Perform indexing in a vocabulary in the Editor
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
     * Generate JSON files for all published vocabularies.
     *
     * @throws IOException
     */
    void generateJsonAllVocabularyPublish() throws IOException;

    /**
     * Generate JSON files for published vocabularies
     *
     * @param vocabularies the published vocabularyDTOs array
     * @throws IOException
     */
    void generateJsonVocabularyPublish( VocabularyDTO... vocabularies ) throws IOException;

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
}
