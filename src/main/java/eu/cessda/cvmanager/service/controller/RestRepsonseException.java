package eu.cessda.cvmanager.service.controller;

import org.springframework.http.HttpStatus;

public class RestRepsonseException extends RuntimeException
{
	private static final long serialVersionUID = 3617246608437128048L;

	private final HttpStatus httpStatus;

	public RestRepsonseException( HttpStatus httpStatus, String message )
	{
		super( message );
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus()
	{
		return httpStatus;
	}
}
