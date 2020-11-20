package eu.cessda.cvs.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {
    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://vocabularies.cessda.eu/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final URI VOCABULARY_ALREADY_EXIST_TYPE = URI.create(PROBLEM_BASE_URL + "/vocabulary-already-exist");
    public static final URI VOCABULARY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/vocabulary-not-found-exist");
    public static final URI CODE_ALREADY_EXIST_TYPE = URI.create(PROBLEM_BASE_URL + "/code-already-exist");
    public static final URI INSUFFICIENT_VOCABULARY_AUTHORITY_TYPE = URI.create(PROBLEM_BASE_URL + "/insufficient-vocabulary-authority");
    private ErrorConstants() {
    }
}
