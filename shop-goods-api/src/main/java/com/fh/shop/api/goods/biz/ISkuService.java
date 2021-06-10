package com.fh.shop.api.goods.biz;

import com.fh.shop.common.ServerResponse;

import java.util.List;

public interface ISkuService {

    public ServerResponse list();

    ServerResponse findSku(Long id);
}
