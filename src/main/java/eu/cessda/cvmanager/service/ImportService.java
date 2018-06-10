package eu.cessda.cvmanager.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.cessda.cvmanager.domain.Code;
import eu.cessda.cvmanager.domain.Version;
import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

@Service
public class ImportService {
	
	private final Logger log = LoggerFactory.getLogger(ImportService.class);
	
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final CodeService codeService;
	private final StardatDDIService stardatDDIService;
	private final VocabularyMapper vocabularyMapper;
	// Elasticsearch repo for Editor
	private final VocabularySearchRepository vocabularySearchRepository;
	private final VersionService versionService;
	public ImportService(AgencyService agencyService, VocabularyService vocabularyService, CodeService codeService,
			StardatDDIService stardatDDIService, VersionService versionService,
			VocabularyMapper vocabularyMapper, VocabularySearchRepository vocabularySearchRepository) {
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		this.stardatDDIService = stardatDDIService;
		this.vocabularyMapper = vocabularyMapper;
		this.vocabularySearchRepository = vocabularySearchRepository;
		this.versionService = versionService;
	}
	
	public void importFromStardat() {
		List<DDIStore> ddiSchemes = stardatDDIService.findStudyByElementType(DDIElement.CVSCHEME);
		// prevent duplicated cvscheme
		Set <String> executedCVSchemeId = new HashSet<>();
		for (DDIStore scheme : ddiSchemes) {
			CVScheme cvScheme = new CVScheme( scheme );
			
			if( executedCVSchemeId.contains(cvScheme.getId()))
				continue;
			
			executedCVSchemeId.add(cvScheme.getId());
			
			// get Vocabulary
			VocabularyDTO vocabulary = vocabularyService.getByUri( cvScheme.getId() );
			
			if( vocabulary == null )
				vocabulary = vocabularyService.getByNotation( cvScheme.getCode() );
			
			if( vocabulary == null ) {
				vocabulary = VocabularyDTO.generateFromCVScheme(cvScheme);
				
				List<CVEditor> owners = cvScheme.getEditor();
				AgencyDTO agencyDto = null;
				if( owners != null && !owners.isEmpty() )
					agencyDto =  agencyService.findByName( owners.get( 0 ).getName());
				
				if( agencyDto == null)
					agencyDto = agencyService.findOne(1L);
				
				vocabulary.setAgencyId( agencyDto.getId());
				vocabulary.setAgencyName( agencyDto.getName());
			}
			else
				vocabulary = VocabularyDTO.generateFromCVScheme(vocabulary, cvScheme);
			
			
			vocabulary.setStatus( Status.DRAFT.toString() );
			vocabulary.addStatus( Status.DRAFT.toString() );
			vocabulary.setDiscoverable( true );
			vocabulary.setPublicationDate( LocalDate.now());
			
			// assign version
			// workaround to prevent save multiple version
			// TODO: check if version already exist
			if( !vocabulary.isPersisted()) {
				for( String lang: vocabulary.getLanguages()) {
					Language langEnum = Language.getEnumByName(lang);
					
					VersionDTO version = null;
					
					if( version == null)
						version = new VersionDTO();
					
					version.setStatus( Status.DRAFT.toString() );
					if( lang.equals( "english")) {
						version.setItemType( ItemType.SL.toString());
						version.setNumber( "1.0" );
					} else {
						version.setItemType( ItemType.TL.toString());
						version.setNumber( "1.0.1" );
					}
					version.setLanguage( lang);
					version.setUri( vocabulary.getUri() );
					version.setNotation( vocabulary.getNotation() );
					version.setTitle( vocabulary.getTitleByLanguage(langEnum) );
					version.setDefinition(vocabulary.getDefinitionByLanguage(langEnum) );
					version.setPreviousVersion( 0L );
					version.setInitialVersion( 0L );
					version.setCreator( 1L );
					version.setPublisher( 1L );
					
					System.out.println( version.getNotation() + " - " + version.getUri() + " " + version.getLanguage() + " " + version.getInitialVersion());
					if( version.getNotation() == null || version.getUri() == null || version.getLanguage() == null ) {
						System.out.println( "Error: " + vocabulary.getNotation() + " - " + version.getNotation() + " - " + version.getUri() + " " + version.getLanguage());
					}
					vocabulary.addVersions(version);
				}
			}
			
			// get codes
			List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType( cvScheme.getContainerId(), DDIElement.CVCONCEPT);
			
//			for (DDIStore concept : ddiConcepts) {
//				CVConcept cvConcept = new CVConcept( concept );
//				
//				// get Code
//				CodeDTO code = codeService.getByUri( cvConcept.getContainerId() );
//				
//				if( code == null )
//					code = codeService.getByNotation( cvConcept.getNotation() );
//				
//				if( code == null ) {
//					code = CodeDTO.generateFromCVConcept(cvConcept);
//					code.setVocabularyId( vocabulary.getId() );
//				}
//				else
//					code = CodeDTO.generateFromCVConcept(code, cvConcept);
//				
//				code.setVocabularyId( vocabulary.getId());
//				codeService.save(code);
//				
//			}
			// assign concepts (structured Code based on Version) to latest version entity
			List<CodeDTO> codes = CvCodeTreeUtils.getCodeDTOByConceptTree( CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvScheme) );

			//saved codes
			List<CodeDTO> savedCodes = new ArrayList<>();
			
			// store code for indexing
			for(CodeDTO code: codes){							
				CodeDTO codeDB = codeService.getByUri( code.getUri() );
				if( codeDB != null)
					code.setId( codeDB.getId());
				code.setDiscoverable( true );
				
				// store code to db
				code = codeService.save(code);
				
				savedCodes.add(code);
				vocabulary.addCode(code);
			};
			
			if( !vocabulary.isPersisted()) {
				for( String lang : vocabulary.getLanguages()){
					Language langEnum = Language.getEnumByName(lang);
					
					VersionDTO.getLatestVersion( vocabulary.getVersions(), lang, null).ifPresent( versionDTO -> {
						Set<ConceptDTO> conceptsFromCodes = CodeDTO.getConceptsFromCodes(savedCodes, langEnum);
						versionDTO.setConcepts(conceptsFromCodes);
					});
				};
			}
			
			// store vocabulary
			vocabulary = vocabularyService.save(vocabulary);
			
			// store code once more now with vocabularry
	        // store code if exist
	        for( CodeDTO code: savedCodes) {
	        	code.setVocabularyId( vocabulary.getId());
	        	code = codeService.save(code);
	        }
			
			// reindex nested codes
			vocabulary.setVers( vocabulary.getVersions());
			Vocabulary vocab = vocabularyMapper.toEntity( vocabulary);
			vocabularySearchRepository.save( vocab );
			
		}
		log.debug("DDIFlatDB imported to database");
	}
}
