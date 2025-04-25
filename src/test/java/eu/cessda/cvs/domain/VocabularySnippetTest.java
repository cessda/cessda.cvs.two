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
