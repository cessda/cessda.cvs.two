/*
 * Copyright © 2017-2022 CESSDA ERIC (support@cessda.eu)
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

package eu.cessda.cvs.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.domain.search.VocabularyEditor;
import eu.cessda.cvs.domain.search.VocabularyPublish;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.LicenceRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.repository.search.AgencyStatSearchRepository;
import eu.cessda.cvs.repository.search.VocabularyEditorSearchRepository;
import eu.cessda.cvs.repository.search.VocabularyPublishSearchRepository;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.*;
import eu.cessda.cvs.service.dto.*;
import eu.cessda.cvs.service.mapper.VocabularyEditorMapper;
import eu.cessda.cvs.service.mapper.VocabularyMapper;
import eu.cessda.cvs.service.mapper.VocabularyPublishMapper;
import eu.cessda.cvs.service.search.EsFilter;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.utils.VersionNumber;
import eu.cessda.cvs.utils.VocabularyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service Implementation for managing {@link Vocabulary}.
 */
@Service
@Transactional
public class VocabularyServiceImpl implements VocabularyService
{

	private static final Logger log = LoggerFactory.getLogger( VocabularyServiceImpl.class );

	private static final String VOCABULARYPUBLISH = "vocabularypublish";

	private static final String UNABLE_TO_FIND_VERSION = "Unable to find version with Id ";
	private static final String UNABLE_TO_FIND_VOCABULARY = "Unable to find vocabulary with Id ";

	public static final String COUNT = "_count";
	public static final String TITLE = "title";
	public static final String DEFINITION = "definition";
	public static final String NOTATION = "notation";
	public static final String HIGHLIGHT_START = "<span class=\"highlight\">";
	public static final String HIGHLIGHT_END = "</span>";
	public static final int SIZE_OF_ITEMS_ON_AGGREGATION = 10000;
	public static final String JSON_FORMAT = ".json";
	public static final String UNABLE_FIND_VOCABULARY = "Unable to find vocabulary with Id ";
	public static final String UNABLE_FIND_VERSION = "Unable to find version with Id ";
	public static final String LATEST = "latest";
	public static final String ALL = "all";

	private static final String CODE_PATH = "codes";

	private final AgencyRepository agencyRepository;

	private final ConceptService conceptService;

	private final ExportService exportService;

	private final LicenceRepository licenceRepository;

	private final VersionService versionService;

	private final VocabularyChangeService vocabularyChangeService;

	private final VocabularyRepository vocabularyRepository;

	private final VocabularyMapper vocabularyMapper;

	private final VocabularyEditorMapper vocabularyEditorMapper;

	private final VocabularyPublishMapper vocabularyPublishMapper;

	private final VocabularyEditorSearchRepository vocabularyEditorSearchRepository;

	private final VocabularyPublishSearchRepository vocabularyPublishSearchRepository;

	private final ElasticsearchTemplate elasticsearchTemplate;

	private final ApplicationProperties applicationProperties;

	private final AgencyStatSearchRepository agencyStatSearchRepository;

	@SuppressWarnings( "squid:S107" ) // since constructor params are autowired
	public VocabularyServiceImpl( AgencyRepository agencyRepository, ConceptService conceptService, ExportService exportService,
			LicenceRepository licenceRepository, VersionService versionService,
			VocabularyChangeService vocabularyChangeService, VocabularyRepository vocabularyRepository,
			VocabularyMapper vocabularyMapper, VocabularyEditorMapper vocabularyEditorMapper,
			VocabularyPublishMapper vocabularyPublishMapper, VocabularyEditorSearchRepository vocabularyEditorSearchRepository,
			VocabularyPublishSearchRepository vocabularyPublishSearchRepository,
			ElasticsearchTemplate elasticsearchTemplate, ApplicationProperties applicationProperties,
			AgencyStatSearchRepository agencyStatSearchRepository )
	{
		this.agencyRepository = agencyRepository;
		this.conceptService = conceptService;
		this.exportService = exportService;
		this.licenceRepository = licenceRepository;
		this.versionService = versionService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.vocabularyRepository = vocabularyRepository;
		this.vocabularyMapper = vocabularyMapper;
		this.vocabularyEditorMapper = vocabularyEditorMapper;
		this.vocabularyPublishMapper = vocabularyPublishMapper;
		this.vocabularyEditorSearchRepository = vocabularyEditorSearchRepository;
		this.vocabularyPublishSearchRepository = vocabularyPublishSearchRepository;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.applicationProperties = applicationProperties;
		this.agencyStatSearchRepository = agencyStatSearchRepository;
	}

	/**
	 * Save a vocabulary.
	 *
	 * @param vocabularyDTO
	 *            the entity to save.
	 * @return the persisted entity.
	 */
	@Override
	public VocabularyDTO save( VocabularyDTO vocabularyDTO )
	{
		log.debug( "Request to save Vocabulary : {}", vocabularyDTO );
		Vocabulary vocabulary;
		// for new vocabulary
		if ( !vocabularyDTO.isPersisted() )
		{
			vocabulary = createNewVocabulary( vocabularyDTO );
		}
		// if updated vocabulary
		else
		{
			vocabulary = updateVocabulary( vocabularyDTO );
		}
		vocabularyDTO = vocabularyMapper.toDto( vocabulary );
		// indexing
		indexEditor( vocabularyDTO );
		return vocabularyDTO;
	}

	private Vocabulary updateVocabulary( VocabularyDTO vocabularyDTO )
	{
		return vocabularyRepository.save( vocabularyMapper.toEntity( vocabularyDTO ) );
	}

	@Override
	public VocabularyDTO saveVocabulary( VocabularySnippet vocabularySnippet )
	{
		if ( vocabularySnippet.getActionType().equals( ActionType.CREATE_CV ) )
		{
			return save( new VocabularyDTO( vocabularySnippet ) );
		}
		else if ( vocabularySnippet.getActionType().equals( ActionType.ADD_TL_CV ) )
		{
			return saveTlVocabulary( vocabularySnippet );
		}
		else
		{
			// get Vocabulary from database
			VocabularyDTO vocabularyDTO = findOne( vocabularySnippet.getVocabularyId() )
					.orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVocabularyId() ) );

			// find version on vocabulary
			VersionDTO versionDTO = vocabularyDTO.getVersions().stream()
					.filter( v -> v.getId().equals( vocabularySnippet.getVersionId() ) ).findFirst()
					.orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVersionId() ) );

			if ( vocabularySnippet.getActionType().equals( ActionType.EDIT_CV ) )
			{
				// check if user authorized to edit VocabularyResource
				SecurityUtils.checkResourceAuthorization( ActionType.EDIT_CV,
						vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
				// set non-editable (via edit-cv-form) versionNumber and language on
				// vocabularySnippet with versionDTO
				vocabularySnippet.setLanguage( versionDTO.getLanguage() );
				vocabularySnippet.setVersionNumber( versionDTO.getNumber() );
				// only title, definition, notes, translateAgency, translateAgencyLink
				vocabularyDTO.setContentByVocabularySnippet( vocabularySnippet );
				versionDTO.setContentByVocabularySnippet( vocabularySnippet );

				// check if codeSnippet contains changeType
				storeChangeType( vocabularySnippet, versionDTO );
			}
			else if ( vocabularySnippet.getActionType().equals( ActionType.EDIT_DDI_CV ) )
			{
				SecurityUtils.checkResourceAuthorization( ActionType.EDIT_DDI_CV,
						vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

				versionDTO.setDdiUsage( vocabularySnippet.getDdiUsage() );
			}
			else if ( vocabularySnippet.getActionType().equals( ActionType.EDIT_VERSION_INFO_CV ) )
			{
				SecurityUtils.checkResourceAuthorization( ActionType.EDIT_VERSION_INFO_CV,
						vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

				updateCvVersionInfo( vocabularySnippet, versionDTO );
			}
			else
			{
				SecurityUtils.checkResourceAuthorization( ActionType.EDIT_NOTE_CV,
						vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );

				versionDTO.setNotes( vocabularySnippet.getNotes() );
			}

			vocabularyDTO = save( vocabularyDTO );

			// regenerate json file is version already published
			if ( versionDTO.getStatus().equals( Status.PUBLISHED.toString() ) )
			{
				generateJsonVocabularyPublish( vocabularyDTO );
			}
			return vocabularyDTO;
		}
	}

	private void updateCvVersionInfo( VocabularySnippet vocabularySnippet, VersionDTO versionDTO )
	{
		versionDTO.setVersionNotes( vocabularySnippet.getVersionNotes() );
		versionDTO.setVersionChanges( vocabularySnippet.getVersionChanges() );

		if ( vocabularySnippet.getVersionNumber() != null )
			versionDTO.setNumber( vocabularySnippet.getVersionNumber() );
		if ( vocabularySnippet.getLicenseId() != null )
			versionDTO.setLicenseId( vocabularySnippet.getLicenseId() );
	}

	@Override
	public VersionDTO createNewVersion(Long prevVersionId)
	{
		log.debug( "Request to create new Vocabulary version from existing version with ID : {}", prevVersionId );
		
		// get version
		VersionDTO prevVersionDTO = versionService
			.findOne( prevVersionId )
			.orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VERSION + prevVersionId));

		if (!prevVersionDTO.getStatus().equals(Status.PUBLISHED.toString())) {
			log.error("Unable to create new version from version with ID {}, version is not yet PUBLISHED", prevVersionDTO.getId());
			throw new IllegalArgumentException(
				"Unable to create new version from version with ID "
				+ prevVersionDTO.getId()
				+ ", version is not yet PUBLISHED"
			);
		}

		VocabularyDTO vocabularyDTO = findOne(prevVersionDTO.getVocabularyId())
			.orElseThrow(() -> new EntityNotFoundException(UNABLE_FIND_VOCABULARY + prevVersionDTO.getVocabularyId()));

		VersionDTO currentSlVersion = vocabularyDTO
			.getVersions()
			.stream()
			.filter(v -> v.getItemType().equals(ItemType.SL.toString()))
			.filter(v -> v.getNumber().equals(vocabularyDTO.getVersionNumber()))
			.findFirst()
			.orElseThrow(() -> new EntityNotFoundException(
				UNABLE_FIND_VERSION + "SL version number " + vocabularyDTO.getVersionNumber()
			));

		VersionDTO newVersion = null;
		if (prevVersionDTO.getItemType().equals(ItemType.SL.toString())){
			// check if user authorized to create new SL version
			SecurityUtils.checkResourceAuthorization(
				ActionType.CREATE_NEW_CV_SL_VERSION,
				vocabularyDTO.getAgencyId(),
				prevVersionDTO.getLanguage()
			);
			newVersion = new VersionDTO(prevVersionDTO, null);
			// update vocabularyDTO
			vocabularyDTO.setVersionNumber(newVersion.getNumber());
			vocabularyDTO.setStatus(Status.DRAFT.toString());
		} else {
			// check if user authorized to create new TL version
			SecurityUtils.checkResourceAuthorization(
				ActionType.CREATE_NEW_CV_TL_VERSION,
				vocabularyDTO.getAgencyId(),
				prevVersionDTO.getLanguage()
			);
			newVersion = new VersionDTO(prevVersionDTO, currentSlVersion);
		}

		newVersion.setCreator(SecurityUtils.getCurrentUserId());
		newVersion = versionService.save( newVersion );

		final VersionDTO finalNewVersion = newVersion;
		currentSlVersion
			.getConcepts()
			.forEach(conceptSlDTO -> {
				ConceptDTO conceptDTO;
				if (finalNewVersion.getItemType().equals( ItemType.SL.toString())) {
					conceptDTO = new ConceptDTO( conceptSlDTO, conceptSlDTO, finalNewVersion.getId() );
				} else {
					ConceptDTO prevConcept = prevVersionDTO
						.getConcepts()
						.stream()
						.filter(c -> c.getNotation().equals(conceptSlDTO.getNotation()))
						.findFirst()
						.orElse(null);
					conceptDTO = new ConceptDTO(conceptSlDTO, prevConcept, finalNewVersion.getId());
				}
				finalNewVersion.addConcept(conceptDTO);
			});
		// save and reindex
		addVersionAndSaveIndexVocabulary( vocabularyDTO, finalNewVersion );
		return finalNewVersion;
	}

	private void addVersionAndSaveIndexVocabulary( VocabularyDTO vocabularyDTO, VersionDTO finalNewVersion )
	{
		// linked vocabulary and version
		vocabularyDTO.addVersion( finalNewVersion );
		// at the end save (version will automatically saved with Cascade)
		Vocabulary vocabulary = vocabularyMapper.toEntity( vocabularyDTO );
		vocabulary = vocabularyRepository.save( vocabulary );
		vocabularyDTO = vocabularyMapper.toDto( vocabulary );
		// index editor
		indexEditor( vocabularyDTO );
	}

	private VocabularyDTO saveTlVocabulary( VocabularySnippet vocabularySnippet )
	{
		// get Vocabulary from database
		VocabularyDTO vocabularyDTO = findOne( vocabularySnippet.getVocabularyId() )
				.orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVocabularyId() ) );
		// get slVersion from vocabularySnippet
		VersionDTO versionSlDTO = vocabularyDTO.getVersions().stream().filter( v -> v.getId().equals( vocabularySnippet.getVersionSlId() ) )
				.findFirst()
				.orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + vocabularySnippet.getVersionSlId() ) );

		// create new tl version by vocabularySnippet
		VersionDTO versionDTO = new VersionDTO( vocabularySnippet, versionSlDTO );
		// find previous version with same language if any
		VersionDTO prevVersionTlDTO = null;
		Optional<VersionDTO> prevVersionTlDTOOptl = vocabularyDTO.getVersions().stream()
				.filter( v -> v.getLanguage().equals( vocabularySnippet.getLanguage() ) ).findFirst();
		if ( prevVersionTlDTOOptl.isPresent() )
		{
			prevVersionTlDTO = prevVersionTlDTOOptl.get();
			versionDTO.setPreviousVersion( prevVersionTlDTO.getId() );
			versionDTO.setInitialVersion(
					prevVersionTlDTO.getInitialVersion() == null ? prevVersionTlDTO.getId() : prevVersionTlDTO.getInitialVersion() );
		}
		versionDTO.setCreator( SecurityUtils.getCurrentUserId() );
		// copy DDI-Usage of SL to TL
		versionDTO.setDdiUsage( versionSlDTO.getDdiUsage() );
		versionDTO = versionService.save( versionDTO );
		// generate concepts from currentSlconcept and previousVersion
		generateConceptFromSlAndPrevVersion( versionDTO, versionSlDTO, prevVersionTlDTO );
		// save and reindex
		addVersionAndSaveIndexVocabulary( vocabularyDTO, versionDTO );
		return vocabularyDTO;
	}

	private void generateConceptFromSlAndPrevVersion( VersionDTO versionDTO, VersionDTO versionSlDTO, VersionDTO prevVersionTlDTO )
	{
		versionSlDTO.getConcepts().forEach( conceptSlDTO ->
		{
			ConceptDTO conceptDTO = new ConceptDTO( conceptSlDTO );
			conceptDTO.setVersionId( versionDTO.getId() );
			// simply fill with prevVersionTlDTO if exist
			// if on SL notation is changed then the prevConcept will not be linked anymore
			// (Improvement in future lo linked the prev concept with modified notation?)
			if ( prevVersionTlDTO != null )
			{
				prevVersionTlDTO.getConcepts().stream().filter( c -> c.getNotation().equals( conceptDTO.getNotation() ) )
						.findFirst().ifPresent( cPrev ->
						{
							conceptDTO.setPreviousConcept( cPrev.getId() );
							conceptDTO.setTitle( cPrev.getTitle() );
							conceptDTO.setDefinition( cPrev.getDefinition() );
						} );
			}
			// add concept to version
			versionDTO.addConcept( conceptDTO );
		} );
	}

	private Vocabulary createNewVocabulary( VocabularyDTO vocabularyDTO )
	{
		// check if Vocabulary already exist for new vocabulary
		if ( vocabularyRepository.existsByNotation( vocabularyDTO.getNotation() ) )
		{
			throw new VocabularyAlreadyExistException();
		}

		// query agency
		final Agency agency = agencyRepository.getOne( vocabularyDTO.getAgencyId() );

		// set Vocabulary attribute for initial version
		vocabularyDTO.setStatus( Status.DRAFT.toString() );
		vocabularyDTO.setAgencyLink( agency.getLink() );
		vocabularyDTO.setAgencyName( agency.getName() );
		vocabularyDTO.setAgencyLogo( agency.getLogopath() );

		// create a new version by vocabularyDTO
		VersionDTO versionDTO = new VersionDTO( vocabularyDTO );
		vocabularyDTO.addVersion( versionDTO );
		// add more version details
		versionDTO.setCreator( SecurityUtils.getCurrentUserId() );

		// save vocabulary
		Vocabulary vocabulary = vocabularyMapper.toEntity( vocabularyDTO );
		Version initialSlVersion = vocabulary.getVersions().iterator().next();
		initialSlVersion.setVocabulary( vocabulary );
		// at the end save (version will automatically saved with Cascade)
		vocabulary = vocabularyRepository.save( vocabulary );
		return vocabulary;
	}

	/**
	 * Save a concept by codeSnippet
	 *
	 * @param codeSnippet
	 *            the minimized version of Code and Version information
	 * @return saved conceptDTO
	 */
	@Override
	public ConceptDTO saveCode( CodeSnippet codeSnippet )
	{
		// if we add code we need to ensure that it will be clonned to every TL probably as the workflow had been changed!!!
		log.debug( "Request to save Concept and Version from CodeSnippet : {}", codeSnippet.getConceptId() );
		ConceptDTO conceptDTO = null;
		// get version
		VersionDTO versionDTO = versionService.findOne( codeSnippet.getVersionId() )
				.orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VERSION + codeSnippet.getVersionId() ) );

		// reject if version status is published
		if ( versionDTO.getStatus().equals( Status.PUBLISHED.toString() ) )
		{
			throw new IllegalArgumentException( "Unable to add Code " + codeSnippet.getNotation() + ", Version is already PUBLISHED" );
		}

		VocabularyDTO vocabularyDTO = findOne( versionDTO.getVocabularyId() )
				.orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VOCABULARY + versionDTO.getVocabularyId() ) );

		if ( codeSnippet.getActionType().equals( ActionType.CREATE_CODE ) )
		{
			conceptDTO = createCode( codeSnippet, versionDTO, vocabularyDTO );
		}
		else if ( codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) ||
				codeSnippet.getActionType().equals( ActionType.ADD_TL_CODE ) ||
				codeSnippet.getActionType().equals( ActionType.EDIT_TL_CODE ) ||
				codeSnippet.getActionType().equals( ActionType.DELETE_TL_CODE ) )
		{
			conceptDTO = editCode( codeSnippet, versionDTO, vocabularyDTO );
		}
		return conceptDTO;
	}

	private ConceptDTO editCode( CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO )
	{
		ConceptDTO conceptDTO;
		// check for authorization
		checkEditCodeAuthorization( codeSnippet, versionDTO, vocabularyDTO );
		// get concept from versionDTO
		conceptDTO = versionDTO.getConcepts().stream().filter( c -> c.getId().equals( codeSnippet.getConceptId() ) ).findFirst()
				.orElseThrow( () -> new EntityNotFoundException( "Unable to find concept with Id " + codeSnippet.getConceptId() ) );

		// check duplicated code notation
		if ( codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) && !conceptDTO.getNotation().equals( codeSnippet.getNotation() ) )
		{
			if ( versionDTO.getConcepts().stream()
					.anyMatch( c -> c.getNotation().equals( codeSnippet.getNotation() ) ) )
			{
				throw new CodeAlreadyExistException();
			}

			// check if this concept influence parent and child concepts
			for ( ConceptDTO next : versionDTO.getConcepts() )
			{
				if ( next.getParent() != null && next.getParent().startsWith( conceptDTO.getNotation() ) )
				{
					next.setParent( next.getParent().replace( conceptDTO.getNotation(), codeSnippet.getNotation() ) );
					next.setNotation( next.getNotation().replace( conceptDTO.getNotation(), codeSnippet.getNotation() ) );
				}
			}

			// change current concept notation property
			conceptDTO.setNotation( codeSnippet.getNotation() );
		}

		if ( codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) ||
				codeSnippet.getActionType().equals( ActionType.ADD_TL_CODE ) ||
				codeSnippet.getActionType().equals( ActionType.EDIT_TL_CODE ) )
		{
			conceptDTO.setTitle( codeSnippet.getTitle() );
			conceptDTO.setDefinition( codeSnippet.getDefinition() );
		}
		else if ( codeSnippet.getActionType().equals( ActionType.DELETE_TL_CODE ) )
		{
			conceptDTO.setTitle( null );
			conceptDTO.setDefinition( null );
		}

		// save versionDTO together with concepts
		versionDTO = versionService.save( versionDTO );

		// check if codeSnippet contains changeType, store if exist
		storeChangeType( codeSnippet, versionDTO );

		// find the newly created code from version
		conceptDTO = versionDTO.findConceptByNotation( conceptDTO.getNotation() );

		// index editor
		indexEditor( vocabularyDTO );
		return conceptDTO;
	}

	private void storeChangeType( VocabularySnippet vocabularySnippet, VersionDTO versionDTO )
	{
		if ( vocabularySnippet.getChangeType() != null )
		{
			vocabularySnippet.setVersionId( versionDTO.getId() );
			VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO( vocabularySnippet, SecurityUtils.getCurrentUser(),
					versionDTO.getVocabularyId() );
			vocabularyChangeService.save( vocabularyChangeDTO );
		}
	}

	public void storeChangeType( CodeSnippet codeSnippet, VersionDTO versionDTO )
	{
		if ( codeSnippet.getChangeType() != null && !versionDTO.isInitialVersion() )
		{
			codeSnippet.setVersionId( versionDTO.getId() );
			VocabularyChangeDTO vocabularyChangeDTO = new VocabularyChangeDTO( codeSnippet, SecurityUtils.getCurrentUser(),
					versionDTO.getVocabularyId() );
			vocabularyChangeService.save( vocabularyChangeDTO );
		}
	}

	private void checkEditCodeAuthorization( CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO )
	{
		if ( codeSnippet.getActionType().equals( ActionType.EDIT_CODE ) )
			SecurityUtils.checkResourceAuthorization( ActionType.EDIT_CODE,
					vocabularyDTO.getAgencyId(), versionDTO.getLanguage() );
		else if ( codeSnippet.getActionType().equals( ActionType.ADD_TL_CODE ) )
			SecurityUtils.checkResourceAuthorization( ActionType.ADD_TL_CODE,
					vocabularyDTO.getAgencyId(), versionDTO.getLanguage() );
		else if ( codeSnippet.getActionType().equals( ActionType.EDIT_TL_CODE ) )
			SecurityUtils.checkResourceAuthorization( ActionType.EDIT_TL_CODE,
					vocabularyDTO.getAgencyId(), versionDTO.getLanguage() );
		else if ( codeSnippet.getActionType().equals( ActionType.DELETE_TL_CODE ) )
			SecurityUtils.checkResourceAuthorization( ActionType.DELETE_TL_CODE,
					vocabularyDTO.getAgencyId(), versionDTO.getLanguage() );
	}

	private Integer getConceptPositionById( Long conceptId, Set<ConceptDTO> concepts )
	{
		if ( conceptId == null )
		{
			return null;
		}
		ConceptDTO conceptDTO = concepts.stream()
				.filter( c -> c.getId().compareTo( conceptId ) == 0 ).findFirst().orElse( null );
		return conceptDTO != null ? conceptDTO.getPosition() : null;
	}

	private ConceptDTO createCode( CodeSnippet codeSnippet, VersionDTO versionDTO, VocabularyDTO vocabularyDTO )
	{
		ConceptDTO conceptDTO;
		// check for authorization
		SecurityUtils.checkResourceAuthorization( ActionType.CREATE_CODE,
				vocabularyDTO.getAgencyId(), versionDTO.getLanguage() );

		// check if concept already exist for new concept
		if ( versionDTO.getConcepts().stream()
				.anyMatch( c -> c.getNotation().equals( codeSnippet.getNotation() ) ) )
		{
			throw new CodeAlreadyExistException();
		}
		// ==> #349: calculate code position at the server side instead of the client side
		Integer refConceptPos = getConceptPositionById( codeSnippet.getInsertionRefConceptId(), versionDTO.getConcepts() );
		if ( refConceptPos == null )
		{
			refConceptPos = versionDTO.getConcepts().size();
		}
		else
		{
			Integer relPosToRefConcept = codeSnippet.getRelPosToRefConcept();
			relPosToRefConcept = Math.min( 1, relPosToRefConcept == null ? 0 : Math.max( 0, relPosToRefConcept ) );
			refConceptPos += relPosToRefConcept;
		}
		codeSnippet.setPosition( refConceptPos );
		// <== #349
		// create concept by codeSnippet
		ConceptDTO newConceptDTO = new ConceptDTO( codeSnippet );
		// add concept to version and save version to save new concept
		versionDTO.addConceptAt( newConceptDTO, newConceptDTO.getPosition() );
		versionDTO = versionService.save( versionDTO );

		// check if codeSnippet contains changeType, store if exist
		storeChangeType( codeSnippet, versionDTO );

		// find the newly created code from version
		conceptDTO = versionDTO.findConceptByNotation( newConceptDTO.getNotation() );

		// index editor
		indexEditor( vocabularyDTO );
		return conceptDTO;
	}

	/**
	 * Get all the vocabularies.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional( readOnly = true )
	public List<VocabularyDTO> findAll()
	{
		log.debug( "Request to get all Vocabularies" );
		List<VocabularyDTO> vocabulariesDTO = vocabularyRepository.findAll().stream()
				.map( vocabularyMapper::toDto )
				.collect( Collectors.toCollection( LinkedList::new ) );

		sortVocabularyVersions( vocabulariesDTO );
		return vocabulariesDTO;
	}

	private void sortVocabularyVersions( List<VocabularyDTO> vocabulariesDTO )
	{
		for ( VocabularyDTO vocabularyDTO : vocabulariesDTO )
		{
			LinkedHashSet<VersionDTO> sortedVersion = vocabularyDTO.getVersions().stream().sorted( VocabularyUtils.versionDtoComparator() )
					.collect( Collectors.toCollection( LinkedHashSet::new ) );
			vocabularyDTO.setVersions( sortedVersion );
		}
	}

	/**
	 * Get all the vocabularies.
	 *
	 * @param pageable
	 *            the pagination information.
	 * @return the list of entities.
	 */
	@Override
	@Transactional( readOnly = true )
	public Page<VocabularyDTO> findAll( Pageable pageable )
	{
		log.debug( "Request to get all Vocabularies" );
		final Page<VocabularyDTO> vocabPage = vocabularyRepository.findAll( pageable ).map( vocabularyMapper::toDto );
		sortVocabularyVersions( vocabPage.getContent() );
		return vocabPage;

	}

	/**
	 * Get one vocabulary by id.
	 *
	 * @param id
	 *            the id of the entity.
	 * @return the entity.
	 */
	@Override
	@Transactional( readOnly = true )
	public Optional<VocabularyDTO> findOne( Long id )
	{
		log.debug( "Request to get Vocabulary : {}", id );
		return vocabularyRepository.findById( id )
				.map( vocabularyMapper::toDto );
	}

	/**
	 * Delete the vocabulary by id.
	 *
	 * @param id
	 *            the id of the entity.
	 */
	@Override
	public void delete( Long id )
	{
		log.debug( "Request to delete Vocabulary : {}", id );
		VocabularyDTO vocabularyDTO = findOne( id )
				.orElseThrow( () -> new EntityNotFoundException( UNABLE_FIND_VOCABULARY + id ) );
		String notation = vocabularyDTO.getNotation();

		// update/create agency publish
		agencyStatSearchRepository.findById( vocabularyDTO.getAgencyId() ).ifPresent( agencyPublish ->
		{
			agencyPublish.deleteVocabStat( vocabularyDTO.getNotation() );
			agencyStatSearchRepository.save( agencyPublish );
		} );

		vocabularyRepository.deleteById( id );
		// delete index
		vocabularyEditorSearchRepository.deleteById( id );
		vocabularyPublishSearchRepository.deleteById( id );
		// delete files if any
		deleteCvJsonDirectoryAndContent( applicationProperties.getVocabJsonPath() + notation );
	}

	public void deleteCvJsonDirectoryAndContent( String path )
	{
		File dirPath = new File( path );
		if ( dirPath.isDirectory() )
		{
			try
			{
				FileUtils.deleteDirectory( dirPath );
			}
			catch (IOException e)
			{
				log.error( "Unable to delete directory: {}", e.getMessage() );
			}
		}
	}

	@Override
	public VocabularyDTO getByNotation( String notation )
	{
		if ( notation == null || notation.isEmpty() )
		{
			log.error( "Error notation could not be empty or null" );
			throw new IllegalArgumentException( "Error notation could not be empty or null" );
		}
		log.debug( "Request to get Vocabulary by notation: {}", notation );
		final List<Vocabulary> vocabularies = vocabularyRepository.findAllByNotation( notation );
		if ( vocabularies.isEmpty() )
		{
			log.error( "Error vocabulary with notation {} does not exist", notation );
			throw new EntityNotFoundException( UNABLE_FIND_VOCABULARY + "or notation " + notation );
		}
		return vocabularyMapper.toDto( vocabularies.get( 0 ) );
	}

	@Override
	public VersionDTO cloneTl( VersionDTO currentVersionSl, VersionDTO prevVersionSl, VersionDTO prevVersionTl )
	{
		VersionDTO newTlVersion = new VersionDTO( prevVersionTl, currentVersionSl );
		newTlVersion.setCreator( SecurityUtils.getCurrentUserId() );
		// if previous TL version is not published yet, assign version as initial version
		if ( !prevVersionTl.getStatus().equals( Status.PUBLISHED.toString() ) )
		{
			newTlVersion.setPreviousVersion( null );
			newTlVersion.setInitialVersion( null );
		}
		newTlVersion = versionService.save( newTlVersion );

		VersionDTO finalNewTlVersion = newTlVersion;
		currentVersionSl.getConcepts().forEach( conceptSlDTO ->
		{
			ConceptDTO newConceptTl = new ConceptDTO( conceptSlDTO );
			newConceptTl.setVersionId( finalNewTlVersion.getId() );

			if ( conceptSlDTO.getPreviousConcept() != null )
			{
				// try to find concept title, definition from previous concept
				// first, try to find the previous concept. in this case the TL prev concept will be
				// found
				// if there is no change in the notation between prev and current SL concept
				ConceptDTO prevConceptTl = prevVersionTl.getConcepts().stream()
						.filter( c -> c.getNotation().equals( conceptSlDTO.getNotation() ) ).findFirst().orElse( null );
				// if not found, try to find old notation from the previous SL concept
				if ( prevConceptTl == null )
				{
					prevConceptTl = getPrevTlConceptByPrevSlNotation( prevVersionSl, prevVersionTl, conceptSlDTO, prevConceptTl );
				}
				// assign title and definition if previous TL concept found
				if ( prevConceptTl != null )
				{
					newConceptTl.setTitle( prevConceptTl.getTitle() );
					newConceptTl.setDefinition( prevConceptTl.getDefinition() );
					newConceptTl.setPreviousConcept( prevConceptTl.getId() );
					// if previous TL version is not published yet, assign concept as initial
					// concept
					if ( !prevVersionTl.getStatus().equals( Status.PUBLISHED.toString() ) )
					{
						newConceptTl.setPreviousConcept( null );
					}
					else
					{
						newConceptTl.setPreviousConcept( prevConceptTl.getId() );
					}
				}
			}

			finalNewTlVersion.addConcept( newConceptTl );
		} );

		// #423 -->
		List<VocabularyChangeDTO> currentChangeLog = vocabularyChangeService.findByVersionId(currentVersionSl.getId());
		currentChangeLog.forEach(change -> {
			change.setVersionId(finalNewTlVersion.getId());
			vocabularyChangeService.save(change);
		});
		finalNewTlVersion.setVersionChanges(currentVersionSl.getVersionChanges());
		// <-- #423
		
		return newTlVersion;
	}

	private ConceptDTO getPrevTlConceptByPrevSlNotation(
			VersionDTO prevVersionSl,
			VersionDTO prevVersionTl,
			ConceptDTO conceptSlDTO,
			ConceptDTO prevConceptTl )
	{
		String oldSlNotation = prevVersionSl.getConcepts().stream()
				.filter( c -> c.getId().equals( conceptSlDTO.getPreviousConcept() ) ).map( ConceptDTO::getNotation ).findFirst()
				.orElse( null );
		if ( oldSlNotation != null )
		{
			prevConceptTl = prevVersionTl.getConcepts().stream()
					.filter( c -> c.getNotation().equals( oldSlNotation ) ).findFirst().orElse( null );
		}
		return prevConceptTl;
	}

	@Override
	public VocabularyDTO getVocabularyByNotationAndVersion( String notation, String slVersionNumber, boolean onlyPublished )
	{
		// Parameter validation
		Objects.requireNonNull( slVersionNumber, "slVersionNumber number cannot be null" );
		if ( slVersionNumber.isEmpty() )
		{
			throw new IllegalArgumentException( "slVersionNumber cannot be empty" );
		}

		// get all available licenses
		final List<Licence> licenceList = licenceRepository.findAll();

		VocabularyDTO vocabulary = getByNotation( notation );
		final Agency agency = agencyRepository.getOne( vocabulary.getAgencyId() );
		vocabulary.setAgencyLink( agency.getLink() );
		vocabulary.setAgencyLogo( agency.getLogopath() );
		vocabulary.setAgencyLink( agency.getLink() );

		if ( !onlyPublished )
		{
			if ( slVersionNumber.equals( ALL ) )
				return vocabulary;
			else if ( slVersionNumber.equals( LATEST ) )
			{
				final VersionDTO latestSlVersion = vocabulary.getVersions().stream().sorted( VocabularyUtils.versionDtoComparator() )
						.findFirst().orElse( null );
				if ( latestSlVersion != null )
					slVersionNumber = latestSlVersion.getNumber().toString();

				getOneLatestVersionEachLanguage( VersionNumber.fromString(slVersionNumber), licenceList, vocabulary, true );
			}
		}
		else
		{
			preparePublishedVocabulary( slVersionNumber, vocabulary );
		}
		return vocabulary;
	}

	private void preparePublishedVocabulary( String slVersionNumber, VocabularyDTO vocabulary )
	{
		boolean findAllLatestVersion = false;
		if ( slVersionNumber.equals( LATEST ) )
		{
			findAllLatestVersion = true;
			final VersionDTO latestSlVersion = vocabulary.getVersions().stream()
					.filter( v -> v.getStatus().equals( Status.PUBLISHED.toString() ) )
					.sorted( VocabularyUtils.versionDtoComparator() )
					.findFirst().orElse( null );
			if ( latestSlVersion != null )
				slVersionNumber = latestSlVersion.getNumber().toString();
		}
		Set<VersionDTO> includedVersions = versionService.findAllPublishedByVocabularyAndVersionSl( vocabulary.getId(), VersionNumber.fromString(slVersionNumber) );

		if ( findAllLatestVersion )
		{
			Set<String> includedLangs = includedVersions.stream().map( VersionDTO::getLanguage ).collect( Collectors.toSet() );
			for ( VersionDTO version : versionService.findAllPublishedByVocabulary( vocabulary.getId() ) )
			{
				if ( !includedLangs.contains( version.getLanguage() ) )
				{
					includedLangs.add( version.getLanguage() );
					includedVersions.add( version );
				}
			}
		}
		// set included version
		vocabulary.setVersions( includedVersions );

		Map<Long, Licence> licenceMap = licenceRepository.findAll().stream()
				.collect( Collectors.toMap( Licence::getId, Function.identity() ) );

		for ( VersionDTO version : vocabulary.getVersions() )
		{
			prepareVersionAndConcept( licenceMap, version );
			addVersionHistories( vocabulary, version );
		}
	}

	private void getOneLatestVersionEachLanguage(
			VersionNumber slVersionNumber,
			List<Licence> licenceList,
			VocabularyDTO vocabulary,
			boolean isAddHistory )
	{
		Set<VersionDTO> versionDTOs = new LinkedHashSet<>();
		List<VersionDTO> versionsGroup = versionService.findAllByVocabularyAndVersionSl( vocabulary.getId(), slVersionNumber );

		Set<String> languages = new HashSet<>();
		for ( VersionDTO version : versionsGroup )
		{
			// check for more than one version in one language, only return latest one
			if ( languages.contains( version.getLanguage() ) )
				continue;
			languages.add( version.getLanguage() );

			// add version to new list and sort concepts
			LinkedHashSet<ConceptDTO> sortedConcepts = version.getConcepts().stream()
					.sorted( Comparator.comparing( ConceptDTO::getPosition ) ).collect( Collectors.toCollection( LinkedHashSet::new ) );
			version.setConcepts( sortedConcepts );

			// assign licence
			if ( licenceList != null )
			{
				licenceList.stream().filter( l -> l.getId().equals( version.getLicenseId() ) ).findFirst().ifPresent( l ->
				{
					version.setLicense( l.getAbbr() );
					version.setLicenseLink( l.getLink() );
					version.setLicenseName( l.getName() );
					version.setLicenseLogo( l.getLogoLink() );
				} );
			}

			// add history
			if ( isAddHistory )
				addVersionHistories( vocabulary, version );

			versionDTOs.add( version );
		}
		vocabulary.setVersions( versionDTOs );
	}

	@Override
	public void indexAllEditor()
	{
		log.info( "INDEXING ALL EDITOR VOCABULARIES START" );
		// remove any index
		vocabularyEditorSearchRepository.deleteAll();
		// index all vocabularies
		findAll().forEach( v ->
		{
			// skip for withdrawn vocabulary
			if ( Boolean.TRUE.equals( v.isWithdrawn() ) )
				return;
			indexEditor( v );
		} );
		log.info( "INDEXING ALL EDITOR VOCABULARIES FINISHED" );
	}

	@Override
	public void indexEditor( VocabularyDTO vocabulary )
	{
		log.info( "Indexing editor vocabulary with id {} and notation {}", vocabulary.getId(), vocabulary.getNotation() );
		// filter only include latest vocabulary
		// get latest version
		final VersionDTO maxSlVersion = vocabulary.getVersions().stream()
				.filter( v -> v.getItemType().equals( ItemType.SL.toString() ) )
				.max( ( v1, v2 ) -> v1.getNumber().compareTo(v2.getNumber()) )
				.orElse( null );

		if ( maxSlVersion == null )
			return;

		// on editor set Version to be latest SL of any version
		if ( !vocabulary.getVersionNumber().equals( maxSlVersion.getNumber() ) )
		{
			vocabulary.setVersionNumber( maxSlVersion.getNumber() );
		}

		getOneLatestVersionEachLanguage( maxSlVersion.getNumber(), null, vocabulary, false );

		// update/create agency publish
		agencyStatSearchRepository.findById( vocabulary.getAgencyId() ).ifPresent( agencyPublish ->
		{
			agencyPublish.updateVocabStat( vocabulary );
			agencyStatSearchRepository.save( agencyPublish );
		} );

		// fill vocabulary with versions
		VocabularyDTO.fillVocabularyByVersions( vocabulary, vocabulary.getVersions() );
		// fill CodeDTO object from versions
		vocabulary.setCodes( CodeDTO.generateCodesFromVersion( vocabulary.getVersions(), true ) );

		VocabularyEditor vocab = vocabularyEditorMapper.toEntity( vocabulary );
		vocabularyEditorSearchRepository.save( vocab );
	}

	@Override
	public void indexAllAgencyStats()
	{
		log.info( "INDEXING ALL AGENCY VOCABULARIES STATISTIC START" );
		findAll().stream().filter( v -> !Boolean.TRUE.equals( v.isWithdrawn() ) ).forEach( this::indexAgencyStats );
		log.info( "INDEXING ALL AGENCY VOCABULARIES STATISTIC FINISHED" );
	}

	@Override
	public void indexAgencyStats( VocabularyDTO vocabulary )
	{
		// update/create agency publish
		agencyStatSearchRepository.findById( vocabulary.getAgencyId() ).ifPresent( agencyPublish ->
		{
			agencyPublish.updateVocabStat( vocabulary );
			agencyStatSearchRepository.save( agencyPublish );
		} );
	}

	@Override
	public Path getPublishedCvPath( String notation, String versionNumber )
	{
		Path path = null;
		if ( versionNumber == null )
		{
			path = Paths.get( applicationProperties.getVocabJsonPath() + notation + File.separator + notation + JSON_FORMAT );
		}
		else
		{
			path = Paths.get( applicationProperties.getVocabJsonPath() + notation + File.separator +
					versionNumber + File.separator + notation + "_" + versionNumber + JSON_FORMAT );
		}
		if ( path == null )
			throw new VocabularyNotFoundException();
		return path;
	}

	@Override
	public Path getPublishedCvPath( String notation )
	{
		return getPublishedCvPath( notation, null );
	}

	@Override
	public void indexAllPublished()
	{
		log.info( "INDEXING ALL PUBLISHED VOCABULARIES START" );
		// remove any indexed vocabularies
		vocabularyPublishSearchRepository.deleteAll();
		// index all vocabularies
		findAll().forEach( v ->
		{
			// skip for withdrawn vocabulary
			if ( Boolean.TRUE.equals( v.isWithdrawn() ) )
				return;
			indexPublished( v );
		} );
		log.info( "INDEXING ALL PUBLISHED VOCABULARIES FINISHED" );
	}

	@Override
	public List<Path> getPublishedCvPaths()
	{
		try ( Stream<Path> paths = Files.walk( Paths.get( applicationProperties.getVocabJsonPath() ), 2 ) )
		{
			return paths.filter( Files::isRegularFile ).collect( Collectors.toList() );
		}
		catch (IOException e)
		{
			log.error( e.getMessage() );
			return Collections.emptyList();
		}
	}

	@Override
	public void indexPublished( Path jsonPath )
	{
		log.info( "Indexing published vocabulary with path {}", jsonPath );
		indexPublished( VocabularyUtils.generateVocabularyByPath( jsonPath ) );
	}

	@Override
	public void indexPublished( VocabularyDTO vocabulary )
	{
		log.info( "Indexing published vocabulary with id {} and notation {}", vocabulary.getId(), vocabulary.getNotation() );
		if ( Boolean.TRUE.equals( vocabulary.isWithdrawn() ) )
			return;
		// check if there is published version
		if ( vocabulary.getVersions().stream().noneMatch( v -> v.getStatus().equals( Status.PUBLISHED.toString() ) ) )
		{
			return;
		}

		setVocabularyWithLatestPublishedVersions( vocabulary );

		// fill vocabulary with versions
		VocabularyDTO.fillVocabularyByVersions( vocabulary, vocabulary.getVersions() );
		// fill CodeDTO object from versions
		vocabulary.setCodes( CodeDTO.generateCodesFromVersion( vocabulary.getVersions(), false ) );

		VocabularyPublish vocab = vocabularyPublishMapper.toEntity( vocabulary );
		vocabularyPublishSearchRepository.save( vocab );
	}

	private void setVocabularyWithLatestPublishedVersions( VocabularyDTO vocabulary )
	{
		final List<VersionDTO> allPublishedVersion = versionService.findAllPublishedByVocabulary( vocabulary.getId() );

		// collect all latest published version
		Set<VersionDTO> latestVersionsEachLangs = new LinkedHashSet<>();
		Set<String> publishedLangs = new HashSet<>();
		for ( VersionDTO version : allPublishedVersion )
		{
			// collect latest version across SL
			if ( !publishedLangs.contains( version.getLanguage() ) )
			{
				latestVersionsEachLangs.add( version );
				publishedLangs.add( version.getLanguage() );
			}
		}
		vocabulary.setVersions( latestVersionsEachLangs );
		vocabulary.setLanguagesPublished( publishedLangs );
	}

	@Override
	public EsQueryResultDetail search( EsQueryResultDetail esQueryResultDetail )
	{
		// get user keyword
		String searchTerm = esQueryResultDetail.getSearchTerm();
		String indiceType = VOCABULARYPUBLISH;

		// determine which language fields include into query
		if ( esQueryResultDetail.getSearchScope().equals( SearchScope.EDITORSEARCH ) )
		{
			indiceType = "vocabularyeditor";
		}
		// search on all fields
		List<String> languageFields = new ArrayList<>();
		if ( !esQueryResultDetail.isSearchAllLanguages() )
		{
			languageFields.add( StringUtils.capitalize( esQueryResultDetail.getSortLanguage() ) );
		}
		else
		{
			languageFields.addAll( Language.getCapitalizedIsos() );
		}

		// build query builder
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
				.withIndices( indiceType ).withTypes( indiceType )
				.withSearchType( SearchType.DEFAULT )
				.withQuery( generateMainAndNestedQuery( searchTerm, languageFields, esQueryResultDetail.getCodeSize() ) )
				.withFilter( generateFilterQuery( esQueryResultDetail.getEsFilters() ) )
				.withPageable( esQueryResultDetail.getPage() );

		// add highlighter after 2nd character typed
		boolean isSearchWithKeyword = searchTerm != null && !searchTerm.isEmpty() && searchTerm.length() > 2;
		if ( isSearchWithKeyword )
			searchQueryBuilder.withHighlightFields( generateHighlightBuilderMain( languageFields ) );

		// add aggregation
		setUpAggregrations( searchQueryBuilder, esQueryResultDetail );

		// at the end build search query
		SearchQuery searchQuery = searchQueryBuilder.build();

		// put the vocabulary results
		Page<VocabularyDTO> vocabularyPage;
		if ( esQueryResultDetail.getSearchScope().equals( SearchScope.EDITORSEARCH ) )
			vocabularyPage = elasticsearchTemplate.queryForPage( searchQuery, VocabularyEditor.class ).map( vocabularyEditorMapper::toDto );
		else
			vocabularyPage = elasticsearchTemplate.queryForPage( searchQuery, VocabularyPublish.class )
					.map( vocabularyPublishMapper::toDto );

		// get search response for aggregation, hits, inner hits and highlighter
		SearchResponse searchResponse = elasticsearchTemplate.query( searchQuery, response -> response );

		// assign aggregation to esQueryResultDetail
		generateAggregationFilter( esQueryResultDetail, searchResponse );

		// update vocabulary based on highlight and inner hit
		if ( isSearchWithKeyword )
			applySearchHitAndHighlight( vocabularyPage, searchResponse, true );
		else
		{
			// remove unnecessary nested code entities
			for ( VocabularyDTO vocab : vocabularyPage.getContent() )
				vocab.setCodes( Collections.emptySet() );
		}

		// set selected language in case language filter is selected with specific language
		setVocabularySelectedLanguage( esQueryResultDetail, vocabularyPage,
				esQueryResultDetail.getSearchScope().equals( SearchScope.EDITORSEARCH ) ? EsFilter.LANGS_AGG : EsFilter.LANGS_PUB_AGG );

		esQueryResultDetail.setVocabularies( vocabularyPage );

		return esQueryResultDetail;
	}

	@Override
	public EsQueryResultDetail searchCode( EsQueryResultDetail esQueryResultDetail )
	{
		// params
		String term = esQueryResultDetail.getSearchTerm();
		String language = StringUtils.capitalize( esQueryResultDetail.getSortLanguage() );

		// nested query for codes
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		if ( term.contains( "*" ) )
		{
			boolQuery
					.should( QueryBuilders.wildcardQuery( CODE_PATH + "." + NOTATION, term.toLowerCase().replace( " ", "" ) )
							.boost( 2.0f ) );
		}
		else
		{
			boolQuery
					.should( QueryBuilders.matchQuery( CODE_PATH + "." + TITLE + language, term ).fuzziness( 0.7 ).boost( 1.0f ) )
					.should( QueryBuilders.wildcardQuery( CODE_PATH + "." + NOTATION, term.toLowerCase().replace( " ", "" ) + "*" )
							.boost( 2.0f ) );
		}
		final NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.Total )
				.innerHit( new InnerHitBuilder( CODE_PATH ).setSize( esQueryResultDetail.getCodeSize() ) );

		// build query builder
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
				.withIndices( VOCABULARYPUBLISH ).withTypes( VOCABULARYPUBLISH )
				.withSearchType( SearchType.DEFAULT )
				.withQuery( nestedQueryBuilder )
				.withFilter( generateFilterQuery( esQueryResultDetail.getEsFilters() ) )
				.withPageable( esQueryResultDetail.getPage() );

		// add aggregations
		setUpAggregrations( searchQueryBuilder, esQueryResultDetail );

		// at the end build search query
		SearchQuery searchQuery = searchQueryBuilder.build();

		Page<VocabularyDTO> vocabularyPage = elasticsearchTemplate.queryForPage( searchQuery, VocabularyPublish.class )
				.map( vocabularyPublishMapper::toDto );
		SearchResponse searchResponse = elasticsearchTemplate.query( searchQuery, response -> response );

		for ( SearchHit hit : searchResponse.getHits() )
		{
			Optional<VocabularyDTO> vocabOpt = VocabularyDTO.findByIdFromList( vocabularyPage.getContent(), hit.getId() );
			if ( vocabOpt.isPresent() )
			{
				VocabularyDTO cvHit = vocabOpt.get();

				if ( hit.getInnerHits() == null )
					continue;

				applyCodeHighlighter( hit, cvHit, esQueryResultDetail.isWithHighlight() );
			}
		}

		generateAggregationFilter( esQueryResultDetail, searchResponse );
		esQueryResultDetail.setVocabularies( vocabularyPage );
		return esQueryResultDetail;
	}

	private void generateAggregationFilter( EsQueryResultDetail esQueryResultDetail, SearchResponse searchResponse )
	{
		// generate aggregation filter
		for ( String field : esQueryResultDetail.getAggFields() )
		{
			if ( !esQueryResultDetail.isAnyFilterActive() )
			{
				buildNonFilteredAggregration( esQueryResultDetail, searchResponse, field );
			}
			else
			{
				buildFilteredAggregation( esQueryResultDetail, searchResponse, field );
			}
		}
	}

	private void buildNonFilteredAggregration( EsQueryResultDetail esQueryResultDetail, SearchResponse searchResponse, String field )
	{
		Terms aggregation = searchResponse.getAggregations().get( field + COUNT );

		if ( aggregation == null )
			return;

		esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter ->
		{
			esFilter.clearBucket();
			for ( Terms.Bucket b : aggregation.getBuckets() )
				esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
		} );
	}

	private void buildFilteredAggregation( EsQueryResultDetail esQueryResultDetail, SearchResponse searchResponse, String field )
	{
		Filters aggFilters = searchResponse.getAggregations().get( "aggregration_filter" );
		if ( aggFilters == null )
			return;

		esQueryResultDetail.getEsFilterByField( field ).ifPresent( esFilter ->
		{
			esFilter.clearBucket();
			@SuppressWarnings( "unchecked" )
			Collection<Filters.Bucket> buckets = (Collection<Filters.Bucket>) aggFilters.getBuckets();
			for ( Filters.Bucket bucket : buckets )
			{
				if ( bucket.getDocCount() == 0 )
					continue;
				fillFilterBucket( esFilter, bucket.getAggregations().get( field + COUNT ) );
			}
		} );
	}

	private void fillFilterBucket( EsFilter esFilter, Terms terms )
	{
		@SuppressWarnings( "unchecked" )
		Collection<Terms.Bucket> bkts = (Collection<Terms.Bucket>) terms.getBuckets();
		for ( Terms.Bucket b : bkts )
		{

			if ( b.getDocCount() == 0 )
				continue;

			if ( !EsFilter.isActiveFilter( esFilter, b.getKeyAsString() ) )
				esFilter.addBucket( b.getKeyAsString(), b.getDocCount() );
			else
				esFilter.addFilteredBucket( b.getKeyAsString(), b.getDocCount() );
		}
	}

	private void applySearchHitAndHighlight( Page<VocabularyDTO> vocabularyPage, SearchResponse searchResponse, boolean withHighlight )
	{
		for ( SearchHit hit : searchResponse.getHits() )
		{
			Optional<VocabularyDTO> vocabOpt = VocabularyDTO.findByIdFromList( vocabularyPage.getContent(), hit.getId() );
			if ( vocabOpt.isPresent() )
			{
				VocabularyDTO cvHit = vocabOpt.get();

				if ( hit.getHighlightFields() != null && withHighlight )
					applyVocabularyHighlighter( hit, cvHit );

				if ( hit.getInnerHits() == null )
					continue;

				applyCodeHighlighter( hit, cvHit, withHighlight );

			}
		}
	}

	private void applyVocabularyHighlighter( SearchHit hit, VocabularyDTO cvHit )
	{
		for ( Map.Entry<String, HighlightField> entry : hit.getHighlightFields().entrySet() )
		{
			String fieldName = entry.getKey();
			HighlightField highlighField = entry.getValue();
			StringBuilder highLightText = new StringBuilder();
			for ( Text text : highlighField.getFragments() )
			{
				highLightText.append( text.string() ).append( " " );
			}

			if ( fieldName.contains( TITLE ) )
				cvHit.setTitleDefinition( highLightText.toString(), null, fieldName.substring( TITLE.length() ), true );
			if ( fieldName.contains( DEFINITION ) )
				cvHit.setTitleDefinition( null, highLightText.toString(), fieldName.substring( DEFINITION.length() ), true );
		}
	}

	private void applyCodeHighlighter( SearchHit hit, VocabularyDTO cvHit, boolean withHighlight )
	{
		Set<CodeDTO> newCodes = new LinkedHashSet<>();

		for ( Map.Entry<String, SearchHits> innerHitEntry : hit.getInnerHits().entrySet() )
		{
			for ( SearchHit innerHit : innerHitEntry.getValue() )
			{
				highlightEachCode( cvHit, newCodes, innerHit, withHighlight );
			}
		}

		cvHit.setCodes( newCodes );
	}

	private void highlightEachCode( VocabularyDTO cvHit, Set<CodeDTO> newCodes, SearchHit innerHit, boolean withHighlight )
	{
		Optional<CodeDTO> codeOpt = CodeDTO.findByIdFromList( cvHit.getCodes(), (int) innerHit.getSourceAsMap().get( "id" ) );
		if ( codeOpt.isPresent() )
		{
			CodeDTO codeHit = codeOpt.get();
			if ( withHighlight )
				applyHighlightCode( cvHit, innerHit, codeHit );
			newCodes.add( codeHit );
		}
	}

	private void applyHighlightCode( VocabularyDTO cvHit, SearchHit innerHit, CodeDTO codeHit )
	{
		for ( Map.Entry<String, HighlightField> entry : innerHit.getHighlightFields().entrySet() )
		{
			String fieldName = entry.getKey();
			HighlightField highlighField = entry.getValue();
			StringBuilder highLightText = new StringBuilder();
			for ( Text text : highlighField.getFragments() )
			{
				if ( !fieldName.contains( TITLE ) && !text.string().endsWith( "." ) )
				{
					highLightText.append( text.string() ).append( " ... " );
				}
				else
					highLightText.append( text.string() );
			}
			if ( fieldName.contains( TITLE ) )
				codeHit.setTitleDefinition( highLightText.toString(), null, fieldName.substring( (CODE_PATH + "." + TITLE).length() ),
						true );
			if ( fieldName.contains( DEFINITION ) )
				codeHit.setTitleDefinition( null, highLightText.toString(), fieldName.substring( (CODE_PATH + "." + DEFINITION).length() ),
						true );
			setSelectedLanguageByHighlight( cvHit, fieldName );
		}
	}

	// set selected language based on highlight
	private void setSelectedLanguageByHighlight( VocabularyDTO cvHit, String highlightField )
	{
		// only set selected language once
		if ( cvHit.getSelectedLang() == null )
		{
			// get last language information from the field and get the Language enum
			String langIso = highlightField.substring( highlightField.length() - 2 );
			cvHit.setSelectedLang( langIso.toLowerCase() );
		}
	}

	public static QueryBuilder generateMainAndNestedQuery( String term, List<String> languageFields, int innerHitSize )
	{
		if ( term != null && !term.isEmpty() && term.length() > 1 )
		{
			return QueryBuilders.boolQuery().should( generateMainQuery( term, languageFields ) )
					.should( generateNestedQuery( term, languageFields, innerHitSize ) );
		}
		else
			return QueryBuilders.matchAllQuery();
	}

	public static QueryBuilder generateMainQuery( String term, List<String> languageFields )
	{
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if ( languageFields.size() == 1 )
		{
			boolQuery
					.should( QueryBuilders.matchQuery( TITLE + languageFields.get( 0 ), term ).fuzziness( 0.7 ).boost( 10.0f ) )
					.should( QueryBuilders.matchQuery( DEFINITION + languageFields.get( 0 ), term ).fuzziness( 0.7 ).boost( 4.0f ) )
					.should( QueryBuilders.wildcardQuery( NOTATION, term.toLowerCase().replace( " ", "" ) + "*" ).boost( 2.0f ) );
		}
		else
		{
			List<String> fields = new ArrayList<>();
			fields.add( NOTATION );
			for ( String langIso : languageFields )
			{
				fields.add( TITLE + langIso );
				fields.add( DEFINITION + langIso );
			}
			boolQuery.should( QueryBuilders.multiMatchQuery( term, fields.toArray( new String[0] ) ) );
		}
		return boolQuery;
	}

	public static QueryBuilder generateNestedQuery( String term, List<String> languageFields, int innerHitSize )
	{
		// query for all languages
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		if ( languageFields.size() == 1 )
		{
			boolQuery
					.should( QueryBuilders.matchQuery( CODE_PATH + "." + TITLE + languageFields.get( 0 ), term ).fuzziness( 0.7 )
							.boost( 3.0f ) )
					.should( QueryBuilders.matchQuery( CODE_PATH + "." + DEFINITION + languageFields.get( 0 ), term ).fuzziness( 0.7 )
							.boost( 2.0f ) )
					.should( QueryBuilders.wildcardQuery( CODE_PATH + "." + NOTATION, term.toLowerCase().replace( " ", "" ) + "*" )
							.boost( 1.0f ) );
		}
		else
		{
			List<String> fields = new ArrayList<>();
			fields.add( CODE_PATH + "." + NOTATION );
			for ( String langIso : languageFields )
			{
				fields.add( CODE_PATH + "." + TITLE + langIso );
				fields.add( CODE_PATH + "." + DEFINITION + langIso );
			}
			boolQuery.should( QueryBuilders.multiMatchQuery( term, fields.toArray( new String[0] ) ) );
		}

		if ( term.length() > 2 )
			return QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.Total )
					.innerHit( new InnerHitBuilder( CODE_PATH )
							.setSize( innerHitSize )
							.setHighlightBuilder( nestedHighlightBuilder( languageFields ) ) );
		return QueryBuilders.nestedQuery( CODE_PATH, boolQuery, ScoreMode.Total )
				.innerHit( new InnerHitBuilder( CODE_PATH ).setSize( innerHitSize ) );
	}

	public static HighlightBuilder.Field[] generateHighlightBuilderMain( List<String> languageFields )
	{
		List<HighlightBuilder.Field> fields = new ArrayList<>();
		// all language fields
		for ( String langIso : languageFields )
		{
			fields.add( new HighlightBuilder.Field( TITLE + langIso ).preTags( HIGHLIGHT_START ).postTags( HIGHLIGHT_END ) );
			fields.add( new HighlightBuilder.Field( DEFINITION + langIso ).preTags( HIGHLIGHT_START ).postTags( HIGHLIGHT_END ) );
		}
		return fields.toArray( new HighlightBuilder.Field[0] );
	}

	private static HighlightBuilder nestedHighlightBuilder( List<String> languageFields )
	{
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// all language fields
		for ( String langIso : languageFields )
		{
			highlightBuilder.field( CODE_PATH + "." + TITLE + langIso ).preTags( HIGHLIGHT_START ).postTags( HIGHLIGHT_END );
			highlightBuilder.field( CODE_PATH + "." + DEFINITION + langIso ).preTags( HIGHLIGHT_START ).postTags( HIGHLIGHT_END );
		}
		return highlightBuilder;
	}

	public static QueryBuilder generateFilterQuery( List<EsFilter> esFilters )
	{
		BoolQueryBuilder boolQueryFilter = QueryBuilders.boolQuery();

		for ( EsFilter esFilter : esFilters )
		{
			if ( esFilter.getValues() != null && !esFilter.getValues().isEmpty() )
			{
				if ( esFilter.getValues().size() == 1 )
				{ // use AND if an item in the filter is selected
					boolQueryFilter.must( QueryBuilders.termQuery( esFilter.getField(), esFilter.getValues().get( 0 ) ) );
				}
				else
				{
					BoolQueryBuilder withinFilterBoolQueryFilter = QueryBuilders.boolQuery();
					// use OR for multiple values within a filter
					for ( String filterValue : esFilter.getValues() )
					{
						withinFilterBoolQueryFilter.should( QueryBuilders.termQuery( esFilter.getField(), filterValue ) );
					}
					// use AND to combine filter with the rest
					boolQueryFilter.must( withinFilterBoolQueryFilter );
				}
			}

		}

		return boolQueryFilter;
	}

	private void setUpAggregrations( NativeSearchQueryBuilder searchQueryBuilder, EsQueryResultDetail esQueryResultDetail )
	{
		if ( !esQueryResultDetail.isAnyFilterActive() )
		{
			for ( String aggField : esQueryResultDetail.getAggFields() )
				searchQueryBuilder.addAggregation(
						AggregationBuilders.terms( aggField + COUNT ).field( aggField ).size( SIZE_OF_ITEMS_ON_AGGREGATION ) );
		}
		else
		{
			FiltersAggregationBuilder filtersAggregation = AggregationBuilders.filters( "aggregration_filter",
					generateFilterQuery( esQueryResultDetail.getEsFilters() ) );

			for ( String aggField : esQueryResultDetail.getAggFields() )
				filtersAggregation.subAggregation(
						AggregationBuilders.terms( aggField + COUNT ).field( aggField ).size( SIZE_OF_ITEMS_ON_AGGREGATION ) );

			searchQueryBuilder.addAggregation( filtersAggregation );
		}
	}

	@Override
	public String generateJsonAllVocabularyPublish()
	{
		StringBuilder output = new StringBuilder();
		output.append( "Performing Vocabulary Publish JSON files creation.\n" );
		// remove all static JSON files on vocabulary (delete entire and including vocabulary
		// directory)
		deleteCvJsonDirectoryAndContent( applicationProperties.getVocabJsonPath() );
		output.append( "Clean up: All existing JSON files are deleted.\n" );

		List<VocabularyDTO> vocabularyDTOS = findAll();
		output.append( "Starting to generate new JSON files.\n" );
		output.append( generateJsonVocabularyPublish( vocabularyDTOS.toArray( new VocabularyDTO[0] ) ) );
		output.append( "Generating new JSON files are done!\n" );
		return output.toString();
	}

	@Override
	public String generateJsonVocabularyPublish( VocabularyDTO... vocabularies )
	{
		StringBuilder output = new StringBuilder();
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule( new JavaTimeModule() );
		mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
		mapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );

		// getAgencyAndLicense
		Map<Long, Agency> agencyMap = agencyRepository.findAll().stream().collect( Collectors.toMap( Agency::getId, Function.identity() ) );
		Map<Long, Licence> licenceMap = licenceRepository.findAll().stream()
				.collect( Collectors.toMap( Licence::getId, Function.identity() ) );

		for ( VocabularyDTO vocabulary : vocabularies )
		{
			output.append( "Generate JSON files for Vocabulary " + vocabulary.getNotation() + ".\n" );
			// get all published versions (sorted by SL and number)
			List<VersionDTO> versions = versionService.findAllPublishedByVocabulary( vocabulary.getId() );
			// skip vocabulary without published version
			if ( versions.isEmpty() )
				continue;
			// remove unused attributes, and add the required attribute for vocabulary
			updateVocabularyContentForJsonfy( agencyMap, vocabulary );
			// put into map based on versionSL, the SL need to be listed in beginning (sorted first)
			Map<String, List<VersionDTO>> slNumberVersionsMap = new LinkedHashMap<>();
			// add special Map for all latest version across SL version
			// just remember that the versions are always already sorted from latest to oldest
			List<VersionDTO> latestVersionsAcrossSl = new ArrayList<>();
			Set<String> latestVersionsLangs = new HashSet<>();
			slNumberVersionsMap.put( LATEST, latestVersionsAcrossSl );
			for ( VersionDTO version : versions )
			{
				output.append( "Generate JSON file for Vocabulary " + vocabulary.getNotation() + " Version" + version.getNumber() + ".\n" );
				prepareVersionAndConceptForJsonfy( licenceMap, vocabulary, slNumberVersionsMap, version, latestVersionsAcrossSl,
						latestVersionsLangs );
			}
			generateJsonFile( mapper, vocabulary, slNumberVersionsMap );
		}
		return output.toString();
	}

	private void prepareVersionAndConceptForJsonfy(
			Map<Long, Licence> licenceMap,
			VocabularyDTO vocabulary,
			Map<String, List<VersionDTO>> slNumberVersionsMap,
			VersionDTO version,
			List<VersionDTO> latestVersionsAcrossSl,
			Set<String> latestVersionsLangs )
	{
		prepareVersionAndConcept( licenceMap, version );

		if ( version.getItemType().equals( ItemType.SL.toString() ) )
		{
			List<VersionDTO> versionDTOs = slNumberVersionsMap.computeIfAbsent( version.getNumber().getMinorVersion(), k -> new ArrayList<>() );
			// check for version history
			addVersionHistories( vocabulary, version );

			versionDTOs.add( version );
		}
		else
		{
			List<VersionDTO> versionDTOs = slNumberVersionsMap.get( version.getNumber().getMinorVersion() );
			if ( versionDTOs == null )
				throw new IllegalArgumentException( "SL version missing from version number of " + version.getNotation() +
						" " + version.getNumber().getMinorVersion() );

			// check for version history
			addVersionHistories( vocabulary, version );

			versionDTOs.add( version );
		}
		// collect latest version across SL
		if ( !latestVersionsLangs.contains( version.getLanguage() ) || version.getNumber().equals(vocabulary.getVersionNumber()) )
		{
			latestVersionsAcrossSl.add( version );
			latestVersionsLangs.add( version.getLanguage() );
		}
	}

	private void prepareVersionAndConcept( Map<Long, Licence> licenceMap, VersionDTO version )
	{
		// get concepts and sorted by position
		List<ConceptDTO> concepts = conceptService.findByVersion( version.getId() );
		// remove unused attributes, and add the required attribute for version
		updateVersionContentForJsonfy( licenceMap, version );
		// remove unused attributes, and add the required attribute for concept
		updateConceptContentForJsonfy( concepts );

		version.setConcepts( new LinkedHashSet<>( concepts ) );
	}

	private void updateConceptContentForJsonfy( List<ConceptDTO> concepts )
	{
		for ( ConceptDTO concept : concepts )
		{
			if ( concept.getDefinition() != null )
				concept.setDefinition( concept.getDefinition().trim() );
			concept.setSlConcept( null );
			concept.setVersionId( null );
		}
	}

	private void updateVersionContentForJsonfy( Map<Long, Licence> licenceMap, VersionDTO version )
	{
		Licence licence = licenceMap.get( version.getLicenseId() );
		if ( licence != null )
		{
			version.setLicense( licence.getAbbr() );
			version.setLicenseLink( licence.getLink() );
			version.setLicenseName( licence.getName() );
			version.setLicenseLogo( licence.getLogoLink() );
		}
		version.setNotes( version.getNotes() == null ? "" : version.getNotes().trim() );
		version.setCreator( null );
		version.setDiscussionNotes( null );
		version.setVocabularyId( null );
		version.setLicenseId( null );
	}

	private void updateVocabularyContentForJsonfy( Map<Long, Agency> agencyMap, VocabularyDTO vocabulary )
	{
		// clear vocabulary content (title, definition), since it is not needed
		vocabulary.clearContent();
		// update data remove unused and add more agency information
		vocabulary.setDiscoverable( null );
		vocabulary.setArchived( null );
		vocabulary.setCodes( null );
		Agency agency = agencyMap.get( vocabulary.getAgencyId() );
		if ( agency != null )
		{
			vocabulary.setAgencyLink( agency.getLink() );
		}
	}

	private void addVersionHistories( VocabularyDTO vocabulary, VersionDTO version )
	{
		List<VersionDTO> olderVersions = versionService.findOlderPublishedByVocabularyLanguageId( vocabulary.getId(), version.getLanguage(),
				version.getId() );
		List<Map<String, Object>> olderVersionHistories = new ArrayList<>();
		for ( VersionDTO olderVersion : olderVersions )
		{
			Map<String, Object> versionHistoryMap = new LinkedHashMap<>();
			versionHistoryMap.put( "id", olderVersion.getId() );
			versionHistoryMap.put( "version", olderVersion.getNumber() );
			versionHistoryMap.put( "date", olderVersion.getPublicationDate().toString() );
			versionHistoryMap.put( "note", olderVersion.getVersionNotes() );
			versionHistoryMap.put( "changes", olderVersion.getVersionChanges() );
			if ( olderVersion.getPreviousVersion() != null && !olderVersion.getPreviousVersion().equals( olderVersion.getId() ) )
				versionHistoryMap.put( "prevVersion", olderVersion.getPreviousVersion() );
			olderVersionHistories.add( versionHistoryMap );
		}
		version.setVersionHistories( olderVersionHistories );
	}

	private void generateJsonFile( ObjectMapper mapper, VocabularyDTO vocabulary, Map<String, List<VersionDTO>> slNumberVersionsMap )
	{
		for ( Map.Entry<String, List<VersionDTO>> entry : slNumberVersionsMap.entrySet() )
		{
			String entryKey = entry.getKey();
			vocabulary.setVersions( new LinkedHashSet<>( entry.getValue() ) );

			String jsonString;
			try
			{
				jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( vocabulary );
				writeToFile( vocabulary.getNotation(), entryKey, jsonString, entryKey.equals( LATEST ) );
			}
			catch (JsonProcessingException e)
			{
				log.error( e.getMessage() );
			}
		}
	}

	private void writeToFile( String notation, String versionNumber, String content, boolean isAllLatestVersion )
	{
		String path = applicationProperties.getVocabJsonPath() + notation + File.separator + versionNumber;
		if ( isAllLatestVersion )
		{
			path = applicationProperties.getVocabJsonPath() + notation;
		}
		File dirPath = new File( path );
		if ( !dirPath.isDirectory() )
		{
			dirPath.mkdirs();
		}
		File file;
		if ( isAllLatestVersion )
		{
			file = new File( path + File.separator + notation + JSON_FORMAT );
		}
		else
		{
			file = new File( path + File.separator + notation + "_" + versionNumber + JSON_FORMAT );
		}
		try (
				BufferedWriter bw = new BufferedWriter( new FileWriter( file ) ) )
		{
			bw.write( content );
			bw.flush();
			log.info( "Written to Temp file : {} ", file.getAbsolutePath() );
		}
		catch (IOException e)
		{
			log.error( e.getMessage() );
		}
	}

	@Override
	public File generateVocabularyPublishFileDownload(
			String vocabularyNotation,
			String versionSl,
			String versionList,
			ExportService.DownloadType downloadType,
			HttpServletRequest request )
	{
		log.info( "Publication generate file {} for Vocabulary {} versionSl {}", downloadType, vocabularyNotation, versionSl );
		VocabularyDTO vocabularyDTO = getVocabularyByNotationAndVersion( vocabularyNotation, versionSl, true );
		return generateVocabularyFileDownload( vocabularyNotation, versionSl, versionList, downloadType, request, vocabularyDTO );
	}

	@Override
	public File generateVocabularyEditorFileDownload(
			String vocabularyNotation,
			String versionSl,
			String versionList,
			ExportService.DownloadType downloadType,
			HttpServletRequest request )
	{
		log.info( "Editor generate file {} for Vocabulary {} versionSl {}", downloadType, vocabularyNotation, versionSl );
		VocabularyDTO vocabularyDTO = getVocabularyByNotationAndVersion( vocabularyNotation, versionSl, false );
		return generateVocabularyFileDownload( vocabularyNotation, versionSl, versionList, downloadType, request, vocabularyDTO );
	}

	@Override
	@Transactional
	public void updateVocabularyUri( Long agencyId, String agencyUri, String agencyUriCode )
	{
		final List<Vocabulary> vocabularies = vocabularyRepository.findAllByAgencyId( agencyId );
		for ( Vocabulary vocabulary : vocabularies )
		{
			vocabulary.setUri(
					VocabularyUtils.generateUri( agencyUri, null, vocabulary ) );
			final List<Version> versions = vocabulary.getVersions().stream().sorted( VocabularyUtils.versionComparator() )
					.collect( Collectors.toList() );
			for ( Version version : versions )
			{
				updateUriForVersionAndConcept( agencyUri, agencyUriCode, vocabulary, version );
				if ( version.getUriSl() != null )
				{
					version.setUriSl( VocabularyUtils.generateUri( agencyUri, true, vocabulary, version, null ) );
				}
			}
			vocabularyRepository.save( vocabulary );
		}
	}

	@Override
	@Transactional
	public void updateVocabularyLogo( Long agencyId, String agencyLogoPath )
	{
		final List<Vocabulary> vocabularies = vocabularyRepository.findAllByAgencyId( agencyId );
		final List<Long> changedVocabularies = new ArrayList<>();
		for ( Vocabulary vocabulary : vocabularies )
		{
			// exit if no logo update
			if ( (vocabulary.getAgencyLogo() != null) && (vocabulary.getAgencyLogo().equals( agencyLogoPath )) )
			{
				return;
			}

			// update db
			vocabulary.setAgencyLogo( agencyLogoPath );
			vocabularyRepository.save( vocabulary );

			// mark the vocabulary as changed
			changedVocabularies.add( vocabulary.getId() );
		}

		// update elasticsearch indexes
		findAll().stream()
				// skip for withdrawn vocabulary
				.filter( v -> !Boolean.TRUE.equals( v.isWithdrawn() ) )
				.filter( v -> changedVocabularies.contains( v.getId() ) )
				.forEach( v ->
				{
					// delete changed vocabularies from indexes
					vocabularyEditorSearchRepository.deleteById( v.getId() );
					vocabularyPublishSearchRepository.deleteById( v.getId() );

					// insert changed vocabulary to indexes
					indexPublished( v );
					indexEditor( v );
				} );
	}

	@Override
	@Transactional
	public VersionDTO forwardStatus( VocabularySnippet vocabularySnippet )
	{
		if ( vocabularySnippet.getVersionId() == null )
		{
			throw new IllegalArgumentException( "Missing version id" );
		}
		if ( vocabularySnippet.getActionType() == null )
		{
			throw new IllegalArgumentException( "Missing action type" );
		}
		VocabularyDTO vocabularyDTO = findOne( vocabularySnippet.getVocabularyId() )
				.orElseThrow( () -> new EntityNotFoundException( UNABLE_TO_FIND_VOCABULARY + vocabularySnippet.getVocabularyId() ) );
		// pick version from vocabularyDTO
		VersionDTO versionDTO = vocabularyDTO.getVersions().stream().filter( v -> v.getId().equals( vocabularySnippet.getVersionId() ) )
				.findFirst()
				.orElseThrow( () -> new EntityNotFoundException( UNABLE_TO_FIND_VERSION + vocabularySnippet.getVersionId() ) );

		Licence licence = null;
		Agency agency = null;
		// probably we don't need to do it for publish, but only for ready to translate and ready to publish!!!
		// no we need it for both, for READY_TO_PUBLISH we need to save it to the DB and for PUBLISH we need to retrieve it
		if ( /*vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_SL_STATUS_PUBLISH ) ||
				vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_TL_STATUS_PUBLISH )*/
				vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE ) ||
				vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH ) )
		{
			licence = licenceRepository.getOne( vocabularySnippet.getLicenseId() );
			agency = agencyRepository.getOne( vocabularySnippet.getAgencyId() );
		}
		// check authorization
		switch (vocabularySnippet.getActionType())
		{
		case FORWARD_CV_SL_STATUS_REVIEW:
			SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_SL_STATUS_REVIEW,
					vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
			versionDTO.setStatus( Status.REVIEW.toString() );
			versionDTO.setLastStatusChangeDate( LocalDate.now() );
			vocabularyDTO.setStatus( Status.REVIEW.toString() );
			break;
		case FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE:
			SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE,
					vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
			versionDTO.setStatus( Status.READY_TO_TRANSLATE.toString() );
			versionDTO.setLastStatusChangeDate( LocalDate.now() );
			vocabularyDTO.setVersionNumberByVocabularySnippet(vocabularySnippet);
			vocabularyDTO.setStatus( Status.READY_TO_TRANSLATE.toString() );
			// we will only publish the version info!!!
			versionDTO.prepareSlPublishing( vocabularySnippet, licence, agency );
			break;
		case FORWARD_CV_SL_STATUS_PUBLISH:
			SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_SL_STATUS_PUBLISH,
					vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
			// If SL is published, increase patch number of the bundle.
			// Otherwise use existing version number, either already set
			// by user or generated by the system.
			final VersionNumber finalPublishingVersionNumber = (
				versionDTO.getStatus().equals(Status.PUBLISHED.toString())
			) ? versionDTO.getNumber().increasePatchNumber() : versionDTO.getNumber();
		
			final VocabularyDTO finalVocabularyDTO = vocabularyDTO;

			// walk through the latest TL versions of the current major.minor bundle
			finalVocabularyDTO
				.getVersions()
				.stream()
				.filter(v -> v.getItemType().equals(ItemType.TL.toString()))
				.filter(v -> v.getNumber().equalMinorVersionNumber(versionDTO.getNumber()))
				.collect(
					Collectors.groupingBy(
						VersionDTO::getLanguage,
						LinkedHashMap::new,
						Collectors.maxBy(Comparator.comparing(VersionDTO::getNumber)))
				)
				.values()
				.stream()
				.map(Optional::get)
				.forEach(v -> {
					// publish TL, if it's ready
					if (v.getStatus().equals(Status.READY_TO_PUBLISH.toString())) {
						// update the TL's version number
						v.setNumber(finalPublishingVersionNumber);
						v.setStatus(Status.PUBLISHED.toString());
						v.setLastStatusChangeDate(LocalDate.now());
						finalVocabularyDTO.setVersionByLanguage(
							v.getLanguage(),
							v.getNumber().toString()
						);
					} else if (v.getStatus().equals(Status.PUBLISHED.toString())) {
						// update the TL's version number
						v.setNumber(finalPublishingVersionNumber);
						// in the vocabulary, update the published TL's version number
						finalVocabularyDTO.setVersionByLanguage(
							v.getLanguage(),
							v.getNumber().toString()
						);
					} else {
						if (v.getNumber().compareTo(finalPublishingVersionNumber) <= 0) {
							// if this version is not yet published or ready to be published,
							// it will be put into the next bundle
							v.setNumber(finalPublishingVersionNumber.increasePatchNumber());
						}
					}
				});

			// update SL version number
			if (!versionDTO.getNumber().equals(finalPublishingVersionNumber)) {
				versionDTO.setNumber(finalPublishingVersionNumber);
			}
			
			// publish SL
			versionDTO.setStatus(Status.PUBLISHED.toString());
			versionDTO.setLastStatusChangeDate(LocalDate.now());
			versionDTO.setPublicationDate(LocalDate.now());
			versionDTO.setDeprecatedConceptsValidUntilVersionId(versionDTO.getId());
			vocabularyDTO.prepareSlPublishing(versionDTO);
			vocabularyDTO.setStatus(Status.PUBLISHED.toString());
			break;
		case FORWARD_CV_TL_STATUS_REVIEW:
			SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_TL_STATUS_REVIEW,
					vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
			versionDTO.setStatus( Status.REVIEW.toString() );
			versionDTO.setLastStatusChangeDate( LocalDate.now() );
			break;
		case FORWARD_CV_TL_STATUS_READY_TO_PUBLISH:
			SecurityUtils.checkResourceAuthorization( ActionType.FORWARD_CV_TL_STATUS_READY_TO_PUBLISH,
					vocabularySnippet.getAgencyId(), vocabularySnippet.getLanguage() );
			versionDTO.setStatus( Status.READY_TO_PUBLISH.toString() );
			versionDTO.setLastStatusChangeDate( LocalDate.now() );
			versionDTO.prepareTlPublishing( vocabularySnippet, licence, agency );
			vocabularyDTO.setVersionByLanguage( versionDTO.getLanguage(), versionDTO.getNumber().toString() );
			vocabularyDTO.setTitleDefinition(versionDTO.getTitle(), versionDTO.getDefinition(), versionDTO.getLanguage(), false);
			break;
		default:
			throw new IllegalArgumentException( "Action type not supported" + vocabularySnippet.getActionType() );
		}
		// check if SL published and not initial version, is there any TL needs to be cloned as
		// DRAFT?
		if (vocabularySnippet.getActionType().equals( ActionType.FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE ) && !versionDTO.isInitialVersion() ) {
			cloneTLsIfAny( vocabularyDTO, versionDTO );
		}
		// save at the end
		vocabularyDTO = save( vocabularyDTO );
		// indexing publication, delete existing one
		if ( versionDTO.getStatus().equals( Status.PUBLISHED.toString() ) )
		{
			// generate json files
			generateJsonVocabularyPublish( vocabularyDTO );

			// reindex published json
			indexPublished( vocabularyDTO );
		}
		return versionDTO;
	}

	private void cloneTLsIfAny( VocabularyDTO vocabularyDTO, VersionDTO versionDTO )
	{
		// find previous SL version, check if there is TLs
		Optional<VersionDTO> prevVersionSlOpt = versionService.findOne( versionDTO.getPreviousVersion() );
		if ( prevVersionSlOpt.isPresent() )
		{
			VersionDTO prevVersionSl = prevVersionSlOpt.get();
			List<VersionDTO> clonedTls = new ArrayList<>();
			List<VersionDTO> prevVersions = vocabularyDTO.getVersionsByMinorVersionNumber(prevVersionSl.getNumber(), true );

			prevVersions.forEach( prevVersion ->
			{
				if ( prevVersion.getItemType().equals( ItemType.SL.toString() ) )
				{
					return;
				}
				log.info( "Clone {} TL {} version {} to version {}_DRAFT", versionDTO.getNotation(),
						prevVersion.getLanguage(), prevVersion.getNumber() + "_" + prevVersion.getStatus(), new VersionNumber(versionDTO.getNumber(), 1) );
				// cloning need currentSL (as main reference for notation and position), previousSl
				// (as secondary reference if notation is changed in the current SL),
				// and previous TL for the rest of properties
				clonedTls.add( cloneTl( versionDTO, prevVersionSl, prevVersion ) );
			} );
			if ( !clonedTls.isEmpty() ) // save if any TLs is cloned
			{
				vocabularyDTO.getVersions().addAll( clonedTls );
			}
		}
	}

	private void updateUriForVersionAndConcept( String agencyUri, String agencyUriCode, Vocabulary vocabulary, Version version )
	{
		if ( version.getStatus().equals( Status.PUBLISHED.toString() ) )
		{
			version.setUri( VocabularyUtils.generateUri( agencyUri, true, vocabulary, version, null ) );
			for ( Concept concept : version.getConcepts() )
			{
				concept.setUri( VocabularyUtils.generateUri( agencyUriCode, false, vocabulary, version, concept ) );
			}
		}
		else
		{
			version.setUri( null );
			for ( Concept concept : version.getConcepts() )
			{
				concept.setUri( null );
			}
		}
	}

	private File generateVocabularyFileDownload(
			String vocabularyNotation,
			String versionSl,
			String versionList,
			ExportService.DownloadType downloadType,
			HttpServletRequest request,
			VocabularyDTO vocabularyDTO )
	{
		Set<VersionDTO> includedVersions = filterOutVocabularyVersions( versionList, vocabularyDTO );
		vocabularyDTO.setVersions( includedVersions );
		Map<String, Object> map = new HashMap<>();
		for ( VersionDTO includedVersion : includedVersions )
		{
			// escaping HTML to strict XHTML
			includedVersion.setVersionNotes( VocabularyUtils.toStrictXhtml( includedVersion.getVersionNotes() ) );
			includedVersion.setVersionChanges( VocabularyUtils.toStrictXhtml( includedVersion.getVersionChanges() ) );
			includedVersion.setDdiUsage( VocabularyUtils.toStrictXhtml( includedVersion.getDdiUsage() ) );
			// sort concepts by position (see #330)
			includedVersion.setConcepts(
					includedVersion.getConcepts().stream().sorted( Comparator.comparing( ConceptDTO::getPosition ) )
							.collect( Collectors.toCollection( LinkedHashSet::new ) ) );
		}

		// sorted versions
		map.put( "versions", includedVersions );

		// agency object
		AgencyDTO agencyDTO = new AgencyDTO();
		agencyDTO.setName( vocabularyDTO.getAgencyName() );
		agencyDTO.setLink( vocabularyDTO.getAgencyLink() );

		if ( downloadType.equals( ExportService.DownloadType.SKOS ) )
		{
			final VersionDTO versionIncluded = includedVersions.iterator().next();
			String uriSl = versionIncluded.getUriSl();
			if ( uriSl == null )
				uriSl = versionIncluded.getUri();
			map.put( "docId", uriSl );
			map.put( "docVersionOf", vocabularyDTO.getUri() );
			map.put( "docNotation", vocabularyDTO.getNotation() );
			map.put( "docVersion", versionIncluded.getNumber().getBasePatchVersion().toString() );
			map.put( "docLicense", versionIncluded.getLicenseName() );
			map.put( "docRight", versionIncluded.getLicenseName() );
			map.put( CODE_PATH, CodeDTO.generateCodesFromVersion( includedVersions, false ) );
		}
		else
		{
			vocabularyDTO.setVersions( includedVersions );
			prepareAdditionalAttributesForNonSkos( vocabularyDTO, map, agencyDTO );
		}

		map.put( "agency", agencyDTO );
		int year = vocabularyDTO.getPublicationDate() == null
				? (vocabularyDTO.getLastModified() == null ? (LocalDate.now().getYear()) : vocabularyDTO.getLastModified().getYear())
				: vocabularyDTO.getPublicationDate().getYear();
		map.put( "year", year );
		map.put( "baseUrl", getURLWithContextPath( request ) );

        File outputFile = new File( System.getProperty("java.io.tmpdir") + File.separator
            + vocabularyNotation + "-" + versionSl + "_" + versionList + "." + downloadType + "." + downloadType );

        try (FileOutputStream outputStream = new FileOutputStream( outputFile ))
        {
            exportService.generateFileByThymeleafTemplate( "export", map, downloadType, outputStream );
            return outputFile;
        }
        catch ( IOException | JAXBException | Docx4JException e )
        {
            throw new VocabularyFileGenerationFailedException( e );
        }
	}

	@Override
	public Set<VersionDTO> filterOutVocabularyVersions( String versionList, VocabularyDTO vocabularyDTO )
	{
		// filter out version
		Set<VersionDTO> includedVersions = new LinkedHashSet<>();
		if ( versionList != null )
		{
			String[] versionSplits = versionList.split( "_" );
			for ( String vs : versionSplits )
			{
				String[] s = vs.split( "-" );
				if ( s.length != 2 )
					continue;
				VersionNumber versionNumberInList = VersionNumber.fromString(s[1]);
				Optional<VersionDTO> versionDTOOpt = vocabularyDTO
					.getVersions()
					.stream()
					.filter(
						v -> v.getLanguage().equals(s[0])
						&& v.getNumber().compareTo(versionNumberInList) == 0
					).findFirst();
				versionDTOOpt.ifPresent( includedVersions::add );
			}
		}
		else
		{
			includedVersions = vocabularyDTO.getVersions();
		}
		return includedVersions;
	}

	private void prepareAdditionalAttributesForNonSkos( VocabularyDTO vocabularyDTO, Map<String, Object> map, AgencyDTO agencyDTO )
	{
		if ( vocabularyDTO.getAgencyLogo() != null )
		{
			File logoFile = new File( applicationProperties.getStaticFilePath() + File.separator + "content" +
					File.separator + "images" + File.separator + "agency" + File.separator + vocabularyDTO.getAgencyLogo() );
			String data = null;
			try
			{
				data = DatatypeConverter.printBase64Binary( Files.readAllBytes( logoFile.toPath() ) );
			}
			catch (IOException e)
			{
				log.error( e.getMessage() );
			}
			agencyDTO.setLogo( "data:image/png;base64," + data );
		}
		if ( !vocabularyDTO.getVersions().isEmpty() )
		{
			vocabularyDTO.getVersions().forEach( version ->
			{
				if ( version.getLicenseLogo() == null )
					return;
				File versionLogo = new File( applicationProperties.getLicenseImagePath() + version.getLicenseLogo() );
				String versionBase64LogoData = null;
				try
				{
					versionBase64LogoData = DatatypeConverter.printBase64Binary( Files.readAllBytes( versionLogo.toPath() ) );
				}
				catch (IOException e)
				{
					log.error( e.getMessage() );
				}
				version.setLicenseLogo( "data:image/png;base64," + versionBase64LogoData );
			} );

			VersionDTO version = vocabularyDTO.getVersions().iterator().next();
			if ( version.getCanonicalUri() != null )
			{
				int index = version.getCanonicalUri().lastIndexOf( ':' );
				map.put( "cvUrn", version.getCanonicalUri().substring( 0, index ) );
			}
		}
	}

	private String getURLWithContextPath( HttpServletRequest request )
	{
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}

	private void setVocabularySelectedLanguage(
			EsQueryResultDetail esQueryResultDetail,
			Page<VocabularyDTO> vocabularyPage,
			String fieldType )
	{
		if ( esQueryResultDetail.isAnyFilterActive() )
		{
			esQueryResultDetail.getEsFilterByField( fieldType ).ifPresent( langFilter ->
			{
				if ( langFilter.getValues().size() == 1 )
				{
					for ( VocabularyDTO vocab : vocabularyPage.getContent() )
					{
						vocab.setSelectedLang( langFilter.getValues().get( 0 ) );
					}
				}
			} );
		}
		else
		{
			setSelectedLangToSourceLang( vocabularyPage );
		}
	}

	private void setSelectedLangToSourceLang( Page<VocabularyDTO> vocabularyPage )
	{
		for ( VocabularyDTO vocab : vocabularyPage.getContent() )
		{
			if ( vocab.getSelectedLang() == null )
				vocab.setSelectedLang( vocab.getSourceLanguage() );
		}
	}

	@Override
	public void task398() {
		log.info( "MIGRATING VERSIONING SYSTEM START" );
		// migrate all vocabularies
		findAll().forEach(this::task398MigrateVocabulary);
		log.info( "MIGRATING VERSIONING SYSTEM FINISHED" );
		indexAllAgencyStats();
		indexAllPublished();
		indexAllEditor();
	}

	private ConceptDTO task398MigrateConcept(ConceptDTO c, Long newVersionId) {
		ConceptDTO conceptMigrated = c.copy();
		// override cloned attributes
		conceptMigrated.setId(null);
		conceptMigrated.setVersionId(newVersionId);
		if (c.getValidUntilVersionId() != null && c.getValidUntilVersionId().equals(c.getId())) {
			conceptMigrated.setValidUntilVersionId(newVersionId);
		}
		conceptMigrated.setPreviousConcept(c.getId());
		// TODO: update URIs
		conceptMigrated.setUri(null);
		return conceptMigrated;
	}

	private VersionDTO task398MigrateVersion(final VersionDTO v, VersionNumber newVersionNumber) {
		
		VersionDTO versionMigrated = v.copy();
		
		// override cloned attributes
		versionMigrated.setId(null);
		versionMigrated.setPreviousVersion(v.getId());
		versionMigrated.setNumber(newVersionNumber);
		versionMigrated.setVersionChanges(null);
		versionMigrated.setVersionNotes(
			(v.getVersionNotes() != null && v.getVersionNotes().length() > 0 ? v.getVersionNotes() + "\n\n---\n" : "")
			+ "To align with a new versioning system, this version has been automaticaly created by copying the previous version " + v.getStatus() + "-" + v.getNumber() + "."
		);
		// TODO: update URIs
		versionMigrated.setCanonicalUri(null);
		versionMigrated.setUri(null);
		versionMigrated.setUriSl(null);

		// save cloned version
		final VersionDTO finalVersionMigrated = versionService.save(versionMigrated);

		// migrate concepts
		v.getConcepts().forEach(c -> {
			ConceptDTO conceptMigrated = task398MigrateConcept(c, finalVersionMigrated.getId());
			finalVersionMigrated.addConcept(conceptMigrated);
		});

		v.getComments().forEach(c -> {
			CommentDTO conceptMigrated = new CommentDTO();
			conceptMigrated.setContent(c.getContent());
			conceptMigrated.setDateTime(c.getDateTime());
			conceptMigrated.setId(null);
			conceptMigrated.setInfo(c.getInfo());
			conceptMigrated.setUserId(c.getUserId());
			conceptMigrated.setVersionId(finalVersionMigrated.getId());
			finalVersionMigrated.addComment(conceptMigrated);
		});
		
		return finalVersionMigrated;
	}

	private VersionNumber getLatestPublishedVersionNumber(VocabularyDTO vocabularyDTO) {
		final VersionDTO version = vocabularyDTO
			.getVersions()
			.stream()
			.filter(v -> v.getStatus().equals(Status.PUBLISHED.toString()))
			.max((v1, v2) -> v1.getNumber().compareTo(v2.getNumber()))
			.orElse(null);
		if (version != null) {
			return version.getNumber();
		}
		return null;
	}

	private VocabularyDTO task398MigrateVocabulary(final VocabularyDTO vocabularyDTO) {

		log.info( "Migrating versioning system for vocabulary with id {} and notation {}", vocabularyDTO.getId(), vocabularyDTO.getNotation() );

		// get the current Sl version
		VersionDTO currentSlVersion = vocabularyDTO
			.getVersions()
			.stream()
			.filter(v -> v.getItemType().equals(ItemType.SL.toString()))
			.max((v1, v2) -> v1.getNumber().compareTo(v2.getNumber()))
			.orElse(null);
		
		if (currentSlVersion == null) {
			log.warn("current SL version is null");
			return vocabularyDTO;
		}

		VersionNumber newVersionNumber = getLatestPublishedVersionNumber(vocabularyDTO);
		
		if (newVersionNumber == null) {
			log.warn("latest published version number is null");
			return vocabularyDTO;
		}
		
		final VersionNumber finalNewVersionNumber = newVersionNumber.increasePatchNumber();
		
		if (currentSlVersion.getNumber().compareTo(finalNewVersionNumber) < 0) {
			// version number of the SL has to be updated
			if (currentSlVersion.getStatus().equals(Status.PUBLISHED.toString())) {
				// clone SL and assign a new version number to it
				currentSlVersion = task398MigrateVersion(currentSlVersion, finalNewVersionNumber);
				vocabularyDTO.addVersion(currentSlVersion);
			} else {
				// just update the version number
				currentSlVersion.setNumber(finalNewVersionNumber);
			}
		}

		List<VersionDTO> clonedTls = new ArrayList<>();
		vocabularyDTO.getVersions()
			.stream()
			.filter(v -> v.getItemType().equals(ItemType.TL.toString()))
			.filter(v -> v.getNumber().equalMinorVersionNumber(finalNewVersionNumber))
			.collect(
				Collectors.groupingBy(
					VersionDTO::getLanguage,
					LinkedHashMap::new,
					Collectors.maxBy(Comparator.comparing(VersionDTO::getNumber)))
			)
			.values()
			.stream()
			.map(Optional::get)
			.forEach(v -> {
				if (v.getStatus().equals(Status.PUBLISHED.toString()) || v.getStatus().equals(Status.READY_TO_PUBLISH.toString())) {
					if (v.getNumber().compareTo(finalNewVersionNumber) < 0) {
						v = task398MigrateVersion(v, finalNewVersionNumber);
						clonedTls.add(v);	
					}
				} else {
					v.setNumber(finalNewVersionNumber.increasePatchNumber());
				}
			});

		if (!clonedTls.isEmpty()) {
			vocabularyDTO.getVersions().addAll(clonedTls);
		}

		// save migrated vocabulary
		Vocabulary vocabularyEntity = vocabularyMapper.toEntity(vocabularyDTO);
		vocabularyEntity = vocabularyRepository.save(vocabularyEntity);
		return vocabularyMapper.toDto(vocabularyEntity);
	}
}
