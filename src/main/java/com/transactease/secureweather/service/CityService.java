package com.transactease.secureweather.service;

import com.transactease.secureweather.repository.CityRepository;
import org.springframework.stereotype.Service;

@Service
public class CityService {


    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

}
