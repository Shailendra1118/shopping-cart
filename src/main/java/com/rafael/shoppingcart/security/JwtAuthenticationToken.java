package com.rafael.shoppingcart.security;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Base class for Authentication objects.
 * Implementations which use this class should be immutable
 */

@ToString
@EqualsAndHashCode(callSuper = false)
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    private JwtPrincipal principal;
    private Object bearerToken;

/*
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }
*/

    public JwtAuthenticationToken(JwtPrincipal principal, List<GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public  Object getDetails() {
        return this.bearerToken;
    }

    @Override
    public void setDetails(Object details) {
        this.bearerToken = details;
    }
}
