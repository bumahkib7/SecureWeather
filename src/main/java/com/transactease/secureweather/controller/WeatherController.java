package com.transactease.secureweather.controller;

import com.transactease.secureweather.client.WeatherApiClient;
import com.transactease.secureweather.model.City;
import com.transactease.secureweather.repository.CityRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/weather")
public class WeatherController {

    private final WeatherApiClient weatherApiClient;
    private final CityRepository cityRepository;

    public WeatherController(WeatherApiClient weatherApiClient,
                             CityRepository cityRepository) {
        this.weatherApiClient = weatherApiClient;
        this.cityRepository = cityRepository;
    }

    @GetMapping("place/{place}")
    @Cacheable("City")
    public Mono<String> getPlaceInfo(@PathVariable String place) {
        City city = new City();
        city.setName(place);
        return weatherApiClient.getPlaceInfo(city.getName()).cache();
    }

}
