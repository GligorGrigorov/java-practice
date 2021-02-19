package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.LocationNotFoundException;
import bg.sofia.uni.fmi.mjt.weather.exceptions.WeatherForecastClientException;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherForecastClient {
    private final HttpClient client;
    private static final String APPID = "your appid";
    private static final int NOT_FOUND_CODE = 404;

    public WeatherForecastClient(HttpClient weatherHttpClient) {
        client = weatherHttpClient;
    }

    /**
     * Fetches the weather forecast for the specified city.
     *
     * @return the forecast
     * @throws LocationNotFoundException if the city is not found
     * @throws WeatherForecastClientException if information regarding the weather for this location could not be retrieved
     */
    public WeatherForecast getForecast(String city) throws WeatherForecastClientException {
        city = city.replace(" ","%20");
        URI uri = null;
        try {
            uri = new URI("http","api.openweathermap.org","/data/2.5/weather", "q=" + city + "&units=metric&lang=bg&appid=" + APPID,null);
        } catch (URISyntaxException e) {
            throw new WeatherForecastClientException("Error in URI");
        }
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == NOT_FOUND_CODE){
                throw new LocationNotFoundException("City not found");
            }
            String json = response.body();
            Gson gson = new Gson();
            return gson.fromJson(json,WeatherForecast.class);
        } catch (IOException | InterruptedException | LocationNotFoundException e) {
            throw new WeatherForecastClientException("Error occurred in client" + e.toString());
        }
    }
}
