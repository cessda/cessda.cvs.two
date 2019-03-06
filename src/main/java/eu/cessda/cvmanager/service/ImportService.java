package eu.cessda.cvmanager.service;

import java.util.Map;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.service.dto.VersionDTO;

/**
 * Service Interface for managing Version.
 */
public interface ImportService {

    /**
     * Import all CVs from Stardat DDI-flatDB to the CV-Manager
     */
	void importFromStardat();

	Map<String, Object> createCvBatch( Cv... cvs);

	/**
	 * Create a new Vocabulary and a new Version for SL
	 * and a new Version for TL, depending the cv.type status.
	 *
	 * The newly generated SL version will have a default 2 sections
	 * version number of 1.0, unless the Cv "version" attribute has different value.
	 * The TL version number will have 3 sections version number and it will follow
	 * the SL version number. E.g 1.0.1
	 *
	 * The newly created Cv will be published directly.
	 *
	 * The Vocabulary Service linear versioning is not yet supported
	 *
	 * @param cv
	 * @return The newly created VersionDTO
	 */
	VersionDTO createCv( Cv cv);

	/**
	 * Update Version entity for either SL or TL, the Version will be updated,
	 * based on either by the URI attribute or the code and version number.
	 *
	 * @param cv
	 * @return The updated VersionDTO
	 */
	VersionDTO updateCv( Cv cv);

	/**
	 * Set/Replace the Set of Concepts from a specific Version
	 *
	 * @param versionId the id of the version
	 * @param cvCodes the concepts for replacing the old concepts
	 *
	 * @return VersionDTO
	 */
	VersionDTO setCvCode( Long versionId, CvCode... cvCodes);

	/**
	 * Set/Replace the Set of Concepts from a specific Version
	 *
	 * @param uri the uri of the version
	 * @param cvCodes the concepts for replacing the old concepts
	 *
	 * @return VersionDTO
	 */
	VersionDTO setCvCode( String uri, CvCode... cvCodes);
}
