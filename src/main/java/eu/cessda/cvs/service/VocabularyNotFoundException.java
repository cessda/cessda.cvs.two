package eu.cessda.cvs.service;

public class VocabularyNotFoundException extends RuntimeException {

    public VocabularyNotFoundException() {
        super("Vocabulary not found!");
    }

}