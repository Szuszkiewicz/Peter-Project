package com.Peter.enums;

public enum ArticleOperationTypeEnums {
    ADD(1,"发布"),
    UPDATE(2,"修改"),
    ;
    int code;
    String msg;
    ArticleOperationTypeEnums(int code, String msg) {
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
