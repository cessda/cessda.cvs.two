package eu.cessda.cvmanager.ui.view.importing;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.Query;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import eu.cessda.cvmanager.ui.view.window.DialogImportCsvCodeWindow;
import org.gesis.wts.domain.enumeration.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class CsvImportLayout extends MCssLayout implements Translatable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(CsvImportLayout.class);

	private File tempCsvFile;

	private MGrid<CsvRow> csvGrid = new MGrid<>(CsvRow.class);
	private MLabel languageComboBox;
	private CsvFileData csvFileData;

	private TextField valueOfCodeTextField;
	private TextField termTextField;
	private TextField definitionTextField;

	private Upload upload;
	private Language selectedLanguage;

	private List<String[]> rawDataList;
	private DialogImportCsvCodeWindow dialogWindow;
	private List<CsvRow> csvRows = new ArrayList<>();

	public CsvImportLayout( I18N i18n, Language selectedLanguage, DialogImportCsvCodeWindow dialogWindow )
	{
		super();
		this.selectedLanguage = selectedLanguage;
		this.dialogWindow = dialogWindow;
		init();
	}

	private void init()
	{
//      /* Create and configure the upload component */
		upload = new Upload( "Upload CSV File", ( filename, mimeType ) ->
		{
			try
			{
				/*
				 * Here, we'll stored the uploaded file as a temporary file. No doubt there's a
				 * way to use a ByteArrayOutputStream, a reader around it, use ProgressListener
				 * (and a progress bar) and a separate reader thread to populate a container
				 * *during* the update. This is quick and easy example, though.
				 */
				tempCsvFile = File.createTempFile( "temp_codes", ".csv" );

				return new FileOutputStream( tempCsvFile );
			}
			catch ( IOException ioException )
			{
				log.error( "Could not write file", ioException );
				return null;
			}
		} );

		HorizontalLayout textFieldsHLayout = new HorizontalLayout();

		upload.addFinishedListener( finishedEvent ->
		{
			try
			{
				/* Let's build a container from the CSV File */
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader( new FileInputStream( tempCsvFile ), StandardCharsets.UTF_8 ) );
				BufferedReader rawDataBufferedReader = new BufferedReader(
						new InputStreamReader( new FileInputStream( tempCsvFile ), StandardCharsets.UTF_8 ) );

				CsvFileUtil csvFileUtil = new CsvFileUtil();

				int termColumnIndex = Integer.parseInt( termTextField.getValue() );
				int definitionColumnIndex = Integer.parseInt( definitionTextField.getValue() );
				int valueOfCodeColumnIndex = Integer.parseInt( valueOfCodeTextField.getValue() );

				csvFileData = csvFileUtil.readData( bufferedReader, termColumnIndex, definitionColumnIndex,
						valueOfCodeColumnIndex );
				rawDataList = csvFileUtil.readRawData( rawDataBufferedReader );

				/* Finally, let's update the table with the container */
				csvGrid.setCaption( finishedEvent.getFilename() );

				updateCsvGrid();

				csvGrid.setVisible( true );

				upload.setVisible( false );
				textFieldsHLayout.setVisible( false );
				dialogWindow.getImportButton().setVisible( true );

				HasValue.ValueChangeListener<String> valueChangeListener = valueChangeEvent ->
				{
					String termIndex = termTextField.getValue();
					String definitionIndex = definitionTextField.getValue();
					String valueOfCodeIndex = valueOfCodeTextField.getValue();

					if ( !isNumberString( termIndex ) || !isNumberString( definitionIndex )
							|| !isNumberString( valueOfCodeIndex ) )
					{
						return;
					}

					int updatedTermColumnIndex = Integer.parseInt( termIndex );
					int updatedDefinitionColumnIndex = Integer.parseInt( definitionIndex );
					int updatedValueOfCodeColumnIndex = Integer.parseInt( valueOfCodeIndex );

					for ( String[] currentLine : rawDataList )
					{
						csvRows.add( csvFileUtil.createCsvRow( currentLine, updatedTermColumnIndex,
								updatedDefinitionColumnIndex, updatedValueOfCodeColumnIndex ) );
					}

					csvGrid.setItems( csvRows );
				};

				termTextField.addValueChangeListener( valueChangeListener );
				definitionTextField.addValueChangeListener( valueChangeListener );
				valueOfCodeTextField.addValueChangeListener( valueChangeListener );

				bufferedReader.close();
				rawDataBufferedReader.close();

				Files.delete( tempCsvFile.toPath() );
			}
			catch ( IOException ioException )
			{
				log.error( "IOException!", ioException );
			}
		});

		/* Table to show the contents of the file */
		csvGrid.setSizeFull();
		csvGrid.setVisible(false);
		csvGrid.setHeightByRows(13);
		csvGrid.setRowHeight(45);
		csvGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
		csvGrid.getEditor().setEnabled(true);

		languageComboBox = new MLabel(Language.getByIso(selectedLanguage.toString()).getFormatted());
		languageComboBox.setCaption("Language");
		languageComboBox.withWidth("200px");

		valueOfCodeTextField = new TextField("Code column number ");
		valueOfCodeTextField.setRequiredIndicatorVisible(true);
		valueOfCodeTextField.setValue("0");

		termTextField = new TextField("Term column number");
		termTextField.setRequiredIndicatorVisible(true);
		termTextField.setValue("1");

		definitionTextField = new TextField("Definition colum number");
		definitionTextField.setRequiredIndicatorVisible(true);
		definitionTextField.setValue("2");

		textFieldsHLayout.addComponents(valueOfCodeTextField, termTextField, definitionTextField);

		HorizontalLayout horizontalLayout = new HorizontalLayout();

		horizontalLayout.addComponents(languageComboBox, upload);

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(true);
		verticalLayout.setSpacing(true);

		verticalLayout.addComponent(textFieldsHLayout);
		verticalLayout.addComponent(horizontalLayout);
		verticalLayout.addComponent(csvGrid);

		verticalLayout.setComponentAlignment(textFieldsHLayout, Alignment.TOP_CENTER);
		verticalLayout.setComponentAlignment(horizontalLayout, Alignment.TOP_CENTER);
		verticalLayout.setComponentAlignment(csvGrid, Alignment.TOP_CENTER);

		HasValue.ValueChangeListener<String> valueChangeListener = valueChangeEvent -> {
			TextField textField = (TextField) valueChangeEvent.getComponent();

			validateTextFieldIsEmpty(textField);
			validateTextFieldIsNumberValue(textField);
			validateTextFieldsOnSameValue();

			upload.setEnabled(isTextFieldsNotEmpty());
		};

		termTextField.addValueChangeListener(valueChangeListener);
		definitionTextField.addValueChangeListener(valueChangeListener);
		valueOfCodeTextField.addValueChangeListener(valueChangeListener);

		this.withFullSize().addComponents(verticalLayout);
	}

	/**
	 * Validate is TextField value is empty and if empty set required.
	 *
	 * @param textField - source text field
	 */
	private void validateTextFieldIsEmpty(TextField textField) {
		textField.setRequiredIndicatorVisible(textField.isEmpty());
	}

	/**
	 * Validate is text field value is number. If not number it will show warning
	 * message and set value to "".
	 *
	 * @param textField - source text field
	 */
	private void validateTextFieldIsNumberValue(TextField textField) {
		String textFieldValue = textField.getValue();

		if (!isStringNotEmpty(textFieldValue)) {
			return;
		}

		if (!isNumberString(textFieldValue)) {
			Notification warningNotification = new Notification("Warning", "Please, enter integer value.",
					Notification.Type.WARNING_MESSAGE, true);

			warningNotification.show(Page.getCurrent());

			textField.setValue("");
		}
	}

	/**
	 * Validate {@link #valueOfCodeTextField} and {@link #termTextField} and
	 * {@link #definitionTextField} on same value and if contains same value it will
	 * show warning message and set TextField value to "".
	 */
	private void validateTextFieldsOnSameValue() {
		String termTextFieldValue = termTextField.getValue();
		String definitionTextFieldValue = definitionTextField.getValue();
		String valueOfCodeTextFieldValue = valueOfCodeTextField.getValue();

		if (isStringNotEmpty(termTextFieldValue) && isStringNotEmpty(definitionTextFieldValue)
				&& termTextFieldValue.equals(definitionTextFieldValue)) {
			Notification warningNotification = new Notification("Warning", "Please, enter other value for definition.",
					Notification.Type.WARNING_MESSAGE, true);

			warningNotification.show(Page.getCurrent());

			definitionTextField.setValue("");
		} else if (isStringNotEmpty(termTextFieldValue) && isStringNotEmpty(valueOfCodeTextFieldValue)
				&& termTextFieldValue.equals(valueOfCodeTextFieldValue)) {
			Notification warningNotification = new Notification("Warning",
					"Please, enter other value for Value Of Code.", Notification.Type.WARNING_MESSAGE, true);

			warningNotification.show(Page.getCurrent());

			valueOfCodeTextField.setValue("");
		} else if (isStringNotEmpty(definitionTextFieldValue) && isStringNotEmpty(valueOfCodeTextFieldValue)
				&& definitionTextFieldValue.equals(valueOfCodeTextFieldValue)) {
			Notification warningNotification = new Notification("Warning",
					"Please, enter other value for Value Of Code.", Notification.Type.WARNING_MESSAGE, true);

			warningNotification.show(Page.getCurrent());

			valueOfCodeTextField.setValue("");
		}
	}

	/**
	 * Check is string not null and not empty.
	 *
	 * @param sourceStr - source string
	 */
	private boolean isStringNotEmpty(String sourceStr) {
		return Objects.nonNull(sourceStr) && !"".equals(sourceStr);
	}

	/**
	 * Check is string contains number or not.
	 *
	 * @param value - source string
	 * @return true if the string can be parsed as a number, false otherwise
	 */
	private boolean isNumberString( String value )
	{
		try
		{
			Integer.valueOf( value );
			return true;
		}
		catch ( NumberFormatException exception )
		{
			return false;
		}
	}

	/**
	 * Check is {@link #valueOfCodeTextField} and {@link #termTextField} and
	 * {@link #definitionTextField} not empty.
	 *
	 * @return boolean
	 */
	private boolean isTextFieldsNotEmpty() {
		return !termTextField.isEmpty() && !valueOfCodeTextField.isEmpty() && !definitionTextField.isEmpty();
	}

	/**
	 * Update {@link #csvGrid} content by selected language.
	 */
	private void updateCsvGrid()
	{
		csvGrid.removeAllColumns();

		csvGrid.addColumn( csvRow ->
		{
			String valueOfCode = csvRow.getNotation();

			if ( Objects.isNull( valueOfCode ) || "".equals( valueOfCode ) )

				if ( valueOfCode.contains( "." ) )
				{
					String[] rows = valueOfCode.split( "\\." );

					return "<div><font size=\"1\">" + rows[0] + "<br/>" + "&nbsp;&nbsp;" + rows[1] + "</font></div>";
				}

			return valueOfCode;
		}, new HtmlRenderer()).setId("valueOfCode");
		csvGrid.addColumn(CsvRow::getTerm).setId("term");
		csvGrid.addColumn(CsvRow::getDefinition).setId("definition");

		Map<String, List<CsvRow>> analysisUnitMap = csvFileData.getCsvRowMap();

		List<CsvRow> selectedLanguageCsvRows = analysisUnitMap.get(Language.ENGLISH.toString());

		if (Objects.isNull(selectedLanguageCsvRows)) {
			Notification warningNotification = new Notification("Warning", "Incomplete columns for selected language.",
					Notification.Type.WARNING_MESSAGE, true);

			warningNotification.show(Page.getCurrent());

			return;
		}

		csvGrid.getColumn("term").setCaption(csvFileData.getTermHeaderTitle());
		csvGrid.getColumn("definition").setCaption(csvFileData.getDefinitionHeaderTitle());
		csvGrid.getColumn("valueOfCode").setCaption(csvFileData.getValueOfCodeHeaderTitle());

		csvGrid.setItems(selectedLanguageCsvRows);
	}

	@Override
	public void updateMessageStrings(Locale locale) {

	}

	public List<CsvRow> getCsvRows() {
		return csvGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
	}

}
