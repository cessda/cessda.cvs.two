package eu.cessda.cvmanager.service.rest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

/**
 * REST controller for managing Vocabullary.
 */
@RestController
@RequestMapping("/cv")
public class VocabularyResource {
	private final Logger log = LoggerFactory.getLogger( VocabularyResource.class );
	
	private final VersionService versionService;
	private final VocabularyService vocabularService;
	
	public VocabularyResource( VocabularyService vocabularService, VersionService versionService ) {
		this.versionService = versionService;
		this.vocabularService = vocabularService;
	}
	
	
	/**
     * GET   : get all the vocabularies (RAW).
     *
     */
    @GetMapping("")
    public Map<String,List<String>> getAllVocabularies() {
        log.debug("REST request to get all Vocabularies");
        Map<String,List<String>> agencyCvMap = new LinkedHashMap<>();
        
        List<VocabularyDTO> findAll = vocabularService.findAll();
        for( VocabularyDTO voc : findAll) {
        	if( voc.isWithdrawn() )
        		continue;
        	List<String> notations = agencyCvMap.get( voc.getAgencyName());
        	if( notations == null ) {
        		notations = new ArrayList<>();
        		agencyCvMap.put(voc.getAgencyName(), notations);
        	}
        	notations.add( voc.getNotation() );
        }
        
        for(Map.Entry<String, List<String>> entry : agencyCvMap.entrySet()) {
        	entry.setValue( entry.getValue().stream().sorted().collect( Collectors.toList()) );
        }
                
        return agencyCvMap;
    }
    
    /**
     * GET   : get list of available languages (iso) from a Cv
     * 
     * @param cvCode the cv short definition/notation
     * @return set of published languages in a Cv
     */
    @GetMapping("/{cvCode}")
    public List<String> getVocabularyIsoLanguages( @PathVariable String cvCode ) {
    	log.debug("REST request to get Vocabulary and language");
        List<String> cvLanguages = new ArrayList<>();
        VocabularyDTO vocab = vocabularService.getByNotation(cvCode);
        cvLanguages.addAll( vocab.getLanguagesPublished());
        
        return cvLanguages;
    }
    
    /**
     * GET   : get list of available version numbers from a Cv
     * @param cvCode the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @return set of available versions in a Cv
     */
    @GetMapping("/{cvCode}/{languageIso}")
    public List<String> getVocabularyVersions( @PathVariable String cvCode, @PathVariable String languageIso ) {
    	log.debug("REST request to get Vocabulary and language");
        List<String> cvLangVersions = new ArrayList<>();
        VocabularyDTO vocab = vocabularService.getByNotation(cvCode);
        List<VersionDTO> versions = vocab.getVersionsByLanguage( languageIso );
        
        cvLangVersions = versions.stream()
	         .filter( v -> v.getStatus().equals( Status.PUBLISHED.toString()))
	         .map( v -> v.getNumber())
	         .collect( Collectors.toList());
        
        return cvLangVersions;
    }
    
    /**
     * GET   : get the detail of a CV in specific language and version
     * @param cvCode the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @param version the Cv version number 
     * @return detail of CV in specific language and version
     */
    @GetMapping("/{cvCode}/{languageIso}/{version}")
    public VersionDTO getVocabularyDetails( @PathVariable String cvCode, @PathVariable String languageIso, @PathVariable String version ) {
    	log.debug("REST request to get Vocabulary and language");
    	VersionDTO versionDTO = versionService.findOneByNotationLangVersion( cvCode, languageIso, version );

        return versionDTO;
    }
	
	/**
     * GET  /raw : get all the vocabularies (RAW).
     *
     */
    @GetMapping("/raw")
    public List<VocabularyDTO> getAllVocabulariesRaw() {
        log.debug("REST request to get all Vocabularies");
        
        List<VocabularyDTO> findAll = vocabularService.findAll();
        return findAll;
    }
}
