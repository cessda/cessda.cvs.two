package eu.cessda.cvmanager.service;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import com.vaadin.data.validator.AbstractValidator;

// Validator for validating the password
public class PasswordValidator extends AbstractValidator<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8740697748579508258L;

	public PasswordValidator() {
		super("The password provided is not valid");
	}

	public PasswordValidator(String caption) {
		super(caption);
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		//
		// Password must be at least 8 characters long and contain at least
		// one number
		//
		if (value != null && (value.length() < 8 || !value.matches(".*\\d.*"))) {
			return ValidationResult.error("Please enter more them 8 characters including at least one digit");
		}
		return ValidationResult.ok();
	}
}