/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.config.Constants;
import eu.cessda.cvs.config.audit.AuditEventPublisher;
import eu.cessda.cvs.domain.User;
import eu.cessda.cvs.repository.UserAgencyRepository;
import eu.cessda.cvs.repository.UserRepository;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.SecurityUtils;
import eu.cessda.cvs.service.MailService;
import eu.cessda.cvs.service.UserAgencyService;
import eu.cessda.cvs.service.UserService;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.dto.UserDTO;
import eu.cessda.cvs.web.rest.errors.BadRequestAlertException;
import eu.cessda.cvs.web.rest.errors.EmailAlreadyUsedException;
import eu.cessda.cvs.web.rest.errors.LoginAlreadyUsedException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    @Autowired
    private AuditEventPublisher auditPublisher;

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final UserAgencyRepository userAgencyRepository;

    private final UserAgencyService userAgencyService;

    private final MailService mailService;

    public UserResource(UserService userService, UserRepository userRepository, UserAgencyRepository userAgencyRepository, UserAgencyService userAgencyService, MailService mailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userAgencyRepository = userAgencyRepository;
        this.userAgencyService = userAgencyService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws MessagingException if the email cannot be sent.
     */
    @PostMapping("/users")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.ADMIN_CONTENT + "\")")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException, MessagingException
    {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);

            //notify the auditing mechanism
            String auditUserString = "";
            Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
            if (auditUser.isPresent()) {
                auditUserString = auditUser.get();
            }
            auditPublisher.publish(auditUserString, userDTO, null, "USER_CREATED");

            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert(applicationName,  "userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/users")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.ADMIN_CONTENT + "\")")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }

        if (userDTO.getUserAgencies() != null) {
            Set<UserAgencyDTO> newUserAgencies = new HashSet<>();
            Set<UserAgencyDTO> userAgencies = userDTO.getUserAgencies();
            Iterator<UserAgencyDTO> userAgenciesIterator = userAgencies.iterator();
            while (userAgenciesIterator.hasNext()) {
                UserAgencyDTO ua = userAgenciesIterator.next();
                if (ua.getId() == null) {
                    newUserAgencies.add(userAgencyService.save(ua));
                    userAgenciesIterator.remove();
                }
            }

            if( !newUserAgencies.isEmpty() )
                userDTO.getUserAgencies().addAll( newUserAgencies );
        }

        Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

        //notify the auditing mechanism
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, userDTO, null, "USER_UPDATED");

        return ResponseUtil.wrapOrNotFound(updatedUser,
            HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getLogin()));
    }

    /**
     * {@code GET /users} : get all users.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(Pageable pageable) {
        final Page<UserDTO> page = userService.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/users/authorities")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.ADMIN_CONTENT + "\")")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    /**
     * {@code GET /users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        User user = null;
        Optional<User> userOpt = userService.getUserWithAuthoritiesByLogin(login);
        if( userOpt.isPresent() ){
            user = userOpt.get();
            user.setUserAgencies(new HashSet<>(userAgencyRepository.findByUser(user.getId())));
        }
        return ResponseUtil.wrapOrNotFound(
            Optional.ofNullable(user).map(UserDTO::new));
    }

    /**
     * {@code DELETE /users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.ADMIN_CONTENT + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);

        //notify the auditing mechanism
        User userTemp = new User();
        Optional<User> user = userRepository.findOneByLogin(login.toLowerCase());
        if (user.isPresent()) {
            userTemp = user.get();
        }
        String auditUserString = "";
        Optional<String> auditUser = SecurityUtils.getCurrentUserLogin();
        if (auditUser.isPresent()) {
            auditUserString = auditUser.get();
        }
        auditPublisher.publish(auditUserString, null, userTemp, "USER_DELETED");
        
        userService.deleteUser(login);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName,  "userManagement.deleted", login)).build();
    }

    /**
     * {@code SEARCH /_search/users/:query} : search for the User corresponding to the query.
     *
     * @param query the query to search.
     * @return the result of the search.
     */
    @GetMapping("/_search/users/{query}")
    public List<User> search(@PathVariable String query) {
        return Collections.emptyList();
    }
}
