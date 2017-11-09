package eu.cessda.cvmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vaadin.ui.UI;

@Service
public class ConfigurationService {
	private final String SOURCE_LANGUAGE = "en";

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

	public String getSourceLanguage() {
		return SOURCE_LANGUAGE;
	}

	
}
