package eu.cessda.cvs.domain.search;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VocabularyEditorTest {
    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyEditor.class);
        VocabularyEditor vocabularyEditor1 = new VocabularyEditor();
        vocabularyEditor1.setId(1L);
        VocabularyEditor vocabularyEditor2 = new VocabularyEditor();
        vocabularyEditor2.setId(vocabularyEditor1.getId());
        assertThat(vocabularyEditor1).isEqualTo(vocabularyEditor2);
        vocabularyEditor2.setId(2L);
        assertThat(vocabularyEditor1).isNotEqualTo(vocabularyEditor2);
        vocabularyEditor1.setId(null);
        assertThat(vocabularyEditor1).isNotEqualTo(vocabularyEditor2);
    }
}
