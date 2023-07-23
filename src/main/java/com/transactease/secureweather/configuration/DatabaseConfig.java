package com.transactease.secureweather.configuration;

import com.transactease.secureweather.utils.RoleReadConverter;
import com.transactease.secureweather.utils.RoleReadingConverter;
import com.transactease.secureweather.utils.RoleWriteConverter;
import com.transactease.secureweather.utils.RoleWritingConverter;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class DatabaseConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.url}")
    private String url;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "balarama.db.elephantsql.com")
                .option(PORT, 5432)  // optional, default 5432
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, "depyhpvp")
                .build());
    }




    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        return new R2dbcCustomConversions(getStoreConversions(), Arrays.asList(new RoleReadingConverter(), new RoleWritingConverter()));
    }


}
