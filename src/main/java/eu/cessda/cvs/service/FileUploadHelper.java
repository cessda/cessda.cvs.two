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

import java.nio.file.Path;

public class FileUploadHelper {
    private FileUploadType fileUploadType;
    private MultipartFile sourceFile;
    private Path uploadBaseDirectory;
    private Path uploadedFile;
    private Path uploadedThumbFile;

    public FileUploadType getFileUploadType() {
        return fileUploadType;
    }

    public void setFileUploadType(FileUploadType fileUploadType) {
        this.fileUploadType = fileUploadType;
    }

    public MultipartFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(MultipartFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Path getUploadBaseDirectory() {
        return uploadBaseDirectory;
    }

    public void setUploadBaseDirectory(Path uploadBaseDirectory) {
        this.uploadBaseDirectory = uploadBaseDirectory;
    }

    public Path getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Path uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Path getUploadedThumbFile() {
        return uploadedThumbFile;
    }

    public void setUploadedThumbFile(Path uploadedThumbFile) {
        this.uploadedThumbFile = uploadedThumbFile;
    }
}
