package com.rafael.shoppingcart.bo;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
public class ProductBo {
    public long id;
    public String name;
    public List<String> editions;
}
