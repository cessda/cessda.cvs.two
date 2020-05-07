package eu.cessda.cvs.config;

import java.time.Duration;

import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import org.hibernate.cache.jcache.ConfigSettings;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

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
            createCache(cm, eu.cessda.cvs.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, eu.cessda.cvs.repository.UserRepository.USERS_BY_EMAIL_CACHE);
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
