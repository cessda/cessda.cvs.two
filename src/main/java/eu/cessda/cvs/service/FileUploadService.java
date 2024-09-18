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
package eu.cessda.cvs.service;

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.domain.enumeration.ObjectType;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.imgscalr.Scalr;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger( FileUploadService.class );

    private final ApplicationProperties applicationProperties;
    private final MetadataFieldService metadataFieldService;

    @Autowired
    private FileUploadService(ApplicationProperties applicationProperties, MetadataFieldService metadataFieldService) {
        this.applicationProperties = applicationProperties;
        this.metadataFieldService = metadataFieldService;
    }

    public Path uploadFile( FileUploadHelper fileUploadHelper ) throws IOException
    {
        switch ( fileUploadHelper.getFileUploadType()) {
            case IMAGE_AGENCY:
                return uploadImage( fileUploadHelper, Path.of( applicationProperties.getAgencyImagePath() ) );
            case IMAGE_LICENSE:
                return uploadImage( fileUploadHelper, Path.of( applicationProperties.getLicenseImagePath() ) );
            case DOCX:
                return uploadSpecificFile( fileUploadHelper );
            default:
                throw new IllegalArgumentException("Unexpected value: " + fileUploadHelper.getFileUploadType());
        }
    }

    private Path uploadSpecificFile( FileUploadHelper fileUploadHelper ) throws IOException
    {
        Path baseDirectory = Path.of(applicationProperties.getUploadFilePath());

        // Create destination directory if it does not exist.
        Files.createDirectories(baseDirectory);

        // Generate destination file name
        Path destinationFileName = baseDirectory.resolve( UUID.randomUUID().toString() );
        String fileExtension = fileUploadHelper.getFileUploadType().getExtension();
        Path uploadedFile = Path.of(destinationFileName + "." + fileExtension);

        // Transfer the file to the destination. To work around an MVC bug, uploadedFile is converted to an absolute location.
        fileUploadHelper.getSourceFile().transferTo( uploadedFile.toAbsolutePath() );
        return uploadedFile;
    }

    /**
     * Generate appropriate sized images from the source image and write them to storage.
     * @param fileUploadHelper the upload context, this will be mutated.
     * @throws IOException if an IO error occurs.
     */
    private static Path uploadImage( FileUploadHelper fileUploadHelper, Path baseDirectory ) throws IOException {

        String fileName = UUID.randomUUID() + "." + fileUploadHelper.getFileUploadType().getExtension();

        Path destFile = baseDirectory.resolve(fileName);
        Path pathFileThumb = baseDirectory.resolve("thumbs");
        Path destFileThumb = pathFileThumb.resolve( fileName );

        // Create destination directories if it does not exist.
        Files.createDirectories(baseDirectory);
        Files.createDirectories(pathFileThumb);

        BufferedImage img = ImageIO.read( fileUploadHelper.getSourceFile().getInputStream() );

        // Scale image to an appropriate size
        BufferedImage scaledImg = Scalr.resize(img, Scalr.Mode.AUTOMATIC, 300, 300);
        ImageIO.write(fillTransparentPixels( scaledImg ), "jpg", destFile.toFile());

        // Create thumbnail variant of the image
        BufferedImage scaledImgThumb = Scalr.resize(img, Scalr.Mode.AUTOMATIC, 180, 180);
        ImageIO.write(fillTransparentPixels( scaledImgThumb ), "jpg", destFileThumb.toFile());

        // Set destination files on successful writes
        return destFile;
    }

    private static BufferedImage fillTransparentPixels( BufferedImage image )
    {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage image2 = new BufferedImage(w, h,
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image2.createGraphics();
        g.setColor( Color.white );
        g.fillRect(0,0,w,h);
        g.drawRenderedImage(image, null);
        g.dispose();
        return image2;
    }

    public Path docx2html(String fileName, String uploadedFileUri) throws IOException, Docx4JException {
        WordprocessingMLPackage wordMLPackage;

        try ( var inputStream = Files.newInputStream( Path.of( applicationProperties.getUploadFilePath(), fileName ) ) )
        {
            wordMLPackage = Docx4J.load( inputStream );
        }

        // Configure Docx4J HTML settings
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setImageDirPath( applicationProperties.getUploadFilePath() );
        htmlSettings.setImageTargetUri( uploadedFileUri );
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
                    byte[] imageFileBytes = Files.readAllBytes(imageFile);
                    String imageBase64LogoData = DatatypeConverter.printBase64Binary(imageFileBytes);
                    String type = Files.probeContentType( imageFile );
                    element.attr( "src", "data:" + type + ";base64," + imageBase64LogoData );
                } catch ( IOException e ) {
                    // Remove the image element if the image cannot be loaded
                    log.warn( "Loading image from {} failed: {}", imageFile, e.toString() );
                    element.remove();
                }
            }
        }

        var outputHTMLFile = Path.of( applicationProperties.getUploadFilePath(), fileName + ".html" );
        try ( BufferedWriter htmlWriter = Files.newBufferedWriter( outputHTMLFile, doc.charset() ) )
        {
            htmlWriter.write( doc.toString() );
        }

        return outputHTMLFile;
    }

    public void html2section(String fileName, String metadataKey) throws IOException {
        Path initialFile = Path.of( applicationProperties.getUploadFilePath(), fileName + ".html" );
        Document doc = Jsoup.parse( initialFile, null );
        Elements elements = doc.body().children();

        metadataFieldService.findOneByMetadataKey(metadataKey).ifPresent(metadataFieldDTO ->
        {
            MetadataValueDTO item = new MetadataValueDTO( "section-1", ObjectType.SYSTEM,
                metadataFieldDTO.getId(), metadataFieldDTO.getMetadataKey(), 1 );
            item.setValue( elements.toString() );

            Set<MetadataValueDTO> metadataValues = Collections.singleton(item);

            metadataFieldDTO.setMetadataValues( metadataValues );
            metadataFieldService.save( metadataFieldDTO );
        } );
    }
}
