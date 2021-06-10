package com.fh.shop.api.goods.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuVo {

    private Long id;

    private String price;

    private String skuName;

    private String image;
}
