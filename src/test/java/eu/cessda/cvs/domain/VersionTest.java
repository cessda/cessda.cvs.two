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

public class VersionTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Version.class);
        Version version1 = new Version();
        version1.setId(1L);
        Version version2 = new Version();
        version2.setId(version1.getId());
        assertThat(version1).isEqualTo(version2);
        version2.setId(2L);
        assertThat(version1).isNotEqualTo(version2);
        version1.setId(null);
        assertThat(version1).isNotEqualTo(version2);
    }
}
