package eu.cessda.cvmanager.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.cessda.cvmanager.domain.Code;
import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.domain.Version;
import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ImportService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkflowManager;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

@Service
public class ImportServiceImpl implements ImportService{
	
	private final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);
	
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final ConceptService conceptService;
	private final CodeService codeService;
	private final StardatDDIService stardatDDIService;
	private final VocabularyMapper vocabularyMapper;
	// Elasticsearch repo for Editor
	private final VocabularySearchRepository vocabularySearchRepository;
	private final VersionService versionService;
	public ImportServiceImpl(AgencyService agencyService, VocabularyService vocabularyService, CodeService codeService,
			ConceptService conceptService, StardatDDIService stardatDDIService, VersionService versionService,
			VocabularyMapper vocabularyMapper, VocabularySearchRepository vocabularySearchRepository) {
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.stardatDDIService = stardatDDIService;
		this.vocabularyMapper = vocabularyMapper;
		this.vocabularySearchRepository = vocabularySearchRepository;
		this.versionService = versionService;
	}
	

	@Override
	public VersionDTO createCv(Cv cv) {
		// First find agency
		AgencyDTO agency = agencyService.findByName( cv.getAgency());
		if( agency == null ) {
			throw new IllegalArgumentException("The agency is not found");
		}
		// Validate Cv
		if( vocabularyService.getByNotation( cv.getCode()) != null ) {
			throw new IllegalArgumentException("The Cv is already exist");
		}
		VersionDTO version = null;
		
		// process Cv based on type (SL or TL)
		// SL means create both Vocabulary and SL Version
		// TL means create TL version and get existing vocabulary
		if(cv.getType().equals( ItemType.SL.toString())) { // SL version
			// create new vocabulary
			VocabularyDTO newVocabulary = WorkflowManager.createVocabulary(agency, cv);
			Optional<VersionDTO> latestSlVersion = newVocabulary.getLatestSlVersion(false);
			if(latestSlVersion.isPresent())
				version = latestSlVersion.get();
		} else  // TL version
		{
			// find vocabulary
			
			// add version
		}
		
		return version;
	}


	@Override
	public VersionDTO updateCv(Cv cv) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public VersionDTO setCvCode(String uri, CvCode... cvCodes) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public VersionDTO setCvCode(Long versionId, CvCode... cvCodes) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	 /**
     * Import all CVs from Stardat DDI-flatDB to the CV-Manager
     * @Deprecated, since this import method does not support versioning
     */
	@Deprecated
	@Override
	public void importFromStardat() {
		List<DDIStore> ddiSchemes = stardatDDIService.findStudyByElementType(DDIElement.CVSCHEME);
		// prevent duplicated cvscheme
		Set <String> executedCVSchemeId = new HashSet<>();
		for (DDIStore scheme : ddiSchemes) {
			CVScheme cvScheme = new CVScheme( scheme );
			
			if( executedCVSchemeId.contains(cvScheme.getId()))
				continue;
			
			// get codes
			List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType( cvScheme.getContainerId(), DDIElement.CVCONCEPT);
			
			// assign concepts (structured Code based on Version) to latest version entity
			List<CodeDTO> codes = CvCodeTreeUtils.getCodeDTOByConceptTree( CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvScheme) );
			
			executedCVSchemeId.add(cvScheme.getId());
			
			// get Vocabulary
			VocabularyDTO vocabulary = vocabularyService.getByUri( cvScheme.getId() );
			
			if( vocabulary == null )
				vocabulary = vocabularyService.getByNotation( cvScheme.getCode() );
			
			if( vocabulary == null ) {
				vocabulary = VocabularyDTO.generateFromCVScheme(cvScheme);
				
				String owner = cvScheme.getOwnerAgency().get(0).getName();
				AgencyDTO agencyDto = null;
				if( owner != null && !owner.isEmpty() )
					agencyDto =  agencyService.findByName( owner );
				
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
			
			// store vocabulary
			vocabulary = vocabularyService.save(vocabulary);
			
			//saved codes
			List<CodeDTO> savedCodes = new ArrayList<>();
			
			// store code for indexing
			for(CodeDTO code: codes){							
				CodeDTO codeDB = codeService.getByUri( code.getUri() );
				if( codeDB != null)
					code.setId( codeDB.getId());
				code.setDiscoverable( true );
				
				// store code to db
				code.setVocabularyId( vocabulary.getId());
				code = codeService.save(code);
				
				savedCodes.add(code);
				vocabulary.addCode(code);
			};
			
			// assign version
			// workaround to prevent save multiple version
			// TODO: check if version already exist
			for( String lang: vocabulary.getLanguages()) {
				Language langEnum = Language.valueOfEnum(lang);
				
				VersionDTO version = null;
				
				if( version == null)
					version = new VersionDTO();
				
				version.setStatus( Status.DRAFT.toString() );
				if( langEnum.equals( Language.ENGLISH )) {
					version.setItemType( ItemType.SL.toString());
					version.setUri( vocabulary.getUri() );
				} else {
					version.setItemType( ItemType.TL.toString());
				}
				version.setLanguage( lang);
				
				version.setNotation( vocabulary.getNotation() );
				version.setTitle( vocabulary.getTitleByLanguage(langEnum) );
				version.setDefinition(vocabulary.getDefinitionByLanguage(langEnum) );
				version.setPreviousVersion( 0L );
				version.setInitialVersion( 0L );
				version.setCreator( SecurityUtils.getCurrentUserDetails().get().getId() );
//				version.setPublisher( 1L );
				version.setVocabularyId( vocabulary.getId());
				
				version = versionService.save(version);
				
				version.setInitialVersion( version.getId() );
				version = versionService.save(version);
				
				// set concept from code
				Set<ConceptDTO> conceptsFromCodes = CodeDTO.getConceptsFromCodes(savedCodes, langEnum, version.getId());
				for( ConceptDTO concept: conceptsFromCodes ){
					concept = conceptService.save(concept);
					version.addConcept(concept);
				};
				
				vocabulary.addVersion(version);
			}
			
			// reindex nested codes
			vocabularyService.index(vocabulary);
			
		}
		log.debug("DDIFlatDB imported to database");
	}

}
