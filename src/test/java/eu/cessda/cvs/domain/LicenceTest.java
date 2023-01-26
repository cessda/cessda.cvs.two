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

public class LicenceTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Licence.class);
        Licence licence1 = new Licence();
        licence1.setId(1L);
        Licence licence2 = new Licence();
        licence2.setId(licence1.getId());
        assertThat(licence1).isEqualTo(licence2);
        licence2.setId(2L);
        assertThat(licence1).isNotEqualTo(licence2);
        licence1.setId(null);
        assertThat(licence1).isNotEqualTo(licence2);
    }
}
