package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
}
