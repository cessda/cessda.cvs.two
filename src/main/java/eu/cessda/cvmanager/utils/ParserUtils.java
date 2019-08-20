package eu.cessda.cvmanager.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParserUtils {
	private ParserUtils() {}
	public static String toXHTML( String html ) {
	    final Document document = Jsoup.parse(html);
	    document.select("script,.hidden,link").remove();
	    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
	    return document.body().html();
	}
}
