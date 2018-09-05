package eu.cessda.cvmanager.ui.layout;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import org.gesis.wts.service.dto.AgencyDTO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader;
import eu.cessda.cvmanager.export.utils.OnDemandFileDownloader.OnDemandStreamResource;
import eu.cessda.cvmanager.export.utils.SaxParserUtils;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.DetailsView;

public class VersionLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final UIEventBus eventBus;
	private final AgencyDTO agency;
	private final VocabularyDTO vocabulary;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
	private final VocabularyChangeService vocabularyChangeService;
	private final ConfigurationService configService;
	private String baseUrl;
	private Map<String, List<VersionDTO>> versionMap;
	
	public VersionLayout(I18N i18n, Locale locale, UIEventBus eventBus, 
			AgencyDTO agencyDTO, VocabularyDTO vocabularyDTO,
			VocabularyChangeService vocabularyChangeService,
			ConfigurationService configService) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.eventBus = eventBus;
		this.agency = agencyDTO;
		this.vocabulary = vocabularyDTO;
		this.vocabularyChangeService = vocabularyChangeService;
		this.configService = configService;
		
		this
			.withFullWidth();
		
		init();
	}

	private void init() {
		baseUrl = configService.getServerContextPath() + "/#!" + DetailView.VIEW_NAME + "/" + vocabulary.getNotation() + "?url=";
		versionMap = VersionDTO.generateVersionMap( vocabulary.getVersions());
		
	}
	
	public void refreshContent( VersionDTO version ) {
		this.removeAllComponents();
		if( version.getItemType().equals(ItemType.SL.toString())) {
			this.add( new MLabel("<h2>Source</h2>").withFullWidth().withContentMode( ContentMode.HTML));
		}
		else {
			this.add( new MLabel("<h2>Translation</h2>").withFullWidth().withContentMode( ContentMode.HTML));
		}
		boolean showSlVersion = false;
		boolean showTlVersion = false;
		for(Map.Entry<String,List<VersionDTO>> eachVersions : versionMap.entrySet()) {
			if(eachVersions.getKey().startsWith( ItemType.SL.toString())) {
				for(VersionDTO orderedVer : eachVersions.getValue()) {
					// only shows its versions
					if( !version.getLanguage().equals( orderedVer.getLanguage()))
						continue;
					
					if( orderedVer.getNumber() == null )
						continue;
					//only show equal or lower version
					if( version.getNumber() == null )
						showSlVersion = true;
					else
						if( orderedVer.getNumber() != null && orderedVer.getNumber().indexOf( version.getNumber() ) == 0) 
							showSlVersion = true;
					
					if( orderedVer.getStatus().equals( Status.PUBLISHED.toString()) && showSlVersion)
						this.add( generateVersionSl(orderedVer));
				}
			} else {
				for(VersionDTO orderedVer : eachVersions.getValue()) {
					// only shows its versions
					if( !version.getLanguage().equals( orderedVer.getLanguage()))
						continue;
					
					if( orderedVer.getNumber() == null )
						continue;
					//only show equal or lower version
					if( version.getNumber() == null )
						showTlVersion = true;
					else
						if( orderedVer.getNumber() != null && orderedVer.getNumber().indexOf( version.getNumber() ) == 0) 
							showTlVersion = true;
					
					if( orderedVer.getStatus().equals( Status.PUBLISHED.toString()) && showTlVersion)
						this.add( generateVersionTl(orderedVer));
				}
			}
		}
	}
	
	public MCssLayout generateVersionSl( VersionDTO versionDTO ) {
		MCssLayout versionLayout = new MCssLayout();
		String cvUrl = null;
		
		MLabel infoVersion = new MLabel().withContentMode( ContentMode.HTML).withFullWidth();
		MLabel noteVersion = new MLabel().withContentMode( ContentMode.HTML).withFullWidth();
		MLabel changeVersion = new MLabel().withContentMode( ContentMode.HTML).withFullWidth();
		
//		infoVersion
//		.withValue("<h2>Source language</h2>" +
//				"<a href='" + baseUrl  + versionDTO.getUri() + "'>" +versionDTO.getLanguage() + ": " + versionDTO.getNumber() +"</a> " +
//				" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication:" + versionDTO.getPublicationDate());
	
		try {
			cvUrl = baseUrl + URLEncoder.encode(versionDTO.getUri(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			cvUrl = baseUrl + versionDTO.getUri();
			e.printStackTrace();
		}
		
		infoVersion
			.withValue("<h2>" +
					"<a href='" + cvUrl + "'>" +versionDTO.getLanguage() + ": " + versionDTO.getNumber() +"</a> " +
					" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication: " + versionDTO.getPublicationDate() + "</h2>");
		
		noteVersion
			.withValue("<h2>Version notes</h2>" +
				versionDTO.getVersionNotes());
		
		
		changeVersion
			.withValue("<h2>Changes since previous version</h2>" +
				versionDTO.getVersionChanges());
		
		versionLayout
			.withStyleName( "version-item" )
			.add(
				infoVersion,
				noteVersion,
				changeVersion
			);
		
		return versionLayout;
	}
	
	public MCssLayout generateVersionTl( VersionDTO versionDTO ) {
		MCssLayout versionLayout = new MCssLayout();
		String cvUrl = null;
		
		MLabel infoVersion = new MLabel().withContentMode( ContentMode.HTML);
		MLabel noteVersion = new MLabel().withContentMode( ContentMode.HTML);
		MLabel changeVersion = new MLabel().withContentMode( ContentMode.HTML);
		
		infoVersion
			.withValue("<h2>" +
					versionDTO.getLanguage() + ": " + versionDTO.getNumber() +
					" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication:" + versionDTO.getPublicationDate() + "</h2>");
		
		noteVersion
			.withValue("<h2>Version notes</h2>" +
				versionDTO.getVersionNotes());
		
		changeVersion
			.withValue("<h2>Changes since previous version</h2>" +
					versionDTO.getVersionChanges());
		
		versionLayout
			.withStyleName( "version-item" )
			.add(
				infoVersion,
				noteVersion,
				changeVersion
			);
		
		return versionLayout;
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
	
	

}
