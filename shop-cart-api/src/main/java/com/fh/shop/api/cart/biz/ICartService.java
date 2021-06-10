package com.fh.shop.api.cart.biz;

import com.fh.shop.common.ServerResponse;

public interface ICartService {

    public ServerResponse addItem(Long memberId, Long skuId, Long count);

    ServerResponse findCart(Long id);

    ServerResponse findCartCount(Long id);

    ServerResponse deleteCart(Long id, Long skuId);

    ServerResponse deleteBatchCart(Long id, String skuIds);
}
