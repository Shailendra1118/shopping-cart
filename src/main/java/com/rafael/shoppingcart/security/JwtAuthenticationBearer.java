package com.rafael.shoppingcart.security;

import com.rafael.shoppingcart.security.sdk.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationBearer {
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    public Mono<Authentication> create(Map<String, Claims> map) { //// SDK claims extends io.jsonwebtoken.Claims
        log.info("Creating JwtAuthenticationToken...");
        Claims claims = map.entrySet().iterator().next().getValue();
        String jwt = map.entrySet().iterator().next().getKey();
        List<GrantedAuthority> authorities = claims.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        String companyUuid = claims.getCompanyUuid().orElse(null);
        JwtPrincipal jwtPrincipal = new JwtPrincipal(claims.getSubject(), claims.getSubType(),
                claims.getTenant(), companyUuid);
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(jwtPrincipal, authorities);
        log.info("Setting details as jwt token");
        jwtToken.setDetails(BEARER_TOKEN_PREFIX + jwt);
        return Mono.justOrEmpty(jwtToken);
    }

}
