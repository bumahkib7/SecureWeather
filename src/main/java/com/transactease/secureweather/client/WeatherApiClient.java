package com.transactease.secureweather.client;

import com.transactease.secureweather.model.City;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Service
@Slf4j
public class WeatherApiClient {

    private final WebClient webClient;

    @Value("${weatherapi.apikey}")
    private String apiKey;

    @Value("${x-rapid.api.host}")
    private String apiHost;

    @Value("${x-rapid.baseUrl}")
    private String baseUrl;

    public WeatherApiClient(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("https://ai-weather-by-meteosource.p.rapidapi.com/").build();
    }



    public Mono<String> getPlaceInfo(@NotBlank String text) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/find_places")
                .queryParam("text", text)
                .queryParam("language", "en")
                .build())
            .header("X-RapidAPI-Key", apiKey)
            .header("X-RapidAPI-Host", apiHost)
            .retrieve()
            .bodyToMono(String.class);
    }

}
