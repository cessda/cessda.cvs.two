package eu.cessda.cvs.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

enum FileUploadType{ IMAGE, CSV }

public class FileUploadHelper {
    private FileUploadType fileUploadType;
    private MultipartFile sourceFile;
    private String uploadBaseDirectory;
    private File uploadedFile;
    private File uploadedThumbFile;

    public FileUploadType getFileUploadType() {
        return fileUploadType;
    }

    public void setFileUploadType(FileUploadType fileUploadType) {
        this.fileUploadType = fileUploadType;
    }

    public FileUploadHelper fileUploadType(FileUploadType fileUploadType) {
        this.fileUploadType = fileUploadType;
        return this;
    }

    public MultipartFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(MultipartFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public FileUploadHelper sourceFIle(MultipartFile sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public String getUploadBaseDirectory() {
        return uploadBaseDirectory;
    }

    public void setUploadBaseDirectory(String uploadBaseDirectory) {
        this.uploadBaseDirectory = uploadBaseDirectory;
    }

    public FileUploadHelper uploadBaseDirectory( String uploadBaseDirectory) {
        this.uploadBaseDirectory = uploadBaseDirectory;
        return this;
    }

    public File getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(File uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public File getUploadedThumbFile() {
        return uploadedThumbFile;
    }

    public void setUploadedThumbFile(File uploadedThumbFile) {
        this.uploadedThumbFile = uploadedThumbFile;
    }
}
