package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VocabularyDTOTest {

    @Test
    public void switchGetterAndSetterVocabularyTest(){
        VocabularyDTO vocabularyDTO = new VocabularyDTO();
        for (Language lang : Language.values()) {
            String uuid1 = UUID.randomUUID().toString();
            String uuid2 = UUID.randomUUID().toString();
            String uuid3 = UUID.randomUUID().toString().substring(0,5);

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
    public void dtoEqualsVerifier() throws Exception {
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
