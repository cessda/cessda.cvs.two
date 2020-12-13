package eu.cessda.cvs.web.rest.errors;

@SuppressWarnings("squid:S110") // since BadRequestAlertException already has 6 parents
public class LoginAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "Login name already used!", "userManagement", "userexists");
    }
}
