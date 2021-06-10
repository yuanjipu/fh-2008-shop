package com.fh.shop.api.cart.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class CartVo {

    private List<CartItemVo> cartItemVoList = new ArrayList<>();

    private Long totalCount;

    private String totalPrice;
}
