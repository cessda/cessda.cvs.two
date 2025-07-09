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
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.repository.AuthorityRepository;
import eu.cessda.cvs.repository.UserRepository;
import eu.cessda.cvs.repository.VocabularyRepository;
import eu.cessda.cvs.repository.search.VocabularyEditorSearchRepository;
import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.TokenProvider;
import eu.cessda.cvs.service.dto.AgencyDTO;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Vocabulary REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN )
public class VocabularyResourceIT extends TestUtil {

    private static final Status DEFAULT_STATUS = Status.DRAFT;

    private static final String DEFAULT_URI = "AAAAAAAAAA";
    private static final String UPDATED_URI = "BBBBBBBBBB";

    private static final String DEFAULT_NOTATION = "AAAAAAAAAA";

    private static final VersionNumber DEFAULT_VERSION_NUMBER = VersionNumber.fromString("1.0.0");

    private static final Long DEFAULT_INITIAL_PUBLICATION = 1L;

    private static final Long DEFAULT_PREVIOUS_PUBLICATION = 1L;

    private static final Boolean DEFAULT_ARCHIVED = false;

    private static final Boolean DEFAULT_WITHDRAWN = false;

    private static final Boolean DEFAULT_DISCOVERABLE = false;

    private static final String DEFAULT_SOURCE_LANGUAGE = "en";

    private static final Long DEFAULT_AGENCY_ID = 1L;

    private static final String DEFAULT_AGENCY_NAME = "AAAAAAAAAA";

    private static final String DEFAULT_AGENCY_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_LOGO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PUBLICATION_DATE = LocalDate.now().minusDays(1);

    static
    {
        LocalDate.now();
    }

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED = ZonedDateTime.now().minusDays(1);

    static
    {
        ZonedDateTime.now();
    }

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_SQ = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_SQ = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_SQ = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_BS = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_BS = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_BS = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_BG = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_BG = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_BG = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_HR = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_HR = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_HR = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_CS = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_CS = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_CS = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_DA = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_DA = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_DA = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_NL = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_NL = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_NL = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_EN = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_EN = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_EN = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_ET = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_ET = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_ET = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_FI = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_FI = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_FI = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_FR = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_FR = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_FR = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_DE = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_DE = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_DE = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_EL = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_EL = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_EL = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_HU = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_HU = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_HU = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_IT = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_IT = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_IT = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_JA = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_JA = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_JA = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_LT = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_LT = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_LT = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_MK = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_MK = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_MK = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_NO = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_NO = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_NO = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_PL = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_PL = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_PL = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_PT = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_PT = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_PT = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_RO = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_RO = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_RO = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_RU = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_RU = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_RU = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_SR = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_SR = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_SR = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_SK = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_SK = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_SK = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_SL = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_SL = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_SL = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_ES = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_ES = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_ES = "AAAAAAAAAA";

    private static final String DEFAULT_VERSION_SV = "AAAAAAAAAA";

    private static final String DEFAULT_TITLE_SV = "AAAAAAAAAA";

    private static final String DEFAULT_DEFINITION_SV = "AAAAAAAAAA";

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

    private final AgencyMapper agencyMapper;
    private final AgencyRepository agencyRepository;
    private final VocabularyRepository vocabularyRepository;
    private final EntityManager em;
    private final MockMvc restVocabularyMockMvc;

    private Vocabulary vocabulary;

    private Agency agency;

    @Autowired
    public VocabularyResourceIT(
        AuthenticationManagerBuilder authenticationManagerBuilder,
        AuthorityRepository authorityRepository,
        UserRepository userRepository,
        TokenProvider tokenProvider,
        PasswordEncoder passwordEncoder,
        AgencyMapper agencyMapper,
        AgencyRepository agencyRepository,
        VocabularyRepository vocabularyRepository,
        VocabularyMapper vocabularyMapper,
        VocabularyEditorMapper vocabularyEditorMapper,
        VocabularyEditorSearchRepository mockVocabularyEditorSearchRepository,
        EntityManager em,
        MockMvc restVocabularyMockMvc
    )
    {
        super( authenticationManagerBuilder, authorityRepository, userRepository, tokenProvider, passwordEncoder );
        this.agencyMapper = agencyMapper;
        this.agencyRepository = agencyRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.em = em;
        this.restVocabularyMockMvc = restVocabularyMockMvc;
    }

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
