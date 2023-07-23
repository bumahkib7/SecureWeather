package com.transactease.secureweather.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.transactease.secureweather.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

public class RoleDeserializer extends JsonDeserializer<Role> {

    private final ConversionService conversionService;

    public RoleDeserializer(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (conversionService.canConvert(String.class, Role.class)) {
            return conversionService.convert(value, Role.class);
        } else {
            throw new UnsupportedOperationException("No converter available for String to Role");
        }
    }



}
