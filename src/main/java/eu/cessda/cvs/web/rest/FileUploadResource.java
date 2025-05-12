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

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.service.FileUploadHelper;
import eu.cessda.cvs.service.FileUploadService;
import eu.cessda.cvs.service.FileUploadType;
import org.apache.commons.io.FilenameUtils;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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


    private final ApplicationProperties applicationProperties;
    private final FileUploadService fileUploadService;

    public FileUploadResource( ApplicationProperties applicationProperties, FileUploadService fileUploadService )
    {
        this.applicationProperties = applicationProperties;
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
        FileUploadHelper fileUploadHelper = new FileUploadHelper( FileUploadType.IMAGE_LICENSE, file );
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
    public ResponseEntity<Void> docx2html( @PathVariable String fileName ) throws IOException, Docx4JException, URISyntaxException
    {
        // Strip out potential path parameters
        fileName = FilenameUtils.getName( fileName );

        log.info( "Converting DOCX to HTML {}", fileName );

        Docx4jProperties.getProperties().setProperty( "docx4j.Log4j.Configurator.disabled", "true" );

        WordprocessingMLPackage wordMLPackage;

        try
        {
            wordMLPackage = Docx4J.load( applicationProperties.getUploadFilePath().resolve( fileName ).toFile() );
        }
        catch ( Docx4JException e )
        {
            // If a FileNotFoundException is thrown, return a 404
            if (e.getCause() instanceof FileNotFoundException)
            {
                return ResponseEntity.notFound().build();
            }
            else
            {
                throw e;
            }
        }

        // Configure Docx4J HTML settings
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setImageDirPath( applicationProperties.getUploadFilePath().toString() );
        htmlSettings.setImageTargetUri( UPLOADED_FILE_URI );
        htmlSettings.setOpcPackage( wordMLPackage );

        // Store the HTML in this buffer
        var outputBuffer = new FastByteArrayOutputStream();

        // Export to HTML, then parse with Jsoup
        Docx4J.toHTML( htmlSettings, outputBuffer, Docx4J.FLAG_EXPORT_PREFER_XSL );
        Document doc = Jsoup.parse( outputBuffer.getInputStream(), null, applicationProperties.getUploadFilePath().toUri().toString() );

        // Replace linked images with embedded Base64 encoded versions
        for ( Element element : doc.select( "img" ) )
        {
            String src = element.attr( "src" );
            if ( !src.startsWith( "data:" ) )
            {
                Path imageFile = applicationProperties.getStaticFilePath().resolve( src );
                try
                {
                    // Attempt to load the image data from the file
                    String imageBase64LogoData = DatatypeConverter.printBase64Binary( Files.readAllBytes( imageFile ) );
                    String type = Files.probeContentType( imageFile );
                    element.attr( "src", "data:" + type + ";base64," + imageBase64LogoData );
                } catch ( IOException e ) {
                    // Remove the image element if the image cannot be loaded
                    log.warn( "Loading image from {} failed: {}", imageFile, e.toString() );
                    element.remove();
                }
            }
        }

        var outputHTMLFile = applicationProperties.getUploadFilePath().resolve(  fileName + HTML );
        try ( BufferedWriter htmlWriter = Files.newBufferedWriter( outputHTMLFile, doc.charset() ) )
        {
            htmlWriter.write( doc.toString() );
        }

        return ResponseEntity.created( new URI( UPLOADED_FILE_URI + fileName + HTML ) ).build();
    }

    @PostMapping( "/html2section/{fileName}/{metadataKey}" )
    public ResponseEntity<Void> html2section( @PathVariable String fileName, @PathVariable String metadataKey ) throws IOException
    {
        // Strip out potential path parameters
        fileName =  FilenameUtils.getName( fileName );

        log.info( "Extracting metadata key {} from file {}", metadataKey, fileName );
        fileUploadService.html2section(fileName, metadataKey);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleNoSuchFileException(NoSuchFileException e)
    {
       return ResponseEntity.notFound().build();
    }
}
