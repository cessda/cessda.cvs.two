/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.repository.*;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.TokenProvider;
import eu.cessda.cvs.utils.VersionNumber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class VocabularyMaintenanceResourceIT {

    private static final String SOURCE_LANGUAGE = "en";
    private static final ItemType ITEM_TYPE_SL = ItemType.SL;
    private static final String NOTATION = "AAAAAAAAAA";
    private static final VersionNumber INIT_VERSION_NUMBER_SL = VersionNumber.fromString("1.0");
    private static final String INIT_STATUS = "DRAFT";
    private static final String INIT_TITLE_EN = "AAAAAAAAAA";
    private static final String INIT_DEFINITION_EN = "AAAAAAAAAA";
    private static final String INIT_NOTES = "AAAAAAAAAA";

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private LicenceRepository licenceRepository;

    public static final String NAME = "AAAAAAAAAA";
    private Agency agency;

    private String jwt;

    private VocabularySnippet vocabularySnippet;

    private Licence license;

    @BeforeEach
    public void initTest() {
        if( license == null ) {
            license = new Licence()
                .name(NAME)
                .link(NAME)
                .logoLink(NAME)
                .abbr(NAME);
            license = licenceRepository.saveAndFlush(license);
        }
        if( agency == null ) {
            agency = new Agency()
                .name(NAME)
                .link(NAME)
                .description(NAME)
                .logopath(NAME)
                .license(NAME)
                .licenseId(license.getId())
                .uri("https://vocabularies-dev.cessda.eu/vocabulary/[VOCABULARY]?v=[VERSION]")
                .uriCode("https://vocabularies-dev.cessda.eu/vocabulary/[VOCABULARY]_[CODE]?v=[VERSION]")
                .canonicalUri(NAME);
            agency = agencyRepository.saveAndFlush(agency);
        }
        vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setLanguage(SOURCE_LANGUAGE);
        vocabularySnippet.setItemType(ITEM_TYPE_SL);
        vocabularySnippet.setNotation(NOTATION);
        vocabularySnippet.setVersionNumber(INIT_VERSION_NUMBER_SL);
        vocabularySnippet.setStatus( INIT_STATUS );
        vocabularySnippet.setTitle( INIT_TITLE_EN );
        vocabularySnippet.setDefinition( INIT_DEFINITION_EN );
        vocabularySnippet.setNotes( INIT_NOTES );
        vocabularySnippet.setAgencyId(agency.getId());

        // need to mock user due to authorization
        if( jwt == null ) {
            // Mock user login as admin
            Authority authority = new Authority();
            authority.setName(AuthoritiesConstants.ADMIN);
            authorityRepository.saveAndFlush(authority );

            User user = new User();
            user.setActivated(true);
            user.setLogin("user-test");
            user.setEmail("user-test@example.com");
            user.setPassword(passwordEncoder.encode("test"));

            Set<Authority> authorities = new HashSet<>();
            authorities.add(authority);
            user.setAuthorities(authorities);

            userRepository.saveAndFlush(user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getLogin(), "test");
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            jwt = tokenProvider.createToken(authentication, false);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @Test
    @Transactional
    void vocabularyMaintenanceTests() throws Exception {
        // publish CV
        vocabularySnippet.setActionType( ActionType.CREATE_CV );
        restMockMvc.perform(post("/api/editors/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
            .andExpect(status().isCreated());
        Vocabulary vocab = vocabularyRepository.findAll().stream().filter(v -> v.getNotation().equals( vocabularySnippet.getNotation() ))
            .findFirst().orElse(null);
        assertThat(vocab).isNotNull();
        Version version = vocab.getVersions().stream().filter( v -> v.getLanguage().equals( vocabularySnippet.getLanguage()))
            .findFirst().orElse(null);
        assertThat(version).isNotNull();
        // publish
        vocabularySnippet.setActionType( ActionType.FORWARD_CV_SL_STATUS_REVIEW );
        vocabularySnippet.setVocabularyId( version.getVocabulary().getId() );
        vocabularySnippet.setVersionId( version.getId());
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
            .andExpect(status().isOk());
        vocabularySnippet.setActionType( ActionType.FORWARD_CV_SL_STATUS_PUBLISH );
        vocabularySnippet.setLicenseId( license.getId() );
        restMockMvc.perform(put("/api/editors/vocabularies/forward-status")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularySnippet)))
            .andExpect(status().isOk());
        // recreate json
        restMockMvc.perform(post("/api/maintenance/publication/generate-json")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        // index agency
        restMockMvc.perform(post("/api/maintenance/index/agency")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        // index agency stat
        restMockMvc.perform(post("/api/maintenance/index/agency-stats")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        // index vocabulary publish
        restMockMvc.perform(post("/api/maintenance/index/vocabulary")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        // index vocabulary editor
        restMockMvc.perform(post("/api/maintenance/index/vocabulary/editor")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
