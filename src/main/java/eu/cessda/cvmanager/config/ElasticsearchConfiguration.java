package eu.cessda.cvmanager.config;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.DefaultEntityMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.geo.CustomGeoModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import eu.cessda.cvmanager.service.ElasticsearchTemplate2;

@Configuration
public class ElasticsearchConfiguration {
	
	@Bean
    public ElasticsearchTemplate2 elasticsearchTemplate(Client client, ObjectMapper objectMapper) {
    	return new ElasticsearchTemplate2(client, new CustomEntityMapper(objectMapper));
    }
	
  public class CustomEntityMapper implements EntityMapper {

      private ObjectMapper objectMapper;

      public CustomEntityMapper(ObjectMapper objectMapper) {
          this.objectMapper = objectMapper;
          objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          	.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
          	.registerModule(new ParameterNamesModule())
          	.registerModule(new Jdk8Module())
          	.registerModule(new JavaTimeModule()); // new module, NOT JSR310Module
      }

      @Override
      public String mapToString(Object object) throws IOException {
          return objectMapper.writeValueAsString(object);
      }

      @Override
      public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
          return objectMapper.readValue(source, clazz);
      }
  }

//    @Bean
//    public ElasticsearchTemplate elasticsearchTemplate(Client client, Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
////        return new ElasticsearchTemplate(client, new CustomEntityMapper(jackson2ObjectMapperBuilder.createXmlMapper(false).build()));
//    	return new ElasticsearchTemplate(client, new DefaultEntityMapper());
//    }
    
//    public class CustomEntityMapper extends DefaultEntityMapper {
//
//        @Inject
//        private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
//
//        public CustomEntityMapper () {
//            objectMapper = jackson2ObjectMapperBuilder.createXmlMapper(false).build();
//        }
//    }

//    public class CustomEntityMapper implements EntityMapper {
//
//        private ObjectMapper objectMapper;
//
//        public CustomEntityMapper(ObjectMapper objectMapper) {
//            this.objectMapper = objectMapper;
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
////            objectMapper.registerModule(new CustomGeoModule());
////            objectMapper.registerModule(new JavaTimeModule());
//        }
//
//        @Override
//        public String mapToString(Object object) throws IOException {
//            return objectMapper.writeValueAsString(object);
//        }
//
//        @Override
//        public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
//            return objectMapper.readValue(source, clazz);
//        }
//    }
}
