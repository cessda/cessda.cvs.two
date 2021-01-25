package eu.cessda.cvs.service;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    private final Logger log = LoggerFactory.getLogger( FileUploadService.class );

    public void uploadFile (FileUploadHelper fileUploadHelper) {
        switch ( fileUploadHelper.getFileUploadType()) {
            case IMAGE_AGENCY:
            case IMAGE_LICENSE:
                uploadSpecificFile(fileUploadHelper, true);
                break;
            case DOCX:
                uploadSpecificFile(fileUploadHelper, false);
                break;
            default:
        }
    }

    private void uploadSpecificFile(FileUploadHelper fileUploadHelper, boolean isImageFile) {
        checkUploadDirectory(fileUploadHelper);
        Path rootLocation = Paths.get( fileUploadHelper.getUploadBaseDirectory());
        try {
            String fileName = storeAndReplaceFileName(fileUploadHelper, rootLocation);
            if( isImageFile)
                storeImageFile(fileUploadHelper, rootLocation, fileName);
            else{
                fileUploadHelper.setUploadedFile(new File( fileUploadHelper.getUploadBaseDirectory() + File.separator + fileName ));
            }
        } catch (Exception e) {
            log.error( e.getMessage() );
        }
    }

    private String storeAndReplaceFileName(FileUploadHelper fileUploadHelper, Path rootLocation) throws IOException {
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString();
        Files.copy(fileUploadHelper.getSourceFile().getInputStream(),
            rootLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void checkUploadDirectory(FileUploadHelper fileUploadHelper) {
        File directory = new File(fileUploadHelper.getUploadBaseDirectory());
        if (! directory.exists()){
            directory.mkdirs();
        }
    }

    private void storeImageFile(FileUploadHelper fileUploadHelper, Path rootLocation, String fileName) throws IOException {
        File imageFile = rootLocation.resolve(fileName).toFile();

        BufferedImage img = ImageIO.read( imageFile );

        BufferedImage scaledImg = Scalr.resize(img, Scalr.Mode.AUTOMATIC, 300, 300);
        File destFile = new File( fileUploadHelper.getUploadBaseDirectory() + File.separator + fileName + ".jpg");
        ImageIO.write(fillTransparentPixels( scaledImg, Color.white ), "jpg", destFile);

        BufferedImage scaledImgThumb = Scalr.resize(img, Scalr.Mode.AUTOMATIC, 180, 180);
        File pathFileThumb = new File( fileUploadHelper.getUploadBaseDirectory() + File.separator + "thumbs");
        if (! pathFileThumb.exists()){
            pathFileThumb.mkdirs();
        }
        File destFileThumb = new File( fileUploadHelper.getUploadBaseDirectory() + File.separator + "thumbs" +
            File.separator + fileName + ".jpg");
        ImageIO.write(fillTransparentPixels( scaledImgThumb, Color.white ), "jpg", destFileThumb);

        fileUploadHelper.setUploadedFile( destFile );
        fileUploadHelper.setUploadedThumbFile( destFileThumb );

        // Delete file
        FileUtils.deleteQuietly( imageFile );
    }

    BufferedImage fillTransparentPixels( BufferedImage image, Color fillColor ) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage image2 = new BufferedImage(w, h,
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image2.createGraphics();
        g.setColor(fillColor);
        g.fillRect(0,0,w,h);
        g.drawRenderedImage(image, null);
        g.dispose();
        return image2;
    }

}
