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

public class VocabularyChangeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyChange.class);
        VocabularyChange vocabularyChange1 = new VocabularyChange();
        vocabularyChange1.setId(1L);
        VocabularyChange vocabularyChange2 = new VocabularyChange();
        vocabularyChange2.setId(vocabularyChange1.getId());
        assertThat(vocabularyChange1).isEqualTo(vocabularyChange2);
        vocabularyChange2.setId(2L);
        assertThat(vocabularyChange1).isNotEqualTo(vocabularyChange2);
        vocabularyChange1.setId(null);
        assertThat(vocabularyChange1).isNotEqualTo(vocabularyChange2);
    }
}
