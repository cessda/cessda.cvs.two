package eu.cessda.cvmanager.ui.view.admin;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gesis.wts.domain.enumeration.Language;

import com.opencsv.CSVReader;

/**
 * Contains methods to read CSV files.
 *
 * Import and parse CSV, example csv file is attached. It should read the csv file and create a grid with three headers:
 * Term, Definition, Value of code
 * Show all the values of each column with respective language(from row 6 to the end of CSV)
 * Column A (Term) is always the same in all languages in the grid.
 *
 * Column B (Definition) is based on the selected language from the user. (B and J-N are different languages for definition)
 *
 * Column C (Value of code) is based on the selected language from the user. (C-I are different languages)
 * if Column C the text is separated by '.' like Family.houseHouldFamily in the given csv in row 9. The row in the grid should be shown as subCategory of the row above.
 *
 * Definition column values are in a different language; if a user selects English for example, it should show the column (B) in the excel file.
 * (English - > column 2)
 * (Finish - >column 10)
 * (German - > column 11)
 * (Swedish - > column 12 )
 * (Norwegian - > column 13)
 * (Danish- > column 14)
 *
 * and for Value of code:
 * (English - > column 3) (Finish - >column 4) (German - > column 5) (Swedish - > column 6) (Norwegian - > column 8) (Danish- > column 9)
 *
 * Term,     | Definition,                                                                                                                |   Value of code
 * Column A  | Column B (Definition) is based on the selected language from the user. (B and J-N are different languages for definition)  |   Column C (Value of code) is based on the selected language from the user. (C-I are different languages)
 */
public class CsvFileUtil {

    /**
     * Read Csv file data to {@link CsvFileData} object.
     *
     * @param reader - file reader
     *
     * @throws IOException -
     *
     * @return CsvFileData
     */
    public CsvFileData readData(Reader reader) throws IOException {
        CSVReader csvReader = new CSVReader(reader);

        String[] currentLine;

        List<CsvRow> englishCsvRows = new ArrayList<>();
        List<CsvRow> finishCsvRows = new ArrayList<>();
        List<CsvRow> germanCsvRows = new ArrayList<>();
        List<CsvRow> swedishCsvRows = new ArrayList<>();
        List<CsvRow> norwegianCsvRows = new ArrayList<>();
        List<CsvRow> danishCsvRows = new ArrayList<>();

        // Read headers.
        String[] headers = csvReader.readNext();

        CsvFileData csvFileData = new CsvFileData();

        csvFileData.setTermHeaderTitle(headers[0]);
        csvFileData.setDefinitionHeaderTitle(headers[1]);
        csvFileData.setValueOfCodeHeaderTitle(headers[2]);

        while ((currentLine = csvReader.readNext()) != null) {
            englishCsvRows.add(createCsvRow(currentLine, 1, 2));
            finishCsvRows.add(createCsvRow(currentLine, 9, 3));
            germanCsvRows.add(createCsvRow(currentLine, 10, 4));
            swedishCsvRows.add(createCsvRow(currentLine, 11, 5));
            norwegianCsvRows.add(createCsvRow(currentLine, 12, 7));
            danishCsvRows.add(createCsvRow(currentLine, 13, 8));
        }

        csvReader.close();

        Map<String, List<CsvRow>> analysisUnitsMap = new HashMap<>();

        analysisUnitsMap.put(Language.ENGLISH.toString(), englishCsvRows);
        analysisUnitsMap.put(Language.FINNISH.toString(), finishCsvRows);
        analysisUnitsMap.put(Language.GERMAN.toString(), germanCsvRows);
        analysisUnitsMap.put(Language.SWEDISH.toString(), swedishCsvRows);
        analysisUnitsMap.put(Language.NORWEGIAN.toString(), norwegianCsvRows);
        analysisUnitsMap.put(Language.DANISH.toString(), danishCsvRows);

        csvFileData.setCsvRowMap(analysisUnitsMap);

        return csvFileData;
    }

    /**
     * Read Csv file data to {@link CsvFileData} object.
     *
     * @param reader - file reader
     *
     * @throws IOException -
     *
     * @return CsvFileData
     */
    public CsvFileData readData(Reader reader, int termColumnIndex, int definitionColumnIndex, int valueOfCodeColumnIndex) throws IOException {
        CSVReader csvReader = new CSVReader(reader);

        String[] currentLine;

        List<CsvRow> analysisUnits = new ArrayList<>();

        // Read headers.
        String[] headers = csvReader.readNext();

        CsvFileData csvFileData = new CsvFileData();

        csvFileData.setTermHeaderTitle(headers[termColumnIndex]);
        csvFileData.setDefinitionHeaderTitle(headers[definitionColumnIndex]);
        csvFileData.setValueOfCodeHeaderTitle(headers[valueOfCodeColumnIndex]);

        while ((currentLine = csvReader.readNext()) != null) {
            analysisUnits.add(createCsvRow(currentLine, termColumnIndex, definitionColumnIndex, valueOfCodeColumnIndex));
        }

        csvReader.close();

        Map<String, List<CsvRow>> analysisUnitsMap = new HashMap<>();

        analysisUnitsMap.put(Language.ENGLISH.toString(), analysisUnits);

        csvFileData.setCsvRowMap(analysisUnitsMap);

        return csvFileData;
    }

    /**
     * Read raw data.
     *
     * @param reader - source reader
     *
     * @throws IOException
     *
     * @return List<String[]>
     */
    public List<String[]> readRawData(Reader reader) throws IOException {
        CSVReader csvReader = new CSVReader(reader);

        String[] currentLine;

        List<String[]> rawDataList = new ArrayList<>();

        // Read headers.

        while ((currentLine = csvReader.readNext()) != null) {
            rawDataList.add(currentLine);
        }

        csvReader.close();

        return rawDataList;
    }

    /**
     * Read data from provided line by column index's.
     *
     * @param currentLine       - current line data array
     * @param titleIndex        - index of title (term) column
     * @param definitionIndex   - index of definition(description) column
     * @param valueOfCodeIndex  - index of value of code column
     *
     * @return CsvRow
     */
    public CsvRow createCsvRow(String[] currentLine, int titleIndex, int definitionIndex, int valueOfCodeIndex) {
        CsvRow analysisUnit = new CsvRow();

        analysisUnit.setTerm(currentLine[titleIndex]);
        analysisUnit.setDefinition(currentLine[definitionIndex]);
        analysisUnit.setValueOfcode(currentLine[valueOfCodeIndex]);

        return analysisUnit;
    }

    /**
     * Create {@link CsvRow} object from provided row data.
     *
     * @param currentLine       - current line array of strings
     * @param definitionIndex   - definition index in current line array
     * @param valueOfCodeIndex  - value of code index in current line array
     *
     * @return CsvRow
     */
    private CsvRow createCsvRow(String[] currentLine, int definitionIndex, int valueOfCodeIndex) {
        CsvRow analysisUnit = new CsvRow();
        analysisUnit.setTerm(currentLine[0]);
        analysisUnit.setDefinition(currentLine[definitionIndex]);
        analysisUnit.setValueOfcode(currentLine[valueOfCodeIndex]);

        return analysisUnit;
    }
}
