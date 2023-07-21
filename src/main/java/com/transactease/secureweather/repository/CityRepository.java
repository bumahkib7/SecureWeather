package com.transactease.secureweather.repository;

import com.transactease.secureweather.model.City;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface CityRepository extends ReactiveCrudRepository<City, UUID> {
}
