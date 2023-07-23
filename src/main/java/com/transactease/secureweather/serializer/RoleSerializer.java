package com.transactease.secureweather.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.transactease.secureweather.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

public class RoleSerializer extends StdSerializer<Role> {

    private final ConversionService conversionService;

    public RoleSerializer(ConversionService conversionService) {
        super(Role.class);
        this.conversionService = conversionService;
    }

    @Override
    public void serialize(Role value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (conversionService.canConvert(Role.class, String.class)) {
            String converted = conversionService.convert(value, String.class);
            gen.writeString(converted);
        } else {
            throw new UnsupportedOperationException("No converter available for Role to String");
        }
    }

}
