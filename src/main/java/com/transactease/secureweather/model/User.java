package com.transactease.secureweather.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID uuid;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name= "password")
    private String password;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_cities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "city_id"))
    private Set<City> favouriteCities = new HashSet<>();
}
