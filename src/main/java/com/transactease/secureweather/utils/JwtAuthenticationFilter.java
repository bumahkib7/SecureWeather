package com.transactease.secureweather.utils;

import com.transactease.secureweather.service.CustomUserDetailsService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final ServerSecurityContextRepository securityContextRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils,
                                   CustomUserDetailsService userDetailsService,
                                   WebSessionServerSecurityContextRepository securityContextRepository) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange,@NotNull WebFilterChain chain) {
        String jwt = parseJwt(exchange.getRequest());
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            return userDetailsService.findByUsername(username)
                .map(userDetails -> {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    return new SecurityContextImpl(authentication);
                })
                .flatMap(securityContext -> securityContextRepository.save(exchange, securityContext))
                .then(chain.filter(exchange));
        }

        return chain.filter(exchange);
    }


    private String parseJwt(ServerHttpRequest request) {
        String headerAuth = request.getHeaders().getFirst("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
