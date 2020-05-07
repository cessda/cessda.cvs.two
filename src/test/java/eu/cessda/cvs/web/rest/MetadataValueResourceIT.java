package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import eu.cessda.cvs.domain.MetadataValue;
import eu.cessda.cvs.repository.MetadataValueRepository;
import eu.cessda.cvs.service.MetadataValueService;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.service.mapper.MetadataValueMapper;

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
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.cessda.cvs.domain.enumeration.ObjectType;
/**
 * Integration tests for the {@link MetadataValueResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class MetadataValueResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final ObjectType DEFAULT_OBJECT_TYPE = ObjectType.AGENCY;
    private static final ObjectType UPDATED_OBJECT_TYPE = ObjectType.GROUP;

    private static final Long DEFAULT_OBJECT_ID = 1L;
    private static final Long UPDATED_OBJECT_ID = 2L;

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
    public static MetadataValue createEntity(EntityManager em) {
        MetadataValue metadataValue = new MetadataValue()
            .value(DEFAULT_VALUE)
            .objectType(DEFAULT_OBJECT_TYPE)
            .objectId(DEFAULT_OBJECT_ID);
        return metadataValue;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetadataValue createUpdatedEntity(EntityManager em) {
        MetadataValue metadataValue = new MetadataValue()
            .value(UPDATED_VALUE)
            .objectType(UPDATED_OBJECT_TYPE)
            .objectId(UPDATED_OBJECT_ID);
        return metadataValue;
    }

    @BeforeEach
    public void initTest() {
        metadataValue = createEntity(em);
    }

    @Test
    @Transactional
    public void createMetadataValue() throws Exception {
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
        assertThat(testMetadataValue.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testMetadataValue.getObjectType()).isEqualTo(DEFAULT_OBJECT_TYPE);
        assertThat(testMetadataValue.getObjectId()).isEqualTo(DEFAULT_OBJECT_ID);
    }

    @Test
    @Transactional
    public void createMetadataValueWithExistingId() throws Exception {
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
    public void getAllMetadataValues() throws Exception {
        // Initialize the database
        metadataValueRepository.saveAndFlush(metadataValue);

        // Get all the metadataValueList
        restMetadataValueMockMvc.perform(get("/api/metadata-values?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metadataValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].objectType").value(hasItem(DEFAULT_OBJECT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));
    }

    @Test
    @Transactional
    public void getMetadataValue() throws Exception {
        // Initialize the database
        metadataValueRepository.saveAndFlush(metadataValue);

        // Get the metadataValue
        restMetadataValueMockMvc.perform(get("/api/metadata-values/{id}", metadataValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metadataValue.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.objectType").value(DEFAULT_OBJECT_TYPE.toString()))
            .andExpect(jsonPath("$.objectId").value(DEFAULT_OBJECT_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMetadataValue() throws Exception {
        // Get the metadataValue
        restMetadataValueMockMvc.perform(get("/api/metadata-values/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMetadataValue() throws Exception {
        // Initialize the database
        metadataValueRepository.saveAndFlush(metadataValue);

        int databaseSizeBeforeUpdate = metadataValueRepository.findAll().size();

        // Update the metadataValue
        MetadataValue updatedMetadataValue = metadataValueRepository.findById(metadataValue.getId()).get();
        // Disconnect from session so that the updates on updatedMetadataValue are not directly saved in db
        em.detach(updatedMetadataValue);
        updatedMetadataValue
            .value(UPDATED_VALUE)
            .objectType(UPDATED_OBJECT_TYPE)
            .objectId(UPDATED_OBJECT_ID);
        MetadataValueDTO metadataValueDTO = metadataValueMapper.toDto(updatedMetadataValue);

        restMetadataValueMockMvc.perform(put("/api/metadata-values")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(metadataValueDTO)))
            .andExpect(status().isOk());

        // Validate the MetadataValue in the database
        List<MetadataValue> metadataValueList = metadataValueRepository.findAll();
        assertThat(metadataValueList).hasSize(databaseSizeBeforeUpdate);
        MetadataValue testMetadataValue = metadataValueList.get(metadataValueList.size() - 1);
        assertThat(testMetadataValue.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testMetadataValue.getObjectType()).isEqualTo(UPDATED_OBJECT_TYPE);
        assertThat(testMetadataValue.getObjectId()).isEqualTo(UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingMetadataValue() throws Exception {
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
    public void deleteMetadataValue() throws Exception {
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
