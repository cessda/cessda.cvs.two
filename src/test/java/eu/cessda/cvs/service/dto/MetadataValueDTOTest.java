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

public class MetadataValueDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetadataValueDTO.class);
        MetadataValueDTO metadataValueDTO1 = new MetadataValueDTO();
        metadataValueDTO1.setId(1L);
        MetadataValueDTO metadataValueDTO2 = new MetadataValueDTO();
        assertThat(metadataValueDTO1).isNotEqualTo(metadataValueDTO2);
        metadataValueDTO2.setId(metadataValueDTO1.getId());
        assertThat(metadataValueDTO1).isEqualTo(metadataValueDTO2);
        metadataValueDTO2.setId(2L);
        assertThat(metadataValueDTO1).isNotEqualTo(metadataValueDTO2);
        metadataValueDTO1.setId(null);
        assertThat(metadataValueDTO1).isNotEqualTo(metadataValueDTO2);
    }
}
