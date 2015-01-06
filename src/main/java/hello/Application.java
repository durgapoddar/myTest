package hello;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

        //TO-DO: verify this method
        /*AppConfiguration appConf = new AppConfiguration();
        System.out.println("airportSvcUrl == " +appConf.airportSvcUrl);
        System.out.println("destinationSvcUrl == " +appConf.destinationSvcUrl);*/

        Properties prop = new Properties();
        InputStream input = null;
        try {

            //TO-DO: NEED to CORRECT to read the PROPERTIES FILE from CLASSPATH instead of ACTUAL PATH
            //input = new FileInputStream("classpath:application.properties");
            input = new FileInputStream("/Users/durga/Office/KLM_assignment/gs-rest-service-master/target/classes/application.properties");
            prop.load(input);

            DataCache.setProperties(prop.getProperty("klm.airportSvc.url"), prop.getProperty("klm.destinationSvc.url"));
            System.out.println("Application:MAIN :: klmAirportSvcUrl ==>> " +prop.getProperty("klm.airportSvc.url"));
            System.out.println("Application:MAIN :: klmDestinationSvcUrl ==>>" +prop.getProperty("klm.destinationSvc.url"));

        }catch(IOException ex){
            ex.printStackTrace();
        }

    }
}
