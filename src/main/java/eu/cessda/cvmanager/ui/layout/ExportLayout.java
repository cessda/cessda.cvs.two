package eu.cessda.cvmanager.ui.layout;

import com.lowagie.text.DocumentException;
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
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.*;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ExportLayout extends MCssLayout implements Translatable
{

	private static final String VAADIN_STYLE_MARGINLEFT20 = "marginleft20";

	private final Set<String> filteredTag = new HashSet<>();

	private static final long serialVersionUID = -2461005203070668382L;

	private final transient CvItem cvItem;
	private final transient ConfigurationService configurationService;
	private final transient VersionService versionService;
	private final transient CodeService codeService;
	private final transient SpringTemplateEngine templateEngine;
	private final transient DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd_HH-mm" );
	private final VocabularyDTO vocabulary;
	private final AgencyDTO agency;
	private final transient StardatDDIService stardatDDIService;
	private static final Logger log = LoggerFactory.getLogger( ExportLayout.class );

	private List<LicenceDTO> licenses;
	private LicenceDTO license;
	private VersionDTO slVersion;
	private final transient List<ExportCV> exportCvItems = new ArrayList<>();
	private MGrid<ExportCV> exportGrid = new MGrid<>( ExportCV.class );
	private final Map<String, List<VersionDTO>> orderedLanguageVersionMap = new LinkedHashMap<>();
	private MCssLayout sectionLayout = new MCssLayout();
	private MVerticalLayout gridLayout = new MVerticalLayout();
	private MButton exportSkos = new MButton( "Export Skos" ).withStyleName( VAADIN_STYLE_MARGINLEFT20 );
	private MButton exportPdf = new MButton( "Export Pdf" ).withStyleName( VAADIN_STYLE_MARGINLEFT20 );
	private MButton exportHtml = new MButton( "Export Html" ).withStyleName( VAADIN_STYLE_MARGINLEFT20 );

	private MCheckBox skosCheckBox = new MCheckBox( "SKOS" );
	private MCheckBox pdfCheckBox = new MCheckBox( "PDF" );
	private MCheckBox htmlCheckBox = new MCheckBox( "HTML" );
	private boolean activateSkosListener = true;
	private boolean activatePdfListener = true;
	private boolean activateHtmlListener = true;

	public void updateGrid( VersionDTO pivotVersion, Map<String, List<VersionDTO>> versionMap )
	{
		orderedLanguageVersionMap.clear();
		orderedLanguageVersionMap.putAll( versionMap );
		exportCvItems.clear();

		Map<String, List<VersionDTO>> filteredVersionMap = getFilteredVersionMap( pivotVersion,
				orderedLanguageVersionMap );
		filteredVersionMap.forEach( ( k, v ) -> exportCvItems.add( new ExportCV( k, v ) ) );

		if ( pivotVersion.getLicenseId() != null )
		{
			licenses.stream().filter( p -> p.getId().equals( pivotVersion.getLicenseId() ) ).findFirst()
					.ifPresent( theLicense -> this.license = theLicense );
		}

		if ( pivotVersion.getStatus().equals( Status.PUBLISHED.toString() ) )
		{
			year = pivotVersion.getPublicationDate().getYear();
		}

		exportGrid.setItems( exportCvItems );
	}

	private boolean publishView;
	private int year = LocalDate.now().getYear();
	private String inSchemeUri = "";

	public ExportLayout( I18N i18n, Locale locale, UIEventBus eventBus, CvItem cvItem, VocabularyDTO vocabulary,
						 AgencyDTO agency, VersionService versionService, CodeService codeService,
						 ConfigurationService configurationService, StardatDDIService stardatDDIService, List<LicenceDTO> licenses,
						 SpringTemplateEngine templateEngine, boolean isPublished )
	{
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
		if (!publishView)
			exportSkos.setVisible(false);

		HeaderRow headerRow = exportGrid.getDefaultHeaderRow();

		exportGrid.addStyleNames("export-grid");
		exportGrid.removeAllColumns();
		exportGrid.withFullWidth().withHeight("200px");

		exportGrid.addColumn(cVersion -> Language.getByIso(cVersion.getLanguage()).getFormatted())
				.setCaption("Language").setExpandRatio(2).setId("languageClm");
		exportGrid.addColumn(ExportCV::getVersionOption, new ComponentRenderer()).setCaption("Version")
				.setExpandRatio(2).setId("versionClm");
		if (publishView) {
			exportGrid.addColumn(ExportCV::getExportSkosCb, new ComponentRenderer()).setCaption("Skos")
					.setExpandRatio(1).setId("skosClm");

			headerRow.getCell("skosClm").setComponent(skosCheckBox);

			skosCheckBox.addValueChangeListener(e -> {
				if (activateSkosListener)
					exportCvItems.forEach(export -> export.getExportSkosCb().setValue(e.getValue()));
				activateSkosListener = true;
			});
		}
		exportGrid.addColumn(ExportCV::getExportPdfCb, new ComponentRenderer()).setCaption("Pdf").setExpandRatio(1)
				.setId("pdfClm");
		headerRow.getCell("pdfClm").setComponent(pdfCheckBox);

		pdfCheckBox.addValueChangeListener(e -> {
			if (activatePdfListener)
				exportCvItems.forEach(export -> export.getExportPdfCb().setValue(e.getValue()));
			activatePdfListener = true;
		});

		exportGrid.addColumn(ExportCV::getExportHtmlCb, new ComponentRenderer()).setCaption("Html").setExpandRatio(1)
				.setId("htmlClm");
		headerRow.getCell("htmlClm").setComponent(htmlCheckBox);

		htmlCheckBox.addValueChangeListener(e -> {
			if (activateHtmlListener)
				exportCvItems.forEach(export -> export.getExportHtmlCb().setValue(e.getValue()));
			activateHtmlListener = true;
		});

		gridLayout.withFullSize().withComponent(exportGrid).withExpand(exportGrid, 1);

		sectionLayout.withFullSize().add(gridLayout, exportSkos, exportPdf, exportHtml);

		this.withFullWidth().add(sectionLayout);
		if (publishView) {
			OnDemandFileDownloader onDemandSkosFileDownloader = new OnDemandFileDownloader(
					createOnDemandResource(DownloadType.SKOS));
			onDemandSkosFileDownloader.extend(exportSkos);
		}
		OnDemandFileDownloader onDemandSkosFileDownloaderPdf = new OnDemandFileDownloader(
				createOnDemandResource( DownloadType.PDF ) );
		onDemandSkosFileDownloaderPdf.extend( exportPdf );

		OnDemandFileDownloader onDemandSkosFileDownloaderHtml = new OnDemandFileDownloader(
				createOnDemandResource( DownloadType.HTML ) );
		onDemandSkosFileDownloaderHtml.extend( exportHtml );
	}

	public enum DownloadType
	{
		SKOS( "rdf" ), PDF( "pdf" ), HTML( "html" );

		private final String type;

		DownloadType( String s )
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

	public Map<String, List<VersionDTO>> getFilteredVersionMap(VersionDTO pivotVersion,
			Map<String, List<VersionDTO>> versionMap) {
		Map<String, List<VersionDTO>> filteredVersionMap = new LinkedHashMap<>();
		if (pivotVersion == null && versionMap.entrySet().iterator().hasNext()) {
			// get latest SL version
			// SL version is always in the first entry
			pivotVersion = versionMap.entrySet().iterator().next().getValue().get(0);

		}
		// always get one SL version
		if (pivotVersion.getItemType().equals(ItemType.TL.toString())) {
			Optional<VersionDTO> versionDTO = vocabulary.getVersionByUri(pivotVersion.getUriSl());
			if ( versionDTO.isPresent() )
			{
				pivotVersion = versionDTO.get();
			}
		}

		slVersion = pivotVersion;

		for (Map.Entry<String, List<VersionDTO>> entry : versionMap.entrySet()) {
			if ( entry.getKey().equals( pivotVersion.getLanguage() ) )
			{
				filteredVersionMap.put( entry.getKey(), Collections.singletonList( pivotVersion ) );
			}
			else
			{
				// get only SL that within SL version
				List<VersionDTO> oldValue = entry.getValue();
				List<VersionDTO> newValue = new ArrayList<>();
				for ( VersionDTO eachVersion : oldValue )
				{
					if ( publishView &&
							!eachVersion.getStatus().equals( Status.PUBLISHED.toString() ) ) // only show published one
					{
						continue;
					}
					if ( eachVersion.getUriSl() != null && eachVersion.getUriSl().equals( pivotVersion.getUri() ) )
					{
						newValue.add( eachVersion );
					}
				}
				if ( !newValue.isEmpty() )
				{
					filteredVersionMap.put( entry.getKey(), newValue );
				}
			}
		}

		return filteredVersionMap;
	}

	private OnDemandStreamResource createOnDemandResource(DownloadType downloadType) {
		return new OnDemandStreamResource()
		{
			private final ArrayList<VersionDTO> exportVersions = new ArrayList<>();
			private static final long serialVersionUID = 3421089012275806929L;

			@Override
			public InputStream getStream()
			{

				exportVersions.clear();
				exportCvItems.stream().map( ExportCV::getFotmatVersionMap )
						.filter( fotmatVersionMap -> fotmatVersionMap.get( downloadType ) != null )
						.forEach( fotmatVersionMap -> exportVersions.add( fotmatVersionMap.get( downloadType ) ) );

				if ( exportVersions.isEmpty() )
				{
					Notification notif = new Notification( "", "No language selected!",
							Notification.TYPE_WARNING_MESSAGE );

					notif.setDelayMsec( 0 );
					notif.setPosition( Position.BOTTOM_RIGHT );

					// Show it in the page
					notif.show( Page.getCurrent() );

					return null;
				}

				if ( downloadType == DownloadType.HTML || downloadType == DownloadType.PDF ||
						downloadType == DownloadType.SKOS )
				{
					try
					{
						return new ByteArrayInputStream( generateExportFile( downloadType, exportVersions )
								.toByteArray() );
					}
					catch ( IOException e )
					{
						log.error( "Export failed!", e );
					}
				}
				return null;
			}

			@Override
			public String getFilename()
			{
				String fileName = generateOnDemandFileName( downloadType );
				log.debug( "Created download with filename: {}", fileName );
				return fileName;
			}
		};
	}

	private ByteArrayOutputStream generateExportFile( DownloadType type, List<VersionDTO> exportVersions ) throws IOException
	{
		Map<String, Object> map = new HashMap<>();
		String cvUrn = null;
		String docId = null;
		String docVersionOf = null;
		String docVersion = null;
		String docLicense = null;
		// sort code
		for ( VersionDTO versionExp : exportVersions )
		{
			versionExp.getConcepts().stream().filter( concept -> concept.getPosition() == null )
					.forEach( concept -> concept.setPosition( 999 ) );
			versionExp.setConcepts( versionExp.getConcepts().stream()
					.sorted( Comparator.comparing( ConceptDTO::getPosition ) )
					.collect( Collectors.toCollection( LinkedHashSet::new ) ) );

			// find one canonicalUrl
			if ( versionExp.getCanonicalUri() != null )
			{
				int index = versionExp.getCanonicalUri().lastIndexOf( ':' );
				cvUrn = versionExp.getCanonicalUri().substring( 0, index );
			}
			if ( type.equals( DownloadType.SKOS ) )
			{
				docId = versionExp.getSkosUri();
				int index = docId.lastIndexOf( '_' );
				docVersionOf = docId.substring( 0, index );
				docVersion = docId.substring( index + 1 );
				Optional<LicenceDTO> dLicence = licenses.stream()
						.filter( l -> l.getId().equals( versionExp.getLicenseId() ) ).findFirst();
				if ( dLicence.isPresent() )
				{
					docLicense = dLicence.get().getName();
				}
			}
		}

		if (type.equals(DownloadType.SKOS)) {
			map.put("docId", docId);
			map.put("docNotation", slVersion.getNotation());
			map.put("docVersionOf", docVersionOf);
			map.put("docVersion", docVersion);
			map.put("docLicense", docLicense);
			map.put("docVersionNotes", slVersion.getVersionNotes());
			map.put("docVersionChanges", slVersion.getVersionChanges());
			map.put("docRight", "Copyright © " + agency.getName() + " " + year);
			List<CodeDTO> codes = codeService.findByVocabularyAndVersion(vocabulary.getId(), slVersion.getId());
			// in some case the codes are empty and workflow codes are needed
			if ( codes.isEmpty() )
			{
				codes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
			}
			map.put( "codes", codes );
		}
		map.put( "cvUrn", cvUrn );
		map.put( "versions", exportVersions );
		map.put( "agency", agency );
		map.put( "license", license );
		map.put( "year", year );
		map.put( "baseUrl", UI.getCurrent().getPage().getLocation().getScheme() + "://"
				+ configurationService.getServerBaseUrl() + configurationService.getServerContextPath() );

		return generateFileByThymeleafTemplate( "export", map, type );
	}

	private String generateOnDemandFileName( DownloadType type )
	{
		StringBuilder title = new StringBuilder();
		title.append( vocabulary.getNotation() ).append( "_" );
		List<String> selectedLanguage;
		if ( type == DownloadType.PDF )
		{
			selectedLanguage = exportCvItems.stream().filter( f -> f.exportPdfCb.getValue() )
					.map( m -> m.getSelectedVersionLabel( publishView ) ).collect( Collectors.toList() );
		}
		else if ( type == DownloadType.HTML )
		{
			selectedLanguage = exportCvItems.stream().filter( f -> f.exportHtmlCb.getValue() )
					.map( m -> m.getSelectedVersionLabel( publishView ) ).collect( Collectors.toList() );
		}
		else
		{
			selectedLanguage = exportCvItems.stream().filter( f -> f.exportSkosCb.getValue() )
					.map( m -> m.getSelectedVersionLabel( publishView ) ).collect( Collectors.toList() );
		}

		title.append( String.join( "-", selectedLanguage ) ).append( "_" )
				.append( LocalDateTime.now().format( dateFormatter ) ).append( "." ).append( type );
		return title.toString();
	}

	private Set<String> getFilteredLanguages()
	{
		// get all available language from vocabulary
		Set<String> vocabLanguages = vocabulary.getLanguages();
		Set<String> checkedLanguages = exportCvItems.stream().filter( f -> f.exportSkosCb.getValue() )
				.map( x -> x.language ).collect( Collectors.toSet() );

		vocabLanguages.removeAll( checkedLanguages );

		return vocabLanguages;
	}

	private ByteArrayOutputStream generateFileByThymeleafTemplate( String templateName, Map<String, Object> map,
																   DownloadType type ) throws IOException
	{
		Context ctx = new Context();
		map.forEach( ctx::setVariable );

		if ( log.isTraceEnabled() ) log.trace( "Type: {}", type.name() );
		switch ( type )
		{
			case PDF:
				try
				{
					return createPdfFile( templateEngine.process( "html/" + templateName + "_" + type, ctx ) );
				}
				catch ( DocumentException e )
				{
					log.error( "PDF creation failed!", e );
					break;
				}
			case HTML:
				return createTextFile( templateEngine.process( "html/" + templateName + "_" + type, ctx ) );
			case SKOS:
				String content = templateEngine.process( "xml/" + templateName + "_" + type, ctx );
				return createTextFile( content.replaceAll( "(?m)^[ \t]*\r?\n", "" ) );
		}
		return null;
	}

	private ByteArrayOutputStream createTextFile( String contents ) throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new OutputStreamWriter( stream, StandardCharsets.UTF_8 ).write( contents );
		return stream;
	}

	public ByteArrayOutputStream createPdfFile( String contents ) throws com.lowagie.text.DocumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString( contents );
		renderer.layout();
		renderer.createPDF( os, false );
		renderer.finishPDF();
		return os;
	}

	@Override
	public void updateMessageStrings( Locale locale )
	{
		// No internationalized string used in this class.

	}

	class ExportCV
	{
		private String language;
		private String type;
		private MCheckBox exportSkosCb = new MCheckBox();
		private MCheckBox exportPdfCb = new MCheckBox();
		private MCheckBox exportHtmlCb = new MCheckBox();
		private List<VersionDTO> versions;
		private ComboBox<VersionDTO> versionOption = new ComboBox<>();

		public ExportCV(String language, List<VersionDTO> versions) {
			this(language, versions, false, false, false);
		}

		public ExportCV(String language, List<VersionDTO> versions, boolean exportSkos, boolean exportPdf,
				boolean exportHtml) {
			this.language = language;
			this.versions = versions;
			this.exportSkosCb.setValue( exportSkos );
			this.exportPdfCb.setValue( exportPdf );
			this.exportHtmlCb.setValue( exportHtml );

//			this.exportSkosCb.addValueChangeListener(skosCheckBoxListener);
//			this.exportPdfCb.addValueChangeListener(pdfCheckBoxListener);
//			this.exportHtmlCb.addValueChangeListener(htmlCheckBoxListener);

			this.versionOption.setItems( versions );
			this.versionOption.setWidth( "100%" );
			this.versionOption.setItemCaptionGenerator( vers ->
					publishView ? vers.getNumber() : vers.getNumber() + " (" + vers.getStatus() + ")" );
			this.versionOption.setValue( versions.get( 0 ) );
			this.versionOption.setEmptySelectionAllowed( false );
			this.versionOption.setTextInputAllowed( false );

			this.type = versions.get( 0 ).getItemType();
			if ( this.type.equals( ItemType.SL.toString() ) )
			{
				this.versionOption.setReadOnly( true );
			}
		}

		public Map<DownloadType, VersionDTO> getFotmatVersionMap()
		{
			Map<DownloadType, VersionDTO> formatVersionMap = new EnumMap<>( DownloadType.class );
			VersionDTO selectedVersion = versionOption.getValue();
			if ( exportSkosCb.getValue() )
			{
				formatVersionMap.put( DownloadType.SKOS, selectedVersion );
			}

			if ( exportPdfCb.getValue() )
			{
				formatVersionMap.put( DownloadType.PDF, selectedVersion );
			}

			if ( exportHtmlCb.getValue() )
			{
				formatVersionMap.put( DownloadType.HTML, selectedVersion );
			}

			return formatVersionMap;
		}

		public String getSelectedVersionLabel(boolean includeStatus) {
			VersionDTO version = versionOption.getValue();
			return getLanguage() + "(" + (includeStatus ? version.getNumber() + "-" + version.getStatus().toLowerCase()
					: version.getNumber()) + ")";
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

}
