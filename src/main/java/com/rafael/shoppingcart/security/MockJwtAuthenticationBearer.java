package com.rafael.shoppingcart.security;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MockJwtAuthenticationBearer {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String MOCK_AUTHORIZATION_FILE = "mockAuthorizationSession.json";

    public Mono<Authentication> create(ServerWebExchange serverWebExchange) {
        log.info("Creating Mock JwtAuthenticationToken...");
        JsonObject authenticationJson = readJsonAuthenticationFromFile();
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(getPrincipalFromJson(authenticationJson), getGrantedAuthoritiesFromJson(authenticationJson));
        log.info("Setting details as mock jwt token: "+jwtToken);
        jwtToken.setDetails(BEARER_TOKEN_PREFIX + jwtToken);
        return Mono.justOrEmpty(jwtToken);
    }

    // private utility method
    private JwtPrincipal getPrincipalFromJson(JsonObject authenticationJson) {
        JsonObject principalJson = authenticationJson.get("principal").getAsJsonObject();
        return new JwtPrincipal(
                principalJson.get("sub").getAsString(),
                principalJson.get("type").getAsString(),
                principalJson.get("tenant").getAsString(),
                principalJson.get("companyUuid").getAsString()
        );
    }

    private List<GrantedAuthority> getGrantedAuthoritiesFromJson(JsonObject authenticationJson) {
        JsonArray authoritiesJson = authenticationJson.get("authorities").getAsJsonArray();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (int i = 0; i < authoritiesJson.size(); i++) {
            GrantedAuthority authority = new SimpleGrantedAuthority(authoritiesJson.get(i).getAsString());
            authorities.add(authority);
        }
        return authorities;
    }

    private JsonObject readJsonAuthenticationFromFile() {
        InputStreamReader mockFileReader = null;
        try {
            mockFileReader = getMockFileReader(MOCK_AUTHORIZATION_FILE);
            return JsonParser.parseReader(mockFileReader).getAsJsonObject();
        } finally {
            if (mockFileReader != null) {
                try {
                    mockFileReader.close();
                } catch (IOException e) {
                    log.error("Error while trying to read the mock authentication file");
                }
            }
        }
    }

    private InputStreamReader getMockFileReader(String mockFileName) {
        InputStream resourceAsStream = MockJwtAuthenticationBearer.class.getClassLoader().getResourceAsStream(mockFileName);
        Assert.notNull(resourceAsStream, "Authorization mock file was not found");
        return new InputStreamReader(resourceAsStream);
    }



}
