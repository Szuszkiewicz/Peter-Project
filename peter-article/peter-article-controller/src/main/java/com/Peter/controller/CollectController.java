package com.Peter.controller;

import com.Peter.CollectsService;
import com.Peter.Param.BaseResult;
import com.Peter.Param.CollectsParam;
import com.Peter.dto.CollectsInfoDto;
import com.Peter.utils.BaseResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collect")
public class CollectController {
    @Autowired
    private CollectsService collectService;
    /**
     * 添加和取消收藏
     */
    @PostMapping("/set")
    public BaseResult set(@RequestBody CollectsParam collectsParam){
        Assert.isTrue(collectsParam!=null,"参数不能为空");
        Assert.isTrue(collectsParam.getUserId()!=null,"用户ID不能为空");
        Assert.isTrue(collectsParam.getTargetId()!=null,"关联对象ID不能为空");
        CollectsInfoDto collectsInfoDto = new CollectsInfoDto();
        BeanUtils.copyProperties(collectsParam,collectsInfoDto);
        int set=collectService.set(collectsInfoDto);

        if(set>0){
            return BaseResultUtils.success();
        }
        return BaseResultUtils.generateError("收藏失败");
    }
}
