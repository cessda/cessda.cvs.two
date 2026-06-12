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
package eu.cessda.cvs.utils;

import eu.cessda.cvs.domain.enumeration.ItemType;

import java.util.Objects;

public final class LanguageVersion
{
    private final VersionNumber versionNumber;
    private final String language;
    private final ItemType itemType;

    public LanguageVersion( VersionNumber versionNumber, String language, ItemType itemType )
    {
        this.versionNumber = versionNumber;
        this.language = language;
        this.itemType = itemType;
    }
    public VersionNumber getVersionNumber()
    {
        return versionNumber;
    }

    public String getLanguage()
    {
        return language;
    }

    public ItemType getItemType()
    {
        return itemType;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( !( o instanceof LanguageVersion ) ) return false;
        LanguageVersion that = (LanguageVersion) o;
        return Objects.equals( versionNumber, that.versionNumber ) &&
            Objects.equals( language, that.language ) && itemType == that.itemType;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( versionNumber, language, itemType );
    }

    @Override
    public String toString()
    {
        return "LanguageVersion{" +
            "versionNumber=" + versionNumber +
            ", language='" + language + '\'' +
            ", itemType=" + itemType +
            '}';
    }
}
