package bg.sofia.uni.fmi.mjt.weather.dto;

public class WeatherCondition {
    private final String description;
    public WeatherCondition(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }
}
