package eu.cessda.cvmanager.ui.layout;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.gesis.stardat.entity.CVConcept;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader.OnDemandStreamResource;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.ConfigurationService;

public class ExportLayout  extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final UIEventBus eventBus;
	private final CvItem cvItem;
	private final ConfigurationService configurationService;
	
	private Set<String> filteredTag = new HashSet<>();
	private Grid<ExportCV> exportGrid;
	private List<ExportCV> exportCvItems;
	private MCssLayout sectionLayout = new MCssLayout();
	private MVerticalLayout gridLayout = new MVerticalLayout();
	private MButton exportSkos = new MButton("Export Skos");
	
	public ExportLayout(I18N i18n, Locale locale, UIEventBus eventBus, CvItem cvItem, ConfigurationService configurationService) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.eventBus = eventBus;
		this.cvItem = cvItem;
		this.configurationService = configurationService;
		
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
			.add( gridLayout, exportSkos );
		
		this.add( sectionLayout);
		
		StreamResource skosStreamSource = createResource();
		OnDemandStreamResource onDemandStreamResource = new OnDemandStreamResource() {
			
			@Override
			public InputStream getStream() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getFilename() {
				// TODO Auto-generated method stub
				return null;
			}
		};
//		OnDemandFileDownloader onDemandFileDownloader = new OnDemandFileDownloader((OnDemandStreamResource) skosStreamSource);
//		onDemandFileDownloader.extend(exportSkos);
		FileDownloader fileDownloader = new FileDownloader(skosStreamSource);
		fileDownloader.extend(exportSkos);
		
	}
	

	private StreamResource createResource() {
		return new StreamResource(new StreamSource() {
			private static final long serialVersionUID = 8813958914144335166L;

			@Override
            public InputStream getStream() {

                try {
                	Set<String> filteredLanguage = exportCvItems.stream()
                									.filter( f -> !f.exportSkos )
                									.map( x -> x.language )
                									.collect( Collectors.toSet());
                	
                	if(filteredLanguage.size() == cvItem.getCvScheme().getLanguagesByTitle().size()){
                		Notification.show( "No language selected!" );
                		return null;
                	}
                	
        			configurationService.getPropertyByKeyAsSet("cvmanager.export.filterTag", ",").ifPresent( c -> filteredTag = c );
                	
        			StringJoiner skosXml = new StringJoiner("\n\n");
        			skosXml.add( 
        					SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguage, cvItem.getCvScheme().getContent())
        						.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:dct=\"http://purl.org/dc/terms/\" xmlns:foaf=\"http://xmlns.com/foaf/spec/\" xmlns:void=\"http://rdfs.org/ns/void#\" xmlns:iqvoc=\"http://try.iqvoc.net/schema#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns=\"http://lod.gesis.org/thesoz/\" xmlns:schema=\"http://lod.gesis.org/thesoz/schema#\" id=\"thesoz\">\n\n" )
        					);
        			skosXml.add( cvItem
        							.getFlattenedCvConceptStreams()
        							.map( x -> SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguage, x.getContent()).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "") )
        							.collect( Collectors.joining("\n\n"))
							);
        			
        			skosXml.add( "\n</rdf:RDF>");
        			        			
                    return new ByteArrayInputStream(skosXml.toString().getBytes(StandardCharsets.UTF_8.name()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }, "test.rdf");
	}
	
	private String setFileName() {
		return cvItem.getCvScheme().getCode() + "_en_.rdf";
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		// TODO Auto-generated method stub
		
	}
	
//	private void exportSkos(ClickEvent event) {
//		StreamResource myResource = createResource();
//		FileDownloader fileDownloader = new FileDownloader(myResource);
//		fileDownloader.extend(downloadButton);
//	
//		setContent(downloadButton);
//	}
//	
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
