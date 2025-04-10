package com.ashish.util;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tools {
    private final RestTemplate restTemplate = new RestTemplate();
    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "for weather queries")
    Map<String, Object> getCurrentWeather(){

        //Get City Location
        String ipApiUrl = "https://ipinfo.io/json";
        Map locationData =  restTemplate.getForObject(ipApiUrl, Map.class);
        String city = locationData.get("city").toString();

        //Get latitude and longitude
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("nominatim.openstreetmap.org")
                .path("/search")
                .queryParam("q", city)
                .queryParam("format", "json");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "weather-app"); // Required by Nominatim
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List> geoResponse = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                List.class
        );

        if (geoResponse.getBody() == null || geoResponse.getBody().isEmpty()) {
            return Map.of("error", "City not found");
        }

        Map<String, Object> location = (Map<String, Object>) geoResponse.getBody().get(0);
        String lat = (String) location.get("lat");
        String lon = (String) location.get("lon");
        String resolvedCity = (String) location.get("display_name");

        // Get weather from Open-Meteo
        UriComponentsBuilder weatherBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("api.open-meteo.com")
                .path("/v1/forecast")
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("current_weather", true);

        Map weatherData = restTemplate.getForObject(weatherBuilder.toUriString(), Map.class);

        // Final response
        Map<String, Object> result = new HashMap<>();
        result.put("query_city", city);
        result.put("resolved_city", resolvedCity);
        result.put("latitude", lat);
        result.put("longitude", lon);
        result.put("current_weather", weatherData.get("current_weather"));
        return result;
    }

}
