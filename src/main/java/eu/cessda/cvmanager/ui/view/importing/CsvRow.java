package eu.cessda.cvmanager.ui.view.importing;

import java.util.Objects;

/**
 * CsvRow.
 */
public class CsvRow {

    private String term;
    private String definition;
    private String notation;

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

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsvRow that = (CsvRow) o;
        return term.equals(that.term) &&
                definition.equals(that.definition) &&
                notation.equals(that.notation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, definition, notation);
    }
}
