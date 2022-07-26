package com.rafael.shoppingcart.security.sdk;

import java.util.List;
import java.util.Optional;

public interface Claims extends io.jsonwebtoken.Claims {

    String getTenant();

    Optional<String> getCompanyUuid();

    Optional<String> getUserUuid();

    List<String> getPermissions();

    String getClientId();

    String getSubType();
}
