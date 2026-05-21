package com.Peter.controller;

import com.Peter.LikesService;
import com.Peter.Param.BaseResult;
import com.Peter.Param.LikesParam;
import com.Peter.dto.LikesInfoDto;
import com.Peter.utils.BaseResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
public class LikesController {
    @Autowired
    private LikesService likesService;
    /**
     * 点赞和取消点赞
     * @param likesParam
     * @return
     */
    @PostMapping("/set")
    public BaseResult set(@RequestBody LikesParam likesParam){

        Assert.isTrue(likesParam!=null,"参数不能为空");
        Assert.isTrue(likesParam.getUserId()!=null,"用户id不能为空");
        Assert.isTrue(likesParam.getTargetId()!=null,"点赞目标不能为空");
        LikesInfoDto likesInfoDto=new LikesInfoDto();
        BeanUtils.copyProperties(likesParam,likesInfoDto);
        int set=likesService.set(likesInfoDto);
        if(set>0){
            return BaseResultUtils.success();
        }
        return BaseResultUtils.generateError("点赞失败");
    }
}
