package eu.cessda.cvs.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class VocabularyChangeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyChange.class);
        VocabularyChange vocabularyChange1 = new VocabularyChange();
        vocabularyChange1.setId(1L);
        VocabularyChange vocabularyChange2 = new VocabularyChange();
        vocabularyChange2.setId(vocabularyChange1.getId());
        assertThat(vocabularyChange1).isEqualTo(vocabularyChange2);
        vocabularyChange2.setId(2L);
        assertThat(vocabularyChange1).isNotEqualTo(vocabularyChange2);
        vocabularyChange1.setId(null);
        assertThat(vocabularyChange1).isNotEqualTo(vocabularyChange2);
    }
}
