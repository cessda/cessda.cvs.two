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
package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConceptDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConceptDTO.class);
        ConceptDTO conceptDTO1 = new ConceptDTO();
        conceptDTO1.setId(1L);
        ConceptDTO conceptDTO2 = new ConceptDTO();
        assertThat(conceptDTO1).isNotEqualTo(conceptDTO2);
        conceptDTO2.setId(conceptDTO1.getId());
        assertThat(conceptDTO1).isEqualTo(conceptDTO2);
        conceptDTO2.setId(2L);
        assertThat(conceptDTO1).isNotEqualTo(conceptDTO2);
        conceptDTO1.setId(null);
        assertThat(conceptDTO1).isNotEqualTo(conceptDTO2);
    }
}
