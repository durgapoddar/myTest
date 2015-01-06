package hello;

public class Destination {
    String code;
    String name;
    String description;
    String country;
    String continent;
    GeoCoordinate coordinates;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public GeoCoordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoCoordinate coordinates) {
        this.coordinates = coordinates;
    }

}
