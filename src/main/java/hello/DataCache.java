package hello;

import java.util.concurrent.atomic.AtomicLong;
import java.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataCache{

    private static DataCache cache = new DataCache();
    private static HashMap<String, String> airportCache = new HashMap();
    private static String klmAirportSvcUrl = "";
    private static String klmDestinationSvcUrl = "";

    private DataCache(){

    }

    public static DataCache getInstance(){
        return cache;
    }

    public static void setProperties(String prop1, String prop2){
        klmAirportSvcUrl = prop1;
        klmDestinationSvcUrl = prop2;
    }

    public static String getKlmAirportSvcUrl(){
        return klmAirportSvcUrl;
    }

    public static String getKlmDestinationSvcUrl(){
        return klmDestinationSvcUrl;
    }

    public static String getAirport(String origin){

        String desc = airportCache.get(origin);
        if (desc == null){
            System.out.println("### Fetching AIRPORT DESC from KLM service.........");
            desc = getAirportDescription(origin);
            airportCache.put(origin, desc);
        }
        else
            System.out.println("### Fetching AIRPORT DESC from CACHE.........");


        return desc;
    }

    private static String getAirportDescription(String origin) {

        String svcUrl = DataCache.getKlmAirportSvcUrl().concat(origin);
        System.out.println("@@ getAirportDescription :: svcUrl ==>> "+svcUrl);

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(svcUrl);
        //target = target.queryParam("origin", origin);

        Response response = target.request().get();
        String responseValue = response.readEntity(String.class);
        responseValue = responseValue.substring(9, (responseValue.length()-1) );

        //System.out.println("Airport Desc RESPONSE -->> " + responseValue);
        response.close();

        try {
            ObjectMapper mapper = new ObjectMapper();

            HashMap map = mapper.readValue(responseValue, new TypeReference<HashMap<String,Object>>(){});
            if (map != null)
                System.out.println("## Airport Details NOT NULL...");

            return (String) map.get("description");

        } catch (JsonGenerationException e) {

            e.printStackTrace();

        } catch (JsonMappingException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return null;

    }

}