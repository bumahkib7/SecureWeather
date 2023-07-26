package com.transactease.secureweather.service;

import com.transactease.secureweather.model.City;
import com.transactease.secureweather.model.UserCity;
import com.transactease.secureweather.repository.CityRepository;
import com.transactease.secureweather.repository.UserCityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class CityService {

    private final CityRepository cityRepository;
    private final UserCityRepository userCityRepository;

    public CityService(CityRepository cityRepository,
                       UserCityRepository userCityRepository) {
        this.cityRepository = cityRepository;
        this.userCityRepository = userCityRepository;
    }

    public Flux<City> getAllCities() {
        return cityRepository.findAll();
    }

    public Mono<City> getCity(UUID id) {
        return cityRepository.findById(id);
    }

    public Mono<City> createCity(City city) {
        return cityRepository.save(city);
    }

    public Mono<City> updateCity(UUID id, City city) {
        return cityRepository.findById(id)
            .flatMap(dbCity -> {
                dbCity.setName(city.getName());
                return cityRepository.save(dbCity);
            });
    }

    public Mono<City> deleteCity(UUID id) {
        log.info("Deleting city with ID: {}", id);
        return cityRepository.findById(id)
            .flatMap(existingCity -> {
                log.info("City found: {}. Proceeding to delete", existingCity);
                return cityRepository.delete(existingCity)
                    .then(Mono.just(existingCity));
            })
            .switchIfEmpty(Mono.defer(() -> {
                log.error("City not found with ID: {}", id);
                return Mono.error(new Exception("City not found, cannot delete"));
            }));
    }

    public Flux<Object> assignCityToUser(UUID userId, UUID cityId) {
        log.info("Assigning city with ID: {} to user with ID: {}", cityId, userId);

        return userCityRepository.findByUserIdAndCityId(userId, cityId)
            .flatMap(existingUserCity -> {
                log.error("City with ID: {} is already assigned to user with ID: {}", cityId, userId);
                return Mono.error(new IllegalStateException("City is already assigned to this user"));
            })
            .switchIfEmpty(cityRepository.findById(cityId)
                .flatMap(city -> {
                    UserCity userCity = new UserCity(userId, cityId);
                    log.info("City found: {}. Proceeding to assign", city);
                    return userCityRepository.save(userCity);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("City not found with ID: {}", cityId);
                    return Mono.error(new Exception("City not found, cannot assign"));
                }))
            );
    }

    public Flux<City> getCitiesByUserId(UUID userId) {
        return userCityRepository.findByUserId(userId)
            .flatMap(userCity -> cityRepository.findById(userCity.getCityId()));
    }

}

