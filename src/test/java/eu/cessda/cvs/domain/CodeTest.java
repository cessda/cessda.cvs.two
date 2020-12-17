package eu.cessda.cvs.domain;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Code.class);
        Code code1 = new Code();
        code1.setId(1L);
        Code code2 = new Code();
        code2.setId(code1.getId());
        assertThat(code1).isEqualTo(code2);
        code2.setId(2L);
        assertThat(code1).isNotEqualTo(code2);
        code1.setId(null);
        assertThat(code1).isNotEqualTo(code2);
    }
}
