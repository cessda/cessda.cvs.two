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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vaadin.data.TreeData;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader.OnDemandStreamResource;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import javassist.tools.rmi.ObjectNotFoundException;

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
	private final CodeService codeService;
	private final SpringTemplateEngine templateEngine;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
	private final VocabularyDTO vocabulary;
	private final AgencyDTO agency;
	private final StardatDDIService stardatDDIService;
	private static final Logger log = LoggerFactory.getLogger(ExportLayout.class);
	
	private List<LicenceDTO> licenses;
	private LicenceDTO license;
	private VersionDTO slVersion;
	
	private Set<String> filteredTag = new HashSet<>();
	private MGrid<ExportCV> exportGrid = new MGrid<>( ExportCV.class );
	private List<ExportCV> exportCvItems = new ArrayList<>();
	private MCssLayout sectionLayout = new MCssLayout();
	private MVerticalLayout gridLayout = new MVerticalLayout();
	private MButton exportSkos = new MButton("Export Skos").withStyleName("marginleft20");
	private MButton exportPdf = new MButton("Export Pdf").withStyleName("marginleft20");
	private MButton exportHtml = new MButton("Export Html").withStyleName("marginleft20");
	
	private MCheckBox skosCheckBox = new MCheckBox("SKOS");
	private MCheckBox pdfCheckBox = new MCheckBox("PDF");
	private MCheckBox htmlCheckBox = new MCheckBox("HTML");
	private boolean activateSkosListener = true;
	private boolean activatePdfListener = true;
	private boolean activateHtmlListener = true;
	
//	private ValueChangeListener<Boolean> skosCheckBoxListener =  e -> {
//		activateSkosListener = false;
////		skosCheckBox.setValue( true );
//		exportCvItems.stream().filter( p -> !p.getExportSkosCb().getValue()).findFirst().ifPresent( export -> skosCheckBox.setValue( false ));
//	};
//	private ValueChangeListener<Boolean> pdfCheckBoxListener =  e -> {
//		activatePdfListener = false;
////		pdfCheckBox.setValue( true );
//		exportCvItems.stream().filter( p -> !p.getExportPdfCb().getValue()).findFirst().ifPresent( export -> pdfCheckBox.setValue( false ));
//	};
//	private ValueChangeListener<Boolean> htmlCheckBoxListener =  e -> {
//		activateHtmlListener = false;
////		htmlCheckBox.setValue( true );
//		exportCvItems.stream().filter( p -> !p.getExportHtmlCb().getValue()).findFirst().ifPresent( export -> htmlCheckBox.setValue( false ));
//	};
	
	private Map<String, List<VersionDTO>> orderedLanguageVersionMap = new LinkedHashMap<>();
	private boolean publishView;
	private int year = LocalDate.now().getYear();
	private String inSchemeUri = "";
	
	public ExportLayout(I18N i18n, Locale locale, UIEventBus eventBus, CvItem cvItem, VocabularyDTO vocabulary, AgencyDTO agency,
			VersionService versionService, CodeService codeService,ConfigurationService configurationService, 
			StardatDDIService stardatDDIService, List<LicenceDTO> licenses, SpringTemplateEngine templateEngine, boolean isPublished) {
		this.i18n = i18n;
		this.locale = locale;
		this.eventBus = eventBus;
		this.cvItem = cvItem;
		this.vocabulary = vocabulary;
		this.agency = agency;
		this.configurationService = configurationService;
		this.versionService = versionService;
		this.codeService = codeService;
		this.templateEngine = templateEngine;
		this.publishView = isPublished;
		this.licenses = licenses;
		this.stardatDDIService = stardatDDIService;
		
		initLayout();
	}

	private void initLayout() {
		if( !publishView )
			exportSkos.setVisible( false );
		
		HeaderRow headerRow = exportGrid.getDefaultHeaderRow();
		
		exportGrid.addStyleNames("export-grid");
		exportGrid.removeAllColumns();		
		exportGrid
			.withFullWidth()
			.withHeight("200px");
			
		exportGrid.addColumn( cVersion -> Language.valueOfEnum( cVersion.getLanguage()).toStringCapitalized())
			.setCaption("Language")
			.setExpandRatio( 2 )
			.setId("languageClm");
		exportGrid.addColumn( cVersion -> {
			return cVersion.getVersionOption();
			}, new ComponentRenderer())
			.setCaption("Version")
			.setExpandRatio( 2 )
			.setId("versionClm");
		if( publishView ) {
			exportGrid.addColumn( cVersion -> {
				return cVersion.getExportSkosCb();
				}, new ComponentRenderer())
				.setCaption("Skos")
				.setExpandRatio( 1 )
				.setId("skosClm");
			
			headerRow.getCell("skosClm").setComponent( skosCheckBox );
			
			skosCheckBox.addValueChangeListener( e-> {
				if( activateSkosListener )
					exportCvItems.forEach( export -> export.getExportSkosCb().setValue( e.getValue()));
				activateSkosListener = true;
			});
		}
		exportGrid.addColumn( cVersion -> {
			return cVersion.getExportPdfCb();
			}, new ComponentRenderer())
			.setCaption("Pdf")
			.setExpandRatio( 1 )
			.setId("pdfClm");
		headerRow.getCell("pdfClm").setComponent( pdfCheckBox );
		
		pdfCheckBox.addValueChangeListener( e-> {
			if( activatePdfListener )
				exportCvItems.forEach( export -> export.getExportPdfCb().setValue( e.getValue()));
			activatePdfListener = true;
		});
		
		exportGrid.addColumn( cVersion -> {
			return cVersion.getExportHtmlCb();
			}, new ComponentRenderer())
			.setCaption("Html")
			.setExpandRatio( 1 )
			.setId("htmlClm");
		headerRow.getCell("htmlClm").setComponent( htmlCheckBox );
		
		htmlCheckBox.addValueChangeListener( e-> {
			if( activateHtmlListener )
				exportCvItems.forEach( export -> export.getExportHtmlCb().setValue( e.getValue()));
			activateHtmlListener = true;
		});
		
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
		if( publishView ) {
			OnDemandFileDownloader onDemandSkosFileDownloader = new OnDemandFileDownloader( createOnDemandResource( DownloadType.SKOS ));
			onDemandSkosFileDownloader.extend(exportSkos);
		}
		OnDemandFileDownloader onDemandSkosFileDownloaderPdf = new OnDemandFileDownloader( createOnDemandResource( DownloadType.PDF ));
		onDemandSkosFileDownloaderPdf.extend(exportPdf);
		
		OnDemandFileDownloader onDemandSkosFileDownloaderHtml = new OnDemandFileDownloader( createOnDemandResource( DownloadType.HTML ));
		onDemandSkosFileDownloaderHtml.extend(exportHtml);
	}
	
	public void updateGrid( VersionDTO pivotVersion , Map<String, List<VersionDTO>> versionMap ) {
		orderedLanguageVersionMap = versionMap;
		exportCvItems.clear();

		Map<String, List<VersionDTO>> filteredVersionMap = getFilteredVersionMap(pivotVersion, orderedLanguageVersionMap);
		filteredVersionMap.forEach( (k,v) -> exportCvItems.add( new ExportCV(k, v)));
		
		if(pivotVersion.getLicenseId() != null) {
			licenses.stream().filter( p -> p.getId().equals( pivotVersion.getLicenseId() )).findFirst().ifPresent( 
	       			 license -> {
	       				 this.license = license;
	       			 });
		}
		
		
		if( pivotVersion.getStatus().equals( Status.PUBLISHED.toString())) {
			year = pivotVersion.getPublicationDate().getYear();
		}
		
		exportGrid.setItems(exportCvItems);
	}
	
	public Map<String, List<VersionDTO>> getFilteredVersionMap ( VersionDTO pivotVersion , Map<String, List<VersionDTO>> versionMap) {
		Map<String, List<VersionDTO>> filteredVersionMap = new LinkedHashMap<>();
		if( pivotVersion == null ) {
			//get latest SL version
			for(Map.Entry<String, List<VersionDTO>> entry : versionMap.entrySet()) {
				pivotVersion = entry.getValue().get(0);
				//break after first loop since SL version is always in the first entry
				break;
			}
		}
		
		// always get one SL version
		if( pivotVersion.getItemType().equals( ItemType.TL.toString())) {
			if(vocabulary.getVersionByUri( pivotVersion.getUriSl()).isPresent())
				pivotVersion = vocabulary.getVersionByUri( pivotVersion.getUriSl()).get();
		}
		
		slVersion = pivotVersion;

		for(Map.Entry<String, List<VersionDTO>> entry : versionMap.entrySet()) {
			if( entry.getKey().equals( pivotVersion.getLanguage() ) )
				filteredVersionMap.put( entry.getKey(), Arrays.asList(pivotVersion));
			else {
				// get only SL that within SL version
				List<VersionDTO> oldValue = entry.getValue();
				List<VersionDTO> newValue = new ArrayList<>();
				for(VersionDTO eachVersion: oldValue) {
					if( publishView && !eachVersion.getStatus().equals(Status.PUBLISHED.toString())) // only show published one
						continue;
					if( eachVersion.getUriSl() != null && eachVersion.getUriSl().equals(pivotVersion.getUri()))
						newValue.add(eachVersion);
				}
				if( !newValue.isEmpty() ) {
					filteredVersionMap.put( entry.getKey(), newValue);
				}
			}

		}
			
		
		return filteredVersionMap;
	}
	
	private OnDemandStreamResource createOnDemandResource( DownloadType downloadType) {
		return new OnDemandStreamResource() {
			List<VersionDTO> exportVersions = new ArrayList<>();
			private static final long serialVersionUID = 3421089012275806929L;
			@Override
			public InputStream getStream() {
				
				exportVersions.clear();
				for(ExportCV ecv : exportCvItems) {
					Map<DownloadType, VersionDTO> fotmatVersionMap = ecv.getFotmatVersionMap();
					if( fotmatVersionMap.get(downloadType) != null ) {
						exportVersions.add( fotmatVersionMap.get(downloadType) );
					}
				}
								
				if(exportVersions.isEmpty()){
					Notification notif = new Notification( "",
							"No language selected!",
						    Notification.TYPE_WARNING_MESSAGE);
					
					notif.setDelayMsec(0);
					notif.setPosition(Position.BOTTOM_RIGHT);
					
					// Show it in the page
					notif.show(Page.getCurrent());
					
					return null;
				}
				
                try { 
                	switch (downloadType) {
	                	case HTML:
						case PDF:
						case SKOS:
							try {
								return new FileInputStream( generateExportFile(downloadType, exportVersions));
							} catch (Exception e) {
								log.error(e.getMessage(), e);
							}
							break;
						default:
							return new ByteArrayInputStream( generateSKOSxml( downloadType, exportVersions ).getBytes(StandardCharsets.UTF_8.name()));
					}
                    
                } catch (IOException | ObjectNotFoundException e) {
					log.error(e.getMessage(), e);
                    return null;
                }
				return null;
			}
			
			@Override
			public String getFilename() {
				return generateOnDemandFileName( downloadType , true);
			}
		};
	}
	
	private String generateSKOSxml( DownloadType type, List<VersionDTO> exportVersions) throws ObjectNotFoundException {

		configurationService.getPropertyByKeyAsSet("cvmanager.export.filterTag", ",").ifPresent( c -> filteredTag = c );
		
		Set<String> filteredLanguages = getFilteredLanguages();
		
		// TODO: remove this after the FlatDB dialog definition fixed
		if( !exportVersions.isEmpty()) {
			if( exportVersions.get(0).getItemType().equals( ItemType.SL.toString()))
				inSchemeUri = exportVersions.get(0).getUri();
			else
				inSchemeUri = exportVersions.get(0).getUriSl();
		}
		
		if( type.equals( DownloadType.SKOS )) {
			// get CvItem from selected sl
			exportVersions.stream().filter( v -> v.getItemType().equals( ItemType.SL.toString())).findFirst().ifPresent( v -> {
				List<DDIStore> ddiSchemes = stardatDDIService.findByIdAndElementType( v.getUri(), DDIElement.CVSCHEME);
				if (ddiSchemes != null && !ddiSchemes.isEmpty()) {
					cvItem.setCvScheme( new CVScheme(ddiSchemes.get(0)) );
					
					
					TreeData<CVConcept> cvCodeTreeData = new TreeData<>();
					List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType(cvItem.getCvScheme().getContainerId(), DDIElement.CVCONCEPT);
					CvCodeTreeUtils.buildCvConceptTree(ddiConcepts, cvItem.getCvScheme(), cvCodeTreeData);
					
					cvItem.setCvConceptTreeData(cvCodeTreeData);
					
				}
			});

			if(cvItem.getCvScheme() == null ) {
				Notification.show("Error: unable to find CV-Scheme XML");
				throw new ObjectNotFoundException("Error: unable to find CV-Scheme XML");
			}
		}
		
		String xmlContent = SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguages, cvItem.getCvScheme().getContent())
				.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
						"         xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" + 
						"         xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"\n" + 
						"         xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" + 
						"         xmlns:gc=\"http://docs.oasis-open.org/codelist/ns/genericode/1.0/\"\n" + 
						"         xmlns:dcterms=\"http://purl.org/dc/terms/\"\n" + 
						"         xmlns:ddi-cv=\"urn:ddi-cv\"\n" + 
						"         xmlns:h=\"http://www.w3.org/1999/xhtml\"\n" + 
						"         xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n\n" )
				// TODO: remove this after the FlatDB dialog definition fixed
				.replace("skos:title", "dcterms:title")
				.replace("skos:nameCode", "skos:notation")
				.replace("skos:version", "owl:versionInfo")
				.replace("id=\"\" ", "")
				.replace("<skos:hasTopConcept rdf:resource=\"http://lod.gesis.org/thesoz/concept_10063164\"/>", "")
				.replace("<!-- status; 1;published, 2:unpublished, 3: under review, 4: review finalized -->", "")
				.replace("<!-- If a translation is added -->", "");

		// TODO: remove this after the FlatDB dialog definition fixed - fix publication date (remove)
		int index = xmlContent.indexOf(" publicationDate=\"");
		if( index > 0 ) {
			xmlContent = xmlContent.substring(0, index) + xmlContent.substring(index + 29); 
		}
		
		StringJoiner skosXml = new StringJoiner("\n\n");
		skosXml.add( xmlContent );
		skosXml.add( cvItem
						.getFlattenedCvConceptStreams()
						.map( x -> SaxParserUtils.filterSkosDoc(filteredTag, filteredLanguages, x.getContent())
								.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")
								// TODO: remove this after the FlatDB dialog definition fixed
								.replace("<skos:inScheme rdf:resource=\"http://lod.gesis.org/thesoz/scheme\"/>", "<skos:inScheme rdf:resource=\""+ inSchemeUri + "\"/>")
								.replace("<skos:narrower rdf:resource=\"http://lod.gesis.org/thesoz/concept_10063164\"/>", "")
							)
						.collect( Collectors.joining("\n\n"))
				);
		
		skosXml.add( "\n</rdf:RDF>");
		
		// return string and remove any blank lines
		return skosXml.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
	}
	
	private File generateExportFile( DownloadType type, List<VersionDTO> exportVersions) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String cvUrn = null;
		String docId = null;
		String docVersionOf = null;
		String docVersion = null;
		String docLicense = null;
		// sort code
		for( VersionDTO versionExp : exportVersions) {
			for( ConceptDTO concept : versionExp.getConcepts()) {
				if( concept.getPosition() == null)
					concept.setPosition(999);
			}
			versionExp.setConcepts( 
					versionExp.getConcepts().stream()
					.sorted((c1,c2) -> c1.getPosition().compareTo(c2.getPosition()))
					.collect(Collectors.toCollection(LinkedHashSet::new)));
			
			// find one canonicalUrl
			if( cvUrn == null ) {
				if( versionExp.getCanonicalUri() != null ) {
					int index = versionExp.getCanonicalUri().lastIndexOf(":");
					cvUrn = versionExp.getCanonicalUri().substring(0, index);
				}
				if( type.equals( DownloadType.SKOS)) {
					docId = versionExp.getSkosUri();
					int index = docId.lastIndexOf("_");
					docVersionOf = docId.substring(0, index);
					docVersion = docId.substring(index + 1);
					Optional<LicenceDTO> dLicence = licenses.stream().filter( l -> l.getId().equals( versionExp.getLicenseId())).findFirst();
					if(dLicence.isPresent())
						docLicense = dLicence.get().getName();
				}
			}
		}
		
		if( type.equals( DownloadType.SKOS)) {
			map.put("docId", docId);
			map.put("docNotation", slVersion.getNotation());
			map.put("docVersionOf", docVersionOf);
			map.put("docVersion", docVersion);
			map.put("docLicense", docLicense);
			map.put("docVersionNotes", slVersion.getVersionNotes());
			map.put("docVersionChanges", slVersion.getVersionChanges());
			map.put("docRight", "Copyright © " + agency.getName() + " " + year);
			map.put("codes", codeService.findByVocabularyAndVersion(vocabulary.getId(), slVersion.getId()));
		}
		map.put("cvUrn", cvUrn);
		map.put("versions", exportVersions);
		map.put("agency", agency);
		map.put("license", license);
		map.put("year", year);
		map.put("baseUrl", UI.getCurrent().getPage().getLocation().getScheme() + "://" + configurationService.getServerBaseUrl() + configurationService.getServerContextPath());
		
		return generateFileByThymeleafTemplate(generateOnDemandFileName( type , false), "export", map, type);
	}
	
	private String generateOnDemandFileName( DownloadType type, boolean withFileFormat) {
		StringBuilder title = new StringBuilder();
		title.append( vocabulary.getNotation() + "_");
		List<String> selectedLanguage = null;
		if( type == DownloadType.PDF)
			selectedLanguage = exportCvItems.stream().filter( f -> f.exportPdfCb.getValue() ).map( m -> m.getSelectedVersionLabel(publishView)).collect( Collectors.toList());
		else if( type == DownloadType.HTML )
			selectedLanguage = exportCvItems.stream().filter( f -> f.exportHtmlCb.getValue() ).map( m -> m.getSelectedVersionLabel(publishView)).collect( Collectors.toList());
		else
			selectedLanguage = exportCvItems.stream().filter( f -> f.exportSkosCb.getValue() ).map( m -> m.getSelectedVersionLabel(publishView)).collect( Collectors.toList());
		
		title.append( String.join("-", selectedLanguage));
		title.append( "_" + LocalDateTime.now().format(dateFormatter) + (withFileFormat ? "." + type.toString() : ""));
		return title.toString();
	}
	
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																													
	private Set<String> getFilteredLanguages() {
		// get all available language from vocabulary
		Set<String> vocabLanguages = vocabulary.getLanguages();
		Set<String> checkedLanguages = exportCvItems.stream()
				.filter( f -> f.exportSkosCb.getValue() == true)
				.map( x -> x.language )
				.collect( Collectors.toSet());
		
		vocabLanguages.removeAll(checkedLanguages);
		
		return vocabLanguages;
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
		
		switch ( type ) {
		case PDF:
			return createPdfFile(outputFile, templateEngine.process("html/" +templateName + "_" + type.toString() , ctx));
		case HTML:
			return createTextFile(outputFile, templateEngine.process("html/" +templateName + "_" + type.toString() , ctx));
		case SKOS:
			return createTextFile(outputFile, templateEngine.process("xml/" +templateName + "_" + type.toString() , ctx).replaceAll("(?m)^[ \t]*\r?\n", ""));
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
			this(language, versions, false,false,false);
		}
		public ExportCV(String language, List<VersionDTO> versions, boolean exportSkos, boolean exportPdf, boolean exportHtml) {
			this.language = language;
			this.versions = versions;
			this.exportSkosCb.setValue( exportSkos );
			this.exportPdfCb.setValue( exportPdf );
			this.exportHtmlCb.setValue( exportHtml );
			
//			this.exportSkosCb.addValueChangeListener(skosCheckBoxListener);
//			this.exportPdfCb.addValueChangeListener(pdfCheckBoxListener);
//			this.exportHtmlCb.addValueChangeListener(htmlCheckBoxListener);
			
			this.versionOption.setItems( versions );
			this.versionOption.setWidth("100%");
			this.versionOption.setItemCaptionGenerator( vers -> {
				return publishView ? vers.getNumber(): vers.getNumber() + " (" + vers.getStatus() + ")";
			});
			this.versionOption.setValue( versions.get(0) );
			this.versionOption.setEmptySelectionAllowed( false );
			this.versionOption.setTextInputAllowed( false );
			
			this.type = versions.get(0).getItemType();
			if( this.type.equals(ItemType.SL.toString())) {
				this.versionOption.setReadOnly( true );
			}
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
		public String getSelectedVersionLabel( boolean includeStatus) {
			VersionDTO version = versionOption.getValue();
			return getLanguage() + "(" + (includeStatus ? version.getNumber() + "-" + version.getStatus().toLowerCase() : version.getNumber()) + ")";
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

	public Map<String, List<VersionDTO>> getOrderedLanguageVersionMap() {
		return orderedLanguageVersionMap;
	}

	public void setOrderedLanguageVersionMap(Map<String, List<VersionDTO>> orderedLanguageVersionMap) {
		this.orderedLanguageVersionMap = orderedLanguageVersionMap;
	}

}
