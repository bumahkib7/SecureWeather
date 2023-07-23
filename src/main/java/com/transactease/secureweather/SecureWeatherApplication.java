package com.transactease.secureweather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
public class SecureWeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureWeatherApplication.class, args);
    }

}
