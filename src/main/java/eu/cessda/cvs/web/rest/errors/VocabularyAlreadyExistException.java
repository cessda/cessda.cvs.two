package eu.cessda.cvs.web.rest.errors;

@SuppressWarnings("squid:S110") // since BadRequestAlertException already has 6 parents
public class VocabularyAlreadyExistException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public VocabularyAlreadyExistException() {
        super(ErrorConstants.VOCABULARY_ALREADY_EXIST_TYPE, "Vocabulary notation/short-name is already exist!", "vocabulary", "vocabularyexists");
    }
}
