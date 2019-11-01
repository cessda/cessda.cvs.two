package eu.cessda.cvmanager.service.manager;

import com.vaadin.data.TreeData;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.*;
import eu.cessda.cvmanager.service.dto.*;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;
import eu.cessda.cvmanager.utils.WorkflowUtils;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceManager {
	private static final Logger log = LoggerFactory.getLogger(WorkspaceManager.class);

	private final ConfigurationService configurationService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final VocabularyChangeService vocabularyChangeService;
	
	public WorkspaceManager(VocabularyService vocabularySvc, VersionService versionSvc,
			CodeService codeSvc, ConceptService conceptSvc, ConfigurationService configurationService,
			VocabularyChangeService vocabularyChangeService) {
		this.vocabularyService = vocabularySvc;
		this.versionService = versionSvc;
		this.codeService = codeSvc;
		this.conceptService = conceptSvc;
		this.configurationService = configurationService;
		this.vocabularyChangeService = vocabularyChangeService;
	}

	public void saveSourceCV( AgencyDTO agency, Language language, VocabularyDTO vocabulary,
			VersionDTO version, String notes ) {
		vocabulary.setNotes( notes == null  ? "": notes );
		vocabulary.setTitleDefinition( version.getTitle(), version.getDefinition(), language);
		version.setNotes( notes == null  ? "": notes );
		
		if( !vocabulary.isPersisted() )
		{
			log.info( "Saving new SL version of {}", version.getNotation() );
			vocabulary.setNotation( version.getNotation() );
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
			UserDetails loggedUser = CvManagerSecurityUtils.getLoggedUser();

			if( loggedUser != null )
				version.setCreator( loggedUser.getId() );

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
			log.info( "Updating  SL version of {}", version.getNotation() );
			versionService.save( version );
			vocabulary = vocabularyService.save(vocabulary);
		}
		
		// index for editor
		vocabularyService.index(vocabulary);
	}
	
	public void saveTargetCV(AgencyDTO agency, Language language, VocabularyDTO vocabulary,
			VersionDTO tlVersion, String translatorAgency, String translatorAgencyLink ) {

		// target (TL) CV is based in existing published source (SL) version
		// therefore get the latestPublished SL version first
		VersionDTO slVersion = null;
		Optional<VersionDTO> latestSlVersion = vocabulary.getLatestSlVersion( true );
		if(latestSlVersion.isPresent())
			slVersion = latestSlVersion.get();
		else {
			log.error( "SL version {} not found", vocabulary.getNotation() );
			throw new IllegalArgumentException("SL version " + vocabulary.getNotation() + " not found");
		}
		
		// store new version
		if( !tlVersion.isPersisted()) {
			String languageIso = language.toString();
			log.info( "Saving new TL version of {}, with language {}", vocabulary.getNotation(), languageIso);
			tlVersion.setUriSl( vocabulary.getUri() );

			tlVersion.setUri( WorkflowUtils.generateAgencyBaseUri( agency.getUri() ) + vocabulary.getNotation() + "/" + languageIso );
			tlVersion.setNotation( vocabulary.getNotation());
			tlVersion.setNumber( vocabulary.getVersionNumber() + ".1");
			tlVersion.setItemType( ItemType.TL.toString());
			tlVersion.setLanguage( languageIso );
			tlVersion.setPreviousVersion( 0L );
			UserDetails loggedUser = CvManagerSecurityUtils.getLoggedUser();
			if( loggedUser != null )
				tlVersion.setCreator( loggedUser.getId() );

			tlVersion.setInitialVersion( 0L );
			tlVersion.setVocabularyId( vocabulary.getId());
			tlVersion.setTranslateAgency( translatorAgency );
			tlVersion.setTranslateAgencyLink( translatorAgencyLink );

			// check if previous version exist and perform cloning
			clonePreviousVersion(agency, language, vocabulary, slVersion, tlVersion);

			// save after assign everything
			tlVersion = versionService.save(tlVersion);

			// if no initial version found
			if( tlVersion.getInitialVersion() == 0L) {
				tlVersion.setInitialVersion(tlVersion.getId());
				tlVersion = versionService.save(tlVersion);
			}

			// save concept if exist
			storeTLConceptWithCode(language, vocabulary, tlVersion, slVersion);

			// update version in vocabulary
			vocabulary.setVersionByLanguage(language, tlVersion.getNumber());
			
			vocabulary.addVersion(tlVersion);
			vocabulary.addVers(tlVersion);
		} else {
			tlVersion = versionService.save(tlVersion);
		}
		// store the variable and index
		vocabulary.setTitleDefinition( tlVersion.getTitle(), tlVersion.getDefinition(), language);
		
		// save to database
		vocabulary = vocabularyService.save(vocabulary);
		
		// index editor
		vocabularyService.index(vocabulary);
	}

	private void storeTLConceptWithCode(Language language, VocabularyDTO vocabulary, VersionDTO tlVersion, VersionDTO slVersion) {
		if( !tlVersion.getConcepts().isEmpty() ) {
			// get codes from latestSL, since codes need to be match between SL and TL on the same slVersion
			List<CodeDTO> codes = codeService.findByVocabularyAndVersion( vocabulary.getId(), slVersion.getId());
			// save concepts with codes ID
			for( CodeDTO code: codes) {
				ConceptDTO
					.getConceptFromCode(tlVersion.getConcepts(), code.getNotation())
					.ifPresent( c ->{
						c.setCodeId( code.getId());
						// in case code need update
						if( code.getTitleByLanguage(language) == null ||
							!compareString( code.getTitleByLanguage(language) ,c.getTitle()) ||
							!compareString( code.getDefinitionByLanguage(language) ,c.getDefinition())) {
							code.setTitleDefinition(c.getTitle(), c.getDefinition(), language);
							codeService.save(code);
						}
					});
			}
			// save versionId property
			for( ConceptDTO newConcept: tlVersion.getConcepts()) {
				newConcept.setVersionId( tlVersion.getId());
				conceptService.save(newConcept);
			}
		}
	}

	private void clonePreviousVersion(AgencyDTO agency, Language language, VocabularyDTO vocabulary, VersionDTO slVersion, VersionDTO versionTl) {
		// get previous version from the same language
		Optional<VersionDTO> latestTlVersion = VersionDTO.getLatestVersion( vocabulary.getVersions(), language.toString(), null);
		if( latestTlVersion.isPresent() ) {
			// if exist then reassign the version number and perform cloning
			VersionDTO prevVersion = latestTlVersion.get();
			String versionNumber = vocabulary.getVersionNumber();
			// get last version number from previous version if exist
			if( prevVersion.getStatus().equals(Status.PUBLISHED.toString()) && prevVersion.getNumber().indexOf( vocabulary.getVersionNumber()) == 0 ) {
				int lastDotIndex = vocabulary.getVersionNumber().lastIndexOf('.');
				versionNumber += "." + (Integer.parseInt(vocabulary.getVersionNumber().substring( lastDotIndex + 1)) + 1);
			} else {
				versionNumber += ".1";
			}

			VersionDTO.clone(versionTl, prevVersion, slVersion, CvManagerSecurityUtils.getLoggedUser().getId(), versionNumber,
					slVersion.getLicenseId(), WorkflowUtils.generateAgencyBaseUri( agency.getUri()), slVersion.getDdiUsage());
		}
	}

	public boolean compareString(String str1, String str2) {
	    return (str1 == null ? str2 == null : str1.equals(str2));
	}
	
	public void saveCodeAndConcept(VocabularyDTO vocabulary, VersionDTO version,
								   CodeDTO code, CodeDTO parentCode, ConceptDTO concept,
								   ConceptDTO slConcept, String notation, String term, String definition) {
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
				code = saveSlConcept(vocabulary, version, code, parentCode, concept, notation);
			} 
			else { // save TL concept
				// setPotitiona and setParent is not necessary since conceptTl will follow conceptSL
				concept.setPosition( code.getPosition() );
				concept.setParent( code.getParent());
				if( slConcept != null )
					concept.setSlConcept(slConcept.getId());
				code = codeService.save(code);
			}
			
			concept.setCodeId( code.getId());
			concept.setVersionId( version.getId() );
			
			concept = conceptService.save(concept);
			
			version.addConcept(concept);
			versionService.save(version);
		} 
		// update concept
		else {
			codeService.save( code );
			conceptService.save( concept );
		}
		
		// indexing editor
		vocabularyService.index(vocabulary);
	}

	private CodeDTO saveSlConcept(VocabularyDTO vocabulary, VersionDTO version, CodeDTO code, CodeDTO parentCode, ConceptDTO concept, String notation) {
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
		return code;
	}

	public void storeChangeLog( VocabularyDTO vocabulary, VersionDTO version,
			String changeType, String changeDescription) {
		if( !version.isInitialVersion() ) {
			VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
			changeDTO.setVocabularyId( vocabulary.getId());
			changeDTO.setVersionId( version.getId()); 
			changeDTO.setChangeType( changeType );
			changeDTO.setDescription( changeDescription );
			changeDTO.setDate( LocalDateTime.now() );
			UserDetails loggedUser = CvManagerSecurityUtils.getLoggedUser();
			changeDTO.setUserId( loggedUser.getId() );
			changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
			
			vocabularyChangeService.save(changeDTO);
		} 
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public VocabularyService getVocabularyService() {
		return vocabularyService;
	}

	public VersionService getVersionService() {
		return versionService;
	}

	public CodeService getCodeService() {
		return codeService;
	}

	public ConceptService getConceptService() {
		return conceptService;
	}
}
