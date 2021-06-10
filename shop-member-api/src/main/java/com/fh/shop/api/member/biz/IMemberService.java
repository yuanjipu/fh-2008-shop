package com.fh.shop.api.member.biz;

import com.fh.shop.common.ServerResponse;

public interface IMemberService {


    ServerResponse login(String memberName, String password);

}
