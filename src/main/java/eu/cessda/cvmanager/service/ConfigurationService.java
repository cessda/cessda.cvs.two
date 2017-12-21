package eu.cessda.cvmanager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.vaadin.ui.UI;

@Service
public class ConfigurationService {
	private final String SOURCE_LANGUAGE = "en";
	
	@Autowired
	private Environment env;

	@Value("${server.contextPath}")
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

	public String getPropertyByKey(String key) {
		return env.getProperty(key);
	}
	
	public List<String> getPropertyByKeyAsList(String key, String splitBy) {
		List<String> valueList = new ArrayList<>();
		String values = env.getProperty(key);
		if( values == null ) 
			return Collections.emptyList();
		
		valueList.addAll(  Arrays.asList(values.split(splitBy)) );
		return valueList;
	}
	
	public Set<String> getPropertyByKeyAsSet(String key, String splitBy) {
		return new LinkedHashSet<>( getPropertyByKeyAsList(key, splitBy));
	}
}
