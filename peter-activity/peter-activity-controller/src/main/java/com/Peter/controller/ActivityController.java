package com.Peter.controller;

import com.Peter.ActivityService;
import com.Peter.Param.ActivityParam;
import com.Peter.Param.BaseResult;
import com.Peter.Param.DeleteActivityParam;
import com.Peter.dto.DeleteActivityInfoDto;
import com.Peter.dto.PostActivityInfoDto;
import com.Peter.utils.BaseResultUtils;
import com.Peter.utils.TokenUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

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
    /**
     * 删除活动
     * @return
     */
   @DeleteMapping("/delete")
   public BaseResult<Boolean>deleteActivity(@RequestBody DeleteActivityParam deleteActivityParam ){
        try {
            log.info("删除活动-入参:{}", JSON.toJSONString(deleteActivityParam));
            Assert.isTrue(deleteActivityParam!= null, "入参不能为空");
            Assert.isTrue(deleteActivityParam.getId() != null, "活动ID不能为空");
            Assert.isTrue(deleteActivityParam.getCreatorId()!= null, "活动创建人不能为空");
            //删除
            DeleteActivityInfoDto deleteActivityInfoDto = new DeleteActivityInfoDto();
            deleteActivityInfoDto.setId(Long.valueOf(deleteActivityParam.getId()));
            deleteActivityInfoDto.setCreatorId(deleteActivityParam.getCreatorId());
            int deleteCount=activityService.DeleteActivity(deleteActivityInfoDto);
            log.info("删除活动-出参:{}",deleteCount>0);
            return BaseResultUtils.generateSuccess(deleteCount>0);
        }catch (Exception e){
            return BaseResultUtils.generateError(e.getMessage());
        }
   }

    private void checkPostActivityParam(ActivityParam activityParam) {
        String userId= String.valueOf(TokenUtils.getUserId());
        Assert.isTrue(userId != null, "用户未登录");
        Assert.isTrue((activityParam.getCreatorId().equals(userId)),"无法修改他人发布的活动");
        Assert.isTrue(activityParam.getIsDelete()==0,"活动已删除");
        Assert.isTrue(activityParam.getId() != null, "活动ID不能为空");
        Assert.isTrue(activityParam.getTime()!= null, "活动时间不能为空");
        Assert.isTrue(activityParam.getLocation()!= null, "活动地点不能为空");
        Assert.isTrue(activityParam.getName()!= null, "活动名称不能为空");
    }

}
