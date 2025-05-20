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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Properties specific to Cvs.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private static final Logger log = LoggerFactory.getLogger( ApplicationProperties.class );

    public final Path staticFilePath;

    public ApplicationProperties(Path staticFilePath) {
        this.staticFilePath = staticFilePath;
    }

    @ConstructorBinding
    public ApplicationProperties(String staticFilePath) throws IOException {
        if (staticFilePath == null) {
            this.staticFilePath = Files.createTempDirectory("cvs-static");
            log.warn( "Static file directory not configured. Using temporary directory \"{}\"", this.staticFilePath );
        } else {
            this.staticFilePath = Path.of( staticFilePath );
        }
    }

    public Path getStaticFilePath() {
        return staticFilePath;
    }

    public Path getVocabJsonPath() {
        return staticFilePath.resolve( "vocabularies" );
    }

    public Path getAgencyImagePath() {
        return staticFilePath.resolve( "images" ).resolve( "agency" );
    }

    public Path getLicenseImagePath() {
        return staticFilePath.resolve( "images" ).resolve( "license" );
    }

    public Path getUploadFilePath() {
        return staticFilePath.resolve( "file" );
    }

    public Path getExportFilePath() {
        return staticFilePath.resolve( "export" );
    }
}
