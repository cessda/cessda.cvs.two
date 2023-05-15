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
import eu.cessda.cvs.domain.VocabularyChange;
import eu.cessda.cvs.repository.VocabularyChangeRepository;
import eu.cessda.cvs.service.VocabularyChangeService;
import eu.cessda.cvs.service.dto.VocabularyChangeDTO;
import eu.cessda.cvs.service.mapper.VocabularyChangeMapper;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link VocabularyChangeResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class VocabularyChangeResourceIT {

    private static final Long DEFAULT_VOCABULARY_ID = 1L;
    private static final Long UPDATED_VOCABULARY_ID = 2L;

    private static final Long DEFAULT_VERSION_ID = 1L;
    private static final Long UPDATED_VERSION_ID = 2L;

    private static final String DEFAULT_CHANGE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CHANGE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private VocabularyChangeRepository vocabularyChangeRepository;

    @Autowired
    private VocabularyChangeMapper vocabularyChangeMapper;

    @Autowired
    private VocabularyChangeService vocabularyChangeService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVocabularyChangeMockMvc;

    private VocabularyChange vocabularyChange;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VocabularyChange createEntity(EntityManager em) {
        return new VocabularyChange()
            .vocabularyId(DEFAULT_VOCABULARY_ID)
            .versionId(DEFAULT_VERSION_ID)
            .changeType(DEFAULT_CHANGE_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .userId(DEFAULT_USER_ID)
            .userName(DEFAULT_USER_NAME)
            .date(DEFAULT_DATE);
    }
    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VocabularyChange createUpdatedEntity(EntityManager em) {
        return new VocabularyChange()
            .vocabularyId(UPDATED_VOCABULARY_ID)
            .versionId(UPDATED_VERSION_ID)
            .changeType(UPDATED_CHANGE_TYPE)
            .description(UPDATED_DESCRIPTION)
            .userId(UPDATED_USER_ID)
            .userName(UPDATED_USER_NAME)
            .date(UPDATED_DATE);
    }

    @BeforeEach
    public void initTest() {
        vocabularyChange = createEntity(em);
    }

    @Test
    @Transactional
    void createVocabularyChange() throws Exception {
        int databaseSizeBeforeCreate = vocabularyChangeRepository.findAll().size();

        // Create the VocabularyChange
        VocabularyChangeDTO vocabularyChangeDTO = vocabularyChangeMapper.toDto(vocabularyChange);
        restVocabularyChangeMockMvc.perform(post("/api/vocabulary-changes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyChangeDTO)))
            .andExpect(status().isCreated());

        // Validate the VocabularyChange in the database
        List<VocabularyChange> vocabularyChangeList = vocabularyChangeRepository.findAll();
        assertThat(vocabularyChangeList).hasSize(databaseSizeBeforeCreate + 1);
        VocabularyChange testVocabularyChange = vocabularyChangeList.get(vocabularyChangeList.size() - 1);
        assertThat(testVocabularyChange.getVocabularyId()).isEqualTo(DEFAULT_VOCABULARY_ID);
        assertThat(testVocabularyChange.getVersionId()).isEqualTo(DEFAULT_VERSION_ID);
        assertThat(testVocabularyChange.getChangeType()).isEqualTo(DEFAULT_CHANGE_TYPE);
        assertThat(testVocabularyChange.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testVocabularyChange.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testVocabularyChange.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testVocabularyChange.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createVocabularyChangeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vocabularyChangeRepository.findAll().size();

        // Create the VocabularyChange with an existing ID
        vocabularyChange.setId(1L);
        VocabularyChangeDTO vocabularyChangeDTO = vocabularyChangeMapper.toDto(vocabularyChange);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVocabularyChangeMockMvc.perform(post("/api/vocabulary-changes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyChangeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VocabularyChange in the database
        List<VocabularyChange> vocabularyChangeList = vocabularyChangeRepository.findAll();
        assertThat(vocabularyChangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVocabularyChanges() throws Exception {
        // Initialize the database
        vocabularyChangeRepository.saveAndFlush(vocabularyChange);

        // Get all the vocabularyChangeList
        restVocabularyChangeMockMvc.perform(get("/api/vocabulary-changes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vocabularyChange.getId().intValue())))
            .andExpect(jsonPath("$.[*].vocabularyId").value(hasItem(DEFAULT_VOCABULARY_ID.intValue())))
            .andExpect(jsonPath("$.[*].versionId").value(hasItem(DEFAULT_VERSION_ID.intValue())))
            .andExpect(jsonPath("$.[*].changeType").value(hasItem(DEFAULT_CHANGE_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getVocabularyChange() throws Exception {
        // Initialize the database
        vocabularyChangeRepository.saveAndFlush(vocabularyChange);

        // Get the vocabularyChange
        restVocabularyChangeMockMvc.perform(get("/api/vocabulary-changes/{id}", vocabularyChange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vocabularyChange.getId().intValue()))
            .andExpect(jsonPath("$.vocabularyId").value(DEFAULT_VOCABULARY_ID.intValue()))
            .andExpect(jsonPath("$.versionId").value(DEFAULT_VERSION_ID.intValue()))
            .andExpect(jsonPath("$.changeType").value(DEFAULT_CHANGE_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingVocabularyChange() throws Exception {
        // Get the vocabularyChange
        restVocabularyChangeMockMvc.perform(get("/api/vocabulary-changes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateVocabularyChange() throws Exception {
        // Initialize the database
        vocabularyChangeRepository.saveAndFlush(vocabularyChange);

        int databaseSizeBeforeUpdate = vocabularyChangeRepository.findAll().size();

        // Update the vocabularyChange
        VocabularyChange updatedVocabularyChange = vocabularyChangeRepository.findById(vocabularyChange.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVocabularyChange are not directly saved in db
        em.detach(updatedVocabularyChange);
        updatedVocabularyChange
            .vocabularyId(UPDATED_VOCABULARY_ID)
            .versionId(UPDATED_VERSION_ID)
            .changeType(UPDATED_CHANGE_TYPE)
            .description(UPDATED_DESCRIPTION)
            .userId(UPDATED_USER_ID)
            .userName(UPDATED_USER_NAME)
            .date(UPDATED_DATE);
        VocabularyChangeDTO vocabularyChangeDTO = vocabularyChangeMapper.toDto(updatedVocabularyChange);

        restVocabularyChangeMockMvc.perform(put("/api/vocabulary-changes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyChangeDTO)))
            .andExpect(status().isOk());

        // Validate the VocabularyChange in the database
        List<VocabularyChange> vocabularyChangeList = vocabularyChangeRepository.findAll();
        assertThat(vocabularyChangeList).hasSize(databaseSizeBeforeUpdate);
        VocabularyChange testVocabularyChange = vocabularyChangeList.get(vocabularyChangeList.size() - 1);
        assertThat(testVocabularyChange.getVocabularyId()).isEqualTo(UPDATED_VOCABULARY_ID);
        assertThat(testVocabularyChange.getVersionId()).isEqualTo(UPDATED_VERSION_ID);
        assertThat(testVocabularyChange.getChangeType()).isEqualTo(UPDATED_CHANGE_TYPE);
        assertThat(testVocabularyChange.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testVocabularyChange.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testVocabularyChange.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testVocabularyChange.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void updateNonExistingVocabularyChange() throws Exception {
        int databaseSizeBeforeUpdate = vocabularyChangeRepository.findAll().size();

        // Create the VocabularyChange
        VocabularyChangeDTO vocabularyChangeDTO = vocabularyChangeMapper.toDto(vocabularyChange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVocabularyChangeMockMvc.perform(put("/api/vocabulary-changes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(vocabularyChangeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VocabularyChange in the database
        List<VocabularyChange> vocabularyChangeList = vocabularyChangeRepository.findAll();
        assertThat(vocabularyChangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVocabularyChange() throws Exception {
        // Initialize the database
        vocabularyChangeRepository.saveAndFlush(vocabularyChange);

        int databaseSizeBeforeDelete = vocabularyChangeRepository.findAll().size();

        // Delete the vocabularyChange
        restVocabularyChangeMockMvc.perform(delete("/api/vocabulary-changes/{id}", vocabularyChange.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<VocabularyChange> vocabularyChangeList = vocabularyChangeRepository.findAll();
        assertThat(vocabularyChangeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
