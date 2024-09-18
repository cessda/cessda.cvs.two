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
import eu.cessda.cvs.domain.enumeration.ObjectType;
import eu.cessda.cvs.service.FileUploadHelper;
import eu.cessda.cvs.service.FileUploadService;
import eu.cessda.cvs.service.FileUploadType;
import eu.cessda.cvs.service.MetadataFieldService;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.web.rest.domain.SimpleResponse;
import org.apache.commons.io.FilenameUtils;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

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

    private final MetadataFieldService metadataFieldService;

    public FileUploadResource( ApplicationProperties applicationProperties, MetadataFieldService metadataFieldService )
    {
        this.applicationProperties = applicationProperties;
        this.metadataFieldService = metadataFieldService;
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
        log.info( "Uploading agency-image file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper();
        fileUploadHelper.setFileUploadType( FileUploadType.IMAGE_AGENCY );
        fileUploadHelper.setSourceFile( file );
        fileUploadHelper.setUploadBaseDirectory( Path.of( applicationProperties.getAgencyImagePath() ) );

        FileUploadService.uploadFile( fileUploadHelper );

        return ResponseEntity.created( new URI( UPLOADED_IMAGES_URI + "agency/" + fileUploadHelper.getUploadedFile().getFileName() ) )
            .body( fileUploadHelper.getUploadedFile().getFileName().toString() );
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
        log.info( "Uploading license-image file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper();
        fileUploadHelper.setFileUploadType( FileUploadType.IMAGE_LICENSE );
        fileUploadHelper.setSourceFile( file );
        fileUploadHelper.setUploadBaseDirectory( Path.of( applicationProperties.getLicenseImagePath() ) );

        FileUploadService.uploadFile( fileUploadHelper );

        return ResponseEntity.created( new URI( UPLOADED_IMAGES_URI + "license/" + fileUploadHelper.getUploadedFile().getFileName() ) )
            .body( fileUploadHelper.getUploadedFile().getFileName().toString() );
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
        log.info( "Uploading file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper();
        fileUploadHelper.setFileUploadType( FileUploadType.DOCX );
        fileUploadHelper.setSourceFile( file );
        fileUploadHelper.setUploadBaseDirectory( Path.of( applicationProperties.getUploadFilePath() ) );

        FileUploadService.uploadFile( fileUploadHelper );

        return ResponseEntity.created( new URI( UPLOADED_FILE_URI + fileUploadHelper.getUploadedFile().getFileName() ) )
            .body( fileUploadHelper.getUploadedFile().getFileName().toString() );
    }

    @PostMapping( "/docx2html/{fileName}" )
    public ResponseEntity<SimpleResponse> docx2html( @PathVariable String fileName ) throws IOException, Docx4JException, URISyntaxException
    {
        // Strip out potential path parameters
        fileName = FilenameUtils.getName( fileName );

        log.info( "Converting DOCX to HTML {}", fileName );

        Docx4jProperties.getProperties().setProperty( "docx4j.Log4j.Configurator.disabled", "true" );

        WordprocessingMLPackage wordMLPackage;

        try ( var inputStream = new FileInputStream( new File( applicationProperties.getUploadFilePath(), fileName ) ) )
        {
            wordMLPackage = Docx4J.load( inputStream );
        }
        catch ( FileNotFoundException e )
        {
            // If a FileNotFoundException is thrown, return a 404
            return ResponseEntity.notFound().build();
        }

        // Configure Docx4J HTML settings
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setImageDirPath( applicationProperties.getUploadFilePath() );
        htmlSettings.setImageTargetUri( UPLOADED_FILE_URI );
        htmlSettings.setOpcPackage( wordMLPackage );

        // Store the HTML in this buffer
        var outputBuffer = new FastByteArrayOutputStream();

        // Export to HTML, then parse with Jsoup
        Docx4J.toHTML( htmlSettings, outputBuffer, Docx4J.FLAG_EXPORT_PREFER_XSL );
        Document doc = Jsoup.parse( outputBuffer.getInputStream(), null, applicationProperties.getUploadFilePath() );

        // Replace linked images with embedded Base64 encoded versions
        for ( Element element : doc.select( "img" ) )
        {
            String src = element.attr( "src" );
            if ( !src.startsWith( "data:" ) )
            {
                Path imageFile = Path.of( applicationProperties.getStaticFilePath(), src );
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

        var outputHTMLFile = Path.of( applicationProperties.getUploadFilePath(), fileName + HTML );
        try ( BufferedWriter htmlWriter = Files.newBufferedWriter( outputHTMLFile, doc.charset() ) )
        {
            htmlWriter.write( doc.toString() );
        }

        return ResponseEntity.created( new URI( UPLOADED_FILE_URI + fileName + HTML ) ).build();
    }

    @PostMapping( "/html2section/{fileName}/{metadataKey}" )
    public ResponseEntity<SimpleResponse> html2section( @PathVariable String fileName, @PathVariable String metadataKey ) throws IOException
    {
        // Strip out potential path parameters
        fileName =  FilenameUtils.getName( fileName );

        log.info( "Uploading file {}", fileName );

        File initialFile = new File( applicationProperties.getUploadFilePath() + fileName + HTML );
        Document doc = Jsoup.parse( initialFile, null );
        Elements elements = doc.body().children();

        metadataFieldService.findOneByMetadataKey( metadataKey ).ifPresent( metadataFieldDTO ->
        {

            Set<MetadataValueDTO> metadataValues = new LinkedHashSet<>();
            MetadataValueDTO item = new MetadataValueDTO( "section-1", ObjectType.SYSTEM,
                metadataFieldDTO.getId(), metadataFieldDTO.getMetadataKey(), 1 );
            item.setValue( elements.toString() );

                metadataValues.add( item );

            final Set<MetadataValueDTO> toBeDeletedOldItems = metadataFieldDTO.getMetadataValues();
            for ( MetadataValueDTO toBeDeletedOldItem : toBeDeletedOldItems )
            {
                toBeDeletedOldItem.setMetadataFieldId( null );
                toBeDeletedOldItem.setMetadataKey( null );
            }
            metadataFieldDTO.getMetadataValues().clear();
            metadataFieldService.save( metadataFieldDTO );

            metadataFieldDTO.setMetadataValues( metadataValues );
            metadataFieldService.save( metadataFieldDTO );
        } );

        return ResponseEntity.ok( new SimpleResponse( "OK", fileName ) );
    }
}
