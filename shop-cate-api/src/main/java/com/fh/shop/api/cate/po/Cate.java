package com.fh.shop.api.cate.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class Cate implements Serializable {

    @ApiModelProperty(value = "会员ID" ,example = "0")
    private Long id;

    @ApiModelProperty(value = "分类名")
    private String cateName;

    @ApiModelProperty(value = "父节点ID")
    private Long fatherId;

    @ApiModelProperty(value = "类型ID")
    private Long typeId;

    @ApiModelProperty(value = "类型名")
    private String typeName;

}
