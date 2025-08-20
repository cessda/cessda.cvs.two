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
import eu.cessda.cvs.service.ExportService;
import eu.cessda.cvs.service.MetadataFieldService;
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

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        restMetadataFieldMockMvc.perform( get("/api/metadata-fields/download/{metadataKey}", metadataField.getMetadataKey())
                .accept( MediaType.APPLICATION_PDF ))
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
        restMetadataFieldMockMvc.perform( get( "/api/metadata-fields/download/{metadataKey}", metadataField.getMetadataKey() )
                .accept( ExportService.MEDIATYPE_WORD ) )
            .andExpect(status().isOk())
            .andExpect( content().contentType( ExportService.MEDIATYPE_WORD ) )
            .andExpect( header().string( HttpHeaders.CONTENT_DISPOSITION, equalTo( "attachment; filename=document.docx" ) ) );
    }

    @Test
    @Transactional
    void getNonExistingMetadataKey() throws Exception {
        // Get the metadataField
        restMetadataFieldMockMvc.perform(get("/api/metadata-fields/metadata-key/{metadataKey}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
}
