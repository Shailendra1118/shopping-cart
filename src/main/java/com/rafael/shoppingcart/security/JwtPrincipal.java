package com.rafael.shoppingcart.security;

import lombok.Data;

import java.io.Serializable;
import java.security.Principal;

@Data
public class JwtPrincipal implements Principal {
    //private static final long serialVersionUID = -4089529089830687421L;
    private String sub;
    private String type;
    private String tenant;
    private String companyUuid;

    public JwtPrincipal(String sub, String type, String tenant, String companyUuid) {
        this.sub = sub;
        this.type = type;
        this.tenant = tenant;
        this.companyUuid = companyUuid;
    }

    @Override
    public String getName() {
        return getSub();
    }
}
