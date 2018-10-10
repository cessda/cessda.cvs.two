package eu.cessda.cvmanager.service.manager;

import java.util.HashSet;
import java.util.Set;

import org.gesis.wts.service.dto.AgencyDTO;
import org.springframework.stereotype.Service;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.CvMapper;
import eu.cessda.cvmanager.service.mapper.CvCodeMapper;

@Service
public class WorkflowManager {
	private static VocabularyService vocabularyService;
	private static VersionService versionService;
	private static CodeService codeService;
	private static ConceptService conceptService;
	private static CvMapper cvMapper;
	private static CvCodeMapper cvCodeMapper;
	
	public WorkflowManager(VocabularyService vocabularySvc, VersionService versionSvc,
			CodeService codeSvc, ConceptService conceptSvc, CvMapper cvMpr,
			CvCodeMapper cvCodeMpr) {
		WorkflowManager.vocabularyService = vocabularySvc;
		WorkflowManager.versionService = versionSvc;
		WorkflowManager.codeService = codeSvc;
		WorkflowManager.conceptService = conceptSvc;
		WorkflowManager.cvMapper = cvMpr;
		WorkflowManager.cvCodeMapper = cvCodeMpr;
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
				concept = conceptService.save(concept);
				version.addConcept(concept);
//				version = versionService.save(version);
			}
		}
		
		vocabulary.addVersion(version);
		vocabulary.addVers(version);
		
		// index
		vocabularyService.index(vocabulary);
		
		return vocabulary;
	}
	
	public static VersionDTO addVocabularyTranslation(VocabularyDTO vocabulary, Cv cv) {
		VersionDTO versionDTO = new VersionDTO();
		
		return versionDTO;
	}
}
