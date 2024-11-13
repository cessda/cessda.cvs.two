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
import eu.cessda.cvs.config.ApplicationProperties;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CvsApp.class)
@ExtendWith( MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FileUploadResourceTestIT
{

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private MockMvc fileUploadResourceMockMvc;

    @Test
    void shouldUploadAgencyImage() throws Exception
    {
        MockMultipartFile cessdaPNG = new MockMultipartFile( "file", Files.readAllBytes( Paths.get( "src/main/webapp/content/images/agency/cessda.png" ) ) );
        MvcResult result = fileUploadResourceMockMvc.perform( multipart( "/api/upload/agency-image" ).file( cessdaPNG ) )
            .andExpect( status().isCreated() )
            .andExpect( header().string( "location", containsString( ".jpg" ) ) )
            .andReturn();

        // Test if the newly created resource can be found at the location header
        String location = result.getResponse().getHeader( "location" );
        assertThat( location ).isNotNull();
        fileUploadResourceMockMvc.perform( get( location ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentType( MediaType.IMAGE_JPEG ) );
    }

    @Test
    void shouldUploadLicenseImage() throws Exception
    {
        MockMultipartFile cessdaPNG = new MockMultipartFile( "file", Files.readAllBytes( Paths.get( "src/main/webapp/content/images/license/by.png" ) ) );
        MvcResult result = fileUploadResourceMockMvc.perform( multipart( "/api/upload/license-image" ).file( cessdaPNG ) )
            .andExpect( status().isCreated() )
            .andExpect( header().string( "location", containsString( ".jpg" ) ) )
            .andReturn();

        // Test if the newly created resource can be found at the location header
        String location = result.getResponse().getHeader( "location" );
        assertThat( location ).isNotNull();
        fileUploadResourceMockMvc.perform( get( location ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentType( MediaType.IMAGE_JPEG ) );
    }

    @Test
    void shouldUploadFile() throws Exception
    {
        // Read in the file in byte form
        byte[] topicClassificationJson = Files.readAllBytes( Paths.get( "src/main/webapp/content/vocabularies/TopicClassification/TopicClassification.json" ) );
        MockMultipartFile multipartFile = new MockMultipartFile( "file", "TopicClassification.json", "application/json", topicClassificationJson );

        MvcResult result = fileUploadResourceMockMvc.perform( multipart( "/api/upload/file" ).file( multipartFile ) )
            .andExpect( status().isCreated() )
            .andExpect( content().string( any( String.class ) ) )
            .andReturn();

        // Test if the newly created resource can be found at the location header
        String location = result.getResponse().getHeader( "location" );
        assertThat( location ).isNotNull();
        fileUploadResourceMockMvc.perform( get( location ) )
            .andExpect( status().isOk() )
            // Uploaded file should be bitwise identical to the source
            .andExpect( content().bytes( topicClassificationJson ) );
    }

    @Test
    void shouldConvertDOCXToHTML() throws Exception
    {
        // Upload the DOCX
        byte[] testDocument = IOUtils.resourceToByteArray( "/upload/TestDocument.docx" );
        MockMultipartFile multipartFile = new MockMultipartFile( "file", "TestDocument.docx",  "application/vnd.openxmlformats-officedocument.wordprocessingml.document", testDocument);
        MvcResult result = fileUploadResourceMockMvc.perform( multipart( "/api/upload/file" ).file( multipartFile ) )
            .andExpect( status().isCreated() )
            .andExpect( content().string( any( String.class ) ) )
            .andReturn();

        // Convert the file to HTML
        String fileName = FilenameUtils.getName( result.getResponse().getHeader( "location" ) );
        assertThat(fileName).isNotNull();
        fileUploadResourceMockMvc.perform( post( "/api/upload/docx2html/{fileName}", fileName ) )
            .andExpect( status().isCreated())
            .andExpect( header().string("location", containsString(fileName + ".html"))  );

        // Verify that the HTML has been generated
        Path htmlFile = applicationProperties.getUploadFilePath().resolve( fileName + ".html" );
        assertThat( PathUtils.readString( htmlFile, StandardCharsets.UTF_8 ) ).contains( "Test document" );
    }

    @Test
    void shouldReturn404IfDocumentWasNotFound() throws Exception
    {
        // Attempt to load a document that doesn't exist
        String fileName = FilenameUtils.getName( UUID.randomUUID() + ".docx" );

        // Verify that a not found response is returned
        fileUploadResourceMockMvc.perform( post( "/api/upload/docx2html/{fileName}", fileName ) )
            .andExpect( status().isNotFound() );
    }
}
