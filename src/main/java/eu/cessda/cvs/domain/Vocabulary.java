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
package eu.cessda.cvs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Vocabulary.
 */
@Entity
@Table( name = "vocabulary" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vocabulary extends VocabularyBase
{

    private static final long serialVersionUID = 1L;

    public Vocabulary() {
        // default constructor for jackson
    }

    @JsonIgnore
    @OneToMany( mappedBy = "vocabulary", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<Version> versions = new HashSet<>();

    public Set<Version> getVersions() {
        return versions;
    }

    public Vocabulary versions(Set<Version> versions) {
        this.versions = versions;
        return this;
    }

    public Vocabulary addVersion(Version version) {
        this.versions.add(version);
        return this;
    }

    public Vocabulary removeVersion(Version version) {
        this.versions.remove(version);
        version.setVocabulary(null);
        return this;
    }

    public void setVersions(Set<Version> versions) {
        this.versions = versions;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        Vocabulary vocabulary = (Vocabulary) o;
        if ( vocabulary.getId() == null || getId() == null )
        {
            return false;
        }
        return Objects.equals( getId(), vocabulary.getId() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), versions );
    }

    @Override
    public String toString()
    {
        return "Vocabulary{" +
            "versions=" + versions +
            "} " + super.toString();
    }
}
