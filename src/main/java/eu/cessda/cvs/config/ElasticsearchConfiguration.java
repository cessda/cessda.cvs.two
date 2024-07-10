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
package eu.cessda.cvs.config;

import eu.cessda.cvs.utils.VersionNumber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@Configuration
public class ElasticsearchConfiguration
{
    @Bean
    @Primary
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
            Arrays.asList(new VersionNumberToString(), new StringToVersionNumber()));
    }

    @WritingConverter
    static class VersionNumberToString implements Converter<VersionNumber, String>
    {
        @Override
        public String convert(VersionNumber source) {
            return source.toString();
        }
    }

    @ParametersAreNonnullByDefault
    @ReadingConverter
    static class StringToVersionNumber implements Converter<String, VersionNumber> {
        @Override
        public VersionNumber convert(String source) {
            return VersionNumber.fromString( source );
        }
    }
}
