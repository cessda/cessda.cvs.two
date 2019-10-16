package eu.cessda.cvmanager.export.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class SaxParserUtils {

	private static final Logger log = LoggerFactory.getLogger(SaxParserUtils.class);

	private SaxParserUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String filterSkosDoc(Set<String> filteredTag, Set<String> filteredLanguage, String xmlString) {
		XMLReader xr;
		try {
			xr = xmlReader(filteredTag, filteredLanguage);

			Source src = new SAXSource(xr, new InputSource(new StringReader(xmlString)));
			StringWriter sw = new StringWriter();
			Result res = new StreamResult(sw);
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				Transformer transformer = factory.newTransformer();

				transformer.transform(src, res);
				return sw.toString();
			} catch (TransformerException | TransformerFactoryConfigurationError e) {
				log.error(e.getMessage(), e);
			}
		} catch (SAXException e) {

			log.error(e.getMessage(), e);
		}
		return null;
	}

	private static XMLReader xmlReader(Set<String> filteredTag, Set<String> filteredLanguage) throws SAXException {
		XMLReader xr;
		XMLReader xmlReaderfactory = XMLReaderFactory.createXMLReader();
		xmlReaderfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		xr = new XMLFilterImpl(xmlReaderfactory) {
			private boolean skip;

			@Override
			public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
				// filter tag
				if (filteredTag != null && filteredTag.contains(qName))
					skip = true;
				else
					skip = false;

				// filter language attribute
				if (!skip && filteredLanguage != null) {
					String langValue = atts.getValue("xml:lang");
					if (langValue != null && filteredLanguage.contains(langValue))
						skip = true;
				}

				if (!skip)
					super.startElement(uri, localName, qName, atts);

			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (filteredTag != null && filteredTag.contains(qName))
					skip = true;

				if (!skip)
					super.endElement(uri, localName, qName);
				skip = false;
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if (!skip) {
					super.characters(ch, start, length);
				}
			}
		};
		return xr;
	}
}
