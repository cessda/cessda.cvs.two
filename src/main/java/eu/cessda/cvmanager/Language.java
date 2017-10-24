package eu.cessda.cvmanager;

import java.util.ArrayList;
import java.util.List;

public enum Language {
	ENGLISH("en"),
	FINNISH("es"),
	FRENCH("fr"),
	GERMAN("de");
	
	private final String resourceType;
	
	private Language(String s){
		resourceType = s;
	}
		
	public String toString() {
       return this.resourceType;
    }
	
    public String getResourceType() {
        return resourceType;
    }
	
	public static Language getEnum(String resourceTypeString) {
        for(Language v : values())
            if(v.getResourceType().equalsIgnoreCase(resourceTypeString)) return v;
        throw new IllegalArgumentException();
    }
	
	public static List<String> getAllEnumCapitalized(){
		List<String> languages = new ArrayList<>();
		for(Language v : values()) {
			languages.add( v.name().substring(0, 1).toUpperCase() + v.name().substring(1));
		}
		return languages;
	}
}
