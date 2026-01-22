package com.sergewesley.forge.service.command;
import com.sergewesley.forge.service.command.api.Command;
import com.sergewesley.forge.dto.openmeteo.GeoResultResponse;
import com.sergewesley.forge.dto.openmeteo.WeatherResponse;
import com.sergewesley.forge.external.openmeteo.OpenMeteoService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WeatherCommand implements Command {

    private final OpenMeteoService openMeteoService;

    public WeatherCommand(OpenMeteoService openMeteoService) {
        this.openMeteoService = openMeteoService;
    }

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String execute(String args) {
        if (args == null || args.trim().isEmpty()) {
            return "Usage: weather <city_name>";
        }

        String city = args.trim();

        // 1. Resolve City
        Optional<GeoResultResponse.GeoLocation> locationOpt = openMeteoService.findCity(city);
        if (locationOpt.isEmpty()) {
            return "City not found: " + city;
        }

        GeoResultResponse.GeoLocation location = locationOpt.get();

        // 2. Fetch Weather
        Optional<WeatherResponse.CurrentWeather> weatherOpt = openMeteoService.getWeather(location.getLatitude(),
                location.getLongitude());
        if (weatherOpt.isEmpty()) {
            return "Could not retrieve weather data for " + location.getName();
        }

        WeatherResponse.CurrentWeather weather = weatherOpt.get();

        return String.format("Weather in %s, %s:\nTemperature: %.1f°C\nWind Speed: %.1f km/h",
                location.getName(),
                location.getCountry() != null ? location.getCountry() : "",
                weather.getTemperature(),
                weather.getWindspeed());
    }

    @Override
    public String getDescription() {
        return "Get current weather for a city. Usage: weather <city>";
    }
}
