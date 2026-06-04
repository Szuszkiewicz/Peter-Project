package com.Peter.enums;

public enum ActivityOperationTypeEnums {
    ADD(1, "新增"),
    UPDATE(2, "修改")
    ;

    int code;
    String msg;
    ActivityOperationTypeEnums(int code, String msg) {
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
