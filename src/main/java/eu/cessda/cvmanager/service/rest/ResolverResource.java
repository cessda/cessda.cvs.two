package eu.cessda.cvmanager.service.rest;
import java.net.URI;

//
//import eu.cessda.cvmanager.service.ResolverService;
//import eu.cessda.cvmanager.web.rest.errors.BadRequestAlertException;
//import eu.cessda.cvmanager.web.rest.util.HeaderUtil;
//import eu.cessda.cvmanager.web.rest.util.PaginationUtil;
//import eu.cessda.cvmanager.service.dto.ResolverDTO;
//import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.dto.ResolverDTO;


/**
 * REST controller for managing Resolver.
 */
@RestController
@RequestMapping("/v1")
public class ResolverResource {

    private final Logger log = LoggerFactory.getLogger(ResolverResource.class);

    private final ResolverService resolverService;
    private final ConfigurationService configurationService;

    public ResolverResource(ResolverService resolverService, ConfigurationService configurationService) {
        this.resolverService = resolverService;
        this.configurationService = configurationService;
    }

    /**
     * GET  /resolvers/:id : get the "id" resolver.
     *
     * @param id the id of the resolverDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the resolverDTO, or with status 404 (Not Found)
     */
    @GetMapping("/resolver/{resolverUri}")
    public ResponseEntity<String> findUrnResolver(@PathVariable String resolverUri) {
        log.debug("REST request to get Resolver by URI: {}", resolverUri);
        ResolverDTO resolverDTO = resolverService.findByResolverURI(resolverUri);
        if( resolverDTO != null ) {
        	HttpHeaders headers = new HttpHeaders();
        	headers.setLocation(URI.create( configurationService.getServerContextPath() + "/#!detail/" + resolverDTO.getResourceURL() ));
        	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "CV with URN: " + resolverUri + " is not found" );
    }


}
