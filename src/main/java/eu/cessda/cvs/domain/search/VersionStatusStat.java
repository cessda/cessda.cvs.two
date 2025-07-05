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
package eu.cessda.cvs.domain.search;

import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.utils.VersionNumber;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDate;

public class VersionStatusStat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field( type = FieldType.Keyword, store = true  )
    private String language;

    @Field( type = FieldType.Keyword, store = true  )
    private ItemType type;

    @Field( type = FieldType.Keyword, store = true  )
    @Type( type = "eu.cessda.cvs.utils.VersionNumberType" )
    private VersionNumber versionNumber;

    @Field( type = FieldType.Keyword, store = true  )
    private Status status;

    @Field( type = FieldType.Date, format = DateFormat.date, store = true  )
    private LocalDate creationDate;

    @Field( type = FieldType.Date, format = DateFormat.date, store = true  )
    private LocalDate date;

    public VersionStatusStat(){}

    public VersionStatusStat(String language, ItemType type, VersionNumber versionNumber, Status status, LocalDate creationDate, LocalDate date) {
        this.language = language;
        this.type = type;
        this.versionNumber = versionNumber;
        this.status = status;
        this.date = date != null ? date : LocalDate.now();
        this.creationDate = creationDate != null ? creationDate : this.date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public VersionNumber getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(VersionNumber versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
