package eu.cessda.cvmanager.ui.view.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.gesis.wts.domain.enumeration.Language;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.dto.LicenceDTO;



public class CsvImportLayout extends MCssLayout implements Translatable {
	private static final long serialVersionUID = 1L;
	
	 private File tempCsvFile;

    private Grid<CsvRow> csvGrid;
    private ComboBox languageComboBox;
    private CsvFileData csvFileData;

    private TextField termTextField;
    private TextField definitionTextField;
    private TextField valueOfCodeTextField;

    private Upload upload;

    static final String NAME = "import";

    private List<String[]> rawDataList;

	public CsvImportLayout(I18N i18n) {
		super();
		init();
	}

	private void init() {
//      /* Create and configure the upload component */
      upload = new Upload("Upload CSV File", (Upload.Receiver) (filename, mimeType) -> {
          try {
            /* Here, we'll stored the uploaded file as a temporary file. No doubt there's
              a way to use a ByteArrayOutputStream, a reader around it, use ProgressListener (and
              a progress bar) and a separate reader thread to populate a container *during*
              the update.
              This is quick and easy example, though.
              */
              tempCsvFile = File.createTempFile("temp_analysis_units_", ".csv");

              return new FileOutputStream(tempCsvFile);
          } catch (IOException ioException) {
              ioException.printStackTrace();
              return null;
          }
      });

      upload.setEnabled(false);

      HorizontalLayout textFieldsHLayout = new HorizontalLayout();

      upload.addFinishedListener((Upload.FinishedListener) finishedEvent -> {
          try {
              /* Let's build a container from the CSV File */
              BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(tempCsvFile), StandardCharsets.UTF_8));
              BufferedReader rawDataBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(tempCsvFile), StandardCharsets.UTF_8));

              CsvFileUtil csvFileUtil = new CsvFileUtil();

              int termColumnIndex = Integer.valueOf(termTextField.getValue());
              int definitionColumnIndex = Integer.valueOf(definitionTextField.getValue());
              int valueOfCodeColumnIndex = Integer.valueOf(valueOfCodeTextField.getValue());

              csvFileData = csvFileUtil.readData(bufferedReader, termColumnIndex, definitionColumnIndex, valueOfCodeColumnIndex);
              rawDataList = csvFileUtil.readRawData(rawDataBufferedReader);

              /* Finally, let's update the table with the container */
              csvGrid.setCaption(finishedEvent.getFilename());

              updateCsvGrid();

              csvGrid.setVisible(true);

              upload.setVisible(false);

              HasValue.ValueChangeListener<String> valueChangeListener = valueChangeEvent -> {
                  String termIndex = termTextField.getValue();
                  String definitionIndex = definitionTextField.getValue();
                  String valueOfCodeIndex = valueOfCodeTextField.getValue();

                  if (!isNumberString(termIndex) || !isNumberString(definitionIndex) ||  !isNumberString(valueOfCodeIndex)) {
                      return;
                  }

                  int updatedTermColumnIndex = Integer.valueOf(termIndex);
                  int updatedDefinitionColumnIndex = Integer.valueOf(definitionIndex);
                  int updatedValueOfCodeColumnIndex = Integer.valueOf(valueOfCodeIndex);

                  List<CsvRow> csvRows = new ArrayList<>();

                  for (String[] currentLine : rawDataList) {
                      csvRows.add(csvFileUtil.createCsvRow(currentLine, updatedTermColumnIndex, updatedDefinitionColumnIndex, updatedValueOfCodeColumnIndex));
                  }

                  csvGrid.setItems(csvRows);
              };

              termTextField.addValueChangeListener(valueChangeListener);
              definitionTextField.addValueChangeListener(valueChangeListener);
              valueOfCodeTextField.addValueChangeListener(valueChangeListener);

              bufferedReader.close();
              rawDataBufferedReader.close();

              tempCsvFile.delete();
          } catch (IOException ioException) {
              ioException.printStackTrace();
          }
      });

      /* Table to show the contents of the file */
      csvGrid = new Grid<>();
      csvGrid.setSizeFull();
      csvGrid.setVisible(false);
      csvGrid.setHeightByRows(13);
      csvGrid.setRowHeight(45);
      csvGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
      csvGrid.getEditor().setEnabled(true);

      csvGrid.addColumn(CsvRow::getTerm).setId("term");
      csvGrid.addColumn(CsvRow::getDefinition).setId("definition");

      csvGrid.addColumn((ValueProvider<CsvRow, String>) analysisUnit -> {
          String valueOfCode = analysisUnit.getValueOfCode();

          if (Objects.isNull(valueOfCode) || "".equals(valueOfCode))

          if (valueOfCode.contains(".")) {
              String[] rows = valueOfCode.split("\\.");

              return "<div><font size=\"1\">" + rows[0] + "<br/>" + "&nbsp;&nbsp;" + rows[1] + "</font></div>";
          }

          return valueOfCode;
      }, new HtmlRenderer()).setId("valueOfCode");

      languageComboBox = new ComboBox("Language");
      languageComboBox.setEmptySelectionAllowed(false);

      languageComboBox.setItems(Language.getAllEnumCapitalized());
//      languageComboBox.setSelectedItem(Language.get);

      termTextField = new TextField("Term");
      termTextField.setRequiredIndicatorVisible(true);

      definitionTextField = new TextField("Definition");
      definitionTextField.setRequiredIndicatorVisible(true);

      valueOfCodeTextField = new TextField("Code");
      valueOfCodeTextField.setRequiredIndicatorVisible(true);

      textFieldsHLayout.addComponents(termTextField, definitionTextField, valueOfCodeTextField);

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
	    
	    this
			.withFullSize()
			.addComponents( verticalLayout );
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
     * Validate is text field value is number. If not number it will show warning message and set value to "".
     *
     * @param textField - source text field
     */
    private void validateTextFieldIsNumberValue(TextField textField) {
        String textFieldValue = textField.getValue();

        if (!isStringNotEmpty(textFieldValue)) {
            return;
        }

        if (!isNumberString(textFieldValue)) {
            Notification warningNotification =
                    new Notification("Warning", "Please, enter integer value.", Notification.Type.WARNING_MESSAGE, true);

            warningNotification.show(Page.getCurrent());

            textField.setValue("");
        }
    }

    /**
     * Validate {@link #valueOfCodeTextField} and {@link #termTextField} and {@link #definitionTextField} on same value and if
     * contains same value it will show warning message and set TextField value to "".
     */
    private void validateTextFieldsOnSameValue() {
        String termTextFieldValue = termTextField.getValue();
        String definitionTextFieldValue = definitionTextField.getValue();
        String valueOfCodeTextFieldValue = valueOfCodeTextField.getValue();

        if (isStringNotEmpty(termTextFieldValue) && isStringNotEmpty(definitionTextFieldValue) && termTextFieldValue.equals(definitionTextFieldValue)) {
            Notification warningNotification =
                    new Notification("Warning", "Please, enter other value for definition.", Notification.Type.WARNING_MESSAGE, true);

            warningNotification.show(Page.getCurrent());

            definitionTextField.setValue("");
        } else if (isStringNotEmpty(termTextFieldValue) && isStringNotEmpty(valueOfCodeTextFieldValue) && termTextFieldValue.equals(valueOfCodeTextFieldValue)) {
            Notification warningNotification =
                    new Notification("Warning", "Please, enter other value for Value Of Code.", Notification.Type.WARNING_MESSAGE, true);

            warningNotification.show(Page.getCurrent());

            valueOfCodeTextField.setValue("");
        } else if (isStringNotEmpty(definitionTextFieldValue) && isStringNotEmpty(valueOfCodeTextFieldValue) && definitionTextFieldValue.equals(valueOfCodeTextFieldValue)) {
            Notification warningNotification =
                    new Notification("Warning", "Please, enter other value for Value Of Code.", Notification.Type.WARNING_MESSAGE, true);

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
     * @return boolean
     */
    private boolean isNumberString(String value) {
        try {
            Integer.valueOf(value);

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Check is {@link #valueOfCodeTextField} and {@link #termTextField} and {@link #definitionTextField} not empty.
     *
     * @return boolean
     */
    private boolean isTextFieldsNotEmpty() {
        return !termTextField.isEmpty()
                && !valueOfCodeTextField.isEmpty()
                && !definitionTextField.isEmpty();
    }

    /**
     * Update {@link #csvGrid} content by selected language.
     */
    private void updateCsvGrid() {
        Map<String, List<CsvRow>> analysisUnitMap = csvFileData.getCsvRowMap();

        List<CsvRow> selectedLanguageCsvRows = analysisUnitMap.get(Language.ENGLISH.toString());

        if (Objects.isNull(selectedLanguageCsvRows)) {
            Notification warningNotification =
                    new Notification("Warning", "Incomplete columns for selected language.", Notification.Type.WARNING_MESSAGE, true);

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

}
