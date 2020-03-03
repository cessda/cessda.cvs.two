package eu.cessda.cvmanager.config;

import eu.cessda.cvmanager.service.ConfigurationService;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DDIFlatDbConfiguration
{
    private final ConfigurationService configurationService;

    public DDIFlatDbConfiguration( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    @Bean
    public RestClient ddiFlatDbRestClient()
    {
        return new RestClient( configurationService.getDdiflatdbRestUrl() );
    }
}
