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
import eu.cessda.cvs.domain.Agency;
import eu.cessda.cvs.repository.AgencyRepository;
import eu.cessda.cvs.service.AgencyService;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.service.mapper.AgencyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AgencyResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class AgencyResourceIT {

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

    private static final String DEFAULT_URI = "AAAAAAAAAA";
    private static final String UPDATED_URI = "BBBBBBBBBB";

    private static final String DEFAULT_URI_CODE = "AAAAAAAAAA";
    private static final String UPDATED_URI_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CANONICAL_URI = "AAAAAAAAAA";
    private static final String UPDATED_CANONICAL_URI = "BBBBBBBBBB";

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private AgencyMapper agencyMapper;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgencyMockMvc;

    private Agency agency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agency createEntity(EntityManager em) {
        Agency agency = new Agency()
            .name(DEFAULT_NAME)
            .link(DEFAULT_LINK)
            .description(DEFAULT_DESCRIPTION)
            .logopath(DEFAULT_LOGOPATH)
            .license(DEFAULT_LICENSE)
            .licenseId(DEFAULT_LICENSE_ID)
            .uri(DEFAULT_URI)
            .uriCode(DEFAULT_URI_CODE)
            .canonicalUri(DEFAULT_CANONICAL_URI);
        return agency;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agency createUpdatedEntity(EntityManager em) {
        Agency agency = new Agency()
            .name(UPDATED_NAME)
            .link(UPDATED_LINK)
            .description(UPDATED_DESCRIPTION)
            .logopath(UPDATED_LOGOPATH)
            .license(UPDATED_LICENSE)
            .licenseId(UPDATED_LICENSE_ID)
            .uri(UPDATED_URI)
            .uriCode(UPDATED_URI_CODE)
            .canonicalUri(UPDATED_CANONICAL_URI);
        return agency;
    }

    @BeforeEach
    public void initTest() {
        agency = createEntity(em);
    }

    @Test
    @Transactional
    public void createAgency() throws Exception {
        int databaseSizeBeforeCreate = agencyRepository.findAll().size();

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);
        restAgencyMockMvc.perform(post("/api/agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(agencyDTO)))
            .andExpect(status().isCreated());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeCreate + 1);
        Agency testAgency = agencyList.get(agencyList.size() - 1);
        assertThat(testAgency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAgency.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testAgency.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAgency.getLogopath()).isEqualTo(DEFAULT_LOGOPATH);
        assertThat(testAgency.getLicense()).isEqualTo(DEFAULT_LICENSE);
        assertThat(testAgency.getLicenseId()).isEqualTo(DEFAULT_LICENSE_ID);
        assertThat(testAgency.getUri()).isEqualTo(DEFAULT_URI);
        assertThat(testAgency.getUri()).isEqualTo(DEFAULT_URI_CODE);
        assertThat(testAgency.getCanonicalUri()).isEqualTo(DEFAULT_CANONICAL_URI);
    }

    @Test
    @Transactional
    public void createAgencyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = agencyRepository.findAll().size();

        // Create the Agency with an existing ID
        agency.setId(1L);
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgencyMockMvc.perform(post("/api/agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(agencyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllAgencies() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        // Get all the agencyList
        restAgencyMockMvc.perform(get("/api/agencies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].logopath").value(hasItem(DEFAULT_LOGOPATH)))
            .andExpect(jsonPath("$.[*].license").value(hasItem(DEFAULT_LICENSE)))
            .andExpect(jsonPath("$.[*].licenseId").value(hasItem(DEFAULT_LICENSE_ID.intValue())))
            .andExpect(jsonPath("$.[*].uri").value(hasItem(DEFAULT_URI)))
            .andExpect(jsonPath("$.[*].uriCode").value(hasItem(DEFAULT_URI_CODE)))
            .andExpect(jsonPath("$.[*].canonicalUri").value(hasItem(DEFAULT_CANONICAL_URI)));
    }

    @Test
    @Transactional
    public void getAgency() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        // Get the agency
        restAgencyMockMvc.perform(get("/api/agencies/{id}", agency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agency.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.logopath").value(DEFAULT_LOGOPATH))
            .andExpect(jsonPath("$.license").value(DEFAULT_LICENSE))
            .andExpect(jsonPath("$.licenseId").value(DEFAULT_LICENSE_ID.intValue()))
            .andExpect(jsonPath("$.uri").value(DEFAULT_URI))
            .andExpect(jsonPath("$.uriCode").value(DEFAULT_URI_CODE))
            .andExpect(jsonPath("$.canonicalUri").value(DEFAULT_CANONICAL_URI));
    }

    @Test
    @Transactional
    public void getNonExistingAgency() throws Exception {
        // Get the agency
        restAgencyMockMvc.perform(get("/api/agencies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAgency() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();

        // Update the agency
        Agency updatedAgency = agencyRepository.findById(agency.getId()).get();
        // Disconnect from session so that the updates on updatedAgency are not directly saved in db
        em.detach(updatedAgency);
        updatedAgency
            .name(UPDATED_NAME)
            .link(UPDATED_LINK)
            .description(UPDATED_DESCRIPTION)
            .logopath(UPDATED_LOGOPATH)
            .license(UPDATED_LICENSE)
            .licenseId(UPDATED_LICENSE_ID)
            .uri(UPDATED_URI)
            .uriCode(UPDATED_URI_CODE)
            .canonicalUri(UPDATED_CANONICAL_URI);
        AgencyDTO agencyDTO = agencyMapper.toDto(updatedAgency);

        restAgencyMockMvc.perform(put("/api/agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(agencyDTO)))
            .andExpect(status().isOk());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
        Agency testAgency = agencyList.get(agencyList.size() - 1);
        assertThat(testAgency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAgency.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testAgency.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAgency.getLogopath()).isEqualTo(UPDATED_LOGOPATH);
        assertThat(testAgency.getLicense()).isEqualTo(UPDATED_LICENSE);
        assertThat(testAgency.getLicenseId()).isEqualTo(UPDATED_LICENSE_ID);
        assertThat(testAgency.getUri()).isEqualTo(UPDATED_URI);
        assertThat(testAgency.getUriCode()).isEqualTo(UPDATED_URI_CODE);
        assertThat(testAgency.getCanonicalUri()).isEqualTo(UPDATED_CANONICAL_URI);
    }

    @Test
    @Transactional
    public void updateNonExistingAgency() throws Exception {
        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgencyMockMvc.perform(put("/api/agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(agencyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAgency() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        int databaseSizeBeforeDelete = agencyRepository.findAll().size();

        // Delete the agency
        restAgencyMockMvc.perform(delete("/api/agencies/{id}", agency.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
