package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfiguration {

    @Autowired
    org.springframework.core.env.Environment environment;

    @Value("${klm.airportSvc.url}")
    public String airportSvcUrl;

    @Value("${klm.destinationSvc.url}")
    public String destinationSvcUrl;

}