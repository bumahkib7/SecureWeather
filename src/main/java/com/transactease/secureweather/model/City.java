package com.transactease.secureweather.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table("city")
public class City {

    @Id
    private UUID uuid;
    private String name;

}
