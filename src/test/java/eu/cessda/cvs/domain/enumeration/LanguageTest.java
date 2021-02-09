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

package eu.cessda.cvs.domain.enumeration;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LanguageTest {

    @Test
    public void enumMethodsTest(){
        Language enLanguage = Language.getByIso("en");
        assertThat(enLanguage).isNotNull();
        assertThat(enLanguage.getIso()).isEqualTo("en");
        assertThat(enLanguage.getIso3()).isEqualTo("en");
        assertThat(enLanguage.getFormatted()).isEqualTo("English (en)");
        assertThat(enLanguage.getNotes()).isEqualTo("English");

        String langToBeFiltered = "en";
        Set<String> langFiltered = new HashSet<>();
        langFiltered.add(langToBeFiltered);
        List<Language> filteredLanguage = Language.getFilteredLanguage( new HashSet<>( Language.getMap().values()), langFiltered);
        assertThat(filteredLanguage.size()).isEqualTo( Language.values().length - 1);

        Set<String> capitalizedIsos = Language.getCapitalizedIsos();
        assertThat(capitalizedIsos.size()).isEqualTo(Language.values().length );
        assertThat(capitalizedIsos).contains("En");

        Set<String> isos = Language.getIsos();
        assertThat(isos.size()).isEqualTo(Language.values().length );
        assertThat(isos).contains("en");

        Language enLang = Language.getByIso("en");
        assertThat(enLang).isNotNull();
    }
}
