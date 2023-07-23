package com.transactease.secureweather.repository;

import com.transactease.secureweather.model.City;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CityRepository extends R2dbcRepository<City, UUID> {
}
