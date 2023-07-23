package com.transactease.secureweather.repository;

import com.transactease.secureweather.model.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, UUID> {

    //@Query("SELECT email, password, string_to_array(roles, ',') as roles FROM app_user WHERE email = :email")
    Mono<UserEntity> findByEmail(String email);


}
