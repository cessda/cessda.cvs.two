package eu.cessda.cvs.service;

import com.lowagie.text.DocumentException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ExportService
{
    private final Logger log = LoggerFactory.getLogger(ExportService.class);

    public static final String HTML = "html";

    public enum DownloadType
	{
		SKOS("rdf"), PDF("pdf"), HTML(ExportService.HTML), WORD("docx");

		private final String type;

		private DownloadType( String s )
		{
			type = s;
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

	public File generateFileByThymeleafTemplate(
			String fileName,
			String templateName,
			Map<String, Object> map,
			DownloadType type ) throws IOException, JAXBException, Docx4JException {
        String tDir = System.getProperty("java.io.tmpdir");
		File outputFile = new File(tDir + File.separator  + fileName + "." + type.toString() );
		org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context();
		if ( map != null )
		{
            final Iterator<Map.Entry<String, Object>> itMap = map.entrySet().iterator();
            while (itMap.hasNext())
			{
                Map.Entry<String, Object> pair = itMap.next();
				ctx.setVariable( pair.getKey(), pair.getValue() );
			}
		}

		switch (type)
		{
		case PDF:
			return createPdfFile( outputFile,
					templateEngine.process( HTML + File.separator  + templateName + "_" + type.toString(), ctx ) );
		case HTML:
			return createTextFile( outputFile,
					templateEngine.process( HTML + File.separator  + templateName + "_" + type.toString(), ctx ) );
		case SKOS:
			String content = templateEngine.process( "xml" + File.separator  + templateName + "_" + type.toString(), ctx );
			return createTextFile( outputFile,
					content.replaceAll( "(?m)^[ \t]*\r?\n", "" ) );
        case WORD:
            return createWordFile( outputFile,
                templateEngine.process( HTML + File.separator  + templateName + "_" + type.toString(), ctx ) );
		default:
			break;
		}
		return null;
	}

	private File createTextFile( File outputFile, String contents ) throws IOException
	{
		try( BufferedWriter bw = new BufferedWriter( new FileWriter( outputFile ) ) )
		{
			bw.write( contents );
            bw.flush();
		}
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
		return outputFile;
	}

	public File createPdfFile( File outputFile, String contents ) {
		try (FileOutputStream os = new FileOutputStream( outputFile ))
		{
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString( contents );
			renderer.layout();
			renderer.createPDF( os, false );
			renderer.finishPDF();
		} catch (DocumentException | IOException e) {
            log.error( e.getMessage() );
        }
        return outputFile;
	}

    public File createWordFile( File outputFile, String contents ) throws Docx4JException, JAXBException {
        ObjectFactory factory = new ObjectFactory();
        // Setup font mapping
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAscii( "Century Gothic" );
        XHTMLImporterImpl.addFontMapping( "Century Gothic", rfonts );

        // Create an empty docx package
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage(
            PageSizePaper.A4, false);
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
        wordMLPackage.save( outputFile );
        return outputFile;
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
