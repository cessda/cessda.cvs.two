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

package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class VocabularyChangeDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VocabularyChangeDTO.class);
        VocabularyChangeDTO vocabularyChangeDTO1 = new VocabularyChangeDTO();
        vocabularyChangeDTO1.setId(1L);
        VocabularyChangeDTO vocabularyChangeDTO2 = new VocabularyChangeDTO();
        assertThat(vocabularyChangeDTO1).isNotEqualTo(vocabularyChangeDTO2);
        vocabularyChangeDTO2.setId(vocabularyChangeDTO1.getId());
        assertThat(vocabularyChangeDTO1).isEqualTo(vocabularyChangeDTO2);
        vocabularyChangeDTO2.setId(2L);
        assertThat(vocabularyChangeDTO1).isNotEqualTo(vocabularyChangeDTO2);
        vocabularyChangeDTO1.setId(null);
        assertThat(vocabularyChangeDTO1).isNotEqualTo(vocabularyChangeDTO2);
    }
}
