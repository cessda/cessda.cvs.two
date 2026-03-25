package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.mapper.VersionMapper;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CvsApp.class)
@ExtendWith( MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN_CONTENT")
class UrnResolverIT
{
    private final VersionMapper versionMapper;
    private final MockMvc mockMvc;

    private Version version;
    private String canonicalUri;

    @Autowired
    public UrnResolverIT( VersionMapper versionMapper, MockMvc mockMvc )
    {
        this.versionMapper = versionMapper;
        this.mockMvc = mockMvc;
    }

    @Transactional
    void createAgencyAndVersion() throws Exception {
        // Create the Version
        VersionDTO versionDTO = versionMapper.toDto(version);
        mockMvc.perform(post("/api/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(versionDTO)))
            .andExpect(status().isCreated());
    }

    @BeforeEach
    void initTest() throws Exception
    {
        // Create version
        version = VersionResourceIT.createEntity();

        // Publish the version
        canonicalUri = version.getCanonicalUri();
        version.setCanonicalUri( canonicalUri + ":" + version.getNumber() );
        version.setStatus( Status.PUBLISHED );

        createAgencyAndVersion();
    }

    @Test
    @Transactional
    void resolveUrn() throws Exception
    {
        mockMvc.perform( get("/urn/" + version.getNotation() ) )
            .andExpect( status().isFound() )
            .andExpect( redirectedUrl( "/vocabulary/" + version.getNotation() ) );
    }

    @Test
    @Transactional
    void resolveUrnWithVersion() throws Exception
    {
        mockMvc.perform( get("/urn/" + version.getCanonicalUri() ) )
            .andExpect( status().isFound() )
            .andExpect( redirectedUrl( "/vocabulary/" + version.getNotation() + "?v=" + version.getNumber() ) );
    }

    @Test
    @Transactional
    void returnNotFound() throws Exception
    {
        mockMvc.perform( get("/urn/" + UUID.randomUUID() ) )
            .andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    void returnNotFoundJson() throws Exception
    {
        mockMvc.perform( get("/urn/" + UUID.randomUUID() ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    void resolveUrnJson() throws Exception
    {
        mockMvc.perform( get("/urn/" + canonicalUri ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isFound() )
            .andExpect( redirectedUrl( "/v2/vocabularies/" + version.getNotation() + "/" + version.getNumber() ) );
    }

    @Test
    @Transactional
    void resolveUrnJsonWithVersion() throws Exception
    {
        mockMvc.perform( get("/urn/" + version.getCanonicalUri() ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isFound() )
            .andExpect( redirectedUrl( "/v2/vocabularies/" + version.getNotation() + "/" + version.getNumber() ) );
    }

    @Test
    @Transactional
    void resolveUrnJsonWithLang() throws Exception
    {
        mockMvc.perform( get("/urn/" + version.getCanonicalUri() + "?lang=" + version.getLanguage() ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isFound() )
            .andExpect( redirectedUrl( "/v2/vocabularies/" + version.getNotation() + "/" + version.getNumber() + "?languageVersion=" + version.getLanguage() + "-" + version.getNumber() ) );
    }
}
