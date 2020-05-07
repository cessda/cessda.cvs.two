package eu.cessda.cvs.service;

public class CodeAlreadyExistException extends RuntimeException {

    public CodeAlreadyExistException() {
        super("Notation already exist!");
    }

}
