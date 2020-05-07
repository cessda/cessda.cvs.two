package eu.cessda.cvs.service;

public class VocabularyAlreadyExistException extends RuntimeException {

    public VocabularyAlreadyExistException() {
        super("Vocabulary already exist!");
    }

}
