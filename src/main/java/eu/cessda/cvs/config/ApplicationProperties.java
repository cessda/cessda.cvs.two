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
}
