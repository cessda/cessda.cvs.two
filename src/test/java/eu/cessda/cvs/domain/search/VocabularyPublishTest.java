package eu.cessda.cvs.domain.search;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VocabularyPublishTest {
    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyPublish.class);
        VocabularyPublish vocabularyPublish1 = new VocabularyPublish();
        vocabularyPublish1.setId(1L);
        VocabularyPublish vocabularyPublish2 = new VocabularyPublish();
        vocabularyPublish2.setId(vocabularyPublish1.getId());
        assertThat(vocabularyPublish1).isEqualTo(vocabularyPublish2);
        vocabularyPublish2.setId(2L);
        assertThat(vocabularyPublish1).isNotEqualTo(vocabularyPublish2);
        vocabularyPublish1.setId(null);
        assertThat(vocabularyPublish1).isNotEqualTo(vocabularyPublish2);
    }
}

