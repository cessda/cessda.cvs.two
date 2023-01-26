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
package eu.cessda.cvs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConfigurationService {
	public static final String SOURCE_LANGUAGE = "en";
	public static final String DEFAULT_CV_LINK = "http://www.example.com/Specification/CV/";

	@Autowired
	private Environment env;

	public String getDefaultSourceLanguage() {
		return SOURCE_LANGUAGE;
	}

	public List<String> getPropertyByKeyAsList(String key, String splitBy) {
		if( env.getProperty(key) == null )
			return Collections.emptyList();
		return Arrays.asList( env.getProperty(key).split(splitBy) );
	}

	public Optional<Set<String>> getPropertyByKeyAsSet(String key, String splitBy) {
		return Optional.of( new LinkedHashSet<>( getPropertyByKeyAsList(key, splitBy)));
	}
}
