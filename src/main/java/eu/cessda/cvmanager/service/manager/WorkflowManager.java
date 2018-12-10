package eu.cessda.cvmanager.service.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.springframework.stereotype.Service;

import com.vaadin.data.TreeData;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.CvMapper;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import eu.cessda.cvmanager.service.mapper.CvCodeMapper;

@Service
public class WorkflowManager {
	private static VocabularyService vocabularyService;
	private static VersionService versionService;
	private static CodeService codeService;
	private static ConceptService conceptService;
	private static CvMapper cvMapper;
	private static CvCodeMapper cvCodeMapper;
	private static StardatDDIService stardatDDIService;
	
	public WorkflowManager(VocabularyService vocabularySvc, VersionService versionSvc,
			CodeService codeSvc, ConceptService conceptSvc, CvMapper cvMpr,
			CvCodeMapper cvCodeMpr, StardatDDIService stardatDDIService) {
		WorkflowManager.vocabularyService = vocabularySvc;
		WorkflowManager.versionService = versionSvc;
		WorkflowManager.codeService = codeSvc;
		WorkflowManager.conceptService = conceptSvc;
		WorkflowManager.cvMapper = cvMpr;
		WorkflowManager.cvCodeMapper = cvCodeMpr;
		WorkflowManager.stardatDDIService = stardatDDIService;
	}
	
	public static VocabularyDTO createVocabulary(AgencyDTO agency, Cv cv) {
		VocabularyDTO vocabulary = new VocabularyDTO();
		String cvUriLink = agency.getUri();
		if(cvUriLink == null )
			cvUriLink = ConfigurationService.DEFAULT_CV_LINK;
		if(!cvUriLink.endsWith("/"))
			cvUriLink += "/";
		
		cvUriLink += cv.getCode() + "/" + cv.getLanguage();
		
		vocabulary.setNotation( cv.getCode() );
		vocabulary.setTitleDefinition(cv.getTerm(), cv.getDefinition(), cv.getLanguage());
		vocabulary.setVersionNumber("1.0");
		vocabulary.setAgencyId( agency.getId());
		vocabulary.setAgencyName( agency.getName());
		vocabulary.setSourceLanguage( cv.getLanguage());
		vocabulary.setStatus( Status.DRAFT.toString());
		vocabulary.addStatus( Status.DRAFT.toString() );
		vocabulary.setVersionByLanguage(cv.getLanguage(), "1.0");
		
		VersionDTO version = cvMapper.toDto(cv);
		
		
		version.setUri( cvUriLink );
		version.setNumber("1.0");
		version.setStatus( Status.DRAFT.toString() );
		version.setPreviousVersion(0L);
		
		// save to database
		vocabulary = vocabularyService.save(vocabulary);
		
		version.setVocabularyId( vocabulary.getId() );
		version = versionService.save( version );
		
		// save initial version
		version.setInitialVersion( version.getId() );
		version = versionService.save( version );
		
		// map concept as well
		if( cv.getCvCodes() != null && cv.getCvCodes().length > 0) {
			Set<String> conceptNotation = new HashSet<>();
			for(CvCode cvCode : cv.getCvCodes()) {
				// validate concept, make sure the concept is unique
				if( conceptNotation.contains( cvCode.getCode()))
					continue;
				
				conceptNotation.add( cvCode.getCode());
				
				ConceptDTO concept = cvCodeMapper.toDto(cvCode);
				// generate Uri by inserting notation after cvScheme notation
				String uri = version.getUri();
				int lastIndex = uri.lastIndexOf("/");
				if( lastIndex == -1) {
					uri = ConfigurationService.DEFAULT_CV_LINK;
					if(!uri.endsWith("/"))
						uri += "/";
					uri += version.getNotation();
				} else {
					uri = uri.substring(0, lastIndex);
				}
				
				CodeDTO code = new CodeDTO();
				code.setTitleDefinition(concept.getTitle(), concept.getDefinition(), cv.getLanguage());

				if( concept.getParent() != null )
					code.setParent( concept.getParent());
				
				code.setNotation( concept.getNotation() );
				code.setUri( code.getNotation() );
				code.setPosition( concept.getPosition());
				code.setVocabularyId( vocabulary.getId());
				code.setSourceLanguage( cv.getLanguage() );
				code = codeService.save(code);
				
				vocabulary.addCode(code);
				concept.setCodeId( code.getId());
				concept.setVersionId( version.getId() );
				concept.setUri( uri + "#" + concept.getNotation() + "/" +  cv.getLanguage());
				concept = conceptService.save(concept);
				version.addConcept(concept);
//				version = versionService.save(version);
			}
		}
		
		vocabulary.addVersion(version);
		vocabulary.addVers(version);
		
		vocabulary = WorkflowManager.publishSlVersion(vocabulary, agency, version);
		
		// index editor
		vocabularyService.index(vocabulary);
		
		// indexing published codes
		vocabularyService.indexPublish(vocabulary, version);
		
		return vocabulary;
	}
	
	public static VocabularyDTO addVocabularyTranslation(VocabularyDTO vocabulary, AgencyDTO agency, Cv cv) {
		
		// check for existing version
		VersionDTO version = null;
		Optional<VersionDTO> versionByLanguage = vocabulary.getLatestVersionByLanguage( cv.getLanguage());
		if(versionByLanguage.isPresent())
			version = versionByLanguage.get();
		
		if( version == null )
			version = new VersionDTO();
		
		// get the SL version
		VersionDTO slVersion = null;
		Optional<VersionDTO> latestSlVersion = vocabulary.getLatestSlVersion(false);
		if(latestSlVersion.isPresent())
			slVersion = latestSlVersion.get();
		else
			throw new IllegalArgumentException("Unable to find SL version, please make sure that SL is exist");
		
		String cvUriLink = agency.getUri();
		if(cvUriLink == null )
			cvUriLink = ConfigurationService.DEFAULT_CV_LINK;
		if(!cvUriLink.endsWith("/"))
			cvUriLink += "/";
		
		cvUriLink += vocabulary.getNotation() + "/" + cv.getLanguage();
		
		version.setUriSl( vocabulary.getUri() );
		version.setUri( cvUriLink );
		
		version.setNotation(cv.getCode());
		version.setTitle( cv.getTerm() );
		version.setDefinition( cv.getDefinition() );
		version.setNumber( slVersion.getNumber() + ".1");
		version.setStatus( Status.DRAFT.toString() );
		version.setItemType( ItemType.TL.toString());
		version.setLanguage( cv.getLanguage());
		
		version.setPreviousVersion( 0L );
		version.setVocabularyId( vocabulary.getId());
		
		version = versionService.save(version);
		version.setInitialVersion( version.getId());
		version = versionService.save(version);
		
		vocabulary.setVersionByLanguage(cv.getLanguage(), version.getNumber());
		vocabulary.setTitleDefinition(version.getTitle(), version.getDefinition(), version.getLanguage());
		vocabulary = vocabularyService.save(vocabulary);
		
		
		// map concept as well
		if( cv.getCvCodes() != null && cv.getCvCodes().length > 0) {
			// get code on the map
			List<CodeDTO> codes = codeService.findArchivedByVocabularyAndVersion( vocabulary.getId(), slVersion.getId());
			Map<String, CodeDTO> codeMap = CodeDTO.getCodeAsMap(codes);
			
			// set code and create new concept
			for(CvCode cvCode : cv.getCvCodes()) {
				// validate concept, make sure the concept is available in code
				CodeDTO code = codeMap.get( cvCode.getCode());
				if( code == null )
					continue;
				code.setTitleDefinition( cvCode.getTerm(), cvCode.getDefinition(), version.getLanguage());
				codeService.save(code);
				
				ConceptDTO concept = cvCodeMapper.toDto(cvCode);
				concept.setCodeId( code.getId());
				concept.setVersionId( version.getId() );
				
				// generate Uri by inserting notation after cvScheme notation
				String uri = version.getUri();
				int lastIndex = uri.lastIndexOf("/");
				if( lastIndex == -1) {
					uri = ConfigurationService.DEFAULT_CV_LINK;
					if(!uri.endsWith("/"))
						uri += "/";
					uri += version.getNotation();
				} else {
					uri = uri.substring(0, lastIndex);
				}
				
				concept.setUri( uri + "#" + concept.getNotation() + "/" +  cv.getLanguage());
				concept = conceptService.save(concept);
				version.addConcept(concept);
			}
		}
		
		vocabulary.addVersion(version);
		vocabulary.addVers(version);
		
		vocabulary = WorkflowManager.publishTlVersion(vocabulary, agency, version);
		
		// index editor
		vocabularyService.index(vocabulary);
		
		// indexing published codes
		vocabularyService.indexPublish(vocabulary, version);
		

		return vocabulary;
	}

	/**
	 * Publish CV SL version directly 
	 * @param vocabulary
	 * @param version the SL version
	 * @return
	 */
	private static VocabularyDTO publishSlVersion(VocabularyDTO vocabulary, AgencyDTO agency, VersionDTO version) {
		
		version.setStatus( Status.PUBLISHED.toString());
		
		// get workflow codes
		List<CodeDTO> codes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
		

		// add version number to URI
		version.setUri( version.getUri() + "/" + version.getNumber());
		
		version.setPublicationDate( version.getPublicationDate() == null ?  LocalDate.now(): version.getPublicationDate());
		version.setLicenseId( agency.getLicenseId());
		
		String urn =  agency.getCanonicalUri();
		if(urn == null) {
			urn = "urn:" + agency.getName().replace(" ", "") + "-cv:";
		}
		version.setCanonicalUri(urn + version.getTitle() + ":" + version.getNumber() + "-" + version.getLanguage());
		
		// add summary
		version.setSummary(
			(version.getSummary() == null ? "":version.getSummary().replaceAll("(\r\n|\n)", "<br />")) +
			"<strong>" + version.getNumber() + "</strong>"+
			" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication:" + version.getPublicationDate() +
			"<br/>Notes:<br/>" + version.getVersionNotes() + (version.getVersionChanges() != null && !version.getVersionChanges().isEmpty() ? "<br/>Changes:<br/>" + version.getVersionChanges() : "") + "<br/><br/>"
		);
		
		// update concept uri
		for(ConceptDTO concept : version.getConcepts()) {
			concept.setUri( concept.getUri() + "/" + version.getNumber());
			// update concept parent and position based on workflow code
			codes.stream().filter( c -> concept.getNotation().equals( c.getNotation())).findFirst().ifPresent( c -> {
				concept.setParent( c.getParent());
				concept.setPosition( c.getPosition());
			});
			
			conceptService.save( concept );
		}
		
		// save current version
		version = versionService.save(version);
		
		vocabulary.setStatus( Status.PUBLISHED.toString());
		vocabulary.setVersionByLanguage(version.getLanguage(), version.getNumber());
		
		vocabulary.setVersionNumber( version.getNumber() );
		// only set Uri everytime SL published
		vocabulary.setUri( version.getUri());
		vocabulary.setPublicationDate( LocalDate.now());
		vocabulary.setLanguages( VocabularyDTO.getLanguagesFromVersions( vocabulary.getVersions()) );
		vocabulary.setLanguagesPublished( null);
		vocabulary.addLanguagePublished( version.getLanguage());

		vocabulary = vocabularyService.save(vocabulary);
		
		// index for editor
		vocabularyService.index(vocabulary);
		
		
		// clone each code
		List<CodeDTO> newCodes = new ArrayList<>();
		for( CodeDTO eachCode : codes ) {
			//TODO:  probably only need to clone published one
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
		
		// create cv scheme
		CVScheme newCvScheme = new CVScheme();
		newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
		newCvScheme.setId( version.getUri());
		newCvScheme.setContainerId(newCvScheme.getId());
		newCvScheme.setStatus( Status.PUBLISHED.toString() );
		
		
		// Store also Owner Agency, Vocabulary and codes
		// store vocabulary content
		newCvScheme = VocabularyDTO.setCvSchemeByVocabulary(newCvScheme, vocabulary);
		
		// store owner agency
		List<CVEditor> editorSet = new ArrayList<>();
		CVEditor cvEditor = new CVEditor();
		cvEditor.setName( agency.getName());
		cvEditor.setLogoPath( agency.getLogopath());
		
		editorSet.add( cvEditor );
		newCvScheme.setOwnerAgency((ArrayList<CVEditor>) editorSet);
		
		newCvScheme.setCode( vocabulary.getNotation());
		
		newCvScheme.save();
		DDIStore ddiStore = stardatDDIService.saveElement(newCvScheme.ddiStore, "API Import", "Publish Cv");
		// TODO: fix unable to store nameCode
		//refresh cvScheme
		newCvScheme = new CVScheme(ddiStore);
		newCvScheme.setCode( vocabulary.getNotation());
		newCvScheme = new CVScheme( stardatDDIService.saveElement(newCvScheme.ddiStore, "API Import", "Cv add missing nameCode"));
		
		 
		// store complete codeDTOs to CVConcept
		TreeData<CodeDTO> codeTree = new TreeData<>();
		CvCodeTreeUtils.buildCvConceptTree(newCodes, codeTree);
		
		// generate tree concept
		TreeData<CVConcept> cvConceptTree = new TreeData<>();
		cvConceptTree = CvCodeTreeUtils.generateCVConceptTreeFromCodeTree(codeTree, newCvScheme);
		
		// save all cvConcepts and update cvScheme
		storeCvConceptTree( cvConceptTree , newCvScheme);
		
		return vocabulary;
	}
	
	private static VocabularyDTO publishTlVersion(VocabularyDTO vocabulary, AgencyDTO agency, VersionDTO version) {
		version.setStatus( Status.PUBLISHED.toString());
		// add version number to URI
		version.setUri( version.getUri() + "/" + version.getNumber());
		
		version.setPublicationDate( version.getPublicationDate() == null ?  LocalDate.now(): version.getPublicationDate());
		
		version.setLicenseId( agency.getLicenseId());
		
		String urn =  agency.getCanonicalUri();
		if(urn == null) {
			urn = "urn:" + agency.getName().replace(" ", "") + "-cv:";
		}
		version.setCanonicalUri(urn + version.getTitle() + ":" + version.getNumber() + "-" + version.getLanguage());
		
		// add summary
		version.setSummary(
			(version.getSummary() == null ? "":version.getSummary().replaceAll("(\r\n|\n)", "<br />")) +
			"<strong>" + version.getNumber() + "</strong>"+
			" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication:" + version.getPublicationDate() +
			"<br/>Notes:<br/>" + version.getVersionNotes() + (version.getVersionChanges() != null && !version.getVersionChanges().isEmpty() ? "<br/>Changes:<br/>" + version.getVersionChanges() : "") + "<br/><br/>"
		);
		
		// update concept uri
		for(ConceptDTO concept : version.getConcepts()) {
			concept.setUri( concept.getUri() + "/" + version.getNumber());
			conceptService.save( concept );
		}
		
		// save current version
		version = versionService.save(version);
		
		vocabulary.setVersionByLanguage(version.getLanguage(), version.getNumber());
		vocabulary.addLanguagePublished( version.getLanguage());
		vocabulary = vocabularyService.save(vocabulary);
		
		// TODO: Update flatDB
				
		return vocabulary;
	}
	
	private static void storeCvConceptTree(TreeData<CVConcept> cvConceptTree, CVScheme newCvScheme) {
		List<CVConcept> rootItems = cvConceptTree.getRootItems();
		for(CVConcept topCvConcept : rootItems) {
			System.out.println("Store CV-concept:" + topCvConcept.getNotation());
			DDIStore ddiStoreTopCvConcept = stardatDDIService.saveElement(topCvConcept.ddiStore, "API Import", "Add Code " + topCvConcept.getNotation());
			newCvScheme.addOrderedMemberList(ddiStoreTopCvConcept.getElementId());
			
			for( CVConcept childCvConcept : cvConceptTree.getChildren(topCvConcept)) {
				storeCvConceptTreeChild( cvConceptTree, childCvConcept, topCvConcept);
			}
		}
		// store top concept
		newCvScheme.save();
		stardatDDIService.saveElement(newCvScheme.ddiStore, "API Import", "Update Top Concept");
	}

	private static void storeCvConceptTreeChild(TreeData<CVConcept> cvConceptTree, CVConcept cCvConcept,
			CVConcept topCvConcept) {
		System.out.println("Store CV-concept c:" + cCvConcept.getNotation());
		// store cvConcept
		DDIStore ddiStoreCvConcept = stardatDDIService.saveElement(cCvConcept.ddiStore, "API Import", "Add Code " + cCvConcept.getNotation());
		// store narrower
		topCvConcept.addOrderedNarrowerList( ddiStoreCvConcept.getElementId());
		topCvConcept.save();
		stardatDDIService.saveElement(topCvConcept.ddiStore, "User", "Add Code narrower");
		for( CVConcept cvConceptChild : cvConceptTree.getChildren(cCvConcept)) {
			storeCvConceptTreeChild( cvConceptTree, cvConceptChild, cCvConcept);
		}
	}
}
