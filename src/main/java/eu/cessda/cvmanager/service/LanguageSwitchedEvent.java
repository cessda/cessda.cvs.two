/**
 * 
 */
package eu.cessda.cvmanager.service;

import java.io.Serializable;
import java.util.Locale;

/**
 * This is an Object Event Class. Purpose is the transmitting of a/ (this)
 * specified Event via EventBus to the classes which have subscribed to the
 * EventBus.
 * 
 * Methods which invoke this event are implemented in the subscribed classes.
 * 
 * @author abrafiea
 *
 */
public class LanguageSwitchedEvent implements Serializable {

	private String webLanguage;

	/**
	 * Accessing Locale, please Review for privacy purpose. if mor favorable to
	 * use String
	 */
	private Locale Locale;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LanguageSwitchedEvent() {

	}

	public LanguageSwitchedEvent(String webLanguage) {
		setWebLanguage(webLanguage);

	}

	public LanguageSwitchedEvent(Locale webLanguage2) {
		setGlobalLanguage(webLanguage2);
	}

	public String getWebLanguage() {
		return webLanguage;
	}

	public void setWebLanguage(String webLanguage) {
		this.webLanguage = webLanguage;
	}

	public Locale getLocale() {
		return Locale;
	}

	public void setGlobalLanguage(Locale locale) {
		this.Locale = locale;
	}

}
