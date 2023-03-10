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

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VersionDTO.class);
        VersionDTO versionDTO1 = new VersionDTO();
        versionDTO1.setId(1L);
        VersionDTO versionDTO2 = new VersionDTO();
        assertThat(versionDTO1).isNotEqualTo(versionDTO2);
        versionDTO2.setId(versionDTO1.getId());
        assertThat(versionDTO1).isEqualTo(versionDTO2);
        versionDTO2.setId(2L);
        assertThat(versionDTO1).isNotEqualTo(versionDTO2);
        versionDTO1.setId(null);
        assertThat(versionDTO1).isNotEqualTo(versionDTO2);
    }

    private Method getRemoveLanguageInformationMethod() throws NoSuchMethodException {
        Method method = VersionDTO.class.getDeclaredMethod("removeLanguageInformation", String.class);
        method.setAccessible(true);
        return method;
    }

    @Test
    void removeLanguageInformationTest() throws Exception {
        assertThat(getRemoveLanguageInformationMethod().invoke(null, "urn:ddi:int.ddi.cv:398-TEST-03:1.0")).isEqualTo("urn:ddi:int.ddi.cv:398-TEST-03:1.0");
        assertThat(getRemoveLanguageInformationMethod().invoke(null, "urn:ddi:int.ddi.cv:398-TEST-03:1.0.0")).isEqualTo("urn:ddi:int.ddi.cv:398-TEST-03:1.0.0");
        assertThat(getRemoveLanguageInformationMethod().invoke(null, "urn:ddi:int.ddi.cv:398-TEST-03:en-1.0")).isEqualTo("urn:ddi:int.ddi.cv:398-TEST-03:1.0");
        assertThat(getRemoveLanguageInformationMethod().invoke(null, "urn:ddi:int.ddi.cv:398-TEST-03:en-1.0.0")).isEqualTo("urn:ddi:int.ddi.cv:398-TEST-03:1.0.0");
    }
}
