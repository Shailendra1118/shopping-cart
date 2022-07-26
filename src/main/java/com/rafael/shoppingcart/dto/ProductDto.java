package com.rafael.shoppingcart.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class ProductDto {
    public long id;
    public String name;
    public List<String> editions;
}
