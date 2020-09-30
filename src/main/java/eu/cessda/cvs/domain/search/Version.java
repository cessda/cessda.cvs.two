package eu.cessda.cvs.domain.search;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Version implements Serializable {
    private static final long serialVersionUID = 1L;

    public Version() {
    }

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

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd")
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
