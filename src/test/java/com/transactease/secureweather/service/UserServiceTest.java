package com.transactease.secureweather.service;

import com.transactease.secureweather.dto.UserDto;
import com.transactease.secureweather.model.User;
import com.transactease.secureweather.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {


    private final UserRepository mockUserRepository = mock(UserRepository.class);
    private UserService userService = new UserService(mockUserRepository);

    @BeforeEach
    void setUp() {
        this.userService = new UserService(mockUserRepository);
    }

    @Test
    void testCreateNewUser() {
        // Given
        UserDto userDto = new UserDto("test@example.com", "password");
        User user = new User();
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        when(mockUserRepository.save(any(User.class))).thenReturn(user);

        // When
        Mono<User> result = userService.createNewUser(userDto);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(savedUser -> savedUser.getEmail().equals(userDto.email()))
            .verifyComplete();
    }

    @Test
    void testCreateNewUser_InvalidInput() {
        // Given
        UserDto userDto = new UserDto("", "password");
        // When
        Mono<User> result = userService.createNewUser(userDto);
        // Then
        StepVerifier.create(result)
            .expectError(IllegalArgumentException.class)
            .verify();
    }

    @Test
    void testDeleteUserById() {
        // Given
        UUID id = UUID.randomUUID();
        // When
        Mono<Void> result = userService.deleteUserById(id);
        // Then
        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void testDeleteUserById_InvalidId() {
        // Given
        UUID id = null;
        // When
        Mono<Void> result = Mono.defer(() -> userService.deleteUserById(id));
        // Then
        StepVerifier.create(result)
            .expectError(NullPointerException.class)
            .verify();
    }


}
