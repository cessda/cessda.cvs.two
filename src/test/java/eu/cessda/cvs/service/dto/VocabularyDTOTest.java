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

import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.utils.VersionNumber;
import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import antlr.Version;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VocabularyDTOTest {

    @Test
    void switchGetterAndSetterVocabularyTest(){
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        for (Language lang : Language.values()) {
            String uuid1 = UUID.randomUUID().toString();
            String uuid2 = UUID.randomUUID().toString();
            String uuid3 = UUID.randomUUID().toString().substring(0,5);

            if( lang.equals(Language.UNKNOWN))
                continue;

            vocabularyDTO.setTitleDefinition(uuid1, uuid2, lang.getIso(), false);
            vocabularyDTO.setVersionByLanguage(lang.getIso(), uuid3);
            assertThat(vocabularyDTO.getTitleByLanguage(lang.getIso())).isEqualTo(uuid1);
            assertThat(vocabularyDTO.getDefinitionByLanguage(lang.getIso())).isEqualTo(uuid2);
            assertThat(vocabularyDTO.getVersionByLanguage(lang.getIso())).isEqualTo(uuid3);
            vocabularyDTO.setTitleDefinition(uuid1, null, lang.getIso(), true);
            assertThat(vocabularyDTO.getTitleByLanguage(lang.getIso())).isEqualTo(uuid1);
            assertThat(vocabularyDTO.getDefinitionByLanguage(lang.getIso())).isEqualTo(uuid2);
        }
    }

    @Test
    void addRemoveVocabularyTest(){
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        VersionDTO version = new VersionDTO();

        vocabularyDTO.setVersions( null );
        vocabularyDTO.addVersion(version);
        assertThat(vocabularyDTO.getVersions()).isNotNull();
        assertThat(vocabularyDTO.getVersions().size()).isEqualTo(1);

        vocabularyDTO.removeVersion(version);
        assertThat(vocabularyDTO.getVersions()).isEmpty();

        vocabularyDTO.setVersions( null );
        final VocabularyDTO vocabularyDTO1 = vocabularyDTO.removeVersion(version);
        assertThat(vocabularyDTO1).isEqualTo(vocabularyDTO);
    }

    @Test
    void addRemoveWithNullTest(){
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        vocabularyDTO.setLanguages( null );
        vocabularyDTO.addLanguage("en");
        assertThat(vocabularyDTO.getLanguages()).isNotNull();

        vocabularyDTO.setLanguagesPublished(null);
        vocabularyDTO.addLanguagePublished("en");
        assertThat(vocabularyDTO.getLanguagesPublished()).isNotNull();

        vocabularyDTO.setStatuses(null);
        vocabularyDTO.addStatuses("DRAFT");
        assertThat(vocabularyDTO.getStatuses()).isNotNull();
    }

    @Test
    void getSwitchCaseByUndefinedLanguageCaseTest(){
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        assertThrows( IllegalArgumentException.class, () -> vocabularyDTO.getTitleByLanguage(Language.UNKNOWN) );
        assertThrows( IllegalArgumentException.class, () -> vocabularyDTO.getDefinitionByLanguage(Language.UNKNOWN) );
        assertThrows( IllegalArgumentException.class, () -> vocabularyDTO.getVersionByLanguage(Language.UNKNOWN) );
        assertThrows( IllegalArgumentException.class, () -> vocabularyDTO.setTitleDefinition("AAA","AAAA",Language.UNKNOWN, true) );
        assertThrows( IllegalArgumentException.class, () -> vocabularyDTO.setVersionByLanguage(Language.UNKNOWN, "1.0") );
    }

    @Test
    void utilsMethodsTest(){
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        VocabularyDTO.cleanUpContentForApi(vocabularyDTO);
        assertThat(vocabularyDTO.isArchived()).isNull();
        assertThat(vocabularyDTO.getAgencyLogo()).isNull();
    }


    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyDTO.class);
        VocabularyDTO vocabularyDTO1 = new VocabularyDTO();
        vocabularyDTO1.setId(1L);
        VocabularyDTO vocabularyDTO2 = new VocabularyDTO();
        assertThat(vocabularyDTO1).isNotEqualTo(vocabularyDTO2);
        vocabularyDTO2.setId(vocabularyDTO1.getId());
        assertThat(vocabularyDTO1).isEqualTo(vocabularyDTO2);
        vocabularyDTO2.setId(2L);
        assertThat(vocabularyDTO1).isNotEqualTo(vocabularyDTO2);
        vocabularyDTO1.setId(null);
        assertThat(vocabularyDTO1).isNotEqualTo(vocabularyDTO2);
    }

    @Test
    void contentByVocabularySnippet() {
        VocabularyDTO vocabularyDTO1 = new VocabularyDTO();
        VocabularySnippet vocabularySnippet1 = new VocabularySnippet();
        vocabularySnippet1.setLanguage(Language.ENGLISH.getIso());
        vocabularySnippet1.setVersionNumber(new VersionNumber(1,0,0));
        vocabularyDTO1.setContentByVocabularySnippet(vocabularySnippet1);
        assertThat(vocabularyDTO1.getVersionEn()).isEqualTo(vocabularySnippet1.getVersionNumber().toString());

        VocabularyDTO vocabularyDTO2 = new VocabularyDTO();
        VocabularySnippet vocabularySnippet2 = new VocabularySnippet();
        vocabularySnippet2.setLanguage(Language.ENGLISH.getIso());
        vocabularyDTO2.setContentByVocabularySnippet(vocabularySnippet2);
        assertThat(vocabularyDTO2.getVersionEn()).isNull();
    }

    @Test
    void constructFromSnippet() {

        Language lang = Language.SLOVAK;

        assertThat(new VocabularyDTO().getSourceLanguage()).isNotEqualTo(lang.getIso());

        VocabularySnippet vocabularySnippet = new VocabularySnippet();
        vocabularySnippet.setLanguage(lang.getIso());

        // snippet is SL
        vocabularySnippet.setItemType(ItemType.SL);
        VocabularyDTO vocabularyDTO1 = new VocabularyDTO(vocabularySnippet);
        assertThat(vocabularyDTO1.getSourceLanguage()).isEqualTo(vocabularySnippet.getLanguage());

        // snippet is TL
        vocabularySnippet.setItemType(ItemType.TL);
        VocabularyDTO vocabularyDTO2 = new VocabularyDTO(vocabularySnippet);
        assertThat(vocabularyDTO2.getSourceLanguage()).isNotEqualTo(vocabularySnippet.getLanguage());
    }

    @Test
    void setGetVersionNumberAsString() {
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        
        vocabularyDTO.setVersionNumber((String) null);
        assertThat(vocabularyDTO.getVersionNumberAsString()).isNull();

        String versionNumber = "9.9.9";
        vocabularyDTO.setVersionNumber(versionNumber);
        assertThat(vocabularyDTO.getVersionNumberAsString()).isEqualTo(versionNumber);
    }
}
