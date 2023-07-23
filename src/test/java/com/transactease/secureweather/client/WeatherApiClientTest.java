package com.transactease.secureweather.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {WeatherApiClient.class})
@ExtendWith(SpringExtension.class)
class WeatherApiClientTest {
    @Autowired
    private WeatherApiClient weatherApiClient;

    /**
     * Method under test: {@link WeatherApiClient#getPlaceInfo(String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetPlaceInfo() {
        // TODO: Complete this test.
        //   Reason: R027 Missing beans when creating Spring context.
        //   Failed to create Spring context due to missing beans
        //   in the current Spring profile:
        //       org.springframework.web.reactive.function.client.WebClient$Builder
        //   See https://diff.blue/R027 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        String text = "";

        // Act
        Mono<String> actualPlaceInfo = this.weatherApiClient.getPlaceInfo(text);

        // Assert
        // TODO: Add assertions on result
    }
}

