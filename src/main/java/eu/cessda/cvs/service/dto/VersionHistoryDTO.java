package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.utils.VersionNumber;

import java.time.LocalDate;

public class VersionHistoryDTO
{
    private final Long id;
    private final VersionNumber version;
    private final LocalDate publicationDate;
    private final String versionNotes;
    private final String versionChanges;
    private final Long previousVersion;

    public VersionHistoryDTO(
        Long id,
        VersionNumber version,
        LocalDate publicationDate,
        String versionNotes,
        String versionChanges,
        Long previousVersion )
    {
        this.id = id;
        this.version = version;
        this.publicationDate = publicationDate;
        this.versionNotes = versionNotes;
        this.versionChanges = versionChanges;
        this.previousVersion = previousVersion;
    }

    public Long getId()
    {
        return id;
    }

    public VersionNumber getVersion()
    {
        return version;
    }

    public LocalDate getPublicationDate()
    {
        return publicationDate;
    }

    public String getVersionNotes()
    {
        return versionNotes;
    }

    public String getVersionChanges()
    {
        return versionChanges;
    }

    public Long getPreviousVersion()
    {
        return previousVersion;
    }
}
