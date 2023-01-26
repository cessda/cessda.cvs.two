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

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

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

    public FileUploadHelper sourceFile(MultipartFile sourceFile) {
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
