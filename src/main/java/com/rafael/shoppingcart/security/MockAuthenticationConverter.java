package com.rafael.shoppingcart.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Component
public class MockAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {
    private final MockJwtAuthenticationBearer mockJwtAuthenticationBearer;

    //constructor
    public MockAuthenticationConverter(MockJwtAuthenticationBearer mockJwtAuthenticationBearer) {
        this.mockJwtAuthenticationBearer = mockJwtAuthenticationBearer;
    }

    @Override
    public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
        log.info("Shopify service: Creating Mock Jwt token");
        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(swe -> mockJwtAuthenticationBearer.create(swe))
                .doOnError(e -> log.error("Exception while processing the mock bearer token from request ", e.getCause(), e));
    }
}
