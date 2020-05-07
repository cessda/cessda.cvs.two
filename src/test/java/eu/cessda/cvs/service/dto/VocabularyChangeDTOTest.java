package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class VocabularyChangeDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyChangeDTO.class);
        VocabularyChangeDTO vocabularyChangeDTO1 = new VocabularyChangeDTO();
        vocabularyChangeDTO1.setId(1L);
        VocabularyChangeDTO vocabularyChangeDTO2 = new VocabularyChangeDTO();
        assertThat(vocabularyChangeDTO1).isNotEqualTo(vocabularyChangeDTO2);
        vocabularyChangeDTO2.setId(vocabularyChangeDTO1.getId());
        assertThat(vocabularyChangeDTO1).isEqualTo(vocabularyChangeDTO2);
        vocabularyChangeDTO2.setId(2L);
        assertThat(vocabularyChangeDTO1).isNotEqualTo(vocabularyChangeDTO2);
        vocabularyChangeDTO1.setId(null);
        assertThat(vocabularyChangeDTO1).isNotEqualTo(vocabularyChangeDTO2);
    }
}
