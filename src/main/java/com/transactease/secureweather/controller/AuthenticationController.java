package com.transactease.secureweather.controller;

import com.transactease.secureweather.configuration.PasswordEncoderConfig;
import com.transactease.secureweather.dto.LoginRequest;
import com.transactease.secureweather.repository.UserRepository;
import com.transactease.secureweather.service.UserService;
import com.transactease.secureweather.utils.JwtResponse;
import com.transactease.secureweather.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.CorePublisher;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final PasswordEncoderConfig passwordEncoder;

    public AuthenticationController(ReactiveAuthenticationManager authenticationManager,
                                    JwtUtils jwtUtils,
                                    UserService userService,
                                    UserRepository userRepository,
                                    PasswordEncoderConfig passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<JwtResponse>> authenticateUser(@RequestBody Mono<LoginRequest> loginRequestMono) {
        return loginRequestMono
            .flatMap(loginRequest ->
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
                    )
                    .zipWith(Mono.just(loginRequest)) // save the loginRequest object for later use
            )
            .flatMap(tuple -> {
                Authentication authentication = tuple.getT1(); // get the authentication object from the tuple
                LoginRequest loginRequest = tuple.getT2(); // get the loginRequest object from the tuple

                SecurityContextHolder.getContext().setAuthentication(authentication);
                return userRepository.findByEmail(loginRequest.email())
                    .flatMap(user -> {
                        if (passwordEncoder.passwordEncoder().matches(loginRequest.password(), user.getPassword())) {
                            String jwt = jwtUtils.generateJwtToken(authentication);
                            return Mono.just(ResponseEntity.ok(new JwtResponse(jwt)));
                        } else {
                            return Mono.error(new BadCredentialsException("Invalid password"));
                        }
                    })
                    .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
            })
            .onErrorResume(BadCredentialsException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()))
            .onErrorResume(UsernameNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


    @PostMapping("/logout")
    public CorePublisher<ResponseEntity<Object>> logout(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(securityContext -> {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(URI.create("/login")); // redirect to login page after logout
                return exchange.getSession()
                    .flatMap(session -> {
                        session.invalidate();
                        return Mono.just(ResponseEntity.ok().build());
                    });
            })
            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }



}
