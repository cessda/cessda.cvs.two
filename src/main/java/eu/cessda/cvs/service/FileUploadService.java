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

package eu.cessda.cvs.service;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadService {

    private FileUploadService() {}

    public static void uploadFile( FileUploadHelper fileUploadHelper ) throws IOException
    {
        switch ( fileUploadHelper.getFileUploadType()) {
            case IMAGE_AGENCY:
            case IMAGE_LICENSE:
                uploadImage(fileUploadHelper);
                break;
            case DOCX:
                uploadSpecificFile(fileUploadHelper );
                break;
            default:
        }
    }

    private static void uploadSpecificFile( FileUploadHelper fileUploadHelper ) throws IOException
    {
        // Create destination directory if it does not exist.
        createUploadDirectory( Paths.get( fileUploadHelper.getUploadBaseDirectory() ) );
        File uploadedFile = new File( fileUploadHelper.getUploadBaseDirectory() + File.separator + UUID.randomUUID() );

        // Transfer the file to the destination. To work around a MVC bug, uploadedFile is converted to an absolute file.
        fileUploadHelper.getSourceFile().transferTo( uploadedFile.getAbsoluteFile() );
        fileUploadHelper.setUploadedFile( uploadedFile );
    }

    /**
     * Create the upload directory, and it's parent directories, if it doesn't exist.
     * @param directory the directory to create.
     * @throws IOException if an IO error occurs.
     */
    private static void createUploadDirectory( Path directory ) throws IOException
    {
        if (!Files.isDirectory( directory ))
        {
            Files.createDirectories( directory );
        }
    }

    /**
     * Generate appropriate sized images from the source image and write them to storage.
     * @param fileUploadHelper the upload context, this will be mutated.
     * @throws IOException if an IO error occurs.
     */
    private static void uploadImage( FileUploadHelper fileUploadHelper ) throws IOException {

        String fileName = UUID.randomUUID() + ".jpg";


        File destFile = new File( fileUploadHelper.getUploadBaseDirectory() + File.separator + fileName );
        File pathFileThumb = new File( fileUploadHelper.getUploadBaseDirectory() + File.separator + "thumbs" );
        File destFileThumb = new File( pathFileThumb + File.separator + fileName );

        // Create destination directories if it does not exist.
        createUploadDirectory( destFile.toPath().getParent() );
        createUploadDirectory( pathFileThumb.toPath() );

        BufferedImage img = ImageIO.read( fileUploadHelper.getSourceFile().getInputStream() );

        // Scale image to an appropriate size
        BufferedImage scaledImg = Scalr.resize(img, Scalr.Mode.AUTOMATIC, 300, 300);
        ImageIO.write(fillTransparentPixels( scaledImg ), "jpg", destFile);

        // Create thumbnail variant of the image
        BufferedImage scaledImgThumb = Scalr.resize(img, Scalr.Mode.AUTOMATIC, 180, 180);
        ImageIO.write(fillTransparentPixels( scaledImgThumb ), "jpg", destFileThumb);

        // Set destination files on successful writes
        fileUploadHelper.setUploadedFile( destFile );
        fileUploadHelper.setUploadedThumbFile( destFileThumb );
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

}
