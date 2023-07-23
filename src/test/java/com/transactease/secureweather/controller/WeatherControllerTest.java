package com.transactease.secureweather.controller;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.transactease.secureweather.client.WeatherApiClient;
import com.transactease.secureweather.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

class WeatherControllerTest {
    /**
     * Method under test: {@link WeatherController#getPlaceInfo(String)}
     */
    @Test
    void testGetPlaceInfo() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Diffblue AI was unable to find a test

        // Arrange
        Mono<String> mono = mock(Mono.class);
        when(mono.cache()).thenReturn(null);
        WeatherApiClient weatherApiClient = mock(WeatherApiClient.class);
        when(weatherApiClient.getPlaceInfo(Mockito.<String>any())).thenReturn(mono);

        // Act and Assert
        assertNull((new WeatherController(weatherApiClient, mock(CityRepository.class))).getPlaceInfo("Place"));
        verify(weatherApiClient).getPlaceInfo(Mockito.<String>any());
        verify(mono).cache();
    }
}

