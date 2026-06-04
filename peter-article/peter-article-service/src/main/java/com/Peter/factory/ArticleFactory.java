package com.Peter.factory;

import com.Peter.ArticlePostService;
import com.Peter.enums.ArticleOperationTypeEnums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArticleFactory {
     private static final  Map<ArticleOperationTypeEnums, ArticlePostService> factory =new ConcurrentHashMap<>();

     public static ArticlePostService fetchArticleService(ArticleOperationTypeEnums articleOperationType){
        return factory.get(articleOperationType);
     }
     /**
      * 初始化
      * @param articleOperationType
      * @param articlePostService
      */
     public static void init(ArticleOperationTypeEnums articleOperationType,ArticlePostService articlePostService){
         factory.put(articleOperationType,articlePostService);
     }

}
