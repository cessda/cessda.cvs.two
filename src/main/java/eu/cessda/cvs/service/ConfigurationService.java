package eu.cessda.cvs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConfigurationService {
	public static final String SOURCE_LANGUAGE = "en";
	public static final String DEFAULT_CV_LINK = "http://www.example.com/Specification/CV/";

	@Autowired
	private Environment env;

	public String getDefaultSourceLanguage() {
		return SOURCE_LANGUAGE;
	}

	public List<String> getPropertyByKeyAsList(String key, String splitBy) {
		if( env.getProperty(key) == null )
			return Collections.emptyList();
		return Arrays.asList( env.getProperty(key).split(splitBy) );
	}

	public Optional<Set<String>> getPropertyByKeyAsSet(String key, String splitBy) {
		return Optional.of( new LinkedHashSet<>( getPropertyByKeyAsList(key, splitBy)));
	}
}
