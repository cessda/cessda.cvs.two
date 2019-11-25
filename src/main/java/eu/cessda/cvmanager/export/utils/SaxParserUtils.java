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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SaxParserUtils
{

	private static final Logger log = LoggerFactory.getLogger( SaxParserUtils.class );

	private SaxParserUtils()
	{
		throw new IllegalStateException( "Utility class" );
	}

	public static String filterSkosDoc( Set<String> filteredTag, Set<String> filteredLanguage, String xmlString )
	{
		XMLReader xr;
		try
		{
			xr = xmlReader( filteredTag, filteredLanguage );

			Source src = new SAXSource( xr, new InputSource( new StringReader( xmlString ) ) );
			StringWriter sw = new StringWriter();
			Result res = new StreamResult( sw );
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
			Transformer transformer = factory.newTransformer();

			transformer.transform( src, res );
			return sw.toString();
		}
		catch (TransformerException | TransformerFactoryConfigurationError e)
		{
			log.error( "Error on transform" );
			log.error( e.getMessage(), e );
		}
		catch (SAXException e)
		{

			log.error( e.getMessage(), e );
		}
		return null;
	}

	private static XMLReader xmlReader( Set<String> filteredTag, Set<String> filteredLanguage ) throws SAXException
	{
		XMLReader xr;
		XMLReader xmlReaderfactory = XMLReaderFactory.createXMLReader();
		xmlReaderfactory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
		xr = new MyXmlFilter( xmlReaderfactory, filteredTag, filteredLanguage );
		return xr;
	}

}