package eu.cessda.cvmanager.export.utils;

import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class MyXmlFilter extends XMLFilterImpl
{
	private boolean skip;

	private Set<String> filteredTag;
	private Set<String> filteredLanguage;

	public MyXmlFilter( XMLReader xmlReaderfactory, Set<String> filteredTag, Set<String> filteredLanguage )
	{
		super( xmlReaderfactory );
		this.filteredTag = filteredTag;
		this.filteredLanguage = filteredLanguage;
	}

	@Override
	public void startElement( String uri, String localName, String qName, Attributes atts ) throws SAXException
	{
		// filter tag
		if ( filteredTag != null && filteredTag.contains( qName ) )
			skip = true;
		else
			skip = false;

		// filter language attribute
		if ( !skip && filteredLanguage != null )
		{
			String langValue = atts.getValue( "xml:lang" );
			if ( langValue != null && filteredLanguage.contains( langValue ) )
				skip = true;
		}

		if ( !skip )
			super.startElement( uri, localName, qName, atts );

	}

	@Override
	public void endElement( String uri, String localName, String qName ) throws SAXException
	{
		if ( filteredTag != null && filteredTag.contains( qName ) )
			skip = true;

		if ( !skip )
			super.endElement( uri, localName, qName );
		skip = false;
	}

	@Override
	public void characters( char[] ch, int start, int length ) throws SAXException
	{
		if ( !skip )
		{
			super.characters( ch, start, length );
		}
	}

}
