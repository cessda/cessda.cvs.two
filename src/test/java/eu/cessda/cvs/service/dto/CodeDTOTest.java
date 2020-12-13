package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.domain.enumeration.Language;
import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CodeDTOTest {
    @Test
    public void switchGetterAndSetterCodeTest(){
        CodeDTO codeDTO = new CodeDTO();
        for (Language lang : Language.values()) {
            String uuid1 = UUID.randomUUID().toString();
            String uuid2 = UUID.randomUUID().toString();

            if( lang.equals(Language.UNKNOWN))
                continue;

            codeDTO.setTitleDefinition(uuid1, uuid2, lang.getIso(), false);
            assertThat(codeDTO.getTitleByLanguage(lang.getIso())).isEqualTo(uuid1);
            assertThat(codeDTO.getDefinitionByLanguage(lang.getIso())).isEqualTo(uuid2);
            codeDTO.setTitleDefinition(uuid1, null, lang.getIso(), true);
            assertThat(codeDTO.getTitleByLanguage(lang.getIso())).isEqualTo(uuid1);
            assertThat(codeDTO.getDefinitionByLanguage(lang.getIso())).isEqualTo(uuid2);
        }
        assertThat(codeDTO.getLanguages().size()).isEqualTo(Language.values().length - 1);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CodeDTO.class);
        CodeDTO codeDTO1 = new CodeDTO();
        codeDTO1.setId(1L);
        CodeDTO codeDTO2 = new CodeDTO();
        assertThat(codeDTO1).isNotEqualTo(codeDTO2);
        codeDTO2.setId(codeDTO1.getId());
        assertThat(codeDTO1).isEqualTo(codeDTO2);
        codeDTO2.setId(2L);
        assertThat(codeDTO1).isNotEqualTo(codeDTO2);
        codeDTO1.setId(null);
        assertThat(codeDTO1).isNotEqualTo(codeDTO2);
    }
}
