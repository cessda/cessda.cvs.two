package eu.cessda.cvs.service;

public class VocabularyFileGenerationFailedException extends RuntimeException
{
    private static final long serialVersionUID = -5766482081112035907L;

    public VocabularyFileGenerationFailedException( Throwable cause )
    {
        super( cause );
    }
}
