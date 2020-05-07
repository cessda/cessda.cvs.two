package eu.cessda.cvs.domain.enumeration;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Language enumeration.
 */
public enum Language {
    ALBANIAN( "sq", "sq" , "Albanian (sq)", "Albanian"),
    BOSNIAN( "bs", "bs" , "Bosnian (bs)", "Bosnian"),
    BULGARIAN( "bg", "bg" , "Bulgarian (bg)", "Bulgarian"),
    CROATIAN( "hr", "hr" , "Croatian (hr)", "Croatian"),
    CZECH( "cs", "cs" , "Czech (cs)", "Czech"),
    DANISH( "da", "da" , "Danish (da)", "Danish"),
    DUTCH( "nl", "nl" , "Dutch (nl)", "Dutch"),
    ENGLISH( "en", "en" , "English (en)", "English"),
    ESTONIAN( "et", "et" , "Estonian (et)", "Estonian"),
    FINNISH( "fi", "fi" , "Finnish (fi)", "Finnish"),
    FRENCH( "fr", "fr" , "French (fr)", "French"),
    GERMAN( "de", "de" , "German (de)", "German"),
    GREEK( "el", "el" , "Greek (el)", "Greek"),
    HUNGARIAN( "hu", "hu" , "Hungarian (hu)", "Hungarian"),
    ITALIAN( "it", "it" , "Italian (it)", "Italian"),
    JAPANESE( "ja", "ja" , "Japanese (ja)", "Japanese"),
    LITHUANIAN( "lt", "lt" , "Lithuanian (lt)", "Lithuanian"),
    MACEDONIAN( "mk", "mk" , "Macedonian (mk)", "Macedonian"),
    NORWEGIAN( "no", "no" , "Norwegian (no)", "Norwegian"),
    POLISH( "pl", "pl" , "Polish (pl)", "Polish"),
    PORTUGUESE( "pt", "pt" , "Portuguese (pt)", "Portuguese"),
    ROMANIAN( "ro", "ro" , "Romanian (ro)", "Romanian"),
    RUSSIAN( "ru", "ru" , "Russian (ru)", "Russian"),
    SERBIAN( "sr", "sr" , "Serbian (sr)", "Serbian"),
    SLOVAK( "sk", "sk" , "Slovak (sk)", "Slovak"),
    SLOVENIAN( "sl", "sl" , "Slovenian (sl)", "Slovenian"),
    SPANISH( "es", "es" , "Spanish (es)", "Spanish"),
    SWEDISH( "sv", "sv" , "Swedish (sv)", "Swedish");

    private final String iso;
    private final String iso3;
    private final String formatted;
    private final String notes;

    private static Map<String, Language> map;

    static {
        Map<String, Language> langMap = new HashMap<>();
        for (Language lang : Language.values()) {
            langMap.put(lang.iso, lang);
        }
        map = Collections.unmodifiableMap(langMap);
    }

    Language(final String iso, final String iso3, final String formatted, final String notes){
        this.iso = iso;
        this.iso3 = iso3;
        this.formatted = formatted;
        this.notes = notes;
    }

    public String getIso() {
        return iso;
    }

    public String getIso3() {
        return iso3;
    }

    public String getFormatted() {
        return formatted;
    }

    public String getNotes() {
        return notes;
    }

    public static Map<String, Language> getMap() {
        return map;
    }

    public static List<Language> getFilteredLanguage(Set<Language> userLanguages, Set<String> filterLanguages){
        userLanguages.removeAll(getLanguagesFromLanguageSet(filterLanguages));

        return userLanguages
            .stream()
            .sorted( Comparator.comparing(Enum::name) )
            .collect( Collectors.toList());
    }

    public static Set<Language> getLanguagesFromLanguageSet(Set<String> langs){
        Set<Language> languages = new HashSet<>();
        for( String lang : langs)
            languages.add( map.get(lang));
        return languages;
    }

    public static Set<String> getCapitalizedIsos(){
        return map.keySet().stream().map(StringUtils::capitalize).collect(Collectors.toSet());
    }

    public static Set<String> getIsos(){
        return new HashSet<>(map.keySet());
    }

    public static Language getByIso(String iso ){
        if( iso.length() > 2)
            return null;
        return map.get(iso.toLowerCase());
    }
}
