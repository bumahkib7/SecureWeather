package com.transactease.secureweather.controller;

import com.transactease.secureweather.dto.UserDto;
import com.transactease.secureweather.model.UserEntity;
import com.transactease.secureweather.service.UserService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final CircuitBreaker circuitBreaker;
    public UserController(UserService userService, CircuitBreaker circuitBreaker) {
        this.userService = userService;
        this.circuitBreaker = circuitBreaker;
    }

    @GetMapping("/get-user/{id}")
    public Mono<ResponseEntity<UserEntity>> getUser(@PathVariable UUID id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)))
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(5)))
            .timeout(Duration.ofSeconds(3));
    }


    @GetMapping("/all")
    public Flux<UserEntity> getAllUsers() {
        return userService.getAllUsers()
            .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(5)));
    }

    @PostMapping("/new-user")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Object> createUser(@RequestBody UserDto userDto) {
        return userService.createNewUser(userDto)
            .onErrorContinue((throwable, o) -> log.error("Error creating user", throwable)) // log the error
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(5)))
            .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)));
    }


    @PutMapping("/update-user/{id}")
    public Mono<ResponseEntity<UserEntity>> updateUser(@PathVariable UUID id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(5)));
    }

    @DeleteMapping("/delete-user/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable UUID id) {
        return userService.deleteUserById(id)
            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
            .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(5)));
    }

    @PostMapping("/{userId}/favourite-cities/{cityId}")
    public Mono<Void> addFavouriteCity(@PathVariable UUID userId, @PathVariable UUID cityId) {
        return userService.addFavouriteCity(userId, cityId);
    }
}
