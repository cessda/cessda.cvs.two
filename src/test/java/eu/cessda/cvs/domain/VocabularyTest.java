package eu.cessda.cvs.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class VocabularyTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vocabulary.class);
        Vocabulary vocabulary1 = new Vocabulary();
        vocabulary1.setId(1L);
        Vocabulary vocabulary2 = new Vocabulary();
        vocabulary2.setId(vocabulary1.getId());
        assertThat(vocabulary1).isEqualTo(vocabulary2);
        vocabulary2.setId(2L);
        assertThat(vocabulary1).isNotEqualTo(vocabulary2);
        vocabulary1.setId(null);
        assertThat(vocabulary1).isNotEqualTo(vocabulary2);
    }
}
