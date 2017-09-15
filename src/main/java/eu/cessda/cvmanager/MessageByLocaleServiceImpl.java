package eu.cessda.cvmanager;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageByLocaleServiceImpl implements MessageByLocaleService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public String getMessage(String id) {
		Locale locale = LocaleContextHolder.getLocale();
		// locale = Locale.GERMAN;
		String message = "mssing localisation";
		try {
			message = messageSource.getMessage(id, null, locale);
		} catch (NoSuchMessageException nsme) {
			nsme.printStackTrace();
		}
		return message;
	}

	@Override
	public void setLocal(Locale locale) {
		LocaleContextHolder.setLocale(locale);
		System.out.println("Activated:LOCAL");
	}

	@Override
	public void getLocalContextHolder() {

	}

	@Override
	public Locale getLocal() {
		return LocaleContextHolder.getLocale();

	}

}
