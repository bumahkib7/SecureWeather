package com.transactease.secureweather.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BadCredentialsException.class})
    public Mono<ResponseEntity<String>> handleBadCredentialsException(BadCredentialsException e) {
        // Handle BadCredentialsException and return response
        log.error("Error occurred ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials provided."));
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public Mono<ResponseEntity<String>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        // Handle UsernameNotFoundException and return response
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
    }
}
