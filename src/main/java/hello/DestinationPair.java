package hello;

public class DestinationPair {

    String origin;

    Destination destination;

    Fare lowestFare;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Fare getLowestFare() {
        return lowestFare;
    }

    public void setLowestFare(Fare lowestFare) {
        this.lowestFare = lowestFare;
    }

}
