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
