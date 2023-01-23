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

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, Constants.USERS_BY_LOGIN_CACHE);
            createCache(cm, Constants.USERS_BY_EMAIL_CACHE);
            createCache(cm, eu.cessda.cvs.domain.User.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Authority.class.getName());
            createCache(cm, eu.cessda.cvs.domain.User.class.getName() + ".authorities");
            createCache(cm, eu.cessda.cvs.domain.Vocabulary.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Vocabulary.class.getName() + ".versions");
            createCache(cm, eu.cessda.cvs.domain.Version.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Version.class.getName() + ".concepts");
            createCache(cm, eu.cessda.cvs.domain.Version.class.getName() + ".comments");
            createCache(cm, eu.cessda.cvs.domain.Concept.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Licence.class.getName());
            createCache(cm, eu.cessda.cvs.domain.MetadataField.class.getName());
            createCache(cm, eu.cessda.cvs.domain.MetadataField.class.getName() + ".metadataValues");
            createCache(cm, eu.cessda.cvs.domain.MetadataValue.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Resolver.class.getName());
            createCache(cm, eu.cessda.cvs.domain.VocabularyChange.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Agency.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Agency.class.getName() + ".userAgencies");
            createCache(cm, eu.cessda.cvs.domain.UserAgency.class.getName());
            createCache(cm, eu.cessda.cvs.domain.Comment.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

}
