package com.rafael.shoppingcart.security.sdk;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;

import java.security.Key;

public class KidSigningKeyResolver implements SigningKeyResolver {

    private final SigningKeyService signingKeyService;

    public KidSigningKeyResolver(SigningKeyService signingKeyService) {
        this.signingKeyService = signingKeyService;
    }

    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        return this.signingKeyService.getKeyByKid(header.getKeyId());
    }

    public Key resolveSigningKey(JwsHeader header, String plaintext) {
        throw new IllegalStateException("Auth service doesn't support plaintext body");
    }
}
