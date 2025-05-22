/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.service;

import org.docx4j.convert.in.xhtml.FormattingOption;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageDimensions;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExportService
{
    public static final String MEDIATYPE_RDF_VALUE = "application/rdf+xml";
    public static final MediaType MEDIATYPE_RDF = MediaType.parseMediaType(MEDIATYPE_RDF_VALUE);
    public static final String MEDIATYPE_WORD_VALUE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final MediaType MEDIATYPE_WORD = MediaType.parseMediaType( MEDIATYPE_WORD_VALUE );

    public enum DownloadType
	{
		SKOS("rdf", MEDIATYPE_RDF),
        PDF("pdf", MediaType.APPLICATION_PDF),
        HTML("html", MediaType.TEXT_HTML),
        WORD("docx", MEDIATYPE_WORD );

		private final String type;
        private final MediaType mediaType;

        public static Optional<DownloadType> fromMediaType( MediaType mediaType )
        {
            for ( var downloadType : DownloadType.values() )
            {
                if ( mediaType.includes( downloadType.getMediaType() ))
                {
                    return Optional.of( downloadType );
                }
            }
            return Optional.empty();
        }

		DownloadType( String type, MediaType mediaType )
		{
			this.type = type;
            this.mediaType = mediaType;
        }

        public MediaType getMediaType()
        {
            return mediaType;
        }

        @Override
		public String toString()
		{
			return this.type;
		}
    }

    private final ObjectFactory factory = new ObjectFactory();
	private final SpringTemplateEngine templateEngine;

	public ExportService( SpringTemplateEngine templateEngine )
	{
        this.templateEngine = templateEngine;
	}

    public void generateFileByThymeleafTemplate(
			String templateName,
			Map<String, Object> map,
			DownloadType type,
            OutputStream outputStream) throws IOException, JAXBException, Docx4JException {
        org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context();
		if ( map != null )
		{
            map.forEach( ctx::setVariable );
		}

        String content;

        switch ( type ) {
            case PDF:
            case HTML:
            case WORD:
                content = templateEngine.process( DownloadType.HTML.type + "/" + templateName + "_" + type, ctx );
                break;
            case SKOS:
                content = templateEngine.process( DownloadType.SKOS.type + "/"  + templateName + "_" + type, ctx )
                    .replaceAll( "(?m)^[ \t]*\r?\n", "" );
                break;
            default:
                throw new IllegalArgumentException( "Unexpected value: " + type );
        }

        switch (type)
		{
            case PDF:
                createPdfFile( content, outputStream );
                break;
            case HTML:
            case SKOS:
                createTextFile( content, outputStream );
                break;
            case WORD:
                createWordFile( content, outputStream );
                break;
		}
	}

	private void createTextFile( String contents, OutputStream outputStream ) throws IOException
    {
        OutputStreamWriter writer = new OutputStreamWriter( outputStream, StandardCharsets.UTF_8 );
        writer.write( contents );
        writer.flush();
	}

    public void createPdfFile( String contents, OutputStream outputStream ) throws IOException
    {
        // Convert document to XHTML
        Document parsedHTML = Jsoup.parse( contents );
        parsedHTML.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        var renderer = new ITextRenderer();

        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setPrint(true);
        sharedContext.setInteractive(false);

        // Load custom fonts
        var fontResolver = renderer.getFontResolver();
        fontResolver.addFont( getFontPath( "/fonts/NotoSansCJKjp-Black.otf" ), true );
        fontResolver.addFont( getFontPath( "/fonts/NotoSansCJKjp-Bold.otf" ), true );
        fontResolver.addFont( getFontPath( "/fonts/NotoSansCJKjp-DemiLight.otf" ), true );
        fontResolver.addFont( getFontPath( "/fonts/NotoSansCJKjp-Light.otf" ), true );
        fontResolver.addFont( getFontPath( "/fonts/NotoSansCJKjp-Medium.otf" ), true );
        fontResolver.addFont( getFontPath( "/fonts/NotoSansCJKjp-Regular.otf" ), true );
        fontResolver.addFont( getFontPath( "/fonts/NotoSansCJKjp-Thin.otf" ), true );

        // Generate the PDF
        renderer.setDocumentFromString(parsedHTML.html());
        renderer.layout();
        renderer.createPDF(outputStream);
	}

    @SuppressWarnings( "DataFlowIssue" )
    private String getFontPath( String name )
    {
        URL fontURL = this.getClass().getResource( name );
        try
        {
            File fontFile = new File( fontURL.toURI() ) ;
            return fontFile.getPath();
        }
        catch ( IllegalArgumentException | URISyntaxException e )
        {
            // fallback
            return fontURL.toString();
        }
    }

    public void createWordFile( String contents, OutputStream outputStream ) throws Docx4JException, JAXBException {
        // Setup font mapping
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAscii( "Century Gothic" );
        XHTMLImporterImpl.addFontMapping( "Century Gothic", rfonts );

        // Create an empty docx package
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage( PageSizePaper.A4, false);
        Body body = wordMLPackage.getMainDocumentPart().getJaxbElement().getBody();

        SectPr.PgSz sectprpgsz = Context.getWmlObjectFactory().createSectPrPgSz();
        sectprpgsz.setH( BigInteger.valueOf( 16838 ) );
        sectprpgsz.setW( BigInteger.valueOf( 11906 ) );

        PageDimensions page = new PageDimensions();
        SectPr.PgMar pgMar = page.getPgMar();
        pgMar.setTop( BigInteger.valueOf(720L));
        pgMar.setRight( BigInteger.valueOf(720L));
        pgMar.setBottom( BigInteger.valueOf(720L));
        pgMar.setLeft( BigInteger.valueOf(720L));
        pgMar.setHeader( BigInteger.valueOf(708L));
        pgMar.setFooter( BigInteger.valueOf(708L));
        pgMar.setGutter( BigInteger.valueOf(0L));

        SectPr sectPr = factory.createSectPr();
        sectPr.setPgSz(sectprpgsz);
        sectPr.setPgMar(pgMar);
        body.setSectPr(sectPr);

        Relationship relationship = createFooterPart( wordMLPackage, factory );
        createFooterReference(wordMLPackage, relationship );

        NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
        wordMLPackage.getMainDocumentPart().addTargetPart( ndp );
        ndp.unmarshalDefaultNumbering();

        // Convert the XHTML, and add it into the empty docx we made
        XHTMLImporterImpl xHTMLImporter = new XHTMLImporterImpl( wordMLPackage );
        xHTMLImporter.setHyperlinkStyle( "Hyperlink" );
        xHTMLImporter.setParagraphFormatting(FormattingOption.CLASS_PLUS_OTHER);
        xHTMLImporter.setRunFormatting(FormattingOption.CLASS_PLUS_OTHER);
        xHTMLImporter.setTableFormatting(FormattingOption.CLASS_PLUS_OTHER);

        // convert and save file
        wordMLPackage.getMainDocumentPart().getContent().addAll( xHTMLImporter.convert( contents, null ) );
        wordMLPackage.save( outputStream );
    }

    private Relationship createFooterPart(WordprocessingMLPackage wordMLPackage, ObjectFactory factory) throws InvalidFormatException {
        FooterPart footerPart = new FooterPart();
        footerPart.setPackage(wordMLPackage);

        footerPart.setJaxbElement(createFooterWithPageNr(factory));

        return wordMLPackage.getMainDocumentPart().addTargetPart(footerPart);
    }

    public Ftr createFooterWithPageNr(ObjectFactory factory) {
        Ftr ftr = factory.createFtr();
        P paragraph = factory.createP();
        addFieldBegin(factory, paragraph);
        addPageNumberField(factory, paragraph);
        addFieldEnd(factory, paragraph);
        ftr.getContent().add(paragraph);
        return ftr;
    }

    private void addFieldBegin(ObjectFactory factory, P paragraph) {
        R run = factory.createR();
        FldChar fldchar = factory.createFldChar();
        fldchar.setFldCharType(STFldCharType.BEGIN);
        run.getContent().add(fldchar);
        paragraph.getContent().add(run);
    }

    private static void addFieldEnd(ObjectFactory factory, P paragraph) {
        FldChar fldcharend = factory.createFldChar();
        fldcharend.setFldCharType(STFldCharType.END);
        R run3 = factory.createR();
        run3.getContent().add(fldcharend);
        paragraph.getContent().add(run3);
    }

    private static void addPageNumberField(ObjectFactory factory, P paragraph) {
        R run = factory.createR();
        PPr ppr = new PPr();
        Jc jc = new Jc();
        jc.setVal(JcEnumeration.RIGHT);
        ppr.setJc(jc);
        paragraph.setPPr(ppr);
        Text txt = new Text();
        txt.setSpace("preserve");
        txt.setValue(" PAGE   \\* MERGEFORMAT ");
        run.getContent().add(factory.createRInstrText(txt));
        paragraph.getContent().add(run);

    }

    public void createFooterReference(WordprocessingMLPackage wordMLPackage, Relationship relationship){

        List<SectionWrapper> sections =
            wordMLPackage.getDocumentModel().getSections();

        SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
        // There is always a section wrapper, but it might not contain a sectPr
        if (sectPr==null ) {
            sectPr = factory.createSectPr();
            wordMLPackage.getMainDocumentPart().addObject(sectPr);
            sections.get(sections.size() - 1).setSectPr(sectPr);
        }

        FooterReference footerReference = factory.createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(HdrFtrRef.DEFAULT);
        sectPr.getEGHdrFtrReferences().add(footerReference);
    }
}
