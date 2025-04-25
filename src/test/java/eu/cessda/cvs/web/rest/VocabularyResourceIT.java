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
import eu.cessda.cvs.domain.Agency;
import eu.cessda.cvs.domain.Authority;
import eu.cessda.cvs.domain.User;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.search.VocabularyEditor;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.AuthorityRepository;
import eu.cessda.cvs.repository.UserRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.repository.search.VocabularyEditorSearchRepository;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.TokenProvider;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.mapper.AgencyMapper;
import eu.cessda.cvs.service.mapper.VocabularyEditorMapper;
import eu.cessda.cvs.service.mapper.VocabularyMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.cessda.cvs.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link VocabularyResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class VocabularyResourceIT {

    private static final String DEFAULT_STATUS = "DRAFT";
    private static final String UPDATED_STATUS = "REVIEW";

    private static final String DEFAULT_URI = "AAAAAAAAAA";
    private static final String UPDATED_URI = "BBBBBBBBBB";

    private static final String DEFAULT_NOTATION = "AAAAAAAAAA";
    private static final String UPDATED_NOTATION = "BBBBBBBBBB";

    private static final VersionNumber DEFAULT_VERSION_NUMBER = VersionNumber.fromString("1.0.0");
    private static final VersionNumber UPDATED_VERSION_NUMBER = VersionNumber.fromString("2.0.0");

    private static final Long DEFAULT_INITIAL_PUBLICATION = 1L;
    private static final Long UPDATED_INITIAL_PUBLICATION = 2L;

    private static final Long DEFAULT_PREVIOUS_PUBLICATION = 1L;
    private static final Long UPDATED_PREVIOUS_PUBLICATION = 2L;

    private static final Boolean DEFAULT_ARCHIVED = false;
    private static final Boolean UPDATED_ARCHIVED = true;

    private static final Boolean DEFAULT_WITHDRAWN = false;
    private static final Boolean UPDATED_WITHDRAWN = true;

    private static final Boolean DEFAULT_DISCOVERABLE = false;
    private static final Boolean UPDATED_DISCOVERABLE = true;

    private static final String DEFAULT_SOURCE_LANGUAGE = "en";
    private static final String UPDATED_SOURCE_LANGUAGE = "de";

    private static final Long DEFAULT_AGENCY_ID = 1L;
    private static final Long UPDATED_AGENCY_ID = 2L;

    private static final String DEFAULT_AGENCY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_LOGO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PUBLICATION_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate UPDATED_PUBLICATION_DATE = LocalDate.now();

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED = ZonedDateTime.now().minusDays(1);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED = ZonedDateTime.now();

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_SQ = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_SQ = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_SQ = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_SQ = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_SQ = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_SQ = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_BS = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_BS = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_BS = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_BS = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_BS = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_BS = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_BG = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_BG = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_BG = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_BG = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_BG = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_BG = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_HR = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_HR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_HR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_HR = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_HR = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_HR = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_CS = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_CS = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_CS = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_CS = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_CS = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_CS = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_DA = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_DA = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_DA = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_DA = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_DA = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_DA = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_NL = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_NL = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_NL = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_NL = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_NL = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_NL = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_EN = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_EN = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_EN = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_EN = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_EN = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_ET = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_ET = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_ET = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_ET = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_ET = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_ET = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_FI = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_FI = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_FI = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_FI = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_FI = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_FI = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_FR = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_FR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_FR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_FR = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_FR = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_FR = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_DE = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_DE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_DE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_DE = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_DE = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_DE = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_EL = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_EL = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_EL = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_EL = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_EL = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_EL = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_HU = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_HU = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_HU = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_HU = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_HU = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_HU = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_IT = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_IT = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_IT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_IT = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_IT = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_IT = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_JA = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_JA = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_JA = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_JA = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_JA = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_JA = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_LT = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_LT = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LT = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_LT = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_LT = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_MK = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_MK = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_MK = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_MK = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_MK = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_MK = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_NO = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_NO = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_NO = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_NO = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_NO = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_NO = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_PL = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_PL = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_PL = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_PL = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_PL = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_PL = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_PT = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_PT = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_PT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_PT = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_PT = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_PT = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_RO = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_RO = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_RO = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_RO = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_RO = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_RO = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_RU = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_RU = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_RU = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_RU = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_RU = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_RU = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_SR = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_SR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_SR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_SR = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_SR = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_SR = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_SK = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_SK = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_SK = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_SK = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_SK = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_SK = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_SL = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_SL = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_SL = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_SL = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_SL = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_SL = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_ES = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_ES = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_ES = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_ES = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_ES = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_ES = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION_SV = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_SV = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_SV = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_SV = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION_SV = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION_SV = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOGOPATH = "AAAAAAAAAA";
    private static final String UPDATED_LOGOPATH = "BBBBBBBBBB";

    private static final String DEFAULT_LICENSE = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE = "BBBBBBBBBB";

    private static final Long DEFAULT_LICENSE_ID = 1L;
    private static final Long UPDATED_LICENSE_ID = 2L;

    private static final String DEFAULT_CANONICAL_URI = "AAAAAAAAAA";
    private static final String UPDATED_CANONICAL_URI = "BBBBBBBBBB";

    @Autowired
    private AgencyMapper agencyMapper;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private VocabularyEditorMapper vocabularyEditorMapper;

    @Autowired
    private VocabularyEditorSearchRepository mockVocabularyEditorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private MockMvc restVocabularyMockMvc;

    private Vocabulary vocabulary;

    private Agency agency;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vocabulary createEntity() {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.status(DEFAULT_STATUS);
        vocabulary.uri(DEFAULT_URI);
        vocabulary.notation(DEFAULT_NOTATION);
        vocabulary.versionNumber(DEFAULT_VERSION_NUMBER);
        vocabulary.initialPublication(DEFAULT_INITIAL_PUBLICATION);
        vocabulary.previousPublication(DEFAULT_PREVIOUS_PUBLICATION);
        vocabulary.archived(DEFAULT_ARCHIVED);
        vocabulary.withdrawn(DEFAULT_WITHDRAWN);
        vocabulary.discoverable(DEFAULT_DISCOVERABLE);
        vocabulary.sourceLanguage(DEFAULT_SOURCE_LANGUAGE);
        vocabulary.agencyId(DEFAULT_AGENCY_ID);
        vocabulary.agencyName(DEFAULT_AGENCY_NAME);
        vocabulary.agencyLogo(DEFAULT_AGENCY_LOGO );
        vocabulary.publicationDate(DEFAULT_PUBLICATION_DATE);
        vocabulary.lastModified(DEFAULT_LAST_MODIFIED);
        vocabulary.notes(DEFAULT_NOTES);
        vocabulary.versionSq(DEFAULT_VERSION_SQ);
        vocabulary.titleSq(DEFAULT_TITLE_SQ);
        vocabulary.definitionSq(DEFAULT_DEFINITION_SQ);
        vocabulary.versionBs(DEFAULT_VERSION_BS);
        vocabulary.titleBs(DEFAULT_TITLE_BS);
        vocabulary.definitionBs(DEFAULT_DEFINITION_BS);
        vocabulary.versionBg(DEFAULT_VERSION_BG);
        vocabulary.titleBg(DEFAULT_TITLE_BG);
        vocabulary.definitionBg(DEFAULT_DEFINITION_BG);
        vocabulary.versionHr(DEFAULT_VERSION_HR);
        vocabulary.titleHr(DEFAULT_TITLE_HR);
        vocabulary.definitionHr(DEFAULT_DEFINITION_HR);
        vocabulary.versionCs(DEFAULT_VERSION_CS);
        vocabulary.titleCs(DEFAULT_TITLE_CS);
        vocabulary.definitionCs(DEFAULT_DEFINITION_CS);
        vocabulary.versionDa(DEFAULT_VERSION_DA);
        vocabulary.titleDa(DEFAULT_TITLE_DA);
        vocabulary.definitionDa(DEFAULT_DEFINITION_DA);
        vocabulary.versionNl(DEFAULT_VERSION_NL);
        vocabulary.titleNl(DEFAULT_TITLE_NL);
        vocabulary.definitionNl(DEFAULT_DEFINITION_NL);
        vocabulary.versionEn(DEFAULT_VERSION_EN);
        vocabulary.titleEn(DEFAULT_TITLE_EN);
        vocabulary.definitionEn(DEFAULT_DEFINITION_EN);
        vocabulary.versionEt(DEFAULT_VERSION_ET);
        vocabulary.titleEt(DEFAULT_TITLE_ET);
        vocabulary.definitionEt(DEFAULT_DEFINITION_ET);
        vocabulary.versionFi(DEFAULT_VERSION_FI);
        vocabulary.titleFi(DEFAULT_TITLE_FI);
        vocabulary.definitionFi(DEFAULT_DEFINITION_FI);
        vocabulary.versionFr(DEFAULT_VERSION_FR);
        vocabulary.titleFr(DEFAULT_TITLE_FR);
        vocabulary.definitionFr(DEFAULT_DEFINITION_FR);
        vocabulary.versionDe(DEFAULT_VERSION_DE);
        vocabulary.titleDe(DEFAULT_TITLE_DE);
        vocabulary.definitionDe(DEFAULT_DEFINITION_DE);
        vocabulary.versionEl(DEFAULT_VERSION_EL);
        vocabulary.titleEl(DEFAULT_TITLE_EL);
        vocabulary.definitionEl(DEFAULT_DEFINITION_EL);
        vocabulary.versionHu(DEFAULT_VERSION_HU);
        vocabulary.titleHu(DEFAULT_TITLE_HU);
        vocabulary.definitionHu(DEFAULT_DEFINITION_HU);
        vocabulary.versionIt(DEFAULT_VERSION_IT);
        vocabulary.titleIt(DEFAULT_TITLE_IT);
        vocabulary.definitionIt(DEFAULT_DEFINITION_IT);
        vocabulary.versionJa(DEFAULT_VERSION_JA);
        vocabulary.titleJa(DEFAULT_TITLE_JA);
        vocabulary.definitionJa(DEFAULT_DEFINITION_JA);
        vocabulary.versionLt(DEFAULT_VERSION_LT);
        vocabulary.titleLt(DEFAULT_TITLE_LT);
        vocabulary.definitionLt(DEFAULT_DEFINITION_LT);
        vocabulary.versionMk(DEFAULT_VERSION_MK);
        vocabulary.titleMk(DEFAULT_TITLE_MK);
        vocabulary.definitionMk(DEFAULT_DEFINITION_MK);
        vocabulary.versionNo(DEFAULT_VERSION_NO);
        vocabulary.titleNo(DEFAULT_TITLE_NO);
        vocabulary.definitionNo(DEFAULT_DEFINITION_NO);
        vocabulary.versionPl(DEFAULT_VERSION_PL);
        vocabulary.titlePl(DEFAULT_TITLE_PL);
        vocabulary.definitionPl(DEFAULT_DEFINITION_PL);
        vocabulary.versionPt(DEFAULT_VERSION_PT);
        vocabulary.titlePt(DEFAULT_TITLE_PT);
        vocabulary.definitionPt(DEFAULT_DEFINITION_PT);
        vocabulary.versionRo(DEFAULT_VERSION_RO);
        vocabulary.titleRo(DEFAULT_TITLE_RO);
        vocabulary.definitionRo(DEFAULT_DEFINITION_RO);
        vocabulary.versionRu(DEFAULT_VERSION_RU);
        vocabulary.titleRu(DEFAULT_TITLE_RU);
        vocabulary.definitionRu(DEFAULT_DEFINITION_RU);
        vocabulary.versionSr(DEFAULT_VERSION_SR);
        vocabulary.titleSr(DEFAULT_TITLE_SR);
        vocabulary.definitionSr(DEFAULT_DEFINITION_SR);
        vocabulary.versionSk(DEFAULT_VERSION_SK);
        vocabulary.titleSk(DEFAULT_TITLE_SK);
        vocabulary.definitionSk(DEFAULT_DEFINITION_SK);
        vocabulary.versionSl(DEFAULT_VERSION_SL);
        vocabulary.titleSl(DEFAULT_TITLE_SL);
        vocabulary.definitionSl(DEFAULT_DEFINITION_SL);
        vocabulary.versionEs(DEFAULT_VERSION_ES);
        vocabulary.titleEs(DEFAULT_TITLE_ES);
        vocabulary.definitionEs(DEFAULT_DEFINITION_ES);
        vocabulary.versionSv(DEFAULT_VERSION_SV);
        vocabulary.titleSv(DEFAULT_TITLE_SV);
        vocabulary.definitionSv(DEFAULT_DEFINITION_SV);
        return vocabulary;
    }
    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vocabulary createUpdatedEntity() {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.status(UPDATED_STATUS);
        vocabulary.uri(UPDATED_URI);
        vocabulary.notation(UPDATED_NOTATION);
        vocabulary.versionNumber(UPDATED_VERSION_NUMBER);
        vocabulary.initialPublication(UPDATED_INITIAL_PUBLICATION);
        vocabulary.previousPublication(UPDATED_PREVIOUS_PUBLICATION);
        vocabulary.archived(UPDATED_ARCHIVED);
        vocabulary.withdrawn(UPDATED_WITHDRAWN);
        vocabulary.discoverable(UPDATED_DISCOVERABLE);
        vocabulary.sourceLanguage(UPDATED_SOURCE_LANGUAGE);
        vocabulary.agencyId(UPDATED_AGENCY_ID);
        vocabulary.agencyName(UPDATED_AGENCY_NAME);
        vocabulary.agencyLogo(UPDATED_AGENCY_LOGO);
        vocabulary.publicationDate(UPDATED_PUBLICATION_DATE);
        vocabulary.lastModified(UPDATED_LAST_MODIFIED);
        vocabulary.notes(UPDATED_NOTES);
        vocabulary.versionSq(UPDATED_VERSION_SQ);
        vocabulary.titleSq(UPDATED_TITLE_SQ);
        vocabulary.definitionSq(UPDATED_DEFINITION_SQ);
        vocabulary.versionBs(UPDATED_VERSION_BS);
        vocabulary.titleBs(UPDATED_TITLE_BS);
        vocabulary.definitionBs(UPDATED_DEFINITION_BS);
        vocabulary.versionBg(UPDATED_VERSION_BG);
        vocabulary.titleBg(UPDATED_TITLE_BG);
        vocabulary.definitionBg(UPDATED_DEFINITION_BG);
        vocabulary.versionHr(UPDATED_VERSION_HR);
        vocabulary.titleHr(UPDATED_TITLE_HR);
        vocabulary.definitionHr(UPDATED_DEFINITION_HR);
        vocabulary.versionCs(UPDATED_VERSION_CS);
        vocabulary.titleCs(UPDATED_TITLE_CS);
        vocabulary.definitionCs(UPDATED_DEFINITION_CS);
        vocabulary.versionDa(UPDATED_VERSION_DA);
        vocabulary.titleDa(UPDATED_TITLE_DA);
        vocabulary.definitionDa(UPDATED_DEFINITION_DA);
        vocabulary.versionNl(UPDATED_VERSION_NL);
        vocabulary.titleNl(UPDATED_TITLE_NL);
        vocabulary.definitionNl(UPDATED_DEFINITION_NL);
        vocabulary.versionEn(UPDATED_VERSION_EN);
        vocabulary.titleEn(UPDATED_TITLE_EN);
        vocabulary.definitionEn(UPDATED_DEFINITION_EN);
        vocabulary.versionEt(UPDATED_VERSION_ET);
        vocabulary.titleEt(UPDATED_TITLE_ET);
        vocabulary.definitionEt(UPDATED_DEFINITION_ET);
        vocabulary.versionFi(UPDATED_VERSION_FI);
        vocabulary.titleFi(UPDATED_TITLE_FI);
        vocabulary.definitionFi(UPDATED_DEFINITION_FI);
        vocabulary.versionFr(UPDATED_VERSION_FR);
        vocabulary.titleFr(UPDATED_TITLE_FR);
        vocabulary.definitionFr(UPDATED_DEFINITION_FR);
        vocabulary.versionDe(UPDATED_VERSION_DE);
        vocabulary.titleDe(UPDATED_TITLE_DE);
        vocabulary.definitionDe(UPDATED_DEFINITION_DE);
        vocabulary.versionEl(UPDATED_VERSION_EL);
        vocabulary.titleEl(UPDATED_TITLE_EL);
        vocabulary.definitionEl(UPDATED_DEFINITION_EL);
        vocabulary.versionHu(UPDATED_VERSION_HU);
        vocabulary.titleHu(UPDATED_TITLE_HU);
        vocabulary.definitionHu(UPDATED_DEFINITION_HU);
        vocabulary.versionIt(UPDATED_VERSION_IT);
        vocabulary.titleIt(UPDATED_TITLE_IT);
        vocabulary.definitionIt(UPDATED_DEFINITION_IT);
        vocabulary.versionJa(UPDATED_VERSION_JA);
        vocabulary.titleJa(UPDATED_TITLE_JA);
        vocabulary.definitionJa(UPDATED_DEFINITION_JA);
        vocabulary.versionLt(UPDATED_VERSION_LT);
        vocabulary.titleLt(UPDATED_TITLE_LT);
        vocabulary.definitionLt(UPDATED_DEFINITION_LT);
        vocabulary.versionMk(UPDATED_VERSION_MK);
        vocabulary.titleMk(UPDATED_TITLE_MK);
        vocabulary.definitionMk(UPDATED_DEFINITION_MK);
        vocabulary.versionNo(UPDATED_VERSION_NO);
        vocabulary.titleNo(UPDATED_TITLE_NO);
        vocabulary.definitionNo(UPDATED_DEFINITION_NO);
        vocabulary.versionPl(UPDATED_VERSION_PL);
        vocabulary.titlePl(UPDATED_TITLE_PL);
        vocabulary.definitionPl(UPDATED_DEFINITION_PL);
        vocabulary.versionPt(UPDATED_VERSION_PT);
        vocabulary.titlePt(UPDATED_TITLE_PT);
        vocabulary.definitionPt(UPDATED_DEFINITION_PT);
        vocabulary.versionRo(UPDATED_VERSION_RO);
        vocabulary.titleRo(UPDATED_TITLE_RO);
        vocabulary.definitionRo(UPDATED_DEFINITION_RO);
        vocabulary.versionRu(UPDATED_VERSION_RU);
        vocabulary.titleRu(UPDATED_TITLE_RU);
        vocabulary.definitionRu(UPDATED_DEFINITION_RU);
        vocabulary.versionSr(UPDATED_VERSION_SR);
        vocabulary.titleSr(UPDATED_TITLE_SR);
        vocabulary.definitionSr(UPDATED_DEFINITION_SR);
        vocabulary.versionSk(UPDATED_VERSION_SK);
        vocabulary.titleSk(UPDATED_TITLE_SK);
        vocabulary.definitionSk(UPDATED_DEFINITION_SK);
        vocabulary.versionSl(UPDATED_VERSION_SL);
        vocabulary.titleSl(UPDATED_TITLE_SL);
        vocabulary.definitionSl(UPDATED_DEFINITION_SL);
        vocabulary.versionEs(UPDATED_VERSION_ES);
        vocabulary.titleEs(UPDATED_TITLE_ES);
        vocabulary.definitionEs(UPDATED_DEFINITION_ES);
        vocabulary.versionSv(UPDATED_VERSION_SV);
        vocabulary.titleSv(UPDATED_TITLE_SV);
        vocabulary.definitionSv(UPDATED_DEFINITION_SV);
        return vocabulary;
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agency createAgencyEntity() {
        return new Agency()
            .name(DEFAULT_NAME)
            .link(DEFAULT_LINK)
            .description(DEFAULT_DESCRIPTION)
            .logopath(DEFAULT_LOGOPATH)
            .license(DEFAULT_LICENSE)
            .licenseId(DEFAULT_LICENSE_ID)
            .uri(DEFAULT_URI)
            .canonicalUri(DEFAULT_CANONICAL_URI);
    }

    @BeforeEach
    public void initTest() {
        if( agency == null ) {
            agency = createAgencyEntity();
            agency = agencyRepository.saveAndFlush(agency);
        }
        vocabulary = createEntity();
        vocabulary.setAgencyId(agency.getId());
    }

    @Test
    @Transactional
    void createVocabulary() throws Exception {
        int databaseSizeBeforeCreate = vocabularyRepository.findAll().size();

        // Mock user login
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
        String jwt = tokenProvider.createToken(authentication, false);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create the Vocabulary
        VocabularyDTO vocabularyDTO = vocabularyMapper.toDto(vocabulary);
        vocabularyDTO.setLastModified(null); // prevent mapper error in elasticsearch
        restVocabularyMockMvc.perform(post("/api/vocabularies")
            .header("Authorization", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyDTO)))
            .andExpect(status().isCreated());

        // Validate the Vocabulary in the database
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeCreate + 1);
        Vocabulary testVocabulary = vocabularyList.get(vocabularyList.size() - 1);
        assertThat(testVocabulary.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testVocabulary.getUri()).isEqualTo(DEFAULT_URI);
        assertThat(testVocabulary.getNotation()).isEqualTo(DEFAULT_NOTATION);
        assertThat(testVocabulary.getVersionNumber()).isEqualTo(DEFAULT_VERSION_NUMBER);
        assertThat(testVocabulary.getInitialPublication()).isEqualTo(DEFAULT_INITIAL_PUBLICATION);
        assertThat(testVocabulary.getPreviousPublication()).isEqualTo(DEFAULT_PREVIOUS_PUBLICATION);
        assertThat(testVocabulary.isArchived()).isEqualTo(DEFAULT_ARCHIVED);
        assertThat(testVocabulary.isWithdrawn()).isEqualTo(DEFAULT_WITHDRAWN);
        assertThat(testVocabulary.isDiscoverable()).isEqualTo(DEFAULT_DISCOVERABLE);
        assertThat(testVocabulary.getSourceLanguage()).isEqualTo(DEFAULT_SOURCE_LANGUAGE);
//        assertThat(testVocabulary.getAgencyId()).isEqualTo(DEFAULT_AGENCY_ID);
        assertThat(testVocabulary.getAgencyName()).isEqualTo(DEFAULT_AGENCY_NAME);
        assertThat(testVocabulary.getAgencyLogo()).isEqualTo(DEFAULT_AGENCY_LOGO);
        assertThat(testVocabulary.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
        assertThat(testVocabulary.getLastModified()).isNull();
        assertThat(testVocabulary.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testVocabulary.getVersionSq()).isEqualTo(DEFAULT_VERSION_SQ);
        assertThat(testVocabulary.getTitleSq()).isEqualTo(DEFAULT_TITLE_SQ);
        assertThat(testVocabulary.getDefinitionSq()).isEqualTo(DEFAULT_DEFINITION_SQ);
        assertThat(testVocabulary.getVersionBs()).isEqualTo(DEFAULT_VERSION_BS);
        assertThat(testVocabulary.getTitleBs()).isEqualTo(DEFAULT_TITLE_BS);
        assertThat(testVocabulary.getDefinitionBs()).isEqualTo(DEFAULT_DEFINITION_BS);
        assertThat(testVocabulary.getVersionBg()).isEqualTo(DEFAULT_VERSION_BG);
        assertThat(testVocabulary.getTitleBg()).isEqualTo(DEFAULT_TITLE_BG);
        assertThat(testVocabulary.getDefinitionBg()).isEqualTo(DEFAULT_DEFINITION_BG);
        assertThat(testVocabulary.getVersionHr()).isEqualTo(DEFAULT_VERSION_HR);
        assertThat(testVocabulary.getTitleHr()).isEqualTo(DEFAULT_TITLE_HR);
        assertThat(testVocabulary.getDefinitionHr()).isEqualTo(DEFAULT_DEFINITION_HR);
        assertThat(testVocabulary.getVersionCs()).isEqualTo(DEFAULT_VERSION_CS);
        assertThat(testVocabulary.getTitleCs()).isEqualTo(DEFAULT_TITLE_CS);
        assertThat(testVocabulary.getDefinitionCs()).isEqualTo(DEFAULT_DEFINITION_CS);
        assertThat(testVocabulary.getVersionDa()).isEqualTo(DEFAULT_VERSION_DA);
        assertThat(testVocabulary.getTitleDa()).isEqualTo(DEFAULT_TITLE_DA);
        assertThat(testVocabulary.getDefinitionDa()).isEqualTo(DEFAULT_DEFINITION_DA);
        assertThat(testVocabulary.getVersionNl()).isEqualTo(DEFAULT_VERSION_NL);
        assertThat(testVocabulary.getTitleNl()).isEqualTo(DEFAULT_TITLE_NL);
        assertThat(testVocabulary.getDefinitionNl()).isEqualTo(DEFAULT_DEFINITION_NL);
        assertThat(testVocabulary.getVersionEn()).isEqualTo(DEFAULT_VERSION_EN);
        assertThat(testVocabulary.getTitleEn()).isEqualTo(DEFAULT_TITLE_EN);
        assertThat(testVocabulary.getDefinitionEn()).isEqualTo(DEFAULT_DEFINITION_EN);
        assertThat(testVocabulary.getVersionEt()).isEqualTo(DEFAULT_VERSION_ET);
        assertThat(testVocabulary.getTitleEt()).isEqualTo(DEFAULT_TITLE_ET);
        assertThat(testVocabulary.getDefinitionEt()).isEqualTo(DEFAULT_DEFINITION_ET);
        assertThat(testVocabulary.getVersionFi()).isEqualTo(DEFAULT_VERSION_FI);
        assertThat(testVocabulary.getTitleFi()).isEqualTo(DEFAULT_TITLE_FI);
        assertThat(testVocabulary.getDefinitionFi()).isEqualTo(DEFAULT_DEFINITION_FI);
        assertThat(testVocabulary.getVersionFr()).isEqualTo(DEFAULT_VERSION_FR);
        assertThat(testVocabulary.getTitleFr()).isEqualTo(DEFAULT_TITLE_FR);
        assertThat(testVocabulary.getDefinitionFr()).isEqualTo(DEFAULT_DEFINITION_FR);
        assertThat(testVocabulary.getVersionDe()).isEqualTo(DEFAULT_VERSION_DE);
        assertThat(testVocabulary.getTitleDe()).isEqualTo(DEFAULT_TITLE_DE);
        assertThat(testVocabulary.getDefinitionDe()).isEqualTo(DEFAULT_DEFINITION_DE);
        assertThat(testVocabulary.getVersionEl()).isEqualTo(DEFAULT_VERSION_EL);
        assertThat(testVocabulary.getTitleEl()).isEqualTo(DEFAULT_TITLE_EL);
        assertThat(testVocabulary.getDefinitionEl()).isEqualTo(DEFAULT_DEFINITION_EL);
        assertThat(testVocabulary.getVersionHu()).isEqualTo(DEFAULT_VERSION_HU);
        assertThat(testVocabulary.getTitleHu()).isEqualTo(DEFAULT_TITLE_HU);
        assertThat(testVocabulary.getDefinitionHu()).isEqualTo(DEFAULT_DEFINITION_HU);
        assertThat(testVocabulary.getVersionIt()).isEqualTo(DEFAULT_VERSION_IT);
        assertThat(testVocabulary.getTitleIt()).isEqualTo(DEFAULT_TITLE_IT);
        assertThat(testVocabulary.getDefinitionIt()).isEqualTo(DEFAULT_DEFINITION_IT);
        assertThat(testVocabulary.getVersionJa()).isEqualTo(DEFAULT_VERSION_JA);
        assertThat(testVocabulary.getTitleJa()).isEqualTo(DEFAULT_TITLE_JA);
        assertThat(testVocabulary.getDefinitionJa()).isEqualTo(DEFAULT_DEFINITION_JA);
        assertThat(testVocabulary.getVersionLt()).isEqualTo(DEFAULT_VERSION_LT);
        assertThat(testVocabulary.getTitleLt()).isEqualTo(DEFAULT_TITLE_LT);
        assertThat(testVocabulary.getDefinitionLt()).isEqualTo(DEFAULT_DEFINITION_LT);
        assertThat(testVocabulary.getVersionMk()).isEqualTo(DEFAULT_VERSION_MK);
        assertThat(testVocabulary.getTitleMk()).isEqualTo(DEFAULT_TITLE_MK);
        assertThat(testVocabulary.getDefinitionMk()).isEqualTo(DEFAULT_DEFINITION_MK);
        assertThat(testVocabulary.getVersionNo()).isEqualTo(DEFAULT_VERSION_NO);
        assertThat(testVocabulary.getTitleNo()).isEqualTo(DEFAULT_TITLE_NO);
        assertThat(testVocabulary.getDefinitionNo()).isEqualTo(DEFAULT_DEFINITION_NO);
        assertThat(testVocabulary.getVersionPl()).isEqualTo(DEFAULT_VERSION_PL);
        assertThat(testVocabulary.getTitlePl()).isEqualTo(DEFAULT_TITLE_PL);
        assertThat(testVocabulary.getDefinitionPl()).isEqualTo(DEFAULT_DEFINITION_PL);
        assertThat(testVocabulary.getVersionPt()).isEqualTo(DEFAULT_VERSION_PT);
        assertThat(testVocabulary.getTitlePt()).isEqualTo(DEFAULT_TITLE_PT);
        assertThat(testVocabulary.getDefinitionPt()).isEqualTo(DEFAULT_DEFINITION_PT);
        assertThat(testVocabulary.getVersionRo()).isEqualTo(DEFAULT_VERSION_RO);
        assertThat(testVocabulary.getTitleRo()).isEqualTo(DEFAULT_TITLE_RO);
        assertThat(testVocabulary.getDefinitionRo()).isEqualTo(DEFAULT_DEFINITION_RO);
        assertThat(testVocabulary.getVersionRu()).isEqualTo(DEFAULT_VERSION_RU);
        assertThat(testVocabulary.getTitleRu()).isEqualTo(DEFAULT_TITLE_RU);
        assertThat(testVocabulary.getDefinitionRu()).isEqualTo(DEFAULT_DEFINITION_RU);
        assertThat(testVocabulary.getVersionSr()).isEqualTo(DEFAULT_VERSION_SR);
        assertThat(testVocabulary.getTitleSr()).isEqualTo(DEFAULT_TITLE_SR);
        assertThat(testVocabulary.getDefinitionSr()).isEqualTo(DEFAULT_DEFINITION_SR);
        assertThat(testVocabulary.getVersionSk()).isEqualTo(DEFAULT_VERSION_SK);
        assertThat(testVocabulary.getTitleSk()).isEqualTo(DEFAULT_TITLE_SK);
        assertThat(testVocabulary.getDefinitionSk()).isEqualTo(DEFAULT_DEFINITION_SK);
        assertThat(testVocabulary.getVersionSl()).isEqualTo(DEFAULT_VERSION_SL);
        assertThat(testVocabulary.getTitleSl()).isEqualTo(DEFAULT_TITLE_SL);
        assertThat(testVocabulary.getDefinitionSl()).isEqualTo(DEFAULT_DEFINITION_SL);
        assertThat(testVocabulary.getVersionEs()).isEqualTo(DEFAULT_VERSION_ES);
        assertThat(testVocabulary.getTitleEs()).isEqualTo(DEFAULT_TITLE_ES);
        assertThat(testVocabulary.getDefinitionEs()).isEqualTo(DEFAULT_DEFINITION_ES);
        assertThat(testVocabulary.getVersionSv()).isEqualTo(DEFAULT_VERSION_SV);
        assertThat(testVocabulary.getTitleSv()).isEqualTo(DEFAULT_TITLE_SV);
        assertThat(testVocabulary.getDefinitionSv()).isEqualTo(DEFAULT_DEFINITION_SV);

        // Validate the VocabularyEditor in Elasticsearch
        // Mocking ElasticRepository is still not successful, thus need real running ES to test ES
        VocabularyEditor vocabularyEditor = vocabularyEditorMapper.toEntity(vocabularyMapper.toDto(testVocabulary));
        assertThat( mockVocabularyEditorSearchRepository.findById(vocabularyEditor.getId())).isNotEmpty();

        // remove instance, since
        mockVocabularyEditorSearchRepository.delete( vocabularyEditor );
    }

    @Test
    @Transactional
    void createVocabularyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vocabularyRepository.findAll().size();

        // Create the Vocabulary with an existing ID
        vocabulary.setId(1L);
        VocabularyDTO vocabularyDTO = vocabularyMapper.toDto(vocabulary);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVocabularyMockMvc.perform(post("/api/vocabularies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Vocabulary in the database
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVocabularies() throws Exception {
        // Initialize the database
        vocabularyRepository.saveAndFlush(vocabulary);

        // Get all the vocabularyList
        restVocabularyMockMvc.perform(get("/api/vocabularies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vocabulary.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].uri").value(hasItem(DEFAULT_URI)))
            .andExpect(jsonPath("$.[*].notation").value(hasItem(DEFAULT_NOTATION)))
            .andExpect(jsonPath("$.[*].versionNumber").value(hasItem(DEFAULT_VERSION_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].initialPublication").value(hasItem(DEFAULT_INITIAL_PUBLICATION.intValue())))
            .andExpect(jsonPath("$.[*].previousPublication").value(hasItem(DEFAULT_PREVIOUS_PUBLICATION.intValue())))
            .andExpect(jsonPath("$.[*].archived").value(hasItem( DEFAULT_ARCHIVED )))
            .andExpect(jsonPath("$.[*].withdrawn").value(hasItem( DEFAULT_WITHDRAWN )))
            .andExpect(jsonPath("$.[*].discoverable").value(hasItem( DEFAULT_DISCOVERABLE )))
            .andExpect(jsonPath("$.[*].sourceLanguage").value(hasItem(DEFAULT_SOURCE_LANGUAGE)))
//            .andExpect(jsonPath("$.[*].agencyId").value(hasItem(DEFAULT_AGENCY_ID.intValue())))
            .andExpect(jsonPath("$.[*].agencyName").value(hasItem(DEFAULT_AGENCY_NAME)))
            .andExpect(jsonPath("$.[*].agencyLogo").value(hasItem(DEFAULT_AGENCY_LOGO)))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(DEFAULT_PUBLICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem( DEFAULT_NOTES )))
            .andExpect(jsonPath("$.[*].versionSq").value(hasItem(DEFAULT_VERSION_SQ)))
            .andExpect(jsonPath("$.[*].titleSq").value(hasItem( DEFAULT_TITLE_SQ )))
            .andExpect(jsonPath("$.[*].definitionSq").value(hasItem( DEFAULT_DEFINITION_SQ )))
            .andExpect(jsonPath("$.[*].versionBs").value(hasItem(DEFAULT_VERSION_BS)))
            .andExpect(jsonPath("$.[*].titleBs").value(hasItem( DEFAULT_TITLE_BS )))
            .andExpect(jsonPath("$.[*].definitionBs").value(hasItem( DEFAULT_DEFINITION_BS )))
            .andExpect(jsonPath("$.[*].versionBg").value(hasItem(DEFAULT_VERSION_BG)))
            .andExpect(jsonPath("$.[*].titleBg").value(hasItem( DEFAULT_TITLE_BG )))
            .andExpect(jsonPath("$.[*].definitionBg").value(hasItem( DEFAULT_DEFINITION_BG )))
            .andExpect(jsonPath("$.[*].versionHr").value(hasItem(DEFAULT_VERSION_HR)))
            .andExpect(jsonPath("$.[*].titleHr").value(hasItem( DEFAULT_TITLE_HR )))
            .andExpect(jsonPath("$.[*].definitionHr").value(hasItem( DEFAULT_DEFINITION_HR )))
            .andExpect(jsonPath("$.[*].versionCs").value(hasItem(DEFAULT_VERSION_CS)))
            .andExpect(jsonPath("$.[*].titleCs").value(hasItem( DEFAULT_TITLE_CS )))
            .andExpect(jsonPath("$.[*].definitionCs").value(hasItem( DEFAULT_DEFINITION_CS )))
            .andExpect(jsonPath("$.[*].versionDa").value(hasItem(DEFAULT_VERSION_DA)))
            .andExpect(jsonPath("$.[*].titleDa").value(hasItem( DEFAULT_TITLE_DA )))
            .andExpect(jsonPath("$.[*].definitionDa").value(hasItem( DEFAULT_DEFINITION_DA )))
            .andExpect(jsonPath("$.[*].versionNl").value(hasItem(DEFAULT_VERSION_NL)))
            .andExpect(jsonPath("$.[*].titleNl").value(hasItem( DEFAULT_TITLE_NL )))
            .andExpect(jsonPath("$.[*].definitionNl").value(hasItem( DEFAULT_DEFINITION_NL )))
            .andExpect(jsonPath("$.[*].versionEn").value(hasItem(DEFAULT_VERSION_EN)))
            .andExpect(jsonPath("$.[*].titleEn").value(hasItem( DEFAULT_TITLE_EN )))
            .andExpect(jsonPath("$.[*].definitionEn").value(hasItem( DEFAULT_DEFINITION_EN )))
            .andExpect(jsonPath("$.[*].versionEt").value(hasItem(DEFAULT_VERSION_ET)))
            .andExpect(jsonPath("$.[*].titleEt").value(hasItem( DEFAULT_TITLE_ET )))
            .andExpect(jsonPath("$.[*].definitionEt").value(hasItem( DEFAULT_DEFINITION_ET )))
            .andExpect(jsonPath("$.[*].versionFi").value(hasItem(DEFAULT_VERSION_FI)))
            .andExpect(jsonPath("$.[*].titleFi").value(hasItem( DEFAULT_TITLE_FI )))
            .andExpect(jsonPath("$.[*].definitionFi").value(hasItem( DEFAULT_DEFINITION_FI )))
            .andExpect(jsonPath("$.[*].versionFr").value(hasItem(DEFAULT_VERSION_FR)))
            .andExpect(jsonPath("$.[*].titleFr").value(hasItem( DEFAULT_TITLE_FR )))
            .andExpect(jsonPath("$.[*].definitionFr").value(hasItem( DEFAULT_DEFINITION_FR )))
            .andExpect(jsonPath("$.[*].versionDe").value(hasItem(DEFAULT_VERSION_DE)))
            .andExpect(jsonPath("$.[*].titleDe").value(hasItem( DEFAULT_TITLE_DE )))
            .andExpect(jsonPath("$.[*].definitionDe").value(hasItem( DEFAULT_DEFINITION_DE )))
            .andExpect(jsonPath("$.[*].versionEl").value(hasItem(DEFAULT_VERSION_EL)))
            .andExpect(jsonPath("$.[*].titleEl").value(hasItem( DEFAULT_TITLE_EL )))
            .andExpect(jsonPath("$.[*].definitionEl").value(hasItem( DEFAULT_DEFINITION_EL )))
            .andExpect(jsonPath("$.[*].versionHu").value(hasItem(DEFAULT_VERSION_HU)))
            .andExpect(jsonPath("$.[*].titleHu").value(hasItem( DEFAULT_TITLE_HU )))
            .andExpect(jsonPath("$.[*].definitionHu").value(hasItem( DEFAULT_DEFINITION_HU )))
            .andExpect(jsonPath("$.[*].versionIt").value(hasItem(DEFAULT_VERSION_IT)))
            .andExpect(jsonPath("$.[*].titleIt").value(hasItem( DEFAULT_TITLE_IT )))
            .andExpect(jsonPath("$.[*].definitionIt").value(hasItem( DEFAULT_DEFINITION_IT )))
            .andExpect(jsonPath("$.[*].versionJa").value(hasItem(DEFAULT_VERSION_JA)))
            .andExpect(jsonPath("$.[*].titleJa").value(hasItem( DEFAULT_TITLE_JA )))
            .andExpect(jsonPath("$.[*].definitionJa").value(hasItem( DEFAULT_DEFINITION_JA )))
            .andExpect(jsonPath("$.[*].versionLt").value(hasItem(DEFAULT_VERSION_LT)))
            .andExpect(jsonPath("$.[*].titleLt").value(hasItem( DEFAULT_TITLE_LT )))
            .andExpect(jsonPath("$.[*].definitionLt").value(hasItem( DEFAULT_DEFINITION_LT )))
            .andExpect(jsonPath("$.[*].versionMk").value(hasItem(DEFAULT_VERSION_MK)))
            .andExpect(jsonPath("$.[*].titleMk").value(hasItem( DEFAULT_TITLE_MK )))
            .andExpect(jsonPath("$.[*].definitionMk").value(hasItem( DEFAULT_DEFINITION_MK )))
            .andExpect(jsonPath("$.[*].versionNo").value(hasItem(DEFAULT_VERSION_NO)))
            .andExpect(jsonPath("$.[*].titleNo").value(hasItem( DEFAULT_TITLE_NO )))
            .andExpect(jsonPath("$.[*].definitionNo").value(hasItem( DEFAULT_DEFINITION_NO )))
            .andExpect(jsonPath("$.[*].versionPl").value(hasItem(DEFAULT_VERSION_PL)))
            .andExpect(jsonPath("$.[*].titlePl").value(hasItem( DEFAULT_TITLE_PL )))
            .andExpect(jsonPath("$.[*].definitionPl").value(hasItem( DEFAULT_DEFINITION_PL )))
            .andExpect(jsonPath("$.[*].versionPt").value(hasItem(DEFAULT_VERSION_PT)))
            .andExpect(jsonPath("$.[*].titlePt").value(hasItem( DEFAULT_TITLE_PT )))
            .andExpect(jsonPath("$.[*].definitionPt").value(hasItem( DEFAULT_DEFINITION_PT )))
            .andExpect(jsonPath("$.[*].versionRo").value(hasItem(DEFAULT_VERSION_RO)))
            .andExpect(jsonPath("$.[*].titleRo").value(hasItem( DEFAULT_TITLE_RO )))
            .andExpect(jsonPath("$.[*].definitionRo").value(hasItem( DEFAULT_DEFINITION_RO )))
            .andExpect(jsonPath("$.[*].versionRu").value(hasItem(DEFAULT_VERSION_RU)))
            .andExpect(jsonPath("$.[*].titleRu").value(hasItem( DEFAULT_TITLE_RU )))
            .andExpect(jsonPath("$.[*].definitionRu").value(hasItem( DEFAULT_DEFINITION_RU )))
            .andExpect(jsonPath("$.[*].versionSr").value(hasItem(DEFAULT_VERSION_SR)))
            .andExpect(jsonPath("$.[*].titleSr").value(hasItem( DEFAULT_TITLE_SR )))
            .andExpect(jsonPath("$.[*].definitionSr").value(hasItem( DEFAULT_DEFINITION_SR )))
            .andExpect(jsonPath("$.[*].versionSk").value(hasItem(DEFAULT_VERSION_SK)))
            .andExpect(jsonPath("$.[*].titleSk").value(hasItem( DEFAULT_TITLE_SK )))
            .andExpect(jsonPath("$.[*].definitionSk").value(hasItem( DEFAULT_DEFINITION_SK )))
            .andExpect(jsonPath("$.[*].versionSl").value(hasItem(DEFAULT_VERSION_SL)))
            .andExpect(jsonPath("$.[*].titleSl").value(hasItem( DEFAULT_TITLE_SL )))
            .andExpect(jsonPath("$.[*].definitionSl").value(hasItem( DEFAULT_DEFINITION_SL )))
            .andExpect(jsonPath("$.[*].versionEs").value(hasItem(DEFAULT_VERSION_ES)))
            .andExpect(jsonPath("$.[*].titleEs").value(hasItem( DEFAULT_TITLE_ES )))
            .andExpect(jsonPath("$.[*].definitionEs").value(hasItem( DEFAULT_DEFINITION_ES )))
            .andExpect(jsonPath("$.[*].versionSv").value(hasItem(DEFAULT_VERSION_SV)))
            .andExpect(jsonPath("$.[*].titleSv").value(hasItem( DEFAULT_TITLE_SV )))
            .andExpect(jsonPath("$.[*].definitionSv").value(hasItem( DEFAULT_DEFINITION_SV )));
    }

    @Test
    @Transactional
    void getVocabulary() throws Exception {
        // Initialize the database
        vocabularyRepository.saveAndFlush(vocabulary);

        // Get the vocabulary
        restVocabularyMockMvc.perform(get("/api/vocabularies/{id}", vocabulary.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vocabulary.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.uri").value(DEFAULT_URI))
            .andExpect(jsonPath("$.notation").value(DEFAULT_NOTATION))
            .andExpect(jsonPath("$.versionNumber").value(DEFAULT_VERSION_NUMBER.toString()))
            .andExpect(jsonPath("$.initialPublication").value(DEFAULT_INITIAL_PUBLICATION.intValue()))
            .andExpect(jsonPath("$.previousPublication").value(DEFAULT_PREVIOUS_PUBLICATION.intValue()))
            .andExpect(jsonPath("$.archived").value( DEFAULT_ARCHIVED ))
            .andExpect(jsonPath("$.withdrawn").value( DEFAULT_WITHDRAWN ))
            .andExpect(jsonPath("$.discoverable").value( DEFAULT_DISCOVERABLE ))
            .andExpect(jsonPath("$.sourceLanguage").value(DEFAULT_SOURCE_LANGUAGE))
//            .andExpect(jsonPath("$.agencyId").value(DEFAULT_AGENCY_ID.intValue()))
            .andExpect(jsonPath("$.agencyName").value(DEFAULT_AGENCY_NAME))
            .andExpect(jsonPath("$.agencyLogo").value(DEFAULT_AGENCY_LOGO))
            .andExpect(jsonPath("$.publicationDate").value(DEFAULT_PUBLICATION_DATE.toString()))
            .andExpect(jsonPath("$.lastModified").value(sameInstant(DEFAULT_LAST_MODIFIED)))
            .andExpect(jsonPath("$.notes").value( DEFAULT_NOTES ))
            .andExpect(jsonPath("$.versionSq").value(DEFAULT_VERSION_SQ))
            .andExpect(jsonPath("$.titleSq").value( DEFAULT_TITLE_SQ ))
            .andExpect(jsonPath("$.definitionSq").value( DEFAULT_DEFINITION_SQ ))
            .andExpect(jsonPath("$.versionBs").value(DEFAULT_VERSION_BS))
            .andExpect(jsonPath("$.titleBs").value( DEFAULT_TITLE_BS ))
            .andExpect(jsonPath("$.definitionBs").value( DEFAULT_DEFINITION_BS ))
            .andExpect(jsonPath("$.versionBg").value(DEFAULT_VERSION_BG))
            .andExpect(jsonPath("$.titleBg").value( DEFAULT_TITLE_BG ))
            .andExpect(jsonPath("$.definitionBg").value( DEFAULT_DEFINITION_BG ))
            .andExpect(jsonPath("$.versionHr").value(DEFAULT_VERSION_HR))
            .andExpect(jsonPath("$.titleHr").value( DEFAULT_TITLE_HR ))
            .andExpect(jsonPath("$.definitionHr").value( DEFAULT_DEFINITION_HR ))
            .andExpect(jsonPath("$.versionCs").value(DEFAULT_VERSION_CS))
            .andExpect(jsonPath("$.titleCs").value( DEFAULT_TITLE_CS ))
            .andExpect(jsonPath("$.definitionCs").value( DEFAULT_DEFINITION_CS ))
            .andExpect(jsonPath("$.versionDa").value(DEFAULT_VERSION_DA))
            .andExpect(jsonPath("$.titleDa").value( DEFAULT_TITLE_DA ))
            .andExpect(jsonPath("$.definitionDa").value( DEFAULT_DEFINITION_DA ))
            .andExpect(jsonPath("$.versionNl").value(DEFAULT_VERSION_NL))
            .andExpect(jsonPath("$.titleNl").value( DEFAULT_TITLE_NL ))
            .andExpect(jsonPath("$.definitionNl").value( DEFAULT_DEFINITION_NL ))
            .andExpect(jsonPath("$.versionEn").value(DEFAULT_VERSION_EN))
            .andExpect(jsonPath("$.titleEn").value( DEFAULT_TITLE_EN ))
            .andExpect(jsonPath("$.definitionEn").value( DEFAULT_DEFINITION_EN ))
            .andExpect(jsonPath("$.versionEt").value(DEFAULT_VERSION_ET))
            .andExpect(jsonPath("$.titleEt").value( DEFAULT_TITLE_ET ))
            .andExpect(jsonPath("$.definitionEt").value( DEFAULT_DEFINITION_ET ))
            .andExpect(jsonPath("$.versionFi").value(DEFAULT_VERSION_FI))
            .andExpect(jsonPath("$.titleFi").value( DEFAULT_TITLE_FI ))
            .andExpect(jsonPath("$.definitionFi").value( DEFAULT_DEFINITION_FI ))
            .andExpect(jsonPath("$.versionFr").value(DEFAULT_VERSION_FR))
            .andExpect(jsonPath("$.titleFr").value( DEFAULT_TITLE_FR ))
            .andExpect(jsonPath("$.definitionFr").value( DEFAULT_DEFINITION_FR ))
            .andExpect(jsonPath("$.versionDe").value(DEFAULT_VERSION_DE))
            .andExpect(jsonPath("$.titleDe").value( DEFAULT_TITLE_DE ))
            .andExpect(jsonPath("$.definitionDe").value( DEFAULT_DEFINITION_DE ))
            .andExpect(jsonPath("$.versionEl").value(DEFAULT_VERSION_EL))
            .andExpect(jsonPath("$.titleEl").value( DEFAULT_TITLE_EL ))
            .andExpect(jsonPath("$.definitionEl").value( DEFAULT_DEFINITION_EL ))
            .andExpect(jsonPath("$.versionHu").value(DEFAULT_VERSION_HU))
            .andExpect(jsonPath("$.titleHu").value( DEFAULT_TITLE_HU ))
            .andExpect(jsonPath("$.definitionHu").value( DEFAULT_DEFINITION_HU ))
            .andExpect(jsonPath("$.versionIt").value(DEFAULT_VERSION_IT))
            .andExpect(jsonPath("$.titleIt").value( DEFAULT_TITLE_IT ))
            .andExpect(jsonPath("$.definitionIt").value( DEFAULT_DEFINITION_IT ))
            .andExpect(jsonPath("$.versionJa").value(DEFAULT_VERSION_JA))
            .andExpect(jsonPath("$.titleJa").value( DEFAULT_TITLE_JA ))
            .andExpect(jsonPath("$.definitionJa").value( DEFAULT_DEFINITION_JA ))
            .andExpect(jsonPath("$.versionLt").value(DEFAULT_VERSION_LT))
            .andExpect(jsonPath("$.titleLt").value( DEFAULT_TITLE_LT ))
            .andExpect(jsonPath("$.definitionLt").value( DEFAULT_DEFINITION_LT ))
            .andExpect(jsonPath("$.versionMk").value(DEFAULT_VERSION_MK))
            .andExpect(jsonPath("$.titleMk").value( DEFAULT_TITLE_MK ))
            .andExpect(jsonPath("$.definitionMk").value( DEFAULT_DEFINITION_MK ))
            .andExpect(jsonPath("$.versionNo").value(DEFAULT_VERSION_NO))
            .andExpect(jsonPath("$.titleNo").value( DEFAULT_TITLE_NO ))
            .andExpect(jsonPath("$.definitionNo").value( DEFAULT_DEFINITION_NO ))
            .andExpect(jsonPath("$.versionPl").value(DEFAULT_VERSION_PL))
            .andExpect(jsonPath("$.titlePl").value( DEFAULT_TITLE_PL ))
            .andExpect(jsonPath("$.definitionPl").value( DEFAULT_DEFINITION_PL ))
            .andExpect(jsonPath("$.versionPt").value(DEFAULT_VERSION_PT))
            .andExpect(jsonPath("$.titlePt").value( DEFAULT_TITLE_PT ))
            .andExpect(jsonPath("$.definitionPt").value( DEFAULT_DEFINITION_PT ))
            .andExpect(jsonPath("$.versionRo").value(DEFAULT_VERSION_RO))
            .andExpect(jsonPath("$.titleRo").value( DEFAULT_TITLE_RO ))
            .andExpect(jsonPath("$.definitionRo").value( DEFAULT_DEFINITION_RO ))
            .andExpect(jsonPath("$.versionRu").value(DEFAULT_VERSION_RU))
            .andExpect(jsonPath("$.titleRu").value( DEFAULT_TITLE_RU ))
            .andExpect(jsonPath("$.definitionRu").value( DEFAULT_DEFINITION_RU ))
            .andExpect(jsonPath("$.versionSr").value(DEFAULT_VERSION_SR))
            .andExpect(jsonPath("$.titleSr").value( DEFAULT_TITLE_SR ))
            .andExpect(jsonPath("$.definitionSr").value( DEFAULT_DEFINITION_SR ))
            .andExpect(jsonPath("$.versionSk").value(DEFAULT_VERSION_SK))
            .andExpect(jsonPath("$.titleSk").value( DEFAULT_TITLE_SK ))
            .andExpect(jsonPath("$.definitionSk").value( DEFAULT_DEFINITION_SK ))
            .andExpect(jsonPath("$.versionSl").value(DEFAULT_VERSION_SL))
            .andExpect(jsonPath("$.titleSl").value( DEFAULT_TITLE_SL ))
            .andExpect(jsonPath("$.definitionSl").value( DEFAULT_DEFINITION_SL ))
            .andExpect(jsonPath("$.versionEs").value(DEFAULT_VERSION_ES))
            .andExpect(jsonPath("$.titleEs").value( DEFAULT_TITLE_ES ))
            .andExpect(jsonPath("$.definitionEs").value( DEFAULT_DEFINITION_ES ))
            .andExpect(jsonPath("$.versionSv").value(DEFAULT_VERSION_SV))
            .andExpect(jsonPath("$.titleSv").value( DEFAULT_TITLE_SV ))
            .andExpect(jsonPath("$.definitionSv").value( DEFAULT_DEFINITION_SV ));
    }

    @Test
    @Transactional
    void getNonExistingVocabulary() throws Exception {
        // Get the vocabulary
        restVocabularyMockMvc.perform(get("/api/vocabularies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateVocabulary() throws Exception {
        // Initialize the database
        vocabularyRepository.saveAndFlush(vocabulary);

        int databaseSizeBeforeUpdate = vocabularyRepository.findAll().size();

        // Update the vocabulary
        Vocabulary updatedVocabulary = vocabularyRepository.findById(vocabulary.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVocabulary are not directly saved in db
        em.detach(updatedVocabulary);
        updatedVocabulary
            .status(UPDATED_STATUS)
            .uri(UPDATED_URI)
            .notation(UPDATED_NOTATION)
            .versionNumber(UPDATED_VERSION_NUMBER)
            .initialPublication(UPDATED_INITIAL_PUBLICATION)
            .previousPublication(UPDATED_PREVIOUS_PUBLICATION)
            .archived(UPDATED_ARCHIVED)
            .withdrawn(UPDATED_WITHDRAWN)
            .discoverable(UPDATED_DISCOVERABLE)
            .sourceLanguage(UPDATED_SOURCE_LANGUAGE)
            .agencyId(UPDATED_AGENCY_ID)
            .agencyName(UPDATED_AGENCY_NAME)
            .agencyLogo(UPDATED_AGENCY_LOGO)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .notes(UPDATED_NOTES)
            .versionSq(UPDATED_VERSION_SQ)
            .titleSq(UPDATED_TITLE_SQ)
            .definitionSq(UPDATED_DEFINITION_SQ)
            .versionBs(UPDATED_VERSION_BS)
            .titleBs(UPDATED_TITLE_BS)
            .definitionBs(UPDATED_DEFINITION_BS)
            .versionBg(UPDATED_VERSION_BG)
            .titleBg(UPDATED_TITLE_BG)
            .definitionBg(UPDATED_DEFINITION_BG)
            .versionHr(UPDATED_VERSION_HR)
            .titleHr(UPDATED_TITLE_HR)
            .definitionHr(UPDATED_DEFINITION_HR)
            .versionCs(UPDATED_VERSION_CS)
            .titleCs(UPDATED_TITLE_CS)
            .definitionCs(UPDATED_DEFINITION_CS)
            .versionDa(UPDATED_VERSION_DA)
            .titleDa(UPDATED_TITLE_DA)
            .definitionDa(UPDATED_DEFINITION_DA)
            .versionNl(UPDATED_VERSION_NL)
            .titleNl(UPDATED_TITLE_NL)
            .definitionNl(UPDATED_DEFINITION_NL)
            .versionEn(UPDATED_VERSION_EN)
            .titleEn(UPDATED_TITLE_EN)
            .definitionEn(UPDATED_DEFINITION_EN)
            .versionEt(UPDATED_VERSION_ET)
            .titleEt(UPDATED_TITLE_ET)
            .definitionEt(UPDATED_DEFINITION_ET)
            .versionFi(UPDATED_VERSION_FI)
            .titleFi(UPDATED_TITLE_FI)
            .definitionFi(UPDATED_DEFINITION_FI)
            .versionFr(UPDATED_VERSION_FR)
            .titleFr(UPDATED_TITLE_FR)
            .definitionFr(UPDATED_DEFINITION_FR)
            .versionDe(UPDATED_VERSION_DE)
            .titleDe(UPDATED_TITLE_DE)
            .definitionDe(UPDATED_DEFINITION_DE)
            .versionEl(UPDATED_VERSION_EL)
            .titleEl(UPDATED_TITLE_EL)
            .definitionEl(UPDATED_DEFINITION_EL)
            .versionHu(UPDATED_VERSION_HU)
            .titleHu(UPDATED_TITLE_HU)
            .definitionHu(UPDATED_DEFINITION_HU)
            .versionIt(UPDATED_VERSION_IT)
            .titleIt(UPDATED_TITLE_IT)
            .definitionIt(UPDATED_DEFINITION_IT)
            .versionJa(UPDATED_VERSION_JA)
            .titleJa(UPDATED_TITLE_JA)
            .definitionJa(UPDATED_DEFINITION_JA)
            .versionLt(UPDATED_VERSION_LT)
            .titleLt(UPDATED_TITLE_LT)
            .definitionLt(UPDATED_DEFINITION_LT)
            .versionMk(UPDATED_VERSION_MK)
            .titleMk(UPDATED_TITLE_MK)
            .definitionMk(UPDATED_DEFINITION_MK)
            .versionNo(UPDATED_VERSION_NO)
            .titleNo(UPDATED_TITLE_NO)
            .definitionNo(UPDATED_DEFINITION_NO)
            .versionPl(UPDATED_VERSION_PL)
            .titlePl(UPDATED_TITLE_PL)
            .definitionPl(UPDATED_DEFINITION_PL)
            .versionPt(UPDATED_VERSION_PT)
            .titlePt(UPDATED_TITLE_PT)
            .definitionPt(UPDATED_DEFINITION_PT)
            .versionRo(UPDATED_VERSION_RO)
            .titleRo(UPDATED_TITLE_RO)
            .definitionRo(UPDATED_DEFINITION_RO)
            .versionRu(UPDATED_VERSION_RU)
            .titleRu(UPDATED_TITLE_RU)
            .definitionRu(UPDATED_DEFINITION_RU)
            .versionSr(UPDATED_VERSION_SR)
            .titleSr(UPDATED_TITLE_SR)
            .definitionSr(UPDATED_DEFINITION_SR)
            .versionSk(UPDATED_VERSION_SK)
            .titleSk(UPDATED_TITLE_SK)
            .definitionSk(UPDATED_DEFINITION_SK)
            .versionSl(UPDATED_VERSION_SL)
            .titleSl(UPDATED_TITLE_SL)
            .definitionSl(UPDATED_DEFINITION_SL)
            .versionEs(UPDATED_VERSION_ES)
            .titleEs(UPDATED_TITLE_ES)
            .definitionEs(UPDATED_DEFINITION_ES)
            .versionSv(UPDATED_VERSION_SV)
            .titleSv(UPDATED_TITLE_SV)
            .definitionSv(UPDATED_DEFINITION_SV);
        VocabularyDTO vocabularyDTO = vocabularyMapper.toDto(updatedVocabulary);

        restVocabularyMockMvc.perform(put("/api/vocabularies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyDTO)))
            .andExpect(status().isOk());

        // Validate the Vocabulary in the database
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeUpdate);
        Vocabulary testVocabulary = vocabularyList.get(vocabularyList.size() - 1);
        assertThat(testVocabulary.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVocabulary.getUri()).isEqualTo(UPDATED_URI);
        assertThat(testVocabulary.getNotation()).isEqualTo(UPDATED_NOTATION);
        assertThat(testVocabulary.getVersionNumber()).isEqualTo(UPDATED_VERSION_NUMBER);
        assertThat(testVocabulary.getInitialPublication()).isEqualTo(UPDATED_INITIAL_PUBLICATION);
        assertThat(testVocabulary.getPreviousPublication()).isEqualTo(UPDATED_PREVIOUS_PUBLICATION);
        assertThat(testVocabulary.isArchived()).isEqualTo(UPDATED_ARCHIVED);
        assertThat(testVocabulary.isWithdrawn()).isEqualTo(UPDATED_WITHDRAWN);
        assertThat(testVocabulary.isDiscoverable()).isEqualTo(UPDATED_DISCOVERABLE);
        assertThat(testVocabulary.getSourceLanguage()).isEqualTo(UPDATED_SOURCE_LANGUAGE);
        assertThat(testVocabulary.getAgencyId()).isEqualTo(UPDATED_AGENCY_ID);
        assertThat(testVocabulary.getAgencyName()).isEqualTo(UPDATED_AGENCY_NAME);
        assertThat(testVocabulary.getAgencyLogo()).isEqualTo(UPDATED_AGENCY_LOGO);
        assertThat(testVocabulary.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
        assertThat(testVocabulary.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testVocabulary.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testVocabulary.getVersionSq()).isEqualTo(UPDATED_VERSION_SQ);
        assertThat(testVocabulary.getTitleSq()).isEqualTo(UPDATED_TITLE_SQ);
        assertThat(testVocabulary.getDefinitionSq()).isEqualTo(UPDATED_DEFINITION_SQ);
        assertThat(testVocabulary.getVersionBs()).isEqualTo(UPDATED_VERSION_BS);
        assertThat(testVocabulary.getTitleBs()).isEqualTo(UPDATED_TITLE_BS);
        assertThat(testVocabulary.getDefinitionBs()).isEqualTo(UPDATED_DEFINITION_BS);
        assertThat(testVocabulary.getVersionBg()).isEqualTo(UPDATED_VERSION_BG);
        assertThat(testVocabulary.getTitleBg()).isEqualTo(UPDATED_TITLE_BG);
        assertThat(testVocabulary.getDefinitionBg()).isEqualTo(UPDATED_DEFINITION_BG);
        assertThat(testVocabulary.getVersionHr()).isEqualTo(UPDATED_VERSION_HR);
        assertThat(testVocabulary.getTitleHr()).isEqualTo(UPDATED_TITLE_HR);
        assertThat(testVocabulary.getDefinitionHr()).isEqualTo(UPDATED_DEFINITION_HR);
        assertThat(testVocabulary.getVersionCs()).isEqualTo(UPDATED_VERSION_CS);
        assertThat(testVocabulary.getTitleCs()).isEqualTo(UPDATED_TITLE_CS);
        assertThat(testVocabulary.getDefinitionCs()).isEqualTo(UPDATED_DEFINITION_CS);
        assertThat(testVocabulary.getVersionDa()).isEqualTo(UPDATED_VERSION_DA);
        assertThat(testVocabulary.getTitleDa()).isEqualTo(UPDATED_TITLE_DA);
        assertThat(testVocabulary.getDefinitionDa()).isEqualTo(UPDATED_DEFINITION_DA);
        assertThat(testVocabulary.getVersionNl()).isEqualTo(UPDATED_VERSION_NL);
        assertThat(testVocabulary.getTitleNl()).isEqualTo(UPDATED_TITLE_NL);
        assertThat(testVocabulary.getDefinitionNl()).isEqualTo(UPDATED_DEFINITION_NL);
        assertThat(testVocabulary.getVersionEn()).isEqualTo(UPDATED_VERSION_EN);
        assertThat(testVocabulary.getTitleEn()).isEqualTo(UPDATED_TITLE_EN);
        assertThat(testVocabulary.getDefinitionEn()).isEqualTo(UPDATED_DEFINITION_EN);
        assertThat(testVocabulary.getVersionEt()).isEqualTo(UPDATED_VERSION_ET);
        assertThat(testVocabulary.getTitleEt()).isEqualTo(UPDATED_TITLE_ET);
        assertThat(testVocabulary.getDefinitionEt()).isEqualTo(UPDATED_DEFINITION_ET);
        assertThat(testVocabulary.getVersionFi()).isEqualTo(UPDATED_VERSION_FI);
        assertThat(testVocabulary.getTitleFi()).isEqualTo(UPDATED_TITLE_FI);
        assertThat(testVocabulary.getDefinitionFi()).isEqualTo(UPDATED_DEFINITION_FI);
        assertThat(testVocabulary.getVersionFr()).isEqualTo(UPDATED_VERSION_FR);
        assertThat(testVocabulary.getTitleFr()).isEqualTo(UPDATED_TITLE_FR);
        assertThat(testVocabulary.getDefinitionFr()).isEqualTo(UPDATED_DEFINITION_FR);
        assertThat(testVocabulary.getVersionDe()).isEqualTo(UPDATED_VERSION_DE);
        assertThat(testVocabulary.getTitleDe()).isEqualTo(UPDATED_TITLE_DE);
        assertThat(testVocabulary.getDefinitionDe()).isEqualTo(UPDATED_DEFINITION_DE);
        assertThat(testVocabulary.getVersionEl()).isEqualTo(UPDATED_VERSION_EL);
        assertThat(testVocabulary.getTitleEl()).isEqualTo(UPDATED_TITLE_EL);
        assertThat(testVocabulary.getDefinitionEl()).isEqualTo(UPDATED_DEFINITION_EL);
        assertThat(testVocabulary.getVersionHu()).isEqualTo(UPDATED_VERSION_HU);
        assertThat(testVocabulary.getTitleHu()).isEqualTo(UPDATED_TITLE_HU);
        assertThat(testVocabulary.getDefinitionHu()).isEqualTo(UPDATED_DEFINITION_HU);
        assertThat(testVocabulary.getVersionIt()).isEqualTo(UPDATED_VERSION_IT);
        assertThat(testVocabulary.getTitleIt()).isEqualTo(UPDATED_TITLE_IT);
        assertThat(testVocabulary.getDefinitionIt()).isEqualTo(UPDATED_DEFINITION_IT);
        assertThat(testVocabulary.getVersionJa()).isEqualTo(UPDATED_VERSION_JA);
        assertThat(testVocabulary.getTitleJa()).isEqualTo(UPDATED_TITLE_JA);
        assertThat(testVocabulary.getDefinitionJa()).isEqualTo(UPDATED_DEFINITION_JA);
        assertThat(testVocabulary.getVersionLt()).isEqualTo(UPDATED_VERSION_LT);
        assertThat(testVocabulary.getTitleLt()).isEqualTo(UPDATED_TITLE_LT);
        assertThat(testVocabulary.getDefinitionLt()).isEqualTo(UPDATED_DEFINITION_LT);
        assertThat(testVocabulary.getVersionMk()).isEqualTo(UPDATED_VERSION_MK);
        assertThat(testVocabulary.getTitleMk()).isEqualTo(UPDATED_TITLE_MK);
        assertThat(testVocabulary.getDefinitionMk()).isEqualTo(UPDATED_DEFINITION_MK);
        assertThat(testVocabulary.getVersionNo()).isEqualTo(UPDATED_VERSION_NO);
        assertThat(testVocabulary.getTitleNo()).isEqualTo(UPDATED_TITLE_NO);
        assertThat(testVocabulary.getDefinitionNo()).isEqualTo(UPDATED_DEFINITION_NO);
        assertThat(testVocabulary.getVersionPl()).isEqualTo(UPDATED_VERSION_PL);
        assertThat(testVocabulary.getTitlePl()).isEqualTo(UPDATED_TITLE_PL);
        assertThat(testVocabulary.getDefinitionPl()).isEqualTo(UPDATED_DEFINITION_PL);
        assertThat(testVocabulary.getVersionPt()).isEqualTo(UPDATED_VERSION_PT);
        assertThat(testVocabulary.getTitlePt()).isEqualTo(UPDATED_TITLE_PT);
        assertThat(testVocabulary.getDefinitionPt()).isEqualTo(UPDATED_DEFINITION_PT);
        assertThat(testVocabulary.getVersionRo()).isEqualTo(UPDATED_VERSION_RO);
        assertThat(testVocabulary.getTitleRo()).isEqualTo(UPDATED_TITLE_RO);
        assertThat(testVocabulary.getDefinitionRo()).isEqualTo(UPDATED_DEFINITION_RO);
        assertThat(testVocabulary.getVersionRu()).isEqualTo(UPDATED_VERSION_RU);
        assertThat(testVocabulary.getTitleRu()).isEqualTo(UPDATED_TITLE_RU);
        assertThat(testVocabulary.getDefinitionRu()).isEqualTo(UPDATED_DEFINITION_RU);
        assertThat(testVocabulary.getVersionSr()).isEqualTo(UPDATED_VERSION_SR);
        assertThat(testVocabulary.getTitleSr()).isEqualTo(UPDATED_TITLE_SR);
        assertThat(testVocabulary.getDefinitionSr()).isEqualTo(UPDATED_DEFINITION_SR);
        assertThat(testVocabulary.getVersionSk()).isEqualTo(UPDATED_VERSION_SK);
        assertThat(testVocabulary.getTitleSk()).isEqualTo(UPDATED_TITLE_SK);
        assertThat(testVocabulary.getDefinitionSk()).isEqualTo(UPDATED_DEFINITION_SK);
        assertThat(testVocabulary.getVersionSl()).isEqualTo(UPDATED_VERSION_SL);
        assertThat(testVocabulary.getTitleSl()).isEqualTo(UPDATED_TITLE_SL);
        assertThat(testVocabulary.getDefinitionSl()).isEqualTo(UPDATED_DEFINITION_SL);
        assertThat(testVocabulary.getVersionEs()).isEqualTo(UPDATED_VERSION_ES);
        assertThat(testVocabulary.getTitleEs()).isEqualTo(UPDATED_TITLE_ES);
        assertThat(testVocabulary.getDefinitionEs()).isEqualTo(UPDATED_DEFINITION_ES);
        assertThat(testVocabulary.getVersionSv()).isEqualTo(UPDATED_VERSION_SV);
        assertThat(testVocabulary.getTitleSv()).isEqualTo(UPDATED_TITLE_SV);
        assertThat(testVocabulary.getDefinitionSv()).isEqualTo(UPDATED_DEFINITION_SV);

        // Validate the VocabularyEditor in Elasticsearch
        VocabularyEditor vocabularyEditor = vocabularyEditorMapper.toEntity(vocabularyMapper.toDto(testVocabulary));
        // Mocking ElasticRepository is still not successful
//        verify(mockVocabularyEditorSearchRepository, times(1)).save(vocabularyEditor);

        // remove after saving in elastic index after Mock ElasticRepository successfull
        mockVocabularyEditorSearchRepository.delete( vocabularyEditor );
    }

    @Test
    @Transactional
    void updateNonExistingVocabulary() throws Exception {
        int databaseSizeBeforeUpdate = vocabularyRepository.findAll().size();

        // Create the Vocabulary
        VocabularyDTO vocabularyDTO = vocabularyMapper.toDto(vocabulary);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVocabularyMockMvc.perform(put("/api/vocabularies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Vocabulary in the database
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVocabulary() throws Exception {
        // Initialize the database
        vocabularyRepository.saveAndFlush(vocabulary);

        int databaseSizeBeforeDelete = vocabularyRepository.findAll().size();

        // Delete the vocabulary
        restVocabularyMockMvc.perform(delete("/api/vocabularies/{id}", vocabulary.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Vocabulary> vocabularyList = vocabularyRepository.findAll();
        assertThat(vocabularyList).hasSize(databaseSizeBeforeDelete - 1);
    }
 
    @Test
    @Transactional
    void shouldUpdateVocabularyLogoOnAgencyUpdate() throws Exception
    {
        // Initialize the database
        vocabularyRepository.saveAndFlush(vocabulary);

        // Update the agency
        Agency updatedAgency = agencyRepository.findById(agency.getId()).orElseThrow();

        // Disconnect from session so that the updates on updatedAgency are not directly saved in db
        em.detach(updatedAgency);
        updatedAgency.name(UPDATED_NAME)
            .link(UPDATED_LINK)
            .description(UPDATED_DESCRIPTION)
            .logopath(UPDATED_LOGOPATH)
            .license(UPDATED_LICENSE)
            .licenseId(UPDATED_LICENSE_ID)
            .uri(UPDATED_URI + "[VOCABULARY]")
            .canonicalUri(UPDATED_CANONICAL_URI);

        AgencyDTO agencyDTO = agencyMapper.toDto(updatedAgency);

        restVocabularyMockMvc.perform(put("/api/agencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(agencyDTO)))
                .andExpect(status().isOk());

        // Verify the vocabulary was updated.
        Vocabulary vocabulary = vocabularyRepository.findAll().get(0);
        assertThat( vocabulary.getAgencyLogo() ).isEqualTo( UPDATED_AGENCY_LOGO );
        // FIXME - the agency name doesn't appear to get updated, is this intentional?
        //assertThat( vocabulary.getAgencyName() ).isEqualTo( UPDATED_AGENCY_NAME );

        // Verify that the agency ID did not change.
        assertThat( vocabulary.getAgencyId() ).isEqualTo( agency.getId() );
    }
}
