package bg.sofia.uni.fmi.mjt.weather.dto;

public class WeatherForecast {
    private final WeatherCondition[] weather;
    private final WeatherData main;
    public WeatherForecast(WeatherCondition[] weather, WeatherData main){
        this.weather = weather;
        this.main = main;
    }

    public WeatherCondition[] getWeather() {
        return weather;
    }
    public WeatherData getMain(){
        return main;
    }
}
