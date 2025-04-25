/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
