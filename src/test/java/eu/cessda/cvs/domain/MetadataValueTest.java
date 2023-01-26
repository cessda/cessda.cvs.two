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

public class MetadataValueTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetadataValue.class);
        MetadataValue metadataValue1 = new MetadataValue();
        metadataValue1.setId(1L);
        MetadataValue metadataValue2 = new MetadataValue();
        metadataValue2.setId(metadataValue1.getId());
        assertThat(metadataValue1).isEqualTo(metadataValue2);
        metadataValue2.setId(2L);
        assertThat(metadataValue1).isNotEqualTo(metadataValue2);
        metadataValue1.setId(null);
        assertThat(metadataValue1).isNotEqualTo(metadataValue2);
    }
}
