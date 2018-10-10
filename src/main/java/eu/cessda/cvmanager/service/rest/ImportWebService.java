package eu.cessda.cvmanager.service.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.cessda.cvmanager.domain.Cv;
import eu.cessda.cvmanager.service.ImportService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VersionDTO;

/**
 * REST controller for managing Import.
 */
@RestController
@RequestMapping
("/import")
public class ImportWebService {
	private final Logger log = LoggerFactory.getLogger( ImportWebService.class );
	
	private final VersionService versionService;
	private final VocabularyService vocabularService;
	private final ImportService importService;
	
	public ImportWebService( VocabularyService vocabularService, VersionService versionService,
			ImportService importService) {
		this.versionService = versionService;
		this.vocabularService = vocabularService;
		this.importService = importService;
	}
	
	@PostMapping("/cv")
	@Transactional
	public ResponseEntity<VersionDTO> createCv(@Valid @RequestBody Cv cv) throws URISyntaxException{
		VersionDTO newVersion = importService.createCv(cv);
		return ResponseEntity.created(new URI("/api/ul-events/" + newVersion.getId())).body(newVersion);
	}
}
