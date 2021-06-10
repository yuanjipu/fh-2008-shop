package com.fh.shop.api.cart.vo;

import lombok.Data;

@Data
public class CartItemVo {

    private Long skuId;

    private String skuName;

    private String price;

    private String skuImage;

    private Long count;

    private String subPrice;
}
