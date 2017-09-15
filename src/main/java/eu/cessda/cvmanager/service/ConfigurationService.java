package eu.cessda.cvmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vaadin.ui.UI;

@Service
public class ConfigurationService {

	@Value("${server.contextPath}")
	private String serverContextPath;

	public String getServerContextPath() {
		return serverContextPath;
	}

	public String getServerBaseUrl() {
		String baseUrl = UI.getCurrent().getPage().getLocation().getHost();
		int port = UI.getCurrent().getPage().getLocation().getPort();

		return port > 0 ? baseUrl + ":" + port : baseUrl;
	}

}
