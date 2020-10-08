package eu.cessda.cvs.domain.search;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VocabStat implements Serializable {
    private static final long serialVersionUID = 1L;

    public VocabStat() {
    }

    public VocabStat(String notation, String sourceLanguage) {
        this.notation = notation;
        this.sourceLanguage = sourceLanguage;
    }

    @Field( type = FieldType.Keyword, store = true )
    private String notation;

    @Field( type = FieldType.Keyword, store = true  )
    private String sourceLanguage;

    @Field( type = FieldType.Keyword, store = true )
    private String currentVersion;

    @Field( type = FieldType.Keyword, store = true )
    private String latestPublishedVersion;

    // language published
    @Field( type = FieldType.Keyword, store = true )
    private List<String> languages = new ArrayList<>();

    // all version status
    @Field(type=FieldType.Nested, store = true )
    private List<VersionStatusStat> versionStatusStats = new ArrayList<>();

    // all codes stat
    @Field(type=FieldType.Nested, store = true )
    private List<VersionCodeStat> versionCodeStats = new ArrayList<>();

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getLatestPublishedVersion() {
        return latestPublishedVersion;
    }

    public void setLatestPublishedVersion(String latestPublishedVersion) {
        this.latestPublishedVersion = latestPublishedVersion;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<VersionStatusStat> getVersionStatusStats() {
        return versionStatusStats;
    }

    public void setVersionStatusStats(List<VersionStatusStat> versionStatusStats) {
        this.versionStatusStats = versionStatusStats;
    }

    public List<VersionCodeStat> getVersionCodeStats() {
        return versionCodeStats;
    }

    public void setVersionCodeStats(List<VersionCodeStat> versionCodeStats) {
        this.versionCodeStats = versionCodeStats;
    }
}
