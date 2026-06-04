package com.Peter.impl;

import com.Peter.ActivityService;
import com.Peter.dao.ActivityDao;
import com.Peter.dto.PostActivityInfoDto;
import com.Peter.entity.Activity;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    ActivityDao activityDao;

    @Override
    public int PostActivity(PostActivityInfoDto postActivityInfoDto){
        try{
            log.info("发布活动-PostActivity-入参:{}", JSON.toJSONString(postActivityInfoDto));
            //如果id不为空，则更新活动信息
            if(postActivityInfoDto.getId() != null){
                Activity activity = activityDao.selectByPrimaryKey(postActivityInfoDto.getId());
                activity.setDesc(postActivityInfoDto.getDesc());
                activity.setName(postActivityInfoDto.getName());
                activity.setLocation(postActivityInfoDto.getLocation());
                activity.setTime(postActivityInfoDto.getTime());
                activity.setCreatorId(postActivityInfoDto.getCreatorId());
                int updateCount=activityDao.updateByPrimaryKeySelective(activity);
                log.info("更新活动信息-updateCount:{}",updateCount);
                return updateCount;
            }else{
                Activity activity = new Activity();
                activity.setDesc(postActivityInfoDto.getDesc());
                activity.setName(postActivityInfoDto.getName());
                activity.setLocation(postActivityInfoDto.getLocation());
                activity.setTime(postActivityInfoDto.getTime());
                activity.setCreatorId(postActivityInfoDto.getCreatorId());
                int insertCount=activityDao.insertSelective(activity);
                log.info("插入活动信息-insertCount:{}",insertCount);
                return insertCount;
            }

        }catch (Exception e){
            log.error("发布活动-PostActivity-异常:{}",e.getMessage());
            return -1;
        }
    }
}
