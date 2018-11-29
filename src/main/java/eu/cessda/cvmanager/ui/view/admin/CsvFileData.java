package eu.cessda.cvmanager.ui.view.admin;

import java.util.List;
import java.util.Map;

/**
 * Contains CSV file data.
 */
public class CsvFileData {
    private String termHeaderTitle;
    private String definitionHeaderTitle;
    private String valueOfCodeHeaderTitle;

    Map<String, List<CsvRow>> analysisUnitMap;

    public String getTermHeaderTitle() {
        return termHeaderTitle;
    }

    public void setTermHeaderTitle(String termHeaderTitle) {
        this.termHeaderTitle = termHeaderTitle;
    }

    public String getDefinitionHeaderTitle() {
        return definitionHeaderTitle;
    }

    public void setDefinitionHeaderTitle(String definitionHeaderTitle) {
        this.definitionHeaderTitle = definitionHeaderTitle;
    }

    public String getValueOfCodeHeaderTitle() {
        return valueOfCodeHeaderTitle;
    }

    public void setValueOfCodeHeaderTitle(String valueOfCodeHeaderTitle) {
        this.valueOfCodeHeaderTitle = valueOfCodeHeaderTitle;
    }

    public Map<String, List<CsvRow>> getCsvRowMap() {
        return analysisUnitMap;
    }

    public void setCsvRowMap(Map<String, List<CsvRow>> analysisUnitMap) {
        this.analysisUnitMap = analysisUnitMap;
    }
}
