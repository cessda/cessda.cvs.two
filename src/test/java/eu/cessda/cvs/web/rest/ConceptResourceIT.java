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
import eu.cessda.cvs.domain.Concept;
import eu.cessda.cvs.repository.ConceptRepository;
import eu.cessda.cvs.service.ConceptService;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.mapper.ConceptMapper;
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
 * Integration tests for the {@link ConceptResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ConceptResourceIT {

    private static final String DEFAULT_URI = "AAAAAAAAAA";
    private static final String UPDATED_URI = "BBBBBBBBBB";

    private static final String DEFAULT_NOTATION = "AAAAAAAAAA";
    private static final String UPDATED_NOTATION = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DEFINITION = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION = "BBBBBBBBBB";

    private static final Long DEFAULT_PREVIOUS_CONCEPT = 1L;
    private static final Long UPDATED_PREVIOUS_CONCEPT = 2L;

    private static final Long DEFAULT_SL_CONCEPT = 1L;
    private static final Long UPDATED_SL_CONCEPT = 2L;

    private static final String DEFAULT_PARENT = "AAAAAAAAAA";
    private static final String UPDATED_PARENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_POSITION = 1;
    private static final Integer UPDATED_POSITION = 2;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private ConceptMapper conceptMapper;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConceptMockMvc;

    private Concept concept;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Concept createEntity(EntityManager em) {
        Concept concept = new Concept()
            .uri(DEFAULT_URI)
            .notation(DEFAULT_NOTATION)
            .title(DEFAULT_TITLE)
            .definition(DEFAULT_DEFINITION)
            .previousConcept(DEFAULT_PREVIOUS_CONCEPT)
            .slConcept(DEFAULT_SL_CONCEPT)
            .parent(DEFAULT_PARENT)
            .position(DEFAULT_POSITION);
        return concept;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Concept createUpdatedEntity(EntityManager em) {
        Concept concept = new Concept()
            .uri(UPDATED_URI)
            .notation(UPDATED_NOTATION)
            .title(UPDATED_TITLE)
            .definition(UPDATED_DEFINITION)
            .previousConcept(UPDATED_PREVIOUS_CONCEPT)
            .slConcept(UPDATED_SL_CONCEPT)
            .parent(UPDATED_PARENT)
            .position(UPDATED_POSITION);
        return concept;
    }

    @BeforeEach
    public void initTest() {
        concept = createEntity(em);
    }

    @Test
    @Transactional
    public void createConcept() throws Exception {
        int databaseSizeBeforeCreate = conceptRepository.findAll().size();

        // Create the Concept
        ConceptDTO conceptDTO = conceptMapper.toDto(concept);
        restConceptMockMvc.perform(post("/api/concepts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(conceptDTO)))
            .andExpect(status().isCreated());

        // Validate the Concept in the database
        List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(databaseSizeBeforeCreate + 1);
        Concept testConcept = conceptList.get(conceptList.size() - 1);
        assertThat(testConcept.getUri()).isEqualTo(DEFAULT_URI);
        assertThat(testConcept.getNotation()).isEqualTo(DEFAULT_NOTATION);
        assertThat(testConcept.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testConcept.getDefinition()).isEqualTo(DEFAULT_DEFINITION);
        assertThat(testConcept.getPreviousConcept()).isEqualTo(DEFAULT_PREVIOUS_CONCEPT);
        assertThat(testConcept.getSlConcept()).isEqualTo(DEFAULT_SL_CONCEPT);
        assertThat(testConcept.getParent()).isEqualTo(DEFAULT_PARENT);
        assertThat(testConcept.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void createConceptWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = conceptRepository.findAll().size();

        // Create the Concept with an existing ID
        concept.setId(1L);
        ConceptDTO conceptDTO = conceptMapper.toDto(concept);

        // An entity with an existing ID cannot be created, so this API call must fail
        restConceptMockMvc.perform(post("/api/concepts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(conceptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Concept in the database
        List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllConcepts() throws Exception {
        // Initialize the database
        conceptRepository.saveAndFlush(concept);

        // Get all the conceptList
        restConceptMockMvc.perform(get("/api/concepts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(concept.getId().intValue())))
            .andExpect(jsonPath("$.[*].uri").value(hasItem(DEFAULT_URI)))
            .andExpect(jsonPath("$.[*].notation").value(hasItem(DEFAULT_NOTATION)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].definition").value(hasItem(DEFAULT_DEFINITION.toString())))
            .andExpect(jsonPath("$.[*].previousConcept").value(hasItem(DEFAULT_PREVIOUS_CONCEPT.intValue())))
            .andExpect(jsonPath("$.[*].slConcept").value(hasItem(DEFAULT_SL_CONCEPT.intValue())))
            .andExpect(jsonPath("$.[*].parent").value(hasItem(DEFAULT_PARENT)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)));
    }

    @Test
    @Transactional
    public void getConcept() throws Exception {
        // Initialize the database
        conceptRepository.saveAndFlush(concept);

        // Get the concept
        restConceptMockMvc.perform(get("/api/concepts/{id}", concept.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(concept.getId().intValue()))
            .andExpect(jsonPath("$.uri").value(DEFAULT_URI))
            .andExpect(jsonPath("$.notation").value(DEFAULT_NOTATION))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.definition").value(DEFAULT_DEFINITION.toString()))
            .andExpect(jsonPath("$.previousConcept").value(DEFAULT_PREVIOUS_CONCEPT.intValue()))
            .andExpect(jsonPath("$.slConcept").value(DEFAULT_SL_CONCEPT.intValue()))
            .andExpect(jsonPath("$.parent").value(DEFAULT_PARENT))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION));
    }

    @Test
    @Transactional
    public void getNonExistingConcept() throws Exception {
        // Get the concept
        restConceptMockMvc.perform(get("/api/concepts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConcept() throws Exception {
        // Initialize the database
        conceptRepository.saveAndFlush(concept);

        int databaseSizeBeforeUpdate = conceptRepository.findAll().size();

        // Update the concept
        Concept updatedConcept = conceptRepository.findById(concept.getId()).get();
        // Disconnect from session so that the updates on updatedConcept are not directly saved in db
        em.detach(updatedConcept);
        updatedConcept
            .uri(UPDATED_URI)
            .notation(UPDATED_NOTATION)
            .title(UPDATED_TITLE)
            .definition(UPDATED_DEFINITION)
            .previousConcept(UPDATED_PREVIOUS_CONCEPT)
            .slConcept(UPDATED_SL_CONCEPT)
            .parent(UPDATED_PARENT)
            .position(UPDATED_POSITION);
        ConceptDTO conceptDTO = conceptMapper.toDto(updatedConcept);

        restConceptMockMvc.perform(put("/api/concepts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(conceptDTO)))
            .andExpect(status().isOk());

        // Validate the Concept in the database
        List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(databaseSizeBeforeUpdate);
        Concept testConcept = conceptList.get(conceptList.size() - 1);
        assertThat(testConcept.getUri()).isEqualTo(UPDATED_URI);
        assertThat(testConcept.getNotation()).isEqualTo(UPDATED_NOTATION);
        assertThat(testConcept.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testConcept.getDefinition()).isEqualTo(UPDATED_DEFINITION);
        assertThat(testConcept.getPreviousConcept()).isEqualTo(UPDATED_PREVIOUS_CONCEPT);
        assertThat(testConcept.getSlConcept()).isEqualTo(UPDATED_SL_CONCEPT);
        assertThat(testConcept.getParent()).isEqualTo(UPDATED_PARENT);
        assertThat(testConcept.getPosition()).isEqualTo(UPDATED_POSITION);
    }

    @Test
    @Transactional
    public void updateNonExistingConcept() throws Exception {
        int databaseSizeBeforeUpdate = conceptRepository.findAll().size();

        // Create the Concept
        ConceptDTO conceptDTO = conceptMapper.toDto(concept);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConceptMockMvc.perform(put("/api/concepts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(conceptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Concept in the database
        List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteConcept() throws Exception {
        // Initialize the database
        conceptRepository.saveAndFlush(concept);

        int databaseSizeBeforeDelete = conceptRepository.findAll().size();

        // Delete the concept
        restConceptMockMvc.perform(delete("/api/concepts/{id}", concept.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Concept> conceptList = conceptRepository.findAll();
        assertThat(conceptList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
