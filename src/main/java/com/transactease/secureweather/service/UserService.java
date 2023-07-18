package com.transactease.secureweather.service;

import com.transactease.secureweather.dto.UserDto;
import com.transactease.secureweather.model.UserEntity;
import com.transactease.secureweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class UserService {


    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<UserEntity> createNewUser(UserDto userDto) {
        if (userDto.email() == null || userDto.email().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Email cannot be null or empty"));
        }

        UserEntity user = new UserEntity();
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));

        return Mono.fromCallable(() -> userRepository.save(user))
            .onErrorResume(Mono::error).block();
    }


    public Mono<Void> deleteUserById(UUID id) {
        if (id == null) {
            throw new NullPointerException();
        }
        return Mono.fromRunnable(() -> userRepository.deleteById(id))
            .onErrorResume(Mono::error).then();
    }


}
