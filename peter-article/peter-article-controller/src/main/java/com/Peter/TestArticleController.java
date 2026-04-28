package com.Peter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class TestArticleController {
    @RequestMapping("/test")
    public String test() {
        return "文章测试" ;
    }

}
