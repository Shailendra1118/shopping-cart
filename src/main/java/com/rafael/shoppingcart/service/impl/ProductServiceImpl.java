package com.rafael.shoppingcart.service.impl;

import com.rafael.shoppingcart.bo.ProductBo;
import com.rafael.shoppingcart.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Override
    public Mono<List<ProductBo>> getProducts() {
        return Mono.fromCallable(() -> {
            List<ProductBo> products = new ArrayList<>();
            ProductBo p1 = ProductBo.builder().id(100).name("Samsung Galaxy").build();
            ProductBo p2 = ProductBo.builder().id(100).name("Samsung A2").build();
            products.add(p1);
            products.add(p2);
            return products;
        });
    }
}
