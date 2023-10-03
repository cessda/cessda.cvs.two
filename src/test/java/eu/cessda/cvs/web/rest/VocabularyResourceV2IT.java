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

import eu.cessda.cvs.CvsApp;
import eu.cessda.cvs.domain.*;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.AuthorityRepository;
import eu.cessda.cvs.repository.LicenceRepository;
import eu.cessda.cvs.repository.UserRepository;
import eu.cessda.cvs.security.ActionType;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.TokenProvider;
import eu.cessda.cvs.service.VocabularyService;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import org.junit.jupiter.api.AfterEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class VocabularyResourceV2IT {

    private Agency agency;

    private Licence license;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private LicenceRepository licenceRepository;

    @Autowired
    private MockMvc restMockMvc;

    private String jwt;

    private VocabularySnippet vocabularySnippetSl;

    private VocabularyDTO vocabularyDTO;

    @BeforeEach
    public void initTest() {
        if( agency == null ) {
            agency = EditorResourceIT.createAgencyEntity();
            agency = agencyRepository.saveAndFlush(agency);
        }
        // prepare licence
        if( license == null ) {
            license = new Licence()
                .name(EditorResourceIT.INIT_LICENSE)
                .link(EditorResourceIT.INIT_LINK)
                .logoLink(EditorResourceIT.INIT_LOGOPATH)
                .abbr(EditorResourceIT.INIT_NAME);
            license = licenceRepository.saveAndFlush(license);
        }

        // need to mock user due to authorization
        if( jwt == null ) {
            // Mock user login as admin
            Authority authority = new Authority();
            authority.setName(AuthoritiesConstants.ADMIN);
            authorityRepository.saveAndFlush(authority );

            Set<Authority> authorities = new HashSet<>();
            authorities.add(authority);

            User user = new User();
            user.setLogin("user-jwt-controller");
            user.setEmail("user-jwt-controller@example.com");
            user.setActivated(true);
            user.setPassword(passwordEncoder.encode("test"));
            user.setAuthorities(authorities);

            userRepository.saveAndFlush(user);

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getLogin(), "test");

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            jwt = tokenProvider.createToken(authentication, false);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        if( vocabularyDTO == null ) {
            vocabularySnippetSl = EditorResourceIT.createVocabularySnippetForSlEn();
            vocabularySnippetSl.setActionType( ActionType.CREATE_CV );
            vocabularySnippetSl.setAgencyId(agency.getId());
            vocabularyDTO = vocabularyService.saveVocabulary(vocabularySnippetSl);
            VersionDTO versionDTO = vocabularyDTO.getVersions().iterator().next();

            CodeSnippet codeSnippetForSl = EditorResourceIT.createCodeSnippet( EditorResourceIT.INIT_CODE_NOTATION,
                EditorResourceIT.INIT_CODE_TITLE, EditorResourceIT.INIT_CODE_DEF, EditorResourceIT.CODE_POSITION, null );
            codeSnippetForSl.setActionType( ActionType.CREATE_CODE );
            codeSnippetForSl.setVersionId( versionDTO.getId() );
            vocabularyService.saveCode( codeSnippetForSl );

            vocabularySnippetSl.setActionType( ActionType.FORWARD_CV_SL_STATUS_PUBLISH );
            vocabularySnippetSl.setVersionId( versionDTO.getId());
            vocabularySnippetSl.setLicenseId( license.getId() );
            vocabularySnippetSl.setVocabularyId( vocabularyDTO.getId() );

            // publish
            versionDTO = vocabularyService.forwardStatus(vocabularySnippetSl);
            vocabularyService.findOne(versionDTO.getVocabularyId()).ifPresent( dto -> this.vocabularyDTO = dto );
        }
    }

    @AfterEach
    void afterEach(){
        if ( vocabularyDTO != null )
            vocabularyService.delete(vocabularyDTO.getId());
    }

    @Test
    @Transactional
    void searchVocabulariesUiTest() throws Exception {
        restMockMvc.perform(get("/v2/search?q=" + EditorResourceIT.INIT_TITLE_EN)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.vocabularies.[0].notation").value(EditorResourceIT.INIT_TITLE_EN));
    }
    @Test
    @Transactional
    void searchVocabulariesTest() throws Exception {
        restMockMvc.perform(get("/v2/search/vocabularies?query=" + EditorResourceIT.INIT_TITLE_EN +
            "&agency=" + EditorResourceIT.INIT_AGENCY_NAME + "&lang=" + EditorResourceIT.SOURCE_LANGUAGE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.vocabularies.[0].notation").value(EditorResourceIT.INIT_TITLE_EN));

        restMockMvc.perform(get("/v2/search/vocabularies?query=" + EditorResourceIT.INIT_TITLE_EN +
            "&agency=" + EditorResourceIT.INIT_AGENCY_NAME + "&lang=" + EditorResourceIT.SOURCE_LANGUAGE)
            .accept(VocabularyResourceV2.JSONLD_TYPE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(VocabularyResourceV2.JSONLD_TYPE));
    }

    @Test
    @Transactional
    void searchCodesTest() throws Exception {
        restMockMvc.perform(get("/v2/search/codes?query=" + EditorResourceIT.INIT_CODE_NOTATION +
            "&agency=" + EditorResourceIT.INIT_AGENCY_NAME + "&lang=" + EditorResourceIT.SOURCE_LANGUAGE +
            "&vocab=" + EditorResourceIT.INIT_NAME)
            .accept(VocabularyResourceV2.JSONLD_TYPE))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void getVocabulariesTest() throws Exception {
        restMockMvc.perform(get("/v2/vocabularies/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "?languageVersion=" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(MediaType.TEXT_HTML_VALUE))
            .andExpect(status().isTemporaryRedirect());

        restMockMvc.perform(get("/v2/vocabularies/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "?languageVersion=" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(MediaType.APPLICATION_XHTML_XML_VALUE))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/v2/vocabularies/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "?languageVersion=" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        restMockMvc.perform(get("/v2/vocabularies/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "?languageVersion=" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(VocabularyResourceV2.JSONLD_TYPE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(VocabularyResourceV2.JSONLD_TYPE));

        restMockMvc.perform(get("/v2/vocabularies/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "?languageVersion=" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/v2/vocabularies/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "?languageVersion=" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(VocabularyResourceV2.DOCX_TYPE))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/v2/vocabularies/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "?languageVersion=" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(MediaType.APPLICATION_XML_VALUE))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void getCodesTest() throws Exception {
        restMockMvc.perform(get("/v2/codes/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "/" + EditorResourceIT.SOURCE_LANGUAGE + "-" +
            EditorResourceIT.INIT_VERSION_NUMBER_SL)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Transactional
    void getVocabularyCompareTest() throws Exception {
        // create new version
        VersionDTO newVersion = vocabularyService.createNewVersion(vocabularyDTO.getVersions().iterator().next().getId());

        vocabularySnippetSl.setActionType( ActionType.FORWARD_CV_SL_STATUS_PUBLISH );
        vocabularySnippetSl.setVersionId( newVersion.getId());
        vocabularySnippetSl.setLicenseId( license.getId() );
        vocabularySnippetSl.setVocabularyId( vocabularyDTO.getId() );

        // publish second version
        newVersion = vocabularyService.forwardStatus(vocabularySnippetSl);

        restMockMvc.perform(get("/v2/compare-vocabulary/" + EditorResourceIT.INIT_TITLE_EN +
            "/" + EditorResourceIT.SOURCE_LANGUAGE + "-" + EditorResourceIT.INIT_VERSION_NUMBER_SL + "/" +
            EditorResourceIT.SOURCE_LANGUAGE + "-" +newVersion.getNumber())
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Transactional
    void getVocabulariesPublishedTest() throws Exception {
        restMockMvc.perform(get("/v2/vocabularies-published")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }


}
