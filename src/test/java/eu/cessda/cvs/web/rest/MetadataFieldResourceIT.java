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
import eu.cessda.cvs.domain.MetadataField;
import eu.cessda.cvs.domain.enumeration.ObjectType;
import eu.cessda.cvs.repository.MetadataFieldRepository;
import eu.cessda.cvs.service.MetadataFieldService;
import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import eu.cessda.cvs.service.mapper.MetadataFieldMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@link MetadataFieldResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class MetadataFieldResourceIT {

    private static final String DEFAULT_METADATA_KEY = "AAAAAAAAAA";
    private static final String UPDATED_METADATA_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ObjectType DEFAULT_OBJECT_TYPE = ObjectType.AGENCY;
    private static final ObjectType UPDATED_OBJECT_TYPE = ObjectType.GROUP;

    @Autowired
    private MetadataFieldRepository metadataFieldRepository;

    @Autowired
    private MetadataFieldMapper metadataFieldMapper;

    @Autowired
    private MetadataFieldService metadataFieldService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMetadataFieldMockMvc;

    private MetadataField metadataField;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetadataField createEntity(EntityManager em) {
        return new MetadataField()
            .metadataKey(DEFAULT_METADATA_KEY)
            .description(DEFAULT_DESCRIPTION)
            .objectType(DEFAULT_OBJECT_TYPE);
    }
    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetadataField createUpdatedEntity(EntityManager em) {
        return new MetadataField()
            .metadataKey(UPDATED_METADATA_KEY)
            .description(UPDATED_DESCRIPTION)
            .objectType(UPDATED_OBJECT_TYPE);
    }

    @BeforeEach
    public void initTest() {
        metadataField = createEntity(em);
    }

    @Test
    @Transactional
    void createMetadataField() throws Exception {
        int databaseSizeBeforeCreate = metadataFieldRepository.findAll().size();

        // Create the MetadataField
        MetadataFieldDTO metadataFieldDTO = metadataFieldMapper.toDto(metadataField);
        restMetadataFieldMockMvc.perform(post("/api/metadata-fields")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataFieldDTO)))
            .andExpect(status().isCreated());

        // Validate the MetadataField in the database
        List<MetadataField> metadataFieldList = metadataFieldRepository.findAll();
        assertThat(metadataFieldList).hasSize(databaseSizeBeforeCreate + 1);
        MetadataField testMetadataField = metadataFieldList.get(metadataFieldList.size() - 1);
        assertThat(testMetadataField.getMetadataKey()).isEqualTo(DEFAULT_METADATA_KEY);
        assertThat(testMetadataField.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMetadataField.getObjectType()).isEqualTo(DEFAULT_OBJECT_TYPE);
    }

    @Test
    @Transactional
    void createMetadataFieldWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = metadataFieldRepository.findAll().size();

        // Create the MetadataField with an existing ID
        metadataField.setId(1L);
        MetadataFieldDTO metadataFieldDTO = metadataFieldMapper.toDto(metadataField);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMetadataFieldMockMvc.perform(post("/api/metadata-fields")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataFieldDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MetadataField in the database
        List<MetadataField> metadataFieldList = metadataFieldRepository.findAll();
        assertThat(metadataFieldList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void checkMetadataKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = metadataFieldRepository.findAll().size();
        // set the field null
        metadataField.setMetadataKey(null);

        // Create the MetadataField, which fails.
        MetadataFieldDTO metadataFieldDTO = metadataFieldMapper.toDto(metadataField);

        restMetadataFieldMockMvc.perform(post("/api/metadata-fields")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataFieldDTO)))
            .andExpect(status().isBadRequest());

        List<MetadataField> metadataFieldList = metadataFieldRepository.findAll();
        assertThat(metadataFieldList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMetadataFields() throws Exception {
        // Initialize the database
        metadataFieldRepository.saveAndFlush(metadataField);

        // Get all the metadataFieldList
        restMetadataFieldMockMvc.perform(get("/api/metadata-fields?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metadataField.getId().intValue())))
            .andExpect(jsonPath("$.[*].metadataKey").value(hasItem(DEFAULT_METADATA_KEY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem( DEFAULT_DESCRIPTION )))
            .andExpect(jsonPath("$.[*].objectType").value(hasItem(DEFAULT_OBJECT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getMetadataField() throws Exception {
        // Initialize the database
        metadataFieldRepository.saveAndFlush(metadataField);

        // Get the metadataField
        restMetadataFieldMockMvc.perform(get("/api/metadata-fields/{id}", metadataField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metadataField.getId().intValue()))
            .andExpect(jsonPath("$.metadataKey").value(DEFAULT_METADATA_KEY))
            .andExpect(jsonPath("$.description").value( DEFAULT_DESCRIPTION ))
            .andExpect(jsonPath("$.objectType").value(DEFAULT_OBJECT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getMetadataFieldByMetadataKey() throws Exception
    {
        // Initialize the database
        metadataFieldRepository.saveAndFlush(metadataField);

        // Get the metadataField
        restMetadataFieldMockMvc.perform( get("/api/metadata-fields/metadata-key/{metadataKey}", metadataField.getMetadataKey()) )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metadataField.getId().intValue()))
            .andExpect(jsonPath("$.metadataKey").value(DEFAULT_METADATA_KEY))
            .andExpect(jsonPath("$.description").value( DEFAULT_DESCRIPTION ))
            .andExpect(jsonPath("$.objectType").value(DEFAULT_OBJECT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getMetadataFieldAsPDF() throws Exception
    {
        // Initialize the database
        metadataFieldRepository.saveAndFlush(metadataField);

        // Get the metadataField
        restMetadataFieldMockMvc.perform( get("/api/metadata-fields/download-pdf/{metadataKey}", metadataField.getMetadataKey()) )
            .andExpect(status().isOk())
            .andExpect( content().contentType( MediaType.APPLICATION_PDF ) )
            .andExpect( header().string( HttpHeaders.CONTENT_DISPOSITION, equalTo( "attachment; filename=document.pdf" ) ) );
    }

    @Test
    @Transactional
    void getMetadataFieldAsDOCX() throws Exception
    {
        // Initialize the database
        metadataFieldRepository.saveAndFlush(metadataField);

        // Get the metadataField
        restMetadataFieldMockMvc.perform( get("/api/metadata-fields/download-word/{metadataKey}", metadataField.getMetadataKey()) )
            .andExpect(status().isOk())
            .andExpect( content().contentType( new MediaType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document" ) ) )
            .andExpect( header().string( HttpHeaders.CONTENT_DISPOSITION, equalTo( "attachment; filename=document.docx" ) ) );
    }

    @Test
    @Transactional
    void getNonExistingMetadataKey() throws Exception {
        // Get the metadataField
        restMetadataFieldMockMvc.perform(get("/api/metadata-fields/metadata-key/{metadataKey}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getNonExistingMetadataField() throws Exception {
        // Get the metadataField
        restMetadataFieldMockMvc.perform(get("/api/metadata-fields/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateMetadataField() throws Exception {
        // Initialize the database
        metadataFieldRepository.saveAndFlush(metadataField);

        int databaseSizeBeforeUpdate = metadataFieldRepository.findAll().size();

        // Update the metadataField
        MetadataField updatedMetadataField = metadataFieldRepository.findById(metadataField.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMetadataField are not directly saved in db
        em.detach(updatedMetadataField);
        updatedMetadataField
            .metadataKey(UPDATED_METADATA_KEY)
            .description(UPDATED_DESCRIPTION)
            .objectType(UPDATED_OBJECT_TYPE);
        MetadataFieldDTO metadataFieldDTO = metadataFieldMapper.toDto(updatedMetadataField);

        restMetadataFieldMockMvc.perform(put("/api/metadata-fields")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataFieldDTO)))
            .andExpect(status().isOk());

        // Validate the MetadataField in the database
        List<MetadataField> metadataFieldList = metadataFieldRepository.findAll();
        assertThat(metadataFieldList).hasSize(databaseSizeBeforeUpdate);
        MetadataField testMetadataField = metadataFieldList.get(metadataFieldList.size() - 1);
        assertThat(testMetadataField.getMetadataKey()).isEqualTo(UPDATED_METADATA_KEY);
        assertThat(testMetadataField.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMetadataField.getObjectType()).isEqualTo(UPDATED_OBJECT_TYPE);
    }

    @Test
    @Transactional
    void updateNonExistingMetadataField() throws Exception {
        int databaseSizeBeforeUpdate = metadataFieldRepository.findAll().size();

        // Create the MetadataField
        MetadataFieldDTO metadataFieldDTO = metadataFieldMapper.toDto(metadataField);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMetadataFieldMockMvc.perform(put("/api/metadata-fields")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataFieldDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MetadataField in the database
        List<MetadataField> metadataFieldList = metadataFieldRepository.findAll();
        assertThat(metadataFieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMetadataField() throws Exception {
        // Initialize the database
        metadataFieldRepository.saveAndFlush(metadataField);

        int databaseSizeBeforeDelete = metadataFieldRepository.findAll().size();

        // Delete the metadataField
        restMetadataFieldMockMvc.perform(delete("/api/metadata-fields/{id}", metadataField.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MetadataField> metadataFieldList = metadataFieldRepository.findAll();
        assertThat(metadataFieldList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
