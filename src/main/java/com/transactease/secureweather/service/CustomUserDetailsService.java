package com.transactease.secureweather.service;

import com.transactease.secureweather.configuration.PasswordEncoderConfig;
import com.transactease.secureweather.model.Role;
import com.transactease.secureweather.model.UserEntity;
import com.transactease.secureweather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoderConfig passwordEncoder;


    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoderConfig passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + email)))
            .map(userEntity -> {
                List<GrantedAuthority> authorities = userEntity.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .collect(Collectors.toList());

                return User.withUsername(userEntity.getUsernameFromEmail(email))
                    .password(userEntity.getPassword())
                    .authorities(authorities)
                    .build();
            });
    }


    public Mono<UserEntity> createUser(String email, String rawPassword, Set<Role> roles) {
        String encodedPassword = passwordEncoder.passwordEncoder().encode(rawPassword);
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }

}
