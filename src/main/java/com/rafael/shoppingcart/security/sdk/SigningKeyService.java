package com.rafael.shoppingcart.security.sdk;

import java.security.Key;

public interface SigningKeyService {
    Key getKeyByKid(String var1);
}
