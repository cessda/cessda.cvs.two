package eu.cessda.cvs.domain;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VocabularySnippetSnippetTest {
    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularySnippet.class);
        VocabularySnippet vocabularySnippet1 = new VocabularySnippet();
        vocabularySnippet1.setVocabularyId(1L);
        VocabularySnippet vocabularySnippet2 = new VocabularySnippet();
        vocabularySnippet2.setVocabularyId(vocabularySnippet1.getVocabularyId());
        assertThat(vocabularySnippet1).isEqualTo(vocabularySnippet2);
        vocabularySnippet2.setVocabularyId(2L);
        assertThat(vocabularySnippet1).isNotEqualTo(vocabularySnippet2);
        vocabularySnippet1.setVocabularyId(null);
        assertThat(vocabularySnippet1).isNotEqualTo(vocabularySnippet2);
    }
}
