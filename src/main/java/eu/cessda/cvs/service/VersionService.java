package eu.cessda.cvs.service;

import eu.cessda.cvs.service.dto.VersionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link eu.cessda.cvs.domain.Version}.
 */
public interface VersionService {

    /**
     * Save a version.
     *
     * @param versionDTO the entity to save.
     * @return the persisted entity.
     */
    VersionDTO save(VersionDTO versionDTO);

    /**
     * Get all the versions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VersionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" version.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VersionDTO> findOne(Long id);

    /**
     * Delete the "id" version.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Find all versions by vocabularyId
     *
     * @param vocabularyId
     * @return
     */
    List<VersionDTO> findAllByVocabulary(Long vocabularyId);

    /**
     * Find all published versions by vocabularyId
     *
     * @param vocabularyId
     * @return
     */
    List<VersionDTO> findAllPublishedByVocabulary(Long vocabularyId);

    /**
     * Find all older published version than given versionId, from specific vocabulary and language
     *
     * @param vocabularyId
     * @param languageIso
     * @param versionId
     * @return
     */
    List<VersionDTO> findOlderPublishedByVocabularyLanguageId(Long vocabularyId, String languageIso, Long versionId);

    /**
     * Find all versions than given versionId, from specific vocabulary and language
     *
     * @param vocabularyId
     * @param versionNumberSl
     * @return
     */
    List<VersionDTO> findAllByVocabularyAnyVersionSl(Long vocabularyId, String versionNumberSl);

    /**
     * Search for the version corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VersionDTO> search(String query, Pageable pageable);

    /**
     * Get list of versions by URN
     * @param urn
     * @return
     */
    List<VersionDTO> findByUrn(String urn);

    /**
     * Get list of versions by URN that starting with
     * @param urn
     * @return
     */
    List<VersionDTO> findByUrnStartingWith(String urn);

}
