package com.Peter.impl;

import Factory.ActivityFactory;
import com.Peter.ActivityPostService;
import com.Peter.dao.ActivityDao;
import com.Peter.dto.PostActivityInfoDto;
import com.Peter.entity.Activity;
import com.Peter.enums.ActivityOperationTypeEnums;
import com.Peter.utils.TokenUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddActivityServiceImpl implements ActivityPostService, InitializingBean {
    @Autowired
    private ActivityDao activityDao;
    @Override
    public int doAction(PostActivityInfoDto postActivityInfoDto){
        log.info("发布活动-PostActivity-入参:{}", JSON.toJSONString(postActivityInfoDto));
        String creatorName= TokenUtils.getUsername();
        Activity activity = new Activity();
        activity.setDesc(postActivityInfoDto.getDesc());
        activity.setUsername(creatorName);
        activity.setName(postActivityInfoDto.getName());
        activity.setLocation(postActivityInfoDto.getLocation());
        activity.setTime(postActivityInfoDto.getTime());
        activity.setCreatorId(postActivityInfoDto.getCreatorId());
        int insertCount=activityDao.insertSelective(activity);
        log.info("插入活动信息-insertCount:{}",insertCount);
        return insertCount;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        ActivityFactory.init(ActivityOperationTypeEnums.ADD,this);
    }
}
