package eu.cessda.cvmanager.ui.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;

import eu.cessda.cvmanager.model.CvItem;

public class ExportLayout  extends MCssLayout implements Translatable {
	
	private final I18N i18n;
	private final Locale locale;
	private final UIEventBus eventBus;
	private final CvItem cvItem;
	
	private Grid<ExportCV> exportGrid;
	private List<ExportCV> exportCvItems;
	
	public ExportLayout(I18N i18n, Locale locale, UIEventBus eventBus, CvItem cvItem) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.eventBus = eventBus;
		this.cvItem = cvItem;
		
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
		
		this.add( exportGrid );
		
	}
	

	@Override
	public void updateMessageStrings(Locale locale) {
		// TODO Auto-generated method stub
		
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
