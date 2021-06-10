package goods.controller;

import com.fh.shop.common.ServerResponse;
import goods.biz.ISkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@Api(tags = "商品接口")
public class SkuController {

    @Resource(name = "skuService")
    private ISkuService skuService;

    @GetMapping("/api/skus/recommend/mewprodect")
    @ApiOperation(value = "展示商品查询")
    public ServerResponse list(){
        return skuService.list();
    }
}
