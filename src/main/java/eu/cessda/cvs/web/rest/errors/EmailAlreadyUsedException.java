package eu.cessda.cvs.web.rest.errors;

@SuppressWarnings("squid:S110") // since BadRequestAlertException already has 6 parents
public class EmailAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "Email is already in use!", "userManagement", "emailexists");
    }
}
