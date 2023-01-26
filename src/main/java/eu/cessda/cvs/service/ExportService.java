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

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.layout.font.FontProvider;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class ExportService
{

    public static final String HTML = "html";

    public enum DownloadType
	{
		SKOS("rdf", MediaType.APPLICATION_XML),
        PDF("pdf", MediaType.APPLICATION_PDF),
        HTML(ExportService.HTML, MediaType.TEXT_HTML),
        WORD("docx", new MediaType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document" ));

		private final String type;
        private final MediaType mediaType;

		DownloadType( String type, MediaType mediaType )
		{
			this.type = type;
            this.mediaType = mediaType;
        }

        public MediaType getMediaType()
        {
            return mediaType;
        }

		public boolean equalsType( String otherType )
		{
			return type.equals( otherType );
		}

		@Override
		public String toString()
		{
			return this.type;
		}
    }

	private final SpringTemplateEngine templateEngine;

	public ExportService(SpringTemplateEngine templateEngine )
	{
		this.templateEngine = templateEngine;
	}

	public File exportQuestion()
	{
		return null;
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
                content = templateEngine.process( HTML + "/" + templateName + "_" + type, ctx );
                break;
            case SKOS:
                content = templateEngine.process( "xml" + "/"  + templateName + "_" + type, ctx )
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
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( outputStream ) );
        bw.write( contents );
        bw.flush();
	}

	public void createPdfFile( String contents, OutputStream outputStream )
    {
        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);
        fontProvider.addDirectory(this.getClass().getResource("/fonts").getPath());
        properties.setFontProvider(fontProvider);
        HtmlConverter.convertToPdf(contents, outputStream, properties);
	}

    public void createWordFile(  String contents, OutputStream outputStream ) throws Docx4JException, JAXBException {
        ObjectFactory factory = new ObjectFactory();
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
        createFooterReference(wordMLPackage, relationship, factory);

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

    public static void createFooterReference(WordprocessingMLPackage wordMLPackage, Relationship relationship, ObjectFactory factory){

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
