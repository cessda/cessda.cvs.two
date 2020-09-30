package eu.cessda.cvs.domain.search;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VersionCodeStat implements Serializable {
    private static final long serialVersionUID = 1L;

    public VersionCodeStat(){}

    public VersionCodeStat(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Field( type = FieldType.Keyword, store = true )
    private String versionNumber;

    @Field( type = FieldType.Keyword, store = true )
    private List<String> codes = new ArrayList<>();

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}
