package eu.cessda.cvs.domain.search;

import eu.cessda.cvs.service.dto.AgencyDTO;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document(indexName = "agency")
public class AgencyPublish {
    private static final long serialVersionUID = 1L;

    public AgencyPublish(AgencyDTO agencyDTO) {
        this(agencyDTO.getId(), agencyDTO.getName(), agencyDTO.getLink(), agencyDTO.getDescription(), agencyDTO.getLogo());
    }

    public AgencyPublish(Long id, String name, String url, String description, String logo) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.description = description;
        this.logo = logo;
    }

    @Id
    private Long id;

    @Field( type = FieldType.Keyword )
    private String name;

    private String url;

    private String description;

    private String logo;

    @Field( type = FieldType.Nested, store = true )
    List<Vocabulary> vocabularies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    public void setVocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }

    public Vocabulary addVocabulary(String urn, String url, String sourceLanguage, String notation, Set<String> languages){
        final Vocabulary vocabulary = new Vocabulary(urn, url, sourceLanguage, notation, languages);
        vocabularies.add( vocabulary );
        return vocabulary;
    }

    public class Vocabulary{

        public Vocabulary(String urn, String url, String sourceLanguage, String notation, Set<String> languages) {
            this.urn = urn;
            this.url = url;
            this.sourceLanguage = sourceLanguage;
            this.notation = notation;
            this.languages = languages;
        }

        private String urn;

        private String url;

        private String sourceLanguage;

        private String notation;

        @Field( type = FieldType.Text )
        private Set<String> languages;

        @Field( type = FieldType.Nested, store = true )
        List<Version> versions = new ArrayList<>();

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

        public Version addVersion(String urn, String url, String number, String type, String language, LocalDate publicationDate, List<String> codes){
            final Version version = new Version(urn, url, number, type, language, publicationDate, codes);
            versions.add( version );
            return version;
        }

        public class Version{

            public Version(String urn, String url, String number, String type, String language, LocalDate publicationDate, List<String> codes) {
                this.urn = urn;
                this.url = url;
                this.number = number;
                this.type = type;
                this.language = language;
                this.publicationDate = publicationDate;
                this.codes = codes;
                this.noOfCodes = codes.size();
            }

            private String urn;

            private String url;

            private String number;

            private String type;

            @Field( type = FieldType.Keyword )
            private String language;

            @Field(type = FieldType.Date, format = DateFormat.basic_date)
            private LocalDate publicationDate;

            private Integer noOfCodes;

            @Field( type = FieldType.Text )
            private List<String> codes;

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

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public LocalDate getPublicationDate() {
                return publicationDate;
            }

            public void setPublicationDate(LocalDate publicationDate) {
                this.publicationDate = publicationDate;
            }

            public Integer getNoOfCodes() {
                return noOfCodes;
            }

            public void setNoOfCodes(Integer noOfCodes) {
                this.noOfCodes = noOfCodes;
            }

            public List<String> getCodes() {
                return codes;
            }

            public void setCodes(List<String> codes) {
                this.codes = codes;
            }
        }
    }
}
