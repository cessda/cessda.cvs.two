/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.domain.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import eu.cessda.cvs.utils.VersionNumber;

public class VersionCodeStat implements Serializable {
    private static final long serialVersionUID = 1L;

    public VersionCodeStat(){}

    public VersionCodeStat(VersionNumber versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Field( type = FieldType.Keyword, store = true )
    @Type( type = "eu.cessda.cvs.utils.VersionNumberType" )
    private VersionNumber versionNumber;

    @Field( type = FieldType.Keyword, store = true )
    private List<String> codes = new ArrayList<>();

    @JsonIgnore
    public VersionNumber getVersionNumber() {
        return versionNumber;
    }
    
    @JsonGetter("versionNumber")
    public String getVersionNumberAsString() {
        if (versionNumber != null) {
            return versionNumber.toString();
        }
        return null;
    }

    @JsonIgnore
    public void setVersionNumber(VersionNumber versionNumber) {
        this.versionNumber = versionNumber;
    }

    @JsonSetter("versionNumber")
    public void setVersionNumber(String str) {
        setVersionNumber(str != null ? new VersionNumber(str) : null);
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}
