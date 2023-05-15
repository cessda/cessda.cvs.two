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
import eu.cessda.cvs.domain.MetadataValue;
import eu.cessda.cvs.domain.enumeration.ObjectType;
import eu.cessda.cvs.repository.MetadataValueRepository;
import eu.cessda.cvs.service.MetadataValueService;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.service.mapper.MetadataValueMapper;
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
 * Integration tests for the {@link MetadataValueResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class MetadataValueResourceIT {

    public static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    public static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    public static final String DEFAULT_VALUE = "AAAAAAAAAA";
    public static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final ObjectType DEFAULT_OBJECT_TYPE = ObjectType.AGENCY;
    private static final ObjectType UPDATED_OBJECT_TYPE = ObjectType.GROUP;

    private static final Long DEFAULT_OBJECT_ID = 1L;
    private static final Long UPDATED_OBJECT_ID = 2L;

    private static final Integer DEFAULT_POSITION = 1;
    private static final Integer UPDATED_POSITION = 2;

    @Autowired
    private MetadataValueRepository metadataValueRepository;

    @Autowired
    private MetadataValueMapper metadataValueMapper;

    @Autowired
    private MetadataValueService metadataValueService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMetadataValueMockMvc;

    private MetadataValue metadataValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetadataValue createEntity() {
        return new MetadataValue()
            .identifier(DEFAULT_IDENTIFIER)
            .position(DEFAULT_POSITION)
            .value(DEFAULT_VALUE)
            .objectType(DEFAULT_OBJECT_TYPE)
            .objectId(DEFAULT_OBJECT_ID);
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetadataValue createUpdatedEntity(EntityManager em) {
        return new MetadataValue()
            .identifier(UPDATED_IDENTIFIER)
            .position(UPDATED_POSITION)
            .value(UPDATED_VALUE)
            .objectType(UPDATED_OBJECT_TYPE)
            .objectId(UPDATED_OBJECT_ID);
    }

    @BeforeEach
    public void initTest() {
        metadataValue = createEntity();
    }

    @Test
    @Transactional
    void createMetadataValue() throws Exception {
        int databaseSizeBeforeCreate = metadataValueRepository.findAll().size();

        // Create the MetadataValue
        MetadataValueDTO metadataValueDTO = metadataValueMapper.toDto(metadataValue);
        restMetadataValueMockMvc.perform(post("/api/metadata-values")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataValueDTO)))
            .andExpect(status().isCreated());

        // Validate the MetadataValue in the database
        List<MetadataValue> metadataValueList = metadataValueRepository.findAll();
        assertThat(metadataValueList).hasSize(databaseSizeBeforeCreate + 1);
        MetadataValue testMetadataValue = metadataValueList.get(metadataValueList.size() - 1);
        assertThat(testMetadataValue.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testMetadataValue.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testMetadataValue.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testMetadataValue.getObjectType()).isEqualTo(DEFAULT_OBJECT_TYPE);
        assertThat(testMetadataValue.getObjectId()).isEqualTo(DEFAULT_OBJECT_ID);
    }

    @Test
    @Transactional
    void createMetadataValueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = metadataValueRepository.findAll().size();

        // Create the MetadataValue with an existing ID
        metadataValue.setId(1L);
        MetadataValueDTO metadataValueDTO = metadataValueMapper.toDto(metadataValue);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMetadataValueMockMvc.perform(post("/api/metadata-values")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MetadataValue in the database
        List<MetadataValue> metadataValueList = metadataValueRepository.findAll();
        assertThat(metadataValueList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void getAllMetadataValues() throws Exception {
        // Initialize the database
        metadataValueRepository.saveAndFlush(metadataValue);

        // Get all the metadataValueList
        restMetadataValueMockMvc.perform(get("/api/metadata-values?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metadataValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].position").value(hasItem( DEFAULT_POSITION )))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].objectType").value(hasItem(DEFAULT_OBJECT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));
    }

    @Test
    @Transactional
    void getMetadataValue() throws Exception {
        // Initialize the database
        metadataValueRepository.saveAndFlush(metadataValue);

        // Get the metadataValue
        restMetadataValueMockMvc.perform(get("/api/metadata-values/{id}", metadataValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metadataValue.getId().intValue()))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER))
            .andExpect(jsonPath("$.position").value( DEFAULT_POSITION ))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.objectType").value(DEFAULT_OBJECT_TYPE.toString()))
            .andExpect(jsonPath("$.objectId").value(DEFAULT_OBJECT_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingMetadataValue() throws Exception {
        // Get the metadataValue
        restMetadataValueMockMvc.perform(get("/api/metadata-values/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateMetadataValue() throws Exception {
        // Initialize the database
        metadataValueRepository.saveAndFlush(metadataValue);

        int databaseSizeBeforeUpdate = metadataValueRepository.findAll().size();

        // Update the metadataValue
        MetadataValue updatedMetadataValue = metadataValueRepository.findById(metadataValue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMetadataValue are not directly saved in db
        em.detach(updatedMetadataValue);
        updatedMetadataValue
            .identifier(UPDATED_IDENTIFIER)
            .value(UPDATED_VALUE)
            .objectType(UPDATED_OBJECT_TYPE)
            .objectId(UPDATED_OBJECT_ID)
            .position(UPDATED_POSITION);
        MetadataValueDTO metadataValueDTO = metadataValueMapper.toDto(updatedMetadataValue);

        restMetadataValueMockMvc.perform(put("/api/metadata-values")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataValueDTO)))
            .andExpect(status().isOk());

        // Validate the MetadataValue in the database
        List<MetadataValue> metadataValueList = metadataValueRepository.findAll();
        assertThat(metadataValueList).hasSize(databaseSizeBeforeUpdate);
        MetadataValue testMetadataValue = metadataValueList.get(metadataValueList.size() - 1);
        assertThat(testMetadataValue.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testMetadataValue.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testMetadataValue.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testMetadataValue.getObjectType()).isEqualTo(UPDATED_OBJECT_TYPE);
        assertThat(testMetadataValue.getObjectId()).isEqualTo(UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void updateNonExistingMetadataValue() throws Exception {
        int databaseSizeBeforeUpdate = metadataValueRepository.findAll().size();

        // Create the MetadataValue
        MetadataValueDTO metadataValueDTO = metadataValueMapper.toDto(metadataValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetadataValueMockMvc.perform(put("/api/metadata-values")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MetadataValue in the database
        List<MetadataValue> metadataValueList = metadataValueRepository.findAll();
        assertThat(metadataValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMetadataValue() throws Exception {
        // Initialize the database
        metadataValueRepository.saveAndFlush(metadataValue);

        int databaseSizeBeforeDelete = metadataValueRepository.findAll().size();

        // Delete the metadataValue
        restMetadataValueMockMvc.perform(delete("/api/metadata-values/{id}", metadataValue.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MetadataValue> metadataValueList = metadataValueRepository.findAll();
        assertThat(metadataValueList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
