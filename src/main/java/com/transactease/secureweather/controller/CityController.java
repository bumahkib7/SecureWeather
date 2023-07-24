package com.transactease.secureweather.controller;

import com.transactease.secureweather.client.WeatherApiClient;
import com.transactease.secureweather.model.City;
import com.transactease.secureweather.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cities")
@Slf4j
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;
    private final WeatherApiClient weatherApiClient;

    @GetMapping("/all")
    public Flux<City> getAllCities() {
        log.info("Fetching all cities");
        return cityService.getAllCities();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<City>> getCity(@PathVariable UUID id) {
        log.info("Fetching city with ID: {}", id);
        return cityService.getCity(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/new-city")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<City> createCity(@RequestBody City city) {
        log.info("Creating new city: {}", city);
        return cityService.createCity(city);
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<City>> updateCity(@PathVariable UUID id, @RequestBody City city) {
        log.info("Updating city with ID: {}", id);
        return cityService.updateCity(id, city)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Object>> deleteCity(@PathVariable UUID id) {
        log.info("Deleting city with ID: {}", id);
        return cityService.deleteCity(id)
            .thenReturn(ResponseEntity.noContent().build())
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/weather/user/{userId}")
    public Flux<String> getUserCitiesWeatherInfo(@PathVariable UUID userId) {
        return cityService.getCitiesByUserId(userId)
            .flatMap(city -> weatherApiClient.getPlaceInfo(city.getName()));
    }

    @PostMapping("{userId}/cities/{cityId}")
    public Flux<Object> assignCityToUser(@PathVariable UUID userId, @PathVariable UUID cityId) {
        return cityService.assignCityToUser(userId, cityId);
    }


}
