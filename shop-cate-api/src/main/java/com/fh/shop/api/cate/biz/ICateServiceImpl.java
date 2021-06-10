package com.fh.shop.api.cate.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.cate.mapper.ICateMapper;
import com.fh.shop.api.cate.po.Cate;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Transactional(rollbackFor = Exception.class)
@Service("cateService")

public class ICateServiceImpl implements ICateService {
    @Resource
    private ICateMapper cateMapper;
    @Override
    @Transactional(readOnly = true)
    public ServerResponse findList() {

//        从redis中获取分类值
        String cateListInfo = RedisUtil.get("cateList");

        if(StringUtils.isNotEmpty(cateListInfo)){
//            值不为空直接转换响应到前台
            List<Cate> cateList = JSON.parseArray(cateListInfo, Cate.class);
            return ServerResponse.success(cateList);
        }

//        为空就从数据库查询，将查询的数据放进redis顺便返回到前台
        List<Cate> cateList = cateMapper.selectList(null);
        String cateListJson = JSON.toJSONString(cateList);
        RedisUtil.set("cateList",cateListJson);
        return ServerResponse.success(cateList);
    }
}
