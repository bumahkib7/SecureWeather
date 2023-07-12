package com.transactease.secureweather.client;

import com.transactease.secureweather.model.City;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@Slf4j
public class WeatherApiClient {

    private final WebClient webClient;

    @Value("${weatherapi.apikey}")
    private String apiKey;

    public WeatherApiClient(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("http://api.weatherstack.com").build();
    }

    public Mono<City> getCurrentWeatherOfACity(City city) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/current")
                .queryParam("access_key", apiKey)
                .queryParam("query", city.getName())
                .build()
            )
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                log.error("4xx error occurred while calling the API.");
                return Mono.error(new RuntimeException("4xx error occurred while calling the API."));
            })
            .onStatus(HttpStatusCode::is5xxServerError, response -> {
                log.error("5xx error occurred while calling the API.");
                return Mono.error(new RuntimeException("5xx error occurred while calling the API."));
            })
            .bodyToMono(City.class)
            .onErrorResume(e -> {
                    log.error("Error occurred: ", e);
                    return Mono.empty();
                }
            )
            .cache()
            .retry(3);
    }

    public Mono<City> getHistoricalWeatherOfSpecificCity(City city, Date date) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/historical")
                .queryParam("access_key", apiKey)
                .queryParam("query", city.getName())
                .queryParam("historical_date", date)
                .build())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                log.error("4xx error occurred while calling the API.");
                return Mono.error(new RuntimeException("4xx error occurred while calling the API."));
            })
            .onStatus(HttpStatusCode::is5xxServerError, response -> {
                log.error("5xx error occurred while calling the API.");
                return Mono.error(new RuntimeException("5xx error occurred while calling the API."));
            })
            .bodyToMono(City.class)
            .cache()
            .onErrorResume(e -> {
                log.error("Error occurred: ", e);
                return Mono.empty();
            })
            .retry(3);
    }

}
