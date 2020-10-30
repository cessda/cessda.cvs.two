package eu.cessda.cvs.web.rest.errors;

public class VocabularyNotFoundException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public VocabularyNotFoundException() {
        super(ErrorConstants.VOCABULARY_NOT_FOUND_TYPE, "Vocabulary not found!", "vocabulary", "vocabularynotfound");
    }
}
