package eu.cessda.cvs.service;

public class InsufficientVocabularyAuthorityException extends RuntimeException {

    public InsufficientVocabularyAuthorityException() {
        super("Authority to access/edit Vocabulary insufficient!");
    }

}
