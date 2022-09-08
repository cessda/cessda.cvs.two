package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.CvsApp;
import org.junit.jupiter.api.Disabled;
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

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Disabled("Files without extensions are not returned by the MVC")
    @Test
    void shouldUploadFile() throws Exception
    {
        // Read in the file in byte form
        byte[] topicClassificationJson = Files.readAllBytes( Paths.get( "src/main/webapp/content/vocabularies/TopicClassification/TopicClassification.json" ) );
        MockMultipartFile multipartFile = new MockMultipartFile( "file", topicClassificationJson );

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

}
