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

package eu.cessda.cvs.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import eu.cessda.cvs.utils.VersionNumber;

/**
 * A Code.
 */
public class Code implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String notation;

    private String uri;

    @Field( type = FieldType.Keyword )
    private Set<String> languages;

    private Integer position;

    private String parent;

    private Long vocabularyId;

    private Long versionId;

    @Type( type = "eu.cessda.cvs.utils.VersionNumberType" )
    private VersionNumber versionNumber;

    @Field( type = FieldType.Date, format = DateFormat.date )
    private LocalDate publicationDate;

    @Field( type = FieldType.Boolean )
    private Boolean deprecated;

    @Field( type = FieldType.Text, store = true )
    private String titleSq;

    @Field( type = FieldType.Text, store = true )
    private String definitionSq;

    @Field( type = FieldType.Text, store = true )
    private String titleBs;

    @Field( type = FieldType.Text, store = true )
    private String definitionBs;

    @Field( type = FieldType.Text, store = true, analyzer = "bulgarian", searchAnalyzer = "bulgarian" )
    private String titleBg;

    @Field( type = FieldType.Text, store = true, analyzer = "bulgarian", searchAnalyzer = "bulgarian" )
    private String definitionBg;

    @Field( type = FieldType.Text, store = true )
    private String titleHr;

    @Field( type = FieldType.Text, store = true )
    private String definitionHr;

    @Field( type = FieldType.Text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String titleCs;

    @Field( type = FieldType.Text, store = true, analyzer = "czech", searchAnalyzer = "czech" )
    private String definitionCs;

    @Field( type = FieldType.Text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String titleDa;

    @Field( type = FieldType.Text, store = true, analyzer = "danish", searchAnalyzer = "danish" )
    private String definitionDa;

    @Field( type = FieldType.Text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String titleNl;

    @Field( type = FieldType.Text, store = true, analyzer = "dutch", searchAnalyzer = "dutch" )
    private String definitionNl;

    @Field( type = FieldType.Text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String titleEn;

    @Field( type = FieldType.Text, store = true, analyzer = "english", searchAnalyzer = "english" )
    private String definitionEn;

    @Field( type = FieldType.Text, store = true )
    private String titleEt;

    @Field( type = FieldType.Text, store = true )
    private String definitionEt;

    @Field( type = FieldType.Text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String titleFi;

    @Field( type = FieldType.Text, store = true, analyzer = "finnish", searchAnalyzer = "finnish" )
    private String definitionFi;

    @Field( type = FieldType.Text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String titleFr;

    @Field( type = FieldType.Text, store = true, analyzer = "french", searchAnalyzer = "french" )
    private String definitionFr;

    @Field( type = FieldType.Text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String titleDe;

    @Field( type = FieldType.Text, store = true, analyzer = "german", searchAnalyzer = "german" )
    private String definitionDe;

    @Field( type = FieldType.Text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String titleEl;

    @Field( type = FieldType.Text, store = true, analyzer = "greek", searchAnalyzer = "greek" )
    private String definitionEl;

    @Field( type = FieldType.Text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String titleHu;

    @Field( type = FieldType.Text, store = true, analyzer = "hungarian", searchAnalyzer = "hungarian" )
    private String definitionHu;

    @Field( type = FieldType.Text, store = true, analyzer = "italian", searchAnalyzer = "italian" )
    private String titleIt;

    @Field( type = FieldType.Text, store = true, analyzer = "italian", searchAnalyzer = "italian" )
    private String definitionIt;

    @Field( type = FieldType.Text, store = true )
    private String titleJa;

    @Field( type = FieldType.Text, store = true )
    private String definitionJa;

    @Field( type = FieldType.Text, store = true )
    private String titleLt;

    @Field( type = FieldType.Text, store = true )
    private String definitionLt;

    @Field( type = FieldType.Text, store = true )
    private String titleMk;

    @Field( type = FieldType.Text, store = true )
    private String definitionMk;

    @Field( type = FieldType.Text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String titleNo;

    @Field( type = FieldType.Text, store = true, analyzer = "norwegian", searchAnalyzer = "norwegian" )
    private String definitionNo;

    @Field( type = FieldType.Text, store = true )
    private String titlePl;

    @Field( type = FieldType.Text, store = true )
    private String definitionPl;

    @Field( type = FieldType.Text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String titlePt;

    @Field( type = FieldType.Text, store = true, analyzer = "portuguese", searchAnalyzer = "portuguese" )
    private String definitionPt;

    @Field( type = FieldType.Text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String titleRo;

    @Field( type = FieldType.Text, store = true, analyzer = "romanian", searchAnalyzer = "romanian" )
    private String definitionRo;

    @Field( type = FieldType.Text, store = true, analyzer = "russian", searchAnalyzer = "russian" )
    private String titleRu;

    @Field( type = FieldType.Text, store = true, analyzer = "russian", searchAnalyzer = "russian" )
    private String definitionRu;

    @Field( type = FieldType.Text, store = true )
    private String titleSr;

    @Field( type = FieldType.Text, store = true )
    private String definitionSr;

    @Field( type = FieldType.Text, store = true )
    private String titleSk;

    @Field( type = FieldType.Text, store = true )
    private String definitionSk;

    @Field( type = FieldType.Text, store = true )
    private String titleSl;

    @Field( type = FieldType.Text, store = true )
    private String definitionSl;

    @Field( type = FieldType.Text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String titleEs;

    @Field( type = FieldType.Text, store = true, analyzer = "spanish", searchAnalyzer = "spanish" )
    private String definitionEs;

    @Field( type = FieldType.Text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String titleSv;

    @Field( type = FieldType.Text, store = true, analyzer = "swedish", searchAnalyzer = "swedish" )
    private String definitionSv;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Set<String> getLanguages()
    {
        return languages;
    }

    public void setLanguages( Set<String> languages )
    {
        this.languages = languages;
    }

    public Long getVersionId()
    {
        return versionId;
    }

    public void setVersionId( Long versionId )
    {
        this.versionId = versionId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public VersionNumber getVersionNumber()
    {
        return versionNumber;
    }

    @JsonGetter("versionNumber")
    public String getVersionNumberAsString() {
        return VersionNumber.toString(versionNumber);
    }

    @JsonIgnore
    public void setVersionNumber( VersionNumber versionNumber )
    {
        this.versionNumber = versionNumber;
    }

    @JsonSetter("versionNumber")
    public void setVersionNumber(String str) {
        setVersionNumber(VersionNumber.fromString(str));
    }
    
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getVocabularyId()
    {
        return vocabularyId;
    }

    public void setVocabularyId( Long vocabularyId )
    {
        this.vocabularyId = vocabularyId;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getTitleBs() {
        return titleBs;
    }

    public void setTitleBs(String titleBs) {
        this.titleBs = titleBs;
    }

    public String getDefinitionBs() {
        return definitionBs;
    }

    public void setDefinitionBs(String definitionBs) {
        this.definitionBs = definitionBs;
    }

    public String getTitleSq() {
        return titleSq;
    }

    public void setTitleSq(String titleSq) {
        this.titleSq = titleSq;
    }

    public String getDefinitionSq() {
        return definitionSq;
    }

    public void setDefinitionSq(String definitionSq) {
        this.definitionSq = definitionSq;
    }

    public String getDefinitionBg() {
        return definitionBg;
    }

    public void setDefinitionBg(String definitionBg) {
        this.definitionBg = definitionBg;
    }

    public String getTitleBg() {
        return titleBg;
    }

    public void setTitleBg(String titleBg) {
        this.titleBg = titleBg;
    }

    public String getTitleCs() {
        return titleCs;
    }

    public void setTitleCs(String titleCs) {
        this.titleCs = titleCs;
    }

    public String getDefinitionCs() {
        return definitionCs;
    }

    public void setDefinitionCs(String definitionCs) {
        this.definitionCs = definitionCs;
    }

    public String getTitleHr() {
        return titleHr;
    }

    public void setTitleHr(String titleHr) {
        this.titleHr = titleHr;
    }

    public String getDefinitionHr() {
        return definitionHr;
    }

    public void setDefinitionHr(String definitionHr) {
        this.definitionHr = definitionHr;
    }

    public String getTitleDa() {
        return titleDa;
    }

    public void setTitleDa(String titleDa) {
        this.titleDa = titleDa;
    }

    public String getDefinitionDa() {
        return definitionDa;
    }

    public void setDefinitionDa(String definitionDa) {
        this.definitionDa = definitionDa;
    }

    public String getDefinitionEn() {
        return definitionEn;
    }

    public void setDefinitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleNl() {
        return titleNl;
    }

    public void setTitleNl(String titleNl) {
        this.titleNl = titleNl;
    }

    public String getDefinitionNl() {
        return definitionNl;
    }

    public void setDefinitionNl(String definitionNl) {
        this.definitionNl = definitionNl;
    }

    public String getTitleEt() {
        return titleEt;
    }

    public void setTitleEt(String titleEt) {
        this.titleEt = titleEt;
    }

    public String getDefinitionEt() {
        return definitionEt;
    }

    public void setDefinitionEt(String definitionEt) {
        this.definitionEt = definitionEt;
    }

    public String getDefinitionFr() {
        return definitionFr;
    }

    public void setDefinitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getTitleFi() {
        return titleFi;
    }

    public void setTitleFi(String titleFi) {
        this.titleFi = titleFi;
    }

    public String getDefinitionFi() {
        return definitionFi;
    }

    public void setDefinitionFi(String definitionFi) {
        this.definitionFi = definitionFi;
    }

    public String getTitleDe() {
        return titleDe;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getTitleEl() {
        return titleEl;
    }

    public void setTitleEl(String titleEl) {
        this.titleEl = titleEl;
    }

    public String getDefinitionEl() {
        return definitionEl;
    }

    public void setDefinitionEl(String definitionEl) {
        this.definitionEl = definitionEl;
    }

    public String getDefinitionDe() {
        return definitionDe;
    }

    public void setDefinitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
    }


    public String getTitleJa() {
        return titleJa;
    }

    public void setTitleJa(String titleJa) {
        this.titleJa = titleJa;
    }

    public String getTitleIt() {
        return titleIt;
    }

    public void setTitleIt(String titleIt) {
        this.titleIt = titleIt;
    }

    public String getDefinitionJa() {
        return definitionJa;
    }

    public void setDefinitionJa(String definitionJa) {
        this.definitionJa = definitionJa;
    }

    public String getDefinitionIt() {
        return definitionIt;
    }

    public void setDefinitionIt(String definitionIt) {
        this.definitionIt = definitionIt;
    }

    public String getDefinitionHu() {
        return definitionHu;
    }

    public void setDefinitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
    }

    public String getTitleHu() {
        return titleHu;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getTitleLt() {
        return titleLt;
    }

    public void setTitleLt(String titleLt) {
        this.titleLt = titleLt;
    }

    public String getDefinitionLt() {
        return definitionLt;
    }

    public void setDefinitionLt(String definitionLt) {
        this.definitionLt = definitionLt;
    }

    public String getTitleNo() {
        return titleNo;
    }

    public void setTitleNo(String titleNo) {
        this.titleNo = titleNo;
    }

    public String getDefinitionNo() {
        return definitionNo;
    }

    public void setDefinitionNo(String definitionNo) {
        this.definitionNo = definitionNo;
    }

    public String getDefinitionMk() {
        return definitionMk;
    }

    public void setDefinitionMk(String definitionMk) {
        this.definitionMk = definitionMk;
    }

    public String getTitleMk() {
        return titleMk;
    }

    public void setTitleMk(String titleMk) {
        this.titleMk = titleMk;
    }

    public String getTitleRo() {
        return titleRo;
    }

    public void setTitleRo(String titleRo) {
        this.titleRo = titleRo;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getTitlePl() {
        return titlePl;
    }

    public void setTitlePl(String titlePl) {
        this.titlePl = titlePl;
    }

    public String getDefinitionPt() {
        return definitionPt;
    }

    public void setDefinitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
    }

    public String getDefinitionRo() {
        return definitionRo;
    }

    public String getDefinitionPl() {
        return definitionPl;
    }

    public void setDefinitionPl(String definitionPl) {
        this.definitionPl = definitionPl;
    }

    public void setDefinitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
    }

    public String getDefinitionSr() {
        return definitionSr;
    }

    public void setDefinitionSr(String definitionSr) {
        this.definitionSr = definitionSr;
    }

    public String getTitleSr() {
        return titleSr;
    }

    public void setTitleSr(String titleSr) {
        this.titleSr = titleSr;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public String getDefinitionRu() {
        return definitionRu;
    }

    public void setDefinitionRu(String definitionRu) {
        this.definitionRu = definitionRu;
    }

    public String getTitleSl() {
        return titleSl;
    }

    public void setTitleSl(String titleSl) {
        this.titleSl = titleSl;
    }

    public String getDefinitionSl() {
        return definitionSl;
    }

    public void setDefinitionSl(String definitionSl) {
        this.definitionSl = definitionSl;
    }

    public String getTitleSk() {
        return titleSk;
    }

    public void setTitleSk(String titleSk) {
        this.titleSk = titleSk;
    }

    public String getDefinitionSk() {
        return definitionSk;
    }

    public void setDefinitionSk(String definitionSk) {
        this.definitionSk = definitionSk;
    }

    public String getDefinitionSv() {
        return definitionSv;
    }

    public void setDefinitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
    }

    public String getTitleSv() {
        return titleSv;
    }

    public void setTitleSv(String titleSv) {
        this.titleSv = titleSv;
    }

    public String getTitleEs() {
        return titleEs;
    }

    public void setTitleEs(String titleEs) {
        this.titleEs = titleEs;
    }

    public String getDefinitionEs() {
        return definitionEs;
    }

    public void setDefinitionEs(String definitionEs) {
        this.definitionEs = definitionEs;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Code)) {
            return false;
        }
        return id != null && id.equals(((Code) o).id);
    }

    @Override
    public String toString() {
        return "Code{" +
            "id=" + getId() +
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", parent='" + getParent() + "'" +
            ", position=" + getPosition() +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", deprecated='" + getDeprecated() + "'" +
            ", titleSq='" + getTitleSq() + "'" +
            ", definitionSq='" + getDefinitionSq() + "'" +
            ", titleBs='" + getTitleBs() + "'" +
            ", definitionBs='" + getDefinitionBs() + "'" +
            ", titleBg='" + getTitleBg() + "'" +
            ", definitionBg='" + getDefinitionBg() + "'" +
            ", titleHr='" + getTitleHr() + "'" +
            ", definitionHr='" + getDefinitionHr() + "'" +
            ", titleCs='" + getTitleCs() + "'" +
            ", definitionCs='" + getDefinitionCs() + "'" +
            ", titleDa='" + getTitleDa() + "'" +
            ", definitionDa='" + getDefinitionDa() + "'" +
            ", titleNl='" + getTitleNl() + "'" +
            ", definitionNl='" + getDefinitionNl() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            ", definitionEn='" + getDefinitionEn() + "'" +
            ", titleEt='" + getTitleEt() + "'" +
            ", definitionEt='" + getDefinitionEt() + "'" +
            ", titleFi='" + getTitleFi() + "'" +
            ", definitionFi='" + getDefinitionFi() + "'" +
            ", titleFr='" + getTitleFr() + "'" +
            ", definitionFr='" + getDefinitionFr() + "'" +
            ", titleDe='" + getTitleDe() + "'" +
            ", definitionDe='" + getDefinitionDe() + "'" +
            ", titleEl='" + getTitleEl() + "'" +
            ", definitionEl='" + getDefinitionEl() + "'" +
            ", titleHu='" + getTitleHu() + "'" +
            ", definitionHu='" + getDefinitionHu() + "'" +
            ", titleIt='" + getTitleIt() + "'" +
            ", definitionIt='" + getDefinitionIt() + "'" +
            ", titleJa='" + getTitleJa() + "'" +
            ", definitionJa='" + getDefinitionJa() + "'" +
            ", titleLt='" + getTitleLt() + "'" +
            ", definitionLt='" + getDefinitionLt() + "'" +
            ", titleMk='" + getTitleMk() + "'" +
            ", definitionMk='" + getDefinitionMk() + "'" +
            ", titleNo='" + getTitleNo() + "'" +
            ", definitionNo='" + getDefinitionNo() + "'" +
            ", titlePl='" + getTitlePl() + "'" +
            ", definitionPl='" + getDefinitionPl() + "'" +
            ", titlePt='" + getTitlePt() + "'" +
            ", definitionPt='" + getDefinitionPt() + "'" +
            ", titleRo='" + getTitleRo() + "'" +
            ", definitionRo='" + getDefinitionRo() + "'" +
            ", titleRu='" + getTitleRu() + "'" +
            ", definitionRu='" + getDefinitionRu() + "'" +
            ", titleSr='" + getTitleSr() + "'" +
            ", definitionSr='" + getDefinitionSr() + "'" +
            ", titleSk='" + getTitleSk() + "'" +
            ", definitionSk='" + getDefinitionSk() + "'" +
            ", titleSl='" + getTitleSl() + "'" +
            ", definitionSl='" + getDefinitionSl() + "'" +
            ", titleEs='" + getTitleEs() + "'" +
            ", definitionEs='" + getDefinitionEs() + "'" +
            ", titleSv='" + getTitleSv() + "'" +
            ", definitionSv='" + getDefinitionSv() + "'" +
            "}";
    }
}
