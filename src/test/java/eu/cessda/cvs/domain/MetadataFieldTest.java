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

public class MetadataFieldTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetadataField.class);
        MetadataField metadataField1 = new MetadataField();
        metadataField1.setId(1L);
        MetadataField metadataField2 = new MetadataField();
        metadataField2.setId(metadataField1.getId());
        assertThat(metadataField1).isEqualTo(metadataField2);
        metadataField2.setId(2L);
        assertThat(metadataField1).isNotEqualTo(metadataField2);
        metadataField1.setId(null);
        assertThat(metadataField1).isNotEqualTo(metadataField2);
    }
}
