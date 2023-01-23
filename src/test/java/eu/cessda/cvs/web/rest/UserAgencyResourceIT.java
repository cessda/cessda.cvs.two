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
import eu.cessda.cvs.domain.UserAgency;
import eu.cessda.cvs.domain.enumeration.AgencyRole;
import eu.cessda.cvs.repository.UserAgencyRepository;
import eu.cessda.cvs.service.UserAgencyService;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.mapper.UserAgencyMapper;
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
 * Integration tests for the {@link UserAgencyResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class UserAgencyResourceIT {

    private static final AgencyRole DEFAULT_AGENCY_ROLE = AgencyRole.ADMIN;
    private static final AgencyRole UPDATED_AGENCY_ROLE = AgencyRole.VIEW;

    private static final String DEFAULT_LANGUAGE = "ALBANIAN";
    private static final String UPDATED_LANGUAGE = "BOSNIAN";

    @Autowired
    private UserAgencyRepository userAgencyRepository;

    @Autowired
    private UserAgencyMapper userAgencyMapper;

    @Autowired
    private UserAgencyService userAgencyService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserAgencyMockMvc;

    private UserAgency userAgency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAgency createEntity(EntityManager em) {
        UserAgency userAgency = new UserAgency()
            .agencyRole(DEFAULT_AGENCY_ROLE)
            .language(DEFAULT_LANGUAGE);
        return userAgency;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAgency createUpdatedEntity(EntityManager em) {
        UserAgency userAgency = new UserAgency()
            .agencyRole(UPDATED_AGENCY_ROLE)
            .language(UPDATED_LANGUAGE);
        return userAgency;
    }

    @BeforeEach
    public void initTest() {
        userAgency = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserAgency() throws Exception {
        int databaseSizeBeforeCreate = userAgencyRepository.findAll().size();

        // Create the UserAgency
        UserAgencyDTO userAgencyDTO = userAgencyMapper.toDto(userAgency);
        restUserAgencyMockMvc.perform(post("/api/user-agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userAgencyDTO)))
            .andExpect(status().isCreated());

        // Validate the UserAgency in the database
        List<UserAgency> userAgencyList = userAgencyRepository.findAll();
        assertThat(userAgencyList).hasSize(databaseSizeBeforeCreate + 1);
        UserAgency testUserAgency = userAgencyList.get(userAgencyList.size() - 1);
        assertThat(testUserAgency.getAgencyRole()).isEqualTo(DEFAULT_AGENCY_ROLE);
        assertThat(testUserAgency.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    public void createUserAgencyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userAgencyRepository.findAll().size();

        // Create the UserAgency with an existing ID
        userAgency.setId(1L);
        UserAgencyDTO userAgencyDTO = userAgencyMapper.toDto(userAgency);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAgencyMockMvc.perform(post("/api/user-agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userAgencyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserAgency in the database
        List<UserAgency> userAgencyList = userAgencyRepository.findAll();
        assertThat(userAgencyList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllUserAgencies() throws Exception {
        // Initialize the database
        userAgencyRepository.saveAndFlush(userAgency);

        // Get all the userAgencyList
        restUserAgencyMockMvc.perform(get("/api/user-agencies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAgency.getId().intValue())))
            .andExpect(jsonPath("$.[*].agencyRole").value(hasItem(DEFAULT_AGENCY_ROLE.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }

    @Test
    @Transactional
    public void getUserAgency() throws Exception {
        // Initialize the database
        userAgencyRepository.saveAndFlush(userAgency);

        // Get the userAgency
        restUserAgencyMockMvc.perform(get("/api/user-agencies/{id}", userAgency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userAgency.getId().intValue()))
            .andExpect(jsonPath("$.agencyRole").value(DEFAULT_AGENCY_ROLE.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserAgency() throws Exception {
        // Get the userAgency
        restUserAgencyMockMvc.perform(get("/api/user-agencies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserAgency() throws Exception {
        // Initialize the database
        userAgencyRepository.saveAndFlush(userAgency);

        int databaseSizeBeforeUpdate = userAgencyRepository.findAll().size();

        // Update the userAgency
        UserAgency updatedUserAgency = userAgencyRepository.findById(userAgency.getId()).get();
        // Disconnect from session so that the updates on updatedUserAgency are not directly saved in db
        em.detach(updatedUserAgency);
        updatedUserAgency
            .agencyRole(UPDATED_AGENCY_ROLE)
            .language(UPDATED_LANGUAGE);
        UserAgencyDTO userAgencyDTO = userAgencyMapper.toDto(updatedUserAgency);

        restUserAgencyMockMvc.perform(put("/api/user-agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userAgencyDTO)))
            .andExpect(status().isOk());

        // Validate the UserAgency in the database
        List<UserAgency> userAgencyList = userAgencyRepository.findAll();
        assertThat(userAgencyList).hasSize(databaseSizeBeforeUpdate);
        UserAgency testUserAgency = userAgencyList.get(userAgencyList.size() - 1);
        assertThat(testUserAgency.getAgencyRole()).isEqualTo(UPDATED_AGENCY_ROLE);
        assertThat(testUserAgency.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void updateNonExistingUserAgency() throws Exception {
        int databaseSizeBeforeUpdate = userAgencyRepository.findAll().size();

        // Create the UserAgency
        UserAgencyDTO userAgencyDTO = userAgencyMapper.toDto(userAgency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAgencyMockMvc.perform(put("/api/user-agencies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userAgencyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserAgency in the database
        List<UserAgency> userAgencyList = userAgencyRepository.findAll();
        assertThat(userAgencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserAgency() throws Exception {
        // Initialize the database
        userAgencyRepository.saveAndFlush(userAgency);

        int databaseSizeBeforeDelete = userAgencyRepository.findAll().size();

        // Delete the userAgency
        restUserAgencyMockMvc.perform(delete("/api/user-agencies/{id}", userAgency.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserAgency> userAgencyList = userAgencyRepository.findAll();
        assertThat(userAgencyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
