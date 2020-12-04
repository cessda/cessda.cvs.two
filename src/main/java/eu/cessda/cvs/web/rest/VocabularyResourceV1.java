package eu.cessda.cvs.web.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.cessda.cvs.service.ExportService;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.utils.VersionUtils;
import eu.cessda.cvs.utils.VocabularyUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.keyValue;


/**
 * REST controller for managing Vocabulary.
 */
@RestController
@RequestMapping( "/v1" )
public class VocabularyResourceV1
{
    private static final String CV_CODE = "cvCode";
    private static final String LANGUAGE_ISO = "languageIso";
    private static final String VERSION = "version";
    public static final String JSON_FORMAT = ".json";
    private static final Logger log = LoggerFactory.getLogger( VocabularyResourceV1.class );

    private final VocabularyService vocabularyService;

    public VocabularyResourceV1(VocabularyService vocabularyService)
    {
        this.vocabularyService = vocabularyService;
    }


    /**
     * GET   : get all the vocabularies .
     *
     * @return map of Agencies and CVs code
     */
    @CrossOrigin
    @GetMapping( "/vocabulary" )
    @ApiOperation( value = "Get list of the agencies and the controlled vocabularies with the details rest API link." )
    public Map<String, Map<String, Map<String, Map<String, String>>>> getAllVocabularies()
    {
        log.debug( "REST: Getting all Vocabularies" );
        Map<String, Map<String, Map<String, Map<String, String>>>> agencyCvMap = new TreeMap<>();

        List<VocabularyDTO> vocabularies = new ArrayList<>();

        List<Path> jsonPaths = vocabularyService.getPublishedCvPaths();
        vocabularies.addAll( jsonPaths.stream().map(VocabularyUtils::generateVocabularyByPath).collect(Collectors.toList()));

        vocabularies = vocabularies.stream().sorted( Comparator.comparing( VocabularyDTO::getNotation ) ).collect( Collectors.toList() );

        vocabularies.stream().filter( voc -> !Boolean.TRUE.equals( voc.isWithdrawn() ) ).forEach( voc ->
        {
            Map<String, Map<String, Map<String, String>>> vocabMap = agencyCvMap.computeIfAbsent( voc.getAgencyName(), k -> new LinkedHashMap<>() );
            List<VersionDTO> versions = voc.getVersions().stream()
                    .sorted( ( c1, c2 ) -> VersionUtils.compareVersion( c1.getNumber(), c2.getNumber() ) )
                    .collect( Collectors.toList() );
            versions.forEach( version ->
            {
                Map<String, Map<String, String>> langMap = vocabMap.computeIfAbsent( version.getNotation(), k -> new LinkedHashMap<>() );
                Map<String, String> versionMap = langMap.computeIfAbsent(
                        version.getLanguage() + "(" + version.getItemType() + ")", k -> new LinkedHashMap<>() );
                versionMap.put( version.getNumber(),
                        "/v1/VocabularyDetails/" + version.getNotation() + "/" + version.getLanguage() + "/" +
                                version.getNumber() );
                for (Map<String, Object> versionHistory : version.getVersionHistories()) {
                    versionMap.put( versionHistory.get("version").toString(),
                        "/v1/VocabularyDetails/" + version.getNotation() + "/" + version.getLanguage() + "/" +
                            versionHistory.get("version").toString() );
                }
            } );
        } );

        return agencyCvMap;
    }

    /**
     * GET   : get list of available languages (iso) from a Cv
     *
     * @param cvCode the cv short definition/notation
     * @return set of published languages in a Cv
     */
    @CrossOrigin
    @GetMapping( "/vocabulary-languages/{cvCode}" )
    @ApiOperation( value = "Get list of the languages in iso-format from a controlled vocabulary." )
    public ResponseEntity<List<String>> getVocabularyIsoLanguages(
            @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String cvCode )
    {
        log.debug( "REST: Getting list of languages for {}", keyValue( CV_CODE, cvCode ) );
        VocabularyDTO vocab = VocabularyUtils.generateVocabularyByPath(vocabularyService.getPublishedCvPath( cvCode ));
        if ( vocab != null )
        {
            return ResponseEntity.ok( new ArrayList<>( vocab.getVersions().stream().map(VersionDTO::getLanguage).collect(Collectors.toList()) ) );
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET   : get list of available version numbers from a Cv
     *
     * @param cvCode      the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @return set of available versions in a Cv
     */
    @CrossOrigin
    @GetMapping( "/vocabulary-version/{cvCode}/{languageIso}" )
    @ApiOperation( value = "Get list of the version from a controlled vocabulary in a specific language" )
    public ResponseEntity<List<String>> getVocabularyVersions(
            @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String cvCode,
            @ApiParam( value = "the CV language in iso format, e.g. en" ) @PathVariable String languageIso )
    {
        log.debug( "REST: Getting list of versions for {}, {}", keyValue( CV_CODE, cvCode ), keyValue( LANGUAGE_ISO, languageIso ) );
        VocabularyDTO vocab = VocabularyUtils.generateVocabularyByPath(vocabularyService.getPublishedCvPath( cvCode ));

        // If the vocab was returned
        if ( vocab != null )
        {
            List<String> cvLangVersions = vocab.getVersions().stream()
                    .filter( v -> v.getLanguage().equals( languageIso ) )
                    .map( VersionDTO::getNumber )
                    .collect( Collectors.toList() );
            if ( !cvLangVersions.isEmpty() )
            {
                return ResponseEntity.ok( cvLangVersions );
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET   : get the details of a CV in specific language and version
     *
     * @param cvCode      the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @param version     the Cv version number
     * @return detail of CV in specific language and version
     */
    @CrossOrigin
    @GetMapping( "/vocabulary-details/{cvCode}/{languageIso}/{version}" )
    @ApiOperation( value = "Get the CV details in JSON format." )
    public ResponseEntity<String> getVocabularyDetails(
            @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String cvCode,
            @ApiParam( value = "the CV language in iso format, e.g. en" ) @PathVariable String languageIso,
            @ApiParam( value = "the CV version number, e.g. 1.0" ) @PathVariable String version ) throws JsonProcessingException
    {
        log.debug( "REST: Getting {}, {}, {}", keyValue( CV_CODE, cvCode ), keyValue( LANGUAGE_ISO, languageIso ),
                keyValue( VERSION, version ) );
        VocabularyDTO vocab = VocabularyUtils.generateVocabularyByPath(vocabularyService.getPublishedCvPath( cvCode, version ));
        VersionDTO versionDTO = vocab.getVersions().stream().filter( v -> v.getLanguage().equals(languageIso)).findFirst().orElse(null);
        if ( versionDTO != null )
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.getSerializerProvider().setNullKeySerializer( new NullKeySerializer() );
            return ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON )
                    .body( mapper.writeValueAsString( versionDTO ) );
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET   : get the details SKOS-rdf of a CV in specific language and version
     *
     * @param cvCode      the cv short definition/notation
     * @param languageIso the Cv language (iso format)
     * @param version     the Cv version number
     * @return detail of CV in specific language and version
     */
    @CrossOrigin
    @GetMapping( "/vocabulary-details-skos/{cvCode}/{languageIso}/{version}" )
    @ApiOperation( value = "Get the CV details in SKOS-rdf format." )
    public ResponseEntity<Resource> getAllVocabulariesRaw(
        HttpServletRequest request,
            @ApiParam( value = "the CV short definition/notation, e.g. AnalysisUnit" ) @PathVariable String cvCode,
            @ApiParam( value = "the CV language in iso format, e.g. en" ) @PathVariable String languageIso,
            @ApiParam( value = "the CV version number, e.g. 1.0" ) @PathVariable String version ) throws IOException {
        log.debug( "REST: Getting {}, {}, {} in SKOS-rdf format", keyValue( CV_CODE, cvCode ),
                keyValue( LANGUAGE_ISO, languageIso ), keyValue( VERSION, version ) );
        File rdfFile = vocabularyService.generateVocabularyPublishFileDownload( cvCode, version, languageIso + "-" + version, ExportService.DownloadType.SKOS, request );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + rdfFile.getName() );
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("text/xml"))
            .body( new ByteArrayResource(Files.readAllBytes(rdfFile.toPath()) ));
    }

    private static class NullKeySerializer extends JsonSerializer<Object>
    {
        @Override
        public void serialize( Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused ) throws IOException
        {
            jsonGenerator.writeFieldName( "" );
        }
    }

}
