package com.transactease.secureweather.utils;

import com.transactease.secureweather.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String jwt = parseJwt(exchange.getRequest());
        log.info("JWT token: {}", jwt);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            return userDetailsService.findByUsername(username)
                .map(userDetails -> {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    return new SecurityContextImpl(authentication);
                })
                .flatMap(securityContext -> {
                    log.info("JWT authentication succeeded for user: {}", securityContext.getAuthentication().getName());
                    return securityContextRepository.save(exchange, securityContext).then(Mono.just(securityContext));
                })
                .doOnError(throwable -> log.error("Error occurred while processing JWT authentication", throwable))
                .then(chain.filter(exchange))
                .doOnError(throwable -> log.error("Error occurred while processing the filter chain", throwable));
        } else {
            log.warn("Invalid or expired JWT token. ");
        }

        return chain.filter(exchange)
            .doOnError(throwable -> log.error("Error occurred while processing the filter chain", throwable));
    }


    private String parseJwt(ServerHttpRequest request) {
        String headerAuth = request.getHeaders().getFirst("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
