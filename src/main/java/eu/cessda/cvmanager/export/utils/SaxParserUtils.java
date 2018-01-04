package eu.cessda.cvmanager.export.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class SaxParserUtils {
	public static String filterSkosDoc(Set<String> filteredTag, Set<String> filteredLanguage, String xmlString){
		XMLReader xr;
		try {
			xr = new XMLFilterImpl(XMLReaderFactory.createXMLReader()) {
			    private boolean skip;

			    @Override
			    public void startElement(String uri, String localName, String qName, Attributes atts)
			            throws SAXException {
			    	// filter tag
			        if ( filteredTag != null && filteredTag.contains( qName ))
			        	skip = true;
			        else
			        	skip = false;
			        
			        // filter language attribute
			        if( !skip && filteredLanguage != null) {
			        	String langValue =  atts.getValue("xml:lang");
			        	if( langValue != null && filteredLanguage.contains(langValue))
			        		skip = true;
			        }

		            if (!skip)
		                super.startElement(uri, localName, qName, atts);
		        
			    }

			    public void endElement(String uri, String localName, String qName) throws SAXException {
			    	if ( filteredTag != null && filteredTag.contains( qName )) {
			        	skip = true;
			        } 
			    	
			       if (!skip) {
			            super.endElement(uri, localName, qName);
			       }
			    }

			    @Override
			    public void characters(char[] ch, int start, int length) throws SAXException {
			        if (!skip) {
			            super.characters(ch, start, length);
			       }
			    }
			};
			Source src = new SAXSource(xr, new InputSource( new StringReader(xmlString)));
			StringWriter sw  =new StringWriter();
		    Result res = new StreamResult( sw );
		    try {
				TransformerFactory.newInstance().newTransformer().transform(src, res);
				return sw.toString();
			} catch (TransformerException | TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
}
