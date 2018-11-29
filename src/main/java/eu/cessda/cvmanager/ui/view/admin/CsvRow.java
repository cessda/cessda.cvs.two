package eu.cessda.cvmanager.ui.view.admin;

import java.util.Objects;

/**
 * CsvRow.
 */
public class CsvRow {

    private String term;
    private String definition;
    private String valueOfcode;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getValueOfCode() {
        return valueOfcode;
    }

    public void setValueOfcode(String valueOfcode) {
        this.valueOfcode = valueOfcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsvRow that = (CsvRow) o;
        return term.equals(that.term) &&
                definition.equals(that.definition) &&
                valueOfcode.equals(that.valueOfcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, definition, valueOfcode);
    }
}
