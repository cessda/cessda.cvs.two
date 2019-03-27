package eu.cessda.cvmanager.service.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vaadin.data.TreeData;
import com.vaadin.ui.Notification;

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
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.CvMapper;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import eu.cessda.cvmanager.utils.WorkflowUtils;
import eu.cessda.cvmanager.service.mapper.CvCodeMapper;

@Service
public class WorkspaceManager {
	private static final Logger log = LoggerFactory.getLogger(WorkspaceManager.class);

	private static ConfigurationService configurationService;
	private static VocabularyService vocabularyService;
	private static VersionService versionService;
	private static CodeService codeService;
	private static ConceptService conceptService;
	private static CvMapper cvMapper;
	private static CvCodeMapper cvCodeMapper;
	private static StardatDDIService stardatDDIService;
	private static ResolverService resolverService;
	private static VocabularyChangeService vocabularyChangeService;
	
	public WorkspaceManager(VocabularyService vocabularySvc, VersionService versionSvc,
			CodeService codeSvc, ConceptService conceptSvc, CvMapper cvMpr,
			CvCodeMapper cvCodeMpr, StardatDDIService stardatDDIService,
			ResolverService resolverService, ConfigurationService configurationService,
			VocabularyChangeService vocabularyChangeService) {
		WorkspaceManager.vocabularyService = vocabularySvc;
		WorkspaceManager.versionService = versionSvc;
		WorkspaceManager.codeService = codeSvc;
		WorkspaceManager.conceptService = conceptSvc;
		WorkspaceManager.cvMapper = cvMpr;
		WorkspaceManager.cvCodeMapper = cvCodeMpr;
		WorkspaceManager.stardatDDIService = stardatDDIService;
		WorkspaceManager.resolverService = resolverService;
		WorkspaceManager.configurationService = configurationService;
		WorkspaceManager.vocabularyChangeService = vocabularyChangeService;
	}

	public static void saveSourceCV( AgencyDTO agency, Language language, VocabularyDTO vocabulary, 
			VersionDTO version, String cvShortName, String cvName, String cvDefinition, String notes ) {
		
		vocabulary.setNotes( notes == null  ? "": notes );
		vocabulary.setTitleDefinition( cvName, cvDefinition, language);
		
		version.setTitle( cvName );
		version.setDefinition( cvDefinition );
		
		if( !vocabulary.isPersisted() )
		{
			vocabulary.setNotation( cvShortName );
			vocabulary.setVersionNumber("1.0");
			vocabulary.setAgencyId( agency.getId());
			vocabulary.setAgencyName( agency.getName());
			vocabulary.setSourceLanguage( language.toString());
			
			
			version.setUri( WorkflowUtils.generateAgencyBaseUri( agency.getUri() ) + vocabulary.getNotation() + "/" + language.toString() );
			version.setNotation( vocabulary.getNotation());
			version.setNumber("1.0");
			version.setItemType( ItemType.SL.toString());
			version.setLanguage( language.toString() );
			version.setPreviousVersion(0L);
			version.setCreator( SecurityUtils.getCurrentUserDetails().get().getId());
			
			// save to database
			vocabulary = vocabularyService.save(vocabulary);
			
			version.setVocabularyId( vocabulary.getId() );
			version = versionService.save( version );
			
			// save initial version
			version.setInitialVersion( version.getId() );
			version = versionService.save( version );
			
			vocabulary.addVersion(version);
			vocabulary.addVers(version);
		} else {
			
			// save to database
			version = versionService.save( version );
			vocabulary = vocabularyService.save(vocabulary);
		}
		
		// index for editor
		vocabularyService.index(vocabulary);
	}
	
	public static void saveTargetCV(AgencyDTO agency, Language language, VocabularyDTO vocabulary, 
			VersionDTO version, String cvName, String cvDefinition, String translatorAgency, String translatorAgencyLink ) {

		version.setTitle( cvName );
		version.setDefinition( cvDefinition );
		
		// store new version
		if( !version.isPersisted()) {

			version.setUriSl( vocabulary.getUri() );
			version.setUri( WorkflowUtils.generateAgencyBaseUri( agency.getUri() ) + vocabulary.getNotation() + "/" + language.toString() );
			
			version.setNotation( vocabulary.getNotation());
			version.setNumber( vocabulary.getVersionNumber() + ".1");
			version.setItemType( ItemType.TL.toString());
			version.setLanguage( language.toString());
			
			version.setPreviousVersion( 0L );
			version.setInitialVersion( 0L );
			version.setVocabularyId( vocabulary.getId());
			version.setTranslateAgency( translatorAgency );
			version.setTranslateAgencyLink( translatorAgencyLink );
			
			version.setCreator( SecurityUtils.getCurrentUserDetails().get().getId());
			
			// get previous version from the same language
			Optional<VersionDTO> latestTlVersion = VersionDTO.getLatestVersion( vocabulary.getVersions(), language.toString(), Status.PUBLISHED.toString());
			if( latestTlVersion.isPresent() ) {
				VersionDTO prevVersion = latestTlVersion.get();
				version.setPreviousVersion( prevVersion.getId() );
				version.setInitialVersion( prevVersion.getInitialVersion() );
				// get last version number from previous version if exist
				if( prevVersion.getNumber().indexOf( vocabulary.getVersionNumber()) == 0 ) {
					int lastDotIndex = vocabulary.getVersionNumber().lastIndexOf(".");
					version.setNumber( vocabulary.getVersionNumber() + "." + (Integer.parseInt(vocabulary.getVersionNumber().substring( lastDotIndex + 1)) + 1));
				}
			}
			
			version = versionService.save(version);
			
			// if no initial version found
			if( version.getInitialVersion() == 0L) {
				// initial version
				version.setInitialVersion( version.getId() );
				version = versionService.save(version);
			}
			// update version in vocabulary
			vocabulary.setVersionByLanguage(language, version.getNumber());
			
			vocabulary.addVersion(version);
			vocabulary.addVers(version);
		} else {
			version = versionService.save(version);
		}
		// store the variable and index
		vocabulary.setTitleDefinition( cvName, cvDefinition, language);
		
		// save to database
		vocabulary = vocabularyService.save(vocabulary);
		
		// index editor
		vocabularyService.index(vocabulary);
	}
	
	
	public static void saveCode(VocabularyDTO vocabulary, VersionDTO version, 
			CodeDTO code, CodeDTO parentCode, ConceptDTO concept, 
			String notation, String term, String definition) {
		// if notation null, it means the TL concept is saved
		if( notation == null )
			notation = code.getNotation();
		else {
			if( !notation.equals( code.getNotation()))
				code.setNotation(notation);
		}
		
		int lastDotIndex = notation.lastIndexOf( '.' );
		if( lastDotIndex > 0) {
			code.setParent( notation.substring(0, lastDotIndex));
			concept.setParent( code.getParent());
		}
		
		boolean isSaveUpdateSlConcept = vocabulary.getSourceLanguage().equals( version.getLanguage());
		
		concept.setNotation( notation );
		concept.setUri( WorkflowUtils.generateCodeUri(version.getUri(), version.getNotation(), notation, version.getLanguage()));
		concept.setTitle( term );
		concept.setDefinition( definition );
		
		code.setTitleDefinition( term, definition, version.getLanguage());
		
		// save new concept regardless SL or TL
		if( !concept.isPersisted()) {
			// in case of the SL, then the code will be new, then set the SL and vocabulary ID
			if( !code.isPersisted() ) {
				code.setSourceLanguage( version.getLanguage());
				code.setVocabularyId( vocabulary.getId() );
			}
			
			// saving SL concept
			if( isSaveUpdateSlConcept ) {
				List<CodeDTO> codeDTOs = codeService.findWorkflowCodesByVocabulary( vocabulary.getId());
				TreeData<CodeDTO> codeTreeData = CvCodeTreeUtils.getTreeDataByCodes( codeDTOs );
				
				if( parentCode == null) {	// if root code
					code.setNotation( notation );
					code.setUri( code.getNotation() );
					codeTreeData.addRootItems(code);
					
				} else { // if child code
					String completeNotation = parentCode.getNotation() + "." + notation;
					code.setNotation( completeNotation );
					code.setUri( code.getNotation() );
					code.setParent( parentCode.getNotation());
					codeTreeData.addItem(parentCode, code);
					
					concept.setNotation( completeNotation );
					concept.setParent(parentCode.getNotation());
					concept.setUri( WorkflowUtils.generateCodeUri(version.getUri(), version.getNotation(), completeNotation, version.getLanguage()));
				}
				
				// save changes on position and parent
				List<CodeDTO> newCodeDTOs = CvCodeTreeUtils.getCodeDTOByCodeTree(codeTreeData);
				for( CodeDTO eachCode: newCodeDTOs) {
					if( !eachCode.isPersisted())
						code = codeService.save(eachCode);
					else
						codeService.save(eachCode);
				}
				
				concept.setPosition( version.getConcepts().size());
				
				vocabulary.addCode(code);
			} 
			else { // save TL concept
				concept.setPosition( code.getPosition() );
				concept.setParent( code.getParent());
				code = codeService.save(code);
			}
			
			concept.setCodeId( code.getId());
			concept.setVersionId( version.getId() );
			
			concept = conceptService.save(concept);
			
			version.addConcept(concept);
			version = versionService.save(version);
		} 
		// update concept
		else {
			code = codeService.save( code );
			concept = conceptService.save( concept );
		}
		
		// indexing editor
		vocabularyService.index(vocabulary);
	}
	
	public static void storeChangeLog( VocabularyDTO vocabulary, VersionDTO version,
			String changeType, String changeDescription) {
		if( !version.isInitialVersion() ) {
			VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
			changeDTO.setVocabularyId( vocabulary.getId());
			changeDTO.setVersionId( version.getId()); 
			changeDTO.setChangeType( changeType );
			changeDTO.setDescription( changeDescription );
			changeDTO.setDate( LocalDateTime.now() );
			UserDetails loggedUser = SecurityUtils.getLoggedUser();
			changeDTO.setUserId( loggedUser.getId() );
			changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
			
			vocabularyChangeService.save(changeDTO);
		} 
	}
}
