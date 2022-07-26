package com.rafael.shoppingcart.security.sdk;

import io.jsonwebtoken.impl.DefaultClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MultiTenantClaims extends DefaultClaims implements Claims {

    private static final Logger log = LoggerFactory.getLogger(MultiTenantClaims.class);
    private static final String TENANT = "tenant";
    private static final String COMPANY_UUID = "companyUuid";
    private static final String SUB_TYPE = "subType";
    private static final String PERMISSIONS = "permissions";
    private static final String CLIENT_ID = "clientId";

    //Constructor
    public MultiTenantClaims(Map<String, Object> body) {
        super(body);
    }

    public String getTenant() {
        return (String)this.get("tenant", String.class);
    }

    public Optional<String> getCompanyUuid() {
        return this.containsKey("companyUuid") ? Optional.of(this.get("companyUuid", String.class)) : Optional.empty();
    }

    public Optional<String> getUserUuid() {
        try {
            UUID userUuid = UUID.fromString(this.getSubject());
            return Optional.of(userUuid.toString());
        } catch (IllegalArgumentException var2) {
            log.debug("Could not parse UUID, sub is probably a client_id representing an Application.", var2);
            return Optional.empty();
        }
    }

    public List<String> getPermissions() {
        return this.get("permissions") != null ? (List)this.get("permissions") : Collections.emptyList();
    }

    public String getClientId() {
        return (String)this.get("clientId", String.class);
    }

    public String getSubType() {
        return (String)this.get("subType", String.class);
    }
}
