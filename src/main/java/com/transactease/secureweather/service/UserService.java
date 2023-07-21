package com.transactease.secureweather.service;

import com.transactease.secureweather.dto.UserDto;
import com.transactease.secureweather.model.City;
import com.transactease.secureweather.model.UserCity;
import com.transactease.secureweather.model.UserEntity;
import com.transactease.secureweather.repository.CityRepository;
import com.transactease.secureweather.repository.UserCityRepository;
import com.transactease.secureweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class UserService {


    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final UserCityRepository userCityRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, CityRepository cityRepository, UserCityRepository userCityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cityRepository = cityRepository;
        this.userCityRepository = userCityRepository;
    }

    public Mono<Mono<UserEntity>> createNewUser(UserDto userDto) {
        if (userDto.email() == null || userDto.email().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Email cannot be null or empty"));
        }
        UserEntity user = new UserEntity();
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        return Mono.fromCallable(() -> userRepository.save(user))
            .onErrorResume(Mono::error);
    }


    public Mono<Void> deleteUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        return Mono.fromRunnable(() -> userRepository.deleteById(id))
            .onErrorResume(Mono::error).then();
    }


    public Mono<Void> addFavouriteCity(UUID userId, City city) {
        return userRepository.findById(userId)
            .flatMap(user -> cityRepository.save(city)
                .flatMap(savedCity -> userCityRepository.save(new UserCity(UUID.randomUUID(), userId, savedCity.getUuid())))
            ).then();
    }

    public Mono<City> getFavouriteCities(UUID userId) {
        return userCityRepository.findAllByUserId(userId)
            .flatMap(userCity -> cityRepository.findById(userCity.getCityId()));
    }


}
