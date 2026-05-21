package com.Peter.enums;

public enum ArticleTypeEnums {
    WEBSITE(1,"网站"),

    ;
    int code;
    String msg;

    ArticleTypeEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ArticleTypeEnums getByCode(int code){
        ArticleTypeEnums[] values = ArticleTypeEnums.values();
        for (int i = 0; i < values.length; i++) {
            ArticleTypeEnums value = values[i];
            if(value.getCode()==code){
                return value;
            }
        }
        return null;
    }
}
