package com.Peter.enums;

public enum ModuleTypeEnums {
    ARTICLE(1,"文章")
    ;
    int code;
    String msg;

    ModuleTypeEnums(int code , String msg){
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
