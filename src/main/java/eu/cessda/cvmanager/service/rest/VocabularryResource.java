package eu.cessda.cvmanager.service.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

/**
 * REST controller for managing Vocabullary.
 */
@RestController
@RequestMapping("/cv")
public class VocabularryResource {
	private final Logger log = LoggerFactory.getLogger( VocabularryResource.class );
	
	private final VocabularyService vocabularService;
	
	public VocabularryResource( VocabularyService vocabularService ) {
		this.vocabularService = vocabularService;
	}
	
	/**
     * GET  /vocabularies : get all the vocabularies.
     *
     */
    @GetMapping("")
    public List<VocabularyDTO> getAllVocabularies() {
        log.debug("REST request to get all Vocabularies");
        
        List<VocabularyDTO> findAll = vocabularService.findAll();
        return findAll;
    }
}
