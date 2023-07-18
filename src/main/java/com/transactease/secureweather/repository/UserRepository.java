package com.transactease.secureweather.repository;

import com.transactease.secureweather.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    void delete(UUID uuid);
}
