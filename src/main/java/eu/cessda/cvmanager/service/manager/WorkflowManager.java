package eu.cessda.cvmanager.service.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.utils.WorkflowUtils;

@Service
public class WorkflowManager {
	private static final Logger log = LoggerFactory.getLogger(WorkflowManager.class);
	private final ConfigurationService configurationService;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final ResolverService resolverService;
	

	public WorkflowManager(VocabularyService vocabularySvc, VersionService versionSvc,
			CodeService codeSvc, ConceptService conceptSvc, ResolverService resolverService,
		   ConfigurationService configurationService) {
		this.vocabularyService = vocabularySvc;
		this.versionService = versionSvc;
		this.codeService = codeSvc;
		this.conceptService = conceptSvc;
		this.resolverService = resolverService;
		this.configurationService =configurationService;
	}
	
	/**
	 * Get next forwarded status
	 * @param currentStatus
	 * @return
	 */
	public Status getForwardStatus( Status currentStatus) {
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
	
	
	public VersionDTO forwardStatus( VocabularyDTO vocabulary, VersionDTO version, AgencyDTO agency,
			List<VersionDTO> latestTlVersions, String versionNumber, String versionNotes, String versionChanges) {
		// forward current version
		version.setStatus( getForwardStatus( version.getEnumStatus() ).toString() );
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
//					createAndStoreCvScheme(vocabulary, version, agency, newCodes);
					
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

					// TODO: also to assign new code-id for concept

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
					log.error(e.getMessage(), e);
				}
				
				// indexing published codes
				vocabularyService.indexPublish(vocabulary, latestSlVersion);
				
				break;
			default:
				break;
	}
		
		
		
		return version;
	}

	private List<CodeDTO> clonePublishedVersionCode(VersionDTO version, List<CodeDTO> codes) {
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


	private void doTlCvCloning(VocabularyDTO vocabulary, VersionDTO currentVersion, AgencyDTO agency,
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
}
