package com.transactease.secureweather.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("app_user")
public class UserEntity {
    @Id
    private UUID uuid;
    private String email;
    private String password;
    @Column("roles")
    private Role[] roles;

    public String getUsernameFromEmail(String email) {
        return email.split("@")[0];
    }

}
