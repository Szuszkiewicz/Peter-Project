package com.Peter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 文章启动类
 *
 */
@EnableDiscoveryClient//注册服务
@EnableFeignClients(basePackages = "com.Peter.api")
@SpringBootApplication(scanBasePackages ={"com.Peter"})
public class ArticleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class, args);
    }
}
