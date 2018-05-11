package eu.cessda.cvmanager.service;

import java.time.LocalDate;
import java.util.List;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.cessda.cvmanager.domain.Vocabulary;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.dto.CodeDTO;
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
	private final VocabularySearchRepository vocabularySearchRepository;
	public ImportService(AgencyService agencyService, VocabularyService vocabularyService, CodeService codeService,
			StardatDDIService stardatDDIService, VocabularyMapper vocabularyMapper, VocabularySearchRepository vocabularySearchRepository) {
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		this.stardatDDIService = stardatDDIService;
		this.vocabularyMapper = vocabularyMapper;
		this.vocabularySearchRepository = vocabularySearchRepository;
	}
	
	public void importFromStardat() {
		List<DDIStore> ddiSchemes = stardatDDIService.findStudyByElementType(DDIElement.CVSCHEME);
		for (DDIStore scheme : ddiSchemes) {
			CVScheme cvScheme = new CVScheme( scheme );
			
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
			
			vocabulary.setDiscoverable( true );
			vocabulary.setPublicationDate( LocalDate.now());
			vocabulary = vocabularyService.save(vocabulary);
			
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
			List<CodeDTO> codes = CvCodeTreeUtils.getCodeDTOByConceptTree( CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvScheme) );
			for(CodeDTO code: codes){
				code.setVocabularyId( vocabulary.getId() );
				
				CodeDTO codeDB = codeService.getByUri( code.getUri() );
				if( codeDB != null)
					code.setId( codeDB.getId());
				code.setDiscoverable( true );
				code = codeService.save(code);
				vocabulary.addCode(code);
			};
			
			// reindex nested codes
			Vocabulary vocab = vocabularyMapper.toEntity( vocabulary);
			vocabularySearchRepository.save( vocab );
		}
		log.debug("DDIFlatDB imported to database");
	}
}
