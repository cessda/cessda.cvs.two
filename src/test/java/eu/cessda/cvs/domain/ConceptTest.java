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
