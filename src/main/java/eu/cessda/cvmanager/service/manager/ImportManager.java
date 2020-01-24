package eu.cessda.cvmanager.service.manager;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.domain.CvCode;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.CvCodeMapper;
import eu.cessda.cvmanager.service.mapper.CvMapper;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ImportManager {
    private static final Logger log = LoggerFactory.getLogger(ImportManager.class);

    private final CvMapper cvMapper;
    private final WorkspaceManager workspaceManager;
    private final WorkflowManager workflowManager;
    private final CvCodeMapper cvCodeMapper;
    private final CodeService codeService;

    public ImportManager(CvMapper cvMapper, WorkspaceManager workspaceManager, WorkflowManager workflowManager, CvCodeMapper cvCodeMapper, CodeService codeService) {
        this.cvMapper = cvMapper;
        this.workspaceManager = workspaceManager;
        this.workflowManager = workflowManager;
        this.cvCodeMapper = cvCodeMapper;
        this.codeService = codeService;
    }

    public VocabularyDTO createVocabulary(AgencyDTO agency, Cv cv) {
        VocabularyDTO vocabulary = VocabularyDTO.createDraft();
        VersionDTO version = cvMapper.toDto(cv);
        version.setStatus( Status.DRAFT.toString() );
        Language language = Language.getByIso( cv.getLanguage() );

        version.setNotation( cv.getCode() );
        version.setTitleAndDefinition( cv.getTerm(), cv.getDefinition() );
        workspaceManager.saveSourceCV(agency, language, vocabulary, version, null);

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
                workspaceManager.saveCodeAndConcept(vocabulary, version, new CodeDTO(), null,
                        concept, null, concept.getNotation(), concept.getTitle(), concept.getDefinition());
            }
        }

        // publish, by assign version with final_review status
        version.setStatus( Status.FINAL_REVIEW.toString());
        workflowManager.forwardStatus(vocabulary, version, agency, null, "1.0", "", "");

        return vocabulary;
    }

    public VocabularyDTO addVocabularyTranslation(VocabularyDTO vocabulary, AgencyDTO agency, Cv cv) {
        // check for existing version
        VersionDTO version = null;
        Language language = Language.getByIso( cv.getLanguage() );
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

        version.setTitleAndDefinition( cv.getTerm(),  cv.getDefinition() );
        workspaceManager.saveTargetCV(agency, language, vocabulary, version, null, null);

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
                workspaceManager.saveCodeAndConcept(vocabulary, version, code, parentCode,
                        concept, slConcept, concept.getNotation(), concept.getTitle(), concept.getDefinition());
            }
        }

        // publish, by assign version with final_review status
        version.setStatus( Status.FINAL_REVIEW.toString());
        workflowManager.forwardStatus(vocabulary, version, agency, null, "1.0.1", "", "");

        return vocabulary;
    }
}
