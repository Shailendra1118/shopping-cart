package com.rafael.shoppingcart.security.sdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.StringJoiner;


@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class JwkWS {
    private String use;
    private String alg;
    private String kty;
    private String kid;
    private String n;
    private String e;

    public JwkWS() {
    }

    public JwkWS(String use, String alg, String kty, String kid, String n, String e) {
        this.use = use;
        this.alg = alg;
        this.kty = kty;
        this.kid = kid;
        this.n = n;
        this.e = e;
    }

    public String getUse() {
        return this.use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getAlg() {
        return this.alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getKty() {
        return this.kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public String getKid() {
        return this.kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getN() {
        return this.n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getE() {
        return this.e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof JwkWS)) {
            return false;
        } else {
            JwkWS that = (JwkWS)o;
            return Objects.equals(this.use, that.use) && Objects.equals(this.alg, that.alg) && Objects.equals(this.kty, that.kty) && Objects.equals(this.kid, that.kid) && Objects.equals(this.n, that.n) && Objects.equals(this.e, that.e);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.use, this.alg, this.kty, this.kid, this.n, this.e});
    }

    public String toString() {
        return (new StringJoiner(", ", JwkWS.class.getSimpleName() + "[", "]")).add("use='" + this.use + "'").add("alg='" + this.alg + "'").add("kty='" + this.kty + "'").add("kid='" + this.kid + "'").add("n='" + this.n + "'").add("e='" + this.e + "'").toString();
    }

    public Key toKey() throws InvalidKeySpecException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(this.getKty());
            return keyFactory.generatePublic(new RSAPublicKeySpec(this.decodeN(), this.decodeE()));
        } catch (NoSuchAlgorithmException var2) {
            throw new InvalidKeySpecException(var2);
        }
    }

    private BigInteger decodeN() throws InvalidKeySpecException {
        try {
            return new BigInteger(Base64.getUrlDecoder().decode(this.getN()));
        } catch (IllegalArgumentException var2) {
            return new BigInteger(Base64.getDecoder().decode(this.getN()));
        } catch (Exception var3) {
            throw new InvalidKeySpecException(var3);
        }
    }

    private BigInteger decodeE() throws InvalidKeySpecException {
        try {
            return new BigInteger(Base64.getUrlDecoder().decode(this.getE()));
        } catch (IllegalArgumentException var2) {
            return new BigInteger(Base64.getDecoder().decode(this.getE()));
        } catch (Exception var3) {
            throw new InvalidKeySpecException(var3);
        }
    }
}
