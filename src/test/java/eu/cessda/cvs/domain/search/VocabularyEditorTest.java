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
