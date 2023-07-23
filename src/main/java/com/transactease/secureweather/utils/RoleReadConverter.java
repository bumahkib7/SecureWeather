package com.transactease.secureweather.utils;

import com.transactease.secureweather.model.Role;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RoleReadConverter implements Converter<Row, Role> {

    @Override
    public Role convert(Row source) {
        return Role.valueOf(source.get("roles", String.class));
    }
}
