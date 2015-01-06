package hello;

import java.lang.StringBuilder;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RestController;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

@RestController
public class GreetingController {

    private static String destinationSvcUrl = "http://www.klm.com/destinations/destination-finder/";
    private final AtomicLong counter = new AtomicLong();
    private long sumResponseTime = 0;

    @RequestMapping("/destinationFinder")
    public String destinationFinder(@RequestParam(value="origin") String origin,
                                      @RequestParam(value="pos") String pos,
                                      @RequestParam(value="maxBudget") String maxBudget,
                                      @RequestParam(value="minBudget", defaultValue="1") String minBudget,
                                      @RequestParam(value="pageNum", defaultValue="0") String pageNum,
                                      @RequestParam(value="pageSize", defaultValue="20") String pageSize,
                                      @RequestParam(value="sortField", defaultValue="destination") String sortField ) {

        try {

            long startTime = System.currentTimeMillis();

            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(DataCache.getKlmDestinationSvcUrl());
            target = target.queryParam("origin", origin);
            target = target.queryParam("pos", pos);
            target = target.queryParam("maxBudget", maxBudget);
            target = target.queryParam("minBudget", minBudget);
            System.out.println("@@ destinationFinder :: svcUrl ==>> "+DataCache.getKlmDestinationSvcUrl());

            Response response = target.request().get();
            String value = response.readEntity(String.class);
            value = value.substring(9, (value.length()-1) );
            //System.out.println("DestinationFnder RESPONSE -->> " + value);

            // TO-DO: NEED TO WRITE ERROR-HANDLING CODE
            // [{"code":404,"message":"The server could not find what was requested."}]

            response.close();

            ObjectMapper mapper = new ObjectMapper();
            //map = mapper.readValue(value, new TypeReference<HashMap<String,Object>>(){});
            Destinations destinations = mapper.readValue(value, Destinations.class);

            if(destinations != null) {
                DataCache cache = DataCache.getInstance();
                String originDesc = cache.getAirport(origin);

                destinations.setOrigin(origin);
                destinations.setOriginDesc(originDesc);

                destinations.setCounter(counter.incrementAndGet());

                //SORTING of Destination Result List
                //Collections.sort(destinations.getDestinations());
                sortDestinationList(destinations.getDestinations(), sortField);

                //PAGINATION of Destination Result List
                int iPageNum = Integer.parseInt(pageNum);
                int iPageSize = Integer.parseInt(pageSize);
                destinations.setDestinations( generatePagedResult( destinations.getDestinations(), iPageNum, iPageSize ) );

                //Average Response Time
                long endTime = System.currentTimeMillis();
                sumResponseTime += (endTime - startTime);
                long avgResponseTime = sumResponseTime / counter.get();
                System.out.println("## DestinationFinder:: Service Hit = " +counter.get()
                        + " && sumResponseTime(ms) = " +sumResponseTime+ " && avgResponseTime(ms) = " +avgResponseTime );

                destinations.setAvgResponseTime(avgResponseTime);

            }

            //return destinations;
            return generateHtmlSource(destinations);

        } catch (JsonGenerationException e) {

            e.printStackTrace();

        } catch (JsonMappingException ex) {

            ex.printStackTrace();

        } catch (IOException ioEx) {

            ioEx.printStackTrace();

        } catch (NumberFormatException numEx) {

            numEx.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/airportDesc")
    public String airportDesc(@RequestParam(value="origin") String origin) {

        DataCache cache = DataCache.getInstance();
        return cache.getAirport(origin);

    }

    /* Comment: First PageNum = "0" */
    private List<DestinationPair> generatePagedResult(List<DestinationPair> destinationList, int pgNum, int pgSize){

        System.out.println("## PAGINATION::Main List size = "+destinationList.size()+" & PageNum = "+pgNum+" & PerPageSize = "+pgSize);

        int from = Math.max( 0, pgNum*pgSize );
        int to = Math.min( destinationList.size(), (pgNum+1)*pgSize );
        System.out.println("## from = "+from+ " & to = "+to);

        List<DestinationPair> sublist = destinationList.subList(from, to);
        System.out.println("## Sublist Size = "+sublist.size());

        return sublist;
    }

    /* Comment: ResultList is SORTed based on DestinationCode OR LowestFare value */
    private void sortDestinationList(List<DestinationPair> destinationList, String sortField){

        System.out.println("## SORTING::Main List size = "+destinationList.size()+" based on "+sortField);

        if(sortField.equals("price")) {

            Collections.sort(destinationList, new Comparator<DestinationPair>() {
                @Override
                public int compare(DestinationPair dest1, DestinationPair dest2) {
                    int cmp = 1;
                    if (dest1.getLowestFare().getValue() < dest2.getLowestFare().getValue()) {
                        cmp = -1;
                    }
                    return cmp;
                }
            });
        } else {
            Collections.sort(destinationList, new Comparator<DestinationPair>() {
                @Override
                public int compare(DestinationPair dest1, DestinationPair dest2) {
                    return dest1.getDestination().getDescription().compareTo(dest2.getDestination().getDescription());
                }
            });
        }
    }
    
    private String generateHtmlSource(Destinations destinations) {

        System.out.println("## Generating Html Source ...");

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("<html><head><title>Destination Finder Result</title></head><body>");

        strBuilder.append("<h4>Svc Hit Counter: ");
        strBuilder.append(destinations.getCounter());
        strBuilder.append("</h4>");
        strBuilder.append("<h4>Avg Response Time (ms): ");
        strBuilder.append(destinations.getAvgResponseTime());
        strBuilder.append("</h4>");
        strBuilder.append("<h4>Origin: ");
        strBuilder.append(destinations.getOrigin());
        strBuilder.append("</h4>");
        strBuilder.append("<h4>Origin Desc: ");
        strBuilder.append(destinations.getOriginDesc());
        strBuilder.append("</h4>");

        strBuilder.append("<table>");
        strBuilder.append("<thead><th>Destination</th><th>Fare</th></thead>");

        strBuilder.append("<tbody>");
        List<DestinationPair> destPairList = destinations.getDestinations();
        Iterator iterator = destPairList.listIterator();
        DestinationPair destPair = null;
        while (iterator.hasNext()) {
            destPair = (DestinationPair)iterator.next();
            strBuilder.append("<tr><td>");
            strBuilder.append(destPair.getDestination().getDescription());
            strBuilder.append("</td><td>");
            strBuilder.append(destPair.getLowestFare().getCurrency());
            strBuilder.append("&nbsp;");
            strBuilder.append(destPair.getLowestFare().getValue());
            strBuilder.append("</td></tr>");
        }
        strBuilder.append("<tbody>");

        strBuilder.append("</table>");
        strBuilder.append("</body></html>");

        return strBuilder.toString();
    }
}
