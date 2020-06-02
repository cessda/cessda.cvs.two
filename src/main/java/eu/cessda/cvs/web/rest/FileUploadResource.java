package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.config.ApplicationProperties;
import eu.cessda.cvs.service.FileUploadHelper;
import eu.cessda.cvs.service.FileUploadService;

import eu.cessda.cvs.service.FileUploadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Filu Upload
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadResource {

    private final Logger log = LoggerFactory.getLogger(FileUploadResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileUploadService fileUploadService;

    private final ApplicationProperties applicationProperties;

    public FileUploadResource(FileUploadService fileUploadService, ApplicationProperties applicationProperties) {
        this.fileUploadService = fileUploadService;
        this.applicationProperties = applicationProperties;
    }

    /**
     * {@code POST  /upload-agency-image} : Upload Agency Image.
     * @param file the MultipartFile to be uploaded
     * @return the UUID of uploaded file name
     */
    @PostMapping("/agency-image")
    public ResponseEntity<String> handleFileUpload(
        @RequestParam("file") MultipartFile file) {
        log.info( "Uploading file " + file.getName() );
        try {
            FileUploadHelper fileUploadHelper = new FileUploadHelper()
                .fileUploadType(FileUploadType.IMAGE_AGENCY)
                .sourceFile(file)
                .uploadBaseDirectory(applicationProperties.getAgencyImagePath());

           fileUploadService.uploadFile( fileUploadHelper );

           return ResponseEntity.status(HttpStatus.OK).body(fileUploadHelper.getUploadedFile().getName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("");
        }
    }
}
