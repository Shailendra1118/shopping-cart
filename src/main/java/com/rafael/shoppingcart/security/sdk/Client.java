package com.rafael.shoppingcart.security.sdk;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import java.util.Map;

public class Client {

    private final KidSigningKeyResolver kidSigningKeyResolver;
    private String issuer = "authService";
    private long allowedClockSkew = 180L;
    private final JwtParser parser;

    public Client(SigningKeyService signingKeyService) {
        this.kidSigningKeyResolver = new KidSigningKeyResolver(signingKeyService);
        this.parser = Jwts.parser().setSigningKeyResolver(this.kidSigningKeyResolver).setAllowedClockSkewSeconds(this.allowedClockSkew).requireIssuer(this.issuer);
    }

    public Client(SigningKeyService signingKeyService, String issuer, long allowedClockSkew) {
        this.kidSigningKeyResolver = new KidSigningKeyResolver(signingKeyService);
        this.issuer = issuer;
        this.allowedClockSkew = allowedClockSkew;
        this.parser = Jwts.parser().setSigningKeyResolver(this.kidSigningKeyResolver).setAllowedClockSkewSeconds(allowedClockSkew).requireIssuer(issuer);
    }

    public Claims parse(String token) {
        try {
            return new MultiTenantClaims((Map)this.parser.parseClaimsJws(token).getBody());
        } catch (ExpiredJwtException var3) {
            throw new RuntimeException("expiredJwt"); //JwtVerificationException("expiredJwt", var3.getMessage(), var3);
        } catch (IncorrectClaimException var4) {
            throw new RuntimeException("incorrectClaims");//JwtVerificationException("incorrectClaims", var4.getMessage(), var4);
        } catch (SignatureException var5) {
            throw new RuntimeException("invalidSignature"); //JwtVerificationException("invalidSignature", var5.getMessage(), var5);
        } catch (Exception var6) {
            throw new RuntimeException("invalidJwt"); // JwtVerificationException("invalidJwt", var6.getMessage(), var6);
        }
    }
}
