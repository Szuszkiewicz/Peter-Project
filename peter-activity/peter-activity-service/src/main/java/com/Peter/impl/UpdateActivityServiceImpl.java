package com.Peter.impl;

import Factory.ActivityFactory;
import com.Peter.ActivityPostService;
import com.Peter.dao.ActivityDao;
import com.Peter.dto.PostActivityInfoDto;
import com.Peter.entity.Activity;
import com.Peter.enums.ActivityOperationTypeEnums;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateActivityServiceImpl implements ActivityPostService, InitializingBean {
    @Autowired
    ActivityDao activityDao;

    @Override
    public int doAction(PostActivityInfoDto postActivityInfoDto){
        log.info("发布活动-PostActivity-入参:{}", JSON.toJSONString(postActivityInfoDto));
        Activity activity = activityDao.selectByPrimaryKey(postActivityInfoDto.getId());
        activity.setDesc(postActivityInfoDto.getDesc());
        activity.setName(postActivityInfoDto.getName());
        activity.setLocation(postActivityInfoDto.getLocation());
        activity.setTime(postActivityInfoDto.getTime());
        activity.setCreatorId(postActivityInfoDto.getCreatorId());
        int updateCount=activityDao.updateByPrimaryKeySelective(activity);
        log.info("更新活动信息-updateCount:{}",updateCount);
        return updateCount;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        ActivityFactory.init(ActivityOperationTypeEnums.UPDATE,this);
    }
}
