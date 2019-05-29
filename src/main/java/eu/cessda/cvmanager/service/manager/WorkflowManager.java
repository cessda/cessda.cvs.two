package eu.cessda.cvmanager.service.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vaadin.data.TreeData;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.CvMapper;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import eu.cessda.cvmanager.utils.WorkflowUtils;
import eu.cessda.cvmanager.service.mapper.CvCodeMapper;

@Service
public class WorkflowManager {
	private static ConfigurationService configurationService;
	private static VocabularyService vocabularyService;
	private static VersionService versionService;
	private static CodeService codeService;
	private static ConceptService conceptService;
	private static CvMapper cvMapper;
	private static CvCodeMapper cvCodeMapper;
	private static StardatDDIService stardatDDIService;
	private static ResolverService resolverService;
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowManager.class);
	
	public WorkflowManager(VocabularyService vocabularySvc, VersionService versionSvc,
			CodeService codeSvc, ConceptService conceptSvc, CvMapper cvMpr,
			CvCodeMapper cvCodeMpr, StardatDDIService stardatDDIService,
			ResolverService resolverService, ConfigurationService configurationService) {
		WorkflowManager.vocabularyService = vocabularySvc;
		WorkflowManager.versionService = versionSvc;
		WorkflowManager.codeService = codeSvc;
		WorkflowManager.conceptService = conceptSvc;
		WorkflowManager.cvMapper = cvMpr;
		WorkflowManager.cvCodeMapper = cvCodeMpr;
		WorkflowManager.stardatDDIService = stardatDDIService;
		WorkflowManager.resolverService = resolverService;
		WorkflowManager.configurationService =configurationService;
	}
	
	public static VocabularyDTO createVocabulary(AgencyDTO agency, Cv cv) {
		VocabularyDTO vocabulary = VocabularyDTO.createDraft();
		VersionDTO version = cvMapper.toDto(cv);
		version.setStatus( Status.DRAFT.toString() );
		Language language = Language.valueOfEnum( cv.getLanguage() );
		
		WorkspaceManager.saveSourceCV(agency, language, vocabulary, version, 
				cv.getCode(), cv.getTerm(), cv.getDefinition(), null);
		
		// map concept as well
		if( cv.getCvCodes() != null && cv.getCvCodes().length > 0) {
			Set<String> conceptNotation = new HashSet<>();
			for(CvCode cvCode : cv.getCvCodes()) {
				// validate concept, make sure the concept is unique
				if( conceptNotation.contains( cvCode.getCode()))
					continue;
				conceptNotation.add( cvCode.getCode());
				
				ConceptDTO concept = cvCodeMapper.toDto(cvCode);
				CodeDTO code = new CodeDTO();
				if( concept.getParent() != null )
					code.setParent( concept.getParent());
				
				// save code
				WorkspaceManager.saveCode(vocabulary, version, new CodeDTO(), null, 
					concept, null, concept.getNotation(), concept.getTitle(), concept.getDefinition());
			}
		}
		
		
		// publish, by assign version with final_review status
		version.setStatus( Status.FINAL_REVIEW.toString());
		forwardStatus(vocabulary, version, agency, null, null, "1.0", "", "");
//		
		return vocabulary;
	}
	

	public static VocabularyDTO addVocabularyTranslation(VocabularyDTO vocabulary, AgencyDTO agency, Cv cv) {
		
		// check for existing version
		VersionDTO version = null;
		Language language = Language.valueOfEnum( cv.getLanguage() );
		Optional<VersionDTO> versionByLanguage = vocabulary.getLatestVersionByLanguage( cv.getLanguage());
		if(versionByLanguage.isPresent())
			version = versionByLanguage.get();
		
		if( version == null ) {
			version = cvMapper.toDto(cv);
			version.setStatus( Status.DRAFT.toString() );
		}
		
		// get the SL version
		VersionDTO slVersion = null;
		Optional<VersionDTO> latestSlVersion = vocabulary.getLatestSlVersion(false);
		if(latestSlVersion.isPresent())
			slVersion = latestSlVersion.get();
		else
			throw new IllegalArgumentException("Unable to find SL version, please make sure that SL is exist");
		
		WorkspaceManager.saveTargetCV(agency, language, vocabulary, version, 
				cv.getTerm(), cv.getDefinition(), null, null);
		
		
		// map concept as well
		if( cv.getCvCodes() != null && cv.getCvCodes().length > 0) {
			// get code on the map
			List<CodeDTO> codes = codeService.findArchivedByVocabularyAndVersion( vocabulary.getId(), slVersion.getId());
			Map<String, CodeDTO> codeMap = CodeDTO.getCodeAsMap(codes);
			
			// get conceptMap from latest SL
			Optional<VersionDTO> latestSlVersionOpt = vocabulary.getLatestSlVersion( true );
			Map<String, ConceptDTO> slConceptMap = new HashMap<>();
			if(latestSlVersionOpt.isPresent())
				slConceptMap = latestSlVersionOpt.get().getConceptAsMap();
			
			// set code and create new concept
			for(CvCode cvCode : cv.getCvCodes()) {
				// validate concept, make sure the concept is available in code
				CodeDTO code = codeMap.get( cvCode.getCode());
				if( code == null )
					continue;
				ConceptDTO concept = cvCodeMapper.toDto(cvCode);
				// create connection with SL concept
				ConceptDTO slConcept = slConceptMap.get( code.getNotation() );
				// get parent code if exist
				CodeDTO parentCode = codeMap.get( code.getParent());
				// save code
				WorkspaceManager.saveCode(vocabulary, version, code, parentCode, 
					concept, slConcept, concept.getNotation(), concept.getTitle(), concept.getDefinition());
			}
		}
		
		// publish, by assign version with final_review status
		version.setStatus( Status.FINAL_REVIEW.toString());
		forwardStatus(vocabulary, version, agency, null, null, "1.0.1", "", "");

		return vocabulary;
	}
	
	/**
	 * Get next forwarded status
	 * @param currentStatus
	 * @return
	 */
	public static Status getForwardStatus( Status currentStatus) {
		switch( currentStatus ) {
			case DRAFT:
				return Status.INITIAL_REVIEW;
			case INITIAL_REVIEW:
				return Status.FINAL_REVIEW;
			case FINAL_REVIEW:
				return Status.PUBLISHED;
			default:
				return null;
		}
	}
	
	
	public static VersionDTO forwardStatus( VocabularyDTO vocabulary, VersionDTO version, AgencyDTO agency, CVScheme cvScheme,
			List<VersionDTO> latestTlVersions, String versionNumber, String versionNotes, String versionChanges) {
		// forward current version
		version.setStatus( WorkflowManager.getForwardStatus( version.getEnumStatus() ).toString() );
		Status currentCvStatus = version.getEnumStatus();
		
		// determine if version is source language
		boolean isVersionSl = false;
		if( vocabulary.getSourceLanguage().equals( version.getLanguage()))
			isVersionSl = true;
		
		// if version is source language, then update vocabulary status
		if( isVersionSl )
			vocabulary.setStatus( currentCvStatus.toString() );
		
		// determine the action based on the state
		switch( currentCvStatus ) {
			case DRAFT:
				// nothing to do here, since this is not possible
				log.info( "Draft CV should be not possible" );
				break;
			case INITIAL_REVIEW:
				// just save after change status
				version = versionService.save(version);
				// index for editor
				vocabularyService.index(vocabulary);
				break;
			case FINAL_REVIEW:
				// just save after change status
				version = versionService.save(version);
				// index for editor
				vocabularyService.index(vocabulary);
				break;
			case PUBLISHED:
				String uri =  version.getUri() + "/" + versionNumber;
				
				
				version.setUri( uri );
				version.setVersionNotes( versionNotes);
				version.setNumber( versionNumber );
				version.setPublicationDate( LocalDate.now());
				version.setVersionChanges( versionChanges );
				version.setCanonicalUri(WorkflowUtils.generateVersionCanonicalURI(agency, version));
				version.createSummary( versionChanges );
				
				vocabulary.setVersionByLanguage( version.getLanguage(), versionNumber);
								
				// TODO: refactor this
				// get workflow codes
				List<CodeDTO> codes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
				// update concept uri
				for(ConceptDTO concept : version.getConcepts()) {
					concept.setUri( concept.getUri() + "/" + versionNumber);
					// update concept parent and position based on workflow code
					codes.stream().filter( c -> concept.getNotation().equals( c.getNotation())).findFirst().ifPresent( c -> {
						concept.setParent( c.getParent());
						concept.setPosition( c.getPosition());
					});
					conceptService.save( concept );
				}
				// If it is publishing SL
				if( isVersionSl ) {
					version.setCitation( VersionDTO.generateCitation(version, null, agency.getName()));
					
					vocabulary.setVersionNumber( versionNumber );
					// only set Uri everytime SL published
					vocabulary.setUri( version.getUri());
					vocabulary.setPublicationDate( LocalDate.now());
					vocabulary.setLanguages( VocabularyDTO.getLanguagesFromVersions( vocabulary.getVersions()) );
					vocabulary.setLanguagesPublished( null);
					vocabulary.addLanguagePublished( version.getLanguage());
					// perform the cloning of available TL
					doTlCvCloning(vocabulary, version, agency, latestTlVersions, codes);
					// save current version
					version = versionService.save(version);
				} 
				// if publishing TL
				else { 
					// if TL is published
					version.setUriSl( vocabulary.getUri());
					// set citation
					Optional<VersionDTO> latestSlVersion = vocabulary.getLatestSlVersion( true );
					if( latestSlVersion.isPresent() )
						version.setCitation( VersionDTO.generateCitation(version, latestSlVersion.get(), agency.getName()));
					
					version = versionService.save(version);
					
					vocabulary.addLanguagePublished( version.getLanguage());
				}
				
				// save vocabulary
				vocabulary = vocabularyService.save(vocabulary);
				
				// index for editor
				vocabularyService.index(vocabulary);
				
				// Now before indexing for the Publication side,
				// the Vocabulary and Codes need to be updated
				VersionDTO latestSlVersion = null;
				if( isVersionSl ) {
					latestSlVersion = version;
					// create Codes for published version, which is cloned from the code workflow
					List<CodeDTO> newCodes = clonePublishedVersionCode(version, codes);
					// Publishing new CV-Scheme since SL is published
					createAndStoreCvScheme(vocabulary, version, agency, newCodes);
					
					// store vocabulary URN, if not exist
					if(version.isInitialVersion()) {
						int index = version.getCanonicalUri().lastIndexOf(":");
						String cvCanonicalUri = version.getCanonicalUri().substring(0, index);
						resolverService.save( 
							ResolverDTO.createUrnResolver()
								.withResourceId( vocabulary.getUri())
								.withResourceURL( vocabulary.getNotation() )
								.withResolverURI( cvCanonicalUri )
						);
					}
				} 
				// TL published
				else {
					// get the latest SL version
					Optional<VersionDTO> optLatestSlVersion = vocabulary.getLatestSlVersion( true );
					if(optLatestSlVersion.isPresent())
						latestSlVersion = optLatestSlVersion.get();
					
					codes = codeService.findArchivedByVocabularyAndVersion( vocabulary.getId(), latestSlVersion.getId());
					Map<String, CodeDTO> codeMap = CodeDTO.getCodeAsMap(codes);
					for( ConceptDTO concept : version.getConcepts()) {
						CodeDTO code = codeMap.get( concept.getNotation());
						if( code == null )
							continue;
						code.setTitleDefinition( concept.getTitle(), concept.getDefinition(), version.getLanguage());
						codeService.save(code);
					}
					
					if( cvScheme == null ) {
						// try to get cv scheme if it is null
						List<DDIStore> ddiSchemes = stardatDDIService.findByIdAndElementType(latestSlVersion.getUri(), DDIElement.CVSCHEME);
						
						if (ddiSchemes != null && !ddiSchemes.isEmpty())
							cvScheme = new CVScheme(ddiSchemes.get(0));
					}
					
					// update CvScheme in FlatDB
					if( cvScheme != null ) {
						log.info("update cvscheme");
						cvScheme.setTitleByLanguage( version.getLanguage(), version.getTitle());
						cvScheme.setDescriptionByLanguage( version.getLanguage(), version.getDefinition());
						cvScheme.save();
						DDIStore ddiStore = stardatDDIService.saveElement(cvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Publish Cv " + version.getNotation() + " TL" + version.getLanguage());
						// if ddiStore is null, means there is duplicated CVScheme
						if( ddiStore != null )
							cvScheme = new CVScheme(ddiStore);
						// save  CvConcept
						List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType(cvScheme.getContainerId(), DDIElement.CVCONCEPT);
						Map<String, CVConcept> cvConceptMaps = new HashMap<>();
						for(DDIStore ddiConcept: ddiConcepts) {
							CVConcept cvConcept = new CVConcept(ddiConcept);
							cvConceptMaps.put( cvConcept.getNotation(), cvConcept);
						}
						for( ConceptDTO concept : version.getConcepts()) {
							CVConcept cvConcept = cvConceptMaps.get( concept.getNotation() );
							if( cvConcept == null )
								continue;
							cvConcept.setPrefLabelByLanguage( version.getLanguage(),  concept.getTitle());
							cvConcept.setDescriptionByLanguage( version.getLanguage(), concept.getDefinition());
							cvConcept.save();
							try {
								stardatDDIService.saveElement(cvConcept.ddiStore, SecurityUtils.getCurrentUserLogin().get() , "publish code " + cvConcept.getNotation());
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
				
				// add URN to resolver
				try {
					// only store SL version, since TL can be resolve from SL
					if( isVersionSl )
						resolverService.save( 
							ResolverDTO.createUrnResolver()
								.withResourceId( version.getUri())
								.withResourceURL( vocabulary.getNotation() + "?url=" + URLEncoder.encode( version.getUri(), "UTF-8")  )
								.withResolverURI( version.getCanonicalUri())
						);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// indexing published codes
				vocabularyService.indexPublish(vocabulary, latestSlVersion);
				
				break;
			default:
				break;
	}
		
		
		
		return version;
	}

	private static List<CodeDTO> clonePublishedVersionCode(VersionDTO version, List<CodeDTO> codes) {
		// clone each code
		List<CodeDTO> newCodes = new ArrayList<>();
		for( CodeDTO eachCode : codes ) {
			// create new published code everytime SL is published
			CodeDTO newCode = CodeDTO.clone(eachCode);
			newCode.setArchived( true );
			newCode.setVersionId(version.getId());
			newCode.setVersionNumber( version.getNumber() );
			newCode = codeService.save( newCode);
			
			newCodes.add(newCode);
			
			// store also the concept "code id changed"
			if( ConceptDTO.getConceptFromCode( version.getConcepts(), eachCode.getId()).isPresent()  ) {
				ConceptDTO c = ConceptDTO.getConceptFromCode( version.getConcepts(), eachCode.getId()).get();
				c.setCodeId( newCode.getId());
				conceptService.save(c);
			}
		}
		return newCodes;
	}

	private static void createAndStoreCvScheme(VocabularyDTO vocabulary, VersionDTO version, AgencyDTO agency,
			List<CodeDTO> newCodes) {
		// create cv scheme
		CVScheme newCvScheme = new CVScheme();
		newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
		newCvScheme.setId( version.getUri());
		newCvScheme.setContainerId(newCvScheme.getId());
		newCvScheme.setStatus( Status.PUBLISHED.toString() );
		
		
		// Store also Owner Agency, Vocabulary and codes
		// store vocabulary content
		VocabularyDTO.setCvSchemeByVocabulary(newCvScheme, vocabulary);
		
		// store owner agency
		List<CVEditor> editorSet = new ArrayList<>();
		CVEditor cvEditor = new CVEditor();
		cvEditor.setName( agency.getName());
		cvEditor.setLogoPath( agency.getLogopath());
		
		editorSet.add( cvEditor );
		newCvScheme.setOwnerAgency((ArrayList<CVEditor>) editorSet);
		newCvScheme.setCode( vocabulary.getNotation());
		newCvScheme.save();
		newCvScheme.ddiStore = stardatDDIService.saveElement(newCvScheme.ddiStore, SecurityUtils.getLoggedUser().getUsername() , "Publish Cv");

		// store complete codeDTOs to CVConcept
		TreeData<CodeDTO> codeTree = new TreeData<>();
		CvCodeTreeUtils.buildCvConceptTree(newCodes, codeTree);
		
		// generate tree concept
		TreeData<CVConcept> cvConceptTree = new TreeData<>();
		cvConceptTree = CvCodeTreeUtils.generateCVConceptTreeFromCodeTree(codeTree, newCvScheme);
		
		// save all cvConcepts and update cvScheme
		if( !cvConceptTree.getRootItems().isEmpty())
			storeCvConceptTree( cvConceptTree , newCvScheme);
	}

	private static void doTlCvCloning(VocabularyDTO vocabulary, VersionDTO currentVersion, AgencyDTO agency,
			List<VersionDTO> latestTlVersions, List<CodeDTO> codes) {
		// clone any latest TL if exist
		for( VersionDTO targetTLversion : latestTlVersions ) {
			// create new version
			VersionDTO newVersion = VersionDTO.clone(targetTLversion, currentVersion,
					SecurityUtils.getLoggedUser().getId(),  currentVersion.getNumber() + ".1", 
					currentVersion.getLicenseId(), WorkflowUtils.generateAgencyBaseUri( agency.getUri()), currentVersion.getDdiUsage());
			newVersion.setUriSl( vocabulary.getUri());
			newVersion = versionService.save(newVersion);
			
			// save concepts with workflow codes ID
			for( CodeDTO code: codes) {
				ConceptDTO
					.getConceptFromCode(newVersion.getConcepts(), code.getNotation())
					.ifPresent( c ->{ 
						c.setCodeId( code.getId());
					});
			}
			// save versionId property
			for( ConceptDTO newConcept: newVersion.getConcepts()) {
				newConcept.setVersionId( newVersion.getId());
				conceptService.save(newConcept);
			}
			
			vocabulary.addVersion(newVersion);
		}
	}
	
	private static void storeCvConceptTree(TreeData<CVConcept> cvConceptTree, CVScheme newCvScheme) {
		List<CVConcept> rootItems = cvConceptTree.getRootItems();
		for(CVConcept topCvConcept : rootItems) {
			try {
				System.out.println("Store CV-concept:" + topCvConcept.getNotation());
				DDIStore ddiStoreTopCvConcept = stardatDDIService.saveElement(topCvConcept.ddiStore, SecurityUtils.getLoggedUser().getUsername(), "Add Code " + topCvConcept.getNotation());
				newCvScheme.addOrderedMemberList(ddiStoreTopCvConcept.getElementId());
				
				for( CVConcept childCvConcept : cvConceptTree.getChildren(topCvConcept)) {
					storeCvConceptTreeChild( cvConceptTree, childCvConcept, topCvConcept);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		// store top concept
		newCvScheme.save();
		stardatDDIService.saveElement(newCvScheme.ddiStore, SecurityUtils.getLoggedUser().getUsername(), "Update Top Concept");
	}

	private static void storeCvConceptTreeChild(TreeData<CVConcept> cvConceptTree, CVConcept cCvConcept, CVConcept topCvConcept) {
		System.out.println("Store CV-concept c:" + cCvConcept.getNotation());
		// store cvConcept
		DDIStore ddiStoreCvConcept = stardatDDIService.saveElement(cCvConcept.ddiStore, SecurityUtils.getLoggedUser().getUsername(), "Add Code " + cCvConcept.getNotation());
		// store narrower
		topCvConcept.addOrderedNarrowerList( ddiStoreCvConcept.getElementId());
		topCvConcept.save();
		stardatDDIService.saveElement(topCvConcept.ddiStore, "User", "Add Code narrower");
		for( CVConcept cvConceptChild : cvConceptTree.getChildren(cCvConcept)) {
			storeCvConceptTreeChild( cvConceptTree, cvConceptChild, cCvConcept);
		}
	}
	
	
	
}
