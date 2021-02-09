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
import eu.cessda.cvs.domain.Resolver;
import eu.cessda.cvs.repository.ResolverRepository;
import eu.cessda.cvs.service.ResolverService;
import eu.cessda.cvs.service.dto.ResolverDTO;
import eu.cessda.cvs.service.mapper.ResolverMapper;

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

import eu.cessda.cvs.domain.enumeration.ResourceType;
import eu.cessda.cvs.domain.enumeration.ResolverType;
/**
 * Integration tests for the {@link ResolverResource} REST controller.
 */
@SpringBootTest(classes = CvsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ResolverResourceIT {

    private static final String DEFAULT_RESOURCE_ID = "AAAAAAAAAA";
    private static final String UPDATED_RESOURCE_ID = "BBBBBBBBBB";

    private static final ResourceType DEFAULT_RESOURCE_TYPE = ResourceType.VOCABULARY;
    private static final ResourceType UPDATED_RESOURCE_TYPE = ResourceType.VOCABULARY;

    private static final String DEFAULT_RESOURCE_URL = "AAAAAAAAAA";
    private static final String UPDATED_RESOURCE_URL = "BBBBBBBBBB";

    private static final ResolverType DEFAULT_RESOLVER_TYPE = ResolverType.DOI;
    private static final ResolverType UPDATED_RESOLVER_TYPE = ResolverType.URN;

    private static final String DEFAULT_RESOLVER_URI = "AAAAAAAAAA";
    private static final String UPDATED_RESOLVER_URI = "BBBBBBBBBB";

    @Autowired
    private ResolverRepository resolverRepository;

    @Autowired
    private ResolverMapper resolverMapper;

    @Autowired
    private ResolverService resolverService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResolverMockMvc;

    private Resolver resolver;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resolver createEntity(EntityManager em) {
        Resolver resolver = new Resolver()
            .resourceId(DEFAULT_RESOURCE_ID)
            .resourceType(DEFAULT_RESOURCE_TYPE)
            .resourceUrl(DEFAULT_RESOURCE_URL)
            .resolverType(DEFAULT_RESOLVER_TYPE)
            .resolverURI(DEFAULT_RESOLVER_URI);
        return resolver;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resolver createUpdatedEntity(EntityManager em) {
        Resolver resolver = new Resolver()
            .resourceId(UPDATED_RESOURCE_ID)
            .resourceType(UPDATED_RESOURCE_TYPE)
            .resourceUrl(UPDATED_RESOURCE_URL)
            .resolverType(UPDATED_RESOLVER_TYPE)
            .resolverURI(UPDATED_RESOLVER_URI);
        return resolver;
    }

    @BeforeEach
    public void initTest() {
        resolver = createEntity(em);
    }

    @Test
    @Transactional
    public void createResolver() throws Exception {
        int databaseSizeBeforeCreate = resolverRepository.findAll().size();

        // Create the Resolver
        ResolverDTO resolverDTO = resolverMapper.toDto(resolver);
        restResolverMockMvc.perform(post("/api/resolvers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(resolverDTO)))
            .andExpect(status().isCreated());

        // Validate the Resolver in the database
        List<Resolver> resolverList = resolverRepository.findAll();
        assertThat(resolverList).hasSize(databaseSizeBeforeCreate + 1);
        Resolver testResolver = resolverList.get(resolverList.size() - 1);
        assertThat(testResolver.getResourceId()).isEqualTo(DEFAULT_RESOURCE_ID);
        assertThat(testResolver.getResourceType()).isEqualTo(DEFAULT_RESOURCE_TYPE);
        assertThat(testResolver.getResourceUrl()).isEqualTo(DEFAULT_RESOURCE_URL);
        assertThat(testResolver.getResolverType()).isEqualTo(DEFAULT_RESOLVER_TYPE);
        assertThat(testResolver.getResolverURI()).isEqualTo(DEFAULT_RESOLVER_URI);
    }

    @Test
    @Transactional
    public void createResolverWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = resolverRepository.findAll().size();

        // Create the Resolver with an existing ID
        resolver.setId(1L);
        ResolverDTO resolverDTO = resolverMapper.toDto(resolver);

        // An entity with an existing ID cannot be created, so this API call must fail
        restResolverMockMvc.perform(post("/api/resolvers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(resolverDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Resolver in the database
        List<Resolver> resolverList = resolverRepository.findAll();
        assertThat(resolverList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkResourceUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = resolverRepository.findAll().size();
        // set the field null
        resolver.setResourceUrl(null);

        // Create the Resolver, which fails.
        ResolverDTO resolverDTO = resolverMapper.toDto(resolver);

        restResolverMockMvc.perform(post("/api/resolvers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(resolverDTO)))
            .andExpect(status().isBadRequest());

        List<Resolver> resolverList = resolverRepository.findAll();
        assertThat(resolverList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkResolverURIIsRequired() throws Exception {
        int databaseSizeBeforeTest = resolverRepository.findAll().size();
        // set the field null
        resolver.setResolverURI(null);

        // Create the Resolver, which fails.
        ResolverDTO resolverDTO = resolverMapper.toDto(resolver);

        restResolverMockMvc.perform(post("/api/resolvers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(resolverDTO)))
            .andExpect(status().isBadRequest());

        List<Resolver> resolverList = resolverRepository.findAll();
        assertThat(resolverList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllResolvers() throws Exception {
        // Initialize the database
        resolverRepository.saveAndFlush(resolver);

        // Get all the resolverList
        restResolverMockMvc.perform(get("/api/resolvers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resolver.getId().intValue())))
            .andExpect(jsonPath("$.[*].resourceId").value(hasItem(DEFAULT_RESOURCE_ID)))
            .andExpect(jsonPath("$.[*].resourceType").value(hasItem(DEFAULT_RESOURCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].resourceUrl").value(hasItem(DEFAULT_RESOURCE_URL)))
            .andExpect(jsonPath("$.[*].resolverType").value(hasItem(DEFAULT_RESOLVER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].resolverURI").value(hasItem(DEFAULT_RESOLVER_URI)));
    }

    @Test
    @Transactional
    public void getResolver() throws Exception {
        // Initialize the database
        resolverRepository.saveAndFlush(resolver);

        // Get the resolver
        restResolverMockMvc.perform(get("/api/resolvers/{id}", resolver.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resolver.getId().intValue()))
            .andExpect(jsonPath("$.resourceId").value(DEFAULT_RESOURCE_ID))
            .andExpect(jsonPath("$.resourceType").value(DEFAULT_RESOURCE_TYPE.toString()))
            .andExpect(jsonPath("$.resourceUrl").value(DEFAULT_RESOURCE_URL))
            .andExpect(jsonPath("$.resolverType").value(DEFAULT_RESOLVER_TYPE.toString()))
            .andExpect(jsonPath("$.resolverURI").value(DEFAULT_RESOLVER_URI));
    }

    @Test
    @Transactional
    public void getNonExistingResolver() throws Exception {
        // Get the resolver
        restResolverMockMvc.perform(get("/api/resolvers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResolver() throws Exception {
        // Initialize the database
        resolverRepository.saveAndFlush(resolver);

        int databaseSizeBeforeUpdate = resolverRepository.findAll().size();

        // Update the resolver
        Resolver updatedResolver = resolverRepository.findById(resolver.getId()).get();
        // Disconnect from session so that the updates on updatedResolver are not directly saved in db
        em.detach(updatedResolver);
        updatedResolver
            .resourceId(UPDATED_RESOURCE_ID)
            .resourceType(UPDATED_RESOURCE_TYPE)
            .resourceUrl(UPDATED_RESOURCE_URL)
            .resolverType(UPDATED_RESOLVER_TYPE)
            .resolverURI(UPDATED_RESOLVER_URI);
        ResolverDTO resolverDTO = resolverMapper.toDto(updatedResolver);

        restResolverMockMvc.perform(put("/api/resolvers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(resolverDTO)))
            .andExpect(status().isOk());

        // Validate the Resolver in the database
        List<Resolver> resolverList = resolverRepository.findAll();
        assertThat(resolverList).hasSize(databaseSizeBeforeUpdate);
        Resolver testResolver = resolverList.get(resolverList.size() - 1);
        assertThat(testResolver.getResourceId()).isEqualTo(UPDATED_RESOURCE_ID);
        assertThat(testResolver.getResourceType()).isEqualTo(UPDATED_RESOURCE_TYPE);
        assertThat(testResolver.getResourceUrl()).isEqualTo(UPDATED_RESOURCE_URL);
        assertThat(testResolver.getResolverType()).isEqualTo(UPDATED_RESOLVER_TYPE);
        assertThat(testResolver.getResolverURI()).isEqualTo(UPDATED_RESOLVER_URI);
    }

    @Test
    @Transactional
    public void updateNonExistingResolver() throws Exception {
        int databaseSizeBeforeUpdate = resolverRepository.findAll().size();

        // Create the Resolver
        ResolverDTO resolverDTO = resolverMapper.toDto(resolver);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResolverMockMvc.perform(put("/api/resolvers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(resolverDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Resolver in the database
        List<Resolver> resolverList = resolverRepository.findAll();
        assertThat(resolverList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteResolver() throws Exception {
        // Initialize the database
        resolverRepository.saveAndFlush(resolver);

        int databaseSizeBeforeDelete = resolverRepository.findAll().size();

        // Delete the resolver
        restResolverMockMvc.perform(delete("/api/resolvers/{id}", resolver.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Resolver> resolverList = resolverRepository.findAll();
        assertThat(resolverList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
