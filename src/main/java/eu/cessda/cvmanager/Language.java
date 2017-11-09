package eu.cessda.cvmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum Language {
	DEUTCH("nl"),
	ENGLISH("en"),
	FINNISH("fi"),
	FRENCH("fr"),
	GERMAN("de"),
	GREEK("el"),
	NORWEGIAN("no"),
	RUSSIAN("ru"),
	SPANISH("es"),
	SWEDISH("sv");
		
	private final String language;
	
	private Language(String s){
		language = s;
	}
		
	public String toString() {
       return this.language;
    }
	
    public String getLanguage() {
        return language;
    }
	
	public static Language getEnum(String resourceTypeString) {
        for(Language v : values())
            if(v.getLanguage().equalsIgnoreCase(resourceTypeString)) return v;
        throw new IllegalArgumentException();
    }
	
	public static List<String> getAllEnumCapitalized(){
		List<String> languages = new ArrayList<>();
		for(Language v : values()) {
			languages.add( v.name().substring(0, 1) + v.name().substring(1).toLowerCase());
		}
		return languages;
	}
	
	public static List<String> getFilteredEnumCapitalized( Set<String> enumValue ){
		List<String> languages = new ArrayList<>();
		for(Language v : values()) {
			if( enumValue.contains( v.toString()))
			languages.add( v.name().substring(0, 1) + v.name().substring(1).toLowerCase());
		}
		return languages;
	}
	
	public static List<String> getAllEnumValue(){
		List<String> languages = new ArrayList<>();
		for(Language v : values()) {
			languages.add( v.toString());
		}
		return languages;
	}
}
