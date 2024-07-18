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
package eu.cessda.cvs.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.cessda.cvs.domain.Code;
import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.utils.HashFunction;
import eu.cessda.cvs.utils.VersionNumber;
import org.hibernate.annotations.Type;

import javax.persistence.Lob;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A DTO for the {@link Code} entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeDTO implements Serializable {
    private static final long serialVersionUID = -5916802523668287620L;

    private Long id;

    @Size(max = 240)
    private String uri;

    @Size(max = 240)
    private String notation;

    private Set<String> languages;

    @Size(max = 240)
    private String parent;

    private Integer position;

    private LocalDate publicationDate;

    private Long vocabularyId;

    private Long versionId;

    @Type( type = "eu.cessda.cvs.utils.VersionNumberType" )
    private VersionNumber versionNumber;

    private Boolean deprecated;

    private Long replacedById;

    private String replacedByUri;

    private String replacedByNotation;

    @Lob
    private String titleSq;

    @Lob
    private String definitionSq;

    @Lob
    private String titleBs;

    @Lob
    private String definitionBs;

    @Lob
    private String titleBg;

    @Lob
    private String definitionBg;

    @Lob
    private String titleHr;

    @Lob
    private String definitionHr;

    @Lob
    private String titleCs;

    @Lob
    private String definitionCs;

    @Lob
    private String titleDa;

    @Lob
    private String definitionDa;

    @Lob
    private String titleNl;

    @Lob
    private String definitionNl;

    @Lob
    private String titleEn;

    @Lob
    private String definitionEn;

    @Lob
    private String titleEt;

    @Lob
    private String definitionEt;

    @Lob
    private String titleFi;

    @Lob
    private String definitionFi;

    @Lob
    private String titleFr;

    @Lob
    private String definitionFr;

    @Lob
    private String titleDe;

    @Lob
    private String definitionDe;

    @Lob
    private String titleEl;

    @Lob
    private String definitionEl;

    @Lob
    private String titleHu;

    @Lob
    private String definitionHu;

    @Lob
    private String titleIt;

    @Lob
    private String definitionIt;

    @Lob
    private String titleJa;

    @Lob
    private String definitionJa;

    @Lob
    private String titleLt;

    @Lob
    private String definitionLt;

    @Lob
    private String titleMk;

    @Lob
    private String definitionMk;

    @Lob
    private String titleNo;

    @Lob
    private String definitionNo;

    @Lob
    private String titlePl;

    @Lob
    private String definitionPl;

    @Lob
    private String titlePt;

    @Lob
    private String definitionPt;

    @Lob
    private String titleRo;

    @Lob
    private String definitionRo;

    @Lob
    private String titleRu;

    @Lob
    private String definitionRu;

    @Lob
    private String titleSr;

    @Lob
    private String definitionSr;

    @Lob
    private String titleSk;

    @Lob
    private String definitionSk;

    @Lob
    private String titleSl;

    @Lob
    private String definitionSl;

    @Lob
    private String titleEs;

    @Lob
    private String definitionEs;

    @Lob
    private String titleSv;

    @Lob
    private String definitionSv;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

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

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public CodeDTO addLanguage(String language) {
        if(languages == null)
        {
            languages = new HashSet<>();
        }
        this.languages.add(language);
        return this;
    }

    public void removeLanguage(String language) {
        if ( languages != null )
        {
            this.languages.remove( language );
        }
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public VersionNumber getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(VersionNumber versionNumber) {
        this.versionNumber = versionNumber;
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

    public String getTitleBg() {
        return titleBg;
    }

    public void setTitleBg(String titleBg) {
        this.titleBg = titleBg;
    }

    public String getDefinitionBg() {
        return definitionBg;
    }

    public void setDefinitionBg(String definitionBg) {
        this.definitionBg = definitionBg;
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

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDefinitionEn() {
        return definitionEn;
    }

    public void setDefinitionEn(String definitionEn) {
        this.definitionEn = definitionEn;
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

    public String getTitleFr() {
        return titleFr;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getDefinitionFr() {
        return definitionFr;
    }

    public void setDefinitionFr(String definitionFr) {
        this.definitionFr = definitionFr;
    }

    public String getTitleDe() {
        return titleDe;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getDefinitionDe() {
        return definitionDe;
    }

    public void setDefinitionDe(String definitionDe) {
        this.definitionDe = definitionDe;
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

    public String getTitleHu() {
        return titleHu;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getDefinitionHu() {
        return definitionHu;
    }

    public void setDefinitionHu(String definitionHu) {
        this.definitionHu = definitionHu;
    }

    public String getTitleIt() {
        return titleIt;
    }

    public void setTitleIt(String titleIt) {
        this.titleIt = titleIt;
    }

    public String getDefinitionIt() {
        return definitionIt;
    }

    public void setDefinitionIt(String definitionIt) {
        this.definitionIt = definitionIt;
    }

    public String getTitleJa() {
        return titleJa;
    }

    public void setTitleJa(String titleJa) {
        this.titleJa = titleJa;
    }

    public String getDefinitionJa() {
        return definitionJa;
    }

    public void setDefinitionJa(String definitionJa) {
        this.definitionJa = definitionJa;
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

    public String getTitleMk() {
        return titleMk;
    }

    public void setTitleMk(String titleMk) {
        this.titleMk = titleMk;
    }

    public String getDefinitionMk() {
        return definitionMk;
    }

    public void setDefinitionMk(String definitionMk) {
        this.definitionMk = definitionMk;
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

    public String getTitlePl() {
        return titlePl;
    }

    public void setTitlePl(String titlePl) {
        this.titlePl = titlePl;
    }

    public String getDefinitionPl() {
        return definitionPl;
    }

    public void setDefinitionPl(String definitionPl) {
        this.definitionPl = definitionPl;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getDefinitionPt() {
        return definitionPt;
    }

    public void setDefinitionPt(String definitionPt) {
        this.definitionPt = definitionPt;
    }

    public String getTitleRo() {
        return titleRo;
    }

    public void setTitleRo(String titleRo) {
        this.titleRo = titleRo;
    }

    public String getDefinitionRo() {
        return definitionRo;
    }

    public void setDefinitionRo(String definitionRo) {
        this.definitionRo = definitionRo;
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

    public String getTitleSr() {
        return titleSr;
    }

    public void setTitleSr(String titleSr) {
        this.titleSr = titleSr;
    }

    public String getDefinitionSr() {
        return definitionSr;
    }

    public void setDefinitionSr(String definitionSr) {
        this.definitionSr = definitionSr;
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

    public String getTitleSv() {
        return titleSv;
    }

    public void setTitleSv(String titleSv) {
        this.titleSv = titleSv;
    }

    public String getDefinitionSv() {
        return definitionSv;
    }

    public void setDefinitionSv(String definitionSv) {
        this.definitionSv = definitionSv;
    }

    public Boolean getDeprecated() {
        return this.deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Long getReplacedById() {
        return this.replacedById;
    }

    public void setReplacedById(Long replacedById) {
        this.replacedById = replacedById;
    }

    public String getReplacedByUri() {
        return this.replacedByUri;
    }

    public void setReplacedByUri(String replacedByUri) {
        this.replacedByUri = replacedByUri;
    }

    public String getReplacedByNotation() {
        return this.replacedByNotation;
    }

    public void setReplacedByNotation(String replacedByNotation) {
        this.replacedByNotation = replacedByNotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CodeDTO codeDTO = (CodeDTO) o;
        if (codeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), codeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CodeDTO{" +
            "id=" + getId() +
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", parent='" + getParent() + "'" +
            ", position=" + getPosition() +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", deprecated='" + getDeprecated() + "'" +
            ", replacedById='" + getReplacedById() + "'" +
            ", replacedByUri='" + getReplacedByUri() + "'" +
            ", replacedByNotation='" + getReplacedByNotation() + "'" +
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

    public CodeDTO setTitleDefinition(String title, String definition, String language, boolean ignoreNullValue) {
        return setTitleDefinition(title, definition, Language.getByIso(language.toLowerCase()), ignoreNullValue);
    }

    public CodeDTO setTitleDefinition(String title, String definition, Language language, boolean ignoreNullValue) {
        if( ignoreNullValue ) {
            if( title == null )
                title = getTitleByLanguage( language );
            if( definition == null )
                definition = getDefinitionByLanguage( language );
        }
        switch (language) {
            case CZECH:
                setDefinitionCs(definition);
                setTitleCs(title);
                break;
            case DANISH:
                setDefinitionDa(definition);
                setTitleDa(title);
                break;
            case DUTCH:
                setDefinitionNl(definition);
                setTitleNl(title);
                break;
            case ENGLISH:
                setDefinitionEn(definition);
                setTitleEn(title);
                break;
            case ESTONIAN:
                setDefinitionEt(definition);
                setTitleEt(title);
                break;
            case FINNISH:
                setDefinitionFi(definition);
                setTitleFi(title);
                break;
            case FRENCH:
                setDefinitionFr(definition);
                setTitleFr(title);
                break;
            case GERMAN:
                setDefinitionDe(definition);
                setTitleDe(title);
                break;
            case GREEK:
                setDefinitionEl(definition);
                setTitleEl(title);
                break;
            case HUNGARIAN:
                setDefinitionHu(definition);
                setTitleHu(title);
                break;
            case ITALIAN:
                setDefinitionIt(definition);
                setTitleIt(title);
                break;
            case JAPANESE:
                setDefinitionJa(definition);
                setTitleJa(title);
                break;
            case LITHUANIAN:
                setDefinitionLt(definition);
                setTitleLt(title);
                break;
            case NORWEGIAN:
                setDefinitionNo(definition);
                setTitleNo(title);
                break;
            case PORTUGUESE:
                setDefinitionPt(definition);
                setTitlePt(title);
                break;
            case ROMANIAN:
                setDefinitionRo(definition);
                setTitleRo(title);
                break;
            case SLOVAK:
                setDefinitionSk(definition);
                setTitleSk(title);
                break;
            case SLOVENIAN:
                setDefinitionSl(definition);
                setTitleSl(title);
                break;
            case SPANISH:
                setDefinitionEs(definition);
                setTitleEs(title);
                break;
            case SWEDISH:
                setDefinitionSv(definition);
                setTitleSv(title);
                break;
            case ALBANIAN:
                setDefinitionSq(definition);
                setTitleSq(title);
                break;
            case BOSNIAN:
                setDefinitionBs(definition);
                setTitleBs(title);
                break;
            case BULGARIAN:
                setDefinitionBg(definition);
                setTitleBg(title);
                break;
            case CROATIAN:
                setDefinitionHr(definition);
                setTitleHr(title);
                break;
            case MACEDONIAN:
                setDefinitionMk(definition);
                setTitleMk(title);
                break;
            case POLISH:
                setDefinitionPl(definition);
                setTitlePl(title);
                break;
            case RUSSIAN:
                setDefinitionRu(definition);
                setTitleRu(title);
                break;
            case SERBIAN:
                setDefinitionSr(definition);
                setTitleSr(title);
                break;
            default:
                throw new IllegalArgumentException("Undefined case in setTitleDefinition for enum Language " + language);
        }
        addLanguage(language.getIso());
        return this;
    }

    public static Optional<CodeDTO> findByIdFromList(Set<CodeDTO> codes, int docId) {
        return codes.stream().filter( voc -> voc.getId() == docId).findFirst();
    }

    public static String generateHash( HashFunction hf, String str, Integer len) {
        // default hash
        String hash = '#' + str;

        if (hf != null) {
            hash = hf.hash(str);
        }

        // truncate
        if (len != null && len > 0) {
            hash = hash.substring(0, len);
        }

        return hash;
    }

    public String callGenerateHash(String hf, String str, Integer len) {
        return generateHash(HashFunction.fromString(hf), str, len);
    }

    // group 0 - hash algorithm, group 1 - truncation; i.e., keep the first n characters
    public static final Pattern patternHashCodeUriPlaceholder = Pattern.compile("\\[CODE-HASH-([^-]+)-(\\d+)\\]", Pattern.CASE_INSENSITIVE);

    public static String rewriteUri(String uri, String hfInput) {

        if (uri == null || uri.isBlank()) {
            return uri;
        }

        Matcher m = patternHashCodeUriPlaceholder.matcher(uri);

        while (m.find()) {
            HashFunction hf = HashFunction.fromString(m.group(1));
            Integer len = Integer.parseInt(m.group(2));
            uri = uri.substring(0, m.start()) + generateHash(hf, hfInput, len) + uri.substring(m.end());
            m = patternHashCodeUriPlaceholder.matcher(uri);
        }

        return uri;
    }

    public static Set<CodeDTO> generateCodesFromVersion(Set<VersionDTO> versions, boolean isForEditor){
        Map<String, CodeDTO> codeDTOsMap = new LinkedHashMap<>();
        // use to ignore version with same lang, e.g. FRv2.0.2 and FRv2.0.1 only FRv.2.0.2 will be chosen
        Set<String> versionLangs = new HashSet<>();
        long codeIndex = 0L;
        // code ID for editor will always on even number, the publication code will be on odd number
        if( isForEditor )
            codeIndex = 1L;
        for (VersionDTO version : versions) {
            if( versionLangs.contains( version.getLanguage()) ) // skip existed version lang
                continue;

            long baseCodeId = version.getId() * 10000;
            versionLangs.add( version.getLanguage());
            for (ConceptDTO concept : version.getConcepts()) {
                CodeDTO codeDTO = codeDTOsMap.get(concept.getNotation());
                if( codeDTO == null ){
                    codeDTO = new CodeDTO();
                    codeDTO.setId( baseCodeId + codeIndex);
                    codeDTO.setNotation( concept.getNotation());
                    codeDTO.setUri( concept.getUri() );
                    codeDTO.setUri(
                        rewriteUri(
                            codeDTO.getUri(),
                            concept.getNotation()
                        )
                    );
                    codeDTO.setPosition( concept.getPosition() );
                    if( concept.getParent() != null )
                        codeDTO.setParent( concept.getParent() );
                    codeDTO.setDeprecated( concept.getDeprecated() );
                    codeDTO.setReplacedById( concept.getReplacedById() );
                    codeDTO.setReplacedByUri( concept.getReplacedByUri() );
                    codeDTO.setReplacedByNotation( concept.getReplacedByNotation() );
                    codeDTOsMap.put(concept.getNotation(), codeDTO);
                    codeIndex+=2L;
                }
                codeDTO.setTitleDefinition(concept.getTitle(), concept.getDefinition(), version.getLanguage(), false);
            }
        }

        return new LinkedHashSet<>(codeDTOsMap.values());
    }

    /**
     * Get title from specific language. Used by Thymeleaf. Do not remove
     */
    public String getTitleByLanguage( String language ) {
        return getTitleByLanguage( Language.getByIso(language.toLowerCase()) );
    }

    public String getTitleByLanguage( Language language ) {
        switch (language) {
            case SERBIAN:       return titleSr;
            case RUSSIAN:       return titleRu;
            case POLISH:        return titlePl;
            case MACEDONIAN:    return titleMk;
            case CROATIAN:      return titleHr;
            case BULGARIAN:     return titleBg;
            case BOSNIAN:       return titleBs;
            case ALBANIAN:      return titleSq;
            case SWEDISH:       return titleSv;
            case SPANISH:       return titleEs;
            case SLOVENIAN:     return titleSl;
            case SLOVAK:        return titleSk;
            case ROMANIAN:      return titleRo;
            case PORTUGUESE:    return titlePt;
            case NORWEGIAN:     return titleNo;
            case LITHUANIAN:    return titleLt;
            case JAPANESE:      return titleJa;
            case ITALIAN:       return titleIt;
            case HUNGARIAN:     return titleHu;
            case GREEK:         return titleEl;
            case GERMAN:        return titleDe;
            case FRENCH:        return titleFr;
            case FINNISH:       return titleFi;
            case ESTONIAN:      return titleEt;
            case ENGLISH:       return titleEn;
            case DUTCH:         return titleNl;
            case DANISH:        return titleDa;
            case CZECH:         return titleCs;
            default:            throw new IllegalArgumentException("Undefined case for enum Language " + language);
        }
    }

    /**
     * Get definition from specific language. Used by Thymeleaf. Do not remove.
     */
    public String getDefinitionByLanguage( String language ) {
        return getDefinitionByLanguage( Language.getByIso(language.toLowerCase()));
    }

    public String getDefinitionByLanguage( Language language ) {
        switch (language) {
            case SERBIAN:       return definitionSr;
            case RUSSIAN:       return definitionRu;
            case POLISH:        return definitionPl;
            case MACEDONIAN:    return definitionMk;
            case CROATIAN:      return definitionHr;
            case BULGARIAN:     return definitionBg;
            case BOSNIAN:       return definitionBs;
            case ALBANIAN:      return definitionSq;
            case SWEDISH:       return definitionSv;
            case SPANISH:       return definitionEs;
            case SLOVENIAN:     return definitionSl;
            case SLOVAK:        return definitionSk;
            case ROMANIAN:      return definitionRo;
            case PORTUGUESE:    return definitionPt;
            case NORWEGIAN:     return definitionNo;
            case LITHUANIAN:    return definitionLt;
            case JAPANESE:      return definitionJa;
            case ITALIAN:       return definitionIt;
            case HUNGARIAN:     return definitionHu;
            case GREEK:         return definitionEl;
            case GERMAN:        return definitionDe;
            case FRENCH:        return definitionFr;
            case FINNISH:       return definitionFi;
            case ESTONIAN:      return definitionEt;
            case ENGLISH:       return definitionEn;
            case DUTCH:         return definitionNl;
            case DANISH:        return definitionDa;
            case CZECH:         return definitionCs;
            default:            throw new IllegalArgumentException("Undefined case for enum Language " + language);
        }
    }

    public void clearContents(){
        setDefinitionSr(null);
        setDefinitionRu(null);
        setDefinitionPl(null);
        setDefinitionMk(null);
        setDefinitionHr(null);
        setDefinitionBg(null);
        setDefinitionBs(null);
        setDefinitionSq(null);
        setTitleSr(null);
        setTitleRu(null);
        setTitlePl(null);
        setTitleMk(null);
        setTitleHr(null);
        setTitleBg(null);
        setTitleBs(null);
        setTitleSq(null);
        setDefinitionSv(null);
        setTitleSv(null);
        setDefinitionEs(null);
        setTitleEs(null);
        setDefinitionSl(null);
        setTitleSl(null);
        setDefinitionSk(null);
        setTitleSk(null);
        setDefinitionRo(null);
        setTitleRo(null);
        setDefinitionPt(null);
        setTitlePt(null);
        setDefinitionNo(null);
        setTitleNo(null);
        setDefinitionLt(null);
        setTitleLt(null);
        setDefinitionJa(null);
        setTitleJa(null);
        setDefinitionIt(null);
        setTitleIt(null);
        setDefinitionHu(null);
        setTitleHu(null);
        setDefinitionEl(null);
        setTitleEl(null);
        setDefinitionDe(null);
        setTitleDe(null);
        setDefinitionFr(null);
        setTitleFr(null);
        setDefinitionFi(null);
        setTitleFi(null);
        setDefinitionEt(null);
        setTitleEt(null);
        setDefinitionEn(null);
        setTitleEn(null);
        setDefinitionNl(null);
        setTitleNl(null);
        setDefinitionDa(null);
        setTitleDa(null);
        setDefinitionCs(null);
        setTitleCs(null);
        setLanguages( null );
    }
}
