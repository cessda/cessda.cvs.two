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
package eu.cessda.cvs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Cvs.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String vocabJsonPath;
    private String staticFilePath;
    private String uploadFilePath;
    private String agencyImagePath;
    private String licenseImagePath;

    public String getVocabJsonPath() {
        return vocabJsonPath;
    }

    public void setVocabJsonPath(String vocabJsonPath) {
        this.vocabJsonPath = vocabJsonPath;
    }

    public String getStaticFilePath() {
        return staticFilePath;
    }

    public void setStaticFilePath(String staticFilePath) {
        this.staticFilePath = staticFilePath;
    }

    public String getAgencyImagePath() {
        return agencyImagePath;
    }

    public void setAgencyImagePath(String agencyImagePath) {
        this.agencyImagePath = agencyImagePath;
    }

    public String getLicenseImagePath() {
        return licenseImagePath;
    }

    public void setLicenseImagePath(String licenseImagePath) {
        this.licenseImagePath = licenseImagePath;
    }

    public String getUploadFilePath() {
        return uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }
}
