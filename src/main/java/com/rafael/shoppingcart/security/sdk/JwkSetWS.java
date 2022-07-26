package com.rafael.shoppingcart.security.sdk;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class JwkSetWS {
    @JsonProperty("keys")
    private List<JwkWS> keys = new ArrayList();

    public JwkSetWS() {
    }

    public JwkSetWS(List<JwkWS> keys) {
        this.keys = keys;
    }

    public List<JwkWS> getKeys() {
        return this.keys;
    }

    public void setKeys(List<JwkWS> keys) {
        this.keys = keys;
    }
}
