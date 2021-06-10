package com.fh.shop.api.member.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class Member {

    @ApiModelProperty(value = "会员ID" ,example = "0")
    private Long id;

    @ApiModelProperty(value = "会员名")
    private String MemberName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String mail;

    @ApiModelProperty(value = "是否激活")
    private String status;

    @ApiModelProperty(value = "会员积分")
    private Integer integral;
}
