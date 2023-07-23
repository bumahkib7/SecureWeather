package com.transactease.secureweather.service;

import com.transactease.secureweather.dto.UserDto;
import com.transactease.secureweather.exceptions.ServiceUnavailableException;
import com.transactease.secureweather.model.City;
import com.transactease.secureweather.model.UserCity;
import com.transactease.secureweather.model.UserEntity;
import com.transactease.secureweather.repository.CityRepository;
import com.transactease.secureweather.repository.UserCityRepository;
import com.transactease.secureweather.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final UserCityRepository userCityRepository;

    private final CustomUserDetailsService userDetailsService;

    private final  TransactionalOperator transactionalOperator;


    public UserService(UserRepository userRepository,
                       CityRepository cityRepository,
                       UserCityRepository userCityRepository,
                       CustomUserDetailsService userDetailsService,
                       TransactionalOperator transactionalOperator) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.userCityRepository = userCityRepository;
        this.userDetailsService = userDetailsService;
        this.transactionalOperator = transactionalOperator;
    }

    @CircuitBreaker(name = "getUser", fallbackMethod = "fallbackForGetUser")
    public Mono<UserEntity> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Mono<UserEntity> fallbackForGetUser(UUID id, Exception t) {
        return Mono.error(new Exception("Exception when fetching user with id: " + id, t));
    }

    @CircuitBreaker(name = "default", fallbackMethod = "fallbackForGetAllUsers")
    public Flux<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Flux<UserEntity> fallbackForGetAllUsers(Exception t) {
        return Flux.error(new Exception("Exception when fetching all users", t));
    }

    @CircuitBreaker(name = "default", fallbackMethod = "fallbackForCreateUser")
    public Mono<Object> createNewUser(UserDto userDto) {
        return userRepository.findByEmail(userDto.email())
            .flatMap(existingUser -> Mono.error(new IllegalArgumentException("User already exists: " + userDto.email())))
            .switchIfEmpty(
                userDetailsService.createUser(userDto.email(), userDto.password(), userDto.roles())
            )
            .as(transactionalOperator::transactional) // apply the transaction
            .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)));
    }


    public Mono<UserEntity> fallbackForCreateUser(UserDto userDto, Exception e) {
        // Throw a service unavailable exception
        throw new ServiceUnavailableException("Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "default", fallbackMethod = "fallbackForUpdateUser")
    public Mono<UserEntity> updateUser(UUID id, UserDto userDto) {
        return userRepository.findById(id)
            .doOnSuccess(user -> {
                user.setEmail(userDto.email());
                user.setPassword(userDto.password());  // Password should be encoded before saving
            })
            .flatMap(userRepository::save);
    }

    public Mono<UserEntity> fallbackForUpdateUser(UUID id, UserDto userDto, Exception t) {
        return Mono.error(new Exception("Exception when updating user with id: " + id, t));
    }

    @CircuitBreaker(name = "default", fallbackMethod = "fallbackForDeleteUser")
    public Mono<Void> deleteUserById(UUID id) {
        return userRepository.deleteById(id);
    }

    public Mono<Void> fallbackForDeleteUser(UUID id, Exception t) {
        return Mono.error(new Exception("Exception when deleting user with id: " + id, t));
    }


    public Mono<Void> addFavouriteCity(UUID userId, UUID cityId) {
        UserCity userCity = new UserCity();
        userCity.setUserId(userId);
        userCity.setCityId(cityId);
        return userCityRepository.save(userCity).then();
    }


    public Mono<City> getFavouriteCities(UUID userId) {
        return userCityRepository.findAllByUserId(userId)
            .flatMap(userCity -> cityRepository.findById(userCity.getCityId()));
    }


}
