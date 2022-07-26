package com.rafael.shoppingcart.security;

import com.rafael.shoppingcart.security.sdk.Claims;
import com.rafael.shoppingcart.security.sdk.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Component
public class CustomBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String APP_AUTHORIZATION = "APP-Authorization";
    private final Client authServiceClient;// from SDK
    private final JwtAuthenticationBearer jwtAuthenticationBearer;

    private static final Predicate<String> isBearerToken = authorizationHeader -> {
        boolean isBearer =
                null != authorizationHeader && authorizationHeader.startsWith(BEARER_TOKEN_PREFIX);
        log.info("Is it a valid bearer token ? : {}", isBearer);
        return isBearer;
    };

    // Constructor
    public CustomBearerAuthenticationConverter(Client authServiceClient, JwtAuthenticationBearer jwtAuthenticationBearer) {
        this.authServiceClient = authServiceClient;
        this.jwtAuthenticationBearer = jwtAuthenticationBearer;
    }

    @Override
    public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(this::extract)
                .filter(isBearerToken)
                .flatMap(getBearerToken)
                .flatMap(bearerToken -> {
                    log.info("Decoding bearer token.");
                    return this.decode(bearerToken);
                })
                .flatMap(jwtAuthenticationBearer::create)
                .doOnError(e -> log.error("Exception while processing the bearer token from request ", e.getCause(), e));
    }

    public static final Function<String, Mono<String>> getBearerToken = authorizationHeader -> {
        log.info("Getting exact token value from authorizationHeader by removing bearer prefix. ");
        return Mono.justOrEmpty(authorizationHeader.substring(BEARER_TOKEN_PREFIX.length()));
    };

    private Mono<String> extract(ServerWebExchange serverWebExchange) {
        log.info("Extracting AD authorization header from request.");
        return Mono.justOrEmpty(serverWebExchange.getRequest()
                .getHeaders()
                .getFirst(APP_AUTHORIZATION));
    }

    private Mono<Map<String, Claims>> decode(String token) {
        log.info("Calling authServiceClient to get claims from token.");
        Map<String, Claims> map;
        try{
            map = new HashMap<>();
            map.put(token, authServiceClient.parse(token));
            log.info("Claims received for token.");
        }catch (Exception e){
            log.error("Exception while fetching claims from token", e);
            throw e;
        }
        //return Mono.justOrEmpty(map).subscribeOn(Schedulers.elastic()); deprecated
        return Mono.justOrEmpty(map).subscribeOn(Schedulers.boundedElastic());
    }
}
ç
