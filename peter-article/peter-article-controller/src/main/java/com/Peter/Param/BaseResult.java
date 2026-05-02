package com.Peter.Param;

import lombok.Data;

//通用返回结果，后端到前端
@Data//添加getter和setter方法
public class BaseResult<T> {

    private Integer code;//响应码
    private Boolean success;//是否成功
    private String message;//提示信息
    private T data;// 数据
   public BaseResult(Integer code, Boolean success, String message, T data){
       this.code = code;
       this.success = success;
       this.message = message;
       this.data = data;
   }
   public BaseResult(){}
}
