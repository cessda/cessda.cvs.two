package eu.cessda.cvmanager.config;

import eu.cessda.cvmanager.repository.VocabularyRepository;
import eu.cessda.cvmanager.repository.search.VocabularyPublishSearchRepository;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.VocabularyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger( ApplicationStartup.class );

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularySearchRepository vocabularySearchRepository;

    @Autowired
    private VocabularyPublishSearchRepository vocabularyPublishSearchRepository;

    @Autowired
    private VocabularyService vocabularyService;

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if( vocabularyRepository.count() > 0 && vocabularySearchRepository.count() == 0L){
            log.info( "Performing indexing on application start up" );
            vocabularySearchRepository.deleteAll();
            vocabularyPublishSearchRepository.deleteAll();
            vocabularyService.findAll().forEach(v -> {
                vocabularyService.index(v);
                vocabularyService.indexPublish(v, null);
            });
        }
    }
}
