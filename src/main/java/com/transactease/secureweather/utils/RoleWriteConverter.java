package com.transactease.secureweather.utils;

import com.transactease.secureweather.model.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class RoleWriteConverter implements Converter<Role, String> {

    @Override
    public String convert(Role source) {
        return source.toString();
    }
}
