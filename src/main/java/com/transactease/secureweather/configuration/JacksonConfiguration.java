package com.transactease.secureweather.configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.transactease.secureweather.model.Role;
import com.transactease.secureweather.serializer.RoleDeserializer;
import com.transactease.secureweather.serializer.RoleSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder(ConversionService conversionService) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Role.class, new RoleSerializer(conversionService));
        module.addDeserializer(Role.class, new RoleDeserializer(conversionService));
        builder.modules(module);
        return builder;
    }
}
