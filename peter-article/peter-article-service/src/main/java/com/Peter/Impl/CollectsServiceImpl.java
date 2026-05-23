package com.Peter.Impl;

import com.Peter.ArticleService;
import com.Peter.CollectsService;
import com.Peter.dao.CollectDao;
import com.Peter.dto.CollectsInfoDto;
import com.Peter.entity.Collect;
import com.Peter.entity.CollectExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CollectsServiceImpl implements CollectsService {
    @Autowired
    private CollectDao collectDao;

    @Autowired
    private ArticleService articleService;

    @Override
    public  int set(CollectsInfoDto collectsInfoDto){
        CollectExample collectExample = new CollectExample();
        CollectExample.Criteria criteria = collectExample.createCriteria();
        criteria.andUserIdEqualTo(collectsInfoDto.getUserId());
        criteria.andModuleEqualTo(collectsInfoDto.getModule());
        criteria.andTargetIdEqualTo(collectsInfoDto.getTargetId());
        //criteria是sql条件查询的构造器
        List<Collect> collects = collectDao.selectByExample(collectExample);//执行条件查询
        //在sql中查询
        //如果收藏过，那就取消收藏
        if (!CollectionUtils.isEmpty(collects)) {
            int delete = collectDao.deleteByPrimaryKey(collects.getFirst().getId());//getFirst() 取出第一个元素(也只有一个元素)，返回: ArticleLikes 对象
            if (delete > 0) {
                //汇总
                return articleService.subtractCollectsCount(collects.getFirst().getTargetId());
            }
            return -1;
        } else {
            Collect collect = new Collect();
            collect.setUserId(collectsInfoDto.getUserId());
            collect.setModule(collectsInfoDto.getModule());
            collect.setTargetId(collectsInfoDto.getTargetId());
            int insert = collectDao.insert(collect);
            if (insert > 0) {
                //汇总
                return articleService.addCollectsCount(collectsInfoDto.getTargetId());
            }

        }
        //如果没收藏过，就收藏
        return -1;
       }
    }

