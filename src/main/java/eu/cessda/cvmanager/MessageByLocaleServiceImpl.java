package eu.cessda.cvmanager;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageByLocaleServiceImpl implements MessageByLocaleService {

	final static Logger log = LoggerFactory.getLogger(MessageByLocaleServiceImpl.class);

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
			log.error("No such message found", nsme);
		}
		return message;
	}

	@Override
	public void setLocal(Locale locale) {
		LocaleContextHolder.setLocale(locale);
		log.info("Activated:LOCAL");
	}

	@Override
	public void getLocalContextHolder() {

	}

	@Override
	public Locale getLocal() {
		return LocaleContextHolder.getLocale();

	}

}
