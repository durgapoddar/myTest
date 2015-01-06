package hello;

import java.util.*;

public class Destinations{

    long counter=0;
    long avgResponseTime=0;
    String origin;
    String originDesc;

    List<DestinationPair> destinations;

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public long getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(long avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginDesc() {
        return originDesc;
    }

    public void setOriginDesc(String originDesc) {
        this.originDesc = originDesc;
    }

    public List<DestinationPair> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationPair> destinations) {
        this.destinations = destinations;
    }
}