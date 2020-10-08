package eu.cessda.cvs.domain.search;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDate;

public class VersionStatusStat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field( type = FieldType.Keyword, store = true  )
    private String language;

    @Field( type = FieldType.Keyword, store = true  )
    private String type;

    @Field( type = FieldType.Keyword, store = true  )
    private String versionNumber;

    @Field( type = FieldType.Keyword, store = true  )
    private String status;

    @Field( type = FieldType.Date, store = true  )
    private LocalDate date;

    public VersionStatusStat(){}

    public VersionStatusStat(String language, String type, String versionNumber, String status, LocalDate date) {
        this.language = language;
        this.type = type;
        this.versionNumber = versionNumber;
        this.status = status;
        this.date = date != null ? date : LocalDate.now();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
