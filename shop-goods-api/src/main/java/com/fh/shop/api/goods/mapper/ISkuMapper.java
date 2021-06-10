package com.fh.shop.api.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.goods.po.Sku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ISkuMapper extends BaseMapper<Sku> {
    List<Sku> findRecommendNewProductList();


    void updateSkuStock(@Param("skuId") Long skuId, @Param("skuCount") Integer skuCount);

    void updateSalesVolume(@Param("skuId") Long skuId, @Param("skuCount") Integer skuCount);
}
