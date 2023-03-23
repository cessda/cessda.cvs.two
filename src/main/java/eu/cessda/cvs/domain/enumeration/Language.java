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
    SLOVENIAN("sl", "sl", "Slovenian (sl)", "Slovenian"),
    SPANISH("es", "es", "Spanish (es)", "Spanish"),
    SWEDISH("sv", "sv", "Swedish (sv)", "Swedish"),
    UNKNOWN("-", "-", "Unknown (-)", "Unknown");

    private final String iso;
    private final String iso3;
    private final String formatted;
    private final String notes;

    private static final Map<String, Language> LANGUAGE_MAP;
    private static final Set<String> CAPITALIZED_ISO_SET;

    static {
        // Create a map
        Map<String, Language> langMap = new HashMap<>();
        for (Language lang : Language.values()) {
            langMap.put(lang.iso, lang);
        }
        LANGUAGE_MAP = Collections.unmodifiableMap(langMap);

        HashSet<String> set = new HashSet<>();
        for (String lang : LANGUAGE_MAP.keySet()) {
            set.add(StringUtils.capitalize(lang));
        }
        CAPITALIZED_ISO_SET = Collections.unmodifiableSet(set);
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
        return LANGUAGE_MAP;
    }

    public static List<Language> getFilteredLanguage(Set<Language> userLanguages, Set<String> filterLanguages){
        userLanguages.removeAll(getLanguagesFromLanguageSet(filterLanguages));

        return userLanguages
            .stream()
            .sorted( Comparator.comparing(Enum::name) )
            .collect( Collectors.toList());
    }

    public static Set<Language> getLanguagesFromLanguageSet(Set<String> langs) {
        Set<Language> languages = EnumSet.noneOf(Language.class);
        for (String lang : langs) {
            languages.add(LANGUAGE_MAP.get(lang));
        }
        return languages;
    }

    public static Set<String> getCapitalizedIsos(){
        return CAPITALIZED_ISO_SET;
    }

    public static Set<String> getIsos(){
        return new HashSet<>(LANGUAGE_MAP.keySet());
    }

    public static Language getByIso(String iso ){
        return LANGUAGE_MAP.get(iso.toLowerCase());
    }
}
