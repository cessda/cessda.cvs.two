package eu.cessda.cvs.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException
{
    private static final long serialVersionUID = -1564529791881544715L;

    public NotFoundException( Class<?> clazz, Number id )
    {
        super( "Unable to find " + clazz.getSimpleName() + " with Id \"" + id + "\"" );
    }
}
