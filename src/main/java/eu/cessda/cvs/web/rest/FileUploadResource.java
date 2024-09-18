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

import eu.cessda.cvs.service.FileUploadHelper;
import eu.cessda.cvs.service.FileUploadService;
import eu.cessda.cvs.service.FileUploadType;
import eu.cessda.cvs.web.rest.domain.SimpleResponse;
import org.apache.commons.io.FilenameUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * REST controller for managing File Upload
 */
@RestController
@RequestMapping( "/api/upload" )
public class FileUploadResource
{

    private static final Logger log = LoggerFactory.getLogger( FileUploadResource.class );

    public static final String HTML = ".html";
    private static final String UPLOADED_FILE_URI = "/content/file/";
    private static final String UPLOADED_IMAGES_URI = "/content/images/";


    private final FileUploadService fileUploadService;

    public FileUploadResource( FileUploadService fileUploadService )
    {
        this.fileUploadService = fileUploadService;
    }

    /**
     * {@code POST  /agency-image} : Upload Agency Image.
     *
     * @param file the MultipartFile to be uploaded
     * @return the UUID of uploaded file name
     */
    @PostMapping( "/agency-image" )
    public ResponseEntity<String> uploadAgencyImage( @RequestParam( "file" ) MultipartFile file ) throws IOException, URISyntaxException
    {
        log.debug( "Uploading agency-image file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper( FileUploadType.IMAGE_AGENCY, file );
        Path uploadedFile = fileUploadService.uploadFile( fileUploadHelper );
        return ResponseEntity.created( new URI( UPLOADED_IMAGES_URI + "agency/" + uploadedFile.getFileName() ) ).build();
    }

    /**
     * {@code POST  /license-image} : Upload License Image.
     *
     * @param file the MultipartFile to be uploaded
     * @return the UUID of uploaded file name
     */
    @PostMapping( "/license-image" )
    public ResponseEntity<String> uploadLicenseImage( @RequestParam( "file" ) MultipartFile file ) throws URISyntaxException, IOException
    {
        log.debug( "Uploading license-image file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper( FileUploadType.IMAGE_LICENSE, file );;
        Path uploadedFile = fileUploadService.uploadFile( fileUploadHelper );
        return ResponseEntity.created( new URI( UPLOADED_IMAGES_URI + "license/" + uploadedFile.getFileName() ) ).build();
    }

    /**
     * {@code POST  /file} : Upload File.
     *
     * @param file the MultipartFile to be uploaded
     * @return the UUID of uploaded file name
     */
    @PostMapping( "/file" )
    public ResponseEntity<String> uploadFile( @RequestParam( "file" ) MultipartFile file ) throws URISyntaxException, IOException
    {
        log.debug( "Uploading docx file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper(FileUploadType.DOCX, file);
        Path uploadedFile = fileUploadService.uploadFile( fileUploadHelper );

        return ResponseEntity.created( new URI( UPLOADED_FILE_URI + uploadedFile.getFileName() ) ).build();
    }

    @PostMapping( "/docx2html/{fileName}" )
    public ResponseEntity<SimpleResponse> docx2html( @PathVariable String fileName ) throws IOException, Docx4JException, URISyntaxException
    {
        // Strip out potential path parameters
        fileName = FilenameUtils.getName( fileName );

        log.info( "Converting DOCX to HTML {}", fileName );
        Path htmlFile = fileUploadService.docx2html(fileName, UPLOADED_FILE_URI);

        return ResponseEntity.created( new URI( UPLOADED_FILE_URI + htmlFile.getFileName() ) ).build();
    }

    @PostMapping( "/html2section/{fileName}/{metadataKey}" )
    public ResponseEntity<SimpleResponse> html2section( @PathVariable String fileName, @PathVariable String metadataKey ) throws IOException
    {
        // Strip out potential path parameters
        fileName =  FilenameUtils.getName( fileName );

        log.info( "Extracting metadata key {} from file {}", metadataKey, fileName );
        fileUploadService.html2section(fileName, metadataKey);

        return ResponseEntity.ok( new SimpleResponse( "OK", fileName ) );
    }
}
