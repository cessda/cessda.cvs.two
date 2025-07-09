package eu.cessda.cvs.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnexpectedIdentifierException extends RuntimeException
{
    private static final long serialVersionUID = -5122655362952074688L;

    public UnexpectedIdentifierException( String message )
    {
        super( message );
    }
}
