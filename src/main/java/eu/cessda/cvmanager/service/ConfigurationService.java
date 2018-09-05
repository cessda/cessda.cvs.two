package eu.cessda.cvmanager.service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.vaadin.ui.UI;

@Service
public class ConfigurationService {
	public static final String SOURCE_LANGUAGE = "en";
	public static final String DEFAULT_CV_LINK = "http://www.example.com/Specification/CV/";
	
	@Autowired
	private Environment env;

	@Value("${server.servlet.context-path}")
	private String serverContextPath;
	
	@Value("${ddiflatdb.rest.url}")
	private String ddiflatdbRestUrl;

	public String getServerContextPath() {
		return serverContextPath;
	}

	public String getServerBaseUrl() {
		String baseUrl = UI.getCurrent().getPage().getLocation().getHost();
		int port = UI.getCurrent().getPage().getLocation().getPort();

		return port > 0 ? baseUrl + ":" + port : baseUrl;
	}

	public String getDdiflatdbRestUrl() {
		return ddiflatdbRestUrl;
	}

	public void setDdiflatdbRestUrl(String ddiflatdbRestUrl) {
		this.ddiflatdbRestUrl = ddiflatdbRestUrl;
	}

	public String getDefaultSourceLanguage() {
		return SOURCE_LANGUAGE;
	}

	public Optional<String> getPropertyByKey(String key) {
		return Optional.of(env.getProperty(key));
	}
	
	public Optional<List<String>> getPropertyByKeyAsList(String key, String splitBy) {
		if (getPropertyByKey(key).isPresent())
			return Optional.of(
					Stream.of( getPropertyByKey(key).get().split(splitBy))
				    .collect(Collectors.toList()));
		else
			return Optional.empty();
	}
	
	public Optional<Set<String>> getPropertyByKeyAsSet(String key, String splitBy) {
		return Optional.of( new LinkedHashSet<>( getPropertyByKeyAsList(key, splitBy).isPresent() ? getPropertyByKeyAsList(key, splitBy).get() : Collections.emptyList()));
	}
}
