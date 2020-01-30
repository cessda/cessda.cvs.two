package eu.cessda.cvmanager.service.rest;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.ExportService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.utils.VersionUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


/**
 * REST controller for managing Vocabullary.
 */
@RestController
@RequestMapping("/v1")
public class VocabularyResource {
	private final Logger log = LoggerFactory.getLogger( VocabularyResource.class );
	
	private final VersionService versionService;
	private final VocabularyService vocabularService;
	private final ExportService exportService;
	
	public VocabularyResource( VocabularyService vocabularService, VersionService versionService,
			ExportService exportService) {
		this.versionService = versionService;
		this.vocabularService = vocabularService;
		this.exportService = exportService;
	}
	
	
	/**
     * GET   : get all the vocabularies .
     *
     * @return map of Agencies and CVs code
     */
    @SuppressWarnings("unchecked")
	@GetMapping("/vocabulary")
    @ApiOperation(value = "Get list of the agencies and the controlled vocabularies with the details rest API link.")
    public Map<String,Object> getAllVocabularies() {
    	// example structure:
    	// e.g:
//    	"DDI":{
//    		"CsvRow":{
//                "en":{
//    				"1.0":"/v1/Vocabulary/CsvRow/en/1.0",
//    				"1.1":"/v1/Vocabulary/CsvRow/en/1.1"
//    			}
//    		}
//    	}
        log.debug( "REST request to get all Vocabularies" );
        Map<String, Object> agencyCvMap = new TreeMap<>();

        List<VocabularyDTO> vocabularies = vocabularService.findAll();

        vocabularies = vocabularies.stream().sorted( Comparator.comparing( VocabularyDTO::getNotation ) ).collect( Collectors.toList() );

        for ( VocabularyDTO voc : vocabularies )
        {
            if ( Boolean.TRUE.equals( voc.isWithdrawn() ) )
                continue;
            Object vocabMap = agencyCvMap.computeIfAbsent( voc.getAgencyName(), k -> new LinkedHashMap<>() );
            List<VersionDTO> versions = voc.getVersions().stream().sorted( ( c1, c2 ) -> VersionUtils.compareVersion( c1.getNumber(), c2.getNumber() ) ).collect( Collectors.toList() );
            for ( VersionDTO version : versions )
            {
                Object langMap = ( (Map<String, Object>) vocabMap )
                        .computeIfAbsent( version.getNotation(), k -> new LinkedHashMap<>() );
                Object versionMap = ( (Map<String, Object>) langMap )
                        .computeIfAbsent( version.getLanguage() + "(" + version.getItemType() + ")", k -> new LinkedHashMap<>() );
                ( (Map<String, Object>) versionMap ).put( version.getNumber(), "/v1/VocabularyDetails/" + version.getNotation() + "/" + version.getLanguage() + "/" + version.getNumber() );
            }

        }

        return agencyCvMap;
    }

    /**
     * GET   : get list of available languages (iso) from a Cv
     *
     * @param cvCode the cv short definition/notation
     * @return set of published languages in a Cv
     */
    @GetMapping( "/vocabulary-languages/{cvCode}" )
    @ApiOperation( value = "Get list of the languages in iso-format from a controlled vocabulary." )
    public List<String> getVocabularyIsoLanguages(
            @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String cvCode )
    {
        VocabularyDTO vocab = vocabularService.getByNotation( cvCode );
        return new ArrayList<>( vocab.getLanguagesPublished() );
    }

    /**
     * GET   : get list of available version numbers from a Cv
     *
     * @param cvCode      the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @return set of available versions in a Cv
     */
    @GetMapping( "/vocabulary-version/{cvCode}/{languageIso}" )
    @ApiOperation( value = "Get list of the version from a controlled vocabulary in a specific language" )
    public ResponseEntity<List<String>> getVocabularyVersions(
            @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String cvCode,
            @ApiParam( value = "the CV language in iso format, e.g. en" ) @PathVariable String languageIso )
    {
        List<String> cvLangVersions;
        VocabularyDTO vocab = vocabularService.getByNotation( cvCode );

        // If the vocab was returned
        if ( vocab != null )
        {
            List<VersionDTO> versions = vocab.getVersionsByLanguage( languageIso );
            cvLangVersions = versions.stream()
                    .filter( v -> v.getStatus().equals( Status.PUBLISHED.toString() ) )
                    .map( VersionDTO::getNumber )
                    .collect( Collectors.toList() );
        }
        else
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok( cvLangVersions );
    }

    /**
     * GET   : get the details of a CV in specific language and version
     * @param cvCode the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @param version the Cv version number 
     * @return detail of CV in specific language and version
     */
    @GetMapping("/vocabulary-details/{cvCode}/{languageIso}/{version}")
    @ApiOperation(value = "Get the CV details in JSON format.")
    public VersionDTO getVocabularyDetails( 
    		@ApiParam(value = "the CV short definition/notation, e.g. AnalysisUnit") @PathVariable String cvCode, 
    		@ApiParam(value = "the CV language in iso format, e.g. en") @PathVariable String languageIso, 
    		@ApiParam(value = "the CV version number, e.g. 1.0")@PathVariable String version ) {

        return versionService.findOneByNotationLangVersion( cvCode, languageIso, version );
    }
	
    /**
     * GET   : get the details SKOS-rdf of a CV in specific language and version
     * @param cvCode the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @param version the Cv version number 
     * @return detail of CV in specific language and version
     */
    @GetMapping("/vocabulary-details-skos/{cvCode}/{languageIso}/{version}")
    @ApiOperation(value = "Get the CV details in SKOS-rdf format.")
    public String getAllVocabulariesRaw( 
    		@ApiParam(value = "the CV short definition/notation, e.g. AnalysisUnit") @PathVariable String cvCode, 
    		@ApiParam(value = "the CV language in iso format, e.g. en") @PathVariable String languageIso, 
    		@ApiParam(value = "the CV version number, e.g. 1.0")@PathVariable String version ) {
		
        return exportService.getGeneratedSkosRdf(cvCode, languageIso, version);
    }
}
