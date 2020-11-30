package eu.cessda.cvs.domain;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeSnippetTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CodeSnippet.class);
        CodeSnippet codeSnippet1 = new CodeSnippet();
        codeSnippet1.setVersionId(1L);
        CodeSnippet codeSnippet2 = new CodeSnippet();
        codeSnippet2.setVersionId(codeSnippet1.getVersionId());
        assertThat(codeSnippet1).isEqualTo(codeSnippet2);
        codeSnippet2.setVersionId(2L);
        assertThat(codeSnippet1).isNotEqualTo(codeSnippet2);
        codeSnippet1.setVersionId(null);
        assertThat(codeSnippet1).isNotEqualTo(codeSnippet2);
    }
}
