package com.rafael.shoppingcart.api;

import com.rafael.shoppingcart.dto.ProductDto;
import com.rafael.shoppingcart.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/internal/products")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/", produces = {"application/json"})
    public Mono<ResponseEntity<List<ProductDto>>> getProducts(
            @RequestHeader(value = "AP-Authorization") String authHeader) {
        log.info("Fetching all products...");
        return productService.getProducts()
                .map(productBos -> {
                    List<ProductDto> dtos = new ArrayList<>();
                    productBos.forEach(p -> {
                        ProductDto dto = ProductDto.builder().id(p.id).name(p.name).editions(p.editions).build();
                        dtos.add(dto);
                    });
                    return ResponseEntity.ok().body(dtos);
                });
    }

}
