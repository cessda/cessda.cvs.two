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

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.enumeration.ObjectType;
import eu.cessda.cvs.service.FileUploadHelper;
import eu.cessda.cvs.service.FileUploadService;
import eu.cessda.cvs.service.FileUploadType;
import eu.cessda.cvs.service.MetadataFieldService;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.web.rest.domain.SimpleResponse;
import org.apache.commons.io.FileUtils;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * REST controller for managing File Upload
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadResource {

    public static final String HTML = ".html";
    private final Logger log = LoggerFactory.getLogger(FileUploadResource.class);

    private final ApplicationProperties applicationProperties;

    private final MetadataFieldService metadataFieldService;

    public FileUploadResource( ApplicationProperties applicationProperties, MetadataFieldService metadataFieldService) {
        this.applicationProperties = applicationProperties;
        this.metadataFieldService = metadataFieldService;
    }

    /**
     * {@code POST  /agency-image} : Upload Agency Image.
     * @param file the MultipartFile to be uploaded
     * @return the UUID of uploaded file name
     */
    @PostMapping("/agency-image")
    public ResponseEntity<String> uploadAgencyImage(
        @RequestParam("file") MultipartFile file) throws IOException, URISyntaxException
    {
        log.info( "Uploading agency-image file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper()
            .fileUploadType(FileUploadType.IMAGE_AGENCY)
            .sourceFile(file)
            .uploadBaseDirectory(applicationProperties.getAgencyImagePath());

       FileUploadService.uploadFile( fileUploadHelper );

       return ResponseEntity.status(HttpStatus.CREATED)
           .location( new URI("/content/images/agency/" + fileUploadHelper.getUploadedFile().getName() ) )
           .body(fileUploadHelper.getUploadedFile().getName());
    }
    /**
     * {@code POST  /license-image} : Upload License Image.
     * @param file the MultipartFile to be uploaded
     * @return the UUID of uploaded file name
     */
    @PostMapping("/license-image")
    public ResponseEntity<String> uploadLicenseImage(
        @RequestParam("file") MultipartFile file) throws URISyntaxException, IOException
    {
        log.info( "Uploading license-image file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper()
            .fileUploadType(FileUploadType.IMAGE_LICENSE)
            .sourceFile(file)
            .uploadBaseDirectory(applicationProperties.getLicenseImagePath());

        FileUploadService.uploadFile( fileUploadHelper );

        return ResponseEntity.status(HttpStatus.CREATED)
            .location( new URI("/content/images/license/" + fileUploadHelper.getUploadedFile().getName() ) )
            .body(fileUploadHelper.getUploadedFile().getName());
    }

    /**
     * {@code POST  /file} : Upload File.
     * @param file the MultipartFile to be uploaded
     * @return the UUID of uploaded file name
     */
    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(
        @RequestParam("file") MultipartFile file) throws URISyntaxException, IOException
    {
        log.info( "Uploading file {}", file.getName() );
        FileUploadHelper fileUploadHelper = new FileUploadHelper()
            .fileUploadType(FileUploadType.DOCX)
            .sourceFile(file)
            .uploadBaseDirectory(applicationProperties.getUploadFilePath());

        FileUploadService.uploadFile( fileUploadHelper );

        return ResponseEntity.status(HttpStatus.CREATED)
            .location( new URI( "/content/file/" + fileUploadHelper.getUploadedFile().getName() ) )
            .body(fileUploadHelper.getUploadedFile().getName());
    }

    @PostMapping("/docx2html/{fileName}")
    public ResponseEntity<SimpleResponse> docx2html(
        @PathVariable String fileName) {
        log.info( "Converting DOCX to HTML {}", fileName );
        try
        {
            Docx4jProperties.getProperties().setProperty( "docx4j.Log4j.Configurator.disabled", "true" );

            FileInputStream is = new FileInputStream( applicationProperties.getUploadFilePath() + fileName );
            WordprocessingMLPackage wordMLPackage = Docx4J.load( is );
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setImageDirPath( applicationProperties.getUploadFilePath() );
            htmlSettings.setImageTargetUri( "/content/file/" );
            htmlSettings.setOpcPackage( wordMLPackage );
            FileOutputStream out = new FileOutputStream( applicationProperties.getUploadFilePath() + fileName + HTML );
            Docx4J.toHTML( htmlSettings, out, Docx4J.FLAG_EXPORT_PREFER_XSL );

            Document doc = Jsoup.parse( new File(
                applicationProperties.getUploadFilePath() + fileName + HTML ), "UTF-8" );
            for ( Element element : doc.select( "img" ) )
            {
                String src = element.attr( "src" );

                if ( !src.startsWith( "data:" ) )
                {

                    Path imageFile = Paths.get( applicationProperties.getStaticFilePath() + src );
                    String imageBase64LogoData = getImageBase64( imageFile );
                    element.attr( "src", "data:image/png;base64," + imageBase64LogoData );

                }
            }

            try ( BufferedWriter htmlWriter = Files.newBufferedWriter(
                Paths.get( applicationProperties.getUploadFilePath() + fileName + "_2" + HTML ), StandardCharsets.UTF_8 )
            )
            {
                htmlWriter.write( doc.toString() );
            }

            return ResponseEntity.status( HttpStatus.OK ).body( new SimpleResponse( "OK", fileName ) );
        }
        catch ( IOException | Docx4JException e )
        {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( new SimpleResponse( "Not OK", fileName ) );
        }
    }

    private String getImageBase64( Path imageFile ) {
        try {
            return DatatypeConverter.printBase64Binary(Files.readAllBytes(imageFile));
        } catch (IOException e) {
            log.error( e.getMessage() );
            return null;
        }
    }

    @PostMapping("/html2section/{fileName}/{metadataKey}")
    public ResponseEntity<SimpleResponse> html2section(
        @PathVariable String fileName,
        @PathVariable String metadataKey) {
        log.info( "Uploading file {}", fileName );
        try {
            File initialFile = new File(applicationProperties.getUploadFilePath() + fileName + HTML);
            String result = FileUtils.readFileToString(initialFile, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(result, "UTF-8");
            Elements elements = doc.select("body").first().children();

            metadataFieldService.findOneByMetadataKey(metadataKey).ifPresent( metadataFieldDTO ->
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
            });

            return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("OK", fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new SimpleResponse("Not-OK", fileName));
        }
    }
}
