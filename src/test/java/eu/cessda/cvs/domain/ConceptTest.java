package eu.cessda.cvs.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class ConceptTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Concept.class);
        Concept concept1 = new Concept();
        concept1.setId(1L);
        Concept concept2 = new Concept();
        concept2.setId(concept1.getId());
        assertThat(concept1).isEqualTo(concept2);
        concept2.setId(2L);
        assertThat(concept1).isNotEqualTo(concept2);
        concept1.setId(null);
        assertThat(concept1).isNotEqualTo(concept2);
    }
}
