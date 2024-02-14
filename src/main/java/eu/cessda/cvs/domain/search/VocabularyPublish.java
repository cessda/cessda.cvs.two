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

import eu.cessda.cvs.domain.Code;
import eu.cessda.cvs.domain.VocabularyBase;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(indexName = "vocabularypublish")
public class VocabularyPublish extends VocabularyBase {
    private static final long serialVersionUID = 1315418060181340404L;

    @Field( type = FieldType.Nested, store = true )
    private Set<Code> codes = new HashSet<>();

    @Field( type = FieldType.Keyword )
    private Set<String> languagesPublished;

    public Set<Code> getCodes()
    {
        return codes;
    }

    public VocabularyBase codes( Set<Code> codes )
    {
        this.codes = codes;
        return this;
    }

    public void setCodes( Set<Code> codes )
    {
        this.codes = codes;
    }

    public Set<String> getLanguagesPublished()
    {
        return languagesPublished;
    }

    public void setLanguagesPublished( Set<String> languagesPublished )
    {
        this.languagesPublished = languagesPublished;
    }

    public VocabularyBase addLanguagePublished( String languagePublished )
    {
        this.languagesPublished.add( languagePublished );
        return this;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        VocabularyPublish vocabularyPublish = (VocabularyPublish) o;
        if ( vocabularyPublish.getId() == null || getId() == null )
        {
            return false;
        }
        return Objects.equals( getId(), vocabularyPublish.getId() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( getId() );
    }

    @Override
    public String toString() {
        return "VocabularyPublish{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", uri='" + getUri() + "'" +
            ", notation='" + getNotation() + "'" +
            ", versionNumber='" + getVersionNumber() + "'" +
            ", initialPublication=" + getInitialPublication() +
            ", previousPublication=" + getPreviousPublication() +
            ", archived='" + isArchived() + "'" +
            ", withdrawn='" + isWithdrawn() + "'" +
            ", discoverable='" + isDiscoverable() + "'" +
            ", sourceLanguage='" + getSourceLanguage() + "'" +
            ", agencyId='" + getAgencyId() + "'" +
            ", agencyName='" + getAgencyName() + "'" +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", notes='" + getNotes() + "'" +
            ", versionSq='" + getVersionSq() + "'" +
            ", titleSq='" + getTitleSq() + "'" +
            ", definitionSq='" + getDefinitionSq() + "'" +
            ", versionBs='" + getVersionBs() + "'" +
            ", titleBs='" + getTitleBs() + "'" +
            ", definitionBs='" + getDefinitionBs() + "'" +
            ", versionBg='" + getVersionBg() + "'" +
            ", titleBg='" + getTitleBg() + "'" +
            ", definitionBg='" + getDefinitionBg() + "'" +
            ", versionHr='" + getVersionHr() + "'" +
            ", titleHr='" + getTitleHr() + "'" +
            ", definitionHr='" + getDefinitionHr() + "'" +
            ", versionCs='" + getVersionCs() + "'" +
            ", titleCs='" + getTitleCs() + "'" +
            ", definitionCs='" + getDefinitionCs() + "'" +
            ", versionDa='" + getVersionDa() + "'" +
            ", titleDa='" + getTitleDa() + "'" +
            ", definitionDa='" + getDefinitionDa() + "'" +
            ", versionNl='" + getVersionNl() + "'" +
            ", titleNl='" + getTitleNl() + "'" +
            ", definitionNl='" + getDefinitionNl() + "'" +
            ", versionEn='" + getVersionEn() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            ", definitionEn='" + getDefinitionEn() + "'" +
            ", versionEt='" + getVersionEt() + "'" +
            ", titleEt='" + getTitleEt() + "'" +
            ", definitionEt='" + getDefinitionEt() + "'" +
            ", versionFi='" + getVersionFi() + "'" +
            ", titleFi='" + getTitleFi() + "'" +
            ", definitionFi='" + getDefinitionFi() + "'" +
            ", versionFr='" + getVersionFr() + "'" +
            ", titleFr='" + getTitleFr() + "'" +
            ", definitionFr='" + getDefinitionFr() + "'" +
            ", versionDe='" + getVersionDe() + "'" +
            ", titleDe='" + getTitleDe() + "'" +
            ", definitionDe='" + getDefinitionDe() + "'" +
            ", versionEl='" + getVersionEl() + "'" +
            ", titleEl='" + getTitleEl() + "'" +
            ", definitionEl='" + getDefinitionEl() + "'" +
            ", versionHu='" + getVersionHu() + "'" +
            ", titleHu='" + getTitleHu() + "'" +
            ", definitionHu='" + getDefinitionHu() + "'" +
            ", versionIt='" + getVersionIt() + "'" +
            ", titleIt='" + getTitleIt() + "'" +
            ", definitionIt='" + getDefinitionIt() + "'" +
            ", versionJa='" + getVersionJa() + "'" +
            ", titleJa='" + getTitleJa() + "'" +
            ", definitionJa='" + getDefinitionJa() + "'" +
            ", versionLt='" + getVersionLt() + "'" +
            ", titleLt='" + getTitleLt() + "'" +
            ", definitionLt='" + getDefinitionLt() + "'" +
            ", versionMk='" + getVersionMk() + "'" +
            ", titleMk='" + getTitleMk() + "'" +
            ", definitionMk='" + getDefinitionMk() + "'" +
            ", versionNo='" + getVersionNo() + "'" +
            ", titleNo='" + getTitleNo() + "'" +
            ", definitionNo='" + getDefinitionNo() + "'" +
            ", versionPl='" + getVersionPl() + "'" +
            ", titlePl='" + getTitlePl() + "'" +
            ", definitionPl='" + getDefinitionPl() + "'" +
            ", versionPt='" + getVersionPt() + "'" +
            ", titlePt='" + getTitlePt() + "'" +
            ", definitionPt='" + getDefinitionPt() + "'" +
            ", versionRo='" + getVersionRo() + "'" +
            ", titleRo='" + getTitleRo() + "'" +
            ", definitionRo='" + getDefinitionRo() + "'" +
            ", versionRu='" + getVersionRu() + "'" +
            ", titleRu='" + getTitleRu() + "'" +
            ", definitionRu='" + getDefinitionRu() + "'" +
            ", versionSr='" + getVersionSr() + "'" +
            ", titleSr='" + getTitleSr() + "'" +
            ", definitionSr='" + getDefinitionSr() + "'" +
            ", versionSk='" + getVersionSk() + "'" +
            ", titleSk='" + getTitleSk() + "'" +
            ", definitionSk='" + getDefinitionSk() + "'" +
            ", versionSl='" + getVersionSl() + "'" +
            ", titleSl='" + getTitleSl() + "'" +
            ", definitionSl='" + getDefinitionSl() + "'" +
            ", versionEs='" + getVersionEs() + "'" +
            ", titleEs='" + getTitleEs() + "'" +
            ", definitionEs='" + getDefinitionEs() + "'" +
            ", versionSv='" + getVersionSv() + "'" +
            ", titleSv='" + getTitleSv() + "'" +
            ", definitionSv='" + getDefinitionSv() + "'" +
            "}";
    }
}
