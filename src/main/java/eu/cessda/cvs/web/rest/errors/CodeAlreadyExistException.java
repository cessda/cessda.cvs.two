package eu.cessda.cvs.web.rest.errors;

public class CodeAlreadyExistException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public CodeAlreadyExistException() {
        super(ErrorConstants.CODE_ALREADY_EXIST_TYPE, "Code is already exist!", "code", "codeexists");
    }
}
