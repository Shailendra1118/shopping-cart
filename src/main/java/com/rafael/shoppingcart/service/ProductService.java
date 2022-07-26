package com.rafael.shoppingcart.service;

import com.rafael.shoppingcart.bo.ProductBo;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {
    Mono<List<ProductBo>> getProducts();
}
