package com.transactease.secureweather.service;

import com.transactease.secureweather.configuration.PasswordEncoderConfig;

import com.transactease.secureweather.model.Role;
import com.transactease.secureweather.model.UserEntity;
import com.transactease.secureweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoderConfig passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    PasswordEncoderConfig passwordEncoder
                                    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmail(email)
            .doOnNext(user -> log.info("Found user: {}", user.getEmail()))
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + email)))
            .flatMap(userEntity -> {
                List<GrantedAuthority> authorities = Arrays.stream(userEntity.getRoles())
                    .map(role -> {
                        log.info("Mapping role: {}", role.getValue());
                        return new SimpleGrantedAuthority(role.getValue());
                    })
                    .collect(Collectors.toList());

                UserDetails userDetails = User.withUsername(userEntity.getEmail())
                    .password(userEntity.getPassword())
                    .authorities(authorities)
                    .build();

                log.info("Mapped user details: {}", userDetails.getUsername());

                return Mono.just(userDetails);
            })
            .doOnError(e -> log.error("Error occurred while fetching user: {}", e.getMessage()));
    }


    public Mono<UserEntity> createUser(String email, String rawPassword, Role[] roles) {
        String encodedPassword = passwordEncoder.passwordEncoder().encode(rawPassword);

        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        newUser.setRoles(roles);

        return userRepository.save(newUser);
    }

}
