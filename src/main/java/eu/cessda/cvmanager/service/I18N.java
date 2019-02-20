package eu.cessda.cvmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class I18N {
	private I18N() { }
	
	private static org.vaadin.spring.i18n.I18N i18n;
	
	@Autowired
	private void setI18n(org.vaadin.spring.i18n.I18N i18n){
		I18N.i18n = i18n;
	}
	
	public static String get( String messageKey, String... arguments ) {
		return i18n.get(messageKey, arguments);
	}
	
//	public static String get( String messageKey) {
//		return get(messageKey, new String[0]);
//	}
}
