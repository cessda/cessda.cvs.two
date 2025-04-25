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

public class AgencyTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Agency.class);
        Agency agency1 = new Agency();
        agency1.setId(1L);
        Agency agency2 = new Agency();
        agency2.setId(agency1.getId());
        assertThat(agency1).isEqualTo(agency2);
        agency2.setId(2L);
        assertThat(agency1).isNotEqualTo(agency2);
        agency1.setId(null);
        assertThat(agency1).isNotEqualTo(agency2);
    }
}
