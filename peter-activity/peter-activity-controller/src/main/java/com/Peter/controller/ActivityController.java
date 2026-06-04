package com.Peter.controller;

import com.Peter.ActivityService;
import com.Peter.Param.ActivityParam;
import com.Peter.Param.BaseResult;
import com.Peter.dto.PostActivityInfoDto;
import com.Peter.utils.BaseResultUtils;
import com.Peter.utils.TokenUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    /**
     * 创建或修改活动
     *
     * @param ActivityParam
     * @return
     */
    @PostMapping("/post")
    public BaseResult<Boolean> postActivity(@RequestBody ActivityParam ActivityParam){
        log.info("发布活动-入参:{}", JSON.toJSONString(ActivityParam));
        checkPostActivityParam(ActivityParam);
        Long userId= TokenUtils.getUserId();
        Assert.isTrue(userId != null, "用户未登录");
        PostActivityInfoDto postActivityInfoDto = new PostActivityInfoDto();
        BeanUtils.copyProperties(ActivityParam,postActivityInfoDto);
        postActivityInfoDto.setCreatorId(userId.toString());
        int count =activityService.PostActivity(postActivityInfoDto);
        log.info("发布活动-出参:{}",count>0);
        return BaseResultUtils.generateSuccess(count>0);
    }

    private void checkPostActivityParam(ActivityParam activityParam) {
        Assert.isTrue(activityParam.getId() != null, "活动ID不能为空");
        Assert.isTrue(activityParam.getTime()!= null, "活动时间不能为空");
        Assert.isTrue(activityParam.getLocation()!= null, "活动地点不能为空");
        Assert.isTrue(activityParam.getName()!= null, "活动名称不能为空");
    }

}
