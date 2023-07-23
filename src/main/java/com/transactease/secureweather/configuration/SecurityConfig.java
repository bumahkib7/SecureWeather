package com.transactease.secureweather.configuration;

import com.transactease.secureweather.service.CustomUserDetailsService;
import com.transactease.secureweather.utils.JwtAuthenticationFilter;
import com.transactease.secureweather.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
public class SecurityConfig {


    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;


    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }


    @Bean
    @Primary
    public ReactiveUserDetailsService userDetailsService() {
        return (ReactiveUserDetailsService) userDetailsService;
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(@Qualifier("customUserDetailsService") ReactiveUserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .addFilterAt(
                new JwtAuthenticationFilter(jwtUtils, (CustomUserDetailsService) userDetailsService(), new WebSessionServerSecurityContextRepository()),
                SecurityWebFiltersOrder.HTTP_BASIC
            )
            .securityMatcher(new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers("/api/v1/users/new-user")))
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/public/**", "/api/auth/login", "/favicon.ico").permitAll()
                .pathMatchers("/api/auth/logout", "/api/v1/users/**").authenticated()
                .anyExchange().authenticated())
            .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()).disable())
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
            );


        return http.build();
    }


    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        WebSessionServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
        securityContextRepository.setSpringSecurityContextAttrName("CUSTOM_SECURITY_CONTEXT_ATTR_NAME"); // Optional:
        // Custom attribute name
        securityContextRepository.setCacheSecurityContext(false); // Optional: You can enable caching if needed
        return securityContextRepository;
    }

    private ServerLogoutSuccessHandler logoutSuccessHandler() {
        return new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.OK);
    }

}
