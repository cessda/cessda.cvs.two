package eu.cessda.cvmanager.ui.layout;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.renderers.ComponentRenderer;

import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader.OnDemandStreamResource;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;

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
	private final VersionService versionService;
	private final TemplateEngine templateEngine;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
	private final VocabularyDTO vocabulary;
	
	private Set<String> filteredTag = new HashSet<>();
	private MGrid<ExportCV> exportGrid = new MGrid<>( ExportCV.class );
	private List<ExportCV> exportCvItems;
	private MCssLayout sectionLayout = new MCssLayout();
	private MVerticalLayout gridLayout = new MVerticalLayout();
	private MButton exportSkos = new MButton("Export Skos").withStyleName("marginleft20");
	private MButton exportPdf = new MButton("Export Pdf").withStyleName("marginleft20");
	private MButton exportHtml = new MButton("Export Html").withStyleName("marginleft20");
	
	public ExportLayout(I18N i18n, Locale locale, UIEventBus eventBus, CvItem cvItem, VocabularyDTO vocabulary,
			VersionService versionService, ConfigurationService configurationService, TemplateEngine templateEngine) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.eventBus = eventBus;
		this.cvItem = cvItem;
		this.vocabulary = vocabulary;
		this.configurationService = configurationService;
		this.versionService = versionService;
		this.templateEngine = templateEngine;
		
		initLayout();
	}

	private void initLayout() {
		exportCvItems = new ArrayList<>();
		
		Map<String, List<VersionDTO>> orderedLanguageVersionMap = null;
		orderedLanguageVersionMap = versionService.getOrderedLanguageVersionMap(vocabulary.getId());
			
		orderedLanguageVersionMap.forEach( (k,v) -> exportCvItems.add( new ExportCV(k, v)));
		
		exportGrid.addStyleNames("export-grid");
		exportGrid.removeAllColumns();		
		exportGrid.setHeight("300px");
		
		
		exportGrid
			.withFullWidth()
			.withHeight("200px")
			.setItems(exportCvItems);
			
		exportGrid.addColumn( cVersion -> cVersion.getLanguage())
			.setCaption("Language")
			.setExpandRatio( 2 )
			.setId("languageClm");
		exportGrid.addColumn( cVersion -> cVersion.getType())
			.setCaption("Type")
			.setExpandRatio( 1 )
			.setId("typeClm");
		exportGrid.addColumn( cVersion -> {
			return cVersion.getVersionOption();
			}, new ComponentRenderer())
			.setCaption("Version")
			.setExpandRatio( 2 )
			.setId("versionClm");
		exportGrid.addColumn( cVersion -> {
			return cVersion.getExportSkosCb();
			}, new ComponentRenderer())
			.setCaption("Skos")
			.setExpandRatio( 1 )
			.setId("skosClm");
		exportGrid.addColumn( cVersion -> {
			return cVersion.getExportPdfCb();
			}, new ComponentRenderer())
			.setCaption("Pdf")
			.setExpandRatio( 1 )
			.setId("pdfClm");
		exportGrid.addColumn( cVersion -> {
			return cVersion.getExportHtmlCb();
			}, new ComponentRenderer())
			.setCaption("Html")
			.setExpandRatio( 1 )
			.setId("htmlClm");
		
		gridLayout
			.withFullSize()
			.withComponent(exportGrid)
			.withExpand(exportGrid, 1);
	
		sectionLayout
			.withFullSize()
			.add( gridLayout, exportSkos, exportPdf , exportHtml);
		
		this
			.withFullWidth()
			.add( sectionLayout);
		
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
				List<VersionDTO> exportVersions = new ArrayList<>();
				
				for(ExportCV ecv : exportCvItems) {
					Map<DownloadType, VersionDTO> fotmatVersionMap = ecv.getFotmatVersionMap();
					if( fotmatVersionMap.get(downloadType) != null ) {
						exportVersions.add( fotmatVersionMap.get(downloadType) );
					}
				}
								
				if(exportVersions.isEmpty()){
					Notification.show( "No language selected!" );
					return null;
				}
				
                try { 
                	switch (downloadType) {
	                	case HTML:
						case PDF:
							try {
								return new FileInputStream( generateExportFile(downloadType, exportVersions));
							} catch (Exception e) {
								e.printStackTrace();
							}
						default:
							return new ByteArrayInputStream( generateSKOSxml( downloadType, exportVersions ).getBytes(StandardCharsets.UTF_8.name()));
					}
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
			}
			
			@Override
			public String getFilename() {
				return generateOnDemandFileName( downloadType , true);
			}
		};
	}
	
	private String generateSKOSxml( DownloadType type, List<VersionDTO> exportVersions) {

		configurationService.getPropertyByKeyAsSet("cvmanager.export.filterTag", ",").ifPresent( c -> filteredTag = c );
		
		Set<String> filteredLanguages = new HashSet<>();
		
		StringJoiner skosXml = new StringJoiner("\n\n");
		skosXml.add( 
				SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguages, cvItem.getCvScheme().getContent())
					.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" "
							+ "xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:dct=\"http://purl.org/dc/terms/\" xmlns:foaf=\"http://xmlns.com/foaf/spec/\" "
							+ "xmlns:void=\"http://rdfs.org/ns/void#\" xmlns:iqvoc=\"http://try.iqvoc.net/schema#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" "
							+ "xmlns=\"http://lod.gesis.org/thesoz/\" xmlns:schema=\"http://lod.gesis.org/thesoz/schema#\" id=\""+ cvItem.getCvScheme().getContainerId()+ "\">\n\n" )
				);
		skosXml.add( cvItem
						.getFlattenedCvConceptStreams()
						.map( x -> SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguages, x.getContent()).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "") )
						.collect( Collectors.joining("\n\n"))
				);
		
		skosXml.add( "\n</rdf:RDF>");
		
		// return string and remove any blank lines
		return skosXml.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
	}
	
	private File generateExportFile( DownloadType type, List<VersionDTO> exportVersions) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("content", generateSKOSxml( type, exportVersions ));
		map.put("filterOutLanguage", exportVersions);
		map.put("scheme", cvItem.getCvScheme());
		map.put("concepts", cvItem.getFlattenedCvConceptStreams().collect(Collectors.toList()));
		map.put("preflabelOl", cvItem.getCvScheme().getTitleByLanguage("en"));
		map.put("definitionOl", cvItem.getCvScheme().getDescriptionByLanguage("en"));
		
		return generateFileByThymeleafTemplate(generateOnDemandFileName( type , false), "export", map, type);
	}

	private Set<String> getFilteredLanguages( DownloadType type, boolean checked) {
//		Stream<ExportCV> exportCvStream = null;
//		
//		if( type == DownloadType.PDF)
//			exportCvStream = exportCvItems.stream().filter( f -> checked ? f.exportPdf : !f.exportPdf );
//		else if( type == DownloadType.HTML )
//			exportCvStream = exportCvItems.stream().filter( f -> checked ? f.exportHtlm : !f.exportHtlm );
//		else
//			exportCvStream = exportCvItems.stream().filter( f -> checked ? f.exportSkos : !f.exportSkos );
//		return exportCvStream
//			.map( x -> x.language )
//			.collect( Collectors.toSet());
		return null;
	}

	
	private File generateFileByThymeleafTemplate(String fileName, String templateName, Map<String, Object> map, DownloadType type) throws Exception {
		File outputFile = File.createTempFile( fileName ,"." + type.toString());
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
			return createPdfFile(outputFile, processedTemplate);
		case HTML:
			return createTextFile(outputFile, processedTemplate);
		default:
			break;
		}
		return null;
	}

	private File createTextFile(File outputFile, String contents) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write(contents);
		bw.close();
		return outputFile;
	}
	
	public File createPdfFile(File outputFile, String contents) throws Exception {
		
		  FileOutputStream os = null;
	        try {
	        	os = new FileOutputStream(outputFile);

	            ITextRenderer renderer = new ITextRenderer();
	            renderer.setDocumentFromString(contents);
	            renderer.layout();
	            renderer.createPDF(os, false);
	            renderer.finishPDF();
	        }
	        finally {
	            if (os != null) {
	                try {
	                    os.close();
	                } catch (IOException e) {}
	            }
	        }
	    return outputFile;
	}

	
	private String generateOnDemandFileName(DownloadType type, boolean withFileFormat) {
		String title = !cvItem.getCvScheme().getCode().isEmpty() ? cvItem.getCvScheme().getCode() : cvItem.getCvScheme().getTitleByLanguage("en");		
		return title + "_" + String.join("-", getFilteredLanguages( type , true)) + "_" + LocalDateTime.now().format(dateFormatter) + (withFileFormat ? "." + type.toString() : "");
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
	
	class ExportCV{
		private String language;
		private String type;
		private MCheckBox exportSkosCb = new MCheckBox();
		private MCheckBox exportPdfCb = new MCheckBox();
		private MCheckBox exportHtmlCb = new MCheckBox();
		private List<VersionDTO> versions;
		private ComboBox<VersionDTO> versionOption = new ComboBox<>();
		public ExportCV(String language, List<VersionDTO> versions) {
			this(language, versions, true,true,true);
		}
		public ExportCV(String language, List<VersionDTO> versions, boolean exportSkos, boolean exportPdf, boolean exportHtml) {
			this.language = language;
			this.versions = versions;
			this.exportSkosCb.setValue( exportSkos );
			this.exportPdfCb.setValue( exportPdf );
			this.exportHtmlCb.setValue( exportHtml );
			
			this.versionOption.setItems( versions );
			this.versionOption.setWidth("100%");
			this.versionOption.setItemCaptionGenerator( vers -> {
				return vers.getNumber() + " (" + vers.getStatus() + ")";
			});
			this.versionOption.setValue( versions.get(0) );
			this.versionOption.setEmptySelectionAllowed( false );
			this.versionOption.setTextInputAllowed( false );
			
			this.type = versions.get(0).getItemType();
		}
		public Map<DownloadType, VersionDTO> getFotmatVersionMap(){
			Map<DownloadType, VersionDTO> formatVersionMap = new HashMap<>();
			VersionDTO selectedVersion = versionOption.getValue();
			if( exportSkosCb.getValue() == true ) {
				formatVersionMap.put(DownloadType.SKOS, selectedVersion);
			}
			
			if( exportPdfCb.getValue() == true ) {
				formatVersionMap.put(DownloadType.PDF, selectedVersion);
			}
			
			if( exportHtmlCb.getValue() == true ) {
				formatVersionMap.put(DownloadType.HTML, selectedVersion);
			}
			
			return formatVersionMap;
		}
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		public MCheckBox getExportSkosCb() {
			return exportSkosCb;
		}
		public void setExportSkosCb(MCheckBox exportSkosCb) {
			this.exportSkosCb = exportSkosCb;
		}
		public MCheckBox getExportPdfCb() {
			return exportPdfCb;
		}
		public void setExportPdfCb(MCheckBox exportPdfCb) {
			this.exportPdfCb = exportPdfCb;
		}
		public MCheckBox getExportHtmlCb() {
			return exportHtmlCb;
		}
		public void setExportHtmlCb(MCheckBox exportHtmlCb) {
			this.exportHtmlCb = exportHtmlCb;
		}
		public ComboBox<VersionDTO> getVersionOption() {
			return versionOption;
		}
		public void setVersionOption(ComboBox<VersionDTO> versionOption) {
			this.versionOption = versionOption;
		}
		public List<VersionDTO> getVersions() {
			return versions;
		}
		public void setVersions(List<VersionDTO> versions) {
			this.versions = versions;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}

}
