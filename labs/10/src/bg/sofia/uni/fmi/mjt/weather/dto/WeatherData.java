package bg.sofia.uni.fmi.mjt.weather.dto;

public class WeatherData {
    private final Double temp;
    private final Double feels_like;

    public WeatherData(Double temp, Double feels_like){
        this.temp = temp;
        this.feels_like = feels_like;
    }
    public Double getTemp(){
        return temp;
    }
    public Double getFeelsLike(){
        return feels_like;
    }
}
