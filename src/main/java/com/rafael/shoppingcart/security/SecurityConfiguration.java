package com.rafael.shoppingcart.security;

import com.rafael.shoppingcart.security.sdk.CachedSigningKeyService;
import com.rafael.shoppingcart.security.sdk.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private static final String MOCK_AUTHORIZATION_PROPERTY = "MOCK_AUTHORIZATION";

    // Actual authentication convertor to be used
    //AuthService SDK is a JWS verification utility for easy validation of AuthService JWTs
    @Bean
    public CustomBearerAuthenticationConverter bearerAuthenticationConverter() {
        return new CustomBearerAuthenticationConverter(authServiceClient(), jwtAuthenticationBearer());
    }

    @Bean
    public Client authServiceClient() {
        return new Client(CachedSigningKeyService.builder().build());
    }

    @Bean
    public JwtAuthenticationBearer jwtAuthenticationBearer() {
        return new JwtAuthenticationBearer();
    }


    // For Mocking
    @Bean
    public MockAuthenticationConverter mockingAuthenticationConverter() {
        return new MockAuthenticationConverter(mockJwtAuthenticationBearer());
    }

    @Bean
    public MockJwtAuthenticationBearer mockJwtAuthenticationBearer() {
        return new MockJwtAuthenticationBearer();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) { //@EnableWebFluxSecurity is required
        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeExchange()
                .pathMatchers("/actuator/prometheus", "/actuator/health", "/actuator/info")
                .permitAll()
                .and()
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    //private method for skipping actual authentication for testing
    private AuthenticationWebFilter bearerAuthenticationFilter() {
        log.info("Setting up bearer authentication filter");
        ReactiveAuthenticationManager authManager = new BearerTokenReactiveAuthenticationManager();
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);

        Function<ServerWebExchange, Mono<Authentication>> bearerConverter;
        if(shouldMockAuthorization()) {
            bearerConverter = mockingAuthenticationConverter();
        } else {
            bearerConverter = bearerAuthenticationConverter();
        }

        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerConverter::apply);
        bearerAuthenticationFilter
                .setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));
        return bearerAuthenticationFilter;
    }

    // Dev Development
    private boolean shouldMockAuthorization() {
        //return Boolean.parseBoolean(System.getenv().getOrDefault(MOCK_AUTHORIZATION_PROPERTY, "false"));
        return false;
    }
}
