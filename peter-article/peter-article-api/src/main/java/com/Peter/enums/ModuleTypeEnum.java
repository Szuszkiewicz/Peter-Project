package com.Peter.enums;

public enum ModuleTypeEnum {
    WEBSITE(1,"网站"),

    ;
    int code;
    String msg;

    ModuleTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ModuleTypeEnum getByCode(int code){
        ModuleTypeEnum[] values = ModuleTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            ModuleTypeEnum value = values[i];
            if(value.getCode()==code){
                return value;
            }
        }
        return null;
    }
}
