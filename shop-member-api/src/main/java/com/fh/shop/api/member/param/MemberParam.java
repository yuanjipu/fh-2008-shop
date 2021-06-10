package com.fh.shop.api.member.param;

import com.fh.shop.api.member.po.Member;
import lombok.Data;

@Data
public class MemberParam extends Member {

    private String code;

    private String configPassword;
}
