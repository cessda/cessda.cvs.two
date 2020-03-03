package eu.cessda.cvmanager.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.io.IOException;

@Configuration
public class ElasticsearchConfiguration {
	
	@Bean
    public ElasticsearchTemplate elasticsearchTemplate(Client client, ObjectMapper objectMapper)
    {
        return new ElasticsearchTemplate( client, new CustomEntityMapper( objectMapper ) );
    }

    public static class CustomEntityMapper implements EntityMapper
    {

        private ObjectMapper objectMapper;

        public CustomEntityMapper( ObjectMapper objectMapper )
        {
            this.objectMapper = objectMapper;
            objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false )
                    .configure( DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true )
                    .registerModule( new ParameterNamesModule() )
                    .registerModule( new Jdk8Module() )
                    .registerModule( new JavaTimeModule() ); // new module, NOT JSR310Module
      }

        @Override
        public String mapToString( Object object ) throws JsonProcessingException
        {
            return objectMapper.writeValueAsString( object );
        }

      @Override
      public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
          return objectMapper.readValue(source, clazz);
      }
  }
}
