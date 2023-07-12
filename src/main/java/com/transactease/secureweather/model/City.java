package com.transactease.secureweather.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "city")
@Data
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;


    private String name;

    @ManyToMany(mappedBy = "favoriteCities")
    private Set<User> users = new HashSet<>();

}
