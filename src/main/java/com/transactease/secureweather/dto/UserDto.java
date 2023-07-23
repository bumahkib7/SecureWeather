package com.transactease.secureweather.dto;

import com.transactease.secureweather.model.Role;

import java.util.Set;

public record UserDto(String email, String password, Role[] roles) {
}
