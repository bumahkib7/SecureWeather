package com.transactease.secureweather.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("user_cities")
public class UserCity {

    @Id
    private UUID id;
    private UUID userId;
    private UUID cityId;

    public UserCity(UUID id, UUID userId, UUID cityId) {
        this.id = id;
        this.userId = userId;
        this.cityId = cityId;

    }
}
