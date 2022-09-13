package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private MockMvc fileUploadResourceMockMvc;

    @Test
    void shouldUploadAgencyImage() throws Exception
    {
        MockMultipartFile cessdaPNG = new MockMultipartFile( "file", Files.readAllBytes( Paths.get( "src/main/webapp/content/images/agency/cessda.png" ) ) );
        MvcResult result = fileUploadResourceMockMvc.perform( multipart( "/api/upload/agency-image" ).file( cessdaPNG ) )
            .andExpect( status().isCreated() )
            .andExpect( content().string( containsString( ".jpg" ) ) )
            .andReturn();

        // Test if the newly created resource can be found at the location header
        fileUploadResourceMockMvc.perform( get( result.getResponse().getHeader( "location" ) ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentType( MediaType.IMAGE_JPEG ) );
    }

    @Test
    void shouldUploadLicenseImage() throws Exception
    {
        MockMultipartFile cessdaPNG = new MockMultipartFile( "file", Files.readAllBytes( Paths.get( "src/main/webapp/content/images/license/by.png" ) ) );
        MvcResult result = fileUploadResourceMockMvc.perform( multipart( "/api/upload/license-image" ).file( cessdaPNG ) )
            .andExpect( status().isCreated() )
            .andExpect( content().string( containsString( ".jpg" ) ) )
            .andReturn();

        // Test if the newly created resource can be found at the location header
        fileUploadResourceMockMvc.perform( get( result.getResponse().getHeader( "location" ) ) )
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
        fileUploadResourceMockMvc.perform( get( result.getResponse().getHeader( "location" ) ) )
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
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.status" ).value( "OK" ) )
            .andExpect( jsonPath( "$.message" ).value( fileName ) );

        // Verify that the HTML has been generated
        File htmlFile = new File( "target/classes/static/content/file/" + fileName + "_2.html" );
        assertThat( FileUtils.readFileToString( htmlFile, StandardCharsets.UTF_8) ).contains( "Test document" );
    }

    @Test
    void shouldReturn503IfDocumentWasNotFound() throws Exception
    {
        // Attempt to load a document that doesn't exist
        String fileName = FilenameUtils.getName( UUID.randomUUID() + ".docx" );

        // Verify that an internal server error is returned
        fileUploadResourceMockMvc.perform( post( "/api/upload/docx2html/{fileName}", fileName ) )
            .andExpect( status().isInternalServerError() )
            .andExpect( jsonPath( "$.status" ).value( "Not OK" ) )
            .andExpect( jsonPath( "$.message" ).value( fileName ) );
    }
}
