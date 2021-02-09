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
import eu.cessda.cvs.domain.Licence;
import eu.cessda.cvs.repository.LicenceRepository;
import eu.cessda.cvs.service.LicenceService;
import eu.cessda.cvs.service.dto.LicenceDTO;
import eu.cessda.cvs.service.mapper.LicenceMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LicenceResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class LicenceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_ABBR = "AAAAAAAAAA";
    private static final String UPDATED_ABBR = "BBBBBBBBBB";

    @Autowired
    private LicenceRepository licenceRepository;

    @Autowired
    private LicenceMapper licenceMapper;

    @Autowired
    private LicenceService licenceService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLicenceMockMvc;

    private Licence licence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Licence createEntity(EntityManager em) {
        Licence licence = new Licence()
            .name(DEFAULT_NAME)
            .link(DEFAULT_LINK)
            .logoLink(DEFAULT_LOGO_LINK)
            .abbr(DEFAULT_ABBR);
        return licence;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Licence createUpdatedEntity(EntityManager em) {
        Licence licence = new Licence()
            .name(UPDATED_NAME)
            .link(UPDATED_LINK)
            .logoLink(UPDATED_LOGO_LINK)
            .abbr(UPDATED_ABBR);
        return licence;
    }

    @BeforeEach
    public void initTest() {
        licence = createEntity(em);
    }

    @Test
    @Transactional
    public void createLicence() throws Exception {
        int databaseSizeBeforeCreate = licenceRepository.findAll().size();

        // Create the Licence
        LicenceDTO licenceDTO = licenceMapper.toDto(licence);
        restLicenceMockMvc.perform(post("/api/licences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(licenceDTO)))
            .andExpect(status().isCreated());

        // Validate the Licence in the database
        List<Licence> licenceList = licenceRepository.findAll();
        assertThat(licenceList).hasSize(databaseSizeBeforeCreate + 1);
        Licence testLicence = licenceList.get(licenceList.size() - 1);
        assertThat(testLicence.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLicence.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testLicence.getLogoLink()).isEqualTo(DEFAULT_LOGO_LINK);
        assertThat(testLicence.getAbbr()).isEqualTo(DEFAULT_ABBR);
    }

    @Test
    @Transactional
    public void createLicenceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = licenceRepository.findAll().size();

        // Create the Licence with an existing ID
        licence.setId(1L);
        LicenceDTO licenceDTO = licenceMapper.toDto(licence);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLicenceMockMvc.perform(post("/api/licences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(licenceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Licence in the database
        List<Licence> licenceList = licenceRepository.findAll();
        assertThat(licenceList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = licenceRepository.findAll().size();
        // set the field null
        licence.setName(null);

        // Create the Licence, which fails.
        LicenceDTO licenceDTO = licenceMapper.toDto(licence);

        restLicenceMockMvc.perform(post("/api/licences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(licenceDTO)))
            .andExpect(status().isBadRequest());

        List<Licence> licenceList = licenceRepository.findAll();
        assertThat(licenceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLicences() throws Exception {
        // Initialize the database
        licenceRepository.saveAndFlush(licence);

        // Get all the licenceList
        restLicenceMockMvc.perform(get("/api/licences?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(licence.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].logoLink").value(hasItem(DEFAULT_LOGO_LINK)))
            .andExpect(jsonPath("$.[*].abbr").value(hasItem(DEFAULT_ABBR)));
    }

    @Test
    @Transactional
    public void getLicence() throws Exception {
        // Initialize the database
        licenceRepository.saveAndFlush(licence);

        // Get the licence
        restLicenceMockMvc.perform(get("/api/licences/{id}", licence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(licence.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK))
            .andExpect(jsonPath("$.logoLink").value(DEFAULT_LOGO_LINK))
            .andExpect(jsonPath("$.abbr").value(DEFAULT_ABBR));
    }

    @Test
    @Transactional
    public void getNonExistingLicence() throws Exception {
        // Get the licence
        restLicenceMockMvc.perform(get("/api/licences/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLicence() throws Exception {
        // Initialize the database
        licenceRepository.saveAndFlush(licence);

        int databaseSizeBeforeUpdate = licenceRepository.findAll().size();

        // Update the licence
        Licence updatedLicence = licenceRepository.findById(licence.getId()).get();
        // Disconnect from session so that the updates on updatedLicence are not directly saved in db
        em.detach(updatedLicence);
        updatedLicence
            .name(UPDATED_NAME)
            .link(UPDATED_LINK)
            .logoLink(UPDATED_LOGO_LINK)
            .abbr(UPDATED_ABBR);
        LicenceDTO licenceDTO = licenceMapper.toDto(updatedLicence);

        restLicenceMockMvc.perform(put("/api/licences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(licenceDTO)))
            .andExpect(status().isOk());

        // Validate the Licence in the database
        List<Licence> licenceList = licenceRepository.findAll();
        assertThat(licenceList).hasSize(databaseSizeBeforeUpdate);
        Licence testLicence = licenceList.get(licenceList.size() - 1);
        assertThat(testLicence.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLicence.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testLicence.getLogoLink()).isEqualTo(UPDATED_LOGO_LINK);
        assertThat(testLicence.getAbbr()).isEqualTo(UPDATED_ABBR);
    }

    @Test
    @Transactional
    public void updateNonExistingLicence() throws Exception {
        int databaseSizeBeforeUpdate = licenceRepository.findAll().size();

        // Create the Licence
        LicenceDTO licenceDTO = licenceMapper.toDto(licence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenceMockMvc.perform(put("/api/licences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(licenceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Licence in the database
        List<Licence> licenceList = licenceRepository.findAll();
        assertThat(licenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLicence() throws Exception {
        // Initialize the database
        licenceRepository.saveAndFlush(licence);

        int databaseSizeBeforeDelete = licenceRepository.findAll().size();

        // Delete the licence
        restLicenceMockMvc.perform(delete("/api/licences/{id}", licence.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Licence> licenceList = licenceRepository.findAll();
        assertThat(licenceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
