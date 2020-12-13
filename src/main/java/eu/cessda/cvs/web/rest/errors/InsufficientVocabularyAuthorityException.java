package eu.cessda.cvs.web.rest.errors;

@SuppressWarnings("squid:S110") // since ForbiddenAlertException already has 6 parents
public class InsufficientVocabularyAuthorityException extends ForbiddenAlertException {

    private static final long serialVersionUID = 1L;

    public InsufficientVocabularyAuthorityException() {
        super(ErrorConstants.INSUFFICIENT_VOCABULARY_AUTHORITY_TYPE, "Authority to access/edit Vocabulary insufficient!", "vocabulary", "insufficientvocabularyauthority");
    }
}
