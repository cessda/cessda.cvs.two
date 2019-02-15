package eu.cessda.cvmanager.service.rest;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;


/**
 * REST controller for managing Resolver.
 */
@RestController
@RequestMapping("/urn")
public class ResolverUrnResource {

    private final Logger log = LoggerFactory.getLogger(ResolverUrnResource.class);

    private final ResolverService resolverService;
    private final ConfigurationService configurationService;

    public ResolverUrnResource(ResolverService resolverService, ConfigurationService configurationService) {
        this.resolverService = resolverService;
        this.configurationService = configurationService;
    }

    /**
     * GET  / : redirect to resourceURL by "resolverUri or URN" resolver.
     *
     * @param resolverUri the resolverUri or URN of the resolverDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and then redirect to resource URL, 
     * or with status 404 (Not Found)
     */
    @GetMapping("/{resolverUri}")
    public ResponseEntity<String> findUrnResolver(@PathVariable String resolverUri) {
        log.debug("REST request to get Resolver by URI: {}", resolverUri);
        ResolverDTO resolverDTO = resolverService.findByResolverURI(resolverUri);
        if( resolverDTO != null ) {
        	HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create( configurationService.getServerContextPath() + "/#!" + PublicationDetailsView.VIEW_NAME + "/" + resolverDTO.getResourceURL()));
			return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "CV with URN: " + resolverUri + " is not found" );
    }


}
