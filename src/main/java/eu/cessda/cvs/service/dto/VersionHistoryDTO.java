/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
