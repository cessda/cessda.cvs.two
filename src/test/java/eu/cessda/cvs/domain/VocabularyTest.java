/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

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
