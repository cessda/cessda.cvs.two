package eu.cessda.cvmanager.ui.layout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader.OnDemandStreamResource;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.ConfigurationService;

public class ExportLayout  extends MCssLayout implements Translatable {
	
	public enum DownloadType{ 
		SKOS("rdf"), 
		PDF("pdf"), 
		HTML("html");
		
		private final String type;       

	    private DownloadType(String s) {
	        type = s;
	    }

	    public boolean equalsType(String otherType) {
	        return type.equals(otherType);
	    }

	    public String toString() {
	       return this.type;
	    }
	}
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final UIEventBus eventBus;
	private final CvItem cvItem;
	private final ConfigurationService configurationService;
	private final TemplateEngine templateEngine;
	
	private Set<String> filteredTag = new HashSet<>();
	private Grid<ExportCV> exportGrid;
	private List<ExportCV> exportCvItems;
	private MCssLayout sectionLayout = new MCssLayout();
	private MVerticalLayout gridLayout = new MVerticalLayout();
	private MButton exportSkos = new MButton("Export Skos");
	private MButton exportPdf = new MButton("Export Pdf");
	private MButton exportHtml = new MButton("Export Html");
	
	public ExportLayout(I18N i18n, Locale locale, UIEventBus eventBus, CvItem cvItem, 
			ConfigurationService configurationService, TemplateEngine templateEngine) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.eventBus = eventBus;
		this.cvItem = cvItem;
		this.configurationService = configurationService;
		this.templateEngine = templateEngine;
		
		initLayout();
	}

	private void initLayout() {
		exportGrid = new Grid<>();
		exportCvItems = new ArrayList<>();
		
		Set<String> languages = cvItem.getCvScheme().getLanguagesByTitle();
		languages.forEach(lang -> {
			exportCvItems.add( new ExportCV(lang, lang + "-latest"));
		});
		exportGrid.setItems(exportCvItems);
		exportGrid.addStyleNames(ValoTheme.TABLE_BORDERLESS, "undefined-height");
		
		exportGrid
			.addComponentColumn(item -> new Label(item.getLanguage()))
			.setCaption("Language");
		exportGrid
			.addComponentColumn(item -> new Label(item.version))
			.setCaption("Version");
		
		Column<ExportCV, Component> skosColumn = exportGrid
			.addComponentColumn(item -> {
				CheckBox checkBox = new CheckBox();
				checkBox.setValue(item.isExportSkos());
				checkBox.addValueChangeListener( e -> item.setExportSkos( e.getValue()));
				return checkBox;
			});
		skosColumn.setCaption("Skos");
		
		Column<ExportCV, Component> pdfColumn = exportGrid
			.addComponentColumn(item -> {
				CheckBox checkBox = new CheckBox();
				checkBox.setValue(item.isExportPdf());
				checkBox.addValueChangeListener( e -> item.setExportPdf( e.getValue()));
				return checkBox;
		});
		pdfColumn.setCaption("Pdf");
		
		Column<ExportCV, Component> htmlColumn = exportGrid
		.addComponentColumn(item -> {
			CheckBox checkBox = new CheckBox();
			checkBox.setValue(item.isExportHtlm());
			checkBox.addValueChangeListener( e -> item.setExportHtlm( e.getValue()));
			return checkBox;
		});
		htmlColumn.setCaption("Html");
		
		gridLayout
			.withFullSize()
			.withComponent(exportGrid)
			.withExpand(exportGrid, 1);
		
		sectionLayout
			.withFullSize()
			.add( gridLayout, exportSkos, exportPdf , exportHtml);
		
		this.add( sectionLayout);
		
		OnDemandFileDownloader onDemandSkosFileDownloader = new OnDemandFileDownloader( createOnDemandResource( DownloadType.SKOS ));
		onDemandSkosFileDownloader.extend(exportSkos);
		
		OnDemandFileDownloader onDemandSkosFileDownloaderPdf = new OnDemandFileDownloader( createOnDemandResource( DownloadType.PDF ));
		onDemandSkosFileDownloaderPdf.extend(exportPdf);
		
		OnDemandFileDownloader onDemandSkosFileDownloaderHtml = new OnDemandFileDownloader( createOnDemandResource( DownloadType.HTML ));
		onDemandSkosFileDownloaderHtml.extend(exportHtml);
	}
	
	private OnDemandStreamResource createOnDemandResource( DownloadType downloadType) {
		return new OnDemandStreamResource() {
			private static final long serialVersionUID = 3421089012275806929L;
			@Override
			public InputStream getStream() {
                try { 
                	switch (downloadType) {
                	case HTML:
					case PDF:
						try {
							return new FileInputStream( generateExportFile(downloadType));
						} catch (Exception e) {
							e.printStackTrace();
						}
					default:
						return new ByteArrayInputStream( generateSKOSxml( downloadType ).toString().getBytes(StandardCharsets.UTF_8.name()));
					}
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
			}
			
			@Override
			public String getFilename() {
				return generateOnDemandFileName( downloadType );
			}
		};
	}
	
	private StringJoiner generateSKOSxml( DownloadType type) {
		Set<String> filteredLanguage = getFilteredLanguages( type , false);
		
		if(filteredLanguage.size() == cvItem.getCvScheme().getLanguagesByTitle().size()){
			Notification.show( "No language selected!" );
			return null;
		}
		
		configurationService.getPropertyByKeyAsSet("cvmanager.export.filterTag", ",").ifPresent( c -> filteredTag = c );
		
		StringJoiner skosXml = new StringJoiner("\n\n");
		skosXml.add( 
				SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguage, cvItem.getCvScheme().getContent())
					.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" "
							+ "xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:dct=\"http://purl.org/dc/terms/\" xmlns:foaf=\"http://xmlns.com/foaf/spec/\" "
							+ "xmlns:void=\"http://rdfs.org/ns/void#\" xmlns:iqvoc=\"http://try.iqvoc.net/schema#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" "
							+ "xmlns=\"http://lod.gesis.org/thesoz/\" xmlns:schema=\"http://lod.gesis.org/thesoz/schema#\" id=\""+ cvItem.getCvScheme().getContainerId()+ "\">\n\n" )
				);
		skosXml.add( cvItem
						.getFlattenedCvConceptStreams()
						.map( x -> SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguage, x.getContent()).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "") )
						.collect( Collectors.joining("\n\n"))
				);
		
		skosXml.add( "\n</rdf:RDF>");
		return skosXml;
	}
	
	private File generateExportFile( DownloadType type) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("content", generateSKOSxml( type ).toString());
		map.put("preflabelOl", cvItem.getCvScheme().getTitleByLanguage("en"));
		map.put("definitionOl", cvItem.getCvScheme().getDescriptionByLanguage("en"));
		
		return generateFileByThymeleafTemplate(generateOnDemandFileName( type ), "export", map, type);
	}

	private Set<String> getFilteredLanguages( DownloadType type, boolean checked) {
		Stream<ExportCV> exportCvStream = null;
		
		if( type == DownloadType.PDF)
			exportCvStream = exportCvItems.stream().filter( f -> checked ? f.exportPdf : !f.exportPdf );
		else if( type == DownloadType.HTML )
			exportCvStream = exportCvItems.stream().filter( f -> checked ? f.exportHtlm : !f.exportHtlm );
		else
			exportCvStream = exportCvItems.stream().filter( f -> checked ? f.exportSkos : !f.exportSkos );
		return exportCvStream
			.map( x -> x.language )
			.collect( Collectors.toSet());
	}

	
	private File generateFileByThymeleafTemplate(String fileName, String templateName, Map<String, Object> map, DownloadType type) throws Exception {
		File outputFile = File.createTempFile( fileName , ".pdf");
		Context ctx = new Context();
		if (map != null) {
		     Iterator<?> itMap = map.entrySet().iterator();
		     while (itMap.hasNext()) {
		    	 @SuppressWarnings("rawtypes")
		    	 Map.Entry pair = (Map.Entry) itMap.next();
		    	 ctx.setVariable(pair.getKey().toString(), pair.getValue());
			}
		}
		String processedTemplate = templateEngine.process(templateName + "_" + type.toString() , ctx);
		
		switch ( type ) {
		case PDF:
			return createPdf(outputFile, processedTemplate);
		default:
			break;
		}

		
		return null;
	}
	
	public File createPdf(File outputFile, String processedHtml) throws Exception {
		
		  FileOutputStream os = null;
	        try {
	            
	        	os = new FileOutputStream(outputFile);

	            ITextRenderer renderer = new ITextRenderer();
	            renderer.setDocumentFromString(processedHtml);
	            renderer.layout();
	            renderer.createPDF(os, false);
	            renderer.finishPDF();
	            
	        }
	        finally {
	            if (os != null) {
	                try {
	                    os.close();
	                } catch (IOException e) { /*ignore*/ }
	            }
	        }
	    return outputFile;
	}

	
	private String generateOnDemandFileName(DownloadType type) {
		String title = !cvItem.getCvScheme().getCode().isEmpty() ? cvItem.getCvScheme().getCode() : cvItem.getCvScheme().getTitleByLanguage("en");		
		return title + "_" + String.join("-", getFilteredLanguages( type , true)) + "_" + LocalDateTime.now() +"." + type.toString();
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
	
	class ExportCV{
		private String language;
		private String version;
		private boolean exportSkos;
		private boolean exportPdf;
		private boolean exportHtlm;
		public ExportCV(String language, String version) {
			this(language, version, true,false,false);
		}
		public ExportCV(String language, String version, boolean exportSkos, boolean exportPdf, boolean exportHtlm) {
			this.language = language;
			this.version = version;
			this.exportSkos = exportSkos;
			this.exportPdf = exportPdf;
			this.exportHtlm = exportHtlm;
		}
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public boolean isExportSkos() {
			return exportSkos;
		}
		public void setExportSkos(boolean exportSkos) {
			this.exportSkos = exportSkos;
		}
		public boolean isExportPdf() {
			return exportPdf;
		}
		public void setExportPdf(boolean exportPdf) {
			this.exportPdf = exportPdf;
		}
		public boolean isExportHtlm() {
			return exportHtlm;
		}
		public void setExportHtlm(boolean exportHtlm) {
			this.exportHtlm = exportHtlm;
		}
	}

}
