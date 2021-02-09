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

package eu.cessda.cvs.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VocabularyUtilsTest {

    @Test
    void generateUri() {
        final String versionUri = "https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]/[VERSION]/[LANGUAGE]";
        final String codeUri = "https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]_[CODE]/[VERSION]/[LANGUAGE]";
        final String versionUri2 = "https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]/[VERSION]";
        final String codeUri2 = "https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]_[CODE]/[VERSION]";
        final String notation = "TopicClassification";
        final String language = "en";
        final String version = "1.0";
        final String code = "Demography";
        // vocabulary
        final String uri1 = VocabularyUtils.generateUri(versionUri, null, notation, null, language, null);
        assertThat( uri1).isEqualTo("https://vocabularies.cessda.eu/vocabulary/%s", notation);
        final String uri2 = VocabularyUtils.generateUri(versionUri2, null, notation, null, null, null);
        assertThat( uri2).isEqualTo("https://vocabularies.cessda.eu/vocabulary/%s", notation);
        // version
        final String uri3 = VocabularyUtils.generateUri(versionUri, true, notation, version, language, null);
        assertThat( uri3).isEqualTo("https://vocabularies.cessda.eu/vocabulary/%s/%s/%s", notation, version, language);
        final String uri4 = VocabularyUtils.generateUri(versionUri2, true, notation, version, null, null);
        assertThat( uri4).isEqualTo("https://vocabularies.cessda.eu/vocabulary/%s/%s", notation, version);
        // version
        final String uri5 = VocabularyUtils.generateUri(codeUri, false, notation, version, language, code);
        assertThat( uri5).isEqualTo("https://vocabularies.cessda.eu/vocabulary/%s_%s/%s/%s", notation, code, version, language);
        final String uri6 = VocabularyUtils.generateUri(codeUri2, false, notation, version, null, code);
        assertThat( uri6).isEqualTo("https://vocabularies.cessda.eu/vocabulary/%s_%s/%s", notation, code, version);
    }
}
