package com.Peter.enums;

public enum ArticleTypeEnum {
    ARTICLE(1,"文章"),
    VIDEO(2,"视频"),
    NOTICE(3,"公告"),
    ;
    int code;
    String msg;

    ArticleTypeEnum(int code , String msg){
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
