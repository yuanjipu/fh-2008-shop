package com.fh.shop.api.goods.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Sku {

    @ApiModelProperty(value = "SkuID")
    private Long id;

    @ApiModelProperty(value = "spuID")
    private Long spuId;

    @ApiModelProperty(value = "sku名字")
    private String skuName;

    @ApiModelProperty(value = "价格" ,example = "0")
    private BigDecimal price;

    @ApiModelProperty(value = "库存" ,example = "0")
    private Integer stock;

    @ApiModelProperty(value = "规格信息")
    private String specInfo;

    @ApiModelProperty(value = "颜色ID")
    private Long colorId;

    @ApiModelProperty(value = "颜色图片")
    private String colorImage;

    @ApiModelProperty(value = "是否上架")
    private Integer status;//是否上架

    @ApiModelProperty(value = "推荐")
    private Integer recommend;//推荐

    @ApiModelProperty(value = "新品" +
            "")
    private Integer newProduct;//新品

    @ApiModelProperty(value = "销量")
    private Long salesVolume;//销量

}
