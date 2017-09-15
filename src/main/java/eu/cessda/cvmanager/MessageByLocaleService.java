package eu.cessda.cvmanager;

import java.util.Locale;

public interface MessageByLocaleService {

	public String getMessage(String id);

	public void setLocal(Locale locale);

	public Locale getLocal();

	public void getLocalContextHolder();
}