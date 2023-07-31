package com.transactease.secureweather.utils;

import com.transactease.secureweather.model.Role;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RoleReadingConverter implements Converter<String, Role> {
    @Override
    public Role convert(@NotNull String source) {
        return Role.fromValue(source);
    }
}
