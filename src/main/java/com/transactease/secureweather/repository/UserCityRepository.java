package com.transactease.secureweather.repository;

import com.transactease.secureweather.model.UserCity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserCityRepository extends ReactiveCrudRepository<UserCity, UUID> {
    Mono<UserCity> findAllByUserId(UUID userId);
}
