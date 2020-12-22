package eu.cessda.cvs.domain;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VocabularyBaseTest {
    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyBase.class);
        VocabularyBase vocabularyBase1 = new VocabularyBase();
        vocabularyBase1.setId(1L);
        VocabularyBase vocabularyBase2 = new VocabularyBase();
        vocabularyBase2.setId(vocabularyBase1.getId());
        assertThat(vocabularyBase1).isEqualTo(vocabularyBase2);
        vocabularyBase2.setId(2L);
        assertThat(vocabularyBase1).isNotEqualTo(vocabularyBase2);
        vocabularyBase1.setId(null);
        assertThat(vocabularyBase1).isNotEqualTo(vocabularyBase2);
    }
}

