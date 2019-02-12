package eu.cessda.cvmanager.service.rest;
//
//import com.codahale.metrics.annotation.Timed;
//import eu.cessda.cvmanager.service.ResolverService;
//import eu.cessda.cvmanager.web.rest.errors.BadRequestAlertException;
//import eu.cessda.cvmanager.web.rest.util.HeaderUtil;
//import eu.cessda.cvmanager.web.rest.util.PaginationUtil;
//import eu.cessda.cvmanager.service.dto.ResolverDTO;
//import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Resolver.
 */
//@RestController
//@RequestMapping("/api")
public class ResolverResource {
//
//    private final Logger log = LoggerFactory.getLogger(ResolverResource.class);
//
//    private static final String ENTITY_NAME = "resolver";
//
//    private final ResolverService resolverService;
//
//    public ResolverResource(ResolverService resolverService) {
//        this.resolverService = resolverService;
//    }
//
//    /**
//     * POST  /resolvers : Create a new resolver.
//     *
//     * @param resolverDTO the resolverDTO to create
//     * @return the ResponseEntity with status 201 (Created) and with body the new resolverDTO, or with status 400 (Bad Request) if the resolver has already an ID
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PostMapping("/resolvers")
//    @Timed
//    public ResponseEntity<ResolverDTO> createResolver(@Valid @RequestBody ResolverDTO resolverDTO) throws URISyntaxException {
//        log.debug("REST request to save Resolver : {}", resolverDTO);
//        if (resolverDTO.getId() != null) {
//            throw new BadRequestAlertException("A new resolver cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        ResolverDTO result = resolverService.save(resolverDTO);
//        return ResponseEntity.created(new URI("/api/resolvers/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * PUT  /resolvers : Updates an existing resolver.
//     *
//     * @param resolverDTO the resolverDTO to update
//     * @return the ResponseEntity with status 200 (OK) and with body the updated resolverDTO,
//     * or with status 400 (Bad Request) if the resolverDTO is not valid,
//     * or with status 500 (Internal Server Error) if the resolverDTO couldn't be updated
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PutMapping("/resolvers")
//    @Timed
//    public ResponseEntity<ResolverDTO> updateResolver(@Valid @RequestBody ResolverDTO resolverDTO) throws URISyntaxException {
//        log.debug("REST request to update Resolver : {}", resolverDTO);
//        if (resolverDTO.getId() == null) {
//            return createResolver(resolverDTO);
//        }
//        ResolverDTO result = resolverService.save(resolverDTO);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, resolverDTO.getId().toString()))
//            .body(result);
//    }
//
//    /**
//     * GET  /resolvers : get all the resolvers.
//     *
//     * @param pageable the pagination information
//     * @return the ResponseEntity with status 200 (OK) and the list of resolvers in body
//     */
//    @GetMapping("/resolvers")
//    @Timed
//    public ResponseEntity<List<ResolverDTO>> getAllResolvers(Pageable pageable) {
//        log.debug("REST request to get a page of Resolvers");
//        Page<ResolverDTO> page = resolverService.findAll(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/resolvers");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }
//
//    /**
//     * GET  /resolvers/:id : get the "id" resolver.
//     *
//     * @param id the id of the resolverDTO to retrieve
//     * @return the ResponseEntity with status 200 (OK) and with body the resolverDTO, or with status 404 (Not Found)
//     */
//    @GetMapping("/resolvers/{id}")
//    @Timed
//    public ResponseEntity<ResolverDTO> getResolver(@PathVariable Long id) {
//        log.debug("REST request to get Resolver : {}", id);
//        ResolverDTO resolverDTO = resolverService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(resolverDTO));
//    }
//
//    /**
//     * DELETE  /resolvers/:id : delete the "id" resolver.
//     *
//     * @param id the id of the resolverDTO to delete
//     * @return the ResponseEntity with status 200 (OK)
//     */
//    @DeleteMapping("/resolvers/{id}")
//    @Timed
//    public ResponseEntity<Void> deleteResolver(@PathVariable Long id) {
//        log.debug("REST request to delete Resolver : {}", id);
//        resolverService.delete(id);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
//    }
}
