package com.Peter.impl;

import Factory.ActivityFactory;
import com.Peter.ActivityPostService;
import com.Peter.ActivityService;
import com.Peter.dao.ActivityDao;
import com.Peter.dto.DeleteActivityInfoDto;
import com.Peter.dto.PostActivityInfoDto;
import com.Peter.entity.Activity;
import com.Peter.enums.ActivityOperationTypeEnums;
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
            //策略模式
            ActivityPostService activityPostService = ActivityFactory.fetchActivityService(postActivityInfoDto.getId()==null? ActivityOperationTypeEnums.ADD:ActivityOperationTypeEnums.UPDATE);
            return activityPostService.doAction(postActivityInfoDto);
        }catch (Exception e){
            log.error("发布活动-PostActivity-异常:{}",e.getMessage());
            return -1;
        }
    }
    @Override
    public int DeleteActivity(DeleteActivityInfoDto deleteActivityInfoDto){
        try{
            log.info("删除活动-DeleteActivity-入参:{}", JSON.toJSONString(deleteActivityInfoDto));
            Activity activity=activityDao.selectByPrimaryKey(deleteActivityInfoDto.getId());
            if(activity==null){
                log.error("删除活动-DeleteActivity-活动不存在");
                return -1;
            }if(activity.getIsDelete()==1){
                log.error("删除活动-DeleteActivity-活动已删除");
                return -1;
            }if(!activity.getCreatorId().equals(deleteActivityInfoDto.getCreatorId())){
                log.error("删除活动-DeleteActivity-用户不匹配，无法删除");
                return -1;
            }
            activity.setIsDelete((byte) 1);
            int deleteCount = activityDao.updateByPrimaryKeySelective(activity);
            log.info("删除活动-DeleteActivity-出参:{}", deleteCount);
            return deleteCount;
        }catch (Exception e){
            log.error("删除活动-DeleteActivity-异常:{}",e.getMessage());
            return -1;
        }
    }

}
