package com.transactease.secureweather.service;

import com.transactease.secureweather.dto.UserDto;
import com.transactease.secureweather.model.User;
import com.transactease.secureweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> createNewUser(UserDto userDto) {
        if (userDto.email() == null || userDto.email().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Email cannot be null or empty"));
        }

        User user = new User();
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());

        return Mono.fromCallable(() -> userRepository.save(user))
            .onErrorResume(Mono::error);
    }


    public Mono<Void> deleteUserById(UUID id) {
        if (id == null) {
            throw new NullPointerException();
        }
        return Mono.fromRunnable(() -> userRepository.deleteById(id))
            .onErrorResume(Mono::error).then();
    }


}
