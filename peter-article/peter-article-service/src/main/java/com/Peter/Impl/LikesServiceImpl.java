package com.Peter.Impl;

import com.Peter.ArticleService;
import com.Peter.LikesService;
import com.Peter.dao.ArticleLikesDao;
import com.Peter.dto.LikesInfoDto;
import com.Peter.entity.ArticleLikes;
import com.Peter.entity.ArticleLikesExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class LikesServiceImpl implements LikesService {
@Autowired
private ArticleLikesDao articleLikesDao;

@Autowired
private ArticleService articleService;
    @Override
    public int set(LikesInfoDto likesInfoDto) {
        //查询该用户是否已经点赞过该目标文章
        ArticleLikesExample articleLikesExample = new ArticleLikesExample();
        ArticleLikesExample.Criteria criteria = articleLikesExample.createCriteria();
        criteria.andUserIdEqualTo(likesInfoDto.getUserId());
        criteria.andModuleEqualTo(likesInfoDto.getModule());
        criteria.andTargetIdEqualTo(likesInfoDto.getTargetId());
       //criteria是sql条件查询的构造器
        List<ArticleLikes> articleLikes = articleLikesDao.selectByExample(articleLikesExample);//执行条件查询
        //在sql中查询
        //如果点赞过，那就取消点赞
        if (!CollectionUtils.isEmpty(articleLikes)) {
            int delete = articleLikesDao.deleteByPrimaryKey(articleLikes.getFirst().getId());//getFirst() 取出第一个元素(也只有一个元素)，返回: ArticleLikes 对象
            if (delete > 0) {
                //汇总
                return articleService.subtractLikesCount(articleLikes.getFirst().getTargetId());
               }
            return -1;
            } else {
                ArticleLikes aLikes = new ArticleLikes();
                aLikes.setUserId(likesInfoDto.getUserId());
                aLikes.setModule(likesInfoDto.getModule());
                aLikes.setTargetId(likesInfoDto.getTargetId());
                int insert = articleLikesDao.insert(aLikes);
                if (insert > 0) {
                    //汇总
                    return articleService.addLikesCount(likesInfoDto.getTargetId());
                }

            }
            //如果没点赞过，就点赞
            return -1;
        }

}
