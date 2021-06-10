package com.fh.shop.api.member.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.member.mapper.IMemberMapper;
import com.fh.shop.api.member.po.Member;
import com.fh.shop.api.member.vo.LoginVo;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service("memberService")
@Transactional(rollbackFor = Exception.class)
public class IMemberServiceImpl implements IMemberService {


    @Resource
    private IMemberMapper memberMapper;


    @Override
    @Transactional(readOnly = true)
    public ServerResponse login(String memberName, String password) {
        if(StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_MEMBER_IS_NULL);
        }
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("memberName",memberName);
        Member memberDB = memberMapper.selectOne(memberQueryWrapper);

        //判断会员是否存在
        if(memberDB==null){
            System.out.println("哒哒哒哒哒哒多多多");
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_MEMBER_NAME_IS_ERROR);
        }

        //判断密码是否正确
        if(!memberDB.getPassword().equals(Md5Util.md5(password))){
            return ServerResponse.error(ResponseEnum.MEMBER_IS_PASSWORD_ERROR);
        }

        //判断是否激活，如果未激活将邮箱和id响应给前台
        if(memberDB.getStatus().equals(Constants.ISACTIVE)){
            Map<String,String> map = new HashMap<>();
            map.put("id",memberDB.getId()+"");
            map.put("mail",memberDB.getMail());
            return ServerResponse.error(ResponseEnum.MEMBER_IS_ACTIVATE_NO,map);
        }

        LoginVo loginVo = new LoginVo();
        Long id = memberDB.getId();
        loginVo.setId(id);
        loginVo.setMemberName(memberDB.getMemberName());
        loginVo.setNickName(memberDB.getNickName());

        //将用户信息转文json字符串
        String memberVoJson = JSON.toJSONString(loginVo);

        //将用户信息的json字符串+秘钥 进行散列生成签名勽
        String sign = Md5Util.sign(memberVoJson,Constants.SECRET);

        //将用户信息和签名进行编码(可解编码)
        byte[] memberVoJsonBytes = memberVoJson.getBytes();//转为byte类型
        byte[] signBytes = sign.getBytes();//转为byte类型
        String memberVoJsonBase64 = Base64.getEncoder().encodeToString(memberVoJsonBytes);
        String signBase64 = Base64.getEncoder().encodeToString(signBytes);

        //在redis中设置用来判断登录过期时间的key
        RedisUtil.setex(KeyUtil.buildMemberKey(id), "" ,Constants.TOKEN_EXPIRE);

        return ServerResponse.success(memberVoJsonBase64+"."+signBase64);

    }

}
