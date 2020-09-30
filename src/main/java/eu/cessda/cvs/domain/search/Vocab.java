package eu.cessda.cvs.domain.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document(indexName = "vocab")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Vocab  implements Serializable {
    private static final long serialVersionUID = 1L;

    public Vocab() {
    }

    public Vocab(Long agencyId, String agencyName, String urn, String url, String sourceLanguage, String notation, Set<String> languages) {
        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.urn = urn;
        this.url = url;
        this.sourceLanguage = sourceLanguage;
        this.notation = notation;
        this.languages = languages;
    }

    @Id
    private Long id;

    @Field( type = FieldType.Keyword )
    private Long agencyId;

    @Field( type = FieldType.Keyword )
    private String agencyName;

    private String urn;

    private String url;

    private String sourceLanguage;

    @Field( type = FieldType.Keyword )
    private String notation;

    @Field( type = FieldType.Text )
    private Set<String> languages;

    @Field( type = FieldType.Nested, store = true )
    List<Version> versions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    public Vocab addVersion(String urn, String url, String number, String type, String language, LocalDate publicationDate, List<String> codes){
        final Version version = new Version(urn, url, number, type, language, publicationDate, codes);
        versions.add( version );
        return this;
    }
}
