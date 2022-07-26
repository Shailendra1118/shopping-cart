package com.rafael.shoppingcart.security.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.cglib.core.internal.LoadingCache;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CachedSigningKeyService implements SigningKeyService{

    private static final Logger log = LoggerFactory.getLogger(CachedSigningKeyService.class);
    private static final String DEFAULT_ISSUER = "http://authService:9090";
    private static final String CERTS_PATH = "/api/v1/certificates";
    private final ObjectMapper mapper;
    private final String issuer;
    private final LoadingCache<String, Key> cache;


    // Constructor
    private CachedSigningKeyService(ObjectMapper mapper, String issuer, LoadingCache<String, Key> cache) {
        this.mapper = null == mapper ? new ObjectMapper() : mapper;
        this.issuer = null == issuer ? "http://authService:9090" : issuer;
        this.cache = null == cache ? this.getDefaultCache() : cache;
    }


    @Override
    public Key getKeyByKid(String kid) {
        try {
            return (Key)this.cache.get(kid);
        } catch (ExecutionException | CacheLoader.InvalidCacheLoadException var3) {
            log.debug("invalid kid", var3);
            return null;
        }
    }

    //private utility method
    private LoadingCache<String, Key> getDefaultCache() {
        return CacheBuilder.newBuilder().expireAfterWrite(12L, TimeUnit.HOURS).maximumSize(100L).build(new CacheLoader<String, Key>() {
            public Key load(String kid) {
                try {
                    Iterator var2 = CachedSigningKeyService.this.getAuthorizationPublicKeys().iterator();

                    while(var2.hasNext()) {
                        JwkWS jwkWS = (JwkWS)var2.next();
                        CachedSigningKeyService.log.debug("public-key-cache: retrieved kid={}, publicKey={}", kid, jwkWS);
                        if (jwkWS.getKid().equals(kid)) {
                            return jwkWS.toKey();
                        }
                    }
                } catch (InvalidKeySpecException | IOException var4) {
                    CachedSigningKeyService.log.error("public-key-cache: error loading keys to cache kid=" + kid, var4);
                }

                return null;
            }
        });
    }

    private List<JwkWS> getAuthorizationPublicKeys() throws IOException {
        URL publicKeysUrl = this.getUrl();
        log.info("retrieving keys from publicKeyUrl={}", publicKeysUrl);
        JwkSetWS jwkSetWS = (JwkSetWS)this.mapper.readValue(publicKeysUrl, JwkSetWS.class);
        return jwkSetWS.getKeys();
    }

    private URL getUrl() throws MalformedURLException {
        String host = this.issuer + "/api/v1/certificates";
        log.debug("issuer host: {}", host);
        return new URL(host);
    }


    public static CachedSigningKeyService.Builder builder() {
        return new CachedSigningKeyService.Builder();
    }

    public static final class Builder {
        private ObjectMapper mapper;
        private String issuer;
        private LoadingCache<String, Key> cache;

        private Builder() {
        }

        public static CachedSigningKeyService.Builder anInMemoryCertificateCache() {
            return new CachedSigningKeyService.Builder();
        }

        public CachedSigningKeyService.Builder mapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public CachedSigningKeyService.Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public CachedSigningKeyService.Builder cache(LoadingCache<String, Key> cache) {
            this.cache = cache;
            return this;
        }

        public CachedSigningKeyService build() {
            return new CachedSigningKeyService(this.mapper, this.issuer, this.cache);
        }
    }
}
