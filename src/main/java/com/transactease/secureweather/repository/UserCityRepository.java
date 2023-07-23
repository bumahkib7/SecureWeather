package com.transactease.secureweather.repository;

import com.transactease.secureweather.model.UserCity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserCityRepository extends R2dbcRepository<UserCity, UUID> {
    Mono<UserCity> findAllByUserId(UUID userId);
}
